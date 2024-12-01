package com.cloudbees.trainapi.ticketing.web.controller;

import com.cloudbees.trainapi.ticketing.application.dto.input.ReceiptInputDTO;
import com.cloudbees.trainapi.ticketing.application.dto.output.ApiResponseDto;
import com.cloudbees.trainapi.ticketing.application.service.TicketingService;
import com.cloudbees.trainapi.ticketing.domain.model.Receipt;
import com.cloudbees.trainapi.ticketing.utils.TicketingServiceConstants;
import com.cloudbees.trainapi.ticketing.web.exception.NoAvailableSeatsException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trains")
@Validated
@Slf4j
public class TicketingController {

    private final TicketingService ticketingService;

    /**
     * Constructs a new instance of TicketingController with the specified TicketingService.
     *
     * @param ticketingService the service responsible for handling ticketing operations
     */
    public TicketingController(TicketingService ticketingService) {
        this.ticketingService = ticketingService;
    }

    /**
     * Handles the purchase of a ticket and creates a receipt for the transaction.
     * This method is invoked when a POST request is made to the /purchase endpoint.
     * It validates the receipt input data and attempts to purchase a ticket. Upon
     * successful purchase, a receipt object is created and returned in the response.
     *
     * @param receiptInputDTO The data transfer object containing receipt input information
     *                        such as name, surname, and email of the passenger.
     * @return A ResponseEntity containing an ApiResponseDto with the Receipt data.
     *         If the ticket is successfully purchased, the response contains the receipt
     *         data with HTTP status 201 (Created). If there are no available seats, a
     *         conflict response with HTTP status 409 is returned. For any other errors,
     *         an internal server error response with HTTP status 500 is returned.
     */
    @PostMapping("/purchase")
    public ResponseEntity<ApiResponseDto<Receipt>> createReceipt(@Valid @RequestBody ReceiptInputDTO receiptInputDTO) {
        try {
            log.info(TicketingServiceConstants.LOG_ATTEMPT_PURCHASE, receiptInputDTO.toString());

            Receipt receipt = ticketingService.purchaseTicket(receiptInputDTO);

            ApiResponseDto<Receipt> response = ApiResponseDto.<Receipt>builder()
                    .status(HttpStatus.CREATED.getReasonPhrase())
                    .statusCode(HttpStatus.CREATED.value())
                    .message(TicketingServiceConstants.RESPONSE_RECEIPT_CREATED)
                    .success(true)
                    .data(receipt)
                    .build();

            log.info(TicketingServiceConstants.LOG_RECEIPT_CREATED, receipt.getId());

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (NoAvailableSeatsException e) {
            log.error(TicketingServiceConstants.LOG_OVERBOOKING_ISSUE, e.getMessage());

            ApiResponseDto<Receipt> errorResponse = ApiResponseDto.<Receipt>builder()
                    .status(HttpStatus.CONFLICT.getReasonPhrase())
                    .statusCode(HttpStatus.CONFLICT.value())
                    .message(TicketingServiceConstants.RESPONSE_OVERBOOKING_ERROR)
                    .success(false)
                    .data(null)
                    .build();

            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error(TicketingServiceConstants.LOG_UNEXPECTED_ERROR, e.getMessage());

            ApiResponseDto<Receipt> errorResponse = ApiResponseDto.<Receipt>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(TicketingServiceConstants.RESPONSE_UNEXPECTED_ERROR)
                    .success(false)
                    .data(null)
                    .build();

            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}