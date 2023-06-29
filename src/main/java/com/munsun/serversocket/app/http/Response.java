package com.munsun.serversocket.app.http;


import java.util.Arrays;

public class Response {
    private ResponseLine responseLine;
    private ResponseHeaders headers;
    private byte[] body;

    public ResponseLine getResponseLine() {
        return responseLine;
    }

    public void setResponseLine(ResponseLine responseLine) {
        this.responseLine = responseLine;
    }

    public ResponseHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(ResponseHeaders headers) {
        this.headers = headers;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    @Override
    public String toString() {
        String clrf = "\r\n";
        return responseLine.toString() + clrf
                + headers.toString() + clrf
                + Arrays.toString(body);
    }
}
