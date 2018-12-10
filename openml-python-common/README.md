Reusable Python Logic for OpenML
------------------------------------------------------------

This module contains reusable library code for Feedzai OpenML providers that use Python.

If you depend on this module to create your own provider, you probably want to follow the
subsequent installation guide.

This module uses Jep (https://github.com/ninia/jep) to load and use models defined in python.
It also depends on a couple of python libraries that should be on your Java library path.

This module compiles without any installation, as it depends on a default Jar file for JEP, installed by Maven.

To use and run tests from this module you should install and compile JEP for your Python environment. 

If you want to do some quick prototyping or test your python based provider, you may find useful to use [this docker image](https://github.com/feedzai/oracle-jep-miniconda) which already contains jep and other requirements installed.

Suggested installation steps:
1. Install anaconda (https://anaconda.org/)
2. Create a new environment:
    - `conda create --name myenv python=3`
3. Load/Activate your environment:
    - `source activate myenv`
4. Install pip and python dependencies with conda:
    - `conda install pip numpy`
5. Install Jep through pip (it is not available through conda):
    - `pip install jep`
    - some environments have shown problems installing jep through pip. Alternatively you can follow the install instructions at https://github.com/ninia/jep#installation
6. Add environment variables pointing to the environment's bin folder
    - Add these to your `~/.profile` and logout+login:
    
```
export PATH=</path/to/your/anaconda/instalation>/envs/myenv/bin:$PATH
export LD_LIBRARY_PATH=</path/to/your/anaconda/instalation>/envs/myenv/lib/<python-version-you-installed>/site-packages/jep:$LD_LIBRARY_PATH
export LD_PRELOAD=</path/to/your/anaconda/instalation>/envs/myenv/lib/libpython<your-python-version>m.so

Example in my case:
export ANACONDA_PATH=/home/luis.reis/anaconda3
export PATH=$ANACONDA_PATH/envs/myenv/bin:$PATH
export LD_LIBRARY_PATH=$ANACONDA_PATH/envs/myenv/lib/python3.6/site-packages/jep:$LD_LIBRARY_PATH
export LD_PRELOAD=$ANACONDA_PATH/envs/myenv/lib/libpython3.6m.so
```

7. If you need to share Python modules across sub-interpreters, you will need to create a "python-packages.xml" file where you define the modules to be shared. By default the provider is already sharing the "numpy" and "tensorflow" modules. This is a workaround for the issues with CPython extensions.
    - Remember that this file should be added to the classpath of your program.

```
<?xml version="1.0"?>
<python>
   <package>my_package_1</package>
   <package>my_package_2</package>
</python>

``` 
