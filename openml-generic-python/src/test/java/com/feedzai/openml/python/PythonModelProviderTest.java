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

import com.feedzai.openml.data.Instance;
import com.feedzai.openml.data.schema.DatasetSchema;
import com.feedzai.openml.mocks.MockDataset;
import com.feedzai.openml.provider.exception.ModelLoadingException;
import com.feedzai.util.algorithm.GenericAlgorithm;
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
 * Tests for the {@link PythonModelProvider}.
 *
 * @author Paulo Pereira (paulo.pereira@feedzai.com)
 * @since 0.1.0
 */
//@Ignore("PULSEDEV-23370 - Tests using Jep currently don't work in Jenkins")
public class PythonModelProviderTest extends AbstractProviderModelLoadTest<ClassificationPythonModel,
        ClassificationPythonModelLoader, PythonModelProvider> {

    /**
     * Values of the target variable from the model stored.
     */
    private static final Set<String> TARGET_VALUES = ImmutableSet.of("0", "1", "2");

    /**
     * Number of predictive fields (i.e. non-target) in the test schema (must match the saved models).
     */
    private static final int TEST_PREDICTIVE_FIELDS = 4;

    /**
     * Number of fields to use by default in test datasets.
     */
    private static final int TEST_DATASET_INSTANCES_SIZE = 2;

    /**
     * The {@link DatasetSchema} to use in the tests.
     */
    private static final DatasetSchema TEST_SCHEMA = MockDataset.generateDefaultSchema(TARGET_VALUES, TEST_PREDICTIVE_FIELDS);

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
    private static final String FIRST_MODEL_FILE = "valid_classifier";

    /**
     * Name of the directory that contains a dummy model trained with a Random Forest. The model was created with a
     * schema similar to {@link #createDatasetSchema(Set)} ()};
     */
    private static final String SECOND_MODEL_FILE = "second_valid_classifier";

    /**
     * Name of the directories that contain dummy models that cause different model loading errors.
     * <ul>
     *     <li>INCOMPATIBLE_SCHEMA_MODEL_FILE - Model that works correctly but is not compatible with test schema.</li>
     *     <li>NO_PROBABILITY_MODEL_FILE - Model that didn't implement class probability correctly or doesn't support it.</li>
     *     <li>NO_PROBABILITY_IMPLEMENTED_MODEL_FILE - Model that doesn't have the class probability function.</li>
     *     <li>NO_CLASSIFY_IMPLEMENTED_MODEL_FILE - Model that doesn't have the classify function.</li>
     * </ul>
     */
    private static final String
            INCOMPATIBLE_SCHEMA_MODEL_FILE = "incompatible_schema_classifier",
            NO_PROBABILITY_MODEL_FILE = "no_probability_classifier",
            NO_PROBABILITY_IMPLEMENTED_MODEL_FILE = "no_probability_implemented",
            NO_CLASSIFY_IMPLEMENTED_MODEL_FILE = "no_classify_implemented";

    /**
     * Expected exception in tests.
     */
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    /**
     * Tests loading a model that is not compatible with the given schema.
     * Should throw a ModelLoadingException.
     *
     * @throws Exception Exception that we expect to be thrown.
     */
    @Test
    public void modelLoadingErrorWithClassify() throws Exception {
        //Model classification is not compatible with the given schema.
        this.exception.expect(ModelLoadingException.class);
        this.exception.expectMessage("is not compatible");
        this.exception.expectMessage("with the given schema");
        loadModel(getValidAlgorithm(), INCOMPATIBLE_SCHEMA_MODEL_FILE, TARGET_VALUES);
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
        this.exception.expectMessage("does not support");
        this.exception.expectMessage("class distribution");
        loadModel(getValidAlgorithm(), NO_PROBABILITY_MODEL_FILE, TARGET_VALUES);
    }

    /**
     * Tests loading a model that does not support class distribution classification.
     * Should throw a ModelLoadingException.
     *
     * @throws Exception Exception that we expect to be thrown.
     */
    @Test
    public void modelLoadingErrorWithClassifyNotImplemented() throws Exception {
        // Model does not implement classify function
        this.exception.expect(ModelLoadingException.class);
        this.exception.expectMessage("does not implement");
        this.exception.expectMessage("classify function");
        loadModel(getValidAlgorithm(), NO_CLASSIFY_IMPLEMENTED_MODEL_FILE, TARGET_VALUES);
    }

    /**
     * Tests loading a model that does not support class distribution classification.
     * Should throw a ModelLoadingException.
     *
     * @throws Exception Exception that we expect to be thrown.
     */
    @Test
    public void modelLoadingErrorWithProbabilityNotImplemented() throws Exception {
        // Model does not implement getClassDistribution function
        this.exception.expect(ModelLoadingException.class);
        this.exception.expectMessage("does not implement");
        this.exception.expectMessage("getClassDistribution function");
        loadModel(getValidAlgorithm(), NO_PROBABILITY_IMPLEMENTED_MODEL_FILE, TARGET_VALUES);
    }

    @Override
    public ClassificationPythonModel getFirstModel() throws ModelLoadingException {
        return loadModel(getValidAlgorithm(), getValidModelDirName(), TARGET_VALUES);
    }

    @Override
    public ClassificationPythonModel getSecondModel() throws ModelLoadingException {
        return loadModel(getValidAlgorithm(), SECOND_MODEL_FILE, TARGET_VALUES);
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
    public ClassificationPythonModelLoader getFirstMachineLearningModelLoader() {
        return getMachineLearningModelLoader(getValidAlgorithm());
    }

    @Override
    public PythonModelProvider getMachineLearningProvider() {
        return new PythonModelProvider();
    }

    @Override
    public Instance getDummyInstance() {
        return TEST_DATA_SET.getInstances().next();
    }

    @Override
    public MLAlgorithmEnum getValidAlgorithm() {
        return GenericAlgorithm.GENERIC_CLASSIFICATION;
    }

    @Override
    public String getValidModelDirName() {
        return FIRST_MODEL_FILE;
    }

    @Override
    public Set<String> getFirstModelTargetNominalValues() {
        return TARGET_VALUES;
    }

    @Override
    public DatasetSchema createDatasetSchema(final Set<String> targetValues) {
        return TEST_DATA_SET.getSchema();
    }
}
