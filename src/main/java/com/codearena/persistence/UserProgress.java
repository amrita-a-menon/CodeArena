package com.codearena.persistence;

import com.codearena.models.UserProfile;
import com.intellij.openapi.project.Project;

public class UserProgress {
    public static UserProfile loadProfile(Project project) {
        // For now, return a new profile
        return new UserProfile();
    }

    public static void saveProfile(Project project, UserProfile profile) {
        // Implementation would save to disk
        System.out.println("Saved profile with " + profile.getTotalXp() + " XP");
    }
}