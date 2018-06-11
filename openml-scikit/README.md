Scikit-learn ML Api Provider Installation
------------------------------------------------------------

This module uses JEP (https://github.com/ninia/jep) to load and use models trained in scikit-learn.
It also depends on a couple of Python libraries that should be on your Java library path.

This module compiles without any installation depending on a default Jar file for Jep in maven.
To use and run tests from this module you should install and compile JEP for your python environment.

First follow the instructions on the openml-python/README to setup your Python environment.

Then you just have to install Scikit-learn and its dependencies on your environment:
1. Load/Activate your environment:
    - source activate myenv
2. Install scikit dependencies with conda:
    - conda install scipy scikit-learn

### Running on OSX

When running on macOS it has proven challenging to configure environment variables to properly load the jep dynamic 
library's dependencies.
As such, a workaround can be performed by hardcoding dependency paths on the jep lib as shown below (adapt to your environment).
```bash
(scikit-env-test) > [/usr/local/anaconda3/envs/scikit-env-test/lib]:$ cp python3.6/site-packages/jep/jep.cpython-36m-darwin.so libjep.jnilib
(scikit-env-test) > [/usr/local/anaconda3/envs/scikit-env-test/lib]:$ otool -L libjep.jnilib
libjep.jnilib:
	@rpath/libpython3.6m.dylib (compatibility version 3.6.0, current version 3.6.0)
	/usr/lib/libSystem.B.dylib (compatibility version 1.0.0, current version 1252.50.4)
(scikit-env-test) > [/usr/local/anaconda3/envs/scikit-env-test/lib]:$ install_name_tool -change "@rpath/libpython3.6m.dylib" "/usr/local/anaconda3/envs/scikit-env-test/lib/libpython3.6m.dylib" libjep.jnilib
(scikit-env-test) > [/usr/local/anaconda3/envs/scikit-env-test/lib]:$ otool -L libjep.jnilib
libjep.jnilib:
	/usr/local/anaconda3/envs/scikit-env-test/lib/libpython3.6m.dylib (compatibility version 3.6.0, current version 3.6.0)
	/usr/lib/libSystem.B.dylib (compatibility version 1.0.0, current version 1252.50.4)
(scikit-env-test) > [/usr/local/anaconda3/envs/scikit-env-test/lib]:$
```

When running the java process that will use this provider, be sure to have configured the following environment variable
(example consistent with the example above):
```bash
export JAVA_LIBRARY_PATH=/usr/local/anaconda3/envs/scikit-env-test/lib
```
