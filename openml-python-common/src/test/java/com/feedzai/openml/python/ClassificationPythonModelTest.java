/*
 * Copyright (c) 2019 Feedzai
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

import com.feedzai.openml.data.schema.CategoricalValueSchema;
import com.feedzai.openml.data.schema.DatasetSchema;
import com.feedzai.openml.data.schema.FieldSchema;
import com.feedzai.openml.data.schema.NumericValueSchema;
import com.feedzai.openml.mocks.MockInstance;
import com.feedzai.openml.python.jep.instance.JepInstance;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the behaviour of a {@link ClassificationPythonModel}.
 *
 * @author Joao Sousa (joao.sousa@feedzai.com)
 * @since 0.2.0
 */
public class ClassificationPythonModelTest {

    /**
     * A numerical field to use in the tests.
     */
    private static final FieldSchema FIELD_SCHEMA =
            new FieldSchema("field", 0, new NumericValueSchema(false));

    /**
     * A categorical field to use in the tests.
     */
    private static final FieldSchema CATEGORICAL_FIELD_SCHEMA =
            new FieldSchema("categorical", 1, new CategoricalValueSchema(false, ImmutableSet.of("this", "that")));

    /**
     * The wrapper for the Jep object used in the tests.
     */
    private JepInstance jepInstance;

    /**
     * Initializes an instance of {@link JepInstance}.
     */
    @Before
    public void setUp() {
        this.jepInstance = new JepInstance();
        this.jepInstance.start();
    }

    /**
     * Tests that by passing a dataset schema with no target variable, will make the model creation fail.
     */
    @Test
    public final void testSchemaWithoutTargetVariable() {
        final ImmutableList<FieldSchema> fields = ImmutableList.of(FIELD_SCHEMA);
        final DatasetSchema schema = new DatasetSchema(fields);

        assertThatThrownBy(() -> new ClassificationPythonModel(this.jepInstance, schema, "classificationModel"))
                .as("A classification model created with a schema with no target variable")
                .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Tests that a classifier which classifies an instance with an invalid target value will cause the model to return
     * NullPointerException.
     */
    @Test
    public final void testInvalidClass() throws URISyntaxException, ExecutionException, InterruptedException {
        final Path modelPath = Paths.get(getClass().getResource("/dummy_model").toURI());
        final String id = "classificationModel";
        final List<FieldSchema> fields = ImmutableList.of(FIELD_SCHEMA, CATEGORICAL_FIELD_SCHEMA);
        final String illegalTargetValue = "those";
        final DatasetSchema schema = new DatasetSchema(1, fields);
        final Random random = new Random();

        this.jepInstance.submitEvaluation(jep -> {
            // Add the model folder to the python import path
            jep.eval("import sys");
            jep.eval(String.format("sys.path.append(\"%s\")", modelPath.toAbsolutePath()));

            // Import the Classifier custom class and store an instance of it in a variable with the name passed in "id"
            jep.eval("from classifier import Classifier");
            jep.eval(String.format("%s = Classifier('%s')", id, illegalTargetValue));

            return null;
        }).get();

        final ClassificationPythonModel model = new ClassificationPythonModel(this.jepInstance, schema, id, "classify", "getClassDistribution");

        assertThatThrownBy(() -> model.classify(new MockInstance(schema, random)))
                .as("A classifier that does not return a valid target value will fail with a null pointer exception")
                .isInstanceOf(NullPointerException.class);
    }

}
