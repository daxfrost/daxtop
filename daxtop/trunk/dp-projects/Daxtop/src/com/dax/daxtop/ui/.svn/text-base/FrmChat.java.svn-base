package com.dax.daxtop.ui;

import com.dax.Main;
import com.dax.daxtop.interfaces.ChatPanel;
import com.dax.daxtop.res.ResourceManager;
import com.dax.daxtop.ui.components.DTabbedPane;
import com.dax.daxtop.utils.Utils;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;

/**
 * @author Dax Booysen
 *
 * This is a frame that holds a chat with a contact
 */
public class FrmChat extends JFrame implements WindowFocusListener, MouseListener
{
    /** Shared static list of ChatScreen objects */
    //private static HashMap<String, ChatPanel> _chats;
    /** The Tab Panel holding all the ChatScreens */
    public static DTabbedPane ChatTabs;

    /** The action used for tabbing forward on chat panes */
    public static final Action tabForwardAction = new AbstractAction()
    {
        public void actionPerformed(ActionEvent e)
        {
            int idx = ChatTabs.getSelectedIndex();
            int max = ChatTabs.getTabCount() - 1;
            ChatTabs.setSelectedIndex(idx != max ? idx + 1 : 0);
        }
    };

    /** The action used for tabbing backward on chat panes */
    public static final Action tabBackwardAction = new AbstractAction()
    {
        public void actionPerformed(ActionEvent e)
        {
            int idx = ChatTabs.getSelectedIndex();
            int max = ChatTabs.getTabCount() - 1;
            ChatTabs.setSelectedIndex(idx != 0 ? idx - 1 : max);
        }
    };

    /** The action used for closing a tab on the chat panes */
    public static final Action tabCloseAction = new AbstractAction()
    {
        public void actionPerformed(ActionEvent e)
        {
            ChatTabs.remove(ChatTabs.getSelectedComponent());
        }
    };

    /** Make sure the ChatPanel is open */
    public static boolean isOpen(String id)
    {
        for (int i = 0; i < ChatTabs.getTabCount(); i++)
        {
            Component c = ChatTabs.getComponentAt(i);

            if (c instanceof ChatPanel)
            {
                ChatPanel cs = (ChatPanel) c;

                if (cs.GetChatId().equals(id))
                {
                    return true;
                }
            }
        }

        // if it wasnt found add it again
        return false;
    }

    /** Constructor */
    public FrmChat(Main main)
    {
        super("daxtop");

        // setup drawing properties
        //setIgnoreRepaint(true);//setDoubleBuffered(true);
        setIconImage(ResourceManager.IMG_DAXTOP_ICON);

        // shared static ChatScreen
        //_chats = new HashMap<String, ChatPanel>();

        // create and start the first tab
        ChatTabs = new DTabbedPane(this);

        // put close buttons on tabs
        ChatTabs.putClientProperty(SubstanceLookAndFeel.TABBED_PANE_CLOSE_BUTTONS_PROPERTY, Boolean.TRUE);

        // init frame and components
        _init(this.getContentPane());
    }

    /** Removes and clears all ChatPanels associatted with this window */
    public void ClearChats(Collection<ChatPanel> screens)
    {
        synchronized (ChatTabs)
        {
            // remove the tabs
            for (ChatPanel c : screens)
            {
                if (SwingUtilities.isEventDispatchThread())
                {
                    ChatTabs.remove((Component)c);
                }
                else
                {
                    final ChatPanel cp = c;

                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            ChatTabs.remove((Component)cp);
                        }
                    });
                }

                Main.Chats.remove(c.GetChatId());
            }
        }
    }

    /** Initialize this frame and its components */
    private void _init(Container c)
    {
        InputMap im = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = getRootPane().getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK), tabCloseAction);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.CTRL_DOWN_MASK), tabForwardAction);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.SHIFT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK), tabBackwardAction);
        am.put(im.get(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK)), tabCloseAction);
        am.put(im.get(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.CTRL_DOWN_MASK)), tabForwardAction);
        am.put(im.get(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.SHIFT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK)), tabBackwardAction);

        // init frame
        Utils.setWindowCentered(this);
        setSize(300, 400);
        //setLocation(300, 200);
        setMinimumSize(new Dimension(250, 280));

        this.addWindowFocusListener(this);
        this.addMouseListener(this);

        this.setLayout(new GridLayout(1, 1));

        synchronized (ChatTabs)
        {
            c.add(ChatTabs);
        }
    }

    /** Adds a new chat panel to the current chats */
    public ChatPanel AddChat(String chatId, String nickname, String tooltip, ImageIcon icon, ChatPanel cp)
    {
        synchronized (ChatTabs)
        {
            if (Main.Chats.containsKey(chatId))
            {
                cp = (ChatPanel)Main.Chats.get(chatId);
            }
            else
            {
                Main.Chats.put(chatId, cp);
            }

            ChatTabs.addTab(nickname, icon, (Component)cp, tooltip);

            ChatTabs.repaint();
            ChatTabs.revalidate();

            return cp;
        }
    }

    public void UpdateChat(String chatId, String nickname, String tooltip)
    {
        synchronized (ChatTabs)
        {
            Component cp = (Component)Main.Chats.get(chatId);
            int index = ChatTabs.indexOfComponent(cp);
            if (index != -1)
            {
                ChatTabs.setTitleAt(index, nickname);
                ChatTabs.setToolTipTextAt(ChatTabs.indexOfComponent(cp), nickname);

                // if the component was the selected one, then we change the title of the form
                int selectedIdx = ChatTabs.getSelectedIndex();
                if (index == selectedIdx)
                {
                    this.setTitle("daxtop - " + ((ChatPanel)cp).GetChatNickname());
                }
            }
        }
    }

    public void UpdateChat(String chatId, String nickname, String tooltip, ImageIcon icon, boolean focusTab)
    {
        synchronized (ChatTabs)
        {
            Component cp = (Component)Main.Chats.get(chatId);

            int selectedIdx = ChatTabs.getSelectedIndex();
            int idx = ChatTabs.indexOfComponent(cp);

            if (idx != -1)
            {
                ChatTabs.setIconAt(idx, icon);
                ChatTabs.setToolTipTextAt(idx, tooltip);
                ChatTabs.setTitleAt(idx, nickname);
                // to be removed...
                //ChatTabs.removeTabAt(idx);
                //ChatTabs.insertTab(nickname, icon, cp, tooltip, idx);
            }

            if (focusTab)
            {
                SetTab(idx, selectedIdx);
            }
        }
    }

    /** Selects the tab specified, provide old selectedIdx and new idx */
    public static void SetTab(int idx, int selectedIdx)
    {
        ChatTabs.setSelectedIndex(idx);

        if (selectedIdx < idx && selectedIdx != -1)
        {
            synchronized (ChatTabs)
            {

                Rectangle bounds = ChatTabs.getUI().getTabBounds(ChatTabs, idx);
                int targetTabRightEdge = new Double(bounds.getX() + bounds.getWidth()).intValue();
                Action action = ChatTabs.getActionMap().get("scrollTabsForwardAction");

                // if something unexpected happens we never want to infinite loop (as in a while loop)
                // so we will never go forward in the tabs more that then # of tabs.

                int tabCount = ChatTabs.getTabCount() - idx;

                for (int i = 0; i < tabCount+1; i++)
                {
                    if (action.isEnabled())
                    {
                        action.actionPerformed(new ActionEvent(ChatTabs, ActionEvent.ACTION_PERFORMED, ""));
                    }
                    else
                    {
                        break;
                    }

                    bounds = ChatTabs.getUI().getTabBounds(ChatTabs, idx);
                    targetTabRightEdge = new Double(bounds.getX() + bounds.getWidth()).intValue();

                    //the -25 is a  padding value for the width of the scroller control
                    if (targetTabRightEdge < (ChatTabs.getWidth() - 25))
                    {
                        break;
                    }
                }
            }
        }
    }

    public static void setTabOverviewKind(String kind)
    {
        ChatTabs.SetTabOverviewKind(kind);
    }

    /** Tries to retrieve a ChatScreen for a specific user */
    public static ChatPanel GetChat(String networkName, String nickname)
    {
        synchronized (ChatTabs)
        {
            ChatPanel cs = (ChatPanel)Main.Chats.get(networkName + nickname);

            if (cs != null)
            {
                return cs;
            }
            return null;
        }
    }

    /** The ChatScreen currently visible */
    public ChatPanel GetCurrentChatScreen()
    {
        synchronized (ChatTabs)
        {
            return ChatTabs.GetCurrentChatScreen();
        }
    }

    public void windowGainedFocus(WindowEvent e)
    {
        synchronized (ChatTabs)
        {
            super.processWindowEvent(e);
            ChatPanel cs = ChatTabs.GetCurrentChatScreen();

            if (cs != null)
            {
                cs.ChatPanelGainedFocus();//Main.frmContacts.UpdateMXitMessageIcons(cs, cs.contact.Nickname, false);
            }
        }
    }

    public void windowLostFocus(WindowEvent e)
    {
    }

    @Override
    public void setVisible(boolean b)
    {
        if (!b)
        {
            setState(Frame.ICONIFIED);
            super.setVisible(b);
        }
        else
        {
            super.setVisible(b);
            for (int i = 0; i < ChatTabs.getTabCount(); i++)
            {
                Component c = ChatTabs.getComponentAt(i);

                if (c instanceof ChatPanel)
                {
                    ChatPanel cs = (ChatPanel) c;
                    cs.refreshScreen();
                }
            }
        }
    }

    /** internal use only, removes a chat from the chat cache */
    public void removeChat(String chatId)
    {
        Main.Chats.remove(chatId);
    }

    public void mouseClicked(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e)
    {
    }

    public void mouseEntered(MouseEvent e)
    {
        // prevention of cursor getting stuck
        if (this.getCursor().getType() != Cursor.getDefaultCursor().getType())
            this.setCursor(Cursor.getDefaultCursor());
    }

    public void mouseExited(MouseEvent e) {

    }
}
