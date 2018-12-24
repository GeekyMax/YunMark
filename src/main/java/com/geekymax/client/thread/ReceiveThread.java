package com.geekymax.client.thread;

import com.geekymax.Document;
import com.geekymax.client.ClientDocument;
import com.geekymax.client.gui.CataloguePane;
import com.geekymax.client.gui.InputPane;
import com.geekymax.operation.Operation;
import com.geekymax.ot.Text;

import javax.naming.ldap.PagedResultsControl;
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
