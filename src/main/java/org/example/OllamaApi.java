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
    private final String model = "gemma3:12b";
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    public String fetchData(String text) {
//        String prompt = """
//Summarize this chat strictly following these rules:
//
//1. **Real-World Constraints**
//   - If someone types limited messages (e.g., "Can’t type much"), note it briefly (e.g., "[Name] couldn’t elaborate").
//
//2. **Offline Context**
//   - Only note offline actions if mentioned (e.g., "[Name] told me offline…").
//   - If someone says "pass/give me [thing]," assume they’re together but don’t invent details.
//
//3. **Urgency**
//   - Highlight urgency if clear (e.g., "HURRY!"). Ignore if vague.
//
//4. **Hierarchies**
//   - Keep instructions neutral (e.g., "[Name] instructed [action]").
//
//5. **Slang/Cultural Terms**
//   - Keep slang verbatim (e.g., "Let’s grab chaat").
//
//6. **Tentative Plans**
//   - Label vague plans (e.g., "[Name] suggested [action] (tentative)").
//
//7. **Sarcasm/Jokes**
//   - Only note if obvious (e.g., "/s" or "Yeah, right…").
//
//8. **Time Sensitivity**
//   - Note deadlines (e.g., "Before 5 PM"), ignore if unclear.
//
//9. **Emoji Reactions**
//    - 👍 = agreed, ❌ = declined, else ignore.
//
//10. **Names**
//    - Use actual names (no P1/P2). Ensure no mix-ups in responses.
//
//11. **Time Breaks**
//    - If >15min gap + new topic, treat as a new conversation.
//
//12. **Addresses**
//    - Relate addresses to context (e.g., "[Name] shared their location for pickup").
//
//13. **Links/Files**
//    - Note shared links/files and infer context from replies (e.g., "[Name] sent a link to a presentation").
//
//**Format:**
//- do not change versions for the same context
//- Short, plain sentences. No bullet points or markdown. stick to details.
//- Only provide the final summary.
//
//Now, summarize this chat in hebrew:
//""";

/*        String prompt = """
               You are summerAIzerBot an ai summarization bot for telegram your job is to summary group chat and follow the flowing instructions
                            Use short, natural sentences.
                            Do not write something the users did not write.
                            Use the original senders names when referencing something they said.
                            No markdown, bullet points, or robotic phrases.
                            Only give the final summarization
                             summarize this in the original chat language chat :

                               """;*/

        String prompt = """
                You are summerAIzerBot, an AI summarization bot for Telegram. Your task is to summarize the following group chat using short, natural sentences, just as people speak.
                 You must only include information that was directly stated by the users. When you refer to something a user said, be sure to use their original name.
                Do not use any asterisks or other special formatting, markdown, bullet points, or robotic phrases. Provide only the final summarization in the original chat language.
                    Here is the group chat:
                                """;

        prompt = prompt.concat(text);
        JsonObject requestData = new JsonObject();
        requestData.addProperty("model", model);
        requestData.addProperty("prompt", prompt);
        requestData.addProperty("stream", false);
        System.out.println(prompt);

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
//                String[] a = text.split("</think>");
//                System.out.println(a[1]);
//                return a[1];
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