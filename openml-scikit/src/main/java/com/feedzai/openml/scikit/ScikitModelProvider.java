/*
 * The copyright of this file belongs to Feedzai. The file cannot be
 * reproduced in whole or in part, stored in a retrieval system,
 * transmitted in any form, or by any means electronic, mechanical,
 * photocopying, or otherwise, without the prior permission of the owner.
 *
 * (c) 2018 Feedzai, Strictly Confidential
 */

package com.feedzai.openml.scikit;

import com.feedzai.openml.provider.MachineLearningProvider;
import com.feedzai.openml.provider.descriptor.MLAlgorithmDescriptor;
import com.feedzai.util.algorithm.MLAlgorithmEnum;

import java.util.Optional;
import java.util.Set;

/**
 * Provider for Scikit-learn classification models.
 *
 * @author Luis Reis (luis.reis@feedzai.com)
 * @since 0.1.0
 */
public class ScikitModelProvider implements MachineLearningProvider<ClassificationScikitModelLoader> {

    /**
     * Name of this provider.
     */
    public static final String PROVIDER_NAME = "Scikit-learn";

    @Override
    public String getName() {
        return PROVIDER_NAME;
    }

    @Override
    public Set<MLAlgorithmDescriptor> getAlgorithms() {
        return MLAlgorithmEnum.getDescriptors(ScikitAlgorithm.values());
    }

    @Override
    public Optional<ClassificationScikitModelLoader> getModelCreator(final String algorithmName) {

        return MLAlgorithmEnum.getByName(ScikitAlgorithm.values(), algorithmName)
                .map(algorithm -> new ClassificationScikitModelLoader());
    }
}
