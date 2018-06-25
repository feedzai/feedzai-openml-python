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

import java.util.concurrent.CompletableFuture;

/**
 * Definition of an evaluation to be run on a {@link JepInstance}.
 * <p>
 * Implements {@link JepFunction} which is the definition of the evaluation to run in Jep.
 * <p>
 * Extends CompletableFuture as a way to synchronously wait for the result of the evaluation.
 *
 * @param <T> Type of the result of the evaluation that will be returned.
 * @author Luis Reis (luis.reis@feedzai.com)
 * @since 0.1.0
 */
public abstract class AbstractJepEvaluation<T> extends CompletableFuture<T> implements JepFunction<T> {

    /**
     * Function that will be called on the JepInstance thread to do this evaluation.
     * In case the evaluation throws a JepException this exception will be returned to the calling thread
     * through the completable future.
     *
     * @param jep Local instance of Jep in the thread to be used in this evaluation.
     */
    void evaluate(final Jep jep) {
        try {
            this.complete(this.apply(jep));
        } catch (final JepException e) {
            this.completeExceptionally(e);
        }
    }
}
