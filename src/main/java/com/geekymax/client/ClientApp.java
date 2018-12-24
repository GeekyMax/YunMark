package com.geekymax.client;


import com.geekymax.client.gui.MainFrame;

import javax.swing.*;
import java.awt.*;

/**
 * the client app entrance
 * @author Max Huang
 */
public class ClientApp {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            // fixme 修改
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Markdown editor");
            UIManager.put("swing.boldMetal", Boolean.FALSE);
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("error: " + e.getMessage());
                e.printStackTrace();
            }
            new MainFrame();
        });
    }
}