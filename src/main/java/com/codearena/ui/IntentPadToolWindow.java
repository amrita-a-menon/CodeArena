package com.codearena.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
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
        JPanel header = new JPanel(new BorderLayout());
        header.add(new JBLabel("🎯 CodeArena Intent Pad"), BorderLayout.WEST);

        // Status label
        JLabel statusLabel = new JBLabel("No intent saved yet");

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
        panel.add(statusLabel, BorderLayout.NORTH);
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