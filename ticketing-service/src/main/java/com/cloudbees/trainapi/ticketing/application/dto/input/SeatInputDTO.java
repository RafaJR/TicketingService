package com.cloudbees.trainapi.ticketing.application.dto.input;

import com.cloudbees.trainapi.ticketing.utils.TicketingServiceConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Schema(description = "Data transfer object for seat input")
public class SeatInputDTO implements Serializable {

    @NotNull
    @Schema(description = "The unique identifier of the receipt", example = "123")
    private Long id;

    @NotNull
    @Pattern(
            regexp = TicketingServiceConstants.SECTION_VALIDATION_REGEX,
            message = TicketingServiceConstants.SECTION_ERROR_MESSAGE
    )
    @Schema(description = "The section of the seat", example = "A")
    private String section;

    @NotNull
    @Min(value = 1, message = TicketingServiceConstants.SEAT_NUMBER_ERROR_MESSAGE)
    @Max(value = 10, message = TicketingServiceConstants.SEAT_NUMBER_ERROR_MESSAGE)
    @Schema(description = "The seat number within the section", example = "5")
    private Integer seatNumber;
}