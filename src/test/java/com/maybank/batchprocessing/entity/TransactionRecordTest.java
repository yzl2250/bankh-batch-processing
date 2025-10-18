package com.maybank.batchprocessing.entity;

import com.maybank.batchprocessing.repository.TransactionRecordRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TransactionRecordTest {

    @Autowired
    private TransactionRecordRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void testVersionFieldIncrementsOnUpdate() {
        TransactionRecord record = TransactionRecord.builder()
                .accountNumber("ACC999")
                .trxAmount(new BigDecimal("50.00"))
                .build();

        TransactionRecord saved = repository.save(record);
        entityManager.flush();
        Integer originalVersion = saved.getVersion();

        saved.setDescription("Updated description");
        TransactionRecord updated = repository.save(saved);
        entityManager.flush();

        assertThat(updated.getVersion()).isNotNull();
        assertThat(updated.getVersion()).isGreaterThan(originalVersion == null ? 0 : originalVersion);
    }
}
