package com.cloudbees.trainapi.ticketing.application.service;

import com.cloudbees.trainapi.ticketing.application.dto.input.ReceiptInputDTO;
import com.cloudbees.trainapi.ticketing.application.mapper.ReceiptMapper;
import com.cloudbees.trainapi.ticketing.domain.model.Receipt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TicketingService {

    private ReceiptMapper receiptMapper;

    public TicketingService(ReceiptMapper receiptMapper) {
        this.receiptMapper = receiptMapper;
    }

    public Receipt purchaseTicket(ReceiptInputDTO dto) {

        Receipt receipt = receiptMapper.mapToEntity(dto);

        log.info("Receipt created: {}", receipt.toString());

        return receipt;
    }
}
