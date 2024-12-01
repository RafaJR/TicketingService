package com.cloudbees.trainapi.ticketing.web.exception;

/**
 * Exception thrown when a receipt is not found by its identifier.
 * This is a runtime exception, indicating that the application
 * has attempted to retrieve a receipt using an ID that does not
 * correspond to any existing receipt in the system.
 * <p>
 * This exception is used to signal that an operation requiring a
 * specific receipt could not be completed because the receipt
 * identifier provided did not match any stored receipt records.
 */
public class ReceiptNotFoundByIdException extends RuntimeException {
    public ReceiptNotFoundByIdException(String message) {
        super(message);
    }
}
