package com.cloudbees.trainapi.ticketing.application.mapper;

import com.cloudbees.trainapi.ticketing.application.dto.input.ReceiptInputDTO;
import com.cloudbees.trainapi.ticketing.domain.model.Receipt;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ReceiptMapper {

    private static final String DEFAULT_ORIGIN = "London";
    private static final String DEFAULT_DESTINATION = "France";
    private static final BigDecimal DEFAULT_PRICE = new BigDecimal("20.00");

    // Método para mapear manualmente ReceiptInputDTO a Receipt
    public Receipt mapToEntity(ReceiptInputDTO dto) {
        Receipt receipt = new Receipt();
        String section = determineSection();
        int seatNumber = determineSeatNumber(section);
        // Establecer valores constantes y/o por defecto
        receipt.setOrigin(DEFAULT_ORIGIN);
        receipt.setDestination(DEFAULT_DESTINATION);
        receipt.setPrice(DEFAULT_PRICE);
        receipt.setSection(determineSection());
        receipt.setSeatNumber(determineSeatNumber(section));
        receipt.setName(dto.getName());
        receipt.setEmail(dto.getEmail());
        receipt.setEmail(dto.getEmail());
        return receipt;
    }

    private String determineSection() {

        return "A";
    }

    private int determineSeatNumber(String section) {

        return 1;
    }
}