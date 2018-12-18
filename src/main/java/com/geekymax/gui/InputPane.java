package com.geekymax.gui;


import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Observable;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


/**
 * @author Max Huang
 */
public final class InputPane extends Observable {
    private final JScrollPane inputPane = new JScrollPane();
    private final JTextArea inputTextArea = new JTextArea();

    /**
     * Creates the text area and add a key listener to call observer every time a key is released.
     */
    public InputPane() {
        inputPane.getViewport().add(inputTextArea, null);
        inputTextArea.setFont(new Font("微软雅黑",Font.PLAIN,16));
        inputTextArea.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                setChanged();
                notifyObservers(inputTextArea.getText());
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }
        });
    }

    /**
     * Returns the JScrollPane object.
     *
     * @return the JScrollPane object.
     */
    public JScrollPane get() {
        return inputPane;
    }
}
