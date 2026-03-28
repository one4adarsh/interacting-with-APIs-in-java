package org.example.requests;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.example.util.Constants.BASE_URL;

public class GetFile {

    // This method works well for small files but can strain memory for larger files.
    public static void requestBasic() {
        String noteName = "welcome.txt";

        // Create HttpClient
        HttpClient client = HttpClient.newHttpClient();

        // Build GET Request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/notes/" + noteName))
                .build();

        try {
            // Send Request and capture response
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            // Check if request was successful
            if (response.statusCode() == 200) {
                // Save file locally
                Path filePath = Paths.get("downloaded_" + noteName);
                Files.write(filePath, response.body());
            } else {
                System.out.println("HTTP error occurred: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    // For larger files, Java's HttpClient allows handling the response as a stream using InputStream, which optimizes memory usage.
    public static void requestStream() {
        String baseUrl = "http://localhost:8000";
        String noteName = "welcome.txt";

        // Create HttpClient
        HttpClient client = HttpClient.newHttpClient();

        // Build GET Request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/notes/" + noteName))
                .build();

        try {
            // Send request and print response
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            // Handle response for successful download
            if (response.statusCode() == 200) {
                Path path = Paths.get("downloaded_" + noteName);
                try (InputStream inputStream = response.body();
                     FileOutputStream fileOutputStream = new FileOutputStream(path.toFile())) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }
                }
            } else {
                System.out.println("HTTP error occurred: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    public static void verifyFileContents(String[] args) {
        String noteName = "downloaded_welcome.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(noteName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("File error occurred: " + e.getMessage());
        }
    }
}
