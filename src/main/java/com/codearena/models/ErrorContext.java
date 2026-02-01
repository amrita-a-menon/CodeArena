package com.codearena.models;

public class ErrorContext {
    private String errorMessage;
    private String errorType;
    private String codeSnippet;
    private String userIntent;
    private String userSkillLevel;

    public ErrorContext(String errorMessage, String errorType, String codeSnippet,
                        String userIntent, String userSkillLevel) {
        this.errorMessage = errorMessage;
        this.errorType = errorType;
        this.codeSnippet = codeSnippet;
        this.userIntent = userIntent;
        this.userSkillLevel = userSkillLevel;
    }

    // Getters
    public String getErrorMessage() { return errorMessage; }
    public String getErrorType() { return errorType; }
    public String getCodeSnippet() { return codeSnippet; }
    public String getUserIntent() { return userIntent; }
    public String getUserSkillLevel() { return userSkillLevel; }
}