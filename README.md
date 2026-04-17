# interacting-with-APIs-in-java

To make HTTP requests in Java, we leverage the HttpClient, which is part of the core `java.net.http` package. For JSON
parsing, we use the Gson library.

### Gson Library

```
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.12.1</version>
</dependency>
```

or

```
implementation("com.google.code.gson:gson:2.12.1")
```

### GET Request structure

```java
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import org.example.entity.Todo;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.io.IOException;

public class GetApiInteraction {
    public static void main(String[] args) {
        String apiUrl = "https://api.example.com/data";

        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl)) 
                .GET()
                .build();

        // path param = apiUrl + "/" + todoId
        // query param = apiUrl + "?" + done=true
        // paginated - run this inside `while(true) { ... page++; }` and `break` when page ends 

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Response Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());

            if (response.statusCode() == 200) {
                // Parse JSON response into List<Todo> POJO
                Type todoListType = new TypeToken<List<Todo>>() {}.getType();
                List<Todo> todos = gson.fromJson(response.body(), todoListType);

                System.out.println("Todos retrieved successfully:");
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
```

### POST Request structure

```java
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import org.example.entity.Todo;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.io.IOException;

public class PostApiInteraction {
    public static void main(String[] args) {
        String apiUrl = "https://api.example.com/data";

        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();

        Todo newTodo = new Todo("Learn Java HttpClient", "Complete a course on Java API calls.", false);
        String requestBody = gson.toJson(newTodo);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Response Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());

            if (response.statusCode() == 201) {
                String responseBody = response.body();

                // Parse JSON response into Todo POJO
                Gson gson = new Gson();
                Todo todo = gson.fromJson(response.body(), Todo.class);

                System.out.println("Todo created successfully:");
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
```

### Notes:

- To be able to deserialize JSON data into a Java object, Gson needs to know the expected Type of that object. For
  non-generic types, such as a Todo a simple `Todo.class` will suffice. For generic types, such as List<>, a `TypeToken`
  is required.
- The `JsonParser.parseString(...).getAsJsonObject()` parses the response JSON string into a JsonObject which can be
  useful for accessing error data in a more structured manner.

### Request Types
- Simple CRUD Operations
- File Download
- File Upload
- Paginated Response
- Authentication
  - Basic Authentication
  - API Keys
  - Sessions
  - JWT Bearer Tokens
  - OAuth