package com.maybank.batchprocessing.batch;

import com.maybank.batchprocessing.MaybankBatchProcessingApplication;
import com.maybank.batchprocessing.entity.TransactionRecord;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.TestPropertySource;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(
        classes = MaybankBatchProcessingApplication.class,
        properties = "input.file=src/test/resources/test-input.txt"
)
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:testdb")
class BatchConfigTest {

    @Autowired
    private FlatFileItemReader<TransactionRecord> reader;

    @Test
    void contextLoads() {
        assertThat(reader).isNotNull();
    }

    @Test
    void reader_shouldParseLineCorrectly() throws Exception {
        Path tempFile = Path.of("src/test/resources/test-input.txt");
        Files.createDirectories(tempFile.getParent());

        try (FileWriter writer = new FileWriter(tempFile.toFile())) {
            writer.write("accountNumber|trxAmount|description|trxDate|trxTime|customerId\n");
            writer.write("12345|100.50|Test transaction|2025-10-15|12:34:56|C001\n");
        }

        reader.setResource(new FileSystemResource(tempFile.toFile()));
        reader.open(new org.springframework.batch.item.ExecutionContext());
        TransactionRecord record = reader.read();

        assertThat(record).isNotNull();
        assertThat(record.getAccountNumber()).isEqualTo("12345");
        assertThat(record.getTrxAmount()).isEqualByComparingTo("100.50");
        assertThat(record.getDescription()).isEqualTo("Test transaction");
        assertThat(record.getTrxDate()).isEqualTo(LocalDate.of(2025, 10, 15));
        assertThat(record.getTrxTime()).isEqualTo(LocalTime.of(12, 34, 56));
        assertThat(record.getCustomerId()).isEqualTo("C001");

        reader.close();
    }
}