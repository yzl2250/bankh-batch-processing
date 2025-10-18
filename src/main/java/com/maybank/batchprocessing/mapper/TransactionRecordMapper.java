package com.maybank.batchprocessing.mapper;

import com.maybank.batchprocessing.response.TransactionRecordResponse;
import com.maybank.batchprocessing.entity.TransactionRecord;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionRecordMapper {
    TransactionRecordResponse toDTO(TransactionRecord entity);
    TransactionRecord toEntity(TransactionRecordResponse dto);
}
