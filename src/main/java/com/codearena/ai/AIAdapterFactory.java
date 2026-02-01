package com.codearena.ai;

import com.codearena.persistence.SettingsManager;

public class AIAdapterFactory {
    public static AIAdapter createAdapter() {
        try {
            String adapterType = SettingsManager.getAIAdapterType();

            switch (adapterType.toUpperCase()) {
                case "OPENAI":
                    OpenAIAdapter openAI = new OpenAIAdapter();
                    if (openAI.isAvailable()) {
                        return openAI;
                    }
                // Fall through if not available
                case "MOCK":
                default:
                    return new MockAIAdapter();
            }
        } catch (Exception e) {
            // Fallback for tests or when ApplicationManager is not initialized
            return new MockAIAdapter();
        }
    }
}