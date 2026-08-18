package com.openclassrooms.starterjwt.exception;


public class UnauthorizedException extends CodeException {
    public UnauthorizedException(String code, Object... args) {
        super(code, args);
    }
}
