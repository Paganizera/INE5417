package ine5417.records;

import java.time.LocalDateTime;

/**
 * Represents an error response
 * @param timestamp the exact moment of th exception
 * @param status the http status
 * @param error the http error
 * @param message the error message
 * @param path the endpoint
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {}