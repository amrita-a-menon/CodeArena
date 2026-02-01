package com.codearena.core;

import com.intellij.execution.ExecutionListener;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.compiler.CompilerMessage;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;

public class ErrorListener {
    private final Project project;
    private String lastErrorSignature = "";
    private long lastErrorTimestamp = 0L;

    public ErrorListener(Project project) {
        this.project = project;
        setupExecutionListener();
        setupCompilationListener();
    }

    private void setupExecutionListener() {
        project.getMessageBus().connect().subscribe(ExecutionManager.EXECUTION_TOPIC, new ExecutionListener() {
            @Override
            public void processStarted(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler) {
                System.out.println("✅ CodeArena: Process started, attached listener");
                handler.addProcessListener(new ProcessListener() {
                    @Override
                    public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                        handleErrorText(event.getText());
                    }

                    @Override
                    public void processTerminated(@NotNull ProcessEvent event) {
                        System.out.println("ℹ️ CodeArena: Process terminated");
                    }

                    @Override
                    public void startNotified(@NotNull ProcessEvent event) {}
                });
            }
        });
    }

    private void setupCompilationListener() {
        CompilerManager.getInstance(project).addCompilationStatusListener(new CompilationStatusListener() {
            @Override
            public void compilationFinished(boolean aborted, int errors, int warnings, CompileContext compileContext) {
                if (aborted || errors <= 0) return;
                CompilerMessage[] messages = compileContext.getMessages(CompilerMessageCategory.ERROR);
                if (messages.length == 0) return;
                handleErrorText(messages[0].getMessage());
            }
        });
    }

    private void handleErrorText(String text) {
        if (!looksLikeError(text)) {
            return;
        }

        String signature = text.trim();
        if (isDuplicate(signature)) {
            return;
        }

        System.out.println("🎯 CodeArena detected error: " + text);

        String fullMessage = text.trim();
        String errorType = extractErrorType(text);
        
        // If it's a stack trace line, try to buffer it or find the main error
        if (text.trim().startsWith("at ") && ErrorContextStore.getLastError(project) != null) {
            // It's part of a stack trace, maybe we should append it to the last error?
            // For now, let's just make sure we don't treat every 'at' line as a NEW error
            return;
        }

        String intent = com.codearena.ui.IntentPadToolWindow.getSavedIntent();
        if (intent == null || intent.isEmpty()) {
            intent = "Running Java program";
        }

        // Capture snippet here, it will be wrapped in ReadAction inside the method
        String codeSnippet = getCurrentCodeSnippet(project);

        com.codearena.models.ErrorContext errorContext =
                new com.codearena.models.ErrorContext(
                        fullMessage,
                        errorType,
                        codeSnippet,
                        intent,
                        "Beginner"
                );

        ErrorContextStore.setLastError(project, errorContext);

        ApplicationManager.getApplication()
                .invokeLater(() -> {
                    try {
                        showChallengeForError(errorContext);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private boolean looksLikeError(String text) {
        if (text == null || text.isBlank()) return false;
        String lower = text.toLowerCase();
        
        // Don't trigger on every single stack trace line, only the main ones
        if (text.trim().startsWith("at ")) return false;

        // Ignore common "no error" summaries
        if (lower.contains("0 errors") || lower.contains("errors: 0") || lower.contains("error: 0") ||
            lower.contains("0 failed") || lower.contains("failed: 0")) {
            return false;
        }

        // Broaden detection for common Java/Kotlin errors
        if (lower.contains("exception") || 
            lower.contains("error:") ||
            lower.contains("error -") ||
            lower.contains("fatal") ||
            lower.contains("severe") ||
            lower.contains("assertionerror") ||
            lower.contains("exception in thread") ||
            lower.contains("compilation failed") || 
            lower.contains("caused by:") ||
            lower.contains("unable to") ||
            lower.contains("could not") ||
            lower.contains("runtimeerror") ||
            lower.contains("invalid") ||
            lower.contains("failed")) {
            return true;
        }
        return false;
    }

    private boolean isDuplicate(String signature) {
        long now = System.currentTimeMillis();
        if (signature.equals(lastErrorSignature) && (now - lastErrorTimestamp) < 2000) {
            return true;
        }
        lastErrorSignature = signature;
        lastErrorTimestamp = now;
        return false;
    }

    private String extractErrorType(String text) {
        if (text.contains("NullPointerException")) return "NullPointerException";
        if (text.contains("ArrayIndexOutOfBounds")) return "ArrayIndexOutOfBounds";
        if (text.contains("ClassCastException")) return "ClassCastException";
        if (text.contains("ArithmeticException")) return "ArithmeticException";
        if (text.contains("NumberFormatException")) return "NumberFormatException";
        if (text.contains("FileNotFoundException")) return "FileNotFoundException";
        if (text.contains("IOException")) return "IOException";
        if (text.toLowerCase().contains("error:")) return "Compilation Error";
        
        int exceptionIndex = text.indexOf("Exception");
        if (exceptionIndex > 0) {
            int start = text.lastIndexOf('\n', exceptionIndex);
            if (start == -1) start = 0;
            else start++;
            
            String line = text.substring(start).trim();
            int firstSpace = line.indexOf(' ');
            int firstColon = line.indexOf(':');
            
            int end = -1;
            if (firstSpace > 0 && firstColon > 0) end = Math.min(firstSpace, firstColon);
            else if (firstSpace > 0) end = firstSpace;
            else if (firstColon > 0) end = firstColon;
            
            if (end > 0) return line.substring(0, end).trim();
            return line;
        }
        return "Runtime Error";
    }

    private String getCurrentCodeSnippet(Project project) {
        return ApplicationManager.getApplication().runReadAction((com.intellij.openapi.util.Computable<String>) () -> {
            Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (editor == null) return "No active editor found";
            Document document = editor.getDocument();
            int line = editor.getCaretModel().getLogicalPosition().line;
            int startLine = Math.max(0, line - 10);
            int endLine = Math.min(document.getLineCount() - 1, line + 10);
            int startOffset = document.getLineStartOffset(startLine);
            int endOffset = document.getLineEndOffset(endLine);
            String snippet = document.getText(new TextRange(startOffset, endOffset)).trim();
            System.out.println("CodeArena: Captured code snippet around line " + (line + 1) + ". Length: " + snippet.length());
            return snippet;
        });
    }

    private void showChallengeForError(com.codearena.models.ErrorContext errorContext) {
        System.out.println("🎯 CodeArena: Triggering challenge generation for error: " + errorContext.getErrorType());

        com.codearena.models.Challenge challenge = ChallengeGenerator.generate(errorContext);

        new com.codearena.ui.ChallengeDialog(
                project,
                challenge
        ).show();
    }
}
