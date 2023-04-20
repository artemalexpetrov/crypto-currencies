package crypto.investments.importer.launcher;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportJobLauncherImplTest {

    public static final String RESOURCE_JOB_PARAMETER = "resource";

    @Mock
    private Job jobMock;

    @Mock
    private JobLauncher jobLauncherMock;

    @InjectMocks
    private ImportJobLauncherImpl importJobLauncher;

    @Captor
    private ArgumentCaptor<JobParameters> jobParameterCaptor;

    @Test
    @SneakyThrows
    void testLaunch() {
        // given
        Resource resource = new FileSystemResource("/path/to/file.csv");
        String resourceUri = resource.getURI().toString();

        // when
        importJobLauncher.launch(resource);

        // then
        verify(jobLauncherMock, times(1)).run(eq(jobMock), jobParameterCaptor.capture());
        verifyNoMoreInteractions(jobLauncherMock);

        JobParameters jobParameters = jobParameterCaptor.getValue();
        String resourceParameter = jobParameters.getString(RESOURCE_JOB_PARAMETER);
        assertEquals(resourceUri, resourceParameter);
    }

    @Test
    @SneakyThrows
    void testLaunch_whenInvalidResourceProvided_expectException() {
        // given
        Resource resource = mock(Resource.class);
        when(resource.getURI()).thenThrow(IOException.class);

        // when / then
        assertThrows(JobExecutionException.class, () -> importJobLauncher.launch(resource));
    }

    @Test
    @SneakyThrows
    void testLaunch_whenJobAlreadyLaunched_expectNoException() {
        // given
        Resource resource = new FileSystemResource("/path/to/file.csv");
        when(jobLauncherMock.run(any(), any())).thenThrow(JobExecutionAlreadyRunningException.class);

        // when/then
        assertDoesNotThrow(() -> importJobLauncher.launch(resource));
    }

    @Test
    @SneakyThrows
    void testLaunch_whenJobAlreadyComplete_expectNoException() {
        // given
        Resource resource = new FileSystemResource("/path/to/file.csv");
        when(jobLauncherMock.run(any(), any())).thenThrow(JobInstanceAlreadyCompleteException.class);

        // when/then
        assertDoesNotThrow(() -> importJobLauncher.launch(resource));
    }

    @Test
    @SneakyThrows
    void testLaunch_whenJobParameterIsInvalid_expectExceptionPropagated() {
        // given
        Resource resource = new FileSystemResource("/path/to/file.csv");
        when(jobLauncherMock.run(any(), any())).thenThrow(JobParametersInvalidException.class);

        // when/then
        assertThrows(JobExecutionException.class, () -> importJobLauncher.launch(resource));
    }

    @Test
    @SneakyThrows
    void testLaunch_whenJobRestartHappens_expectExceptionPropagated() {
        // given
        Resource resource = new FileSystemResource("/path/to/file.csv");
        when(jobLauncherMock.run(any(), any())).thenThrow(JobRestartException.class);

        // when/then
        assertThrows(JobExecutionException.class, () -> importJobLauncher.launch(resource));
    }
}