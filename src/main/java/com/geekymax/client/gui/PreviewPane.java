package com.geekymax.client.gui;

import com.geekymax.util.FileUtil;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

/**
 * 预览面板
 * xxx Observer 已过时, 改用javafx
 *
 * @author Max Huang
 */
public final class PreviewPane implements Observer {
    private final JScrollPane previewPane = new JScrollPane();
    private JEditorPane jEditorPane = new JEditorPane();
    private final String template = "<body class=\"markdown-body\">%s</body></html>";
    private static PreviewPane instance = new PreviewPane();

    public static PreviewPane getInstance() {
        return instance;
    }

    /**
     * Creates the HTML JLabel and sets its vertical alignment as in the top.
     */
    private PreviewPane() {
        jEditorPane.setContentType("text/html");
        jEditorPane.setEditable(false);
        jEditorPane.setContentType("text/html");
        HTMLEditorKit kit = new HTMLEditorKit();

        jEditorPane.setEditorKit(kit);
        try {
            StyleSheet styleSheet = kit.getStyleSheet();
            File file = new File(FileUtil.getAbsolutePath("/assets/github-markdown.css"));
            styleSheet.importStyleSheet(file.toURI().toURL());
            kit.setStyleSheet(styleSheet);
            jEditorPane.setEditorKit(kit);
        } catch (Exception e) {
            e.printStackTrace();
        }
        previewPane.getViewport().add(jEditorPane, null);
    }

    /**
     * Returns the JScrollPane object.
     *
     * @return the JScrollPane object.
     */
    public JScrollPane get() {
        return previewPane;
    }

    /**
     * Updates the content of the label by converting the input data to html and setting them to the label.
     * <p>
     * This method will be called by an {@code InputPane} observable.
     * </p>
     *
     * @param o    the observable element which will notify this class.
     * @param data a String object containing the input data to be converted into HTML.
     */
    @Override
    public void update(final Observable o, final Object data) {
        if (o instanceof InputPane) {
            SwingUtilities.invokeLater(() -> {
                String content = (String) data;
                Parser parser = Parser.builder().build();
                Node document = parser.parse(content);
                HtmlRenderer renderer = HtmlRenderer.builder().build();
                jEditorPane.setText(String.format(template, renderer.render(document)).replaceAll("src=\"", "src=\"file:"));
            });
        }
    }
}
