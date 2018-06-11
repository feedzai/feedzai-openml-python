/*
 * The copyright of this file belongs to Feedzai. The file cannot be
 * reproduced in whole or in part, stored in a retrieval system,
 * transmitted in any form, or by any means electronic, mechanical,
 * photocopying, or otherwise, without the prior permission of the owner.
 *
 * (c) 2018 Feedzai, Strictly Confidential
 */
package com.feedzai.openml.python.jep.instance;

import com.google.common.util.concurrent.Uninterruptibles;
import jep.Jep;
import jep.JepException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Wrapper for a Jep object that runs it on a separate dedicated thread.
 * The thread will be on an infinite loop waiting for evaluations ({@link AbstractJepEvaluation}).
 *
 * @author Luis Reis (luis.reis@feedzai.com)
 * @since 0.1.0
 */
public class JepInstance implements Runnable {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(JepInstance.class);

    /**
     * Maximum time to wait for the Jep instance thread to finish.
     */
    private static final int THREAD_JOIN_TIMEOUT_SECONDS = 10;

    /**
     * Volatile boolean that stores whether the thread is (still) running.
     * Initially false, set to true when the instance is started.
     * Set to false when the Jep instance crashes or on stop.
     */
    private volatile boolean running;

    /**
     * Thread that holds the Jep instance.
     */
    private final Thread thread;

    /**
     * Queue with evaluations to be done.
     */
    private final BlockingQueue<AbstractJepEvaluation> evaluationQueue;

    /**
     * Constructor.
     */
    public JepInstance() {
        this.running = false;
        this.thread = new Thread(this);
        this.evaluationQueue = new LinkedBlockingQueue<>();
    }

    /**
     * Starts the thread and consequently the Jep instance inside it.
     * Sets running to true signalling this instance is ready to receive evaluations.
     */
    public void start() {
        this.thread.start();
        this.running = true;
    }

    /**
     * Sets running to false signalling the instance is no longer ready to receive evaluations.
     * Interrupts the thread that should be on an infinite evaluation loop.
     *
     * @implNote Currently this interrupt can catch an evaluation in which case it's future will never return.
     */
    public void stop() {
        this.running = false;
        this.thread.interrupt();
        Uninterruptibles.joinUninterruptibly(this.thread, THREAD_JOIN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Runnable that will run on the instance thread.
     * Creates a local Jep instance and enters an infinite read-eval loop doing evaluations ({@link AbstractJepEvaluation}).
     */
    @Override
    public void run() {

        try (final Jep jep = new Jep(false)) {
            while (this.running) {
                this.evaluationQueue.take().evaluate(jep);
            }
        } catch (final JepException e) {
            logger.error("A problem occurred that caused Jep to crash!", e);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            this.running = false;
        }

    }

    /**
     * Submits an evaluation to the evaluation queue.
     *
     * @param <T>                Type of the result of the evaluation.
     * @param evaluationFunction JepFunction with evaluation to be done by the Jep instance.
     * @return An {@link AbstractJepEvaluation} evaluation object that can be used to retrieve the result of the evaluation.
     * @implNote The result of the evaluation will be returned through {@link AbstractJepEvaluation} which is a CompletableFuture.
     */
    public <T> AbstractJepEvaluation<T> submitEvaluation(final JepFunction<T> evaluationFunction) {
        if (!this.running) {
            throw new RuntimeException("Jep instance is not running.");
        }

        final AbstractJepEvaluation<T> evaluation = new AbstractJepEvaluation<T>() {
            @Override
            public T apply(final Jep jep) throws JepException {
                return evaluationFunction.apply(jep);
            }
        };

        this.evaluationQueue.add(evaluation);

        return evaluation;
    }
}
