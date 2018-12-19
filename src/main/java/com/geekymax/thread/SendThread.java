package com.geekymax.thread;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;

public class SendThread implements Runnable {
    private Socket socket;


    public SendThread(Socket socket, Object lock) {
        this.socket = socket;
        this.lock = lock;
    }

    private final Object lock;
    private String text;
    private boolean endLoop = false;

    public void setText(String text) {
        this.text = text;
    }

    public void setEndLoop(boolean endLoop) {
        this.endLoop = endLoop;
    }

    @Override
    public void run() {
        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            while (!endLoop) {
                synchronized (lock) {
                    lock.wait();
                    System.out.println("send:" + text);
                    writer.write(text);
                    writer.flush();
                }
            }
            writer.flush();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
