package com.codearena.core;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.codearena.models.ErrorContext;

public class ErrorDetector {
    private final Project project;
    private final ErrorAnalysisService errorAnalysisService;

    public ErrorDetector(Project project) {
        this.project = project;
        this.errorAnalysisService = new ErrorAnalysisService();
    }

    public void onErrorDetected(ErrorContext errorContext) {
        // Get user intent if available
        String userIntent = IntentService.getCurrentIntent(project);

        // Analyze error with AI
        ErrorContext enhancedContext = errorAnalysisService.analyzeError(
                errorContext,
                userIntent
        );

        // Generate challenge
        Challenge challenge = ChallengeGenerator.generate(enhancedContext);

        // Show challenge dialog
        ChallengeDialog dialog = new ChallengeDialog(project, challenge);
        dialog.show();
    }
}