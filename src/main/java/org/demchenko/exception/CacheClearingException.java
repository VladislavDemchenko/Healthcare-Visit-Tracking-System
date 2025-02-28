package org.demchenko.exception;

public class CacheClearingException extends RuntimeException {
    public CacheClearingException(String message) {
        super(message);
    }
}
