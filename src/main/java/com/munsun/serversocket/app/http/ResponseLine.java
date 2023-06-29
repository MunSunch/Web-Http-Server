package com.munsun.serversocket.app.http;

import com.munsun.serversocket.app.http.HttpStatus;

public class ResponseLine {
    private String protocol;
    private HttpStatus httpStatus;

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Override
    public String toString() {
        return protocol + " " + httpStatus.getCode() + " " + httpStatus.getMessage();
    }
}
