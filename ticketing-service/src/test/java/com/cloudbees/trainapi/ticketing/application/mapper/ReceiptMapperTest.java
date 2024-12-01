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


@SpringBootTest
public class ReceiptMapperTest {

    @InjectMocks
    private ReceiptMapper receiptMapper;

    private ReceiptInputDTO receiptInputDTO;

    private static final String DEFAULT_ORIGIN = "London";
    private static final String DEFAULT_DESTINATION = "France";
    private static final BigDecimal DEFAULT_PRICE = new BigDecimal("20.00");

    @BeforeEach
    public void setup() {
        receiptInputDTO = new ReceiptInputDTO();
        receiptInputDTO.setName("John");
        receiptInputDTO.setSurname("Doe");
        receiptInputDTO.setEmail("john.doe@example.com");
    }

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