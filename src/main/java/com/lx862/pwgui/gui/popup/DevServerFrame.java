package com.lx862.pwgui.gui.popup;

import com.lx862.pwgui.gui.components.kui.KButton;
import com.lx862.pwgui.util.Util;
import com.lx862.pwgui.Main;
import com.lx862.pwgui.executable.ProgramExecution;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DevServerFrame extends JDialog {
    private final JTextArea logTextArea;
    private final ExecutorService serverExecutor;
    private ProgramExecution packwizServeProgram;

    public DevServerFrame(JFrame frame) {
        super(frame, Util.withTitlePrefix("Packwiz Serve"));

        setSize(400, 400);
        setLocationRelativeTo(frame);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.serverExecutor = Executors.newSingleThreadExecutor();

        JLabel description = new JLabel("This runs a local HTTP server for development, allowing packwiz-installer to reference a local pack.");
        add(description);

        logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        add(new JScrollPane(logTextArea));

        startServer();

        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        KButton startButton = new KButton("Start");
        startButton.setMnemonic(KeyEvent.VK_S);
        KButton stopButton = new KButton("Stop");
        stopButton.setMnemonic(KeyEvent.VK_S);
        stopButton.addActionListener(actionEvent -> {
            stopServer();
            stopButton.setVisible(false);
            startButton.setVisible(true);
        });
        actionButtons.add(stopButton);

        startButton.addActionListener(actionEvent -> {
            stopButton.setVisible(true);
            startButton.setVisible(false);
            startServer();
        });
        actionButtons.add(startButton);
        startButton.setVisible(false);

        add(actionButtons, BorderLayout.PAGE_END);
    }

    private void startServer() {
        logTextArea.append("----- Development Server Started -----\n");
        packwizServeProgram = Main.packwiz.buildCommand("serve")
            .whenStdout((line) -> {
                logTextArea.append(line + "\n");
            });

        try {
            packwizServeProgram.execute("Launched by user", serverExecutor);
        } catch (Exception e) {
            Main.LOGGER.exception(e);
            logTextArea.append(Util.withBracketPrefix(String.format("ERROR: %s", e.getMessage())));
        }
    }

    private void stopServer() {
        if(packwizServeProgram != null) packwizServeProgram.stop();
        serverExecutor.shutdown();
        logTextArea.append("----- Development Server Stopped -----\n");
    }

    @Override
    public void dispose() {
        stopServer();
        super.dispose();
    }
}
