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

package com.feedzai.openml.python.modules;

import org.assertj.core.util.Files;
import org.junit.Test;

import java.io.File;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the retrieving of the modules to be shared across sub-interpreters from a XML file.
 *
 * @author Paulo Pereira (paulo.pereira@feedzai.com)
 * @since 0.1.5
 */
public class SharedModulesParserTest {

    /**
     * Tests the retrieving of the shared modules from a valid XML file with name of two modules to be shared.
     */
    @Test
    public void validXMLFileTest() {
        final Set<String> sharedPythonPackages = new SharedModulesParser().getSharedModules();
        assertThat(sharedPythonPackages)
                .as("Set of shared modules.")
                .hasSize(2)
                .contains("my_package_1", "my_package_2");
    }

    /**
     * Tests the retrieving of the shared modules from an empty file.
     */
    @Test
    public void emptyFileTest() {
        final File file = Files.newTemporaryFile();
        file.deleteOnExit();

        final SharedModulesParser sharedModulesParser = new SharedModulesParser(file.getAbsolutePath());
        assertThat(sharedModulesParser.getSharedModules())
                .as("Set of shared modules.")
                .hasSize(0);
    }

    /**
     * Tests the retrieving of the shared modules from a non existing file.
     */
    @Test
    public void nonExistingFileTest() {
        final SharedModulesParser sharedModulesParser = new SharedModulesParser("non_existing_file");
        assertThat(sharedModulesParser.getSharedModules())
                .as("Set of shared modules.")
                .hasSize(0);
    }
}
