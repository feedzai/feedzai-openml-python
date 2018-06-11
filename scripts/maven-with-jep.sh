#!/usr/bin/env bash

export LD_PRELOAD=${TO_PRELOAD?TO_PRELOAD variable undefined}

source activate ${CONDA_ENVIRONMENT?CONDA_ENVIRONMENT variable undefined}

mvn $@
