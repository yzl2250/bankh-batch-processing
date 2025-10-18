package com.maybank.batchprocessing.integration;

import com.maybank.batchprocessing.entity.TransactionRecord;
import com.maybank.batchprocessing.repository.TransactionRecordRepository;
import jakarta.persistence.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TransactionRecordRepositoryIT {

    @Autowired
    private TransactionRecordRepository repository;

    @Autowired
    private EntityManagerFactory emf;

    @Test
    void testOptimisticLocking_shouldThrowExceptionOnConcurrentUpdate() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        TransactionRecord record = new TransactionRecord();
        record.setAccountNumber("ACC123");
        record.setDescription("initial");
        record.setTrxAmount(BigDecimal.TEN);
        em.persist(record);
        em.getTransaction().commit();
        em.close();

        EntityManager em1 = emf.createEntityManager();
        em1.getTransaction().begin();
        TransactionRecord r1 = em1.find(TransactionRecord.class, record.getId());

        EntityManager em2 = emf.createEntityManager();
        em2.getTransaction().begin();
        TransactionRecord r2 = em2.find(TransactionRecord.class, record.getId());

        r1.setDescription("update 1");
        em1.getTransaction().commit();
        em1.close();

        r2.setDescription("update 2");
        assertThatThrownBy(() -> {
            em2.getTransaction().commit();
        }).isInstanceOf(RollbackException.class)
                .hasCauseInstanceOf(OptimisticLockException.class);

        em2.close();
    }

}
