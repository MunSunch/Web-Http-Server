package com.munsun.serversocket.app;

import com.munsun.serversocket.app.exceptions.BadRequestException;
import com.munsun.serversocket.app.http.HttpMethodType;
import com.munsun.serversocket.app.http.Request;
import com.munsun.serversocket.app.http.RequestLine;
import org.apache.hc.core5.net.URLEncodedUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpByteParser implements Parser {
    private int pointer;
    private static final byte[] CLRF = new byte[]{'\r', '\n'};
    private static final byte[] DOUBLE_CLRF = new byte[]{'\r', '\n', '\r', '\n'};

    @Override
    public Request toRequest(byte[] rawRequest) {
        Request request = new Request();
            request.setRequestLine(parseRequestLine(rawRequest));
            request.setHeaders(parseHeaders(rawRequest));
            request.setBody(parseBody(rawRequest));
        reset();
        return request;
    }


    private RequestLine parseRequestLine(byte[] rawRequest) {
        int endRequestLine = indexOf(rawRequest, CLRF);
        if(endRequestLine == -1) {
            throw new BadRequestException("Request line not found!");
        }
        String[] partsRequestLine = new String(Arrays.copyOfRange(rawRequest, pointer, endRequestLine)).split(" ");
        pointer += endRequestLine + CLRF.length;

        RequestLine line = new RequestLine();
            line.setProtocol(partsRequestLine[2]);
            line.setMethodType(HttpMethodType.valueOf(partsRequestLine[0]));
            line.setUri(partsRequestLine[1]);
        return line;
    }

    private int indexOf(byte[] buffer, byte[] sequence) {
        return indexOf(buffer, 0, sequence);
    }

    private int indexOf(byte[] buffer, int startIndex, byte[] sequence) {
        OUTER:
        for (int i = startIndex; i < buffer.length; i++) {
            for (int j = 0; j < sequence.length; j++) {
                if(buffer[i+j] != sequence[j]) {
                    continue OUTER;
                }
            }
            return i;
        }
        return -1;
    }

    private Map<String, String> parseHeaders(byte[] rawRequest) {
        int endHeadersRequest = indexOf(rawRequest, DOUBLE_CLRF);
        if(endHeadersRequest == -1 || endHeadersRequest == (pointer+1)) {
            throw new BadRequestException("Headers not found!");
        }

        Map<String, String> headers = new HashMap<>();
        int endHeaderLine = 0;
        for (int i = pointer; i < endHeadersRequest+1;) {
            endHeaderLine = indexOf(rawRequest, i, CLRF);
            String[] partsHeader = new String(Arrays.copyOfRange(rawRequest, i, endHeaderLine)).split(":");
            if(partsHeader.length == 3) {
                headers.put(partsHeader[0], (partsHeader[1] + ":" + partsHeader[2]).trim());
            } else {
                headers.put(partsHeader[0], partsHeader[1].trim());
            }
            i = endHeaderLine + CLRF.length;
        }
        pointer += endHeaderLine + CLRF.length;
        return headers;
    }

    private byte[] parseBody(byte[] rawRequest) {
        return Arrays.copyOfRange(rawRequest, pointer, rawRequest.length);
    }

    private void reset() {
        pointer = 0;
    }
}
