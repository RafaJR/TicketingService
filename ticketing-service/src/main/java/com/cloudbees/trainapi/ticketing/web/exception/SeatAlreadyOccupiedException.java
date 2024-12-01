package com.cloudbees.trainapi.ticketing.web.exception;

/**
 * Exception thrown to indicate that a seat is already occupied.
 * This exception is typically used in scenarios where a system
 * attempts to reserve or allocate a seat that is not available
 * due to it being already in use or reserved by another entity.
 */
public class SeatAlreadyOccupiedException extends RuntimeException {
    public SeatAlreadyOccupiedException(String message) {
        super(message);
    }
}
