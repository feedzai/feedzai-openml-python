/*
 * The copyright of this file belongs to Feedzai. The file cannot be
 * reproduced in whole or in part, stored in a retrieval system,
 * transmitted in any form, or by any means electronic, mechanical,
 * photocopying, or otherwise, without the prior permission of the owner.
 *
 * (c) 2018 Feedzai, Strictly Confidential
 */
package com.feedzai.openml.scikit;

import com.feedzai.openml.provider.descriptor.MLAlgorithmDescriptor;
import com.feedzai.openml.provider.descriptor.MachineLearningAlgorithmType;
import com.feedzai.util.algorithm.MLAlgorithmEnum;
import com.google.common.collect.ImmutableSet;

/**
 * Enumeration of algorithms supported by Scikit-learn provider.
 *
 * @author Luis Reis (luis.reis@feedzai.com)
 * @since 0.1.0
 */
public enum ScikitAlgorithm implements MLAlgorithmEnum {

    /**
     * Default Classification algorithm used for all Scikit-learn models.
     */
    DEFAULT_CLASSIFICATION(MLAlgorithmEnum.createDescriptor(
            "Classification Algorithm",
            ImmutableSet.of(),
            MachineLearningAlgorithmType.MULTI_CLASSIFICATION,
            "http://scikit-learn.org/stable/documentation.html"
    ));

    /**
     * {@link MLAlgorithmDescriptor} for this algorithm.
     */
    public final MLAlgorithmDescriptor descriptor;

    /**
     * Constructor.
     *
     * @param descriptor {@link MLAlgorithmDescriptor} for this algorithm.
     */
    ScikitAlgorithm(final MLAlgorithmDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public MLAlgorithmDescriptor getAlgorithmDescriptor() {
        return this.descriptor;
    }
}
