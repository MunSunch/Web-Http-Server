package com.munsun.serversocket.app;

import com.munsun.serversocket.app.http.*;

import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final CopyOnWriteArrayList<String> validPaths;
    private final ExecutorService pool;
    private final Parser parser;

    public Server(int countThread, Parser parser) {
        this.validPaths = new CopyOnWriteArrayList<>();
            validPaths.add("/spring.png");
            validPaths.add("/resources.html");
            validPaths.add("/apache-tomcat.jpg");
            validPaths.add("/spring-framework-logo.png");
            validPaths.add("/style.css");
            validPaths.add("/classic.html");
        this.pool = Executors.newFixedThreadPool(countThread);
        this.parser = parser;
    }

    public void start(int port) {
        try (final var serverSocket = new ServerSocket(port)) {
            while(true) {
                final var socket = serverSocket.accept();
                if(!socket.isClosed()) {
                    pool.execute(() -> {
                        try (
                                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                final var out = new BufferedOutputStream(socket.getOutputStream());
                        ) {
                            String rawRequest = readRequest(in);
                            Request request = parser.toRequest(rawRequest);
                            Response response = executeRequest(request);
                            writeResponse(out, response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Thread.currentThread().interrupt();
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeResponse(BufferedOutputStream out, Response response) throws IOException {
        String clrf = "\r\n";
        out.write(response.getResponseLine().toString().getBytes());
        out.write(clrf.getBytes());
        out.write(response.getHeaders().toString().getBytes());
        out.write(clrf.getBytes());
        out.write(clrf.getBytes());
        out.write(response.getBody());
        out.flush();
    }

    private String readRequest(BufferedReader in) throws IOException {
        String line;
        StringBuilder res = new StringBuilder();
        while(!"".equals(line = in.readLine())) {
            res.append(line);
            res.append("\r\n");
        }
        return res.toString();
    }

    private Response executeRequest(Request request) throws IOException {
        String path = request.getRequestLine().getUri();
        if (!validPaths.contains(path)) {
            return new ResponseBuilder()
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .addHeader("Content-Length", "0")
                    .addHeader("Connection", "close")
                    .noBody()
                    .build();
        }

        final var filePath = Path.of("./src/main/resources/static", path);
        final var mimeType = Files.probeContentType(filePath);
        final var length = Files.size(filePath);

        // special case for classic
        if (path.equals("/classic.html")) {
            final var template = Files.readString(filePath);
            final var content = template.replace(
                    "{time}",
                    LocalDateTime.now().toString()
            ).getBytes();

            return new ResponseBuilder()
                    .httpStatus(HttpStatus.OK)
                    .addHeader("Content-Type", mimeType)
                    .addHeader("Content-Length", String.valueOf(content.length))
                    .addHeader("Connection", "close")
                    .body(content)
                    .build();
        }

        var body = Files.readAllBytes(filePath);
        return new ResponseBuilder()
                .httpStatus(HttpStatus.OK)
                .addHeader("Content-Type", mimeType)
                .addHeader("Content-Length", String.valueOf(length))
                .addHeader("Connection", "close")
                .body(body)
                .build();
    }
}
