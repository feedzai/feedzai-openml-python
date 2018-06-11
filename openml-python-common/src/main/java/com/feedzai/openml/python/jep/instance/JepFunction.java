/*
 * The copyright of this file belongs to Feedzai. The file cannot be
 * reproduced in whole or in part, stored in a retrieval system,
 * transmitted in any form, or by any means electronic, mechanical,
 * photocopying, or otherwise, without the prior permission of the owner.
 *
 * (c) 2018 Feedzai, Strictly Confidential
 */
package com.feedzai.openml.python.jep.instance;

import jep.Jep;
import jep.JepException;

/**
 * Functional interface for a Function that takes a Jep instance and may throw a JepException.
 * Definition of a {@link AbstractJepEvaluation } (Takes the Jep instance where it will run and returns the result of
 * the evaluation).
 *
 * @param <T>   Type of the result of a Jep evaluation.
 * @author Luis Reis (luis.reis@feedzai.com)
 * @since 0.1.0
 */
@FunctionalInterface
public interface JepFunction<T> {
    /**
     * Implementation of the Jep evaluation.
     *
     * @param jep Instance of jep that will be injected into this evaluation.
     * @return The result of the evaluation.
     * @throws JepException Exception that may be thrown during evaluation.
     */
    T apply(Jep jep) throws JepException;
}
