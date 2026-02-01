package com.codearena.ai;

public class LocalModelAdapter implements AIAdapter {
    public String generate(String prompt) { 
        return "{}"; 
    }
    public boolean isAvailable() { 
        return false; 
    }
    public String getName() { 
        return "Local Model"; 
    }
}
