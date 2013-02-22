/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dax.lib.components;

/**
 *
 * @author Dax Booysen
 */
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;

/**
 * An extension of JLabel which looks like a link and responds appropriately
 * when clicked. Note that this class will only work with Swing 1.1.1 and later.
 * Note that because of the way this class is implemented, getText() will not
 * return correct values, user <code>getNormalText</code> instead.
 */
public class JLinkLabel extends JLabel
{

    /**
     * The normal text set by the user.
     */
    private String text;

    /** The button to decide the new color scheme */
    private static JButton b = new JButton();

    /**
     * Creates a new LinkLabel with the given text.
     */
    public JLinkLabel(String text)
    {
        setText(text, b.getForeground());

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        enableEvents(MouseEvent.MOUSE_EVENT_MASK);
    }

    /**
     * Sets the text of the label.
     */
    public void setText(String text, Color c)
    {
        // #0000CF - default
        setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        this.setText("<html><font color=\"#" + Integer.toHexString(c.getRGB()).substring(2) + "\">" + text + "</font></html>"); //$NON-NLS-1$ //$NON-NLS-2$
        this.text = text;
    }

    /**
     * Returns the text set by the user.
     */
    public String getNormalText()
    {
        return text;
    }

    /**
     * Processes mouse events and responds to clicks.
     */
    @Override
    protected void processMouseEvent(MouseEvent evt)
    {
        super.processMouseEvent(evt);

        if (evt.getID() == MouseEvent.MOUSE_PRESSED)
        {
            setBorder(null);

            // change color
            setText(text, b.getBackground());

            fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getNormalText()));
        }
        else if (evt.getID() == MouseEvent.MOUSE_ENTERED)
        {
            setBorder(BorderFactory.createLineBorder(b.getForeground()));
        }
        else
        {
            setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        }
    }

    /**
     * Adds an ActionListener to the list of listeners receiving notifications
     * when the label is clicked.
     */
    public void addActionListener(ActionListener listener)
    {
        listenerList.add(ActionListener.class, listener);
    }

    /**
     * Removes the given ActionListener from the list of listeners receiving
     * notifications when the label is clicked.
     */
    public void removeActionListener(ActionListener listener)
    {
        listenerList.remove(ActionListener.class, listener);
    }

    /**
     * Fires an ActionEvent to all interested listeners.
     */
    protected void fireActionPerformed(ActionEvent evt)
    {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2)
        {
            if (listeners[i] == ActionListener.class)
            {
                ActionListener listener = (ActionListener) listeners[i + 1];
                listener.actionPerformed(evt);
            }
        }
    }

    @Override
    public void updateUI() {

        b = new JButton();

        if (this.getForeground() != null)
            setText(text, this.getForeground());
        super.updateUI();
    }
}
