package com.maybank.batchprocessing.batch;

import com.maybank.batchprocessing.repository.TransactionRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;

import static org.mockito.Mockito.*;

class StartupRunnerTest {

    @Mock
    private TransactionRecordRepository transactionRecordRepository;

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job importTransactionsJob;

    @Mock
    private JobExecution jobExecution;

    @InjectMocks
    private StartupRunner startupRunner;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        startupRunner = new StartupRunner(transactionRecordRepository, jobLauncher, importTransactionsJob);
    }

    @Test
    void whenDataAlreadyImported_shouldSkipJob() throws Exception {
        when(transactionRecordRepository.count()).thenReturn(5L);
        CommandLineRunner runner = startupRunner.runJobOnStartup();
        runner.run();

        verify(transactionRecordRepository, times(1)).count();
        verify(jobLauncher, never()).run(any(), any());
    }

    @Test
    void whenNoData_shouldRunJob() throws Exception {
        when(transactionRecordRepository.count()).thenReturn(0L);
        when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(jobExecution);
        when(jobExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);

        CommandLineRunner runner = startupRunner.runJobOnStartup();

        runner.run();

        verify(transactionRecordRepository, times(1)).count();
        verify(jobLauncher, times(1)).run(eq(importTransactionsJob), any(JobParameters.class));
        verify(jobExecution, times(1)).getStatus();
    }

    @Test
    void whenJobThrowsException_shouldCatchAndPrintStacktrace() throws Exception {
        when(transactionRecordRepository.count()).thenReturn(0L);
        when(jobLauncher.run(any(Job.class), any(JobParameters.class)))
                .thenThrow(new RuntimeException("Simulated failure"));

        CommandLineRunner runner = startupRunner.runJobOnStartup();
        runner.run();

        verify(jobLauncher, times(1)).run(eq(importTransactionsJob), any(JobParameters.class));
    }
}
