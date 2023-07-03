package com.munsun.serversocket.app;

import com.munsun.serversocket.app.handlers.Handler;
import com.munsun.serversocket.app.handlers.impl.DefaultHandler;
import com.munsun.serversocket.app.handlers.impl.NotFoundExceptionHandler;
import com.munsun.serversocket.app.http.*;

import java.io.*;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final CopyOnWriteArrayList<String> validPaths;
    private final ConcurrentHashMap<String, Handler> handlers;
    private final ExecutorService pool;
    private final Parser parser;
    private final int limit;
    private static final int DEFAULT_LIMIT = 4096;

    public Server(int countThread, Parser parser) {
        this(countThread, parser, DEFAULT_LIMIT);
    }

    public Server(int countThread, Parser parser, int limit) {
        this.validPaths = new CopyOnWriteArrayList<>();
        this.handlers = new ConcurrentHashMap<>();
        this.pool = Executors.newFixedThreadPool(countThread);
        this.parser = parser;
        this.limit = limit;
    }

    public void start(int port) {
        try (final var serverSocket = new ServerSocket(port)) {
            while(true) {
                final var socket = serverSocket.accept();
                if(!socket.isClosed()) {
                    pool.execute(() -> {
                        try (
                                final var in = new BufferedInputStream(socket.getInputStream());
                                final var out = new BufferedOutputStream(socket.getOutputStream());
                        ) {
                            byte[] rawRequest = readRequest(in);
                            Request request = parser.toRequest(rawRequest);

                            //Query params - GET
                            System.out.println(request.getQueryParams());
                            System.out.println(request.getQueryParam("image"));

                            executeRequest(request, out);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] readRequest(BufferedInputStream in) throws IOException {
        in.mark(limit);
        var buffer = new byte[limit];
        var eof = in.read(buffer);
        return buffer;
    }

    public void addHandler(HttpMethodType methodType, String path) {
        this.handlers.putIfAbsent(path, new DefaultHandler());
    }

    public void addHandler(HttpMethodType methodType, String path, Handler handler) {
        this.handlers.putIfAbsent(path, handler);
    }

    private void executeRequest(Request request, BufferedOutputStream out) throws IOException {
        String path = request.getRequestLine().getUri();
        int indexStartQuery = path.indexOf('?');
        if(indexStartQuery != -1)
            path = path.substring(0, indexStartQuery);

        if(!handlers.containsKey(path)) {
            new NotFoundExceptionHandler().handle(request, out);
        } else {
            this.handlers.get(path).handle(request, out);
        }
    }
}
