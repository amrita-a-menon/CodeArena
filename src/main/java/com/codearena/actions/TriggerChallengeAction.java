package com.codearena.actions;

import com.codearena.core.ChallengeGenerator;
import com.codearena.core.ErrorContextStore;
import com.codearena.models.Challenge;
import com.codearena.models.ErrorContext;
import com.codearena.ui.ChallengeDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class TriggerChallengeAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (e.getProject() == null) return;
        ErrorContext errorContext = ErrorContextStore.getLastError(e.getProject());
        if (errorContext == null) {
            String intent = com.codearena.ui.IntentPadToolWindow.getSavedIntent();
            if (intent == null || intent.isEmpty()) {
                intent = "Manual trigger without saved intent";
            }
            String codeSnippet = getCurrentCodeSnippet(e.getProject());
            errorContext = new ErrorContext(
                    "Manual trigger (no error captured)",
                    "Manual Trigger",
                    codeSnippet,
                    intent,
                    "Beginner"
            );
        }
        
        try {
            // Get intent from context
            String intent = errorContext.getUserIntent();
            System.out.println("CodeArena: Manual trigger. Intent: " + intent);
            
            // Generate challenge
            Challenge challenge = ChallengeGenerator.generate(errorContext);
            
            // Show the challenge dialog
            new ChallengeDialog(e.getProject(), challenge).show();
            
        } catch (Exception ex) {
            System.err.println("CodeArena: Error in TriggerChallengeAction: " + ex.getMessage());
            ex.printStackTrace();
            // Get intent safely for fallback
            String intent = errorContext != null ? errorContext.getUserIntent() : "Unknown";
            String errorType = errorContext != null ? errorContext.getErrorType() : "Unknown";
            
            // Fallback to demo challenge
            Messages.showInfoMessage(
                "Generated demo challenge!\n\n" +
                "Error Detected: " + errorType + "\n" +
                "Question: An error occurred: " + errorType + ". How should you start debugging this?\n" +
                "Your Intent: " + intent + "\n\n" +
                "1. Read the stack trace to find the failing line (Correct)\n" +
                "2. Guess which variable is broken\n" +
                "3. Delete the code and start over\n\n" +
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

    private String getCurrentCodeSnippet(com.intellij.openapi.project.Project project) {
        return ApplicationManager.getApplication().runReadAction((com.intellij.openapi.util.Computable<String>) () -> {
            Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (editor == null) return "No active editor found";
            Document document = editor.getDocument();
            int line = editor.getCaretModel().getLogicalPosition().line;
            int startLine = Math.max(0, line - 10);
            int endLine = Math.min(document.getLineCount() - 1, line + 10);
            int startOffset = document.getLineStartOffset(startLine);
            int endOffset = document.getLineEndOffset(endLine);
            return document.getText(new TextRange(startOffset, endOffset)).trim();
        });
    }
}
