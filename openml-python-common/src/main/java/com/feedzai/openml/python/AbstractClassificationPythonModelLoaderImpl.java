/*
 * Copyright (c) 2018 Feedzai
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.feedzai.openml.python;

import com.feedzai.openml.data.schema.DatasetSchema;
import com.feedzai.openml.model.MachineLearningModel;
import com.feedzai.openml.provider.descriptor.fieldtype.ParamValidationError;
import com.feedzai.openml.provider.exception.ModelLoadingException;
import com.feedzai.openml.provider.model.MachineLearningModelLoader;
import com.feedzai.openml.python.jep.instance.JepInstance;
import com.feedzai.openml.util.load.LoadSchemaUtils;
import com.feedzai.openml.util.validate.ClassificationValidationUtils;
import com.feedzai.openml.util.validate.ValidationUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Abstract implementation of a model loader for classification models implemented in Python.
 * Any model loader that loads python models should extend this class.
 * In theory the only thing a python model loader should need to implement is the
 * {@link #getModelImpl(DatasetSchema, JepInstance, String)} method, and even then it can be made very easy if the
 * {@link ClassificationPythonModel} can be reused.
 *
 * @author Luis Reis (luis.reis@feedzai.com)
 * @since 0.1.0
 */
public abstract class AbstractClassificationPythonModelLoaderImpl implements MachineLearningModelLoader<ClassificationPythonModel> {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractClassificationPythonModelLoaderImpl.class);

    @Override
    public ClassificationPythonModel loadModel(final Path modelPath,
                                               final DatasetSchema schema) throws ModelLoadingException {

        logger.info("Trying to load a model in path [{}]...", modelPath);

        ClassificationValidationUtils.validateParamsModelToLoad(this, modelPath, schema, ImmutableMap.of());

        final JepInstance jepInstance = new JepInstance();
        final String id = generateNamesafeId();

        try {
            // Start the Jep instance thread
            jepInstance.start();

            // Run provider specific model loading logic
            modelLoadLogic(jepInstance, id, modelPath);

            // Load common imports
            jepInstance.submitEvaluation((jep) -> {
                jep.eval("import numpy");
                return null;
            }).get();

        } catch (final InterruptedException | ExecutionException e) {
            logger.error("Could not load the model [{}].", modelPath, e);
            throw new ModelLoadingException("Error while loading the model.", e);
        }

        // Since only classification is supported, we need to validate that the target variable is compatible
        final Optional<ParamValidationError> validationError = ValidationUtils.validateCategoricalSchema(schema);

        Preconditions.checkArgument(!validationError.isPresent(), "Target variable must be categorical: %s", validationError);

        // Creates the model object giving it the jep instance containing the imported model
        final ClassificationPythonModel model = getModelImpl(schema, jepInstance, id);

        validateLoadedModel(schema, jepInstance, id, model);

        logger.info("Model loaded successfully.");

        return model;
    }

    /**
     * Gets the actual instance of the {@link MachineLearningModel} to use.
     *
     * @param schema The {@link DatasetSchema} of the incoming instances.
     * @param jepInstance The {@link JepInstance} that provides access to the python environment.
     * @param id The identifier that has been assigned to the model.
     * @return The concrete model implementation.
     */
    protected ClassificationPythonModel getModelImpl(final DatasetSchema schema,
                                                     final JepInstance jepInstance,
                                                     final String id) {
        return new ClassificationPythonModel(jepInstance, schema, id);
    }

    /**
     * Specific implementation of python logic that loads the model.
     *
     * @param jepInstance Instance of Jep that will store and handle the model.
     * @param id          Name of the variable on the Jep environment that will hold the model.
     * @param modelPath   Path to the model.
     * @throws InterruptedException  Exception that is thrown in case the Jep instance is closed during or before
     *                               the evaluation of the model loading function.
     * @throws ExecutionException    Exception that is thrown in case a JepException is thrown while loading the model.
     * @throws ModelLoadingException Generic exception for problems that may occur during model loading.
     */
    protected abstract void modelLoadLogic(final JepInstance jepInstance,
                                           final String id,
                                           final Path modelPath)
            throws InterruptedException, ExecutionException, ModelLoadingException;

    /**
     * Generates a unique id to use as a variable name to store the loaded model in a Jep instance.
     * This is a UUID encoded with an URL safe Base64 encoder.
     *
     * @return Variable name safe id.
     */
    private String generateNamesafeId() {
        final byte[] base64encodedUUID = Base64.getUrlEncoder().encode(UUID.randomUUID().toString().getBytes());
        return String.format("model_%s", new String(base64encodedUUID));
    }


    /**
     * Validates a loaded python model.
     * The model should be able to support calls to a classification and class distribution with a mocked instance based
     * on the provided model schema.
     *
     * @param schema      Schema of the loaded model.
     * @param jepInstance Instance of Jep where the model was loaded.
     * @param id          Name of the variable inside the Jep instance where the model was stored.
     * @param model       Loaded model.
     * @throws ModelLoadingException Exception thrown when the model has validation problems.
     */
    private void validateLoadedModel(final DatasetSchema schema,
                                     final JepInstance jepInstance,
                                     final String id,
                                     final ClassificationPythonModel model)
            throws ModelLoadingException {

        // Checks if functions exist
        model.validate(jepInstance, id);

        ClassificationValidationUtils.validateClassificationModel(schema, model);
    }

    @Override
    public List<ParamValidationError> validateForLoad(final Path modelPath, final DatasetSchema schema, final Map<String, String> params) {
        return ValidationUtils.baseLoadValidations(schema, params);
    }

    @Override
    public DatasetSchema loadSchema(final Path modelPath) throws ModelLoadingException {
        return LoadSchemaUtils.datasetSchemaFromJson(modelPath);
    }

}
