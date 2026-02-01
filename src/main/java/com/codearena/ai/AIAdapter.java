package com.codearena.ai;

public interface AIAdapter {
    String generate(String prompt);
    boolean isAvailable();
    String getName();
}