package com.cloudbees.trainapi.ticketing.application.service;

import com.cloudbees.trainapi.ticketing.application.dto.input.ReceiptInputDTO;
import com.cloudbees.trainapi.ticketing.application.dto.input.SeatInputDTO;
import com.cloudbees.trainapi.ticketing.application.dto.output.ReceiptOutputDTO;
import com.cloudbees.trainapi.ticketing.application.dto.output.UserSeatOutputDTO;
import com.cloudbees.trainapi.ticketing.application.mapper.ReceiptMapper;
import com.cloudbees.trainapi.ticketing.domain.model.Receipt;
import com.cloudbees.trainapi.ticketing.domain.repository.ReceiptRepository;
import com.cloudbees.trainapi.ticketing.utils.TicketingServiceConstants;
import com.cloudbees.trainapi.ticketing.web.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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

    /**
     * Deletes a receipt from the database based on the provided ID.
     *
     * @param receiptId the ID of the receipt to be deleted.
     * @throws ReceiptNotFoundException if the receipt with the specified ID does not exist.
     */
    public void deleteReceipt(Long receiptId) {
        log.info(TicketingServiceConstants.LOG_ATTEMPT_DELETE_RECEIPT, receiptId);

        if (!receiptRepository.existsById(receiptId)) {
            log.warn(TicketingServiceConstants.LOG_RECEIPT_NOT_FOUND, receiptId);
            throw new ReceiptNotFoundException(TicketingServiceConstants.RECEIPT_NOT_FOUND_ERROR_MESSAGE);
        }

        receiptRepository.deleteById(receiptId);
        log.info(TicketingServiceConstants.LOG_RECEIPT_DELETED, receiptId);
    }

    /**
     * Updates a receipt's section and seat number.
     *
     * @param seatInputDTO the DTO containing the updated section and seat number.
     * @throws ReceiptNotFoundException if the receipt with the given ID does not exist.
     * @throws SeatAlreadyOccupiedException if the seat in the section is already occupied.
     */
    @Transactional
    public void updateReceipt(SeatInputDTO seatInputDTO) {
        log.info(TicketingServiceConstants.LOG_ATTEMPT_UPDATE_RECEIPT, seatInputDTO.getId());

        if (!receiptRepository.existsById(seatInputDTO.getId())) {
            log.warn(TicketingServiceConstants.LOG_RECEIPT_NOT_FOUND, seatInputDTO.getId());
            throw new ReceiptNotFoundException(TicketingServiceConstants.RECEIPT_NOT_FOUND_ERROR_MESSAGE);
        }

        if (receiptRepository.existsBySectionAndSeatNumber(seatInputDTO.getSection(), seatInputDTO.getSeatNumber())) {
            log.warn(TicketingServiceConstants.LOG_SEAT_ALREADY_OCCUPIED, seatInputDTO.getSection(), seatInputDTO.getSeatNumber());
            throw new SeatAlreadyOccupiedException(TicketingServiceConstants.SEAT_OCCUPIED_ERROR_MESSAGE);
        }

        Receipt receipt = receiptRepository.findById(seatInputDTO.getId()).get();
        receipt.setSection(seatInputDTO.getSection());
        receipt.setSeatNumber(seatInputDTO.getSeatNumber());
        receiptRepository.save(receipt);

        log.info(TicketingServiceConstants.LOG_RECEIPT_UPDATED, seatInputDTO.getId());
    }

    /**
     * Retrieves all receipts for a specified section.
     *
     * @param section The section to filter receipts by.
     * @return A list of UserSeatOutputDTO objects for the specified section.
     * @throws ReceiptsNotFoundException if no receipts are found for the given section.
     */
    public List<UserSeatOutputDTO> getReceiptsBySection(String section) {
        log.info(TicketingServiceConstants.LOG_ATTEMPT_FETCH_RECEIPTS_BY_SECTION, section);

        List<UserSeatOutputDTO> receipts = receiptRepository.findAllBySection(section);

        if (receipts.isEmpty()) {
            log.warn(TicketingServiceConstants.LOG_RECEIPT_NOT_FOUND_BY_SECTION, section);
            throw new ReceiptsNotFoundException(TicketingServiceConstants.NO_RECEIPTS_FOUND_ERROR_MESSAGE);
        }

        log.info(TicketingServiceConstants.LOG_RECEIPTS_FETCHED_SUCCESS, receipts.size());
        return receipts;
    }


    /**
     * Retrieves a receipt by its unique identifier.
     *
     * @param id the unique identifier of the receipt to be retrieved
     * @return the receipt matching the provided identifier
     * @throws ReceiptNotFoundByIdException if no receipt is found for the specified identifier
     */
    public ReceiptOutputDTO getReceiptById(Long id) {
        log.info(TicketingServiceConstants.LOG_ATTEMPT_FETCH_RECEIPT_BY_ID, id);

        Optional<ReceiptOutputDTO> receipts = receiptRepository.findReceiptOutputById(id);

        if (receipts.isEmpty()) {
            log.warn(TicketingServiceConstants.LOG_RECEIPT_NOT_FOUND_BY_ID, id);
            throw new ReceiptNotFoundByIdException(TicketingServiceConstants.RECEIPT_NOT_FOUND_BY_ID_MESSAGE);
        }

        log.info(TicketingServiceConstants.LOG_RECEIPT_FETCHED_BY_ID_SUCCESS, id);
        return receipts.get();
    }

}
