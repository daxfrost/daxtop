package com.dax.list;

import com.dax.interfaces.ListItemListener;
import com.dax.list.item.DaxListBaseItem;
import com.dax.list.item.DaxListGroupItem;
import com.dax.list.item.DaxListItem;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 * @author Dax Booysen
 */
public class DaxDynamicList extends JPanel implements MouseListener, ComponentListener
{
    /** The current list data */
    HashMap<String, DaxListBaseItem> items = new HashMap<String, DaxListBaseItem>();
    /** List of list item listeners */
    private ArrayList<ListItemListener> listItemListeners = new ArrayList<ListItemListener>();

    /** Tells the overall control that an update is in progress */
    public boolean updating = false;
    
    /** Constructs a new DaxDynamicList */
    public DaxDynamicList()
    {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        this.setDoubleBuffered(true);
        
        addComponentListener(this);
    }

    /** Adds a new item to the list */
    public void AppendItem(DaxListBaseItem item)
    {
        items.put(item.getId(), item);
        this.add(item);

        // add mouse listener
        
        //item.removeMouseListener(this);
        //if(item.getMouseListeners().length == 0)
        //    item.addMouseListener(this);
    }
    
    @Override
    public Component add(Component comp, int index)
    {
        // add mouse listener
        //comp.addMouseListener(this);
        
        DaxListItem item = (DaxListItem)comp;
        
        items.put(item.getId(), item);
        
        return super.add(comp, index);
    }

    /** Clears all the items in the list */
    public void ClearItems()
    {
        items.clear();
        this.removeAll();
    }

    /** Clears all groups specifically */
    public void ClearGroups()
    {
        for(Component item : getComponents())
        {
            if(item instanceof DaxListGroupItem)
                this.remove(item);
        }
    }

    /** Returns whether or not there are components in this control */
    public boolean isEmpty()
    {
        return items.isEmpty();
    }
    
    /** Returns the specified item in the list */
    public DaxListBaseItem GetItem(String id)
    {
        return items.get(id);
    }

    /** Adds a new ListItemListener to this component */
    public void addListItemListener(ListItemListener l)
    {
        listItemListeners.add(l);
    }
    
    @Override
    public void paint(Graphics g)
    {
        if(items.size() > 0)
        {
            // size correctly
            for (Component c : getComponents())
            {
                //c.setPreferredSize(new Dimension((int)this.getVisibleRect().getWidth(), c.getHeight()));
                //c.setMinimumSize(new Dimension((int)this.getVisibleRect().getWidth(), c.getHeight()));
                //c.setMaximumSize(new Dimension((int)this.getVisibleRect().getWidth(), c.getHeight()));
                c.setSize(new Dimension((int)this.getVisibleRect().getWidth(), c.getHeight()));
            }
        }
        
        // do actual paitning
        if(!updating)
            super.paint(g);
    }

    public void mouseClicked(MouseEvent e)
    {
        if (e.getButton() == MouseEvent.BUTTON1)
        {
            for (ListItemListener l : listItemListeners)
            {
                l.ItemClicked(e.getSource());
            }
        }
    }

    public void mousePressed(MouseEvent e)
    {
    }

    public void mouseReleased(MouseEvent e)
    {
        if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1)
        {
            for (ListItemListener l : listItemListeners)
            {
                l.ItemDoubleClicked(e.getSource());
            }
        }
        else if (e.getButton() == MouseEvent.BUTTON3)
        {
            for (ListItemListener l : listItemListeners)
            {
                l.RightClicked(e.getSource());
            }
        }
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }

    public void componentResized(ComponentEvent e)
    {
        //this.setPreferredSize(new Dimension(getParent().getWidth(), this.getHeight()));
        // size correctly
        for (Component c : getComponents())
        {
            //c.setPreferredSize(new Dimension((int)this.getVisibleRect().getWidth(), c.getHeight()));
            //c.setMinimumSize(new Dimension((int)this.getVisibleRect().getWidth(), c.getHeight()));
            //c.setMaximumSize(new Dimension((int)this.getVisibleRect().getWidth(), c.getHeight()));
            c.setSize(new Dimension((int)this.getVisibleRect().getWidth(), c.getHeight()));
        }
    }

    public void componentMoved(ComponentEvent e)
    {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void componentShown(ComponentEvent e)
    {
        //this.setPreferredSize(new Dimension(getParent().getWidth(), this.getHeight()));
        // size correctly
        for (Component c : getComponents())
        {
            //c.setPreferredSize(new Dimension((int)this.getVisibleRect().getWidth(), c.getHeight()));
            //c.setMinimumSize(new Dimension((int)this.getVisibleRect().getWidth(), c.getHeight()));
            //c.setMaximumSize(new Dimension((int)this.getVisibleRect().getWidth(), c.getHeight()));
            c.setSize(new Dimension((int)this.getVisibleRect().getWidth(), c.getHeight()));
        }
    }

    public void componentHidden(ComponentEvent e)
    {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
}
