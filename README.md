# interacting-with-APIs-in-java

To make HTTP requests in Java, we leverage the HttpClient, which is part of the core java.net.http package. For JSON
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

### Request structure

```java

public class ApiInteraction {
    public static void main(String[] args) {
        String apiUrl = "https://api.example.com/data";

        Todo newTodo = new Todo("Learn Java HttpClient", "Complete a course on Java API calls.", false);
        String requestBody = gson.toJson(newTodo);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                String responseBody = response.body();

                // Parse JSON response using Gson
                Gson gson = new Gson();
                Todo todo = gson.fromJson(response.body(), Todo.class);

//                Type todoListType = new TypeToken<List<Todo>>() {}.getType();
//                List<Todo> todos = gson.fromJson(response.body(), todoListType);
            } else {
                System.out.printf("\nUnexpected Status Code: %d%n", response.statusCode());
                var error = JsonParser.parseString(response.body()).getAsJsonObject();
                System.out.println("Error Details: " + error);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

Notes:

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