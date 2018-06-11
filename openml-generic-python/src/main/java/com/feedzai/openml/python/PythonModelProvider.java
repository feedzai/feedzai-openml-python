/*
 * The copyright of this file belongs to Feedzai. The file cannot be
 * reproduced in whole or in part, stored in a retrieval system,
 * transmitted in any form, or by any means electronic, mechanical,
 * photocopying, or otherwise, without the prior permission of the owner.
 *
 * (c) 2018 Feedzai, Strictly Confidential
 */
package com.feedzai.openml.python;

import com.feedzai.openml.provider.MachineLearningProvider;
import com.feedzai.openml.provider.descriptor.MLAlgorithmDescriptor;
import com.feedzai.util.algorithm.GenericAlgorithm;
import com.feedzai.util.algorithm.MLAlgorithmEnum;

import java.util.Optional;
import java.util.Set;

/**
 * Provider for Generic user made Python models.
 * <p>
 * @see ClassificationPythonModelLoader for information on how these models should be implemented and formatted for
 * use with this provider.
 *
 * @author Luis Reis (luis.reis@feedzai.com)
 * @since 0.1.0
 */
public class PythonModelProvider implements MachineLearningProvider<ClassificationPythonModelLoader> {

    /**
     * Name of this provider.
     */
    private static final String PROVIDER_NAME = "Custom Python Model";

    @Override
    public String getName() {
        return PROVIDER_NAME;
    }

    @Override
    public Set<MLAlgorithmDescriptor> getAlgorithms() {
        return MLAlgorithmEnum.getDescriptors(GenericAlgorithm.values());
    }

    @Override
    public Optional<ClassificationPythonModelLoader> getModelCreator(final String algorithmName) {

        return MLAlgorithmEnum.getByName(GenericAlgorithm.values(), algorithmName)
                .map(algorithm -> new ClassificationPythonModelLoader());
    }
}
