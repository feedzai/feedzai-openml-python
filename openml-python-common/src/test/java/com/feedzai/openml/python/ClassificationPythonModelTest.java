package com.feedzai.openml.python;/*
 * The copyright of this file belongs to Feedzai. The file cannot be
 * reproduced in whole or in part, stored in a retrieval system,
 * transmitted in any form, or by any means electronic, mechanical,
 * photocopying, or otherwise, without the prior permission of the owner.
 *
 * © 2019 Feedzai, Strictly Confidential
 */

import com.feedzai.openml.data.schema.DatasetSchema;
import com.feedzai.openml.data.schema.FieldSchema;
import com.feedzai.openml.data.schema.NumericValueSchema;
import com.feedzai.openml.python.jep.instance.JepInstance;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the behaviour of a {@link ClassificationPythonModel}.
 *
 * @author Joao Sousa (joao.sousa@feedzai.com)
 * @since @@@feedzai.next.release@@@
 */
public class ClassificationPythonModelTest {

    /**
     * A field to use in the tests.
     */
    private static final FieldSchema FIELD_SCHEMA =
            new FieldSchema("field", 0, new NumericValueSchema(false));


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

}
