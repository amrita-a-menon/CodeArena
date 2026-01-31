package com.codearena.core;

import com.codearena.ai.AIAdapter;
import com.codearena.models.Challenge;
import com.codearena.models.ErrorContext;

public class ChallengeGenerator {
    private static final AIAdapter aiAdapter = new OpenAIAdapter();

    public static Challenge generate(ErrorContext context) {
        String prompt = buildChallengePrompt(context);
        String aiResponse = aiAdapter.generate(prompt);

        return parseAIResponse(aiResponse, context);
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
            Format as JSON with: question, options[], correctIndex, explanation, hints[]
            """,
                context.getErrorMessage(),
                context.getErrorType(),
                context.getCodeSnippet(),
                context.getUserIntent(),
                context.getUserSkillLevel()
        );
    }
}