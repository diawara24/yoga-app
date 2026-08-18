package com.openclassrooms.starterjwt.exception;

public class BadRequestException extends CodeException {
    public BadRequestException(String code, Object... args) {
        super(code, args);
    }
}
