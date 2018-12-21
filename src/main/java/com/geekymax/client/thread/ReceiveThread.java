package com.geekymax.client.thread;

import com.geekymax.Document;
import com.geekymax.client.ClientDocument;
import com.geekymax.operation.Operation;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


/**
 * @author Max Huang
 */
public class ReceiveThread implements Runnable {
    private Socket socket;
    private ClientDocument document;

    public ReceiveThread(Socket socket) {
        this.socket = socket;
        this.document = ClientDocument.getInstance();
    }

    @Override
    public void run() {
        try {
            // 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
            InputStream inputStream = socket.getInputStream();
            //只有当客户端关闭它的输出流的时候，服务端才能取得结尾的-1
            while (true) {
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                Operation operation = (Operation) objectInputStream.readObject();
                System.out.println("receive:" + operation);
                document.receiveOperation(operation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
