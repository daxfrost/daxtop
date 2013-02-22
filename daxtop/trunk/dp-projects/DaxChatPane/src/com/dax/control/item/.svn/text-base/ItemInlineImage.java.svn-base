package com.dax.control.item;

import java.awt.Image;

/**
 * @author Dax Booysen
 * 
 * the inline image item used for the chat control
 */
public class ItemInlineImage extends Item
{
    public Object data = null;
    public String url = null;
    public Image img = null;
    /** 0 = none, 1 = left, 2 = middle and 4 = right */
    public int alignment = 0;
    /** 1 = inline, 2 wrap around, 3 on its own line */
    public int flow = 1;

    /** Contructs a new ItemInlineImage */
    public ItemInlineImage(Object data, Image img, String align, String flow)
    {
        this.data = data;
        this.img = img;
        if (align!=null)
            this.alignment = Integer.parseInt(align);
        if (flow!=null)
            this.flow = Integer.parseInt(flow);
    }

    /** Contructs a new ItemInlineImage */
    public ItemInlineImage(Object data, String url)
    {
        this.data = data;
        this.url = url;
    }
}
