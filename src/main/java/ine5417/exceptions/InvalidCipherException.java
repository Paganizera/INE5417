package ine5417.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidCipherException extends RuntimeException {
    public InvalidCipherException(String message) {
        super(message);
    }
}