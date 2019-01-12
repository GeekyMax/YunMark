package com.geekymax.client.thread;

import com.geekymax.client.ClientDocumentService;
import com.geekymax.operation.Operation;
import com.geekymax.ot.Changes;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * this is a thread which sends message to the server
 * @author Max Huang
 */
public class SendThread implements Runnable {
    private Socket socket;
    private ClientDocumentService document;
    private ReentrantLock sendThreadReentrantLock;
    private Condition sendThreadCondition;
    private boolean endLoop = false;
    private volatile Queue<Operation> operationQueue = new LinkedList<>();

    public SendThread(Socket socket, ReentrantLock sendThreadReentrantLock, Condition sendThreadCondition) {
        this.socket = socket;
        this.sendThreadReentrantLock = sendThreadReentrantLock;
        this.sendThreadCondition = sendThreadCondition;
        this.document = ClientDocumentService.getInstance();

    }

    public synchronized Operation addOperation(Changes changes) {
        Operation operation = new Operation(0, document.getVersion(), changes);
        operationQueue.offer(operation);
        System.out.println("add Operation: " + operationQueue);
        return operation;
    }

    public void setEndLoop(boolean endLoop) {
        this.endLoop = endLoop;
    }

    @Override
    public void run() {
        try {
            while (!endLoop) {
                sendThreadReentrantLock.lock();
                sendThreadCondition.await();
                int index = 0;
                if (operationQueue.size() > 1) {
                    System.out.print("");
                }
                while (!operationQueue.isEmpty()) {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    if (index != 0) {
                        System.out.println("wow,something happened");
                    }
                    Operation operation = operationQueue.poll();
                    objectOutputStream.writeObject(operation);
                    objectOutputStream.flush();
                    System.out.println("send:" + operation);
                    index++;
                }
                sendThreadReentrantLock.unlock();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
