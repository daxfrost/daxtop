package com.dax.control.item;

import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JProgressBar;

/**
 * @author Dax Frost
 * 
 * the loading bar item used for the chat control
 */
public class ItemLoadingBar extends Item
{
    /** The progress bar */
    public JProgressBar progressBar = null;

    /** The open button */
    public JButton btnOpen = null;

    /** The save button */
    public JButton btnSave = null;

    /** Contructs a new Progress Bar */
    public ItemLoadingBar(int maxValue, String btnOpenText, String btnSaveText)
    {
        progressBar = new JProgressBar(0, maxValue);
        progressBar.setPreferredSize(new Dimension(100, 25));
        progressBar.setStringPainted(true);
        btnOpen = new JButton(btnOpenText);
        btnOpen.setPreferredSize(new Dimension(50, 25));
        btnOpen.setFocusable(false);
        btnSave = new JButton(btnSaveText);
        btnSave.setPreferredSize(new Dimension(50, 25));
        btnSave.setFocusable(false);
    }
}
