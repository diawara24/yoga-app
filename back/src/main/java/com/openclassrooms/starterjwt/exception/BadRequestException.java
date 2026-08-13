package com.openclassrooms.starterjwt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class BadRequestException extends CodeException {
    public BadRequestException(String code, Object... args) {
        super(code, args);
    }
}
