package com.geekymax.server.thread;


import com.geekymax.Document;
import com.geekymax.operation.Operation;

import java.io.*;
import java.net.Socket;


/**
 * 用于接受客户端发送的operation的线程
 *
 * @author Max Huang
 */
public class ClientThread implements Runnable {
    private Socket socket;
    private int index;
    private Document document;
    private ObjectOutputStream objectOutputStream;
    private Object broadcastLock;

    public ClientThread(Socket socket, int index, Object broadcastLock) {
        this.socket = socket;
        this.index = index;
        document = Document.getInstance();
        this.broadcastLock = broadcastLock;
    }

    @Override
    public void run() {
        try {
            System.out.println("client " + index + " is online!");
            // 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
            InputStream inputStream = socket.getInputStream();
            while (true) {
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                Operation operation = (Operation) objectInputStream.readObject();
                System.out.println("receive: " + operation);
                if (operation == null) {
                    break;
                }
                // 消息广播
                BroadcastThread broadcastThread = BroadcastThread.getInstance();
                synchronized (broadcastLock) {
                    broadcastThread.setOperation(operation);
                    broadcastThread.setExcludeIndex(index);
                    broadcastLock.notify();
                }
            }
            inputStream.close();
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

    public int getIndex() {
        return index;
    }

    public Socket getSocket() {
        return socket;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }
}
