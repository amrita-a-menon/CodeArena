package com.codearena.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class IntentPadToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, 
                                       @NotNull ToolWindow toolWindow) {
        IntentPadToolWindow intentPad = new IntentPadToolWindow();
        ProgressPanel progressPanel = new ProgressPanel(project);
        LeaderboardPanel leaderboardPanel = new LeaderboardPanel(project);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Intent", intentPad.getContent());
        tabs.addTab("Progress", progressPanel.getContent());
        tabs.addTab("Leaderboard", leaderboardPanel.getContent());

        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(
            tabs,
            "",
            false
        );

        toolWindow.getContentManager().addContent(content);
    }
    
    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        // Tool window is always available
        return true;
    }
}
