package com.munsun.serversocket.app.handlers.impl;

import com.munsun.serversocket.app.handlers.Handler;
import com.munsun.serversocket.app.http.HttpStatus;
import com.munsun.serversocket.app.http.Request;
import com.munsun.serversocket.app.http.Response;
import com.munsun.serversocket.app.http.ResponseBuilder;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DefaultHandler implements Handler {
    @Override
    public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
        String uri = request.getRequestLine().getUri();
        int indexStartQuery = uri.indexOf('?');
        if(indexStartQuery != -1)
            uri = uri.substring(0, indexStartQuery);

        final var filePath = Path.of("./src/main/resources/static", uri);
        final var mimeType = Files.probeContentType(filePath);
        final var length = Files.size(filePath);
        var body = Files.readAllBytes(filePath);
        Response response =  new ResponseBuilder()
                                .httpStatus(HttpStatus.OK)
                                .addHeader("Content-Type", mimeType)
                                .addHeader("Content-Length", String.valueOf(length))
                                .addHeader("Connection", "close")
                                .body(body)
                                .build();
        responseStream.write(response.getBytes());
        responseStream.flush();
    }
}
