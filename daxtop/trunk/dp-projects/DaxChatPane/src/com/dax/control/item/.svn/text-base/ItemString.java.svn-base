package com.dax.control.item;

import java.awt.Color;
import java.awt.Font;

/**
 * @author Dax Booysen
 * 
 * The String item used for the chat control.
 */
public class ItemString extends Item
{
    public String text = "";
    
    public Font font;
    
    public Color color;

    private boolean _underlined = false;
    
    /** Contructs a new ItemString */
    public ItemString(String t)
    {
        text = t;
        font = new Font("Arial", Font.PLAIN, 12);
        color = new Color(0);
    }
    
    /** Contructs a new ItemString */
    public ItemString(String t, Font f,  Color c, boolean underlined)
    {
        text = t;
        font = new Font(f.getFamily(), f.getStyle(), f.getSize());
        color = new Color(c.getRGB());
    }
    
    /** Gets whether this string is underlined */
    public boolean isUnderlined()
    {
        return _underlined;
    }
    
    /** Sets the underline state of the text */
    public void setUnderlined(boolean u)
    {
        _underlined = u;
    }
}
