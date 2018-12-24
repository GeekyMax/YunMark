package com.geekymax.client.gui;


import com.geekymax.client.ClientDocument;
import com.geekymax.client.thread.ReceiveThread;
import com.geekymax.client.thread.SendThread;
import com.geekymax.operation.Operation;
import com.geekymax.ot.Changes;
import com.geekymax.ot.Delete;
import com.geekymax.ot.Insert;
import com.geekymax.ot.Retain;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.Socket;
import java.util.Observable;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Max Huang
 */
public final class InputPane extends Observable {
    private final JScrollPane scrollPane = new JScrollPane();
    private static final JTextArea inputTextArea = new JTextArea();
    private ReentrantLock sendThreadReentrantlock;
    private Condition sendThreadCondition;
    private SendThread sendThread;
    private ReceiveThread receiveThread;
    private static InputPane inputPane;

    static {
        inputPane = new InputPane();
    }

    public static synchronized JTextArea getInputTextArea() {
        return inputTextArea;
    }

    public static InputPane getInstance() {
        return inputPane;
    }

    /**
     * Creates the text area and add a key listener to call observer every time a key is released.
     */
    private InputPane() {
        sendThreadReentrantlock = new ReentrantLock();
        sendThreadCondition = sendThreadReentrantlock.newCondition();
        scrollPane.getViewport().add(inputTextArea, null);
        ClientDocument.getInstance().setInputPane(this);
        inputTextArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        try {
            Socket socket = new Socket("127.0.0.1", 9999);
            sendThread = new SendThread(socket, sendThreadReentrantlock, sendThreadCondition);
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
                updatePreview();
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

            protected synchronized void changeFilter(DocumentEvent event) {

                if (ClientDocument.getInstance().isUpdating()) {
                    System.out.println("is updating");
                    return;
                }
                // todo 未解决的一个NullPointerException
                try {
                    CataloguePane.getInstance().updateTree(event.getDocument().getText(0, event.getDocument().getLength()));
                } catch (Exception e) {
                    System.out.println("error here3");
                    e.printStackTrace();
                }
                javax.swing.text.Document document = event.getDocument();
                try {
                    int offset = event.getOffset();
                    int length = event.getLength();
                    Changes changes;
                    if (event.getType() == DocumentEvent.EventType.INSERT) {
                        changes = new Changes(new Retain(offset), new Insert(document.getText(event.getOffset(), event.getLength())));
                    } else {
                        changes = new Changes(new Retain(offset), new Delete(length));
                    }
                    System.out.println("input: " + changes);
                    Operation operation = new Operation(0, ClientDocument.getInstance().getVersion(), changes);
                    ClientDocument.getInstance().handleSelfOperation(operation);
                    sendThreadReentrantlock.lock();
                    sendThread.setChanges(changes);
                    sendThreadCondition.signal();
                    sendThreadReentrantlock.unlock();
                } catch (
                        Exception ex) {
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
        return scrollPane;
    }

    public synchronized void updatePreview() {
        setChanged();
        notifyObservers(inputTextArea.getText());
    }


}
