package com.codearena.ui;

import com.codearena.models.UserProfile;
import com.codearena.persistence.UserProgress;
import com.codearena.persistence.UserProgressListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;

public class ProgressPanel {
    private final JPanel panel;
    private final JLabel rankValue;
    private final JLabel xpValue;
    private final JLabel streakValue;
    private final JLabel accuracyValue;
    private final JLabel completedValue;

    public ProgressPanel(Project project) {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        rankValue = new JLabel();
        xpValue = new JLabel();
        streakValue = new JLabel();
        accuracyValue = new JLabel();
        completedValue = new JLabel();

        panel.add(labelRow("Rank:", rankValue));
        panel.add(labelRow("Total XP:", xpValue));
        panel.add(labelRow("Current Streak:", streakValue));
        panel.add(labelRow("Accuracy:", accuracyValue));
        panel.add(labelRow("Challenges:", completedValue));

        update(UserProgress.loadProfile(project));

        project.getMessageBus().connect().subscribe(UserProgressListener.TOPIC, profile ->
                ApplicationManager.getApplication().invokeLater(() -> update(profile))
        );
    }

    public JPanel getContent() {
        return panel;
    }

    private void update(UserProfile profile) {
        rankValue.setText(profile.getRank());
        xpValue.setText(String.valueOf(profile.getTotalXp()));
        streakValue.setText(String.valueOf(profile.getCurrentStreak()));
        accuracyValue.setText(String.format("%.1f%%", profile.getAccuracy()));
        completedValue.setText(profile.getChallengesCompleted() + " (" + profile.getChallengesCorrect() + " correct)");
    }

    private JPanel labelRow(String label, JLabel value) {
        JPanel row = new JPanel(new BorderLayout());
        row.add(new JLabel(label), BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);
        row.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        return row;
    }
}
