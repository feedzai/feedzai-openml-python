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
import com.feedzai.openml.provider.descriptor.fieldtype.ParamValidationError;
import com.feedzai.openml.provider.exception.ModelLoadingException;
import com.feedzai.openml.python.AbstractClassificationPythonModelLoaderImpl;
import com.feedzai.openml.python.ClassificationPythonModel;
import com.feedzai.openml.python.jep.instance.JepInstance;
import com.feedzai.util.load.LoadModelUtils;
import com.feedzai.util.validate.ValidationUtils;
import com.google.common.collect.ImmutableList;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Model loader for Scikit-learn classification models.
 *
 * @author Luis Reis (luis.reis@feedzai.com)
 * @since 0.1.0
 */
public class ClassificationScikitModelLoader extends AbstractClassificationPythonModelLoaderImpl {

    @Override
    public List<ParamValidationError> validateForLoad(final Path modelPath,
                                                      final DatasetSchema schema,
                                                      final Map<String, String> params) {

        final ImmutableList.Builder<ParamValidationError> errors = ImmutableList.builder();

        errors.addAll(super.validateForLoad(modelPath, schema, params));
        errors.addAll(ValidationUtils.validateModelInDir(modelPath));

        ValidationUtils.validateCategoricalSchema(schema).ifPresent(errors::add);

        return errors.build();
    }

    @Override
    protected void modelLoadLogic(final JepInstance jepInstance,
                                  final String id,
                                  final Path modelPath)
            throws InterruptedException, ExecutionException, ModelLoadingException {

        final String modelFilePath = LoadModelUtils.getModelFilePath(modelPath).toAbsolutePath().toString();

        jepInstance.submitEvaluation((jep) -> {
            jep.eval("from sklearn.externals import joblib");
            jep.eval(String.format("%s = joblib.load('%s')", id, modelFilePath));

            return null;
        }).get();

    }

    @Override
    protected ClassificationPythonModel getModelImpl(final DatasetSchema schema,
                                                     final JepInstance jepInstance,
                                                     final String id) {
        return new ClassificationScikitModel(
                jepInstance,
                schema,
                id
        );
    }
}
