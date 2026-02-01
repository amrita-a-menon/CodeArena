package com.codearena.actions;

import com.codearena.persistence.SettingsManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class TestAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String currentKey = SettingsManager.getOpenAIKey();
        String currentAdapter = SettingsManager.getAIAdapterType();

        String newKey = Messages.showInputDialog(
                e.getProject(),
                "Current OpenAI Key: " + (currentKey.isEmpty() ? "Not set" : "****" + currentKey.substring(Math.max(0, currentKey.length() - 4))) +
                        "\n\nCurrent Adapter: " + currentAdapter +
                        "\n\nEnter new OpenAI API Key (leave blank to keep current):",
                "CodeArena AI Settings",
                null
        );

        if (newKey != null && !newKey.isEmpty()) {
            SettingsManager.setOpenAIKey(newKey);
            SettingsManager.setAIAdapterType("OPENAI");
            Messages.showInfoMessage("API Key saved and adapter switched to OPENAI!", "Settings Updated");
        } else {
            String[] options = {"OPENAI", "MOCK"};
            int choice = Messages.showDialog(
                    e.getProject(),
                    "Choose AI Adapter Type:",
                    "CodeArena AI Settings",
                    options,
                    currentAdapter.equals("OPENAI") ? 0 : 1,
                    null
            );
            if (choice >= 0) {
                SettingsManager.setAIAdapterType(options[choice]);
                Messages.showInfoMessage("Adapter switched to " + options[choice], "Settings Updated");
            }
        }
    }
}
