package com.cloudbees.trainapi.ticketing.web.exception;


/**
 * Exception thrown when a requested receipt cannot be found in the database.
 *
 * This exception is typically used in operations where a receipt is
 * expected to be present, such as when attempting to delete or retrieve
 * a receipt by its ID. The presence of this exception indicates that an
 * operation cannot proceed because the receipt does not exist.
 */
public class ReceiptNotFoundException extends RuntimeException {

    public ReceiptNotFoundException(String message) {
        super(message);
    }
}
