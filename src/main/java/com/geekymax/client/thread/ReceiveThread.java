package com.geekymax.client.thread;

import com.geekymax.client.ClientDocumentService;
import com.geekymax.client.gui.CataloguePane;
import com.geekymax.operation.Operation;
import com.geekymax.ot.Text;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;


/**
 * @author Max Huang
 */
public class ReceiveThread implements Runnable {
    private Socket socket;
    private ClientDocumentService document;

    public ReceiveThread(Socket socket) {
        this.socket = socket;
        this.document = ClientDocumentService.getInstance();
    }

    @Override
    public void run() {
        try {
            // 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
            InputStream inputStream = socket.getInputStream();
            //只有当客户端关闭它的输出流的时候，服务端才能取得结尾的-1
            while (true) {
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                Object object = objectInputStream.readObject();
                if (object instanceof Operation) {
                    Operation operation = (Operation) object;
                    System.out.println("receive:" + operation);
                    document.receiveOperation(operation);
                    try {
                        CataloguePane.getInstance().updateTree(document.getText());
                    } catch (Exception e) {
                        System.out.println("error here1");
                        e.printStackTrace();
                    }
                } else if (object instanceof String) {
                    String text = (String) object;
                    document.setText(Text.wrap(text));
                    try {
                        CataloguePane.getInstance().updateTree(document.getText());
                    } catch (Exception e) {
                        System.out.println("error here2");
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
