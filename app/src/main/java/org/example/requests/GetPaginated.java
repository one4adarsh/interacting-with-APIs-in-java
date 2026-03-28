package org.example.requests;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.entity.Todo;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.example.util.Constants.BASE_URL;

public class GetPaginated {
    private static final HttpClient client = HttpClient.newHttpClient();

    public static void request() {
        int page = 1;  // Start from the first page
        Gson gson = new Gson();
        Type todoListType = new TypeToken<List<Todo>>() {
        }.getType();

        while (true) {
            try {
                // Construct the request URI with query parameters
                URI uri = new URI(BASE_URL + "/todos?page=" + page + "&limit=3");
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(uri)
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    System.out.println("HTTP error on page " + page + ": " + response.statusCode());
                    break;
                }

                // Parse response using Gson
                List<Todo> pageTodos = gson.fromJson(response.body(), todoListType);

                if (pageTodos.isEmpty()) {  // Exit loop if no more data
                    break;
                }

                System.out.println("Page " + page + " fetched successfully!");
                for (Todo todo : pageTodos) {
                    System.out.printf("- Title: %s:, Description: %s, Done: %s\n",
                            todo.title,
                            todo.description,
                            todo.done);
                }

                page++;  // Advance to the next page

            } catch (Exception e) {
                System.out.println("Error on page " + page + ": " + e.getMessage());
                break;
            }
        }
    }
}
