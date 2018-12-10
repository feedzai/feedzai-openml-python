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

import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.Set;

/**
 * Class responsible for parsing a XML file in order to retrieve the name of the Python modules to be shared across
 * sub-interpreters.
 *
 * @author Paulo Pereira (paulo.pereira@feedzai.com)
 * @since 0.1.5
 */
public class SharedModulesParser {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(SharedModulesParser.class);

    /**
     * Default value for {@link #xmlFile}.
     */
    private static final String DEFAULT_XML_FILE = "python-packages.xml";

    /**
     * The filename of a XML file with the name of the Python modules to be shared across sub-interpreters.
     */
    private final String xmlFile;

    /**
     * Constructor.
     *
     * @param xmlFileName The name of a XML file.
     */
    public SharedModulesParser(final String xmlFileName) {
        this.xmlFile = xmlFileName;
    }

    /**
     * Constructor.
     */
    public SharedModulesParser() {
        this(DEFAULT_XML_FILE);
    }

    /**
     * Gets the {@link InputStream} of {@link #xmlFile} that exists in the current classpath.
     *
     * @return The {@link InputStream} of {@link #xmlFile}.
     */
    private InputStream getXMLInputStream() {
        final ClassLoader classLoader = getClass().getClassLoader();
        return classLoader.getResourceAsStream(this.xmlFile);
    }

    /**
     * Parses the {@link #xmlFile} to retrieve the name of the Python modules to be shared across sub-interpreters.
     *
     * @return A {@link Set} with the name of the Python modules to be shared across sub-interpreters.
     * @throws Exception If there is an error while parsing {@link #xmlFile}.
     */
    private Set<String> parseXMLFile() throws Exception {
        final ImmutableSet.Builder<String> sharedModulesBuilder = ImmutableSet.builder();
        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        try (final InputStream xmlFile = getXMLInputStream()) {
            final Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            final NodeList nList = doc.getElementsByTagName("package");
            for (int i = 0; i < nList.getLength(); i++) {
                sharedModulesBuilder.add(nList.item(i).getFirstChild().getNodeValue());
            }
        }
        return sharedModulesBuilder.build();
    }

    /**
     * Retrieves a {@link Set} with the name of the Python modules to be shared across sub-interpreters.
     *
     * @return The name of the Python modules to be shared across sub-interpreters.
     */
    public Set<String> getSharedModules() {
        Set<String> sharedModules = ImmutableSet.of();
        try {
            sharedModules = parseXMLFile();
        } catch (final Exception e) {
            logger.warn("Problem while getting the XML file with the Python modules to be shared.", e);
        }
        return sharedModules;
    }
}
