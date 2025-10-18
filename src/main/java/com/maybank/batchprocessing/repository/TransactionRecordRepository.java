package com.maybank.batchprocessing.repository;

import com.maybank.batchprocessing.entity.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransactionRecordRepository
        extends JpaRepository<TransactionRecord, Long>, JpaSpecificationExecutor<TransactionRecord> {
}

