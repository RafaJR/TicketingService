package com.cloudbees.trainapi.ticketing.application.service;

import com.cloudbees.trainapi.ticketing.application.dto.input.ReceiptInputDTO;
import com.cloudbees.trainapi.ticketing.application.mapper.ReceiptMapper;
import com.cloudbees.trainapi.ticketing.domain.model.Receipt;
import com.cloudbees.trainapi.ticketing.domain.repository.ReceiptRepository;
import com.cloudbees.trainapi.ticketing.web.exception.NoAvailableSeatsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for the TicketingService, responsible for unit testing the ticket purchase functionality.
 * Utilizes Mockito to mock dependencies and simulate the behavior of the under-test class.
 */
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class TicketingServiceTest {

    @Mock
    private ReceiptMapper receiptMapper;

    @Mock
    private ReceiptRepository receiptRepository;

    @InjectMocks
    private TicketingService ticketingService;

    /**
     * Tests the purchaseTicket method of the TicketingService class.
     *
     * This test case verifies the process of purchasing a ticket using a
     * ReceiptInputDTO object. It sets up mock interactions with the receiptRepository
     * and receiptMapper objects to ensure that the method under test behaves
     * correctly and interacts with its dependencies as expected.
     *
     * The method performs the following validations:
     *
     * - Ensures that the receipt is correctly created with the expected name and surname.
     * - Checks that the section with the fewest tickets and the first available seat
     *   number are correctly identified and used during the purchase process.
     * - Verifies that the mapper correctly converts the input DTO to a Receipt entity.
     * - Confirms that the receipt is saved to the repository.
     *
     * Uses assertions to confirm that the resulting Receipt has the correct name and surname,
     * and uses Mockito to verify interactions with mocked dependencies.
     */
    @Test
    void testPurchaseTicket() {
        ReceiptInputDTO inputDTO = new ReceiptInputDTO();
        inputDTO.setName("John");
        inputDTO.setSurname("Doe");
        inputDTO.setEmail("john.doe@example.com");

        Receipt receipt = new Receipt();
        receipt.setName("John");
        receipt.setSurname("Doe");

        when(receiptRepository.findSectionWithFewestTickets()).thenReturn("A");
        when(receiptRepository.findFirstFreeSeatNumber("A")).thenReturn(OptionalInt.of(1));
        when(receiptMapper.mapToEntity(any())).thenReturn(receipt);
        when(receiptRepository.save(any())).thenReturn(receipt);

        Receipt result = ticketingService.purchaseTicket(inputDTO);

        assertEquals("John", result.getName());
        assertEquals("Doe", result.getSurname());
        verify(receiptRepository, times(1)).findSectionWithFewestTickets();
        verify(receiptRepository, times(1)).findFirstFreeSeatNumber("A");
        verify(receiptMapper, times(1)).mapToEntity(any());
        verify(receiptRepository, times(1)).save(any(Receipt.class));
    }

    /**
     * Tests the scenario where a ticket purchase is attempted but no seats are left.
     *
     * This test case simulates a situation in which all seats are occupied in the
     * section with the fewest tickets, resulting in no available seats to purchase.
     * It verifies that the appropriate exception is thrown and that none of the
     * receipt mapping or saving operations are performed.
     *
     * The test performs the following steps:
     *
     * - Creates a ReceiptInputDTO object with dummy user data.
     * - Mocks the receiptRepository to return a section with no available seats.
     * - Asserts that a NoAvailableSeatsException is thrown when attempting to
     *   purchase a ticket.
     * - Verifies that the repository methods for finding the section and seat were
     *   called exactly once, and that the mapper and save methods were not called.
     */
    @Test
    void testPurchaseTicketNoSeatsLeft() {
        ReceiptInputDTO inputDTO = new ReceiptInputDTO();
        inputDTO.setName("John");
        inputDTO.setSurname("Doe");
        inputDTO.setEmail("john.doe@example.com");

        when(receiptRepository.findSectionWithFewestTickets()).thenReturn("A");
        when(receiptRepository.findFirstFreeSeatNumber("A")).thenReturn(OptionalInt.empty());

        assertThrows(NoAvailableSeatsException.class, () -> ticketingService.purchaseTicket(inputDTO));

        verify(receiptRepository, times(1)).findSectionWithFewestTickets();
        verify(receiptRepository, times(1)).findFirstFreeSeatNumber("A");
        verify(receiptMapper, times(0)).mapToEntity(any());
        verify(receiptRepository, times(0)).save(any());
    }

    /**
     * Tests the purchaseTicket method in the TicketingService class with an available seat scenario.
     *
     * This unit test verifies that when a ticket is purchased with available seating, the method
     * correctly returns a Receipt object with expected details. The method sets up a ReceiptInputDTO
     * object and utilizes mocking to simulate interactions with external dependencies such as the
     * receiptRepository and receiptMapper.
     *
     * The test performs the following verifications:
     *
     * - Ensures that the receipt is correctly mapped from input data by validating the output matches
     *   the mock receipt.
     * - Confirms that the section with the fewest tickets and the first available seat number are
     *   appropriately determined and assigned.
     * - Checks the mapper functionality by verifying that it converts the input DTO into the expected
     *   Receipt entity.
     * - Validates that the receipt is properly saved into the repository.
     *
     * Assertions are used to confirm that the resulting Receipt object is not null and equals the
     * expected mock receipt. Mockito's verify method is used to ensure that the save operation on the
     * repository is invoked exactly once.
     */
    @Test
    void purchaseTicket_ShouldReturnReceipt_WhenSeatIsAvailable() {
        // Crear un DTO de entrada de prueba
        ReceiptInputDTO dto = new ReceiptInputDTO();
        dto.setName("John");
        dto.setSurname("Doe");
        dto.setEmail("john.doe@example.com");

        // Definir el objeto mockReceipt que será esperado como resultado
        Receipt mockReceipt = Receipt.builder()
                .origin("London")
                .destination("France")
                .price(new BigDecimal("20.00"))
                .name("John")
                .surname("Doe")
                .email("john.doe@example.com")
                .section("A")
                .seatNumber(1)
                .build();

        // Configurar el mock del repositorio y mapper para devolver valores específicos
        when(receiptRepository.findSectionWithFewestTickets()).thenReturn("A");
        when(receiptRepository.findFirstFreeSeatNumber("A")).thenReturn(OptionalInt.of(1));
        when(receiptMapper.mapToEntity(dto)).thenReturn(mockReceipt);
        when(receiptRepository.save(mockReceipt)).thenReturn(mockReceipt);

        // Ejecutar el método bajo prueba
        Receipt result = ticketingService.purchaseTicket(dto);

        // Verificar el resultado y las interacciones
        assertNotNull(result);
        assertEquals(mockReceipt, result);
        verify(receiptRepository, times(1)).save(mockReceipt);
    }

    /**
     * Tests the purchaseTicket method of the TicketingService when no seats are available.
     *
     * This unit test verifies that the purchaseTicket method throws a
     * NoAvailableSeatsException when attempting to purchase a ticket
     * in a situation where no seats are available in the identified section.
     *
     * Test procedure:
     * - Initializes a ReceiptInputDTO with sample data.
     * - Mocks the receiptRepository behavior to simulate all seats being occupied.
     * - Asserts that the purchaseTicket method throws a NoAvailableSeatsException.
     *
     * Expected outcome:
     * - The purchaseTicket method should throw a NoAvailableSeatsException, indicating
     *   that there are no free seats available for purchase.
     */
    @Test
    void purchaseTicket_ShouldThrowNoAvailableSeatsException_WhenNoSeatsAreAvailable() {
        ReceiptInputDTO dto = new ReceiptInputDTO();
        when(receiptRepository.findSectionWithFewestTickets()).thenReturn("A");
        when(receiptRepository.findFirstFreeSeatNumber("A")).thenReturn(OptionalInt.empty());

        assertThrows(NoAvailableSeatsException.class, () -> ticketingService.purchaseTicket(dto));
    }
}