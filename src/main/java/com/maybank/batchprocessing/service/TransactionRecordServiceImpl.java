package com.maybank.batchprocessing.service;

import com.maybank.batchprocessing.exception.NotFoundException;
import com.maybank.batchprocessing.response.TransactionRecordResponse;
import com.maybank.batchprocessing.entity.TransactionRecord;
import com.maybank.batchprocessing.mapper.TransactionRecordMapper;
import com.maybank.batchprocessing.repository.TransactionRecordRepository;
import com.maybank.batchprocessing.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;


@Service @RequiredArgsConstructor
public class TransactionRecordServiceImpl implements TransactionRecordService {

    private final TransactionRecordRepository repository;
    private final TransactionRecordMapper mapper;

    @Override
    public PagedResponse<TransactionRecordResponse> search(String customerId, String accountNumber, String description, Pageable pageable) {
        Specification<TransactionRecord> spec = Specification.unrestricted();

        if (customerId != null && !customerId.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(root.get("customerId"), "%" + customerId + "%"));
        }

        if (accountNumber != null && !accountNumber.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(root.get("accountNumber"), "%" + accountNumber + "%"));
        }

        if (description != null && !description.isBlank()) {
            String pattern = "%" + description.toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("description")), pattern));
        }
        Page<TransactionRecord> entities = repository.findAll(spec, pageable);
        List<TransactionRecordResponse> content = entities
                .getContent()
                .stream()
                .map(mapper::toDTO)
                .toList();

        return new PagedResponse<>(
                content,
                entities.getNumber(),
                entities.getSize(),
                entities.getTotalElements(),
                entities.getTotalPages(),
                entities.isFirst(),
                entities.isLast()
        );
    }

    @Override
    public TransactionRecordResponse updateDescription(Long id, String newDescription) {
        TransactionRecord record = repository.findById(id)
                .orElseThrow(() -> NotFoundException.forEntity("Transaction record", id));

        record.setDescription(newDescription);
        TransactionRecord updated = repository.save(record);
        return mapper.toDTO(updated);
    }

}
