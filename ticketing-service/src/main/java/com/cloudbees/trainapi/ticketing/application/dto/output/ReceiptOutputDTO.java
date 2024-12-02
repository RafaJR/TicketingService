package com.cloudbees.trainapi.ticketing.application.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Schema(description = "Data transfer object for receipt output")
public class ReceiptOutputDTO implements Serializable {

    @Schema(description = "The origin of the trip", example = "New York")
    private String origin;

    @Schema(description = "The destination of the trip", example = "Los Angeles")
    private String destination;

    @Schema(description = "The price of the ticket", example = "199.99")
    private BigDecimal price;

    @Schema(description = "The first name of the passenger", example = "John")
    private String name;

    @Schema(description = "The surname of the passenger", example = "Doe")
    private String surname;

    @Schema(description = "The email of the passenger", example = "john.doe@example.com")
    private String email;

    @Schema(description = "The section of the seat", example = "A")
    private String section;

    @Schema(description = "The seat number within the section", example = "5")
    private Integer seatNumber;
}