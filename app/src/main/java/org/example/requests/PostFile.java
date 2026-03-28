package org.example.requests;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.example.util.Constants.BASE_URL;

public class PostFile {
    public static void request() {
        String fileName = "meeting_notes.txt";
        Path filePath = Paths.get(fileName);

        HttpClient client = HttpClient.newHttpClient();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/notes"))
                    .header("Content-Type", "multipart/form-data;boundary=boundary")
                    .POST(ofMimeMultipartData(filePath))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                System.out.println("File uploaded successfully: " + fileName);
            } else {
                System.out.println("Failed to upload file: " + response.statusCode());
                System.out.println("Error: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error occurred: " + e.getMessage());
        }
    }

    private static HttpRequest.BodyPublisher ofMimeMultipartData(Path filePath) throws IOException {
        var byteArrays = List.of(
                "--boundary\r\n".getBytes(),
                "Content-Disposition: form-data; name=\"file\"; filename=\"".getBytes(),
                filePath.getFileName().toString().getBytes(),
                "\"\r\nContent-Type: text/plain\r\n\r\n".getBytes(),
                Files.readAllBytes(filePath),
                "\r\n--boundary--\r\n".getBytes());

        return HttpRequest.BodyPublishers.ofByteArrays(byteArrays);
    }
}
