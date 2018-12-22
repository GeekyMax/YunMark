package com.geekymax.client;

import com.geekymax.Document;
import com.geekymax.client.gui.InputPane;
import com.geekymax.operation.Operation;
import com.geekymax.ot.Changes;
import com.geekymax.ot.Text;

import javax.swing.*;
import java.util.Vector;

/**
 * @author Max Huang
 */
public class ClientDocument {
    private static ClientDocument document;
    private Vector<Operation> operationVector;
    private final Object lock;
    private Text text;
    private InputPane inputPane;
    private boolean isUpdating;
    private JTextArea inputTextArea;

    static {
        document = new ClientDocument();
    }

    private ClientDocument() {
        this.operationVector = new Vector<>();
        this.lock = new Object();
        text = Text.empty();
        isUpdating = false;
        inputTextArea = InputPane.getInputTextArea();
    }

    public void handleSelfOperation(Operation operation) throws Exception {
        synchronized (lock) {
            operation.getTextChange().apply(0, text);
            operationVector.add(operation);
        }
    }

    public void receiveOperation(Operation operation) throws Exception {
        synchronized (lock) {
            int nowCaretDot = inputTextArea.getCaret().getDot();
            int newCaretDot = operation.getTextChange().calculateCaret(nowCaretDot);
            operation.getTextChange().apply(0, text);
            operationVector.add(operation);
            isUpdating = true;
            inputTextArea.setText(text.toString());
            inputTextArea.getCaret().setDot(newCaretDot);
            inputPane.updatePreview();
            isUpdating = false;
        }

    }

    public static ClientDocument getInstance() {
        return document;
    }

    public int getVersion() {
        return operationVector.size();
    }

    public void setInputPane(InputPane inputPane) {
        this.inputPane = inputPane;
    }

    public boolean isUpdating() {
        return isUpdating;
    }
}
