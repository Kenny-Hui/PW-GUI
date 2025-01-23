package com.lx862.pwgui.gui.popup;

import com.lx862.pwgui.core.Constants;
import com.lx862.pwgui.Main;
import com.lx862.pwgui.executable.Executable;
import com.lx862.pwgui.executable.ProgramExecution;
import com.lx862.pwgui.gui.base.kui.KButton;
import com.lx862.pwgui.gui.base.kui.KGridBagLayoutPanel;
import com.lx862.pwgui.gui.base.kui.KTextField;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ConsoleDialog extends JDialog {
    private final List<String> commandHistory;
    private final Executable executable;
    private final JTextArea logTextArea;
    private ProgramExecution currentExecution;

    public ConsoleDialog(Executable executable, JFrame frame) {
        super(frame, Util.withTitlePrefix(String.format("%s Console", executable.getProgramName())));
        this.commandHistory = new ArrayList<>();
        this.executable = executable;

        setSize(600, 450);
        setLocationRelativeTo(frame);

        JLabel description = new JLabel(String.format("Here you can directly run %s commands.", executable.getProgramName()));
        add(description);

        logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        add(new JScrollPane(logTextArea));

        KGridBagLayoutPanel inputArea = new KGridBagLayoutPanel(4, 3);
        KTextField inputField = new KTextField("help");
        inputField.addActionListener(actionEvent -> {
            executeCommand(inputField.getText(), false);
            inputField.setText("");
        });

        KButton sendButton = new KButton("Send");
        sendButton.addActionListener(actionEvent -> {
            executeCommand(inputField.getText(), false);
            inputField.setText("");
        });

        inputArea.addRow(1, 1, new JLabel(executable.getProgramName()), inputField, sendButton);

        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        KButton closeButton = new KButton("Close");
        closeButton.setMnemonic(KeyEvent.VK_S);
        closeButton.addActionListener(actionEvent -> {
            dispose();
        });
        actionButtons.add(closeButton);

        add(inputArea, BorderLayout.PAGE_END);
        executeCommand("", true);
    }

    private void executeCommand(String args, boolean helpMessage) {
        if(currentExecution != null) {
            logTextArea.append(String.format("\n> %s\n", args));
            currentExecution.enterInput(args);
        } else {
            if(!helpMessage) {
                logTextArea.append(String.format("\n> %s %s\n", executable.getProgramName(), args));
                commandHistory.add(args);
            }

            StringTokenizer argsTokenizer = new StringTokenizer(args);
            String[] splitArgs = new String[argsTokenizer.countTokens()];

            for(int i = 0; argsTokenizer.hasMoreTokens(); ++i) {
                splitArgs[i] = argsTokenizer.nextToken();
            }

            ProgramExecution programExecution = Main.packwiz.buildCommand(splitArgs);
            currentExecution = programExecution;
            programExecution.whenStdout(line -> {
                logTextArea.append(line + "\n");
                logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
            });
            programExecution.whenExit(exitCode -> {
                if(helpMessage) {
                    logTextArea.select(0, 0);
                } else {
                    logTextArea.append(String.format("Exited with code %d\n", exitCode));
                }
                currentExecution = null;
            });
//            programExecution.useRuntime();
            programExecution.execute(helpMessage ? "Display help message" : Constants.REASON_TRIGGERED_BY_USER);
        }
    }
}
