package com.munsun.serversocket.app;

import com.munsun.serversocket.app.http.Request;

public interface Parser {
    Request toRequest(byte[] rawRequest);
}
