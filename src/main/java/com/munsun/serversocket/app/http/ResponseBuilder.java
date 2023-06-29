package com.munsun.serversocket.app.http;

import java.io.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ResponseBuilder {
    private String protocol;
    private HttpStatus httpStatus;

    private Map<String, String> headers;
    private byte[] body;

    public ResponseBuilder() {
        headers = new HashMap<>();
    }

    public ResponseBuilder protocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public ResponseBuilder httpStatus(HttpStatus status) {
        this.httpStatus = status;
        return this;
    }

    public ResponseBuilder addHeader(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public ResponseBuilder body(byte[] body) {
        this.body = body;
        return this;
    }

    public ResponseBuilder noBody() {
        this.body = new byte[]{20};
        return this;
    }

    public Response build() {
        Response response = new Response();
            ResponseLine line = new ResponseLine();
                line.setHttpStatus(this.httpStatus);
                if(protocol == null)
                    line.setProtocol("HTTP/1.1");
                else
                    line.setProtocol(protocol);
            ResponseHeaders headers = new ResponseHeaders();
                headers.setHeaders(this.headers);
            response.setResponseLine(line);
            response.setHeaders(headers);
            if(body == null)
                this.noBody();
            response.setBody(body);
        return response;
    }
}
