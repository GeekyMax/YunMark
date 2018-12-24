package com.geekymax.client.gui;

import javax.swing.*;
import java.awt.*;

/**
 * the main frame
 * @author Max Huang
 */
public final class MainFrame {

    /**
     * Creates the main window and makes it visible.
     */
    public MainFrame() {
        // xxx 需要更好的配置
        Dimension frameSize = new Dimension(600, 400);

        JFrame mainFrame = new JFrame("YunMark 在线协作markdown编辑器");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(frameSize);
        mainFrame.setMinimumSize(frameSize);

        MenuBar menu = new MenuBar();
        mainFrame.setJMenuBar(menu.get());
        MainPanel panel = new MainPanel();
        mainFrame.getContentPane().add(panel.get());
        // Center main frame
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
}
