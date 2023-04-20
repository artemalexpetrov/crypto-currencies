package crypto.investments.importer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * A base {@see Tasklet} for all handlers for imported files.
 * Extracts `resource` parameter from a step context, validates it,
 * and passes into {@code handleResource} method which must be implemented
 * in successors.
 *
 * The idea is that potentially the app could import data from different resources,
 * e.g. filesystem (file:/...), S3 Bucket (s3:/...), etc. and to prevent repeated
 * attempt to import the same resource many times we could "mark" that resource as processed.
 *
 * Currently, the app supports only import from filesystem. Once other sources added it's better
 * to consider strategy pattern here.
 */
@Slf4j
public abstract class ImportedResourceHandler implements Tasklet {

    abstract void handleResource(String resourceUri);

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext context) {
        String importedResourceUri = getResourceUriFromContext(context);
        if (importedResourceUri != null) {
            handleResource(importedResourceUri);
            contribution.incrementWriteCount(1);
        } else {
            handleUnknownResourceParameter(contribution, context);
        }

        return RepeatStatus.FINISHED;
    }

    private String getResourceUriFromContext(ChunkContext context) {
        if (context == null) {
            return null;
        }

        JobParameters jobParameters = getJobParametersFromContext(context);
        return jobParameters.getString("resource");
    }

    private JobParameters getJobParametersFromContext(ChunkContext context) {
        return context.getStepContext()
            .getStepExecution()
            .getJobParameters();
    }

    private void handleUnknownResourceParameter(StepContribution contribution, ChunkContext context) {
        Long jobId = context.getStepContext().getJobInstanceId();
        log.error("Job {} doesn't contain 'resource' parameter", jobId);
        contribution.incrementProcessSkipCount();
    }
}
