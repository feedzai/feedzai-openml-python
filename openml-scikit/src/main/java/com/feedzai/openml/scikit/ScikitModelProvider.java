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
