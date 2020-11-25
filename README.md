# Feedzai OpenML Providers for Python
[![Build Status](https://travis-ci.com/feedzai/feedzai-openml-python.svg?branch=hf-0.3.X)](https://travis-ci.com/feedzai/feedzai-openml-python)
[![codecov](https://codecov.io/gh/feedzai/feedzai-openml-python/branch/hf-0.3.X/graph/badge.svg)](https://codecov.io/gh/feedzai/feedzai-openml-python)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/4cb28e8bf4104cffa20812e207514b9b?branch=hf-0.3.X)](https://www.codacy.com/app/feedzai/feedzai-openml-python?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=feedzai/feedzai-openml-python&amp;utm_campaign=Badge_Grade)

Implementations of the Feedzai OpenML API to allow support for machine
learning models in [Python](https://www.python.org/)
using [Java Embedded Python](https://github.com/ninia/jep). 

## Modules

### Generic Python
[![Maven metadata URI](https://img.shields.io/maven-metadata/v/http/central.maven.org/maven2/com/feedzai/openml-generic-python/maven-metadata.xml.svg)](https://mvnrepository.com/artifact/com.feedzai/openml-generic-python)

The `openml-generic-python` module contains a provider that allows
developers to load Python code that conforms to a simple API.
This is the most powerful approach (yet more cumbersome) since models
can actually hold state.

Pull the provider from Maven Central:
```xml
<dependency>
  <groupId>com.feedzai</groupId>
  <artifactId>openml-generic-python</artifactId>
  <!-- See project tags for latest version -->
  <version>0.3.0</version>
</dependency>
```

### Scikit-learn
[![Maven metadata URI](https://img.shields.io/maven-metadata/v/http/central.maven.org/maven2/com/feedzai/openml-scikit/maven-metadata.xml.svg)](https://mvnrepository.com/artifact/com.feedzai/openml-scikit)

The implementation in the `openml-scikit` module adds support for models built with
[scikit-learn](http://scikit-learn.org/stable/index.html).

Pull this module from Maven Central:
```xml
<dependency>
  <groupId>com.feedzai</groupId>
  <artifactId>openml-scikit</artifactId>
  <!-- See project tags for latest version -->
  <version>0.3.0</version>
</dependency>
```

## Building
This is a Maven project which you can build using the following command:
```bash
mvn clean install
```

## Environment

To use these providers you need to have Python 3.6 with the following packages installed in your environment:

    * numpy 
    * scipy
    * jep (this requires JAVA_HOME to be configured)
    * scikit-learn (for the scikit provider)
    
Note that this section only describes the known prerequisites that are common to any model generated in Python.
Before importing a model you need to ensure that the required packages for that model are also installed.

## Running the tests 

To actually run the tests, two other configurations may be necessary for Jep to work properly:

* The java.library.path property needs to point to the Jep library. An approach for this that typically works is setting the `LD_LIBRARY_PATH` environment variable: `export LD_LIBRARY_PATH=/...path to.../python3.6/site-packages/jep:$LD_LIBRARY_PATH`
    
* Depending on the environment and package manager it may also be necessary to set the `LD_PRELOAD` variable to include the Python library: `export LD_PRELOAD=/...path to.../lib/libpython3.6m.so`


Feedzai has built a helpful docker image for testing, [available on docker hub](https://hub.docker.com/r/feedzai/oracle-jep-miniconda/),
that is being used in this repository's continuous integration. See the [travis-ci configuration](.travis.yml) commands
on how to use it.
The image's [Dockerfile](https://github.com/feedzai/oracle-jep-miniconda/blob/master/Dockerfile) also provides an example 
of the environment installation.
