package org.example.requests;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.example.entity.Todo;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.example.util.Constants.BASE_URL;

public class Put {
    public static void request() {
        String endpoint = "/todos";
        int todoId = 1;

        Gson gson = new Gson();
        Todo updatedTodo = new Todo("Buy groceries and snacks", "Milk, eggs, bread, coffee, and chips.", true);
        String requestBody = gson.toJson(updatedTodo);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint + "/" + todoId))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Response Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());

            if (response.statusCode() == 200) {
                Todo replacedTodo = gson.fromJson(response.body(), Todo.class);

                System.out.println("Todo replaced successfully:");
                System.out.println("Title: " + replacedTodo.title);
                System.out.println("Description: " + replacedTodo.description);
                System.out.println("Done: " + replacedTodo.done);
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
}
