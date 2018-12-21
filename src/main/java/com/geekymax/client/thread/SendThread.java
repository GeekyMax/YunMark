package com.geekymax.client.thread;

import com.geekymax.client.ClientDocument;
import com.geekymax.operation.Operation;
import com.geekymax.ot.Changes;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Max Huang
 */
public class SendThread implements Runnable {
    private Socket socket;
    private ClientDocument document;
    private final Object lock;
    private Changes changes;
    private boolean endLoop = false;
    private Queue<Changes> changesQueue = new LinkedList<>();

    public SendThread(Socket socket, Object lock) {
        this.socket = socket;
        this.lock = lock;
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

                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                synchronized (lock) {
                    lock.wait();
                    int index = 0;
                    while (!changesQueue.isEmpty()) {
                        if (index != 0) {
                            System.out.println("wow,something happened");
                        }
                        System.out.println("send:" + changes);
                        Changes c = changesQueue.poll();
                        Operation operation = new Operation(0, document.getVersion(), c);
                        objectOutputStream.writeObject(operation);
                        objectOutputStream.flush();
                        index++;
                    }
                }
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
