package com.codearena.models;

public class UserProfile {
    private String userId;
    private String rank = "Syntax Survivor";
    private int totalXp = 0;
    private int currentStreak = 0;
    private int challengesCompleted = 0;
    private int challengesCorrect = 0;

    // Getters and Setters
    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }

    public int getTotalXp() { return totalXp; }
    public void addXp(int xp) { this.totalXp += xp; }

    public int getCurrentStreak() { return currentStreak; }
    public void incrementStreak() { this.currentStreak++; }
    public void resetStreak() { this.currentStreak = 0; }

    public double getAccuracy() {
        if (challengesCompleted == 0) return 0.0;
        return (challengesCorrect * 100.0) / challengesCompleted;
    }
}