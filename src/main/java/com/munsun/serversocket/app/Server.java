package com.munsun.serversocket.app;

import com.munsun.serversocket.app.http.*;

import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final CopyOnWriteArrayList<String> validPaths;
    private final ConcurrentHashMap<String, Handler> handlers;
    private final ExecutorService pool;
    private final Parser parser;

    public Server(int countThread, Parser parser) {
        this.validPaths = new CopyOnWriteArrayList<>();
        this.pool = Executors.newFixedThreadPool(countThread);
        this.parser = parser;
        this.handlers = new ConcurrentHashMap<>();
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
                            executeRequest(request, out);
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

    private String readRequest(BufferedReader in) throws IOException {
        String line;
        StringBuilder res = new StringBuilder();
        while(!"".equals(line = in.readLine())) {
            res.append(line);
            res.append("\r\n");
        }
        return res.toString();
    }

    public void addHandler(HttpMethodType methodType, String path) {
        this.handlers.putIfAbsent(path, new DefaultHandler());
    }

    public void addHandler(HttpMethodType methodType, String path, Handler handler) {
        this.handlers.putIfAbsent(path, handler);
    }

    private void executeRequest(Request request, BufferedOutputStream out) throws IOException {
        String path = request.getRequestLine().getUri();
        if(!handlers.containsKey(path)) {
            new NotFoundExceptionHandler().handle(request, out);
        } else {
            this.handlers.get(path).handle(request, out);
        }
    }
}
