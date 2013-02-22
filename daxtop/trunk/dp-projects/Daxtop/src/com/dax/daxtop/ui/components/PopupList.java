package com.dax.daxtop.ui.components;

import com.dax.daxtop.ui.components.interfaces.PopupListListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.LinkedHashMap;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.text.Position.Bias;

/**
 * @author Dax Booysen
 *
 * A popup list for various functions
 */
public final class PopupList extends JDialog implements KeyListener, WindowFocusListener
{

    /** The list */
    private static DList _list = new DList();
    /** The singleton */
    public static PopupList _instance = new PopupList();
    /** The main panel */
    private static JScrollPane scrollPane;
    /** requesting Component */
    private static Component c;

    /** Constructor */
    private PopupList()
    {
        this.setLayout(new BorderLayout());
        scrollPane = new JScrollPane(_list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scrollPane, BorderLayout.CENTER);
        this.addWindowFocusListener(this);
    }

    /** The list */
    public static void ShowList(Component comp, int x, int y, int width, LinkedHashMap<Object, Icon> listItems)
    {
        c = comp;
        _instance.setVisible(false);
        // create list from data
        _instance.getContentPane().removeAll();
        DefaultListModel dlm = new DefaultListModel();
        for (Object o : listItems.keySet().toArray())
        {
            dlm.addElement(o.toString());
        }
        _list = new DList(dlm);
        _list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _list.addKeyListener(_instance);
        scrollPane = new JScrollPane(_list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        _instance.add(scrollPane, BorderLayout.CENTER);
        // setup cell renderer
        _list.setCellRenderer(new IconListRenderer(listItems));
        // finally show list
        _instance.setBounds(x, y, width, 100);
        _instance.setVisible(true);
        _instance.requestFocus();
        _list.requestFocus();
        if (!dlm.isEmpty())
        {
            _list.setSelectedIndex(0);
        }
    }

    /** Custom prefix setter */
    public static void setPrefix(String text)
    {
        _list.setPrefixText(text);
    }

    public void keyTyped(KeyEvent e)
    {
    }

    public void keyPressed(KeyEvent e)
    {
        if (_list.getFirstVisibleIndex() == -1)
        {
            return;
        }

        if (e.getSource() == _list)
        {
            if (e.getKeyCode() == KeyEvent.VK_DOWN)
            {
                _list.setSelectedIndex(_list.getSelectedIndex() < _list.getMaxSelectionIndex() ? _list.getSelectedIndex() + 1 : _list.getMinSelectionIndex());
            }
            else if (e.getKeyCode() == KeyEvent.VK_UP)
            {
                _list.setSelectedIndex(_list.getSelectedIndex() > _list.getMinSelectionIndex() ? _list.getSelectedIndex() - 1 : _list.getMaxSelectionIndex());
            }
            else if (e.getKeyCode() == KeyEvent.VK_ENTER)
            {
                // user chose the text
                setVisible(false);
                if (c instanceof PopupListListener)
                {
                    ((PopupListListener) c).PopupListItemChosen((String) _list.getSelectedValue());
                }
            }
            else if (e.getKeyCode() == KeyEvent.VK_SPACE)
            {
                // user chose the text
                setVisible(false);
                if (c instanceof PopupListListener)
                {
                    ((PopupListListener) c).PopupListItemChosen((String) _list.getSelectedValue() + " ");
                }
            }
            else if (e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
            {
                setVisible(false);
                if (c instanceof PopupListListener)
                {
                    ((PopupListListener) c).PopupListCancelled();
                }
            }
        }
    }

    public void keyReleased(KeyEvent e)
    {
        
    }

    public void windowGainedFocus(WindowEvent e) { }

    public void windowLostFocus(WindowEvent e) 
    {
        _instance.setVisible(false);
    }
}
/** Custom icon list renderer */
class IconListRenderer extends DefaultListCellRenderer
{

    /** the list of objects mapped to icons */
    private LinkedHashMap<Object, Icon> icons = null;

    public IconListRenderer(LinkedHashMap<Object, Icon> icons)
    {
        this.icons = icons;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
        // Get the renderer component from parent class

        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        // Get icon to use for the list item value
        Icon icon = icons.get(value);

        // Set icon to display for value

        label.setIcon(icon);
        return label;
    }
}

class DList extends JList
{
    static String prefixText = "";

    public DList()
    {
    }

    public DList(ListModel model)
    {
        super(model);
    }

    @Override
    public int getNextMatch(String prefix, int startIndex, Bias bias)
    {
        prefix = prefixText + prefix;

        return super.getNextMatch(prefix, startIndex, bias);
    }

    public void setPrefixText(String t)
    {
        prefixText = t;
    }
}
