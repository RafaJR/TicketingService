package com.cloudbees.trainapi.ticketing.application.dto.output;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserSeatOutputDTO implements Serializable {

    private String name;

    private String surname;

    private String email;

    private String section;

    private Integer seatNumber;
}
