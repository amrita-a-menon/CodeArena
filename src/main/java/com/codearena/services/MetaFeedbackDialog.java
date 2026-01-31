package com.codearena.services;

import com.codearena.ai.AIAdapter;
import com.codearena.models.UserProfile;
import com.codearena.persistence.ChallengeHistory;

import java.util.List;
import java.util.Map;

public class MetaFeedbackService {
    public static String generateMetaFeedback(Project project) {
        UserProfile profile = UserProgress.loadProfile(project);
        List<ChallengeHistory> history = ChallengeHistory.getRecentChallenges(project, 20);

        // Analyze patterns
        Map<String, Integer> errorPatterns = analyzeErrorPatterns(history);
        Map<String, Integer> solutionPatterns = analyzeSolutionPatterns(history);

        String prompt = buildMetaFeedbackPrompt(profile, errorPatterns, solutionPatterns);

        return AIAdapter.generate(prompt);
    }

    private static String buildMetaFeedbackPrompt(UserProfile profile,
                                                  Map<String, Integer> errors,
                                                  Map<String, Integer> solutions) {
        return String.format("""
            As a coding coach, provide personalized feedback:
            
            User Stats:
            - Rank: %s
            - Total XP: %d
            - Accuracy: %.1f%%
            - Common Error Types: %s
            
            Learning Patterns:
            - Strengths: %s
            - Areas for Improvement: %s
            
            Give 2-3 specific, actionable recommendations.
            Focus on debugging mindset, not just syntax.
            Keep it encouraging but honest.
            Format as 3 bullet points max.
            """,
                profile.getRank(),
                profile.getTotalXp(),
                profile.getAccuracy(),
                errors.toString(),
                extractStrengths(solutions),
                extractWeaknesses(errors)
        );
    }
}