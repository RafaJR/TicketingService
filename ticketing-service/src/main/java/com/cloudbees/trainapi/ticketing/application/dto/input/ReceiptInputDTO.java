package com.cloudbees.trainapi.ticketing.application.dto.input;
import com.cloudbees.trainapi.ticketing.utils.TicketingServiceConstants;
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
public class ReceiptInputDTO implements Serializable {

    @NotNull
    @Pattern(regexp = TicketingServiceConstants.USER_NAME_VALIDATION_REGEX, message = TicketingServiceConstants.USER_NAME_ERROR_MESSAGE)
    private String name;

    @NotNull
    @Pattern(regexp = TicketingServiceConstants.USER_NAME_VALIDATION_REGEX, message = TicketingServiceConstants.USER_SURNAME_ERROR_MESSAGE)
    private String surname;

    @NotNull
    @Email(message = TicketingServiceConstants.USER_EMAIL_ERROR_MESSAGE)
    private String email;
}
