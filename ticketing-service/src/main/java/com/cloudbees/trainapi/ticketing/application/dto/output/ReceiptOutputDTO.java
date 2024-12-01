package com.cloudbees.trainapi.ticketing.application.dto.output;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ReceiptOutputDTO implements Serializable {

    private String origin;

    private String destination;

    private BigDecimal price;

    private String name;

    private String surname;

    private String email;

    private String section;

    private Integer seatNumber;
}
