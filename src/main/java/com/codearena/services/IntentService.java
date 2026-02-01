package com.codearena.services;

import com.intellij.openapi.project.Project;

public class IntentService {
    public static String getCurrentIntent(Project project) {
        // For now, return mock intent
        return "I want to parse a CSV and compute averages";
    }

    public static void saveIntent(Project project, String intent) {
        // Save intent to project settings
        System.out.println("Saved intent: " + intent);
    }
}