package com.cloudbees.trainapi.ticketing.application.service;

import com.cloudbees.trainapi.ticketing.application.dto.input.ReceiptInputDTO;
import com.cloudbees.trainapi.ticketing.application.mapper.ReceiptMapper;
import com.cloudbees.trainapi.ticketing.domain.model.Receipt;
import com.cloudbees.trainapi.ticketing.domain.repository.ReceiptRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class TicketingService {

    private ReceiptMapper receiptMapper;
    private ReceiptRepository receiptRepository;

    public TicketingService(ReceiptMapper receiptMapper, ReceiptRepository receiptRepository) {
        this.receiptMapper = receiptMapper;
        this.receiptRepository = receiptRepository;
    }

    public Receipt purchaseTicket(ReceiptInputDTO dto) {

        String section = receiptRepository.findSectionWithFewestTickets();
        Optional<Integer> seatNumber = receiptRepository.findFirstFreeSeatNumber(section);

        if (seatNumber.isEmpty()) {
            throw new IllegalStateException("No available seats in section " + section);
        }

        Receipt receipt = receiptMapper.mapToEntity(dto);
        receipt.setSection(section);
        receipt.setSeatNumber(seatNumber.get());

        log.info("Receipt created: {}", receipt.toString());

        return receiptRepository.save(receipt);
    }
}
