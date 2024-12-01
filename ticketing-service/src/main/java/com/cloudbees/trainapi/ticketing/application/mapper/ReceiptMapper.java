package com.cloudbees.trainapi.ticketing.application.mapper;

import com.cloudbees.trainapi.ticketing.application.dto.input.ReceiptInputDTO;
import com.cloudbees.trainapi.ticketing.domain.model.Receipt;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * The ReceiptMapper class is responsible for mapping data from a ReceiptInputDTO
 * object to a Receipt entity. It applies default values for the origin, destination,
 * and price attributes of the Receipt while using the provided name and email
 * data from the ReceiptInputDTO.
 */
@Component
public class ReceiptMapper {

    private static final String DEFAULT_ORIGIN = "London";
    private static final String DEFAULT_DESTINATION = "France";
    private static final BigDecimal DEFAULT_PRICE = new BigDecimal("20.00");


    /**
     * Maps the data from a ReceiptInputDTO instance to a new Receipt entity.
     * The method applies default values for origin, destination, and price,
     * while transferring the name and email from the provided DTO.
     *
     * @param dto the ReceiptInputDTO instance containing the data to be mapped.
     * @return a new Receipt entity populated with data from the provided DTO
     *         and default values for other fields.
     */
    public Receipt mapToEntity(ReceiptInputDTO dto) {
        Receipt receipt = new Receipt();
        receipt.setOrigin(DEFAULT_ORIGIN);
        receipt.setDestination(DEFAULT_DESTINATION);
        receipt.setPrice(DEFAULT_PRICE);
        receipt.setName(dto.getName());
        receipt.setSurname(dto.getSurname());
        receipt.setEmail(dto.getEmail());
        receipt.setEmail(dto.getEmail());
        return receipt;
    }
}