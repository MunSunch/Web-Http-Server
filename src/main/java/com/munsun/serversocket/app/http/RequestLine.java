package com.munsun.serversocket.app.http;

import com.munsun.serversocket.app.http.HttpMethodType;

public class RequestLine {
    private String protocol;
    private HttpMethodType methodType;
    private String uri;

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public HttpMethodType getMethodType() {
        return methodType;
    }

    public void setMethodType(HttpMethodType methodType) {
        this.methodType = methodType;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
