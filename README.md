# interacting-with-APIs-in-java

### Sync vs Async vs Reactive HTTP Clients

| Aspect       | Synchronous                              | Asynchronous                                            | Reactive                                          |
|--------------|------------------------------------------|---------------------------------------------------------|---------------------------------------------------|
| Blocking     | Blocks the thread until response arrives | Non-blocking, uses callbacks, returns CompletableFuture | Non-blocking, event-loop based, returns Mono/Flux |
| Thread usage | 1 thread per request                     | Thread released while waiting                           | Minimal threads via event loop                    |
| Java API     | client.send()                            | client.sendAsync()                                      | WebClient (Spring WebFlux)                        |
| Best for     | Simple scripts, small apps               | Moderate concurrency                                    | High-throughput, streaming                        |

## API Client - Synchronous

To make HTTP requests in Java, we leverage the `HttpClient`, which is part of the core `java.net.http` package. For JSON
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

#### GET Request structure

```java
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import org.example.entity.Todo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.io.IOException;
import java.util.List;

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
                Type todoListType = new TypeToken<List<Todo>>() {
                }.getType();
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

#### POST Request structure

```java
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import org.example.entity.Todo;

import java.net.URI;
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
                // Parse JSON response into Todo POJO
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

#### Notes:

- To be able to deserialize JSON data into a Java object, Gson needs to know the expected Type of that object. For
  non-generic types, such as a Todo a simple `Todo.class` will suffice. For generic types, such as List<>, a `TypeToken`
  is required.
- The `JsonParser.parseString(...).getAsJsonObject()` parses the response JSON string into a JsonObject which can be
  useful for accessing error data in a more structured manner.

### Jackson Library

```
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.18.2</version>
</dependency>
```

or

```
implementation("com.fasterxml.jackson.core:jackson-databind:2.18.2")
```

#### GET Request structure using Jackson

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import org.example.entity.Todo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.io.IOException;
import java.util.List;

public class GetApiInteractionJackson {
    public static void main(String[] args) {
        String apiUrl = "https://api.example.com/data";

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

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
                List<Todo> todos = mapper.readValue(response.body(), new TypeReference<List<Todo>>() {
                });

                System.out.println("Todos retrieved successfully:");
            } else {
                System.out.printf("\nUnexpected Status Code: %d%n", response.statusCode());
                var error = mapper.readTree(response.body());
                System.out.println("Error Details: " + error);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
```

#### POST Request structure using Jackson

```java
import com.fasterxml.jackson.databind.ObjectMapper;

import org.example.entity.Todo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.io.IOException;

public class PostApiInteractionJackson {
    public static void main(String[] args) {
        String apiUrl = "https://api.example.com/data";

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        try {
            Todo newTodo = new Todo("Learn Java HttpClient", "Complete a course on Java API calls.", false);
            String requestBody = mapper.writeValueAsString(newTodo);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Response Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());

            if (response.statusCode() == 201) {
                // Parse JSON response into Todo POJO
                Todo todo = mapper.readValue(response.body(), Todo.class);

                System.out.println("Todo created successfully:");
            } else {
                System.out.printf("\nUnexpected Status Code: %d%n", response.statusCode());
                var error = mapper.readTree(response.body());
                System.out.println("Error Details: " + error);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
```

#### Notes:

- Jackson uses `ObjectMapper` as the main entry point instead of Gson's `Gson` class.
- `mapper.readValue()` replaces `gson.fromJson()` for deserialization.
- `mapper.writeValueAsString()` replaces `gson.toJson()` for serialization.
- For generic types like `List<Todo>`, Jackson uses `TypeReference` instead of Gson's `TypeToken`.
- `mapper.readTree()` replaces `JsonParser.parseString().getAsJsonObject()` for parsing raw JSON into a tree structure (
  JsonNode).
- Jackson requires POJOs to have a no-arg constructor and either public fields or getters/setters (or use
  `@JsonProperty` annotations).

## API Client - Asynchronous

The `HttpClient` in Java supports async requests via `sendAsync()`, which returns a `CompletableFuture` instead of
blocking the thread.

### Jackson Library

#### GET Request structure using Jackson

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import org.example.entity.Todo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GetApiInteractionAsync {
    public static void main(String[] args) {
        String apiUrl = "https://api.example.com/data";

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        CompletableFuture<List<Todo>> future = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    System.out.println("Response Status Code: " + response.statusCode());
                    System.out.println("Response Body: " + response.body());

                    try {
                        if (response.statusCode() == 200) {
                            List<Todo> todos = mapper.readValue(response.body(), new TypeReference<List<Todo>>() {
                            });
                            System.out.println("Todos retrieved successfully:");
                            return todos;
                        } else {
                            System.out.printf("\nUnexpected Status Code: %d%n", response.statusCode());
                            var error = mapper.readTree(response.body());
                            System.out.println("Error Details: " + error);
                            return null;
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    throw new RuntimeException(ex);
                });

        List<Todo> todos = future.join();
    }
}
```

#### POST Request structure using Jackson

```java
import com.fasterxml.jackson.databind.ObjectMapper;

import org.example.entity.Todo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.concurrent.CompletableFuture;

public class PostApiInteractionAsync {
    public static void main(String[] args) throws Exception {
        String apiUrl = "https://api.example.com/data";

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        Todo newTodo = new Todo("Learn Java HttpClient", "Complete a course on Java API calls.", false);
        String requestBody = mapper.writeValueAsString(newTodo);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        CompletableFuture<Todo> future = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    System.out.println("Response Status Code: " + response.statusCode());
                    System.out.println("Response Body: " + response.body());

                    try {
                        if (response.statusCode() == 201) {
                            Todo todo = mapper.readValue(response.body(), Todo.class);
                            System.out.println("Todo created successfully:");
                            return todo;
                        } else {
                            System.out.printf("\nUnexpected Status Code: %d%n", response.statusCode());
                            var error = mapper.readTree(response.body());
                            System.out.println("Error Details: " + error);
                            return null;
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    throw new RuntimeException(ex);
                });

        Todo todo = future.join();
    }
}
```

#### Multiple Requests in Parallel

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entity.Todo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ConcurrentApiInteraction {
    public static void main(String[] args) {
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        // 2 POST calls
        List<CompletableFuture<Todo>> postFutures = List.of(
                        "https://api.example.com/data",
                        "https://api.example.com/data"
                ).stream()
                .map(url -> {
                    try {
                        String body = mapper.writeValueAsString(new Todo("Task", "Description", false));
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(url))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(body))
                                .build();
                        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                                .thenApply(response -> {
                                    System.out.println("Response Status Code: " + response.statusCode());
                                    System.out.println("Response Body: " + response.body());

                                    try {
                                        return mapper.readValue(response.body(), Todo.class);
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        // Wait for all POSTs to complete
        CompletableFuture.allOf(postFutures.toArray(new CompletableFuture[0])).join();

        // 2 GET calls after POST completion
        List<CompletableFuture<Todo>> getFutures = postFutures.stream()
                .map(CompletableFuture::join)
                .map(todo -> "https://api.example.com/data/" + todo.getId() + "/title")
                .map(url -> HttpRequest.newBuilder().uri(URI.create(url)).GET().build())
                .map(request -> client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenApply(response -> {
                            System.out.println("Response Status Code: " + response.statusCode());
                            System.out.println("Response Body: " + response.body());

                            try {
                                return mapper.readValue(response.body(), Todo.class);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }))
                .toList();

        CompletableFuture.allOf(getFutures.toArray(new CompletableFuture[0])).join();

        List<Todo> todos = getFutures.stream()
                .map(CompletableFuture::join)
                .toList();
    }
}
```

#### Notes:

- `sendAsync()` returns a `CompletableFuture<HttpResponse<String>>`, which allows non-blocking chaining of response
  handling.
- `future.join()` blocks the calling thread until the future completes — useful in `main` to prevent the program from
  exiting early.
- Use `CompletableFuture.allOf(...)` to wait for multiple async requests to complete in parallel.
- For fire-and-forget calls, use `thenAccept` instead of `thenApply`.

These are `CompletableFuture` chaining methods:

| Method                     | Input             | Returns                | Purpose                                        |
|----------------------------|-------------------|------------------------|------------------------------------------------|
| `thenApply(Function)`      | value             | transformed value      | Transform result (like `map`)                  |
| `thenAccept(Consumer)`     | value             | `Void`                 | Consume result, return nothing                 |
| `thenRun(Runnable)`        | nothing           | `Void`                 | Run action, ignores result                     |
| `thenCompose(Function)`    | value             | `CompletableFuture<U>` | Chain another async operation (like `flatMap`) |
| `exceptionally(Function)`  | exception         | fallback value         | Handle errors                                  |
| `handle(BiFunction)`       | value + exception | transformed value      | Handle both success and error                  |
| `whenComplete(BiConsumer)` | value + exception | `Void`                 | Side-effect on completion, doesn't transform   |

Each also has an `*Async` variant (e.g., `thenApplyAsync`) that runs the callback on a different thread instead of the
completing thread.

## API Client - Reactive

For reactive HTTP clients, need external libraries like Spring WebClient (from Spring WebFlux) or libraries built on
Project Reactor or RxJava.

### RxJava

RxJava is a Java implementation of ReactiveX (Reactive Extensions) — a library for composing asynchronous and
event-based programs using observable sequences.

```
<dependency>
    <groupId>io.reactivex.rxjava3</groupId>
    <artifactId>rxjava</artifactId>
    <version>3.1.9</version>
</dependency>
```

or

```
implementation("io.reactivex.rxjava3:rxjava:3.1.9")
```

#### Notes:

- It provides types like `Observable`, `Flowable`, `Single`, `Maybe`, and `Completable` to represent streams of data,
  and operators (`map`, `filter`, `flatMap`, `zip`, etc.) to transform and combine them.

| RxJava Type   | Emits                  | Backpressure |
|---------------|------------------------|--------------|
| `Observable`  | 0..N items             | No           |
| `Flowable`    | 0..N items             | Yes          |
| `Single`      | Exactly 1 item         | N/A          |
| `Maybe`       | 0 or 1 item            | N/A          |
| `Completable` | No items (just signal) | N/A          |

It's an alternative to Project Reactor (`Mono`/`Flux`) used by Spring WebFlux. Both implement the Reactive Streams
specification, but RxJava can be used independently of Spring.

## Request Types

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