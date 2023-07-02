package com.munsun.serversocket.app;

import com.munsun.serversocket.app.http.HttpMethodType;
import com.munsun.serversocket.app.http.HttpStatus;
import com.munsun.serversocket.app.http.Response;
import com.munsun.serversocket.app.http.ResponseBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class ServerMain {
    private static final int DEFAULT_COUNT_THREAD = 64;
    public static void main(String[] args) {
        Server server = new Server(DEFAULT_COUNT_THREAD, new HttpByteParser());
        server.addHandler(HttpMethodType.GET, "/spring.png");
        server.addHandler(HttpMethodType.GET, "/apache-tomcat.jpg");
        server.addHandler(HttpMethodType.GET, "/spring-framework-logo.png");
        server.addHandler(HttpMethodType.GET, "/style.css");
        server.addHandler(HttpMethodType.GET, "/resources.html");
        server.addHandler(HttpMethodType.GET, "/classic.html", (request, responseStream) -> {
            final var filePath = Path.of("./src/main/resources/static", request.getRequestLine().getUri());
            final var mimeType = Files.probeContentType(filePath);
            final var template = Files.readString(filePath);
            final var content = template.replace(
                    "{time}",
                    LocalDateTime.now().toString()
            ).getBytes();

            Response response = new ResponseBuilder()
                                    .httpStatus(HttpStatus.OK)
                                    .addHeader("Content-Type", mimeType)
                                    .addHeader("Content-Length", String.valueOf(content.length))
                                    .addHeader("Connection", "close")
                                    .body(content)
                                    .build();
            responseStream.write(response.getBytes());
            responseStream.flush();
        });
        server.addHandler(HttpMethodType.GET, "/forms-get.html");
        server.addHandler(HttpMethodType.POST, "/forms-post.html");

        server.start(9998);
    }
}
