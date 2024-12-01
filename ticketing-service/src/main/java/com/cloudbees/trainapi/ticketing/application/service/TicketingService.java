package com.cloudbees.trainapi.ticketing.application.service;

import com.cloudbees.trainapi.ticketing.application.dto.input.ReceiptInputDTO;
import com.cloudbees.trainapi.ticketing.application.mapper.ReceiptMapper;
import com.cloudbees.trainapi.ticketing.domain.model.Receipt;
import com.cloudbees.trainapi.ticketing.domain.repository.ReceiptRepository;
import com.cloudbees.trainapi.ticketing.utils.TicketingServiceConstants;
import com.cloudbees.trainapi.ticketing.web.exception.NoAvailableSeatsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.OptionalInt;

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
        log.info(TicketingServiceConstants.LOG_FIND_SECTION);

        String section = receiptRepository.findSectionWithFewestTickets();
        log.info(TicketingServiceConstants.LOG_SECTION_SELECTED, section);

        OptionalInt seatNumber = receiptRepository.findFirstFreeSeatNumber(section);

        if (seatNumber.isEmpty()) {
            log.warn(TicketingServiceConstants.LOG_NO_SEATS_LEFT, section);
            throw new NoAvailableSeatsException(TicketingServiceConstants.OVER_BOOKING_ERROR_MESSAGE);
        }

        Receipt receipt = receiptMapper.mapToEntity(dto);
        log.info(TicketingServiceConstants.LOG_MAPPING_SUCCESS, receipt.toString());

        receipt.setSection(section);
        receipt.setSeatNumber(seatNumber.getAsInt());

        Receipt savedReceipt = receiptRepository.save(receipt);
        log.info(TicketingServiceConstants.LOG_RECEIPT_SAVED, savedReceipt.getId());

        return savedReceipt;
    }
}
