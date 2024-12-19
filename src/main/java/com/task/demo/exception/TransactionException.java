package com.task.demo.exception;

public class TransactionException extends IllegalArgumentException {

    public TransactionException(String msg) {
        super(msg);
    }

    public TransactionException(String msg, Throwable cause) {
        super(msg, cause);
    }
}