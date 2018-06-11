/*
 * The copyright of this file belongs to Feedzai. The file cannot be
 * reproduced in whole or in part, stored in a retrieval system,
 * transmitted in any form, or by any means electronic, mechanical,
 * photocopying, or otherwise, without the prior permission of the owner.
 *
 * (c) 2018 Feedzai, Strictly Confidential
 */

package com.feedzai.openml.python;

import com.feedzai.openml.data.Instance;
import com.feedzai.openml.data.schema.AbstractValueSchema;
import com.feedzai.openml.data.schema.CategoricalValueSchema;
import com.feedzai.openml.data.schema.DatasetSchema;
import com.feedzai.openml.model.ClassificationMLModel;
import com.feedzai.openml.provider.exception.ModelLoadingException;
import com.feedzai.openml.python.jep.instance.JepInstance;
import com.feedzai.util.data.encoding.EncodingHelper;
import com.google.common.collect.ImmutableList;
import jep.JepException;
import jep.NDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Representation of a Python classification model that has been loaded to the given Jep instance.
 * The model should be in a "global" variable in the Jep instance with name {@code id}.
 * <p>
 * This representation is used for any Python based model including Scikit models and Generic user made models.
 * <p>
 * Some use cases can reuse this implementation and simply instantiate it with custom function names passed to
 * the {@link #ClassificationPythonModel(JepInstance, DatasetSchema, String, String, String) constructor}.
 *
 * @author Luis Reis (luis.reis@feedzai.com)
 * @since 0.1.0
 */
public class ClassificationPythonModel implements ClassificationMLModel {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(ClassificationPythonModel.class);

    /**
     * Default name for the Python function that returns the index of the class for the prediction on a
     * given instance.
     */
    public static final String DEFAULT_CLASSIFY_FUNCTION_NAME = "classify";

    /**
     * Default name for the Python function that returns the probability distribution array for the prediction on a
     * given instance.
     */
    public static final String DEFAULT_GETCLASSDISTRIBUTION_FUNCTION_NAME = "getClassDistribution";

    /**
     * Converter function that knows how to convert the class value into the class index (in the field's schema).
     */
    private final Function<Serializable, Integer> classToIndexConverter;

    /**
     * Jep instance where this model is loaded.
     */
    private final JepInstance jepInstance;

    /**
     * Translation structure that contains only the indexes of the predictive (i.e. non-target) fields of the incoming
     * instances so that a double[] containing only those fields can be sent to Python for scoring.
     */
    private final int[] predictiveFieldIndexes;

    /**
     * Schema for this model.
     */
    private final DatasetSchema schema;

    /**
     * Name of the "global" variable inside the Jep instance where the model is stored.
     */
    private final String id;

    /**
     * Name of the python method in the model used to classify instances.
     */
    private final String classifyFunctionName;

    /**
     * Name of the python method in the model used to get the class distribution of instances.
     */
    private final String getClassDistributionFunctionName;

    /**
     * Constructor for this model's representation.
     *
     * @param jepInstance                      Jep instance where this model is loaded.
     * @param schema                           Schema of the instances this model receives.
     * @param id                               Name of the variable that stores this model in the Jep instance.
     * @param classifyFunctionName             Name of the Python function in the loaded model that returns the index
     *                                         of the class for the prediction on a given instance.
     * @param getClassDistributionFunctionName Name of the Python function in the loaded model that returns the
     *                                         probability distribution array for the prediction on a given instance.
     */
    public ClassificationPythonModel(final JepInstance jepInstance,
                                     final DatasetSchema schema,
                                     final String id,
                                     final String classifyFunctionName,
                                     final String getClassDistributionFunctionName) {

        this.jepInstance = jepInstance;
        this.schema = schema;
        this.predictiveFieldIndexes = IntStream.range(0, schema.getFieldSchemas().size())
                .filter(index -> index != schema.getTargetIndex())
                .toArray();

        this.id = id;
        this.classifyFunctionName = classifyFunctionName;
        this.getClassDistributionFunctionName = getClassDistributionFunctionName;
        this.classToIndexConverter = getClassToIndexConverter(schema);
    }

    /**
     * Constructor for this model's representation using default names for
     * {@link #DEFAULT_CLASSIFY_FUNCTION_NAME classification} and {@link #DEFAULT_GETCLASSDISTRIBUTION_FUNCTION_NAME scoring}.
     *
     * @param jepInstance Jep instance where this model is loaded.
     * @param schema      Schema of the instances this model receives.
     * @param id          Name of the variable that stores this model in the Jep instance.
     */
    public ClassificationPythonModel(final JepInstance jepInstance,
                                     final DatasetSchema schema,
                                     final String id) {
        this(jepInstance, schema, id, DEFAULT_CLASSIFY_FUNCTION_NAME, DEFAULT_GETCLASSDISTRIBUTION_FUNCTION_NAME);
    }

    @Override
    public boolean save(final Path dir, final String name) {
        // Python models are only load-able and thus cannot be saved.
        return false;
    }

    @Override
    public DatasetSchema getSchema() {
        return this.schema;
    }

    @Override
    public double[] getClassDistribution(final Instance instance) {
        final NDArray<double[]> result = invokeFunction(
                instance,
                this.getClassDistributionFunctionName,
                "numpy.array(%s)"
        );

        // Although the Python API returns a double[] for every input instance, going through the NDArray flattens that
        // into a single double[]
        return result.getData();
    }

    @Override
    public int classify(final Instance instance) {
        // The Python API supports an array of instances and returns an array of results, we need to adapt to a
        // single result.
        final String classValue = invokeFunction(instance, this.classifyFunctionName, "str(%s[0])");

        final int asNotNullable;
        try {
            asNotNullable = this.classToIndexConverter.apply(classValue);
        } catch (final NullPointerException e) {
            logger.warn("Unexpected class provided by model: {}", classValue, e);
            throw e;
        }
        return asNotNullable;
    }

    /**
     * Validates that the model's Jep environment has everything loaded and available.
     *
     * @param jepInstance The {@link JepInstance Jep environment}.
     * @param id          The id with which the model has been declared in the environment.
     * @throws ModelLoadingException If there is an error in the current environment.
     */
    public void validate(final JepInstance jepInstance, final String id) throws ModelLoadingException {

        // Check if model implements needed functions
        try {
            jepInstance.submitEvaluation((jep) -> {
                final List<String> functionNames = ImmutableList.of(
                        this.classifyFunctionName,
                        this.getClassDistributionFunctionName
                );

                for (final String functionName : functionNames) {
                    if (!((boolean) jep.getValue(String.format(
                            "callable(getattr(%s, \"%s\", None))",
                            id,
                            functionName
                    )))) {
                        throw new JepException(String.format("Model does not implement %s function.", functionName));
                    }
                }
                return null;
            }).get();
        } catch (final InterruptedException | ExecutionException e) {
            logger.error(e.getMessage(), e);
            throw new ModelLoadingException(e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        this.jepInstance.stop();
    }

    /**
     * Builds a function that knows how to convert from the class value that the Python API {@code classify} method
     * returns to the class value index that is expected by the
     * {@link ClassificationMLModel#classify(Instance) OpenML API method}.
     *
     * @param schema The {@link DatasetSchema} for this model.
     * @return The conversion function.
     */
    private Function<Serializable, Integer> getClassToIndexConverter(final DatasetSchema schema) {
        final AbstractValueSchema targetVariableSchema = schema.getTargetFieldSchema().getValueSchema();
        if (!(targetVariableSchema instanceof CategoricalValueSchema)) {
            logger.warn("Provided schema's target field is not categorical: {}", schema);
            throw new IllegalArgumentException("Classification models require Categorical target fields. Got " + targetVariableSchema);
        }
        return EncodingHelper.classToIndexConverter((CategoricalValueSchema) targetVariableSchema);
    }

    /**
     * Calls a given python method in the model with the data of an instance encoded into a Numpy array.
     * Returns the result also encoded as a Numpy array.
     *
     * @param instance               The instance to classify.
     * @param classificationFunction Name of the classification function to call.
     * @param pythonResultWrapping   {@link String#format(String, Object...) Format string} to wrap the call to the
     *                               function to perform result handling.
     * @param <T>                    The type of object the function returns.
     * @return The result of calling the result wrapping code on the resulting object.
     */
    private <T> T invokeFunction(final Instance instance,
                                 final String classificationFunction,
                                 final String pythonResultWrapping) {

        // Encode this instance data into a Numpy array for the python model to use without the target variable
        // The result of this encoding is an array with a single array inside as the classification functions
        // in most python models are expecting an array of instances to classify.
        final int numberPredictiveFields = this.predictiveFieldIndexes.length;
        final double[] data = new double[numberPredictiveFields];
        for (int index = 0; index < numberPredictiveFields; index++) {
            data[index] = instance.getValue(this.predictiveFieldIndexes[index]);
        }
        final NDArray<double[]> encodedInstance = new NDArray<>(data, 1, numberPredictiveFields);
        try {

            return this.jepInstance.submitEvaluation((jep) -> {

                // Retrieve the invoked function from the model
                final String defineFunction = String.format(
                        "classification_function = %s.%s",
                        ClassificationPythonModel.this.id,
                        classificationFunction
                );
                jep.eval(defineFunction);

                // Classify the instance and retrieve the encoded result wrapped in an Numpy array
                // for compatibility in case the implementation does not return a Numpy array.
                jep.set("encodedInstance", encodedInstance);

                final String callFunction = String.format(
                        pythonResultWrapping,
                        "classification_function(encodedInstance)"
                );
                //noinspection unchecked
                return (T) jep.getValue(callFunction);

            }).get();

        } catch (final Exception e) {
            logger.warn("Error during instance evaluation.");
            throw new RuntimeException("Error during instance evaluation.", e);
        }
    }

}
