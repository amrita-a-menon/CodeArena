package com.codearena.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.codearena.models.Challenge;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ChallengeDialog extends DialogWrapper {
    private final Challenge challenge;
    private final Project project;
    private ButtonGroup buttonGroup;

    public ChallengeDialog(Project project, Challenge challenge) {
        super(project);
        this.project = project;
        this.challenge = challenge;
        setTitle("🎮 CodeArena Challenge");
        setOKButtonText("Submit Answer");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Question
        JLabel question = new JLabel("<html><h3>" + challenge.getQuestion() + "</h3></html>");
        panel.add(question, BorderLayout.NORTH);

        // Options
        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        buttonGroup = new ButtonGroup();

        for (int i = 0; i < challenge.getOptions().size(); i++) {
            JRadioButton option = new JRadioButton(challenge.getOptions().get(i));
            option.setActionCommand(String.valueOf(i));
            buttonGroup.add(option);
            optionsPanel.add(option);
        }

        panel.add(optionsPanel, BorderLayout.CENTER);

        // XP indicator
        JLabel xpLabel = new JLabel("🎯 XP at stake: " + challenge.getXpValue());
        panel.add(xpLabel, BorderLayout.SOUTH);

        return panel;
    }

    @Override
    protected void doOKAction() {
        String selected = buttonGroup.getSelection().getActionCommand();
        int selectedIndex = Integer.parseInt(selected);

        boolean correct = (selectedIndex == challenge.getCorrectIndex());

        // Update scoring
        ScoringSystem.processAnswer(project, challenge, correct, false);

        // Show feedback
        if (correct) {
            JOptionPane.showMessageDialog(getPanel(),
                    "✅ Correct! +" + challenge.getXpValue() + " XP\n\n" +
                            challenge.getExplanation());
        } else {
            JOptionPane.showMessageDialog(getPanel(),
                    "❌ Incorrect. -5 XP\n\n" +
                            "Hint: " + challenge.getHints().get(0) + "\n\n" +
                            "Explanation: " + challenge.getExplanation());
        }

        super.doOKAction();
    }
}