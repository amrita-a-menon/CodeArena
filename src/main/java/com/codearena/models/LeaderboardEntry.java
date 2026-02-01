package com.codearena.models;

public class LeaderboardEntry {
    private String name;
    private String rank;
    private int totalXp;
    private double accuracy;

    public LeaderboardEntry(String name, String rank, int totalXp, double accuracy) {
        this.name = name;
        this.rank = rank;
        this.totalXp = totalXp;
        this.accuracy = accuracy;
    }

    public String getName() { return name; }
    public String getRank() { return rank; }
    public int getTotalXp() { return totalXp; }
    public double getAccuracy() { return accuracy; }
}
