package com.geekymax.server;

import com.geekymax.server.thread.BroadcastThread;
import com.geekymax.server.thread.ClientThread;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 服务器程序启动入口
 * @author Max Huang
 */
public class ServerApp {
    public static Object broadcastLock = new Object();

    public static void main(String[] args) {
        List<ClientThread> clientThreadList = new ArrayList<>();
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10), threadFactory);
        BroadcastThread broadcastThread = BroadcastThread.getInstance().init(broadcastLock,clientThreadList);
        threadPoolExecutor.execute(broadcastThread);
        threadPoolExecutor.execute(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(9999);
                int index = 0;
                while (true) {
                    System.out.println("waiting!");
                    Socket socket = serverSocket.accept();
                    System.out.println("connected!");
                    ClientThread clientThread = new ClientThread(socket, index,broadcastLock);
                    clientThreadList.add(clientThread);
                    threadPoolExecutor.execute(clientThread);
                    index++;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
