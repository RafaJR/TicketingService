package com.cloudbees.trainapi.ticketing.domain.model;

import com.cloudbees.trainapi.ticketing.utils.TicketingServiceConstants;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * The Receipt class represents a receipt entity with details such as the origin and destination,
 * price, passenger's personal information, and seating details.
 * <p>
 * This class is mapped to the "RECEIPT" table in the database, with an index on the "SECTION" column
 * for optimized querying. The class uses various annotations to enforce constraints and define the
 * structure of the entity, including validation for fields to ensure data integrity.
 * <p>
 * Fields:
 * - id: The unique identifier for each receipt, generated automatically.
 * - from: The departure location, restricted to "London" or "France".
 * - to: The arrival location, restricted to "London" or "France".
 * - price: The cost of the ticket, which cannot be null.
 * - name: The passenger's first name, must start with an uppercase letter and contain only alphabetic characters.
 * - surname: The passenger's surname, can include two parts and must start with uppercase letters; only alphabetic characters are allowed.
 * - email: The passenger's email address, must be in a valid email format.
 * - section: The section of the seating, restricted to "A" or "B".
 * - seatNumber: The number of the seat, restricted to a value between 1 and 10.
 * <p>
 * This class also includes a custom validation to ensure the 'from' and 'to' fields have different values.
 */
@Entity
@Table(name = "RECEIPT", indexes = {
        @Index(name = "idx_section", columnList = "SECTION")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "ORIGIN", nullable = false)
    @NotNull
    @Pattern(regexp = TicketingServiceConstants.ORIGIN_DESTINATION_VALIDATION_REGEX, message = TicketingServiceConstants.ORIGIN_TICKET_ERROR_MESSAGE)
    private String from;

    @Column(name = "DESTINATION", nullable = false)
    @NotNull
    @Pattern(regexp = TicketingServiceConstants.ORIGIN_DESTINATION_VALIDATION_REGEX, message = TicketingServiceConstants.DESTINY_TICKET_ERROR_MESSAGE)
    private String to;

    @Column(name = "PRICE", nullable = false)
    @NotNull
    private Double price;

    @Column(name = "NAME", nullable = false, length = 30)
    @NotNull
    @Pattern(regexp = TicketingServiceConstants.USER_NAME_VALIDATION_REGEX, message = TicketingServiceConstants.USER_NAME_ERROR_MESSAGE)
    private String name;

    @Column(name = "SURNAME", nullable = false, length = 60)
    @NotNull
    @Pattern(regexp = TicketingServiceConstants.USER_SURNAME_VALIDATION_REGEX, message = TicketingServiceConstants.USER_SURNAME_ERROR_MESSAGE)
    private String surname;

    @Column(name = "EMAIL", nullable = false, length = 80)
    @NotNull
    @Email(message = "Must be a valid email format")
    @Pattern(regexp = TicketingServiceConstants.EMAIL_FORMAT_REGEX, message = TicketingServiceConstants.USER_EMAIL_ERROR_MESSAGE)
    private String email;

    @Column(name = "SECTION", nullable = false)
    @NotNull
    @Pattern(regexp = TicketingServiceConstants.SECTION_VALIDATION_REGEX, message = TicketingServiceConstants.SECTION_ERROR_MESSAGE)
    private String section;

    @Column(name = "SEAT_NUMBER", nullable = false)
    @NotNull
    @Min(value = 1, message = TicketingServiceConstants.SEAT_NUMBER_ERROR_MESSAGE)
    @Max(value = 10, message = TicketingServiceConstants.SEAT_NUMBER_ERROR_MESSAGE)
    private Integer seatNumber;

    @AssertTrue(message = TicketingServiceConstants.ORIGIN_DESTINY_TICKET_ERROR_MESSAGE)
    private boolean isFromAndToDifferent() {
        return !from.equals(to);
    }
}