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
