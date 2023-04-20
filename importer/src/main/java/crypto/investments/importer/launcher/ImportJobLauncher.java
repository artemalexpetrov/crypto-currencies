package crypto.investments.importer.launcher;

import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.core.io.Resource;

public interface ImportJobLauncher {

    /**
     * Prepares job parameters and launches the import job for the specified resource
     *
     * @param resource a resource to launch import job for
     * @throws JobExecutionException see {@link JobLauncher#run} for more details
     */
    void launch(Resource resource) throws JobExecutionException;
}
