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
     * Maps the provided ReceiptInputDTO to a Receipt entity. This method sets the
     * origin, destination, and price fields of the Receipt entity to default values,
     * and the name, surname, and email fields are populated with the content of the
     * provided DTO.
     *
     * @param dto the ReceiptInputDTO object containing the data to map to the Receipt entity
     * @return a newly constructed Receipt entity with data from the provided DTO and default values for other fields
     */
    public Receipt mapToEntity(ReceiptInputDTO dto) {
        return Receipt.builder()
                .origin(DEFAULT_ORIGIN)
                .destination(DEFAULT_DESTINATION)
                .price(DEFAULT_PRICE)
                .name(dto.getName())
                .surname(dto.getSurname())
                .email(dto.getEmail())
                .build();
    }
}