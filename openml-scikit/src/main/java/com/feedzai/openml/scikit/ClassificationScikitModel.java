/*
 * The copyright of this file belongs to Feedzai. The file cannot be
 * reproduced in whole or in part, stored in a retrieval system,
 * transmitted in any form, or by any means electronic, mechanical,
 * photocopying, or otherwise, without the prior permission of the owner.
 *
 * (c) 2018 Feedzai, Strictly Confidential
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
