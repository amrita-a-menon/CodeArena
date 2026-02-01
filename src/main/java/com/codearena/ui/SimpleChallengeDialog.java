package com.codearena.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SimpleChallengeDialog extends DialogWrapper {
    private final String errorMessage;
    private final String userIntent;
    private ButtonGroup buttonGroup;

    public SimpleChallengeDialog(Project project, String errorMessage, String userIntent) {
        super(project);
        this.errorMessage = errorMessage;
        this.userIntent = userIntent;
        setTitle("🎮 CodeArena Challenge");
        setOKButtonText("Submit Answer");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Header with error info
        String displayError = errorMessage.length() > 100
                ? errorMessage.substring(0, 100) + "..."
                : errorMessage;

        JLabel header = new JLabel("<html><b>Error Detected:</b> " + displayError +
                "<br><b>Your Intent:</b> " + userIntent + "</html>");
        panel.add(header, BorderLayout.NORTH);

        // Question
        JLabel question = new JLabel("<html><h3>What's the most likely cause of this NullPointerException?</h3></html>");
        panel.add(question, BorderLayout.CENTER);

        // Options panel
        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        buttonGroup = new ButtonGroup();

        String[] options = {
                "The object/variable was never initialized (is null)",
                "The method name is spelled incorrectly",
                "Missing import statement for the class",
                "The object was garbage collected"
        };

        for (int i = 0; i < options.length; i++) {
            JRadioButton radio = new JRadioButton((i + 1) + ". " + options[i]);
            radio.setActionCommand(String.valueOf(i));
            buttonGroup.add(radio);
            optionsPanel.add(radio);
        }

        panel.add(optionsPanel, BorderLayout.SOUTH);

        // Set dialog size
        setSize(600, 400);

        return panel;
    }

    @Override
    protected Action[] createActions() {
        return new Action[]{
                getOKAction(),
                getCancelAction()
        };
    }

    @Override
    protected void doOKAction() {
        // Check which option was selected
        if (buttonGroup.getSelection() == null) {
            JOptionPane.showMessageDialog(getWindow(), "Please select an answer!");
            return;
        }

        int selected = Integer.parseInt(buttonGroup.getSelection().getActionCommand());
        boolean correct = (selected == 0); // First option is correct

        if (correct) {
            JOptionPane.showMessageDialog(getWindow(),
                    "✅ Correct! +15 XP\n\n" +
                            "Explanation: NullPointerException happens when you try to use " +
                            "an object reference that hasn't been initialized (is null).\n" +
                            "Solution: Always initialize objects before using them, or add null checks.",
                    "Well Done!",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(getWindow(),
                    "❌ Not quite right. -5 XP\n\n" +
                            "The correct answer is: The object/variable was never initialized (is null)\n\n" +
                            "Hint: Look for variables that are declared but not assigned with 'new' keyword.",
                    "Try Again",
                    JOptionPane.WARNING_MESSAGE);
        }

        super.doOKAction();
    }
}