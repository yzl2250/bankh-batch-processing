package com.maybank.batchprocessing.controller;

import com.maybank.batchprocessing.request.TransactionRecordRequest;
import com.maybank.batchprocessing.response.TransactionRecordResponse;
import com.maybank.batchprocessing.response.PagedResponse;
import com.maybank.batchprocessing.service.TransactionRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transactions", description = "Manage transaction records")
public class TransactionRecordController {

    private final TransactionRecordService service;

    @GetMapping
    @Operation(
            summary = "Get paged transactions",
            description = "Returns a paged list of transaction records. You can filter by customerId, accountNumber, or description."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Paged list retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PagedResponse.class, example = """
                                        {
                                          "content": [
                                            {
                                              "id": 21,
                                              "accountNumber": "8872838299",
                                              "trxAmount": 12345.00,
                                              "description": "ATM WITHDRAWAL",
                                              "trxDate": "2019-09-11",
                                              "trxTime": "11:11:11",
                                              "customerId": "123"
                                            }
                                          ],
                                          "page": 0,
                                          "size": 10,
                                          "totalElements": 1,
                                          "totalPages": 1,
                                          "first": true,
                                          "last": true
                                        }
                                        """)
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Database connection failed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                      "timestamp": "2025-10-18 22:03:01:122",
                                      "status": 503,
                                      "error": "Service Unavailable",
                                      "message": "Database connection failed. Please try again later."
                                    }
                                    """)
                    )
            )
    })
    public PagedResponse<TransactionRecordResponse> search(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String accountNumber,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.search(customerId, accountNumber, description, PageRequest.of(page, size));
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Update transaction description",
            description = "Updates the description of a transaction record identified by its ID. Accepts a JSON body with the new description."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Transaction updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionRecordResponse.class,
                                    example = """
                                        {
                                          "content": [
                                            {
                                              "id": 21,
                                              "accountNumber": "8872838299",
                                              "trxAmount": 12345.00,
                                              "description": "ATM WITHDRAWAL",
                                              "trxDate": "2019-09-11",
                                              "trxTime": "11:11:11",
                                              "customerId": "123"
                                            }
                                          ],
                                          "page": 0,
                                          "size": 10,
                                          "totalElements": 1,
                                          "totalPages": 1,
                                          "first": true,
                                          "last": true
                                        }
                                        """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transaction not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                      "timestamp": "2025-10-17 23:59:59:123",
                                      "status": 404,
                                      "error": "Not Found",
                                      "message": "Transaction record not found for id 123"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Concurrent update detected",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                      "timestamp": "2025-10-17 23:59:59:123",
                                      "status": 409,
                                      "error": "Conflict",
                                      "message": "Concurrent update detected, please retry"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Database connection failed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                      "timestamp": "2025-10-18 22:03:01:122",
                                      "status": 503,
                                      "error": "Service Unavailable",
                                      "message": "Database connection failed. Please try again later."
                                    }
                                    """)
                    )
            )
    })
    public TransactionRecordResponse updateDescription(@PathVariable Long id, @RequestBody TransactionRecordRequest dto) {
        return service.updateDescription(id, dto.getDescription());
    }
}
