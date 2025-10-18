package com.maybank.batchprocessing.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TransactionRecordResponse {
    private Long id;
    private String accountNumber;
    private BigDecimal trxAmount;
    private String description;
    private LocalDate trxDate;
    private LocalTime trxTime;
    private String customerId;
}
