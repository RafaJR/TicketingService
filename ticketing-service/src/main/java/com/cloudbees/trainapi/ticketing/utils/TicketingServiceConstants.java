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
    public static final String USER_SURNAME_ERROR_MESSAGE = "Each surname must start with uppercase and contain only alphabetic characters," +
            " up to two surnames separated by space";
    public static final String USER_EMAIL_ERROR_MESSAGE = "Must be a valid email format, e.g. 'example@mail.com'";
    public static final String SECTION_ERROR_MESSAGE = "The ticket section must be either A or B";
    public static final String SEAT_NUMBER_ERROR_MESSAGE = "The seat must be a number between 1 and 10";
    public static final String PRIZE_ERROR_MESSAGE = "The price must be a decimal number with up to 10 digits and 2 decimal places.";

    // Runtime exception messages
    public static final String OVER_BOOKING_ERROR_MESSAGE = "Saving failed due to an over booking issue: there are no free seats left in any of the train sections.";
    public static final String RECEIPT_NOT_FOUND_ERROR_MESSAGE = "Receipt not found with the given ID.";

    // Service Log messages
    public static final String LOG_FIND_SECTION = "Finding the section with the fewest tickets...";
    public static final String LOG_SECTION_SELECTED = "Section selected: {}";
    public static final String LOG_NO_SEATS_LEFT = "No available seats left in section: {}";
    public static final String LOG_MAPPING_SUCCESS = "Mapping DTO to entity successful: {}";
    public static final String LOG_RECEIPT_SAVED = "Receipt saved successfully with ID: {}";
    public static final String LOG_ATTEMPT_DELETE_RECEIPT = "Attempting to delete receipt with ID: {}";
    public static final String LOG_RECEIPT_NOT_FOUND = "Receipt not found with ID: {}";
    public static final String LOG_RECEIPT_DELETED = "Receipt deleted successfully with ID: {}";

    // Controller log messages
    public static final String LOG_ATTEMPT_PURCHASE = "Attempting to purchase ticket for ReceiptInputDTO: {}";
    public static final String LOG_RECEIPT_CREATED = "Receipt created successfully with ID: {}";
    public static final String LOG_OVERBOOKING_ISSUE = "Overbooking issue encountered: {}";
    public static final String LOG_UNEXPECTED_ERROR = "An error occurred while creating receipt: {}";

    // HTTP response messages
    public static final String RESPONSE_RECEIPT_CREATED = "Receipt created successfully";
    public static final String RESPONSE_OVERBOOKING_ERROR = "Saving failed due to an overbooking issue: there are no free seats left" +
            " in any of the train sections. Remember that there are only two sections (A and B) and that each section has only 10 seats.";
    public static final String RESPONSE_UNEXPECTED_ERROR = "An unexpected error occurred while processing your request.";
    public static final String RESPONSE_RECEIPT_DELETED = "Receipt deleted successfully";
    public static final String RESPONSE_RECEIPT_NOT_FOUND = "Receipt not found with the specified ID";


    // Log and response messages for updating a receipt
    public static final String LOG_ATTEMPT_UPDATE_RECEIPT = "Attempting to update receipt with ID: {}";
    public static final String LOG_RECEIPT_UPDATED = "Receipt updated successfully with ID: {}";
    public static final String LOG_SEAT_ALREADY_OCCUPIED = "Attempt to assign an occupied seat: section {}, seat number {}";

    // Exception and response messages for the receipt update process
    public static final String SEAT_OCCUPIED_ERROR_MESSAGE = "The selected seat is already occupied in the specified section.";
    public static final String RESPONSE_RECEIPT_UPDATED = "Receipt updated successfully";

    // Log and response messages for seats by section query
    public static final String LOG_ATTEMPT_FETCH_RECEIPTS_BY_SECTION = "Attempting to fetch receipts for section: {}";
    public static final String LOG_RECEIPT_NOT_FOUND_BY_SECTION = "No receipts found for section: {}";
    public static final String LOG_RECEIPTS_FETCHED_SUCCESS = "Fetched {} receipts successfully for section: {}";
    public static final String NO_RECEIPTS_FOUND_ERROR_MESSAGE = "No receipts found for the specified section.";
    public static final String RESPONSE_RECEIPTS_FETCHED_SUCCESS = "Receipts fetched successfully for the specified section.";

    private TicketingServiceConstants() {
    }
}
