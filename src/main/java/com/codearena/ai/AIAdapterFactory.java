package com.codearena.ai;

import com.codearena.persistence.SettingsManager;

public class AIAdapterFactory {
    public static AIAdapter createAdapter() {
        try {
            String adapterType = "MOCK";
            try {
                adapterType = SettingsManager.getAIAdapterType();
            } catch (Exception e) {
                System.out.println("CodeArena: Could not access SettingsManager (likely in test environment). Using MOCK.");
            }
            System.out.println("CodeArena: Factory creating adapter. Type from settings: " + adapterType);

            switch (adapterType.toUpperCase()) {
                case "OPENAI":
                    OpenAIAdapter openAI = new OpenAIAdapter();
                    if (openAI.isAvailable()) {
                        System.out.println("CodeArena: OpenAI Adapter is available.");
                        return openAI;
                    } else {
                        System.out.println("CodeArena: OpenAI Adapter NOT available (likely missing API key).");
                    }
                // Fall through if not available
                case "MOCK":
                default:
                    System.out.println("CodeArena: Returning MockAIAdapter.");
                    return new MockAIAdapter();
            }
        } catch (Exception e) {
            System.err.println("CodeArena: Factory exception: " + e.getMessage());
            e.printStackTrace();
            return new MockAIAdapter();
        }
    }
}