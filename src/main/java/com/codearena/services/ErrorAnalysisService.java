package com.codearena.services;

import com.codearena.models.ErrorContext;

public class ErrorAnalysisService {
    public ErrorContext analyzeError(ErrorContext errorContext, String userIntent) {
        // For now, just return the enhanced context
        return new ErrorContext(
                errorContext.getErrorMessage(),
                errorContext.getErrorType(),
                errorContext.getCodeSnippet(),
                userIntent,
                "Intermediate"
        );
    }
}