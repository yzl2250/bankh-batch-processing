package com.maybank.batchprocessing.batch;

import com.maybank.batchprocessing.entity.TransactionRecord;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@Configuration
public class BatchConfig {

    @Bean
    public FlatFileItemReader<TransactionRecord> reader(@Value("${input.file}") String inputFile) {
        FlatFileItemReader<TransactionRecord> reader = new FlatFileItemReader<>();
        Resource original = new FileSystemResource(inputFile);
        reader.setResource(new FilteredResource(original));  // Wrap the resource so blank lines are removed on-the-fly if there is any
        reader.setLinesToSkip(1);

        DefaultLineMapper<TransactionRecord> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer("|");
        tokenizer.setNames("accountNumber", "trxAmount", "description", "trxDate", "trxTime", "customerId");
        lineMapper.setLineTokenizer(tokenizer);

        BeanWrapperFieldSetMapper<TransactionRecord> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(TransactionRecord.class);

        fieldSetMapper.setCustomEditors(Map.of(
                LocalDate.class, new PropertyEditorSupport() {
                    @Override
                    public void setAsText(String text) {
                        setValue(LocalDate.parse(text));
                    }
                },
                LocalTime.class, new PropertyEditorSupport() {
                    @Override
                    public void setAsText(String text) {
                        setValue(LocalTime.parse(text));
                    }
                }
        ));

        lineMapper.setFieldSetMapper(fieldSetMapper);

        reader.setLineMapper(lineMapper);
        return reader;
    }

    @Bean
    public Job importJob(JobRepository jobRepository, Step step1) {
        return new JobBuilder("importTransactionsJob", jobRepository)
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository,
                      PlatformTransactionManager transactionManager,
                      FlatFileItemReader<TransactionRecord> reader,
                      JpaItemWriter<TransactionRecord> writer) {
        return new StepBuilder("step1", jobRepository)
                .<TransactionRecord, TransactionRecord>chunk(10, transactionManager)
                .reader(reader)
                .writer(writer)
                .build();
    }

    @Bean
    public JpaItemWriter<TransactionRecord> writer(EntityManagerFactory emf) {
        JpaItemWriter<TransactionRecord> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(emf);
        return writer;
    }
}
