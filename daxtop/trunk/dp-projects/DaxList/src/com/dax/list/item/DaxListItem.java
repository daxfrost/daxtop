package com.dax.list.item;

import com.dax.list.item.state.MessageState;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

/**
 * @author Dax Booysen
 * 
 */
public class DaxListItem extends DaxListBaseItem
{
    /** Only used for messages */
    public MessageState messageState = MessageState.CLEAR;
    /** unread message icon */
    public static ImageIcon unreadMessageIcon = null;
    /** read message icon */
    public static ImageIcon readMessageIcon = null;
    /** Use the status icon */
    private static boolean useStatus = false;
    /** Only used for status */
    public Image statusIcon = null;
    /** Only used for mood */
    public Image moodIcon = null;

    /** Constructs a new list item */
    public DaxListItem()
    {
        super("","");

        _text = "";
        _status = "";
        super.setVerticalAlignment(SwingConstants.CENTER);
        super.setHorizontalAlignment(SwingConstants.LEFT);
    }

    /** Constructs a new list item with text */
    public DaxListItem(String id, String text)
    {
        super(id, text);
        _text = text;
        _status = "";
        super.setVerticalAlignment(SwingConstants.CENTER);
        super.setHorizontalAlignment(SwingConstants.LEFT);
    }

    /** Constructs a new list item with text and an icon */
    public DaxListItem(String id, String text, ImageIcon icon, String status)
    {
        super(id, "<html>" + text + "<br/><font color=\"#777777\">" + status + "</font></html>");
        //super.setText("<html>" + text + "<br/><font color=\"#777777\">" + status + "</font></html>");
        super.setPreferredSize(new Dimension(this.getWidth(), icon.getIconHeight()+5));
        _text = text;
        _status = status;
        setIcon(icon);
        super.setVerticalAlignment(SwingConstants.CENTER);
        super.setHorizontalAlignment(SwingConstants.LEFT);
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);

        int fromRight = this.getWidth() - 4;
        int fromTop = 0;

        if (useStatus)
        {
            fromRight -= (statusIcon.getWidth(null) + 8);
            fromTop = (this.getHeight() - (statusIcon.getHeight(null))) / 2;
            g.drawImage(statusIcon, fromRight, fromTop, statusIcon.getWidth(null), statusIcon.getHeight(null), this);
        }

        // if a mood icon need be appended
        if (moodIcon != null)
        {
            fromRight -= moodIcon.getWidth(null) + 8;
            fromTop = (this.getHeight() - (moodIcon.getHeight(null))) / 2;
            g.drawImage(moodIcon, fromRight, fromTop, moodIcon.getWidth(null), moodIcon.getHeight(null), this);
        }

        // if a message icon need be appended
        if (messageState != MessageState.CLEAR)
        {
            Image messageIcon = null;

            if (messageState == MessageState.UNREAD)
            {
                messageIcon = unreadMessageIcon.getImage();
            }
            else
            {
                messageIcon = readMessageIcon.getImage();
            }

            fromRight -= messageIcon.getWidth(null) + 8;
            fromTop = (this.getHeight() - (messageIcon.getHeight(null))) / 2;
            g.drawImage(messageIcon, fromRight, fromTop, messageIcon.getWidth(null), messageIcon.getHeight(null), this);
        }

        // make sure status text doesnt wrap
        FontMetrics fm = g.getFontMetrics(g.getFont());
        int sLen = _status.length() + 1;
        int statusLen = 0;
        do
        {
            sLen--;
            statusLen = fm.stringWidth(_status.substring(0, sLen)) + getIcon().getIconWidth() + (getWidth() - fromRight);
        }
        while (statusLen > getWidth());

        String status = "";

        // if ... need be added
        if (sLen < _status.length())
        {
            status = _status.substring(0, sLen - 4) + "...";
        }
        else
        {
            status = _status;
        }

        sLen = _text.length() + 1;
        statusLen = 0;
        do
        {
            sLen--;
            statusLen = fm.stringWidth(_text.substring(0, sLen)) + getIcon().getIconWidth() + (getWidth() - fromRight);
        }
        while (statusLen > getWidth());

        String useText = "";

        if (sLen < _text.length())
        {
            useText = _text.substring(0, sLen - 4) + "...";
        }
        else
        {
            useText = _text;
        }

        //if (super.getText().length() != ("<html>" + _text + "<br/><font color=\"#777777\">" + status + "</font></html>").length())
        {
            if (getText().length() != ("<html>" + useText + "<br/><font color=\"#777777\">" + status + "</font></html>").length())
            {
                String escapedText = useText.replace("<", "&lt;");
                escapedText = escapedText.replace(">", "&gt;");

                setText("<html>" + escapedText + "<br/><font color=\"#777777\">" + status + "</font></html>");
            }
            //setText("<html>" + _text + "<br/><font color=\"#777777\">" + status + "</font></html>");//super.setText("<html><font color=\"" + GetHexStringFromColor(foreColor) + "\">" + _text + "</font><br/><font color=\"#777777\">" + status + "</font></html>");
        }
    }

    @Override
    public void setIcon(Icon defaultIcon)
    {
        super.setPreferredSize(new Dimension(this.getWidth(), defaultIcon.getIconHeight()+5));
        super.setIcon(defaultIcon);
    }

    /** Sets whether all DaxListItem's should use the status icon */
    public static void setUseStatus(boolean state)
    {
        useStatus = state;
    }
}
