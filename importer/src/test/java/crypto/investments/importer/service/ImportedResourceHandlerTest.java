package crypto.investments.importer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.test.MetaDataInstanceFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ImportedResourceHandlerTest {

    private ImportedResourceHandler resourceHandler;

    @BeforeEach
    void setUp() {
        resourceHandler = spy(new NoopImportedResourceHandler());
    }

    @Test
    void testExecute() {
        // given
        String importedResourceUri = "file:/file.csv";

        StepExecution stepExecution = createStepExecution(importedResourceUri);
        StepContribution contribution = createStepContribution(stepExecution);
        ChunkContext context = createChunkContext(stepExecution);

        // when
        resourceHandler.execute(contribution, context);

        // then
        verify(resourceHandler, times(1)).handleResource(importedResourceUri);
        assertEquals(1, contribution.getWriteCount());
    }

    @Test
    void testExecute_whenResourceIsNotInJobParameter_expectSkip() {
        // given
        StepExecution stepExecution = createStepExecution();
        StepContribution contribution = createStepContribution(stepExecution);
        ChunkContext context = createChunkContext(stepExecution);

        // when
        resourceHandler.execute(contribution, context);

        // then
        verify(resourceHandler, never()).handleResource(anyString());
        assertEquals(1, contribution.getProcessSkipCount());
    }

    private StepExecution createStepExecution() {
        return MetaDataInstanceFactory.createStepExecution();
    }

    private StepExecution createStepExecution(String importResourceUri) {
        JobParameters jobParameters = new JobParametersBuilder()
            .addString("resource", importResourceUri)
            .toJobParameters();

        return MetaDataInstanceFactory.createStepExecution(jobParameters);
    }

    private ChunkContext createChunkContext(StepExecution execution) {
        return new ChunkContext(new StepContext(execution));
    }

    private StepContribution createStepContribution(StepExecution stepExecution) {
        return new StepContribution(stepExecution);
    }

    private static class NoopImportedResourceHandler extends ImportedResourceHandler {
        @Override
        void handleResource(String resourceUri) {
            // noop
        }
    }
}