package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;
import java.util.List;
import java.util.Map;

public class MessageSummarizer {
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public String summarize(List<String> messages) {
        try {
            String payload = mapper.writeValueAsString(Map.of("messages", messages));
            OllamaApi ollamaApi = new OllamaApi();
            return String.valueOf(ollamaApi.fetchData( payload ));

        } catch (Exception e) {
            return "Summarization error";
        }
    }
}