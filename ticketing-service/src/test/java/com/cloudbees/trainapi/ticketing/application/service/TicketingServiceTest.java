package com.cloudbees.trainapi.ticketing.application.service;

import com.cloudbees.trainapi.ticketing.application.dto.input.ReceiptInputDTO;
import com.cloudbees.trainapi.ticketing.application.dto.input.SeatInputDTO;
import com.cloudbees.trainapi.ticketing.application.dto.output.ReceiptOutputDTO;
import com.cloudbees.trainapi.ticketing.application.dto.output.UserSeatOutputDTO;
import com.cloudbees.trainapi.ticketing.application.mapper.ReceiptMapper;
import com.cloudbees.trainapi.ticketing.domain.model.Receipt;
import com.cloudbees.trainapi.ticketing.domain.repository.ReceiptRepository;
import com.cloudbees.trainapi.ticketing.web.exception.NoAvailableSeatsException;
import com.cloudbees.trainapi.ticketing.web.exception.ReceiptNotFoundByIdException;
import com.cloudbees.trainapi.ticketing.web.exception.ReceiptNotFoundException;
import com.cloudbees.trainapi.ticketing.web.exception.SeatAlreadyOccupiedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
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
     * <p>
     * This test case verifies the process of purchasing a ticket using a
     * ReceiptInputDTO object. It sets up mock interactions with the receiptRepository
     * and receiptMapper objects to ensure that the method under test behaves
     * correctly and interacts with its dependencies as expected.
     * <p>
     * The method performs the following validations:
     * <p>
     * - Ensures that the receipt is correctly created with the expected name and surname.
     * - Checks that the section with the fewest tickets and the first available seat
     * number are correctly identified and used during the purchase process.
     * - Verifies that the mapper correctly converts the input DTO to a Receipt entity.
     * - Confirms that the receipt is saved to the repository.
     * <p>
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
     * <p>
     * This test case simulates a situation in which all seats are occupied in the
     * section with the fewest tickets, resulting in no available seats to purchase.
     * It verifies that the appropriate exception is thrown and that none of the
     * receipt mapping or saving operations are performed.
     * <p>
     * The test performs the following steps:
     * <p>
     * - Creates a ReceiptInputDTO object with dummy user data.
     * - Mocks the receiptRepository to return a section with no available seats.
     * - Asserts that a NoAvailableSeatsException is thrown when attempting to
     * purchase a ticket.
     * - Verifies that the repository methods for finding the section and seat were
     * called exactly once, and that the mapper and save methods were not called.
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
     * <p>
     * This unit test verifies that when a ticket is purchased with available seating, the method
     * correctly returns a Receipt object with expected details. The method sets up a ReceiptInputDTO
     * object and utilizes mocking to simulate interactions with external dependencies such as the
     * receiptRepository and receiptMapper.
     * <p>
     * The test performs the following verifications:
     * <p>
     * - Ensures that the receipt is correctly mapped from input data by validating the output matches
     * the mock receipt.
     * - Confirms that the section with the fewest tickets and the first available seat number are
     * appropriately determined and assigned.
     * - Checks the mapper functionality by verifying that it converts the input DTO into the expected
     * Receipt entity.
     * - Validates that the receipt is properly saved into the repository.
     * <p>
     * Assertions are used to confirm that the resulting Receipt object is not null and equals the
     * expected mock receipt. Mockito's verify method is used to ensure that the save operation on the
     * repository is invoked exactly once.
     */
    @Test
    void purchaseTicket_ShouldReturnReceipt_WhenSeatIsAvailable() {

        ReceiptInputDTO dto = new ReceiptInputDTO();
        dto.setName("John");
        dto.setSurname("Doe");
        dto.setEmail("john.doe@example.com");

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


        when(receiptRepository.findSectionWithFewestTickets()).thenReturn("A");
        when(receiptRepository.findFirstFreeSeatNumber("A")).thenReturn(OptionalInt.of(1));
        when(receiptMapper.mapToEntity(dto)).thenReturn(mockReceipt);
        when(receiptRepository.save(mockReceipt)).thenReturn(mockReceipt);

        Receipt result = ticketingService.purchaseTicket(dto);

        assertNotNull(result);
        assertEquals(mockReceipt, result);
        verify(receiptRepository, times(1)).save(mockReceipt);
    }

    /**
     * Tests the purchaseTicket method of the TicketingService when no seats are available.
     * <p>
     * This unit test verifies that the purchaseTicket method throws a
     * NoAvailableSeatsException when attempting to purchase a ticket
     * in a situation where no seats are available in the identified section.
     * <p>
     * Test procedure:
     * - Initializes a ReceiptInputDTO with sample data.
     * - Mocks the receiptRepository behavior to simulate all seats being occupied.
     * - Asserts that the purchaseTicket method throws a NoAvailableSeatsException.
     * <p>
     * Expected outcome:
     * - The purchaseTicket method should throw a NoAvailableSeatsException, indicating
     * that there are no free seats available for purchase.
     */
    @Test
    void purchaseTicket_ShouldThrowNoAvailableSeatsException_WhenNoSeatsAreAvailable() {
        ReceiptInputDTO dto = new ReceiptInputDTO();
        when(receiptRepository.findSectionWithFewestTickets()).thenReturn("A");
        when(receiptRepository.findFirstFreeSeatNumber("A")).thenReturn(OptionalInt.empty());

        assertThrows(NoAvailableSeatsException.class, () -> ticketingService.purchaseTicket(dto));
    }

    /**
     * Tests the deleteReceipt method of the TicketingService when the receipt to be deleted exists.
     * <p>
     * The unit test verifies that if the receipt exists, then a record sees it deleted from the repository.
     * <p>
     * Test procedure:
     * - Mocks the receiptRepository behavior to simulate receipt existence.
     * - Calls the deleteReceipt method.
     * - Asserts that the deleteById method on the repository is called exactly once.
     */
    @Test
    void deleteReceipt_shouldRemoveReceipt_whenExists() {
        Long receiptId = 1L;
        when(receiptRepository.existsById(receiptId)).thenReturn(true);
        ticketingService.deleteReceipt(receiptId);
        verify(receiptRepository, times(1)).deleteById(receiptId);
    }

    /**
     * Tests the deleteReceipt method of the TicketingService when the receipt to be deleted does not exist.
     * <p>
     * The unit test verifies that if the receipt does not exist, then a ReceiptNotFoundException is thrown.
     * <p>
     * Test procedure:
     * - Mocks the receiptRepository behavior to simulate receipt non-existence.
     * - Calls the deleteReceipt method.
     * - Asserts that a ReceiptNotFoundException is thrown.
     */
    @Test
    void deleteReceipt_shouldThrowReceiptNotFoundException_whenDoesNotExist() {
        Long receiptId = 1L;
        when(receiptRepository.existsById(receiptId)).thenReturn(false);
        assertThrows(ReceiptNotFoundException.class, () -> ticketingService.deleteReceipt(receiptId));
    }

    /**
     * Tests the updateReceipt method in the TicketingService class when the receipt exists and seat is available.
     * <p>
     * This unit test verifies the scenario where a seat reassignment for a receipt is requested.
     * It mocks the testing environment to simulate seat availability and an existing receipt.
     * Under these conditions, it checks whether the seat reassignment is successful and persists changes.
     */
    @Test
    void updateReceipt_ShouldUpdateReceipt_WhenReceiptExistsAndSeatAvailable() {
        SeatInputDTO inputDTO = new SeatInputDTO();
        inputDTO.setId(1L);
        inputDTO.setSection("B");
        inputDTO.setSeatNumber(1);

        Receipt receipt = new Receipt();
        receipt.setId(1L);
        receipt.setSection("A");
        receipt.setSeatNumber(2);

        when(receiptRepository.existsById(inputDTO.getId())).thenReturn(true);
        when(receiptRepository.existsBySectionAndSeatNumber(inputDTO.getSection(), inputDTO.getSeatNumber())).thenReturn(false);
        when(receiptRepository.findById(inputDTO.getId())).thenReturn(Optional.of(receipt));
        when(receiptRepository.save(any())).thenReturn(receipt);

        ticketingService.updateReceipt(inputDTO);

        assertEquals("B", receipt.getSection());
        assertEquals(1, receipt.getSeatNumber());
        verify(receiptRepository, times(1)).save(any());
    }

    /**
     * Tests the updateReceipt method in the TicketingService class when the receipt does not exist.
     * <p>
     * This test case simulates the scenario where there is an attempt to update a receipt that does not exist.
     * It verifies the appropriate exception is thrown in such cases.
     */
    @Test
    void updateReceipt_ShouldThrowReceiptNotFoundException_WhenReceiptDoesNotExist() {
        SeatInputDTO inputDTO = new SeatInputDTO();
        inputDTO.setId(1L);
        inputDTO.setSection("B");
        inputDTO.setSeatNumber(1);

        when(receiptRepository.existsById(inputDTO.getId())).thenReturn(false);

        assertThrows(ReceiptNotFoundException.class, () -> ticketingService.updateReceipt(inputDTO));
    }

    /**
     * Tests the updateReceipt method in the TicketingService class when the seat is already occupied.
     * <p>
     * This test case simulates the scenario where there is an attempt to update a receipt with a section
     * and seat number which is already occupied. It verifies that the appropriate exception is thrown.
     */
    @Test
    void updateReceipt_ShouldThrowSeatAlreadyOccupiedException_WhenSeatIsOccupied() {
        SeatInputDTO inputDTO = new SeatInputDTO();
        inputDTO.setId(1L);
        inputDTO.setSection("B");
        inputDTO.setSeatNumber(1);

        when(receiptRepository.existsById(inputDTO.getId())).thenReturn(true);
        when(receiptRepository.existsBySectionAndSeatNumber(inputDTO.getSection(), inputDTO.getSeatNumber())).thenReturn(true);

        assertThrows(SeatAlreadyOccupiedException.class, () -> ticketingService.updateReceipt(inputDTO));
    }

    /**
     * Tests the getReceiptsBySection method of the TicketingService when receipts with the given section exist.
     * <p>
     * The unit test simulates a scenario where the receipts with the given section exist in the repository.
     * It mocks the repository to return a list of UserSeatOutputDTO and verifies if the returned list is as expected.
     */
    @Test
    void getReceiptsBySection_ShouldReturnReceipts_WhenReceiptsExistsWithGivenSection() {
        String section = "A";
        List<UserSeatOutputDTO> mockReceipts = List.of(
                new UserSeatOutputDTO("John", "Doe", "john.doe@example.com", "A", 1),
                new UserSeatOutputDTO("Jane", "Doe", "jane.doe@example.com", "A", 2)
        );

        when(receiptRepository.findAllBySection(section)).thenReturn(mockReceipts);

        List<UserSeatOutputDTO> result = ticketingService.getReceiptsBySection(section);

        assertNotNull(result);
        assertEquals(mockReceipts, result);
    }


    /**
     * Tests the getReceiptById method of the TicketingService class when a receipt with the specified ID exists.
     *
     * This unit test verifies that the getReceiptById method correctly retrieves a receipt when it exists in the
     * repository. It uses mock interactions with the receiptRepository to simulate this scenario.
     *
     * Test procedure:
     * - Sets up a mock ReceiptOutputDTO object to simulate an existing receipt.
     * - Configures the receiptRepository mock to return the mock receipt when queried with the specified receipt ID.
     * - Invokes the getReceiptById method of the TicketingService with the given receipt ID.
     * - Asserts that the result is not null and the receipt retrieved matches the expected mock receipt.
     */
    @Test
    void getReceiptById_ShouldReturnReceipt_WhenReceiptExistsWithGivenId() {
        Long receiptId = 1L;
        ReceiptOutputDTO mockReceipt = new ReceiptOutputDTO();
        when(receiptRepository.findReceiptOutputById(receiptId)).thenReturn(Optional.of(mockReceipt));

        ReceiptOutputDTO result = ticketingService.getReceiptById(receiptId);

        assertNotNull(result);
        assertEquals(mockReceipt, result);
    }

    /**
     * Test the getReceiptById method of the TicketingService when the receipt with the given id does not exist.
     * <p>
     * The unit test verifies a scenario where no receipt with the given id exists in the repository.
     * It mocks the repository data set and verifies the appropriate exception is thrown.
     */
    @Test
    void getReceiptById_ShouldThrowReceiptNotFoundByIdException_WhenNoReceiptExistsWithGivenId() {
        Long receiptId = 1L;
        when(receiptRepository.findReceiptOutputById(receiptId)).thenReturn(Optional.empty());

        assertThrows(ReceiptNotFoundByIdException.class, () -> ticketingService.getReceiptById(receiptId));
    }
}
