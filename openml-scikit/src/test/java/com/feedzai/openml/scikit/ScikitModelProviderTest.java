/*
 * The copyright of this file belongs to Feedzai. The file cannot be
 * reproduced in whole or in part, stored in a retrieval system,
 * transmitted in any form, or by any means electronic, mechanical,
 * photocopying, or otherwise, without the prior permission of the owner.
 *
 * (c) 2018 Feedzai, Strictly Confidential
 */

package com.feedzai.openml.scikit;

import com.feedzai.openml.data.Instance;
import com.feedzai.openml.data.schema.DatasetSchema;
import com.feedzai.openml.mocks.MockDataset;
import com.feedzai.openml.provider.exception.ModelLoadingException;
import com.feedzai.openml.python.ClassificationPythonModel;
import com.feedzai.util.algorithm.MLAlgorithmEnum;
import com.feedzai.util.provider.AbstractProviderModelLoadTest;
import com.google.common.collect.ImmutableSet;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Tests for the {@link ScikitModelProvider}.
 *
 * @author Paulo Pereira (paulo.pereira@feedzai.com)
 * @since 0.1.0
 */
//@Ignore("PULSEDEV-23370 - Tests using Jep currently don't work in Jenkins")
public class ScikitModelProviderTest extends AbstractProviderModelLoadTest<ClassificationPythonModel,
        ClassificationScikitModelLoader, ScikitModelProvider> {

    /**
     * Values of the target variable from the model stored.
     */
    private static final Set<String> TARGET_VALUES = ImmutableSet.of("0", "1", "2");

    /**
     * Number of predictive fields (i.e. non-target) in the test schema (must match the saved models).
     */
    private static final int TEST_PREDICTIVE_FIELDS = 4;

    /**
     * The {@link DatasetSchema} to use in the tests.
     */
    private static final DatasetSchema TEST_SCHEMA = MockDataset.generateDefaultSchema(TARGET_VALUES, TEST_PREDICTIVE_FIELDS);

    /**
     * Number of fields to use by default in test datasets.
     */
    private static final int TEST_DATASET_INSTANCES_SIZE = 2;

    /**
     * Test dataset used in tests.
     */
    private static final MockDataset TEST_DATA_SET = new MockDataset(
            TEST_SCHEMA,
            TEST_DATASET_INSTANCES_SIZE,
            new Random(123456789)
    );

    /**
     * Name of the directory that contains a dummy model trained with a SVM. The model was created with a schema similar
     * to {@link #createDatasetSchema(Set)} ()};
     */
    private static final String SVM_MODEL_FILE = "svm";

    /**
     * Name of the directory that contains a dummy model trained with a Random Forest. The model was created with a
     * schema similar to {@link #createDatasetSchema(Set)} ()};
     */
    private static final String RF_MODEL_FILE = "rf";

    /**
     * Name of the directory that contains a dummy model does not support class distribution classification. The model
     * was created with a schema similar to {@link #createDatasetSchema(Set)} ()};
     */
    private static final String NO_PROBABILITY_MODEL_FILE = "no_probability";

    /**
     * Expected exception in tests.
     */
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    /**
     * Tests loading a model that does not support class distribution classification.
     * Should throw a ModelLoadingException.
     *
     * @throws Exception Exception that we expect to be thrown.
     */
    @Test
    public void modelLoadingErrorWithClassDistribution() throws Exception {
        // Model does not support class distribution
        this.exception.expect(ModelLoadingException.class);
        this.exception.expectMessage("does not implement");
        this.exception.expectMessage("predict_proba function");
        loadModel(getValidAlgorithm(), NO_PROBABILITY_MODEL_FILE, TARGET_VALUES);
    }

    @Override
    public ClassificationPythonModel getFirstModel() throws ModelLoadingException {
        return loadModel(getValidAlgorithm(), getValidModelDirName(), TARGET_VALUES);
    }

    @Override
    public ClassificationPythonModel getSecondModel() throws ModelLoadingException {
        return loadModel(getValidAlgorithm(), RF_MODEL_FILE, TARGET_VALUES);
    }

    @Override
    public Set<Integer> getClassifyValuesOfFirstModel() {
        return TARGET_VALUES.stream().map(Integer::parseInt).collect(Collectors.toSet());
    }

    @Override
    public Set<Integer> getClassifyValuesOfSecondModel() {
        return getClassifyValuesOfFirstModel();
    }

    @Override
    public ClassificationScikitModelLoader getFirstMachineLearningModelLoader() {
        return getMachineLearningModelLoader(getValidAlgorithm());
    }

    @Override
    public ScikitModelProvider getMachineLearningProvider() {
        return new ScikitModelProvider();
    }

    @Override
    public Instance getDummyInstance() {
        return TEST_DATA_SET.getInstances().next();
    }

    @Override
    public DatasetSchema createDatasetSchema(final Set<String> targetValues) {
        return TEST_DATA_SET.getSchema();
    }

    @Override
    public MLAlgorithmEnum getValidAlgorithm() {
        return ScikitAlgorithm.DEFAULT_CLASSIFICATION;
    }

    @Override
    public String getValidModelDirName() {
        return SVM_MODEL_FILE;
    }

    @Override
    public Set<String> getFirstModelTargetNominalValues() {
        return TARGET_VALUES;
    }
}
