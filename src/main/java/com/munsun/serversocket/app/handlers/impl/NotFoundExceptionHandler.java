package com.munsun.serversocket.app.handlers.impl;

import com.munsun.serversocket.app.handlers.Handler;
import com.munsun.serversocket.app.http.HttpStatus;
import com.munsun.serversocket.app.http.Request;
import com.munsun.serversocket.app.http.Response;
import com.munsun.serversocket.app.http.ResponseBuilder;

import java.io.BufferedOutputStream;
import java.io.IOException;

public class NotFoundExceptionHandler implements Handler {
    @Override
    public void handle(Request request, BufferedOutputStream out) throws IOException {
        Response response = new ResponseBuilder()
                                .httpStatus(HttpStatus.NOT_FOUND)
                                .addHeader("Content-Length", "0")
                                .addHeader("Connection", "close")
                                .noBody()
                                .build();
        out.write(response.getBytes());
        out.flush();
    }
}
