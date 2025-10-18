package com.maybank.batchprocessing.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRecordRequest {
    @Schema(description = "New description for the transaction", example = "Payment for invoice #123", required = true)
    private String description;
}
