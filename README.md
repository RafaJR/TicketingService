# Train Ticketing Service

## Table of Contents

- [Overview](#overview)
- [System Requirements](#system-requirements)
- [Build and Run](#build-and-run)
- [Running Unit Tests](#running-unit-tests)
- [Deployment URL](#deployment-url)
- [Postman Collection](#postman-collection)
- [H2 Database Access](#h2-database-access)
- [API Documentation](#api-documentation)
- [Extra Details](#extra-details)
- [Actuator Endpoints](#actuator-endpoints)

## Overview

The Train Ticketing Service is a RESTful API designed to manage train ticket purchases, user seating assignments, and related operations. Built with Spring Boot, it utilizes features such as Spring Data JPA for data access, Jersey for RESTful endpoints, and includes OpenAPI documentation.

## System Requirements

- JDK version 17
- Maven version 3.6 or higher

## Build and Run

To compile and run the application, use the following Maven commands:

```bash
# To compile the application
mvn clean install

# To start the application
mvn spring-boot:run
```

## Running Unit Tests

You can run all JUnit tests using Maven:

```bash
mvn test
```

Alternatively, you can execute individual test cases directly through your IDE or command line.

## Deployment URL

Once the application is running, it will be available at:

http://localhost:8080/api/trains/

## Postman Collection

A Postman collection file `202411301922-CloudBeesAssessment.postman_collection.json` is available in the root directory of the project to facilitate API consumption.

## H2 Database Access

The in-memory H2 database console can be accessed at the following URL:

**URL**: `http://localhost:8080/h2-console/`  
**Driver Class**: `org.h2.Driver`  
**JDBC URL**: `jdbc:h2:mem:ticketingservicebd`  
**User Name**: `admin`  
**Password**: (Leave it empty)

## API Documentation

The API endpoints are documented via Swagger. It can be accessed at:

http://localhost:8080/swagger-ui/index.html#/

### API Endpoints

#### Purchase a Ticket

- **Endpoint**: `POST /api/trains/purchase`
- **Description**: Handles the purchase of a ticket and returns a receipt.
- **Request Body**: `application/json`

```json
{
  "name": "John",
  "surname": "Doe",
  "email": "john.doe@example.com"
}
```

- **Responses**:
    - `201 Created`: Receipt created successfully.
    - `409 Conflict`: No available seats.
    - `500 Internal Server Error`: Indicates server-side issues.

#### Delete a Receipt

- **Endpoint**: `DELETE /api/trains/delete/{id}`
- **Description**: Deletes a receipt by its ID.
- **Parameters**:
    - `id`: Long (path variable), represents the ID of the receipt.
- **Responses**:
    - `200 OK`: Receipt deleted successfully.
    - `404 Not Found`: Receipt not found.
    - `500 Internal Server Error`: Indicates server-side issues.

#### Update Receipt Seat Information

- **Endpoint**: `PUT /api/trains/update-seat`
- **Description**: Updates the section and seat number of a receipt.
- **Request Body**: `application/json`

```json
{
  "id": 123,
  "section": "A",
  "seatNumber": 5
}
```

- **Responses**:
    - `200 OK`: Receipt updated successfully.
    - `404 Not Found`: Receipt not found.
    - `409 Conflict`: Seat already occupied.
    - `500 Internal Server Error`: Indicates server-side issues.

#### Get Receipts by Section

- **Endpoint**: `GET /api/trains/receipts/section/{section}`
- **Description**: Fetches all receipts for a particular section.
- **Parameters**:
    - `section`: String (path variable), must match pattern `TicketingServiceConstants.SECTION_VALIDATION_REGEX`.
- **Responses**:
    - `200 OK`: Receipts fetched successfully.
    - `404 Not Found`: No receipts found for section.
    - `500 Internal Server Error`: Indicates server-side issues.

#### Get Receipt by ID

- **Endpoint**: `GET /api/trains/receipt/{id}`
- **Description**: Retrieves a receipt using its unique ID.
- **Parameters**:
    - `id`: Long (path variable), represents the unique identifier of the receipt.
- **Responses**:
    - `200 OK`: Receipt fetched successfully.
    - `404 Not Found`: Receipt not found by ID.
    - `500 Internal Server Error`: Indicates server-side issues.

## Extra Details

- **Automatic Seat Assignment**: The system automatically assigns seats by always reserving a seat in the least occupied section. The seat number is the first available within each section. If no seats are available, an exception is triggered.
## Actuator Endpoints

Spring Boot Actuator provides several endpoints that are useful for monitoring our application and retrieving vital information about its state:

- **`/actuator/health`**: Provides the health status of the application, showing whether it is up and running.

- **`/actuator/info`**: Displays general information about the application, such as its name and version, which can be configured in the `application.yml`.

- **`/actuator/metrics`**: Offers detailed metrics, including memory statistics, CPU usage, and more.

- **`/actuator/loggers`**: Allows you to view and change the logging levels of the application in real-time.

- **`/actuator/env`**: Shows all the configuration properties and environment variables of the application.

- **`/actuator/beans`**: Lists all the Spring beans present in the application context.

- **`/actuator/threaddump`**: Provides a thread dump of the active JVM threads, useful for debugging concurrency or performance issues.

- **`/actuator/httptrace`**: Displays the trace of the latest HTTP requests processed by the application.

- **`/actuator/mappings`**: Presents a map of all HTTP routes exposed by Spring controllers.

- **`/actuator/scheduledtasks`**: Lists the active scheduled tasks in the application.

- **`/actuator/auditevents`**: Exposes important audit events for the application.