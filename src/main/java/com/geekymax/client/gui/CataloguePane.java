package com.geekymax.client.gui;

import com.geekymax.client.pojo.CatalogueNodeInfo;
import com.geekymax.util.FileUtil;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;

/**
 * @author Max Huang
 */
public class CataloguePane {
    private final JScrollPane scrollPane = new JScrollPane();
    private JTree catalogueTree;
    private DefaultMutableTreeNode rootNode;
    private static CataloguePane cataloguePane = new CataloguePane();

    public static CataloguePane getInstance() {
        return cataloguePane;
    }

    private CataloguePane() {
        rootNode = new DefaultMutableTreeNode(new CatalogueNodeInfo(0, -1, "目录"));
        // 自定义结点渲染器
        DefaultTreeCellRenderer render = new DefaultTreeCellRenderer();
        Icon arrowDownIcon = new ImageIcon(FileUtil.getAbsolutePath("/image/arrow-down-16.png"));
        Icon arrowRightIcon = new ImageIcon(FileUtil.getAbsolutePath("/image/arrow-right-16.png"));
        Icon dotIcon = new ImageIcon(FileUtil.getAbsolutePath("/image/dot-16.png"));
        render.setClosedIcon(arrowRightIcon);
        render.setOpenIcon(arrowDownIcon);
        render.setLeafIcon(dotIcon);
        catalogueTree = new JTree(rootNode);
        catalogueTree.setCellRenderer(render);
        // 设置根节点可见
        catalogueTree.setRootVisible(true);
        catalogueTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {

                DefaultMutableTreeNode node = (DefaultMutableTreeNode) catalogueTree.getLastSelectedPathComponent();
                if (node == null) {
                    return;
                }
                CatalogueNodeInfo nodeInfo = (CatalogueNodeInfo) node.getUserObject();
                JScrollPane scrollPane = InputPane.getInstance().get();
                JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
                scrollBar.setValue(16 * nodeInfo.getLineNumber());
            }
        });
        scrollPane.getViewport().add(catalogueTree);
    }

    /**
     * 根据文本内容更新目录树
     *
     * @param text 文本内容
     */
    public synchronized void updateTree(String text) {
        String[] lines = text.split("\\n");
        rootNode.removeAllChildren();
        DefaultMutableTreeNode parentNode = rootNode;
        int parentLevel = 0;
        for (int i = 1; i <= lines.length; i++) {
            String line = lines[i - 1];
            DefaultMutableTreeNode newTreeNode = createTreeNode(i, line);
            if (newTreeNode == null) {
                continue;
            }
            int thisLevel = ((CatalogueNodeInfo) (newTreeNode.getUserObject())).getLevel();
            if (thisLevel > parentLevel) {
                parentNode.add(newTreeNode);
                newTreeNode.setParent(parentNode);

            } else {
                while (true) {
                    parentNode = (DefaultMutableTreeNode) parentNode.getParent();
                    parentLevel = ((CatalogueNodeInfo) (parentNode.getUserObject())).getLevel();
                    if (thisLevel > parentLevel) {
                        parentNode.add(newTreeNode);
                        newTreeNode.setParent(parentNode);
                        break;
                    }
                }
            }
            parentNode = newTreeNode;
            parentLevel = thisLevel;
        }
        catalogueTree.updateUI();
        expandAll(catalogueTree, new TreePath(rootNode), true);
    }

    /**
     * 从行内容中,建造一个树节点
     *
     * @param lineNumber 行号
     * @param text       文本内容
     * @return 树节点 如果非标题 返回null
     */
    private DefaultMutableTreeNode createTreeNode(int lineNumber, String text) {
        text = text.trim();
        Pattern pattern = compile("^(#+) +(.+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            int length = matcher.group(1).length();
            String title = matcher.group(2);
            return new DefaultMutableTreeNode(new CatalogueNodeInfo(length, lineNumber, title));

        } else {
            return null;
        }
    }

    private static void expandAll(JTree tree, TreePath parent, boolean expand) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }

    public JScrollPane get() {
        return scrollPane;
    }

}
