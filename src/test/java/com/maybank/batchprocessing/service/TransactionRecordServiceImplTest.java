package com.maybank.batchprocessing.service;

import com.maybank.batchprocessing.exception.NotFoundException;
import com.maybank.batchprocessing.response.PagedResponse;
import com.maybank.batchprocessing.response.TransactionRecordResponse;
import com.maybank.batchprocessing.entity.TransactionRecord;
import com.maybank.batchprocessing.mapper.TransactionRecordMapper;
import com.maybank.batchprocessing.repository.TransactionRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TransactionRecordServiceImplTest {

    @Mock
    private TransactionRecordRepository repository;

    @Mock
    private TransactionRecordMapper mapper;

    @InjectMocks
    private TransactionRecordServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSearch_withFilters_shouldReturnPagedResponse() {
        TransactionRecord entity = TransactionRecord.builder()
                .id(1L)
                .accountNumber("ACC001")
                .trxAmount(BigDecimal.TEN)
                .description("Test record")
                .customerId("CUST001")
                .build();

        TransactionRecordResponse dto = TransactionRecordResponse.builder()
                .id(1L)
                .accountNumber("ACC001")
                .description("Test record")
                .build();

        Page<TransactionRecord> page = new PageImpl<>(List.of(entity), PageRequest.of(0, 10), 1);

        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);
        when(mapper.toDTO(entity)).thenReturn(dto);

        PagedResponse<TransactionRecordResponse> result = service.search("CUST001", "ACC001", "Test", PageRequest.of(0, 10));
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getAccountNumber()).isEqualTo("ACC001");
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.isFirst()).isTrue();

        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(mapper, times(1)).toDTO(entity);
    }

    @Test
    void testUpdateDescription_shouldUpdateSuccessfully() {
        TransactionRecord entity = TransactionRecord.builder()
                .id(1L)
                .description("Old description")
                .build();

        TransactionRecord updated = TransactionRecord.builder()
                .id(1L)
                .description("New description")
                .build();

        TransactionRecordResponse dto = TransactionRecordResponse.builder()
                .id(1L)
                .description("New description")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(updated);
        when(mapper.toDTO(updated)).thenReturn(dto);

        TransactionRecordResponse result = service.updateDescription(1L, "New description");

        assertThat(result.getDescription()).isEqualTo("New description");

        verify(repository).findById(1L);
        verify(repository).save(entity);
        verify(mapper).toDTO(updated);
    }

    @Test
    void testUpdateDescription_shouldThrowWhenRecordNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateDescription(1L, "desc"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Transaction record not found");

        verify(repository).findById(1L);
        verify(repository, never()).save(any());
    }

    @Test
    void testUpdateDescription_shouldHandleOptimisticLockingFailure() {
        TransactionRecord entity = TransactionRecord.builder()
                .id(1L)
                .description("desc")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(any())).thenThrow(new OptimisticLockingFailureException("lock error"));

        assertThatThrownBy(() -> service.updateDescription(1L, "new desc"))
                .isInstanceOf(OptimisticLockingFailureException.class)
                .hasMessageContaining("lock error");

        verify(repository).findById(1L);
        verify(repository).save(any());
    }
}
