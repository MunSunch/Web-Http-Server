package com.munsun.serversocket.app.http;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public byte[] getBytes() {
        byte[] clrf = "\r\n".getBytes();
        var bytesResponseLine = getResponseLine().toString().getBytes();
        var bytesHeaders = getHeaders().toString().getBytes();
        var bytesBody = getBody();
        byte[] result = new byte[bytesResponseLine.length
                                    +bytesHeaders.length
                                    +bytesBody.length
                                    +clrf.length
                                    +clrf.length
                                    +clrf.length];

        int i=0;
        for (int j = 0; j < bytesResponseLine.length; i++, j++) {
            result[i] = bytesResponseLine[j];
        }
        for (int j=0; j < clrf.length; i++, j++) {
            result[i] = clrf[j];
        }
        for (int j=0; j < bytesHeaders.length; i++,j++) {
            result[i] = bytesHeaders[j];
        }
        for (int j=0; j < clrf.length; i++, j++) {
            result[i] = clrf[j];
        }
        for (int j=0; j < clrf.length; i++,j++) {
            result[i] = clrf[j];
        }
        for (int j=0; j < bytesBody.length; i++,j++) {
            result[i] = bytesBody[j];
        }
        return result;
    }

    @Override
    public String toString() {
        String clrf = "\r\n";
        return responseLine.toString() + clrf
                + headers.toString() + clrf
                + Arrays.toString(body);
    }
}
