package com.feedzai.openml.python;

import com.feedzai.openml.data.schema.DatasetSchema;
import com.feedzai.openml.mocks.MockInstance;
import com.feedzai.openml.provider.exception.ModelLoadingException;
import com.feedzai.openml.util.algorithm.GenericAlgorithm;
import com.feedzai.openml.util.load.LoadSchemaUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;

public class TensorflowReloadModelTest {

    /**
     * Minimal working example that shows that reloading a tensor flow model in the same JVM fails.
     */
    @Test
    public void testReloadTensorflowModel() throws IOException, ModelLoadingException {
        final ClassificationPythonModelLoader modelLoader = new PythonModelProvider()
                .getModelCreator(GenericAlgorithm.GENERIC_CLASSIFICATION.getAlgorithmDescriptor().getAlgorithmName())
                .get();

        final Path path = Paths.get(this.getClass().getResource("/tensorflow_valid").getPath());
        final DatasetSchema datasetSchema = LoadSchemaUtils.datasetSchemaFromJson(path);

        // 1st load passes
        modelLoader.loadModel(path, datasetSchema).classify(new MockInstance(datasetSchema, ThreadLocalRandom.current()));

        // 2nd load fails
        modelLoader.loadModel(path, datasetSchema).classify(new MockInstance(datasetSchema, ThreadLocalRandom.current()));
    }

}
