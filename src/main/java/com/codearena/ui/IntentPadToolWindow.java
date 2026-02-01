package com.codearena.ui;

import com.codearena.core.ErrorContextStore;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBLabel;
import com.intellij.notification.Notifications;

import javax.swing.*;
import java.awt.*;

public class IntentPadToolWindow {
    private JPanel panel;
    private JBTextArea intentArea;
    private static String savedIntent = "";

    public IntentPadToolWindow() {
        panel = new JPanel(new BorderLayout(10, 10));

        // Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(new JBLabel("🎯 CodeArena Intent Pad"));

        // Status label
        JLabel statusLabel = new JBLabel("No intent saved yet");
        header.add(statusLabel);

        // Intent input
        intentArea = new JBTextArea();
        intentArea.setLineWrap(true);
        intentArea.setRows(8);
        intentArea.setToolTipText("Describe what you're trying to accomplish...\nExample: 'I want to read a CSV file and calculate averages'");

        // Load any existing intent
        if (!savedIntent.isEmpty()) {
            intentArea.setText(savedIntent);
            statusLabel.setText("Intent loaded from memory");
        }

        JButton saveButton = new JButton("💾 Save Intent");
        saveButton.addActionListener(e -> {
            String intent = intentArea.getText().trim();
            if (!intent.isEmpty()) {
                savedIntent = intent;
                statusLabel.setText("Intent saved! ✔️");

                // Show notification
                Notifications.Bus.notify(
                        new com.intellij.notification.Notification(
                                "CodeArena",
                                "🎯 Intent Saved",
                                "Your coding intent has been saved for error context.",
                                com.intellij.notification.NotificationType.INFORMATION
                        )
                );

                System.out.println("Saved intent: " + intent);

                // Update intent in the existing error context (if any) without creating a dummy error.
                for (Project p : ProjectManager.getInstance().getOpenProjects()) {
                    com.codearena.models.ErrorContext existing = ErrorContextStore.getLastError(p);
                    if (existing == null) {
                        continue;
                    }
                    String codeSnippet = existing.getCodeSnippet();
                    if (codeSnippet == null || codeSnippet.isEmpty() || codeSnippet.equals("No active editor found")) {
                        // Try to get a fresh snippet if the existing one is poor
                        try {
                            Editor editor = FileEditorManager.getInstance(p).getSelectedTextEditor();
                            if (editor != null) {
                                Document document = editor.getDocument();
                                int line = editor.getCaretModel().getLogicalPosition().line;
                                int startLine = Math.max(0, line - 10);
                                int endLine = Math.min(document.getLineCount() - 1, line + 10);
                                codeSnippet = document.getText(new TextRange(document.getLineStartOffset(startLine), document.getLineEndOffset(endLine))).trim();
                            }
                        } catch (Exception ex) {
                            // Ignore snippet update errors here
                        }
                    }

                    ErrorContextStore.setLastError(p, new com.codearena.models.ErrorContext(
                            existing.getErrorMessage(),
                            existing.getErrorType(),
                            codeSnippet,
                            intent,
                            existing.getUserSkillLevel()
                    ));
                }
            } else {
                statusLabel.setText("Please enter an intent first!");
            }
        });

        JButton clearButton = new JButton("🗑️ Clear");
        clearButton.addActionListener(e -> {
            intentArea.setText("");
            savedIntent = "";
            statusLabel.setText("Intent cleared");
        });

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);

        // Add everything
        panel.add(header, BorderLayout.NORTH);
        panel.add(new JScrollPane(intentArea), BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(southPanel, BorderLayout.SOUTH);
    }

    public JPanel getContent() {
        return panel;
    }

    // Method to get the saved intent from anywhere
    public static String getSavedIntent() {
        return savedIntent;
    }
}
