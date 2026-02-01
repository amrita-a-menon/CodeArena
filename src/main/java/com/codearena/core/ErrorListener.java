package com.codearena.core;

import com.codearena.ui.SimpleChallengeDialog;
import com.intellij.execution.ExecutionListener;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;

public class ErrorListener {
    private final Project project;

    public ErrorListener(Project project) {
        this.project = project;
        setupExecutionListener();
    }

    private void setupExecutionListener() {
        project.getMessageBus().connect().subscribe(ExecutionManager.EXECUTION_TOPIC, new ExecutionListener() {
            @Override
            public void processStarted(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler) {
                handler.addProcessListener(new ProcessListener() {
                    @Override
                    public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                        String text = event.getText();

                        // Check for common Java errors
                        if (text.contains("NullPointerException") ||
                                text.contains("ArrayIndexOutOfBoundsException") ||
                                text.contains("ClassCastException") ||
                                text.contains("IOException")) {

                            System.out.println("🎯 CodeArena detected error: " + text);

                            // Get current intent
                            String intent = com.codearena.ui.IntentPadToolWindow.getSavedIntent();
                            if (intent == null || intent.isEmpty()) {
                                intent = "Running Java program";
                            }

                            // Create error context
                            com.codearena.models.ErrorContext errorContext =
                                    new com.codearena.models.ErrorContext(
                                            text.trim(),
                                            extractErrorType(text),
                                            getCurrentCodeSnippet(project),
                                            intent,
                                            "Beginner"
                                    );

                            // Trigger challenge (on UI thread)
                            com.intellij.openapi.application.ApplicationManager.getApplication()
                                    .invokeLater(() -> {
                                        try {
                                            showChallengeForError(errorContext);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    });
                        }
                    }

                    private String extractErrorType(String text) {
                        if (text.contains("NullPointerException")) return "NullPointerException";
                        if (text.contains("ArrayIndexOutOfBounds")) return "ArrayIndexOutOfBounds";
                        if (text.contains("ClassCastException")) return "ClassCastException";
                        return "Runtime Exception";
                    }

                    private String getCurrentCodeSnippet(Project project) {
                        return "public class Main {\n" +
                                "    public static void main(String[] args) {\n" +
                                "        String input = null;\n" +
                                "        System.out.println(input.length());\n" +
                                "    }\n" +
                                "}";
                    }

                    @Override
                    public void processTerminated(@NotNull ProcessEvent event) {}

                    @Override
                    public void startNotified(@NotNull ProcessEvent event) {}
                });
            }
        });
    }

    private void showChallengeForError(com.codearena.models.ErrorContext errorContext) {
        System.out.println("🎯 Showing challenge for: " + errorContext.getErrorMessage());

        // Generate challenge via AI
        com.codearena.models.Challenge challenge = ChallengeGenerator.generate(errorContext);

        // Show the real challenge dialog
        new com.codearena.ui.ChallengeDialog(
                project,
                challenge
        ).show();
    }
}