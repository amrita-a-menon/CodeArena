package com.codearena.core;

import com.codearena.models.Challenge;
import com.codearena.models.UserProfile;
import com.codearena.persistence.UserProgress;
import com.intellij.openapi.project.Project;

public class ScoringSystem {
    private static final int BASE_CORRECT_XP = 15;
    private static final int BASE_WRONG_XP = -5;
    private static final int STREAK_BONUS = 10;

    public static void processAnswer(Project project,
                                     Challenge challenge,
                                     boolean correct,
                                     boolean usedHint) {
        UserProfile profile = UserProgress.loadProfile(project);

        profile.incrementChallengesCompleted();

        if (correct) {
            profile.incrementChallengesCorrect();
            int xpGained = challenge.getXpValue() > 0 ? challenge.getXpValue() : BASE_CORRECT_XP;

            // Streak bonus
            if (profile.getCurrentStreak() >= 3) {
                xpGained += STREAK_BONUS;
            }

            // No-hint multiplier
            if (!usedHint) {
                xpGained = (int)(xpGained * 1.5);
            }

            profile.addXp(xpGained);
            profile.incrementStreak();

            // Check level up
            checkLevelUp(project, profile);
        } else {
            profile.addXp(BASE_WRONG_XP);
            profile.resetStreak();
        }

        UserProgress.saveProfile(project, profile);
    }

    private static void checkLevelUp(Project project, UserProfile profile) {
        int[] xpThresholds = {0, 100, 300, 600, 1000, 1500};
        String[] ranks = {
                "Syntax Survivor",
                "Bug Hunter",
                "Debug Strategist",
                "Runtime Whisperer",
                "Compiler Overlord"
        };

        for (int i = xpThresholds.length - 1; i >= 0; i--) {
            if (profile.getTotalXp() >= xpThresholds[i]) {
                if (!profile.getRank().equals(ranks[i])) {
                    profile.setRank(ranks[i]);
                    // Trigger level-up celebration
                    showLevelUpNotification(project, ranks[i]);
                }
                break;
            }
        }
    }

    private static void showLevelUpNotification(Project project, String newRank) {
        com.intellij.notification.Notification notification =
                new com.intellij.notification.Notification(
                        "CodeArena",
                        "🏆 Level Up!",
                        "Congratulations! You've reached the rank of: " + newRank,
                        com.intellij.notification.NotificationType.INFORMATION
                );
        com.intellij.notification.Notifications.Bus.notify(notification, project);
    }
}
