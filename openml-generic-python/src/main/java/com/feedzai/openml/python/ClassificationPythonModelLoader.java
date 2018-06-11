/*
 * The copyright of this file belongs to Feedzai. The file cannot be
 * reproduced in whole or in part, stored in a retrieval system,
 * transmitted in any form, or by any means electronic, mechanical,
 * photocopying, or otherwise, without the prior permission of the owner.
 *
 * (c) 2018 Feedzai, Strictly Confidential
 */
package com.feedzai.openml.python;

import com.feedzai.openml.python.jep.instance.JepInstance;

import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

/**
 * Model loader for a Generic user made python model implementation.
 *
 * The model should provide two functions:
 * <ul>
 *     <li>classify - That takes an array of instances and returns an array with the classification of each one.</li>
 *     <li>getClassDistribution - That takes an array of instances and returns an array with the class
 *     probability distribution of each one.</li>
 * </ul>
 *
 * The main class of the model should be named "Classifier" and be located on a file named "classifier.py" in the root
 * of the folder that contains the model implementation (passed in {@code modelPath}).
 *
 * @author Luis Reis (luis.reis@feedzai.com)
 * @since 0.1.0
 */
public class ClassificationPythonModelLoader extends AbstractClassificationPythonModelLoaderImpl {

    @Override
    protected void modelLoadLogic(final JepInstance jepInstance,
                                  final String id,
                                  final Path modelPath)
            throws InterruptedException, ExecutionException {

        jepInstance.submitEvaluation((jep) -> {

            // Add the model folder to the python import path
            jep.eval("import sys");
            jep.eval(String.format("sys.path.append(\"%s\")", modelPath.toAbsolutePath()));

            // Import the Classifier custom class and store an instance of it in a variable with the name passed in "id"
            jep.eval("from classifier import Classifier");
            jep.eval(String.format("%s = Classifier()", id));

            return null;
        }).get();

    }
}
