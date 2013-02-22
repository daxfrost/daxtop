package com.dax.control.item;

/**
 * @author Dax Booysen
 * 
 * the clear screen item used for the chat control
 */
public class ItemCommandClearScreen extends Item
{
    public Object data = null;
    public boolean auto = false;

    /** Contructs a new ItemEmoticon */
    public ItemCommandClearScreen(Object data, boolean auto)
    {
        this.data = data;
        this.auto = auto;
    }
}
