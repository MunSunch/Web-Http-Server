package com.munsun.serversocket.app.exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super("Bad request! " + message);
    }
}
