package com.dax.control.item;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Dax Booysen
 * 
 * The emoticon item used for the chat control. This Item type can be amongst
 * and around text items, embedded or standing alone in any chat control.
 */
public class ItemCommand extends Item implements ActionListener
{
    public Object data = null;
    public String replyMsg = "";
    public String selMsg = "";
    public String dest = null;

    ActionListener aListener;

    /** Contructs a new ItemEmoticon */
    public ItemCommand(Object data, String replyMsg, String selMsg, ActionListener al)
    {
        this.data = data;
        this.replyMsg = replyMsg;
        this.selMsg = selMsg;
        this.aListener = al;
    }

    /** Contructs a new ItemEmoticon */
    public ItemCommand(Object data, String replyMsg, String selMsg, ActionListener al, String dest)
    {
        this.data = data;
        this.replyMsg = replyMsg;
        this.selMsg = selMsg;
        this.aListener = al;
        this.dest = dest;
    }

    /** Item was clicked, drop event to chat screen */
    public void actionPerformed(ActionEvent e)
    {
        e.setSource(this);
        aListener.actionPerformed(e);
    }
}
