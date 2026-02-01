package com.codearena.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;

public class CodeArenaAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        Messages.showInfoMessage("CodeArena is installed and running ✅", "CodeArena");
    }
}
