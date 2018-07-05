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

import com.feedzai.openml.provider.MachineLearningProvider;
import com.feedzai.openml.provider.descriptor.MLAlgorithmDescriptor;
import com.feedzai.openml.util.algorithm.GenericAlgorithm;
import com.feedzai.openml.util.algorithm.MLAlgorithmEnum;

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
