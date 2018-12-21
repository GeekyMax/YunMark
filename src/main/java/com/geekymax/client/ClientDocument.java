package com.geekymax.client;

import com.geekymax.Document;
import com.geekymax.operation.Operation;
import com.geekymax.ot.Text;

import javax.swing.*;
import java.util.Vector;

/**
 * @author Max Huang
 */
public class ClientDocument {
    private static ClientDocument document;
    private Vector<Operation> operationVector;
    private Object lock;
    private Text text;
    private JTextArea inputTextArea;
    private boolean isUpdating;
    static {
        document = new ClientDocument();
    }

    private ClientDocument() {
        this.operationVector = new Vector<>();
        this.lock = new Object();
        text = Text.empty();
        isUpdating = false;
    }

    public void handleSelfOperation(Operation operation) throws Exception {
        synchronized (lock) {
            operation.getTextChange().apply(0, text);
            operationVector.add(operation);
        }
    }

    public void receiveOperation(Operation operation) throws Exception {
        synchronized (lock) {
            operation.getTextChange().apply(0, text);
            operationVector.add(operation);
            isUpdating=true;
            inputTextArea.setText(text.toString());
            isUpdating=false;
        }

    }

    public static ClientDocument getInstance() {
        return document;
    }

    public int getVersion() {
        return operationVector.size();
    }

    public void setInputTextArea(JTextArea inputTextArea) {
        this.inputTextArea = inputTextArea;
    }

    public boolean isUpdating() {
        return isUpdating;
    }
}
