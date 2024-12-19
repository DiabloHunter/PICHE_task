package com.task.demo.exception;

public class NotFoundException extends Exception {

    public NotFoundException(String msg) {
        super(msg);
    }

    public NotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
