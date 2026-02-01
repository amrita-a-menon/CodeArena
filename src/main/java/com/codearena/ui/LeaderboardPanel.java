package com.codearena.ui;

import com.codearena.models.UserProfile;
import com.codearena.persistence.UserProgress;
import com.codearena.persistence.UserProgressListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class LeaderboardPanel {
    private final JPanel panel;
    private final DefaultTableModel tableModel;

    public LeaderboardPanel(Project project) {
        panel = new JPanel(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"Player", "Rank", "XP", "Accuracy"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        refresh(UserProgress.loadProfile(project));

        project.getMessageBus().connect().subscribe(UserProgressListener.TOPIC, profile ->
                ApplicationManager.getApplication().invokeLater(() -> refresh(profile))
        );
    }

    public JPanel getContent() {
        return panel;
    }

    private void refresh(UserProfile profile) {
        tableModel.setRowCount(0);
        String player = profile.getUserId() == null ? "Player" : profile.getUserId();
        tableModel.addRow(new Object[]{
                player,
                profile.getRank(),
                profile.getTotalXp(),
                String.format("%.1f%%", profile.getAccuracy())
        });
    }
}
