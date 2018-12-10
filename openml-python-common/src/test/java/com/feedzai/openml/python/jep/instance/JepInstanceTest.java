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

package com.feedzai.openml.python.jep.instance;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Contains the tests for the wrapper for the Jep object.
 *
 * @author Paulo Pereira (paulo.pereira@feedzai.com)
 * @since 0.1.5
 */
public class JepInstanceTest {

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
     * Tears downs the instance of {@link JepInstance}.
     */
    @After
    public void tearDown() {
        this.jepInstance.stop();
    }

    /**
     * Tests the submission of evaluations on a {@link JepInstance}.
     *
     * @throws Exception If there is a problem while getting the result.
     */
    @Test
    public void submitEvaluationTest() throws Exception {
        final String result = this.jepInstance.submitEvaluation((jep) -> jep.getValue("1 + 2")).get().toString();
        assertThat(result)
            .as("The result of the evaluation")
            .isEqualTo("3");
    }
}
