package com.codearena.actions;

import com.codearena.core.ChallengeGenerator;
import com.codearena.models.Challenge;
import com.codearena.models.ErrorContext;
import com.codearena.ui.ChallengeDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class TriggerChallengeAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (e.getProject() == null) return;
        
        // Get saved intent
        String intent = com.codearena.ui.IntentPadToolWindow.getSavedIntent();
        if (intent.isEmpty()) {
            intent = "No intent specified (demo mode)";
        }
        
        // Create a simulated error context
        ErrorContext errorContext = new ErrorContext(
            "NullPointerException: Cannot invoke \"String.length()\" because \"s\" is null",
            "Runtime Exception",
            "public class Test {\n    public static void main(String[] args) {\n        String s = null;\n        System.out.println(s.length());\n    }\n}",
            intent,
            "Beginner"
        );
        
        try {
            // Generate challenge
            Challenge challenge = ChallengeGenerator.generate(errorContext);
            
            // Show the challenge dialog
            new ChallengeDialog(e.getProject(), challenge).show();
            
        } catch (Exception ex) {
            // Fallback to demo challenge
            Messages.showInfoMessage(
                "Generated demo challenge!\n\n" +
                "Question: What causes this NullPointerException?\n" +
                "Your Intent: " + intent + "\n\n" +
                "Try implementing the full ChallengeGenerator!",
                "CodeArena Demo"
            );
        }
    }
    
    @Override
    public void update(@NotNull AnActionEvent e) {
        // This action is always available
        e.getPresentation().setEnabled(true);
    }
}
