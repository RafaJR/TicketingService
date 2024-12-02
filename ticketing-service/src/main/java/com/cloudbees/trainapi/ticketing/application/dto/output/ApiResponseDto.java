package com.cloudbees.trainapi.ticketing.application.dto.output;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Schema(description = "Generic API response wrapper")
public class ApiResponseDto<T> {

    @Schema(description = "Status of the API response", example = "OK")
    private String status;

    @Schema(description = "HTTP status code of the response", example = "200")
    private Integer statusCode;

    @Schema(description = "Message providing additional information about the response", example = "Request successful")
    private String message;

    @Schema(description = "Indicates whether the API call was successful", example = "true")
    private boolean success;

    @Schema(description = "The actual data returned in the response")
    private T data;
}
