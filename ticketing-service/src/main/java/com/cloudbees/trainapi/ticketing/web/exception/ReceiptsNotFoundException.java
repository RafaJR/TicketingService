package com.cloudbees.trainapi.ticketing.web.exception;

/**
 * Thrown to indicate that no receipts were found when attempting
 * to access a collection or database of receipts.
 *
 * This exception is a type of {@link RuntimeException}, meaning
 * it is an unchecked exception that does not need to be declared
 * in a method or constructor's {@code throws} clause.
 *
 * This can be used in scenarios where receipt processing
 * requires at least one receipt to be present, and the absence
 * of receipts is considered an exceptional condition.
 *
 * The exception carries a message that provides additional
 * information about the cause of the exception.
 */
public class ReceiptsNotFoundException extends RuntimeException {
    public ReceiptsNotFoundException(String message) {
        super(message);
    }
}
