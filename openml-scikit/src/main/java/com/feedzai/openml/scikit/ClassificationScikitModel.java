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
import com.feedzai.openml.python.ClassificationPythonModel;
import com.feedzai.openml.python.jep.instance.JepInstance;

/**
 * Specific implementation of {@link ClassificationPythonModel} for scikit-learn.
 *
 * @author Henrique Costa (henrique.costa@feedzai.com)
 * @since 0.1.0
 */
public class ClassificationScikitModel extends ClassificationPythonModel {

    /**
     * Function name to be called on the Scikit model to predict the instance class.
     *
     * @see ClassificationPythonModel#classifyFunctionName
     */
    private static final String CLASSIFY_FUNCTION_NAME = "predict";

    /**
     * function name to be called on the Scikit model to obtain a class probability distribution.
     *
     * @see ClassificationPythonModel#getClassDistributionFunctionName
     */
    private static final String GETCLASSDISTRIBUTION_FUNCTION_NAME = "predict_proba";

    /**
     * Constructor for this model's representation.
     *
     * @param jepInstance Jep instance where this model is loaded.
     * @param schema      Schema of the instances this model receives.
     * @param id          Name of the variable that stores this model in the Jep instance.
     */
    ClassificationScikitModel(final JepInstance jepInstance,
                              final DatasetSchema schema,
                              final String id) {
        super(jepInstance, schema, id, CLASSIFY_FUNCTION_NAME, GETCLASSDISTRIBUTION_FUNCTION_NAME);
    }
}
