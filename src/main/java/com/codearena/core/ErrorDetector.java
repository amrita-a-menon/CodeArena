package com.codearena.core;

import com.codearena.ui.SimpleChallengeDialog;  // USE SIMPLE VERSION
import com.intellij.openapi.project.Project;

public class ErrorDetector {
    private final Project project;

    public ErrorDetector(Project project) {
        this.project = project;
    }

    public void onErrorDetected() {
        // Show simple challenge dialog
        SimpleChallengeDialog dialog = new SimpleChallengeDialog(project, "Simulated Error", "Demo Intent");
        dialog.show();
    }
}