package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class OllamaApi {

    private final String baseUrl = "http://127.0.0.1:11434/api/generate";
    private final String model = "gemma3:4b" ;
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    public String fetchData(String text) {
        String prompt = """
                 Your task is to extract the **main subject** and summary the following conversion:
                                     """;
        String promptRules = """  
                 your rules:
                 your default language = Hebrew.
                 if motioned english write in english else keep sending output only in Hebrew.
                 1. **Summary Style**:
                      - 1-2 short sentences max. Only expand if absolutely necessary.
                      - Focus on the **overall topic**, not individual comments.
                      - Ignore small talk, greetings, or repetitive points.

                   2. **Key Details to Include**:
                      - Decisions made (e.g., "The group agreed to meet Saturday").
                      - Critical info (e.g., "Alex shared the event link").
                      - Conflicts or key disagreements.

                   3. **Avoid**:
                     - Play-by-play of who said what.
                     - Quotes unless absolutely crucial.
                     - Bullet points, markdown, or robotic phrasing.
                   
                    4.**Do**:
                     - mention usernames.
                     - notice "no" and mention it.
                """;
        prompt = prompt.concat(text.concat(promptRules));
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
                String text = jsonObject.get("response").getAsString();
                return text;
            } else {
                return jsonResponse;
            }
        } catch (Exception e) {
            System.err.println("Error parsing JSON response: " + e.getMessage());
            return null;
        }
    }

}