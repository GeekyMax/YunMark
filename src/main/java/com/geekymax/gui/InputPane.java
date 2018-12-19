package com.geekymax.gui;


import com.geekymax.thread.ReceiveThread;
import com.geekymax.thread.SendThread;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.Observable;
import java.util.concurrent.*;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.GapContent;
import javax.swing.text.Segment;
import javax.swing.undo.CompoundEdit;


/**
 * @author Max Huang
 */
public final class InputPane extends Observable {
    private final JScrollPane inputPane = new JScrollPane();
    private final JTextArea inputTextArea = new JTextArea();
    private static String previousText;
    private final Object lock;
    private SendThread sendThread;
    private ReceiveThread receiveThread;

    /**
     * Creates the text area and add a key listener to call observer every time a key is released.
     */
    public InputPane() {
        lock = new Object();
        inputPane.getViewport().add(inputTextArea, null);
        inputTextArea.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        try {
            Socket socket = new Socket("127.0.0.1", 9999);
            sendThread = new SendThread(socket, lock);
            receiveThread = new ReceiveThread(socket);
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 4, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2), threadFactory);
            threadPoolExecutor.execute(sendThread);
            threadPoolExecutor.execute(receiveThread);
            ;
        } catch (Exception e) {
            e.printStackTrace();
        }

        inputTextArea.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                previousText = inputTextArea.getText();
                setChanged();
                notifyObservers(inputTextArea.getText());
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }
        });
        inputTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changeFilter(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changeFilter(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changeFilter(e);
            }

            protected void changeFilter(DocumentEvent event) {
                javax.swing.text.Document document = event.getDocument();
                try {
                    String type;
                    if (event.getType() == DocumentEvent.EventType.INSERT) {
                        type = "ins";
                    } else if (event.getType() == DocumentEvent.EventType.REMOVE) {
                        type = "del";
                    } else {
                        type = "upd";
                    }
                    int offset = event.getOffset();
                    int length = event.getLength();
                    String content;
                    if (event.getType() == DocumentEvent.EventType.REMOVE) {
                        content = previousText.substring(offset, offset + length);
                    } else {
                        content = document.getText(event.getOffset(), event.getLength());
                    }
                    String operation = type + "[" + offset + "," + "\"" + content + "\"]";
                    synchronized (lock) {
                        System.out.println(operation);
                        sendThread.setText(operation);
                        lock.notify();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
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
