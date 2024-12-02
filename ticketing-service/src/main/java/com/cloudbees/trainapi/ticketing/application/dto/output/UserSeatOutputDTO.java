package com.cloudbees.trainapi.ticketing.application.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Schema(description = "Data transfer object for user seat output")
public class UserSeatOutputDTO implements Serializable {

    @Schema(description = "The first name of the user", example = "John")
    private String name;

    @Schema(description = "The surname of the user", example = "Doe")
    private String surname;

    @Schema(description = "The email address of the user", example = "john.doe@example.com")
    private String email;

    @Schema(description = "The section where the user's seat is located", example = "A")
    private String section;

    @Schema(description = "The seat number assigned to the user within the section", example = "5")
    private Integer seatNumber;
}