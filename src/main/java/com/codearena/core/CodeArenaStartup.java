package com.codearena.core;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CodeArenaStartup implements ProjectActivity {
    @Nullable
    @Override
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        System.out.println("✅ CodeArena plugin loaded!");
        System.out.println("Project: " + project.getName());

        // Initialize error detection
        new ErrorListener(project);

        // Show notification
        com.intellij.notification.Notification notification =
                new com.intellij.notification.Notification(
                        "CodeArena",
                        "🎮 CodeArena Ready",
                        "Error detection active! Run code with errors to trigger challenges.",
                        com.intellij.notification.NotificationType.INFORMATION
                );

        com.intellij.notification.Notifications.Bus.notify(notification);
        return Unit.INSTANCE;
    }
}
