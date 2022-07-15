package ru.yandex.practicum.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.http.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserApiTest {

    private static final String URL = "http://localhost:8080/user";
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();

    @BeforeEach
    public void initData() throws IOException, InterruptedException {
        sendDeleteRequest();

        sendPostRequest(new User("User 1"));
        sendPostRequest(new User("User 2"));
        sendPostRequest(new User("User 3"));
    }

    @Test
    public void testPost() throws IOException, InterruptedException {
        HttpResponse<String> response = sendPostRequest(new User("User 4"));
        assertEquals(201, response.statusCode());

        response = sendGetRequest();
        assertEquals(200, response.statusCode());
        List<User> users = mapper.readValue(response.body(), new TypeReference<>() {
        });
        assertEquals(4, users.size());
    }

    @Test
    public void testGet() throws IOException, InterruptedException {
        HttpResponse<String> response = sendGetRequest(1);
        assertEquals(200, response.statusCode());
        User user = mapper.readValue(response.body(), User.class);
        assertEquals("User 1", user.getName());
    }

    @Test
    public void testPut() throws IOException, InterruptedException {
        HttpResponse<String> response = sendPutRequest(1, new User("User 111"));
        assertEquals(200, response.statusCode());

        response = sendGetRequest(1);
        assertEquals(200, response.statusCode());
        User user = mapper.readValue(response.body(), User.class);
        assertEquals("User 111", user.getName());

        response = sendGetRequest();
        assertEquals(200, response.statusCode());
        List<User> users = mapper.readValue(response.body(), new TypeReference<>() {
        });
        assertEquals(3, users.size());
    }

    @Test
    public void testDelete() throws IOException, InterruptedException {
        HttpResponse<String> response = sendDeleteRequest(1);
        assertEquals(204, response.statusCode());

        response = sendGetRequest();
        assertEquals(200, response.statusCode());
        List<User> users = mapper.readValue(response.body(), new TypeReference<>() {
        });
        assertEquals(2, users.size());
    }

    private HttpResponse<String> sendPutRequest(long id, User user) throws IOException, InterruptedException {
        return client.send(HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(user)))
                .uri(URI.create(URL + "/" + id))
                .build(), HttpResponse.BodyHandlers.ofString());
    }


    private HttpResponse<String> sendPostRequest(User user) throws IOException, InterruptedException {
        return client.send(HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(user)))
                .uri(URI.create(URL))
                .build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendDeleteRequest() throws IOException, InterruptedException {
        return client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(URL))
                .build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendDeleteRequest(long id) throws IOException, InterruptedException {
        return client.send(HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(URL + "/" + id))
                .build(), HttpResponse.BodyHandlers.ofString());
    }


    private HttpResponse<String> sendGetRequest() throws IOException, InterruptedException {
        return client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(URL))
                .build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendGetRequest(long id) throws IOException, InterruptedException {
        return client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(URL + "/" + id))
                .build(), HttpResponse.BodyHandlers.ofString());
    }
}
