package com.cloudbees.trainapi.ticketing.web.controller;

import com.cloudbees.trainapi.ticketing.application.dto.input.ReceiptInputDTO;
import com.cloudbees.trainapi.ticketing.application.dto.input.SeatInputDTO;
import com.cloudbees.trainapi.ticketing.application.dto.output.ApiResponseDto;
import com.cloudbees.trainapi.ticketing.application.dto.output.ReceiptOutputDTO;
import com.cloudbees.trainapi.ticketing.application.dto.output.UserSeatOutputDTO;
import com.cloudbees.trainapi.ticketing.application.service.TicketingService;
import com.cloudbees.trainapi.ticketing.domain.model.Receipt;
import com.cloudbees.trainapi.ticketing.utils.TicketingServiceConstants;
import com.cloudbees.trainapi.ticketing.web.exception.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @Operation(summary = "Purchase a ticket", description = "Handles the purchase of a ticket and returns a receipt.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Receipt created successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "No available seats",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
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

    /**
     * Handles the deletion of a receipt identified by its ID.
     * This method is invoked when a DELETE request is made to the /delete/{id} endpoint.
     * It attempts to delete the receipt from the database and returns the result.
     *
     * @param receiptId The ID of the receipt to be deleted.
     * @return A ResponseEntity indicating the success or failure of the operation.
     *         If the deletion is successful, a HTTP status 200 (OK) is returned.
     *         If the receipt is not found, a HTTP status 404 (Not Found) is returned.
     *         For any other errors, an internal server error response with HTTP status 500 is returned.
     */
    @Operation(summary = "Delete a receipt", description = "Deletes a receipt by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receipt deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Receipt not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteReceipt(@PathVariable("id") Long receiptId) {
        try {
            log.info(TicketingServiceConstants.LOG_ATTEMPT_DELETE_RECEIPT, receiptId);

            ticketingService.deleteReceipt(receiptId);

            ApiResponseDto<Void> response = ApiResponseDto.<Void>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .statusCode(HttpStatus.OK.value())
                    .message(TicketingServiceConstants.RESPONSE_RECEIPT_DELETED)
                    .success(true)
                    .data(null)
                    .build();

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (ReceiptNotFoundException e) {
            log.error(TicketingServiceConstants.LOG_RECEIPT_NOT_FOUND, receiptId);

            ApiResponseDto<Void> errorResponse = ApiResponseDto.<Void>builder()
                    .status(HttpStatus.NOT_FOUND.getReasonPhrase())
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .message(TicketingServiceConstants.RESPONSE_RECEIPT_NOT_FOUND)
                    .success(false)
                    .data(null)
                    .build();

            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error(TicketingServiceConstants.LOG_UNEXPECTED_ERROR, e.getMessage());

            ApiResponseDto<Void> errorResponse = ApiResponseDto.<Void>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(TicketingServiceConstants.RESPONSE_UNEXPECTED_ERROR)
                    .success(false)
                    .data(null)
                    .build();

            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates the section and seat number of a receipt based on the provided SeatInputDTO.
     *
     * @param seatInputDTO the SeatInputDTO containing the new section and seat number.
     * @return ResponseEntity containing the result of the update operation.
     */
    @Operation(summary = "Update receipt seat information", description = "Updates the section and seat number of a receipt.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receipt updated successfully",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Receipt not found",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Seat already occupied",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @PutMapping("/update-seat")
    public ResponseEntity<ApiResponseDto<Void>> updateReceipt(@RequestBody @Validated SeatInputDTO seatInputDTO) {
        try {
            log.info(TicketingServiceConstants.LOG_ATTEMPT_UPDATE_RECEIPT, seatInputDTO);

            ticketingService.updateReceipt(seatInputDTO);

            ApiResponseDto<Void> response = ApiResponseDto.<Void>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .statusCode(HttpStatus.OK.value())
                    .message(TicketingServiceConstants.RESPONSE_RECEIPT_UPDATED)
                    .success(true)
                    .data(null)
                    .build();

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (ReceiptNotFoundException e) {
            log.error(TicketingServiceConstants.RESPONSE_RECEIPT_NOT_FOUND, seatInputDTO.getId());

            ApiResponseDto<Void> errorResponse = ApiResponseDto.<Void>builder()
                    .status(HttpStatus.NOT_FOUND.getReasonPhrase())
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .message(TicketingServiceConstants.RECEIPT_NOT_FOUND_ERROR_MESSAGE)
                    .success(false)
                    .data(null)
                    .build();

            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);

        } catch (SeatAlreadyOccupiedException e) {
            log.error(TicketingServiceConstants.LOG_SEAT_ALREADY_OCCUPIED, seatInputDTO.getSection(), seatInputDTO.getSeatNumber());

            ApiResponseDto<Void> errorResponse = ApiResponseDto.<Void>builder()
                    .status(HttpStatus.CONFLICT.getReasonPhrase())
                    .statusCode(HttpStatus.CONFLICT.value())
                    .message(TicketingServiceConstants.SEAT_OCCUPIED_ERROR_MESSAGE)
                    .success(false)
                    .data(null)
                    .build();

            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);

        } catch (Exception e) {
            log.error(TicketingServiceConstants.LOG_UNEXPECTED_ERROR, e.getMessage());

            ApiResponseDto<Void> errorResponse = ApiResponseDto.<Void>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(TicketingServiceConstants.RESPONSE_UNEXPECTED_ERROR)
                    .success(false)
                    .data(null)
                    .build();

            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get receipts by section", description = "Fetches all receipts for a particular section.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receipts fetched successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "No receipts found for section",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @GetMapping("/receipts/section/{section}")
    public ResponseEntity<ApiResponseDto<List<UserSeatOutputDTO>>> getReceiptsBySection(
            @PathVariable("section") @Pattern(regexp = TicketingServiceConstants.SECTION_VALIDATION_REGEX,
                    message = TicketingServiceConstants.SECTION_ERROR_MESSAGE) String section) {
        try {
            List<UserSeatOutputDTO> receipts = ticketingService.getReceiptsBySection(section);

            ApiResponseDto<List<UserSeatOutputDTO>> response = ApiResponseDto.<List<UserSeatOutputDTO>>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .statusCode(HttpStatus.OK.value())
                    .message(TicketingServiceConstants.RESPONSE_RECEIPTS_FETCHED_SUCCESS)
                    .success(true)
                    .data(receipts)
                    .build();

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (ReceiptsNotFoundException e) {
            log.error(TicketingServiceConstants.LOG_RECEIPT_NOT_FOUND_BY_SECTION, section);

            ApiResponseDto<List<UserSeatOutputDTO>> errorResponse = ApiResponseDto.<List<UserSeatOutputDTO>>builder()
                    .status(HttpStatus.NOT_FOUND.getReasonPhrase())
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .message(TicketingServiceConstants.NO_RECEIPTS_FOUND_ERROR_MESSAGE)
                    .success(false)
                    .data(null)
                    .build();

            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            log.error(TicketingServiceConstants.LOG_UNEXPECTED_ERROR, e.getMessage());

            ApiResponseDto<List<UserSeatOutputDTO>> errorResponse = ApiResponseDto.<List<UserSeatOutputDTO>>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(TicketingServiceConstants.RESPONSE_UNEXPECTED_ERROR)
                    .success(false)
                    .data(null)
                    .build();

            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of receipts based on the provided receipt ID.
     *
     * @param id the unique identifier of the receipt to be retrieved.
     * @return a ResponseEntity containing an ApiResponseDto with the list of ReceiptOutputDTOs
     *         if the receipt is found. The response includes HTTP status and additional
     *         details about the success or failure of the operation.
     */
    @Operation(summary = "Get receipt by ID", description = "Retrieves a receipt using its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receipt fetched successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Receipt not found by ID",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @GetMapping("/receipt/{id}")
    public ResponseEntity<ApiResponseDto<ReceiptOutputDTO>> getReceiptById(
            @PathVariable("id") Long id) {
        try {
            ReceiptOutputDTO receipts = ticketingService.getReceiptById(id);

            ApiResponseDto<ReceiptOutputDTO> response = ApiResponseDto.<ReceiptOutputDTO>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .statusCode(HttpStatus.OK.value())
                    .message(TicketingServiceConstants.RESPONSE_RECEIPT_FETCHED_BY_ID_SUCCESS)
                    .success(true)
                    .data(receipts)
                    .build();

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (ReceiptNotFoundByIdException e) {
            log.error(TicketingServiceConstants.LOG_RECEIPT_NOT_FOUND_BY_ID, id);

            ApiResponseDto<ReceiptOutputDTO> errorResponse = ApiResponseDto.<ReceiptOutputDTO>builder()
                    .status(HttpStatus.NOT_FOUND.getReasonPhrase())
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .message(e.getMessage())
                    .success(false)
                    .data(null)
                    .build();

            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            log.error(TicketingServiceConstants.LOG_UNEXPECTED_ERROR, e.getMessage());

            ApiResponseDto<ReceiptOutputDTO> errorResponse = ApiResponseDto.<ReceiptOutputDTO>builder()
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