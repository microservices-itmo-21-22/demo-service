package com.itmo.microservices.demo.payment.impl.utils;

import com.itmo.microservices.demo.payment.api.model.UserAccountFinancialLogRecordDto;
import com.itmo.microservices.demo.payment.impl.model.UserAccountFinancialLogRecord;
import org.modelmapper.ModelMapper;

public class UserAccountFinancialLogRecordUtils {

    private static final ModelMapper mapper = new ModelMapper();

    public static UserAccountFinancialLogRecordDto entityToDto(
            UserAccountFinancialLogRecord entity) {

        return mapper.map(entity, UserAccountFinancialLogRecordDto.class);
    }
}
