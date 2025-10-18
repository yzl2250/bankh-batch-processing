package com.maybank.batchprocessing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "transaction_record")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Integer version;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private BigDecimal trxAmount;

    private String description;
    private LocalDate trxDate;
    private LocalTime trxTime;
    private String customerId;
}
