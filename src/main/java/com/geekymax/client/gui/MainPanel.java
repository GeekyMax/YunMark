package com.geekymax.client.gui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;

/**
 * @author Max Huang
 */
public final class MainPanel {
    private final MigLayout layout = new MigLayout(
            // Layout constraints
            "",
            // Column constraints
            "[fill,20%] 10 [fill, 40%] 10 [fill,40%]",
            // Row constraints
            "[grow,fill]");
    private final JPanel mainPanel = new JPanel(layout);

    private final InputPane input = InputPane.getInstance();
    private final PreviewPane preview = new PreviewPane();

    /**
     * Creates the main panel, adding observer to the input and building the GUI.
     */
    public MainPanel() {
        // Add observer
        input.addObserver(preview);

        // Build GUI
        mainPanel.add(CataloguePane.getInstance().get());
        mainPanel.add(input.get());
        mainPanel.add(preview.get());
    }

    /**
     * Returns the JPanel object.
     *
     * @return the JPanel object.
     */
    public JPanel get() {
        return mainPanel;
    }
}