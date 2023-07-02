package com.munsun.serversocket.app.http;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URLEncodedUtils;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Request {
    private RequestLine requestLine;
    private Map<String, String> headers;
    private byte[] body;

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public void setRequestLine(RequestLine requestLine) {
        this.requestLine = requestLine;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public List<String> getQueryParam(String name) {
        return getQueryParams().stream()
                .filter(x -> name.equals(x.getName()))
                .map(NameValuePair::getValue)
                .collect(Collectors.toList());
    }

    public List<NameValuePair> getQueryParams() {
        var uri = URI.create(requestLine.getUri());
        return URLEncodedUtils.parse(uri, StandardCharsets.UTF_8);
    }

    public List<String> getPostParam(String name) {
        if(!"x-www-form-urlencoded".equals(headers.get("Content-Type"))) {
            return null;
        }
        return getPostParams().stream()
                .filter(x -> name.equals(x.getName()))
                .map(NameValuePair::getValue)
                .collect(Collectors.toList());
    }

    public List<NameValuePair> getPostParams() {
        if(!"application/x-www-form-urlencoded".equals(headers.get("Content-Type"))) {
            return null;
        }
        String stringBody = new String(body);
        return URLEncodedUtils.parse(stringBody.subSequence(0, stringBody.length()), StandardCharsets.UTF_8);
    }

    @Override
    public String toString() {
        String clrf = "\r\n";
        String stringHeaders = headers.entrySet().stream()
                .map(x -> x.getKey() + ":" +x.getValue())
                .collect(Collectors.joining(clrf));
        return requestLine.getMethodType() + " " + requestLine.getUri() + " " + requestLine.getProtocol() + clrf
             + stringHeaders + clrf + clrf
             + new String(body, StandardCharsets.UTF_8);
    }
}
