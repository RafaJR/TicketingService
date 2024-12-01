package com.cloudbees.trainapi.ticketing.web.exception;

/**
 * Exception thrown when an attempt is made to purchase a ticket, but there
 * are no available seats left in the selected section.
 *
 * This exception is typically used in the context of ticket purchasing systems
 * to indicate that the desired operation cannot be completed because of a lack
 * of available seating. It signals to the calling process that alternative actions
 * might be required, such as selecting a different section or waiting for a
 * cancellation.
 */
public class NoAvailableSeatsException extends RuntimeException {
    public NoAvailableSeatsException(String message) {
        super(message);
    }
}
