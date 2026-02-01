package com.codearena.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class IntentPadToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, 
                                       @NotNull ToolWindow toolWindow) {
        // Create the actual content
        IntentPadToolWindow intentPad = new IntentPadToolWindow();
        
        // Get content factory and create content
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(
            intentPad.getContent(),  // Your panel
            "",                      // Display name (empty for tool window title)
            false                    // Not lockable
        );
        
        // Add content to tool window
        toolWindow.getContentManager().addContent(content);
    }
    
    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        // Tool window is always available
        return true;
    }
}
