package com.codearena.ai;

import com.codearena.persistence.SettingsManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class OpenAIAdapter implements AIAdapter {
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String generate(String prompt) {
        String apiKey = SettingsManager.getOpenAIKey();
        if (apiKey == null || apiKey.isEmpty()) {
            return new MockAIAdapter().generate(prompt);
        }

        try {
            ObjectNode rootNode = objectMapper.createObjectNode();
            rootNode.put("model", "gpt-3.5-turbo");
            
            ArrayNode messages = rootNode.putArray("messages");
            ObjectNode systemMessage = messages.addObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", "You are a helpful coding assistant that generates debugging challenges in JSON format.");
            
            ObjectNode userMessage = messages.addObject();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);

            String requestBody = objectMapper.writeValueAsString(rootNode);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                var responseJson = objectMapper.readTree(response.body());
                return responseJson.get("choices").get(0).get("message").get("content").asText();
            } else {
                System.err.println("OpenAI API error: " + response.statusCode() + " - " + response.body());
                return new MockAIAdapter().generate(prompt);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new MockAIAdapter().generate(prompt);
        }
    }

    @Override
    public boolean isAvailable() {
        String apiKey = SettingsManager.getOpenAIKey();
        return apiKey != null && !apiKey.isEmpty();
    }

    @Override
    public String getName() {
        return "OpenAI";
    }
}