package ru.yandex.practicum.http.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.http.model.User;
import ru.yandex.practicum.http.storage.Storage;
import ru.yandex.practicum.http.storage.UserStorage;

import java.io.IOException;
import java.io.OutputStream;
import java.util.NoSuchElementException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class UserHandler implements HttpHandler {

    public static final String PATH = "/user";

    private final ObjectMapper mapper = new ObjectMapper();
    private final Storage<User> userStorage = new UserStorage();

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            if ("GET".equals(httpExchange.getRequestMethod())) {
                get(httpExchange);
            } else if ("POST".equals(httpExchange.getRequestMethod())) {
                post(httpExchange);
            } else if ("PUT".equals(httpExchange.getRequestMethod())) {
                put(httpExchange);
            } else if ("DELETE".equals(httpExchange.getRequestMethod())) {
                delete(httpExchange);
            } else {
                httpExchange.sendResponseHeaders(405, 0);
            }
        } catch (NoSuchElementException e) {
            response(httpExchange, 404);
        } catch (IllegalArgumentException e) {
            response(httpExchange, 400);
        } catch (Error error) {
            response(httpExchange, 500);
        } finally {
            httpExchange.close();
        }
    }

    private void delete(HttpExchange httpExchange) throws IOException {
        String param = getPathParam(httpExchange);
        if (param.isEmpty()) {
            userStorage.removeAll();
        } else {
            long id = Integer.parseInt(param);
            userStorage.remove(id);
        }
        response(httpExchange, 204);
    }

    private void put(HttpExchange httpExchange) throws IOException {
        String param = getPathParam(httpExchange);
        if (param.isEmpty()) {
            throw new IllegalArgumentException("Bad Request");
        } else {
            long id = Integer.parseInt(param);
            String body = getBody(httpExchange);

            User user = mapper.readValue(body, User.class);
            user = userStorage.update(id, user);
            response(httpExchange, 200, mapper.writeValueAsString(user));
        }
    }

    private void post(HttpExchange httpExchange) throws IOException {
        String body = getBody(httpExchange);
        User user = mapper.readValue(body, User.class);
        userStorage.create(user);
        response(httpExchange, 201, mapper.writeValueAsString(user));
    }

    private String getBody(HttpExchange httpExchange) throws IOException {
        return new String(httpExchange.getRequestBody().readAllBytes(), UTF_8);
    }

    private void get(HttpExchange httpExchange) throws IOException {
        String param = getPathParam(httpExchange);
        if (param.isEmpty()) {
            response(httpExchange, 200, mapper.writeValueAsString(userStorage.findAll()));
        } else {
            long id = Integer.parseInt(param);
            response(httpExchange, 200, mapper.writeValueAsString(userStorage.findById(id).orElseThrow()));
        }
    }

    private String getPathParam(HttpExchange httpExchange) {
        String requestedPath = httpExchange.getRequestURI().getPath();
        String path = PATH + "/";

        return requestedPath.substring(requestedPath.indexOf(path) + path.length());
    }


    private void response(HttpExchange httpExchange, int code, String body) throws IOException {
        httpExchange.sendResponseHeaders(code, 0);

        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(body.getBytes());
        }
    }

    private void response(HttpExchange httpExchange, int code) throws IOException {
        response(httpExchange, code, "");
    }


}