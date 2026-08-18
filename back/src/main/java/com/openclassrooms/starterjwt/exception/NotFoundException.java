package com.openclassrooms.starterjwt.exception;

public class NotFoundException extends CodeException {
    public NotFoundException(String code, Object... args) {
        super(code, args);
    }
}
