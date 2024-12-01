package com.cloudbees.trainapi.ticketing.application.mapper;

import com.cloudbees.trainapi.ticketing.application.dto.input.ReceiptInputDTO;
import com.cloudbees.trainapi.ticketing.domain.model.Receipt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * Unit test class for the ReceiptMapper, which is responsible for mapping data
 * from a ReceiptInputDTO to a Receipt entity. This test suite ensures that the mapping
 * occurs correctly, with the appropriate values being assigned to the Receipt properties.
 *
 * The ReceiptMapperTest verifies if the default values for origin, destination,
 * and price are correctly applied, and if the personal information such as name,
 * surname, and email from ReceiptInputDTO is accurately transferred to the
 * Receipt entity.
 *
 * An instance of ReceiptMapper is injected for testing using the @InjectMocks annotation.
 *
 * The setup method initializes a ReceiptInputDTO with sample data for use in the tests.
 *
 * Test Method:
 * - whenMapToEntityCalled_thenReceiptShouldBeMappedCorrectly: Validates that when
 *   the ReceiptMapper's mapToEntity method is called with a ReceiptInputDTO, a Receipt
 *   with the correct default and input values is returned.
 */
@SpringBootTest
class ReceiptMapperTest {

    @InjectMocks
    private ReceiptMapper receiptMapper;

    private ReceiptInputDTO receiptInputDTO;

    private static final String DEFAULT_ORIGIN = "London";
    private static final String DEFAULT_DESTINATION = "France";
    private static final BigDecimal DEFAULT_PRICE = new BigDecimal("20.00");

    /**
     * Sets up the initial conditions for each unit test in the ReceiptMapperTest class.
     * This method is annotated with @BeforeEach, indicating it is executed before each test method.
     * It initializes a new instance of ReceiptInputDTO with predefined sample data,
     * specifically setting the name, surname, and email fields to predetermined values.
     * This setup ensures that each test has a consistent starting state, essential for reliable and repeatable test results.
     */
    @BeforeEach
    public void setup() {
        receiptInputDTO = new ReceiptInputDTO();
        receiptInputDTO.setName("John");
        receiptInputDTO.setSurname("Doe");
        receiptInputDTO.setEmail("john.doe@example.com");
    }

    /**
     * Validates the behavior of the ReceiptMapper's mapToEntity method, ensuring that it
     * correctly transforms a ReceiptInputDTO into a Receipt entity with both default and
     * input-specific values.
     *
     * This test case performs the following validations:
     * - Checks that the resulting Receipt entity is not null.
     * - Verifies that the origin of the Receipt matches the default value set in the mapper.
     * - Validates that the destination of the Receipt matches the default value set in the mapper.
     * - Confirms that the price of the Receipt aligns with the default value specified.
     * - Ensures that the name in the Receipt matches the input value provided in the ReceiptInputDTO.
     * - Validates that the surname in the Receipt equals the input value from the ReceiptInputDTO.
     * - Confirms that the email within the Receipt corresponds to the input value from the ReceiptInputDTO.
     */
    @Test
    void whenMapToEntityCalled_thenReceiptShouldBeMappedCorrectly() {

        // When
        Receipt receipt = receiptMapper.mapToEntity(receiptInputDTO);

        // Then
        assertNotNull(receipt);
        assertEquals(DEFAULT_ORIGIN, receipt.getOrigin());
        assertEquals(DEFAULT_DESTINATION, receipt.getDestination());
        assertEquals(DEFAULT_PRICE, receipt.getPrice());
        assertEquals(receiptInputDTO.getName(), receipt.getName());
        assertEquals(receiptInputDTO.getSurname(), receipt.getSurname());
        assertEquals(receiptInputDTO.getEmail(), receipt.getEmail());
    }
}