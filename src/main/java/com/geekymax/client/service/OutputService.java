package com.geekymax.client.service;

import com.geekymax.client.ClientDocument;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * @author Max Huang
 */
public class OutputService {
    private static OutputService instance = new OutputService();
    private ClientDocument clientDocument = ClientDocument.getInstance();
    private final String template = "<!DOCTYPE html>\n" +
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

    public static OutputService getInstance() {
        return instance;
    }

    private OutputService() {

    }

    public void outputAsMarkdown(File file) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file));
            outputStreamWriter.write(clientDocument.getText());
            outputStreamWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void outputAsHtml(File file) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file));
            Parser parser = Parser.builder().build();
            Node document = parser.parse(clientDocument.getText());
            HtmlRenderer renderer = HtmlRenderer.builder().build();
            outputStreamWriter.write(String.format(template, renderer.render(document)));
            outputStreamWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
