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
