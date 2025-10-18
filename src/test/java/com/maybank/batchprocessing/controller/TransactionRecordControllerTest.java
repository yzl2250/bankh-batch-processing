package com.maybank.batchprocessing.controller;

import com.maybank.batchprocessing.response.PagedResponse;
import com.maybank.batchprocessing.response.TransactionRecordResponse;
import com.maybank.batchprocessing.service.TransactionRecordService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionRecordController.class)
class TransactionRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionRecordService transactionRecordService;

    @Test
    @WithMockUser(username = "testuser")
    void testSearchTransactions() throws Exception {
        TransactionRecordResponse dto = new TransactionRecordResponse();
        dto.setId(1L);
        dto.setDescription("Test Transaction");

        PagedResponse<TransactionRecordResponse> response = new PagedResponse<>();
        response.setContent(List.of(dto));
        response.setPage(0);
        response.setSize(10);
        response.setTotalElements(1);
        response.setTotalPages(1);
        response.setFirst(true);
        response.setLast(false);

        Mockito.when(transactionRecordService.search(any(), any(), any(), any(PageRequest.class)))
                .thenReturn(response);

        mockMvc.perform(get("/api/transactions")
                        .param("customerId", "123")
                        .param("accountNumber", "ABC123")
                        .param("description", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].description").value("Test Transaction"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testUpdateDescription() throws Exception {
        // Arrange
        TransactionRecordResponse updatedDto = new TransactionRecordResponse();
        updatedDto.setId(1L);
        updatedDto.setDescription("Updated Description");

        Mockito.when(transactionRecordService.updateDescription(eq(1L), eq("Updated Description")))
                .thenReturn(updatedDto);

        // Act & Assert
        mockMvc.perform(patch("/api/transactions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\": \"Updated Description\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }
}
