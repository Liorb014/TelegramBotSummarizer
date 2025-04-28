package org.example;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class LlamaApi {

    private final String baseUrl = "http://127.0.0.1:11434/api/generate";
    private final String model = "llama3.2";
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    public String fetchData2(String text) throws ExecutionException, InterruptedException, IOException {
        String prompt = "summary the flowing conversation in as few word as you can be accurate and dont deviate from the topic, use the names of the user :";
        prompt = prompt.concat(text);
        JsonObject requestData = new JsonObject();
        requestData.addProperty("model", model);
        requestData.addProperty("prompt", prompt);
        requestData.addProperty("stream", false);

        String requestBody = gson.toJson(requestData);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        CompletableFuture<String> a = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::extractResponse);
        return a.join();
    }

    private String extractResponse(String jsonResponse) {
        try {
            JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
            if (jsonObject.has("response")) {
                System.out.println(jsonObject.get("response").getAsString());
                return jsonObject.get("response").getAsString();
            } else {
                return jsonResponse; // Return the whole JSON if "response" field is missing
            }
        } catch (Exception e) {
            System.err.println("Error parsing JSON response: " + e.getMessage());
            return null;
        }
    }

}