package com.openclassrooms.starterjwt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class NotFoundException extends CodeException {
    public NotFoundException(String code, Object... args) {
        super(code, args);
    }
}
