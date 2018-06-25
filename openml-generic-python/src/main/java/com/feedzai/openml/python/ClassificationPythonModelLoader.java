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
