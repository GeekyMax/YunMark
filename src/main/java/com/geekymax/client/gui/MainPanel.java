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

    /**
     * Creates the main panel, adding observer to the input and building the GUI.
     */
    public MainPanel() {
        // Add observer

        // Build GUI
        InputPane input = InputPane.getInstance();
        PreviewPane preview = PreviewPane.getInstance();
        CataloguePane cataloguePane = CataloguePane.getInstance();
        input.addObserver(preview);
        mainPanel.add(cataloguePane.get());
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