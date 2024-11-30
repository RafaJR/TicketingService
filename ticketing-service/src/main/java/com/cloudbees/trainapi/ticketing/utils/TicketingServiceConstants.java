package com.cloudbees.trainapi.ticketing.utils;

public class TicketingServiceConstants {

    // Regular expressions for data input validation
    public static final String ORIGIN_DESTINATION_VALIDATION_REGEX = "London|France";
    public static final String USER_NAME_VALIDATION_REGEX = "^[A-Z][a-zA-Z]*$";
    public static final String SECTION_VALIDATION_REGEX = "[AB]";
    // Error input data messages
    public static final String ORIGIN_TICKET_ERROR_MESSAGE = "The ticket origin must be either London or France.";
    public static final String DESTINY_TICKET_ERROR_MESSAGE = "The ticket destiny must be either London or France.";
    public static final String ORIGIN_DESTINY_TICKET_ERROR_MESSAGE = "The ticket origin and destiny must be different.";
    public static final String USER_NAME_ERROR_MESSAGE = "The user name must start with uppercase and contain only alphabetic characters";
    public static final String USER_SURNAME_ERROR_MESSAGE = "Each surname must start with uppercase and contain only alphabetic characters, up to two surnames separated by space";
    public static final String USER_EMAIL_ERROR_MESSAGE = "Must be a valid email format, e.g. 'example@mail.com'";
    public static final String SECTION_ERROR_MESSAGE = "The ticket section must be either A or B";
    public static final String SEAT_NUMBER_ERROR_MESSAGE = "The seat must be a number between 1 and 10";
    public static final String PRIZE_ERROR_MESSAGE = "The price must be a decimal number with up to 10 digits and 2 decimal places.";

    private TicketingServiceConstants() {
    }
}
