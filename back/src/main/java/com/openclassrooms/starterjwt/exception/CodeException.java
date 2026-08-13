package com.openclassrooms.starterjwt.exception;

import lombok.Getter;

import java.util.Arrays;

@Getter
public abstract class CodeException extends RuntimeException {

    private final String code;
    private final Object[] args;


    protected CodeException(String code, Object... args) {
        super(code + (args.length > 0 ? " " + Arrays.toString(args) : ""));
        this.code = code;
        this.args = args;
    }
}
