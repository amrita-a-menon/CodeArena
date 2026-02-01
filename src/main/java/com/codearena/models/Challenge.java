package com.codearena.models;

import java.util.List;

public class Challenge {
    private String question;
    private List<String> options;
    private int correctIndex;
    private String explanation;
    private List<String> hints;
    private int xpValue;

    public Challenge(String question, List<String> options, int correctIndex,
                     String explanation, List<String> hints, int xpValue) {
        this.question = question;
        this.options = options;
        this.correctIndex = correctIndex;
        this.explanation = explanation;
        this.hints = hints;
        this.xpValue = xpValue;
    }

    // Getters
    public String getQuestion() { return question; }
    public List<String> getOptions() { return options; }
    public int getCorrectIndex() { return correctIndex; }
    public String getExplanation() { return explanation; }
    public List<String> getHints() { return hints; }
    public int getXpValue() { return xpValue; }
}