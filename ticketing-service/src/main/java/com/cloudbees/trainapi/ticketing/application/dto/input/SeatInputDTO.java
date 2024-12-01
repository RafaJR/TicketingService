package com.cloudbees.trainapi.ticketing.application.dto.input;

import com.cloudbees.trainapi.ticketing.utils.TicketingServiceConstants;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SeatInputDTO implements Serializable {

    @NotNull
    private Long id;

    @NotNull
    @Pattern(regexp = TicketingServiceConstants.SECTION_VALIDATION_REGEX, message = TicketingServiceConstants.SECTION_ERROR_MESSAGE)
    private String section;

    @NotNull
    @Min(value = 1, message = TicketingServiceConstants.SEAT_NUMBER_ERROR_MESSAGE)
    @Max(value = 10, message = TicketingServiceConstants.SEAT_NUMBER_ERROR_MESSAGE)
    private Integer seatNumber;

}
