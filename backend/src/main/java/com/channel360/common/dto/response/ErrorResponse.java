package com.channel360.common.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    boolean success,
    String message,
    List<String> errors,
    LocalDateTime timestamp,
    int statusCode
) {
    public static ErrorResponseBuilder builder() {
        return new ErrorResponseBuilder().success(false);
    }
}
