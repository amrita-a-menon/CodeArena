package com.codearena.core;

import com.codearena.models.Challenge;
import com.codearena.models.ErrorContext;
import com.codearena.ai.AIAdapter;
import com.codearena.ai.AIAdapterFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

public class ChallengeGenerator {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Challenge generate(ErrorContext context) {
        try {
            AIAdapter aiAdapter = AIAdapterFactory.createAdapter();
            System.out.println("CodeArena: Using AI Adapter: " + aiAdapter.getName());

            String prompt = buildChallengePrompt(context);
            String aiResponse = aiAdapter.generate(prompt);
            System.out.println("CodeArena: AI Response obtained. Parsing...");

            Challenge challenge = parseAIResponse(aiResponse, context);
            if (challenge != null && challenge.getQuestion().contains("(Mock Challenge)")) {
                System.out.println("CodeArena: Detected Mock Challenge even after AI generation attempt.");
                System.out.println("CodeArena: AI response snippet: " + (aiResponse.length() > 100 ? aiResponse.substring(0, 100) : aiResponse));
            }
            return challenge;
        } catch (Exception e) {
            System.err.println("CodeArena: Error in ChallengeGenerator.generate: " + e.getMessage());
            e.printStackTrace();
            return getFallbackChallenge(context);
        }
    }

    private static String buildChallengePrompt(ErrorContext context) {
        return String.format("""
            Generate a unique debugging challenge based STRICTLY on the following context:
            
            1. ERROR MESSAGE: %s
            2. ERROR TYPE: %s
            3. CODE SNIPPET (The code where the error happened):
            ---
            %s
            ---
            4. USER INTENT: %s
            5. SKILL LEVEL: %s
            
            IMPORTANT INSTRUCTIONS:
            - Do NOT use generic examples or common interview questions.
            - Focus specifically on the provided CODE SNIPPET and how it relates to the ERROR.
            - If the code snippet is provided, use the actual variable names and logic from it in the question and options.
            - Generate exactly 4 multiple-choice options.
            - Include one correct answer and 3 plausible distractors.
            - Format as JSON with: question, options[], correctIndex, explanation, hints[], xpValue
            """,
                context.getErrorMessage(),
                context.getErrorType(),
                context.getCodeSnippet(),
                context.getUserIntent(),
                context.getUserSkillLevel()
        );
    }

    private static Challenge parseAIResponse(String aiResponse, ErrorContext context) {
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
            return getFallbackChallenge(context);
        }
    }

    private static Challenge getFallbackChallenge(ErrorContext context) {
        String errorType = context != null ? context.getErrorType() : "Error";
        return new Challenge(
                "An error occurred: " + errorType + ". How should you start debugging this?",
                Arrays.asList(
                        "Read the stack trace and find the failing line",
                        "Restart the computer",
                        "Change variables randomly until it works",
                        "Delete the project"
                ),
                0,
                "The first step in debugging is identifying the cause and location of the error in the source code.",
                Arrays.asList("Look at the console output", "Check the line number in the stack trace"),
                15
        );
    }
}