package com.codearena.core;

import com.codearena.models.Challenge;
import com.codearena.models.ErrorContext;
import com.codearena.ai.AIAdapter;
import com.codearena.ai.AIAdapterFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

public class ChallengeGenerator {
    private static final AIAdapter aiAdapter = AIAdapterFactory.createAdapter();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Challenge generate(ErrorContext context) {
        String prompt = buildChallengePrompt(context);
        String aiResponse = aiAdapter.generate(prompt);

        return parseAIResponse(aiResponse);
    }

    private static String buildChallengePrompt(ErrorContext context) {
        return String.format("""
            Generate a debugging challenge based on:
            Error: %s
            Error Type: %s
            Code Snippet: %s
            User Intent: %s
            Skill Level: %s
            
            Generate exactly 4 multiple-choice options.
            Include one correct answer and 3 plausible distractors.
            Focus on teaching debugging thinking, not syntax.
            Format as JSON with: question, options[], correctIndex, explanation, hints[], xpValue
            """,
                context.getErrorMessage(),
                context.getErrorType(),
                context.getCodeSnippet(),
                context.getUserIntent(),
                context.getUserSkillLevel()
        );
    }

    private static Challenge parseAIResponse(String aiResponse) {
        try {
            // Parse the JSON response
            var jsonNode = objectMapper.readTree(aiResponse);

            String question = jsonNode.get("question").asText();

            List<String> options = Arrays.asList(
                    jsonNode.get("options").get(0).asText(),
                    jsonNode.get("options").get(1).asText(),
                    jsonNode.get("options").get(2).asText(),
                    jsonNode.get("options").get(3).asText()
            );

            int correctIndex = jsonNode.get("correctIndex").asInt();
            String explanation = jsonNode.get("explanation").asText();

            List<String> hints = Arrays.asList(
                    jsonNode.get("hints").get(0).asText(),
                    jsonNode.get("hints").get(1).asText()
            );

            int xpValue = jsonNode.get("xpValue").asInt();

            return new Challenge(question, options, correctIndex, explanation, hints, xpValue);
        } catch (Exception e) {
            // Fallback to a default challenge
            return getFallbackChallenge();
        }
    }

    private static Challenge getFallbackChallenge() {
        return new Challenge(
                "What causes a NullPointerException?",
                Arrays.asList(
                        "Accessing an uninitialized object",
                        "Using the wrong loop type",
                        "Missing import statements",
                        "Incorrect method parameters"
                ),
                0,
                "NullPointerException occurs when you try to use an object reference that has not been initialized.",
                Arrays.asList("Check object initialization", "Look for 'new' keyword"),
                15
        );
    }
}