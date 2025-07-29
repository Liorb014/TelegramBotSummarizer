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

Rules:

Output in Hebrew unless the input is in English, then use English.
Keep all names exactly as they appear in the input, without changes.
Use correct Hebrew grammar, including gender-specific verbs, for Hebrew output.

1. **Summary Style**:
- Write short, clear sentences.
- Summarize the main topic, ignoring greetings or repetitive messages.
- Always link actions or statements to the correct username.
- Check each message’s username before summarizing to avoid mixing up names.

2. **Include**:
- Decisions (e.g., "Group agreed to meet Saturday").
- Key information (e.g., "Alex shared a link").
- Disagreements, naming users (e.g., "Dana disagreed with Yogev").
- Explicit refusals (e.g., "Yogev said no").

3. **Avoid**:
- Detailed conversation recaps.
- Quotes unless critical.
- Mixing up usernames or their statements.
- Incorrect Hebrew grammar.

4. **Do**:
- Mention usernames for every action or statement.
- If a name is unclear, summarize without naming (e.g., "Someone suggested a new plan").
- For similar names (e.g., Alex1, Alex2), double-check the message’s username.

5. **Example**:
Input:
Alex: Let’s meet Saturday.
Dana: No, I prefer Sunday.
Yogev: I shared the link.
Output (Hebrew):
הקבוצה דנה בזמן פגישה. Alex הציע שבת, אבל Dana העדיפה יום ראשון. Yogev שיתף קישור.

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