package com.codearena.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBLabel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class IntentPadToolWindow implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project,
                                        @NotNull ToolWindow toolWindow) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.add(new JBLabel("🎯 CodeArena Intent Pad"), BorderLayout.WEST);

        // Intent input
        JBTextArea intentArea = new JBTextArea();
        intentArea.setLineWrap(true);
        intentArea.setRows(5);
        intentArea.setToolTipText("Describe what you're trying to accomplish...");

        JButton saveButton = new JButton("Save Intent");
        saveButton.addActionListener(e -> {
            IntentService.saveIntent(project, intentArea.getText());
            JOptionPane.showMessageDialog(panel, "Intent saved!");
        });

        panel.add(header, BorderLayout.NORTH);
        panel.add(new JScrollPane(intentArea), BorderLayout.CENTER);
        panel.add(saveButton, BorderLayout.SOUTH);

        toolWindow.getComponent().add(panel);
    }
}