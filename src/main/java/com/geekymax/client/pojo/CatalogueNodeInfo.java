package com.geekymax.client.pojo;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 目录树结点信息
 * @author Max Huang
 */
public class CatalogueNodeInfo {
    private int level;
    private int lineNumber;
    private String title;

    public CatalogueNodeInfo(int level, int lineNumber, String title) {
        this.level = level;
        this.lineNumber = lineNumber;
        this.title = title;
    }

    public int getLevel() {
        return level;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return title;
    }
}
