package com.maybank.batchprocessing.service;

import com.maybank.batchprocessing.response.TransactionRecordResponse;
import com.maybank.batchprocessing.response.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface TransactionRecordService {
    PagedResponse<TransactionRecordResponse> search(String customerId, String accountNumber, String description, Pageable pageable);
    TransactionRecordResponse updateDescription(Long id, String newDescription);
}