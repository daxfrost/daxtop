package com.dax.list.item;

import java.awt.event.FocusListener;

/**
 * @author Dax Booysen
 */
public abstract class DaxListBaseItem extends javax.swing.JButton
{
    protected String _identifier;
    protected String _text;
    protected String _status;

    public DaxListBaseItem(String identifier, String text)
    {
        super(text);

        this._identifier = identifier;

        super.setFocusable(false);

        super.repaint();
    }
    
    /** Gets the actual text */
    public String getLabelText()
    {
        return _text;
    }
    
    /** Sets the actual text */
    public void setLabelText(String value)
    {
        _text = value;
    }

    /** Sets the status text */
    public void setStatusText(String status)
    {
        _status = status;
    }

    /** Gets the identifier for this item */
    public String getId()
    {
        return _identifier;
    }

    /** Sets the identifier for this item */
    public void setId(String id)
    {
        this._identifier = id;
    }
}
