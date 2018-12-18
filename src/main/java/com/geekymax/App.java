package com.geekymax;

import com.geekymax.console.ArgsParser;
import com.geekymax.console.ConsoleMode;
import com.geekymax.console.DisplayUsageException;
import com.geekymax.gui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;

/**
 * @author Max Huang
 */
public class App {
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
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
            }
        });
    }
}