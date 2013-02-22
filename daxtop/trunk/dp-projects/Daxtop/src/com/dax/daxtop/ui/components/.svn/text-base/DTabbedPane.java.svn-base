package com.dax.daxtop.ui.components;

import com.dax.Main;
import com.dax.daxtop.interfaces.ChatPanel;
import com.dax.daxtop.ui.FrmChat;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.pushingpixels.lafwidget.LafWidget;
import org.pushingpixels.lafwidget.utils.LafConstants.TabOverviewKind;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.tabbed.TabCloseListener;

/**
 * @author Dax Booysen
 * 
 * A JTabbedPane with extra functionality specific to Daxtop chat facilities
 */
public class DTabbedPane extends JTabbedPane implements ChangeListener, MouseListener {

    private FrmChat _parentChat;
    private Component newSelectedIndex;
    private int index = 0;
    TabCloseListener tabCloseListener;

    /** Constructor */
    public DTabbedPane(FrmChat parent)
    {
        super();

        _parentChat = parent;

        init();
    }

    /** Constructor */
    public DTabbedPane(FrmChat parent, int arg0)
    {
        super(arg0);

        _parentChat = parent;

        init();
    }

    /** Constructor */
    public DTabbedPane(FrmChat parent, int arg0, int arg1)
    {
        super(arg0, arg1);

        _parentChat = parent;

        init();
    }

    /** Initialize the DTabbedPane */
    private void init()
    {
        tabCloseListener = new TabCloseListener()
        {
            public void tabClosing(JTabbedPane tabbedPane, Component tabComponent)
            {
                index = tabbedPane.indexOfComponent(tabComponent);

                if (index >= 1)
                    index--;

                //neededIndex = previouslySelectedIndex;
            }

            public void tabClosed(JTabbedPane tabbedPane, Component tabComponent)
            {
                // remove tab from Main
                if (tabComponent instanceof ChatPanel) {
                    ChatPanel cp = ((ChatPanel) tabComponent);
                    cp.ChatPaneClosed();
                }

                // set focus to previous tabs
                if (tabbedPane.getTabCount() > 0)
                {
                    try
                    {
                        setSelectedIndex(index);
                        //setSelectedComponent(neededIndex);
                    } 
                    catch (IllegalArgumentException iae)
                    {
                        try
                        {
                            if (index >= 1)
                                setSelectedIndex(index-1);
                            //setSelectedComponent(previouslySelectedIndex);
                        } 
                        catch (IllegalArgumentException iae2)
                        {
                            setSelectedIndex(0);
                        }
                    }
                } 
                else
                {
                    Main.frmChat.setVisible(false);
                }
            }
        };

        // register tab close listener on the specific tabbed pane.
        SubstanceLookAndFeel.registerTabCloseChangeListener(this, tabCloseListener);

        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        this.addChangeListener(this);
        this.addMouseListener(this);
    }
    /*
    @Override
    protected void processKeyEvent(KeyEvent e) {
    super.processKeyEvent(e);
    }*/

    @Override
    public void processKeyEvent(KeyEvent e)
    {
        super.processKeyEvent(e);
    }

    /** The ChatScreen currently visible */
    public ChatPanel GetCurrentChatScreen()
    {
        return (ChatPanel) super.getSelectedComponent();
    }

    /** Sets the current FrmChat _parent object */
    public void setFrmChat(FrmChat parent)
    {
        _parentChat = parent;
    }

    /** Gets the current parent chat */
    public FrmChat getFrmChat()
    {
        return _parentChat;
    }

    public void stateChanged(ChangeEvent e)
    {
        //previouslySelectedIndex = newSelectedIndex;
        newSelectedIndex = ((JTabbedPane) e.getSource()).getSelectedComponent();

        if (newSelectedIndex != null)
        {
            Main.frmChat.setTitle("daxtop - " + ((ChatPanel) newSelectedIndex).GetChatNickname());
        }
    }

    @Override
    public void remove(Component component) {
        super.remove(component);

        if (getTabCount() < 1)
        {
            Main.frmChat.setVisible(false);
        }
    }

    @Override
    public void repaint()
    {
        super.repaint();
    }

    public void SetTabOverviewKind(String kind)
    {
        if (kind.equals("None"))
        {
            this.putClientProperty(LafWidget.TABBED_PANE_PREVIEW_PAINTER, null);
        } 
        else
        {
            this.putClientProperty(LafWidget.TABBED_PANE_PREVIEW_PAINTER, ContactTabOverviewPainter.TabOverviewPainter);

            if (kind.equals("Carousel"))
            {
                ContactTabOverviewPainter.OverviewKind = TabOverviewKind.ROUND_CAROUSEL;
            } else if (kind.equals("Grid"))
            {
                ContactTabOverviewPainter.OverviewKind = TabOverviewKind.GRID;
            } else if (kind.equals("Shuffle"))
            {
                ContactTabOverviewPainter.OverviewKind = TabOverviewKind.MENU_CAROUSEL;
            }
        }
    }

    public void mouseClicked(MouseEvent e)
    {
    }

    /** Catches the mouse pressed event. We use this to get the index of the tab that was selected
     * Currently we only care about the middle button, which allows us to close the tab
     */
    public void mousePressed(MouseEvent e)
    {
        if (e.getButton() == MouseEvent.BUTTON2)
        {
            int tabNumber = getUI().tabForCoordinate(this, e.getX(), e.getY());
            if (tabNumber < 0)
            {
                return;
            }
            else
            {
                Component t = super.getComponentAt(tabNumber);

                if (t!=null)
                {
                    tabCloseListener.tabClosing(this, t);
                    this.removeTabAt(tabNumber);
                    tabCloseListener.tabClosed(this, t);
                }
            }

            e.consume();
        }
    }

    /** Catches the mouse released event. If the index is the same as the current index, and the middle
     * mouse button was pressed, then we close the tab
     */
    public void mouseReleased(MouseEvent e)
    {
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }
}
