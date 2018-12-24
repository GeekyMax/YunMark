package com.geekymax;

import com.geekymax.ot.*;
import com.geekymax.operation.Operation;

import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * @author Max Huang
 */
public class Document {
    private Text text;
    private static Document document;
    private Vector<Operation> operationVector;
    private Object lock;

    static {
        document = new Document();
    }

    private Document() {
        this.text = Text.wrap("This is the text in the server");
//        this.version = 0;
        this.operationVector = new Vector<>();
        this.lock = new Object();
    }

    public static Document getInstance() {
        return document;
    }


    public synchronized int getVersion() {
        return operationVector.size();
    }

    public Operation receiveOperation(Operation operation) throws Exception {
        if (operation.getVersion() < 0 || operation.getVersion() > operationVector.size()) {
            throw new Exception("operation revision not in history");

        }
        return handleServerOperation(operation);
    }

    private Operation handleServerOperation(Operation operation) throws Exception {
        synchronized (lock) {
            List<Operation> concurrentOperations = operationVector.subList(operation.getVersion(), operationVector.size());
            Changes newChangesForServer = operation.getTextChange();
            for (Operation beforeOperation : concurrentOperations) {
                Change change = (Transform.transform(newChangesForServer, beforeOperation.getTextChange()).getLeft());
                newChangesForServer = new Changes(change);
            }
            System.out.println("change for server" + newChangesForServer.toString());
            newChangesForServer.apply(0, this.text);
            Operation newOperation = new Operation(operation.getClientIndex(), operationVector.size(), newChangesForServer);
            operationVector.add(newOperation);
            System.out.println(operationVector.size() + ":" + text.toString());
            return newOperation;
        }
    }

    private Operation createOperation(int clientIndex, int version, String operationText) {
        Pattern pattern = compile("(\\D+)\\[(\\d+),\"(.+)\"]");
        Matcher matcher = pattern.matcher(operationText);
        if (matcher.find()) {
            String opText = matcher.group(1);
            int pos = Integer.parseInt(matcher.group(2));
            String text = matcher.group(3);
            Retain retain = new Retain(pos);
            TextChange textChange;
            if ("ins".equals(opText)) {
                textChange = new Insert(text);
            } else if ("del".equals(opText)) {
                textChange = new Delete(Integer.parseInt(text));
            } else {
                return null;
            }
            Changes changes = new Changes(retain, textChange);
            return new Operation(clientIndex, version, changes);
        } else {
            return null;
        }
    }


    public void setText(Text text) {
        this.text = text;
    }

    public Text getText() {
        return text;
    }
}
