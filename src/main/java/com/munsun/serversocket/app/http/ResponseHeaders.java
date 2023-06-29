package com.munsun.serversocket.app.http;

import java.util.Map;
import java.util.stream.Collectors;

public class ResponseHeaders {
    private Map<String, String> headers;

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    @Override
    public String toString() {
        String clrf = "\r\n";
        String headers = this.headers.entrySet().stream()
                .map(x -> x.getKey() + ":" + x.getValue())
                .collect(Collectors.joining(clrf));
        return headers;
    }
}
