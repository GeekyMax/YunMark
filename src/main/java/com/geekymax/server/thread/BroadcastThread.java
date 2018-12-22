package com.geekymax.server.thread;

import com.geekymax.operation.Operation;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 向各个客户端广播operation
 *
 * @author Max Huang
 */
public class BroadcastThread implements Runnable {
    private Object broadcastLock;
    private List<ClientThread> clientThreadList;
    private static BroadcastThread instance;
    private int excludeIndex;
    private Operation operation;
    private Queue<Operation> operationQueue = new LinkedList<>();

    static {
        instance = new BroadcastThread();
    }


    public void setExcludeIndex(int excludeIndex) {
        this.excludeIndex = excludeIndex;
    }

    public synchronized Object getBroadcastLock() {
        return broadcastLock;
    }

    public BroadcastThread init(Object broadcastLock, List<ClientThread> clientThreadList) {
        this.broadcastLock = broadcastLock;
        this.clientThreadList = clientThreadList;
        return this;
    }

    public static BroadcastThread getInstance() {
        return instance;
    }

    private BroadcastThread() {
    }

    @Override
    public void run() {
        System.out.println("b running");
        while (true) {
            try {
                synchronized (broadcastLock) {
                    broadcastLock.wait();
                    int index=0;
                    while (!operationQueue.isEmpty()) {
                        if (index!=0){
                            System.out.println("wow,something happened");
                        }
                        Operation o = operationQueue.poll();
                        System.out.println("broadcast: " + o);
                        clientThreadList.forEach(clientThread -> {
                            if (clientThread.getIndex() != excludeIndex) {
                                Socket socket = clientThread.getSocket();
                                if (socket.isClosed()) {
                                    return;
                                }
                                try {
                                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                                    objectOutputStream.writeObject(o);
                                    objectOutputStream.flush();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        index++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setOperation(Operation operation) {
        System.out.println("set operation:" + operation);
        this.operation = operation;
        operationQueue.offer(operation);
    }

    public void deleteSocket(int index) {

    }
}
