package com.geekymax.client.thread;

import com.geekymax.client.ClientDocument;
import com.geekymax.operation.Operation;
import com.geekymax.ot.Changes;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Max Huang
 */
public class SendThread implements Runnable {
    private Socket socket;
    private ClientDocument document;
    private final Object lock;
    private ReentrantLock sendThreadReentrantLock;
    private Condition sendThreadCondition;
    private volatile Changes changes;
    private boolean endLoop = false;
    private volatile Queue<Changes> changesQueue = new LinkedList<>();

    public SendThread(Socket socket, Object lock) {
        this.socket = socket;
        this.lock = lock;
        this.document = ClientDocument.getInstance();
    }

    public SendThread(Socket socket, ReentrantLock sendThreadReentrantLock, Condition sendThreadCondition) {
        this.lock = new Object();
        this.socket = socket;
        this.sendThreadReentrantLock = sendThreadReentrantLock;
        this.sendThreadCondition = sendThreadCondition;
        this.document = ClientDocument.getInstance();

    }

    public synchronized void setChanges(Changes changes) {
        changesQueue.offer(changes);
        this.changes = changes;
        System.out.println("setChanges" + changes);
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
                if (changesQueue.size() > 1) {
                    System.out.print("");
                }
                while (!changesQueue.isEmpty()) {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    if (index != 0) {
                        System.out.println("wow,something happened");
                    }
                    Changes c = changesQueue.poll();
                    Operation operation = new Operation(0, document.getVersion(), c);
                    objectOutputStream.writeObject(operation);
                    objectOutputStream.flush();
                    System.out.println("send:" + c);
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
