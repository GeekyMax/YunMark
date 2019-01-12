package com.geekymax.server;

import com.geekymax.ot.*;
import com.geekymax.operation.Operation;

import java.util.List;
import java.util.Vector;

import static java.util.regex.Pattern.compile;

/**
 * this is the service for server document.
 * it provide some method to handle operations
 * @author Max Huang
 */
public class ServerDocumentService {
    private Text text;
    private static ServerDocumentService serverDocumentService;
    private Vector<Operation> operationVector;

    static {
        serverDocumentService = new ServerDocumentService();
    }

    private ServerDocumentService() {
        this.text = Text.empty();
        this.operationVector = new Vector<>();
    }

    public static ServerDocumentService getInstance() {
        return serverDocumentService;
    }


    public synchronized int getVersion() {
        return operationVector.size();
    }

    /**
     * call me when receive a new operation from the client
     * @param operation the received operation
     * @return the operation
     * @throws Exception
     */
    public synchronized Operation receiveOperation(Operation operation) throws Exception {
        if (operation.getVersion() < 0 || operation.getVersion() > operationVector.size()) {

            throw new Exception("operation revision not in history");

        }
        List<Operation> concurrentOperations = operationVector.subList(operation.getVersion(), operationVector.size());
        Changes newChangesForServer = operation.getTextChange();
        for (Operation beforeOperation : concurrentOperations) {
            AbstractChange change = (Transform.transform(newChangesForServer, beforeOperation.getTextChange()).getLeft());
            newChangesForServer = new Changes(change);
        }
        System.out.println("change for server" + newChangesForServer.toString());
        newChangesForServer.apply(0, this.text);
        Operation newOperation = new Operation(operation.getClientIndex(), operationVector.size(), newChangesForServer);
        // in this situation, this vector may raise ConcurrentModificationException
        operationVector.add(newOperation);
        System.out.println(operationVector.size() + ":" + text.toString());
        return newOperation;
    }


    public void setText(Text text) {
        this.text = text;
    }

    public Text getText() {
        return text;
    }
}
