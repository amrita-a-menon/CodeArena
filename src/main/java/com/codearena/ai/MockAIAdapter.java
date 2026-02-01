package com.codearena.ai;

public class MockAIAdapter implements AIAdapter {
    @Override
    public String generate(String prompt) {
        // Return a mock challenge in JSON format
        return """
            {
                "question": "What most likely caused this NullPointerException?",
                "options": [
                    "You forgot to initialize the object before use",
                    "The loop condition is incorrect",
                    "The method signature is wrong",
                    "Java garbage collected the object"
                ],
                "correctIndex": 0,
                "explanation": "The object was declared but not initialized with 'new' keyword.",
                "hints": ["Check line 42 for object declaration", "Look for missing 'new' keyword"],
                "xpValue": 15
            }
            """;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getName() {
        return "Mock AI";
    }
}