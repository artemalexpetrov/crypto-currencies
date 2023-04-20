package crypto.investments.importer.launcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImportJobLauncherImpl implements ImportJobLauncher {

    private static final String RESOURCE_PARAMETER_NAME = "resource";

    private final JobLauncher launcher;
    private final Job importJob;

    @Override
    public void launch(Resource resource) throws JobExecutionException {
        log.info("Staring import job for {}", resource.getDescription());
        JobParameters parameters = getJobParameters(resource);
        doLaunch(parameters);
    }

    private void doLaunch(JobParameters parameters) throws JobExecutionException {
        try {
            launcher.run(importJob, parameters);
        } catch (JobExecutionAlreadyRunningException e) {
            log.warn("Import is already in progress for {}", parameters.getString(RESOURCE_PARAMETER_NAME));
        } catch (JobInstanceAlreadyCompleteException e) {
            log.warn("Import has been completed for {}", parameters.getString(RESOURCE_PARAMETER_NAME));
        }
    }

    private JobParameters getJobParameters(Resource resource) throws JobExecutionException {
        // TODO: add file's hash as identifying parameter to prevent execution for the same file
        try {
            String resourceUri = resource.getURI().toString();
            return new JobParametersBuilder()
                .addString(RESOURCE_PARAMETER_NAME, resourceUri)
                .toJobParameters();
        } catch (IOException e) {
            throw new JobExecutionException("Unable to get URI of the resource", e);
        }
    }
}
