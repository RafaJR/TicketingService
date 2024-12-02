package com.cloudbees.trainapi.ticketing.application.dto.input;
import com.cloudbees.trainapi.ticketing.utils.TicketingServiceConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.validation.constraints.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Schema(description = "Data transfer object for receipt input")
public class ReceiptInputDTO implements Serializable {

    @NotNull
    @Pattern(
            regexp = TicketingServiceConstants.USER_NAME_VALIDATION_REGEX,
            message = TicketingServiceConstants.USER_NAME_ERROR_MESSAGE
    )
    @Schema(description = "The first name of the passenger", example = "John")
    private String name;

    @NotNull
    @Pattern(
            regexp = TicketingServiceConstants.USER_NAME_VALIDATION_REGEX,
            message = TicketingServiceConstants.USER_SURNAME_ERROR_MESSAGE
    )
    @Schema(description = "The surname of the passenger", example = "Doe")
    private String surname;

    @NotNull
    @Email(message = TicketingServiceConstants.USER_EMAIL_ERROR_MESSAGE)
    @Schema(description = "The email of the passenger", example = "john.doe@example.com")
    private String email;
}
