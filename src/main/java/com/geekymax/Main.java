package com.geekymax;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import com.github.rjeschke.txtmark.Processor;
import org.markdownj.MarkdownProcessor;

public class Main {
    public static void main(String[] args) {
        File file = new File("C:/Code/我的文档库/我的文档/test.md");
        try {

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Scanner scanner = new Scanner(bufferedReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            Parser parser = Parser.builder().build();
            Node document = parser.parse(stringBuilder.toString());
            HtmlRenderer renderer = HtmlRenderer.builder().build();
            String result = renderer.render(document);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
