package com.dax.list.item;

import com.dax.list.DaxDynamicList;
import com.dax.list.item.state.MessageState;
import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * @author Dax Booysen
 * 
 */
public class DaxListGroupItem extends DaxListBaseItem
{
    private boolean _expanded = false;
    private LinkedHashMap<String, DaxListItem> _group = new LinkedHashMap<String, DaxListItem>();
    /** unread message icon (any kind of update that has not been viewed) */
    public static ImageIcon unreadMessageIcon = null;
    /** Status icons */
    private Vector<ImageIcon> _statusIcons = new Vector<ImageIcon>();

    public int onlineUsers = 0;

    /** Constructs a new list item */
    public DaxListGroupItem()
    {
        super("", "");
        _text = "";
        super.setHorizontalAlignment(SwingConstants.CENTER);
    }

    /** Constructs a new list item with text */
    public DaxListGroupItem(String text)
    {
        super(text, text);
        _text = text;
        super.setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);

        if (!_expanded)
        {
            boolean unread = false;

            for (DaxListItem item : _group.values())
            {
                if (item.messageState == MessageState.UNREAD)
                {
                    unread = true;
                    break;
                }
            }

            int fromRight = 0;
            int fromTop = 0;

            if (unread)
            {
                fromRight = (this.getWidth() - unreadMessageIcon.getIconWidth()) - 10;
                fromTop = (this.getHeight() - (unreadMessageIcon.getIconHeight())) / 2;
                g.drawImage(unreadMessageIcon.getImage(), fromRight, fromTop, unreadMessageIcon.getIconWidth(), unreadMessageIcon.getIconHeight(), this);
            }

            if (_statusIcons.size() > 0)
            {
                // calculate drawing of the icons
                int statusCount = _statusIcons.size();
                fromTop = (this.getHeight() - (_statusIcons.get(0).getIconHeight())) / 2;

                for (int i = 1; i < statusCount; i++)
                {
                    fromRight = (this.getWidth() - (_statusIcons.get(i - 1).getIconWidth() * (i + 1))) - 10;
                    g.drawImage(_statusIcons.get(i - 1).getImage(), fromRight, fromTop, _statusIcons.get(i - 1).getIconWidth(), _statusIcons.get(i - 1).getIconHeight(), this);
                }
            }
        }
    }

    /** Toggles this groups expanded/collapsed state */
    public void toggleExpandCollapse()
    {
        _expanded = !_expanded;

        final DaxDynamicList list = ((DaxDynamicList) this.getParent());

        if (_expanded)
        {
            int i = 1;

            for (DaxListItem item : _group.values())
            {
                //for(MouseListener ml : item.getMouseListeners())
                //if (item.getMouseListeners().length < 2)
                //    item.addMouseListener(list);

                list.add(item, list.getComponentZOrder(this) + i++);
                list.revalidate();
            }
        }
        else
        {
            for (DaxListItem item : _group.values())
            {
                //for(MouseListener ml : item.getMouseListeners())
                //    item.removeMouseListener(ml);
                
                list.remove(item);
                list.revalidate();
            }
        }

        try
        {
            SwingUtilities.invokeLater(new Runnable()
            {

                public void run()
                {
                    list.revalidate();
                    list.repaint();
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /** Adds an item to this group */
    public void addItem(DaxListItem item)
    {
        _group.put(item.getId(), item);
    }

    /** Gets all the items associatted with this group item */
    public ArrayList<DaxListItem> getItems()
    {
        return new ArrayList<DaxListItem>(_group.values());
    }

    /** Tries to find the item in this group */
    public DaxListItem getItem(String txt)
    {
        for(DaxListItem item : _group.values())
        {
            if(item.getId().equals(txt))
                return item;
        }
        
        return null;
    }
    
    /** Gets expanded status */
    public boolean isExpanded()
    {
        return _expanded;
    }

    /** Sets this groups expanded/collapsed state */
    public void setExpanded(boolean expanded)
    {
        _expanded = expanded;
    }

    /** Remove a status icon */
    public void removeStatusIcon(ImageIcon icon)
    {
        _statusIcons.remove(icon);
    }

    /** Appends a status icon */
    public void addStatusIcon(ImageIcon icon)
    {
        _statusIcons.add(icon);
        repaint();
    }

    /** Clears all currently attached status icons */
    public void ClearStatusIcons()
    {
        _statusIcons.clear();
    }
}