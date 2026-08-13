package com.openclassrooms.starterjwt.exception;


public class BusinessException extends CodeException {
    public BusinessException(String code, Object... args) {
        super(code, args);
    }
}
