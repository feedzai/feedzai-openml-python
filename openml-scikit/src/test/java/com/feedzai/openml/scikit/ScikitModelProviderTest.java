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

package com.feedzai.openml.scikit;

import com.feedzai.openml.data.Instance;
import com.feedzai.openml.data.schema.DatasetSchema;
import com.feedzai.openml.mocks.MockDataset;
import com.feedzai.openml.model.ClassificationMLModel;
import com.feedzai.openml.provider.exception.ModelLoadingException;
import com.feedzai.openml.python.ClassificationPythonModel;
import com.feedzai.openml.util.algorithm.MLAlgorithmEnum;
import com.feedzai.openml.util.provider.AbstractProviderModelLoadTest;
import com.google.common.collect.ImmutableSet;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Tests for the {@link ScikitModelProvider}.
 *
 * @author Paulo Pereira (paulo.pereira@feedzai.com)
 * @since 0.1.0
 */
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
     * Verifies that the {@link ClassificationMLModel#classify(Instance)} " returns the index of the greatest value in
     * the class probability distribution produced by the calling
     * {@link ClassificationMLModel#getClassDistribution(Instance)} on the model
     *
     * @see ClassificationMLModel
     */
    @Test
    public void canGetClassDistributionMaxValueIndex() throws Exception {

        final ClassificationPythonModel model = getFirstModel();

        final Instance instance = getDummyInstance();

        this.canGetClassDistributionMaxValueIndex(model, instance);

    }

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
    public Instance getDummyInstanceDifferentResult() {
        final Iterator<Instance> instances = TEST_DATA_SET.getInstances();
        // ignores the first instance because it is being used by #getDummyInstance()
        instances.next();
        return instances.next();
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
