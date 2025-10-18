package com.maybank.batchprocessing.batch;

import com.maybank.batchprocessing.repository.TransactionRecordRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StartupRunner {

    private final TransactionRecordRepository transactionRecordRepository;
    private final JobLauncher jobLauncher;
    private final Job importTransactionsJob;

    public StartupRunner(TransactionRecordRepository transactionRecordRepository,
                         JobLauncher jobLauncher,
                         Job importTransactionsJob) {
        this.transactionRecordRepository = transactionRecordRepository;
        this.jobLauncher = jobLauncher;
        this.importTransactionsJob = importTransactionsJob;
    }

    @Bean
    public CommandLineRunner runJobOnStartup() {
        return args -> {
            if (transactionRecordRepository.count() > 0) {
                System.out.println("Data already imported, skipping batch job.");
                return;
            }

            JobParameters params = new JobParametersBuilder()
                    .addLong("run.id", System.currentTimeMillis())
                    .toJobParameters();

            try {
                JobExecution execution = jobLauncher.run(importTransactionsJob, params);
                System.out.println("Batch job completed with status: " + execution.getStatus());
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}

