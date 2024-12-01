package com.cloudbees.trainapi.ticketing.web.controller;

import com.cloudbees.trainapi.ticketing.application.dto.input.ReceiptInputDTO;
import com.cloudbees.trainapi.ticketing.application.dto.input.SeatInputDTO;
import com.cloudbees.trainapi.ticketing.application.dto.output.ReceiptOutputDTO;
import com.cloudbees.trainapi.ticketing.application.dto.output.UserSeatOutputDTO;
import com.cloudbees.trainapi.ticketing.application.service.TicketingService;
import com.cloudbees.trainapi.ticketing.domain.model.Receipt;
import com.cloudbees.trainapi.ticketing.web.exception.NoAvailableSeatsException;
import com.cloudbees.trainapi.ticketing.web.exception.ReceiptNotFoundByIdException;
import com.cloudbees.trainapi.ticketing.web.exception.ReceiptNotFoundException;
import com.cloudbees.trainapi.ticketing.web.exception.ReceiptsNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
     * surname as "Doe", and email as "john.doe@example.com".
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
     * <p>
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
     * <p>
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
     * <p>
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


    /**
     * Tests the scenario where a valid request is made to delete a receipt,
     * ensuring that the server responds with an 'Ok' HTTP status.
     * <p>
     * This test sets up a mock receipt object with a predefined ID and uses
     * the ticketingService mock in order to simulate the process of deleting a receipt.
     * The request is then performed using the mockMvc, by sending a DELETE request to the specified API URL,
     * along with the id of the receipt that is to be deleted.
     * Finally, it verifies if the HTTP response status is '200 Ok', indicating
     * successful delete operation.
     *
     * @throws Exception if an error occurs during the request processing
     */
    @Test
    void givenValidRequest_whenDeleteReceipt_thenOkStatus() throws Exception {
        Long receiptId = 1L;
        doNothing().when(ticketingService).deleteReceipt(receiptId);

        mockMvc.perform(delete("/api/trains/delete/" + receiptId))
                .andExpect(status().isOk());
    }

    /**
     * Tests the scenario where a request is made to delete a receipt that does not exist,
     * validating that the system responds with a 'Not Found' HTTP status.
     * <p>
     * This test simulates a condition where the ticketing service can not find the receipt
     * that is requested to be deleted and throws a ReceiptNotFoundException.
     * The mockMvc instance is used to perform a DELETE request to the API with a non-existing receipt id.
     * Upon initiating the request, the test asserts that the HTTP response status is 404 Not Found, indicating
     * that the requested resource could not be found on the server.
     *
     * @throws Exception if an error occurs during the request processing
     */
    @Test
    void givenNonExistingReceipt_whenDeleteReceipt_thenNotFoundStatus() throws Exception {
        Long nonExistingReceiptId = 2L;
        doThrow(new ReceiptNotFoundException("Essay not found")).when(ticketingService).deleteReceipt(nonExistingReceiptId);

        mockMvc.perform(delete("/api/trains/delete/" + nonExistingReceiptId))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests the behavior of the system when a service exception is encountered during the receipt
     * deleting process. This test simulates an unexpected error scenario by configuring the
     * ticketing service to throw a RuntimeException when attempting to delete a receipt.
     * <p>
     * The test performs a DELETE request to the API endpoint using mockMvc with an receipt id.
     * The expected result is an HTTP response status of 500 Internal Server Error, indicating that
     * the server encountered an unexpected condition that prevented it from fulfilling the request.
     *
     * @throws Exception if an error occurs during the mock request processing
     */
    @Test
    void givenServiceException_whenDeleteReceipt_thenInternalServerErrorStatus() throws Exception {
        Long receiptId = 3L;
        doThrow(new RuntimeException("Unexpected error")).when(ticketingService).deleteReceipt(receiptId);

        mockMvc.perform(delete("/api/trains/delete/" + receiptId))
                .andExpect(status().isInternalServerError());
    }

    /**
     * Tests the scenario where a valid request is made to update a receipt,
     * ensuring that the server responds with an 'Ok' HTTP status.
     * <p>
     * This test sets up a mock receipt object with a predefined ID and SeatInputDTO with
     * values and uses the ticketingService mock in order to simulate the process
     * of updating a receipt. The request is then performed using the mockMvc,
     * by sending a PUT request to the specified API URL,
     * along with the data of the receipt that is to be updated.
     * Finally, it verifies if the HTTP response status is '200 Ok',
     * indicating successful update operation.
     *
     * @throws Exception if an error occurs during the request processing
     */
    @Test
    void givenValidRequest_whenUpdateReceipt_thenOkStatus() throws Exception {
        SeatInputDTO seatInputDTO = SeatInputDTO.builder()
                .id(1L)
                .section("A")
                .seatNumber(2)
                .build();

        doNothing().when(ticketingService).updateReceipt(seatInputDTO);

        mockMvc.perform(put("/api/trains/update-seat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(seatInputDTO)))
                .andExpect(status().isOk());
    }

    /**
     * Tests the scenario where a request is made to update a receipt that does not exist,
     * validating that the system responds with a 'Not Found' HTTP status.
     * <p>
     * This test simulates a condition where the ticketing service can not find the receipt
     * that is requested to be updated and throws a ReceiptNotFoundException.
     * The mockMvc instance is used to perform a PUT request to the API with a non-existing receipt id.
     * Upon initiating the request, the test asserts that the HTTP response status is 404 Not Found, indicating
     * that the requested resource could not be found on the server.
     *
     * @throws Exception if an error occurs during the request processing
     */
    @Test
    void givenNonExistingReceipt_whenUpdateReceipt_thenNotFoundStatus() throws Exception {
        SeatInputDTO nonExistingSeatInputDTO = SeatInputDTO.builder()
                .id(2L)
                .section("B")
                .seatNumber(3)
                .build();

        doThrow(new ReceiptNotFoundException("Receipt not found")).when(ticketingService).updateReceipt(nonExistingSeatInputDTO);

        mockMvc.perform(put("/api/trains/update-seat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(nonExistingSeatInputDTO)))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests the behavior of the system when a service exception is encountered during the receipt
     * updating process. This test simulates an unexpected error scenario by configuring the
     * ticketing service to throw a RuntimeException when attempting to update a receipt.
     * The test performs a PUT request to the API endpoint using mockMvc with an receipt id.
     * The expected result is an HTTP response status of 500 Internal Server Error, indicating that
     * the server encountered an unexpected condition that prevented it from fulfilling the request.
     *
     * @throws Exception if an error occurs during the mock request processing
     */
    @Test
    void givenServiceException_whenUpdateReceipt_thenInternalServerErrorStatus() throws Exception {
        SeatInputDTO seatInputDTO = SeatInputDTO.builder()
                .id(3L)
                .section("B")
                .seatNumber(4)
                .build();

        doThrow(new RuntimeException("Unexpected error")).when(ticketingService).updateReceipt(seatInputDTO);

        mockMvc.perform(put("/api/trains/update-seat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(seatInputDTO)))
                .andExpect(status().isInternalServerError());
    }

    /**
     * Tests the scenario where a valid request is made to fetch all receipts by section,
     * ensuring that the server responds with a 'OK' HTTP status.
     * <p>
     * This test sets up a stub for the ticketing service that will return a list of UserSeatOutputDTO
     * when it's getReceiptsBySection method is called in a specific section.
     * Then the mockMvc instance initiates a GET request to the specified API URL
     * and checks if the returned HTTP status is '200 OK' indicating a successful fetch operation.
     *
     * @throws Exception if an error occurs during the request processing
     */
    @Test
    void givenValidRequest_whenGetReceiptsBySection_thenOkStatus() throws Exception {
        String section = "A";
        List<UserSeatOutputDTO> userSeatOutputDTOList = new ArrayList<>();
        UserSeatOutputDTO userSeatOutputDTO = new UserSeatOutputDTO();
        userSeatOutputDTO.setName("John");
        userSeatOutputDTO.setSurname("Doe");
        userSeatOutputDTO.setSection("A");
        userSeatOutputDTO.setSeatNumber(1);
        userSeatOutputDTO.setEmail("john.doe@example.com");
        userSeatOutputDTOList.add(userSeatOutputDTO);
        when(ticketingService.getReceiptsBySection(section)).thenReturn(userSeatOutputDTOList);

        mockMvc.perform(get("/api/trains/receipts/section/" + section))
                .andExpect(status().isOk());
    }

    /**
     * Tests the scenario where a request is made to fetch receipts by a section that does not exist,
     * validating that the system responds with a 'Not Found' HTTP status.
     * <p>
     * This test sets up a stub for the ticketing service that will throw a ReceiptsNotFoundException
     * when it's getReceiptsBySection method is called in a non-existing section.
     * Then the mockMvc instance initiates a GET request to the specified API URL
     * and checks if the returned HTTP status is '404 NOT FOUND' indicating that there are
     * no receipts in the specified section.
     *
     * @throws Exception if an error occurs during the request processing
     */
    @Test
    void givenNonExistingSection_whenGetReceiptsBySection_thenNotFoundStatus() throws Exception {
        String nonExistingSection = "A";
        when(ticketingService.getReceiptsBySection(nonExistingSection)).thenThrow(new ReceiptsNotFoundException("No receipts found for section " + nonExistingSection));

        mockMvc.perform(get("/api/trains/receipts/section/" + nonExistingSection))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests the behavior of the system when a service exception is encountered during the receipt
     * fetch process. This test simulates an unexpected error scenario by configuring the
     * ticketing service to throw a RuntimeException when attempting to fetch receipts by section.
     * <p>
     * The test performs a GET request to the API endpoint using mockMvc with a section name.
     * The expected result is an HTTP response status of 500 Internal Server Error, indicating that
     * the server encountered an unexpected condition that prevented it from fulfilling the request.
     *
     * @throws Exception if an error occurs during the mock request processing
     */
    @Test
    void givenServiceException_whenGetReceiptsBySection_thenInternalServerErrorStatus() throws Exception {
        String section = "B";
        when(ticketingService.getReceiptsBySection(section)).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/trains/receipts/section/" + section))
                .andExpect(status().isInternalServerError());
    }

    /**
     * Tests the scenario where a valid request is made to fetch a receipt by id,
     * ensuring that the server responds with an 'OK' HTTP status.
     * <p>
     * This test sets up a stub for the ticketing service that will return a ReceiptOutputDTO
     * when it's getReceiptById method is called with a valid id.
     * Then the mockMvc instance initiates a GET request to the specified API URL
     * and checks if the returned HTTP status is '200 OK' indicating a successful fetch operation.
     *
     * @throws Exception if an error occurs during the request processing
     */
    @Test
    void givenValidRequest_whenGetReceiptById_thenOkStatus() throws Exception {
        Long receiptId = 1L;
        ReceiptOutputDTO receiptOutputDTO = new ReceiptOutputDTO();
        receiptOutputDTO.setName("John");
        receiptOutputDTO.setSurname("Doe");
        receiptOutputDTO.setEmail("john.doe@example.com");
        when(ticketingService.getReceiptById(receiptId)).thenReturn(receiptOutputDTO);

        mockMvc.perform(get("/api/trains/receipt/" + receiptId))
                .andExpect(status().isOk());
    }

    /**
     * Tests the scenario where a request is made to fetch receipt by an id that does not exist,
     * validating that the system responds with a 'Not Found' HTTP status.
     * <p>
     * This test sets up a stub for the ticketing service that will throw a ReceiptNotFoundByIdException
     * when it's getReceiptById method is called with a non-existing id.
     * Then the mockMvc instance initiates a GET request to the specified API URL
     * and checks if the returned HTTP status is '404 NOT FOUND' indicating that there is
     * no receipt with the specified id.
     *
     * @throws Exception if an error occurs during the request processing
     */
    @Test
    void givenNonExistingReceipt_whenGetReceiptById_thenNotFoundStatus() throws Exception {
        Long nonExistingReceiptId = 2L;
        when(ticketingService.getReceiptById(nonExistingReceiptId)).thenThrow(new ReceiptNotFoundByIdException("Receipt not found"));

        mockMvc.perform(get("/api/trains/receipt/" + nonExistingReceiptId))
                .andExpect(status().isNotFound());
    }
}