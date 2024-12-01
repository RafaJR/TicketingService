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

    /**
     * Purchases a ticket for a passenger based on the provided input data. The method finds the
     * section with the fewest tickets available and attempts to allocate the first free seat
     * within that section. If a seat is available, the details are recorded on the receipt
     * and saved, effectively completing the ticket purchase process.
     *
     * @param dto a {@code ReceiptInputDTO} object containing the passenger's personal information
     *            such as name, surname, and email.
     * @return a {@code Receipt} object representing the successfully purchased ticket, including
     *         details such as section and seat number.
     * @throws NoAvailableSeatsException if there are no available seats in the selected section.
     */
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
