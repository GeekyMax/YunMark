package com.geekymax.client;

import com.geekymax.client.gui.InputPane;
import com.geekymax.operation.Operation;
import com.geekymax.ot.Text;

import javax.swing.*;
import java.util.Vector;

/**
 *  this is the service for client document.
 *  it provide some method to handle operations
 *  <p>singleton class</p>
 * @author Max Huang
 */
public class ClientDocumentService {
    private static ClientDocumentService document;
    private Vector<Operation> operationVector;
    private Text text;
    private InputPane inputPane;
    private boolean isUpdating;
    private JTextArea inputTextArea;

    static {
        document = new ClientDocumentService();
    }

    private ClientDocumentService() {
        this.operationVector = new Vector<>();
        text = Text.empty();
        isUpdating = false;
        inputTextArea = InputPane.getInputTextArea();
    }


    public static ClientDocumentService getInstance() {
        return document;
    }

    /**
     * called when this client has a new operation
     * @param operation
     * @throws Exception
     */
    public synchronized void handleSelfOperation(Operation operation) throws Exception {
        operation.getTextChange().apply(0, text);
        operationVector.add(operation);
    }

    /**
     * receive the operation and update the local text and refresh the pane
     * @param operation
     * @throws Exception
     */
    public synchronized void receiveOperation(Operation operation) throws Exception {
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

    public int getVersion() {
        return operationVector.size();
    }

    public void setInputPane(InputPane inputPane) {
        this.inputPane = inputPane;
    }

    public boolean isUpdating() {
        return isUpdating;
    }

    public synchronized void setText(Text text) {
        this.text = text;
        isUpdating = true;
        inputTextArea.setText(text.toString());
        inputTextArea.getCaret().setDot(text.toString().length());
        isUpdating = false;
    }

    public String getText() {
        return text.toString();
    }
}
