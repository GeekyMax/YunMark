package com.geekymax.gui;

import javax.swing.*;
import java.awt.*;

/**
 * @author Max Huang
 */
public final class MainFrame {
    private final JFrame mainFrame = new JFrame("Markdown editor");
    private final MenuBar menu = new MenuBar();
    private final MainPanel panel = new MainPanel();

    /**
     * Creates the main window and makes it visible.
     */
    public MainFrame() {
        Dimension frameSize = new Dimension(1280, 900);

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(frameSize);
        mainFrame.setMinimumSize(frameSize);

        mainFrame.setJMenuBar(menu.get());
        mainFrame.getContentPane().add(panel.get());
        // Center main frame
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
}
