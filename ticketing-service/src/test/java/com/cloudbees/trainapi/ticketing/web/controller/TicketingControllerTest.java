package com.cloudbees.trainapi.ticketing.web.controller;

import com.cloudbees.trainapi.ticketing.application.dto.input.ReceiptInputDTO;
import com.cloudbees.trainapi.ticketing.application.service.TicketingService;
import com.cloudbees.trainapi.ticketing.domain.model.Receipt;
import com.cloudbees.trainapi.ticketing.web.exception.NoAvailableSeatsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit test class for the TicketingController, focusing on the endpoint responsible for
 * ticket purchase operations. This class uses the Spring MockMvc framework to mimic HTTP
 * requests and verify the behavior of the TicketingController's ticket purchasing endpoint.
 * It includes tests for different scenarios such as successful ticket purchase, no available
 * seats, and internal server error conditions.
 */
@WebMvcTest(TicketingController.class)
@ExtendWith(MockitoExtension.class)
class TicketingControllerTest {

    private static final String API_PURCHASE_URL = "/api/trains/purchase";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketingService ticketingService;

    /**
     * Converts the given object into its JSON representation as a String.
     *
     * @param obj the object to be converted into a JSON string
     * @return a JSON string representation of the provided object
     * @throws RuntimeException if the object cannot be converted to JSON
     */
    private static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a default instance of {@link ReceiptInputDTO} with predefined values
     * for name, surname, and email. This method is typically used in test scenarios
     * to provide a standard input for receipt-based operations.
     *
     * @return a {@link ReceiptInputDTO} instance with default values set for name as "John",
     *         surname as "Doe", and email as "john.doe@example.com".
     */
    private ReceiptInputDTO createDefaultReceiptInputDTO() {
        return ReceiptInputDTO.builder()
                .name("John")
                .surname("Doe")
                .email("john.doe@example.com")
                .build();
    }

    /**
     * Tests the scenario where a valid request is made to create a receipt,
     * ensuring that the server responds with a 'Created' HTTP status.
     *
     * This test sets up a mock receipt object with a predefined ID and uses
     * the ticketingService mock to simulate the process of purchasing a ticket.
     * The request is then performed using the mockMvc, sending a JSON content
     * representing the receipt input DTO to the specified purchase API URL.
     * Finally, it verifies if the HTTP response status is '201 Created', indicating
     * successful receipt creation.
     *
     * @throws Exception if an error occurs during the request processing
     */
    @Test
    void givenValidRequest_whenCreateReceipt_thenCreatedStatus() throws Exception {
        Receipt receipt = new Receipt();
        receipt.setId(1L);
        when(ticketingService.purchaseTicket(any())).thenReturn(receipt);

        mockMvc.perform(post(API_PURCHASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(createDefaultReceiptInputDTO())))
                .andExpect(status().isCreated());
    }

    /**
     * Tests the scenario where an attempt is made to create a receipt when no seats are available,
     * validating that the system responds correctly with a 'Conflict' HTTP status.
     *
     * This test simulates a condition where the ticketing service is unable to find any available
     * seats, raising a NoAvailableSeatsException. The mockMvc instance is used to perform a POST
     * request to the purchase API with a predefined JSON content. Upon initiating the request,
     * the test asserts that the HTTP response status is 409 Conflict, indicating that the operation
     * could not be completed due to a lack of available resources.
     *
     * @throws Exception if an error occurs during the request processing
     */
    @Test
    void givenNoAvailableSeats_whenCreateReceipt_thenConflictStatus() throws Exception {
        when(ticketingService.purchaseTicket(any())).thenThrow(new NoAvailableSeatsException("No seats available."));

        mockMvc.perform(post(API_PURCHASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(createDefaultReceiptInputDTO())))
                .andExpect(status().isConflict());
    }

    /**
     * Tests the behavior of the system when a service exception is encountered during the receipt
     * creation process. This test simulates an unexpected error scenario by configuring the
     * ticketing service to throw a RuntimeException when attempting to purchase a ticket.
     *
     * The test performs a POST request to the purchase API endpoint using mockMvc, with a JSON
     * content representing a default receipt input DTO. The expected result is an HTTP response
     * status of 500 Internal Server Error, indicating that the server encountered an unexpected
     * condition that prevented it from fulfilling the request.
     *
     * @throws Exception if an error occurs during the mock request processing
     */
    @Test
    void givenServiceException_whenCreateReceipt_thenInternalServerErrorStatus() throws Exception {
        when(ticketingService.purchaseTicket(any())).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post(API_PURCHASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(createDefaultReceiptInputDTO())))
                .andExpect(status().isInternalServerError());
    }
}