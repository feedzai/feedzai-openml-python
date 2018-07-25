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

import com.feedzai.openml.data.schema.DatasetSchema;
import com.feedzai.openml.mocks.MockDataset;
import com.feedzai.openml.model.ClassificationMLModel;
import com.feedzai.openml.provider.exception.ModelLoadingException;
import com.google.common.collect.ImmutableSet;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for ensuring that XGboost Scikit models work.
 *
 * @author Nuno Diegues (nuno.diegues@feedzai.com)
 * @since @@@feedzai.next.release@@@
 */
public class ScikitXGboostTest {

    /**
     * Values of the target variable from the model stored.
     */
    private static final Set<String> TARGET_VALUES = ImmutableSet.of("0.0", "1.0");

    /**
     * Number of predictive fields (i.e. non-target) in the test schema (must match the saved models).
     */
    private static final int TEST_PREDICTIVE_FIELDS = 4;

    /**
     * The {@link DatasetSchema} to use in the tests.
     */
    private static final DatasetSchema TEST_SCHEMA = MockDataset.generateDefaultSchema(TARGET_VALUES, TEST_PREDICTIVE_FIELDS);

    /**
     * Number of instances to use by default in test datasets.
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
     * Simple regression test for https://github.com/feedzai/feedzai-openml-python/issues/20
     */
    @Test
    public void test() throws ModelLoadingException {
        final ClassificationScikitModelLoader modelLoader = new ScikitModelProvider().getModelCreator(
                ScikitAlgorithm.DEFAULT_CLASSIFICATION.getAlgorithmDescriptor().getAlgorithmName()
        ).get();

        final String modelPath = this.getClass().getResource("/xgboost").getPath();
        final ClassificationMLModel model = modelLoader.loadModel(Paths.get(modelPath), TEST_DATA_SET.getSchema());

        TEST_DATA_SET.getInstances().forEachRemaining(instance ->
                assertThat(Arrays.stream(model.getClassDistribution(instance)).sum())
                        .as("sum of the class distribution")
                        .isCloseTo(1.0D, Assertions.within(0.01D))
        );
    }

}
