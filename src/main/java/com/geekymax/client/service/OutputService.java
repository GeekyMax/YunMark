package com.geekymax.client.service;

import com.geekymax.client.ClientDocumentService;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * 用于提供文档输出功能的单例类
 *
 * @author Max Huang
 */
public class OutputService {
    private static OutputService instance = new OutputService();
    private ClientDocumentService clientDocument = ClientDocumentService.getInstance();

    /**
     * 获得单例
     *
     * @return singleton instance
     */
    public static OutputService getInstance() {
        return instance;
    }

    private OutputService() {

    }

    /**
     * 将document中的文本内容以Markdown文件格式输出到指定文件
     *
     * @param file 想要输出的文件
     */
    public void outputAsMarkdown(File file) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file));
            outputStreamWriter.write(clientDocument.getText());
            outputStreamWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将document中的文本内容以HTML文件格式输出到指定文件
     *
     * @param file 想要输出的文件
     */
    public void outputAsHtml(File file) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file));
            Parser parser = Parser.builder().build();
            Node document = parser.parse(clientDocument.getText());
            HtmlRenderer renderer = HtmlRenderer.builder().build();
            String template = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title>Title</title>\n" +
                    "    <link href=\"https://cdn.bootcss.com/github-markdown-css/2.10.0/github-markdown.css\" rel=\"stylesheet\">\n" +
                    "    <!--<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/9.13.1/styles/tomorrow.min.css\">-->\n" +
                    "    <!--<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/9.13.1/styles/github.min.css\">-->\n" +
                    "    <link rel=\"stylesheet\"\n" +
                    "          href=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/9.13.1/styles/atom-one-light.min.css\">\n" +
                    "    <script src=\"//cdnjs.cloudflare.com/ajax/libs/highlight.js/9.13.1/highlight.min.js\"></script>\n" +
                    "    <script>hljs.initHighlightingOnLoad();</script>\n" +
                    "</head>\n" +
                    "<body class=\"markdown-body\">" +
                    "%s" +
                    "</body>" +
                    "</html>";
            outputStreamWriter.write(String.format(template, renderer.render(document)));
            outputStreamWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
