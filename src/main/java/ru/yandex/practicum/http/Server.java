package ru.yandex.practicum.http;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.http.handler.UserHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(8080), 0);
        httpServer.createContext(UserHandler.PATH, new UserHandler());
        httpServer.start();
    }


}
