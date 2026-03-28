package org.example.requests;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.example.entity.Todo;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.example.util.Constants.BASE_URL;

public class Get {
    public static void request() {
        String endpoint = "/todos";

        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Response Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());

            if (response.statusCode() == 200) {
                // Type token is only used for generic types, such as Lists
                Type todoListType = new TypeToken<List<Todo>>() {
                }.getType();
                List<Todo> todos = gson.fromJson(response.body(), todoListType);

                System.out.println("Todos retrieved successfully:");
                for (Todo todo : todos) {
                    System.out.println("Title: " + todo.title);
                    System.out.println("Description: " + todo.description);
                    System.out.println("Done: " + todo.done);
                }
            } else if (response.statusCode() == 400) {
                System.out.println("\nBad Request. The server could not understand the request due to invalid syntax.");
                var error = JsonParser.parseString(response.body()).getAsJsonObject();
                System.out.println("Error Details: " + error);
            } else if (response.statusCode() == 401) {
                System.out.println("\nUnauthorized. Access is denied due to invalid credentials.");
                var error = JsonParser.parseString(response.body()).getAsJsonObject();
                System.out.println("Error Details: " + error);
            } else if (response.statusCode() == 404) {
                System.out.println("\nNot Found. The requested resource could not be found on the server.");
                var error = JsonParser.parseString(response.body()).getAsJsonObject();
                System.out.println("Error Details: " + error);
            } else if (response.statusCode() == 500) {
                System.out.println("\nInternal Server Error. The server has encountered a situation it doesn't know how to handle.");
                var error = JsonParser.parseString(response.body()).getAsJsonObject();
                System.out.println("Error Details: " + error);
            } else {
                System.out.printf("\nUnexpected Status Code: %d%n", response.statusCode());
                var error = JsonParser.parseString(response.body()).getAsJsonObject();
                System.out.println("Error Details: " + error);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void requestWithPathParams() {
        String endpoint = "/todos";
        int todoId = 3;

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint + "/" + todoId))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Raw Response:");
            System.out.println(response.body());

            if (response.statusCode() == 200) {
                Gson gson = new Gson();

                // Type token is only used for generic types, such as Lists
                Type todoListType = new TypeToken<List<Todo>>() {
                }.getType();
                List<Todo> todos = gson.fromJson(response.body(), todoListType);

                System.out.println("Todos retrieved successfully:");
                for (Todo todo : todos) {
                    System.out.println("Title: " + todo.title);
                    System.out.println("Description: " + todo.description);
                    System.out.println("Done: " + todo.done);
                }
            } else {
                System.out.printf("\nUnexpected Status Code: %d%n", response.statusCode());
                var error = JsonParser.parseString(response.body()).getAsJsonObject();
                System.out.println("Error Details: " + error);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void requestWithQueryParam() {
        String endpoint = "/todos";
        String params = "done=true&title=c";

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint + "?" + params))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Raw Response:");
            System.out.println(response.body());

            if (response.statusCode() == 200) {
                Gson gson = new Gson();

                // Type token is only used for generic types, such as Lists
                Type todoListType = new TypeToken<List<Todo>>() {
                }.getType();
                List<Todo> todos = gson.fromJson(response.body(), todoListType);

                System.out.println("Todos retrieved successfully:");
                for (Todo todo : todos) {
                    System.out.println("Title: " + todo.title);
                    System.out.println("Description: " + todo.description);
                    System.out.println("Done: " + todo.done);
                }
            } else {
                System.out.printf("\nUnexpected Status Code: %d%n", response.statusCode());
                var error = JsonParser.parseString(response.body()).getAsJsonObject();
                System.out.println("Error Details: " + error);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
