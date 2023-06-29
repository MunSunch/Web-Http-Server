package com.munsun.serversocket.app;

import com.munsun.serversocket.app.http.HttpMethodType;
import com.munsun.serversocket.app.http.Request;
import com.munsun.serversocket.app.http.RequestLine;

import java.util.HashMap;
import java.util.Map;

public class HttpParser implements Parser {
    private int pointer;

    @Override
    public Request toRequest(String rawRequest) {
        String clrf = "\r\n";
        String[] parts = rawRequest.split(clrf);

        Request request = new Request();
            request.setRequestLine(parseRequestStartLine(parts));
            request.setHeaders(parseHeaders(parts));
            request.setBody(parseBody(parts));
        resetPointer();
        return request;
    }

    private RequestLine parseRequestStartLine(String[] parts) {
        String[] partsRequestStartLine = parts[0].split(" ");
        checkRequestStartLine(partsRequestStartLine);
        pointer++;

        RequestLine line = new RequestLine();
            line.setMethodType(HttpMethodType.valueOf(partsRequestStartLine[0]));
            line.setUri(partsRequestStartLine[1]);
            line.setProtocol(partsRequestStartLine[2]);
        return line;
    }

    private void checkRequestStartLine(String[] partsRequestStartLine) {
        checkCountParts(partsRequestStartLine);
    }

    private void checkCountParts(String[] partsRequestStartLine) {
        if(partsRequestStartLine.length != 3)
            throw new RuntimeException("Request failed!");
    }

    private Map<String, String> parseHeaders(String[] parts) {
        Map<String, String> headers = new HashMap<>();
        while(pointer < parts.length && !"".equals(parts[pointer])) {
            String[] partsHeader = parts[pointer].split(":");
            String key = partsHeader[0].trim();
            String value = partsHeader[1].trim();
            headers.put(key, value);
            pointer++;
        }
        return headers;
    }

    private String parseBody(String[] parts) {
        if(pointer >= parts.length)
            return "";
        return parts[pointer++];
    }

    private void resetPointer() {
        pointer = 0;
    }
}
