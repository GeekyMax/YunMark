package com.geekymax.thread;

import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @author Max Huang
 */
public class ReceiveThread implements Runnable {
    private Socket socket;

    public ReceiveThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            // 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
            InputStream inputStream = socket.getInputStream();
            byte[] bytes = new byte[1024];
            int len;
            //只有当客户端关闭它的输出流的时候，服务端才能取得结尾的-1
            while ((len = inputStream.read(bytes)) != -1) {
                // 注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
                String text = new String(bytes, 0, len, StandardCharsets.UTF_8);
                System.out.println("receive:" + text);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
