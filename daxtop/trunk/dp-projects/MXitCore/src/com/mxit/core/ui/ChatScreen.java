/*
 * This is the basic chat screen that will be used in conjunction with a contact
 * in memory of the daxtop MXit client. This will be a UI JPanel with a set
 * of components built into it for chatting.
 */
package com.mxit.core.ui;

import com.dax.Main;
import com.dax.control.ChatMessage;
import com.dax.control.DaxChatPane;
import com.dax.control.item.Item;
import com.dax.control.item.ItemCommand;
import com.dax.control.item.ItemString;
import com.dax.daxtop.ui.components.*;
import com.mxit.core.model.MXitMessage;
import com.mxit.core.model.type.MXitMessageFlags;
import com.mxit.core.model.type.MXitMessageType;
import com.dax.control.type.MessageTxType;
import com.dax.daxtop.interfaces.ChatPanel;
import com.dax.list.item.state.MessageState;
import com.mxit.core.model.MXitContact;
import com.mxit.core.model.type.MXitContactType;
import com.mxit.core.model.type.MXitPresence;
import com.dax.daxtop.res.ResourceManager;
import com.dax.daxtop.ui.FrmChat;
import com.dax.daxtop.ui.UISettings;
import com.dax.daxtop.ui.components.interfaces.PopupListListener;
import com.mxit.MXitNetwork;
import com.mxit.core.model.MXitContactList;
import com.mxit.core.model.MXitMessageBuilder;
import com.mxit.core.model.type.MXitContactPresence;
import com.mxit.core.res.MXitRes;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

/**
 * @author Dax Booysen
 */
public class ChatScreen extends JPanel implements KeyListener, ChatPanel, ChangeListener, FocusListener, ComponentListener, ActionListener, PopupListListener
{
    // Main panels
    JPanel southPane, northPane, btnPane;
    /** Chat Control */
    public DaxChatPane chatControl;
    /** Scroll Pane for Chat area */
    JScrollPane chatScrollPane;
    /** Scroll Pane for text input field */
    JScrollPane txtInputScrollPane;
    /** MXitContact for whom this chat screen belongs */
    public MXitContact contact = null;
    /** Message Input Control */
    DInputField txtInput;
    /** Toolbar input box */
    JToolBar toolBar;
    /** Toolbar buttons */
    JButton btnEmoticons;
    JButton btnFont;
    JButton btnMenu;
    /** Holding DTabbedPane */
    private DTabbedPane parentTabs;
    /** Built in list of keywords for this chat */
    private ArrayList<String> aiMemory = new ArrayList<String>();
    /** Menu for text formatting */
    private JPopupMenu popupMarkup;
    /** Menu for emoticon formatting */
    private PopupGrid popupEmoticons;
    /** MXitNetwork reference */
    private MXitNetwork mxitNetwork = null;

    /** Constructor */
    public ChatScreen(MXitNetwork network, MXitContact c, DTabbedPane dtabs)
    {
        // set network
        mxitNetwork = network;

        // setup the ChatScreen panel
        setLayout(null);//new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);

        // load the chat zone commands
        if (MXitRes.chatZoneCommands == null)
            MXitRes.LoadChatZoneCommands(mxitNetwork);

        // MXitContact
        contact = c;

        synchronized (dtabs)
        {
            // tabbed parent pane and parent tab
            parentTabs = dtabs;
            parentTabs.addFocusListener(this);
        }

        // initialize the components
        _init(this);

        // load chat zone commands into aiMemory items
        if(c.Type == MXitContactType.ChatZone)
            aiMemory.addAll(MXitRes.chatZoneCommands);

        // this is needed for the custom layout to repaint and resize!
        this.addComponentListener(this);
    }

    /** Changes the presence of the current chat screen */
    public void UpdatePresence(MXitContactPresence Presence)
    {
        synchronized (parentTabs)
        {
            // update icon only if no unread messages
            if (contact.messageState != MessageState.UNREAD)
            {
                int index = parentTabs.indexOfComponent(this);

                if (index != -1)
                {
                    if (Presence.presence == MXitPresence.Online)
                    {
                        parentTabs.setIconAt(index, MXitRes.IMG_STATUS_ONLINE);
                    }
                    else if (Presence.presence == MXitPresence.Away)
                    {
                        parentTabs.setIconAt(index, MXitRes.IMG_STATUS_AWAY);
                    }
                    else if (Presence.presence == MXitPresence.DND)
                    {
                        parentTabs.setIconAt(index, MXitRes.IMG_STATUS_DND);
                    }
                    else if (Presence.presence == MXitPresence.Offline)
                    {
                        parentTabs.setIconAt(index, MXitRes.IMG_STATUS_OFFLINE);
                    }
                }
            }
            else
            {
                parentTabs.setIconAt(parentTabs.indexOfComponent(this), MXitRes.IMG_UNREAD_MESSAGES);
            }
        }
    }

    /** Initialize this frame and its components */
    private void _init(Container c)
    {
        // GridBagConstraints used for all components
        GridBagConstraints gbc = new GridBagConstraints();

        // setup northPane
        northPane = new JPanel();
        northPane.setLayout(new GridBagLayout());
        northPane.setOpaque(false);
        //northPane.setMinimumSize(new Dimension(10, 30));
        c.add(northPane);

        // setup southPane
        southPane = new JPanel();
        southPane.setLayout(new BorderLayout());//new GridBagLayout());
        southPane.setOpaque(false);
        c.add(southPane);

        // setup chatControl
        chatControl = new DaxChatPane();
        chatControl.setFont(UISettings.chatScreenFont);
        chatControl.addKeyListener(this);
        chatScrollPane = new JScrollPane(chatControl, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chatScrollPane.getViewport().addChangeListener(this);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipady = 0;      //make this component tall
        gbc.weightx = 0.1;
        gbc.weighty = 0.9;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(1, 0, 1, 0);
        northPane.add(chatScrollPane, gbc);

        // setup toolbar
        _buildToolBar();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 0;      //make this component tall
        gbc.weightx = 0.1;
        gbc.weighty = 0.0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(1, 0, 1, 0);
        northPane.add(btnPane, gbc);

        // setup txtInput  (within a jscrollpane with hidden scrollbars to force scrolling)
        txtInput = new DInputField(mxitNetwork, this);
        txtInput.setFont(UISettings.inputBoxFont);
        txtInput.setText("");
        txtInput.addKeyListener(this);
        txtInputScrollPane = new JScrollPane(txtInput, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        txtInputScrollPane.getViewport().addChangeListener(this);
        southPane.add(txtInputScrollPane, BorderLayout.CENTER);

        // borders
        txtInputScrollPane.setBorder(BorderFactory.createBevelBorder(1));
        chatScrollPane.setBorder(BorderFactory.createBevelBorder(1));
        chatControl.setMargin(new Insets(-2,-2,-2,-2));

        // attach the popup menu to chat control
        txtInput.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent e)
            {
                if (e.getButton() == MouseEvent.BUTTON3)
                {
                    JPopupMenu menuInput = new JPopupMenu(contact.Nickname);

                    JMenuItem mnuCopy = new JMenuItem("Copy");
                    menuInput.add(mnuCopy);
                    mnuCopy.addActionListener(new ActionListener()
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            txtInput.copy();
                        }
                    });

                    JMenuItem mnuCut = new JMenuItem("Cut");
                    menuInput.add(mnuCut);
                    mnuCut.addActionListener(new ActionListener()
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            txtInput.cut();
                        }
                    });

                    JMenuItem mnuPaste = new JMenuItem("Paste");
                    menuInput.add(mnuPaste);
                    mnuPaste.addActionListener(new ActionListener()
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            txtInput.paste();
                        }
                    });

                    if (txtInput.getSelectedText() == null)
                    {
                            mnuCopy.setEnabled(false);
                            mnuCut.setEnabled(false);
                    }

                    menuInput.show(txtInput, e.getX(), e.getY());
                }
            }
            
            public void mousePressed(MouseEvent e) { }
            public void mouseReleased(MouseEvent e) { }
            public void mouseEntered(MouseEvent e) { }
            public void mouseExited(MouseEvent e) { }
        });

        // attach the popup menu to chat control
        chatControl.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e)
            {
                // mouse clicked
                if (e.getButton() == MouseEvent.BUTTON3)
                {
                    JPopupMenu menuChatControl = new JPopupMenu(contact.Nickname);

                    JMenuItem mnuCopyChat = new JMenuItem("Copy");
                    mnuCopyChat.addActionListener(new ActionListener()
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            chatControl.copy();
                        }
                    });

                    menuChatControl.add(mnuCopyChat);
                    menuChatControl.add(new JSeparator());

                    if (chatControl.getSelectedText() == null)
                        mnuCopyChat.setEnabled(false);

                    JMenuItem mnuFindInChat = new JMenuItem("Find...");
                    mnuFindInChat.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));
                    mnuFindInChat.addActionListener(new ActionListener()
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            chatControl.ShowSearch();
                        }
                    });
                    menuChatControl.add(mnuFindInChat);

                    JMenuItem mnuClearChat = new JMenuItem("Clear");
                    mnuClearChat.setAccelerator(KeyStroke.getKeyStroke("F4"));
                    mnuClearChat.addActionListener(new ActionListener()
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            chatControl.ClearScreen();
                        }
                    });
                    menuChatControl.add(mnuClearChat);

                    if (contact.Type == MXitContactType.Gallery || contact.Type == MXitContactType.Service || contact.Type == MXitContactType.Info || contact.Type == MXitContactType.ChatZone)
                    {
                        JMenuItem mnuReload = new JMenuItem("Refresh");
                        mnuReload.setAccelerator(KeyStroke.getKeyStroke("F5"));
                        mnuReload.addActionListener(new ActionListener()
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                Reload();
                            }
                        });
                        menuChatControl.add(mnuReload);
                    }

                    menuChatControl.show(chatControl, e.getX(), e.getY());
                }
            }
            public void mousePressed(MouseEvent e) { }
            public void mouseReleased(MouseEvent e) { }
            public void mouseEntered(MouseEvent e) { }
            public void mouseExited(MouseEvent e) { }
        });

        // attach the popup menu
        btnFont.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e)
            {
                // setup popup menus
                popupMarkup = new JPopupMenu();

                // create menu items
                JMenuItem mnuPlain = new JMenuItem("Plain");
                JMenuItem mnuBold = new JMenuItem("<html><b>Bold</b></html>");
                JMenuItem mnuItalic = new JMenuItem("<html><font style=\"font-style:italic;\">Italic</font></html>");
                JMenuItem mnuUnderline = new JMenuItem("<html><u>Underline</u></html>");
                JMenuItem mnuColor = new JMenuItem("<html><font color=#ff0000>C</font><font color=#00ff00>o</font><font color=#00ffff>l</font><font color=#ff00ff>o</font><font color=#ffff00>r</font></html>");
                JMenu mnuBiggerText = new JMenu("<html><font style=\"font-size:15pt;\">S</font><font style=\"font-size:12pt;\">i</font><font style=\"font-size:14pt;\">z</font><font style=\"font-size:12pt;\">e</font></html>");
                JMenuItem mnuTextSize1 = new JMenuItem("<html><font style=\"font-size:8pt;\">1</font></html>");
                JMenuItem mnuTextSize2 = new JMenuItem("<html><font style=\"font-size:10pt;\">2</font></html>");
                JMenuItem mnuTextSize3 = new JMenuItem("<html><font style=\"font-size:11pt;\">3</font></html>");
                JMenuItem mnuTextSize4 = new JMenuItem("<html><font style=\"font-size:13pt;\">4</font></html>");
                JMenuItem mnuTextSize5 = new JMenuItem("<html><font style=\"font-size:15pt;\">5</font></html>");
                JMenuItem mnuTextSize6 = new JMenuItem("<html><font style=\"font-size:17pt;\">6</font></html>");
                JMenuItem mnuTextSize7 = new JMenuItem("<html><font style=\"font-size:18pt;\">7</font></html>");
                JCheckBoxMenuItem mnuFilterMarkup = new JCheckBoxMenuItem("Inline Markup", mxitNetwork.CurrentProfile.InlineMarkup);

                // user clicks on bolden text
                mnuPlain.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        //AttributeSet set = txtInput.getCharacterAttributes();
                        txtInput.setCharacterAttributes(txtInput.getStyle("Plain"), false);
                        txtInput.requestFocus(true);
                    }
                });

                // user clicks on bolden text
                mnuBold.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        //AttributeSet set = txtInput.getCharacterAttributes();
                        txtInput.setCharacterAttributes(txtInput.getStyle("Bold"), false);
                        txtInput.requestFocus(true);
                    }
                });

                // user clicks on italic text
                mnuItalic.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        txtInput.setCharacterAttributes(txtInput.getStyle("Italic"), false);
                        txtInput.requestFocus(true);
                    }
                });

                // user clicks on italic text
                mnuUnderline.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        txtInput.setCharacterAttributes(txtInput.getStyle("Underline"), false);
                        txtInput.requestFocus(true);
                    }
                });

                // user clicks on color text
                mnuColor.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        Color c = JColorChooser.showDialog(Main.frmChat, "Select a colour", txtInput.getForeground());
                        if (c!=null)
                        {
                            String styleName = String.valueOf(c.getRGB());
                            Style style = txtInput.getStyle(styleName);
                            if (style==null)
                                style = txtInput.addStyle(styleName, null);
                            StyleConstants.setForeground(style, c);
                            txtInput.setCharacterAttributes(style, false);
                        }
                        txtInput.requestFocus(true);
                    }
                });

                // user clicks on text size change
                mnuTextSize1.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        String styleName = "size1";
                        Style style = txtInput.getStyle(styleName);
                        if (style==null)
                            style = txtInput.addStyle(styleName, null);
                        StyleConstants.setFontSize(style, 9);
                        txtInput.setCharacterAttributes(style, false);
                        txtInput.requestFocus(true);
                    }
                });

                // user clicks on text size change
                mnuTextSize2.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        String styleName = "size2";
                        Style style = txtInput.getStyle(styleName);
                        if (style==null)
                            style = txtInput.addStyle(styleName, null);
                        StyleConstants.setFontSize(style, 12);
                        txtInput.setCharacterAttributes(style, false);
                        txtInput.requestFocus(true);
                    }
                });

                // user clicks on text size change
                mnuTextSize3.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        String styleName = "size3";
                        Style style = txtInput.getStyle(styleName);
                        if (style==null)
                            style = txtInput.addStyle(styleName, null);
                        StyleConstants.setFontSize(style, 15);
                        txtInput.setCharacterAttributes(style, false);
                        txtInput.requestFocus(true);
                    }
                });

                // user clicks on text size change
                mnuTextSize4.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        String styleName = "size4";
                        Style style = txtInput.getStyle(styleName);
                        if (style==null)
                            style = txtInput.addStyle(styleName, null);
                        StyleConstants.setFontSize(style, 18);
                        txtInput.setCharacterAttributes(style, false);
                        txtInput.requestFocus(true);
                    }
                });

                // user clicks on text size change
                mnuTextSize5.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        String styleName = "size5";
                        Style style = txtInput.getStyle(styleName);
                        if (style==null)
                            style = txtInput.addStyle(styleName, null);
                        StyleConstants.setFontSize(style, 21);
                        txtInput.setCharacterAttributes(style, false);
                        txtInput.requestFocus(true);
                    }
                });

                // user clicks on text size change
                mnuTextSize6.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        String styleName = "size6";
                        Style style = txtInput.getStyle(styleName);
                        if (style==null)
                            style = txtInput.addStyle(styleName, null);
                        StyleConstants.setFontSize(style, 24);
                        txtInput.setCharacterAttributes(style, false);
                        txtInput.requestFocus(true);
                    }
                });

                // user clicks on text size change
                mnuTextSize7.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        String styleName = "size7";
                        Style style = txtInput.getStyle(styleName);
                        if (style==null)
                            style = txtInput.addStyle(styleName, null);
                        StyleConstants.setFontSize(style, 27);
                        txtInput.setCharacterAttributes(style, false);
                        txtInput.requestFocus(true);
                    }
                });

                mnuFilterMarkup.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        mxitNetwork.CurrentProfile.InlineMarkup = !mxitNetwork.CurrentProfile.InlineMarkup;
                    }
                });

                // add menu items
                popupMarkup.add(mnuPlain);
                popupMarkup.add(mnuBold);
                popupMarkup.add(mnuItalic);
                popupMarkup.add(mnuUnderline);
                popupMarkup.add(mnuColor);
                popupMarkup.add(mnuBiggerText);
                mnuBiggerText.add(mnuTextSize1);
                mnuBiggerText.add(mnuTextSize2);
                mnuBiggerText.add(mnuTextSize3);
                mnuBiggerText.add(mnuTextSize4);
                mnuBiggerText.add(mnuTextSize5);
                mnuBiggerText.add(mnuTextSize6);
                mnuBiggerText.add(mnuTextSize7);
                popupMarkup.add(new JSeparator());
                popupMarkup.add(mnuFilterMarkup);

                // finally show
                popupMarkup.show(btnFont, btnFont.getX(), btnFont.getY());
                txtInput.requestFocus(true);
            }
        });

        final ChatScreen cs = this;

        btnEmoticons.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                // attach the popup menu
                popupEmoticons = PopupGrid.SetupGrid(cs, 185, 5, mxitNetwork.Res.emoticons);

                popupEmoticons.show(btnEmoticons, btnEmoticons.getX()+btnEmoticons.getWidth()-2, btnEmoticons.getY()-2);
            }
        });

        if (contact.Type != MXitContactType.Info && contact.Type != MXitContactType.ChatZone)
        {
            btnMenu.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e)
                {
                    // attach the popup menu
                    JPopupMenu popupMXit = new JPopupMenu();

                    if(contact.Type != MXitContactType.MultiMX)
                    {
                        // create menu items
                        JMenuItem mnuSendFile = new JMenuItem("Send File...");
                        mnuSendFile.addActionListener(new ActionListener()
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                ArrayList<String> recipients = new ArrayList<String>();
                                recipients.add(contact.MXitID);

                                mxitNetwork.SendFileDirect(Main.frmChat, recipients);
                            }
                        });

                        JMenuItem mnuInlineImage = new JMenuItem("Send Image...");
                        mnuInlineImage.addActionListener(new ActionListener()
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                ArrayList<String> recipients = new ArrayList<String>();
                                recipients.add(contact.MXitID);

                                mxitNetwork.SendInlineImage(Main.frmChat, recipients);
                            }
                        });

                        // add items
                        popupMXit.add(mnuSendFile);
                        popupMXit.add(mnuInlineImage);
                    }
                    // if its a multimx
                    else
                    {
                        JMenuItem mnuListMultiMX = new JMenuItem("List Members");
                        mnuListMultiMX.addActionListener(new ActionListener(){
                            public void actionPerformed(ActionEvent e)
                            {
                                // send the .list
                                MXitMessage toSend = new MXitMessage(contact.MXitID, ".list", MXitMessageType.Normal, MXitMessageFlags.MARKUP | MXitMessageFlags.CUSTOM_EMOTICONS, com.mxit.core.model.type.MessageTxType.Sent);
                                // actually send the message
                                mxitNetwork.SendMXitMessage(toSend);
                            }
                        });

                        JMenuItem mnuLeaveMultiMX = new JMenuItem("Leave MultiMx");
                        mnuLeaveMultiMX.addActionListener(new ActionListener(){
                            public void actionPerformed(ActionEvent e)
                            {
                                // send the .list
                                MXitMessage toSend = new MXitMessage(contact.MXitID, ".exit", MXitMessageType.Normal, MXitMessageFlags.MARKUP | MXitMessageFlags.CUSTOM_EMOTICONS, com.mxit.core.model.type.MessageTxType.Sent);
                                // actually send the message
                                mxitNetwork.SendMXitMessage(toSend);
                                // remove the contact
                                mxitNetwork.currentContactList.RemoveContact(contact.MXitID);
                                mxitNetwork.MXitContactListUpdated(mxitNetwork.currentContactList);
                                chatControl.AppendMessage(new ChatMessage("You have left the MultiMx."));
                                chatControl.setCaretPosition(chatControl.getDocument().getLength());
                            }
                        });

                        // do we own the MultMX
                        final String roomId = mxitNetwork.userSession.isMultiMXOwned(contact.Nickname);
                        if (roomId != null)
                        {
                            // owner abilities
                            JMenuItem mnuAddContactMultiMX = new JMenuItem("Add Member");
                            popupMXit.add(mnuAddContactMultiMX);
                            mnuAddContactMultiMX.addActionListener(new ActionListener(){
                                public void actionPerformed(ActionEvent e)
                                {
                                    // select a contact
                                    ArrayList<MXitContact> contacts = mxitNetwork.currentContactList.GetContacts(MXitContactList.MXit, true, true);
                                    Object[] cntcts = new Object[contacts.size()];
                                    for (int i = 0; i < contacts.size(); i++)
                                    {
                                        cntcts[i] = contacts.get(i).Nickname;
                                    }
                                    String option = (String)JOptionPane.showInputDialog(Main.frmChat, "Invite a contact to the MultiMx...", "Add Member...", JOptionPane.QUESTION_MESSAGE, null, cntcts, "");

                                    if (option != null)
                                    {
                                        String MXitID = "";

                                        // invite contacts
                                        for (MXitContact c : contacts)
                                        {
                                            if (c.Nickname.equals(option))
                                            {
                                                MXitID = c.MXitID;
                                                break;
                                            }
                                        }

                                        ArrayList<String> invitedContacts = new ArrayList<String>();
                                        invitedContacts.add(MXitID);

                                        mxitNetwork.AddMultiMxMember(roomId, invitedContacts);

                                        chatControl.AppendMessage(new ChatMessage("You have invited " + option + "."));
                                        chatControl.setCaretPosition(chatControl.getDocument().getLength());
                                    }
                                }
                            });

                            JMenuItem mnuKickMultiMX = new JMenuItem("Kick Members");
                            //popupMXit.add(mnuKickMultiMX);
                            mnuKickMultiMX.addActionListener(new ActionListener(){
                                public void actionPerformed(ActionEvent e)
                                {
                                    // send the .list to get the members to kick
                                    MXitMessage toSend = new MXitMessage(contact.MXitID, ".list", MXitMessageType.Normal, MXitMessageFlags.MARKUP | MXitMessageFlags.CUSTOM_EMOTICONS, com.mxit.core.model.type.MessageTxType.Sent);
                                    // actually send the message
                                    mxitNetwork.SendMXitMessage(toSend);
                                }
                            });
                        }

                        // add items
                        popupMXit.add(mnuListMultiMX);
                        popupMXit.add(mnuLeaveMultiMX);
                    }

                    // finally show
                    popupMXit.show(btnMenu, btnMenu.getX()-btnMenu.getWidth(), btnMenu.getY()-2);
                }
            });
        }
    }

    /** reload command */
    private void Reload()
    {
        chatControl.ClearScreen();
        mxitNetwork.SendMXitMessage(new MXitMessage(contact.MXitID, "", MXitMessageType.Normal, MXitMessageFlags.MARKUP | MXitMessageFlags.CUSTOM_EMOTICONS, com.mxit.core.model.type.MessageTxType.Sent));
    }

    /** Builds the toolbar */
    private void _buildToolBar()
    {
        toolBar = new JToolBar(JToolBar.HORIZONTAL);
        toolBar.setFloatable(false);

        btnEmoticons = new JButton(new ImageIcon(mxitNetwork.Res.IMG_BTN_EMOTICON));
        btnFont = new JButton(new ImageIcon(MXitRes.IMG_BTN_FONT));

        btnEmoticons.setFocusable(false);
        btnFont.setFocusable(false);
        

        toolBar.add(btnEmoticons);
        toolBar.add(btnFont);
        
        if (contact.Type != MXitContactType.ChatZone || contact.Type != MXitContactType.Info)
        {
            btnMenu = new JButton(new ImageIcon(MXitRes.IMG_BTN_MXIT));
            btnMenu.setFocusable(false);
            toolBar.add(btnMenu);
        }

        // finally add to btnPane
        btnPane = new JPanel();
        btnPane.setLayout(new BorderLayout());
        btnPane.add(toolBar, BorderLayout.WEST);
    }

    /** Send current text in input field */
    public void SendMessage()
    {
        // deal with it in here
        contact.messageReceived = true;
        mxitNetwork.contactScreen.UpdateMXitMessageIcons(this, contact, false);

        // send message and clear text input
        String msg = txtInput.fetchText();

        // create the message
        MXitMessage toSend = new MXitMessage(contact.MXitID, msg, MXitMessageType.Normal, MXitMessageFlags.MARKUP | MXitMessageFlags.CUSTOM_EMOTICONS, com.mxit.core.model.type.MessageTxType.Sent);

        // append message sent to chat
        ChatMessage m = new ChatMessage("", MessageTxType.Sent, MXitMessageBuilder.ProcessMessage(mxitNetwork, msg,  toSend.Type, toSend.Flags, toSend.TxType, contact.Type, this), System.currentTimeMillis());

        // append the message on event dispatch thread
        chatControl.AppendMessage(m);

        // user not logged in, say not connected
        if (!mxitNetwork.LoggedIn)
        {
            chatControl.AppendMessage(new ChatMessage("You are not connected to MXit!"));
        }

        // actually send the message
        mxitNetwork.SendMXitMessage(toSend);

        // force screen down
        //chatControl.scrollRectToVisible(new Rectangle(0,chatControl.getBounds(null).height+1000,1,1));
        chatControl.setCaretPosition(chatControl.getDocument().getLength());
        
        // make sure screen sizing refreshes properly
        refreshScreen();
    }

    /** Appends a new plain MXitMessage */
    public String AppendMessage(MXitMessage message, String nickname)
    {
        // declarations
        int lengthBeforeAddition = chatControl.getDocument().getLength();
        String chatNick = null;

        // deal with it in here
        mxitNetwork.contactScreen.UpdateMXitMessageIcons(this, contact, true);

        // check if this is a chat room to format nicknames
        //if (contact.Type == MXitContactType.ChatZone)
        //{
            MessageAndNick mandn = new MessageAndNick(message, nickname);
            mandn = getChatroomNick(mandn);
            aiMemory.add(mandn.nick);
            nickname = mandn.nick;
            message.Body = mandn.m.Body;

            // if a chatroom nickname was found
            if (mandn.isNickname)
            {
                // create the message
                final ChatMessage m = new ChatMessage(MXitMessageBuilder.ProcessChatNick(mxitNetwork, mandn.nick), nickname, MessageTxType.Recieved, MXitMessageBuilder.ProcessMessage(mxitNetwork, message.Body,  message.Type, message.Flags, message.TxType, contact.Type, this), message.TimeStamp);
                // append the message on event dispatch thread
                chatControl.AppendMessage(m);

                // set the nick for usage later
                chatNick = mandn.nick;
            }
            else
            {
                // create the message
                final ChatMessage m = new ChatMessage(nickname, MessageTxType.Recieved, MXitMessageBuilder.ProcessMessage(mxitNetwork, message.Body, message.Type, message.Flags, message.TxType, contact.Type, this), message.TimeStamp);

                // append the message on event dispatch thread
                chatControl.AppendMessage(m);
            }
        /*}
        else
        {
            // create the message
            final ChatMessage m = new ChatMessage(nickname, MessageTxType.Recieved, MXitMessageBuilder.ProcessMessage(mxitNetwork, message.Body, message, contact, this), message.TimeStamp);

            // append the message on event dispatch thread
            chatControl.AppendMessage(m);
        }*/

        String returnText = "";

        try
        {
            // try get the position we would start from for the new message text
            int returnMessageLength = lengthBeforeAddition + 10 + ((chatNick != null ? chatNick.length() : nickname.length()));
            
            // get the document length
            int newLength = chatControl.getDocument().getLength();

            // if there was no clear screen
            if (newLength > returnMessageLength)
                returnText = chatControl.getText(returnMessageLength, newLength - returnMessageLength);
            else // there was a clear screen and now the total text should be used as the new message return text
                returnText = chatControl.getText(10 + (chatNick != null ? chatNick.length() : nickname.length()), newLength -  (10 + (chatNick != null ? chatNick.length() : nickname.length())));

            // if we need to prefix the message with the chat nick
            returnText = (chatNick != null ? chatNick + ": " + returnText : returnText);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        // play new message audio
        MXitRes.PlaySound(mxitNetwork, ResourceManager.SOUND_NEW_MESSAGE);

        // set caret position if the user hasn't got any text selected
        if (chatControl.getSelectedText() == null)
            chatControl.setCaretPosition(chatControl.getDocument().getLength());

        return returnText.trim();
    }

    /** Gets the chatroom nickname out of the message */
    private static MessageAndNick getChatroomNick(MessageAndNick mandn)
    {
        int i = mandn.m.Body.indexOf('\n');

        if (!mandn.m.Body.startsWith("<") || i < 1)
        {
            return mandn;
        }

        // we have a nickname
        mandn.isNickname = true;
        mandn.nick = mandn.m.Body.substring(1, i - 1);
        mandn.m.Body = mandn.m.Body.substring(i + 1);

        return mandn;
    }

    /** Link or item clicked */
    public void actionPerformed(ActionEvent e)
    {
        // user clicked a reply command
        if(e.getSource() instanceof ItemCommand)
        {
            ItemCommand commandClicked = (ItemCommand)e.getSource();
            if (commandClicked.dest != null)
            {
                try
                {
                    java.awt.Desktop.getDesktop().browse(new URI(commandClicked.dest));
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
            else
            {
                // append message sent to chat
                ArrayList<Item> item = new ArrayList<Item>();
                item.add(new ItemString(commandClicked.selMsg));
                ChatMessage m = new ChatMessage("", MessageTxType.Sent, item, System.currentTimeMillis());
                // append the message on event dispatch thread
                chatControl.AppendMessage(m);
                mxitNetwork.SendMXitMessage(new MXitMessage(contact.MXitID, commandClicked.replyMsg, MXitMessageType.Normal, 0, com.mxit.core.model.type.MessageTxType.Sent));
                txtInput.requestFocus(true);
            }
        }
    }

    /** Window gained focus */
    public void ChatPanelGainedFocus()
    {
        Main.frmContacts.ReceivedAttention(GetChatId());
        mxitNetwork.contactScreen.UpdateMXitMessageIcons(this, contact, false);
        refreshScreen();
        revalidate();
        txtInput.requestFocus(true);
    }

    public void ChatPaneClosed()
    {
        // do nothing
    }

    public String GetChatNetworkName()
    {
        return mxitNetwork.networkName;
    }

    public String GetChatNickname()
    {
        return contact.Nickname;
    }

    public String GetChatId()
    {
        return mxitNetwork.networkName + contact.MXitID;
    }

    /** popup list show and item chosen */
    public void PopupListItemChosen(Object item)
    {
        if(item instanceof String)
        {
            String val = item.toString();
            if(txtInput.getText().endsWith(".") && val.startsWith("."))
                txtInput.setText(txtInput.getText().substring(0, txtInput.getText().length()-1));
            txtInput.insertText(item.toString());
            txtInput.requestFocus();
        }
    }

    public void PopupListCancelled()
    {
        txtInput.requestFocus();
    }

    /** Updates the icons after external changes */
    public void UpdateIcons()
    {
        btnEmoticons.setIcon(new ImageIcon(mxitNetwork.Res.IMG_BTN_EMOTICON));
    }

    /** Class purely for passing through calculateNick method */
    class MessageAndNick
    {

        MXitMessage m;
        String nick;
        boolean isNickname = false;

        public MessageAndNick(MXitMessage msg, String n)
        {
            m = msg;
            nick = n;
        }
    }

    /** Change tab icon */
    public void updateIcon(boolean visible)
    {
        if (!visible)
        {
            synchronized (parentTabs)
            {
                int index = parentTabs.indexOfComponent(this);

                if (index != -1)
                {
                    parentTabs.setIconAt(index, MXitRes.IMG_UNREAD_MESSAGES);
                }
            }
        }
        else
        {
            UpdatePresence(contact.Presence);
        }
    }

    /** Give this ChatScreen and its holding FrmChat focus and bring to front */
    public void GetFocus()
    {
        synchronized (parentTabs)
        {
            final FrmChat c = parentTabs.getFrmChat();
            if (!(c.isVisible() && c.hasFocus()))
            {
                c.setVisible(true);
                c.toFront();
            }

            // refresh screen on dispatch thread
            refreshScreen();
            c.repaint();
        }
    }

    //////////////////////// KEY LISTENER EVENTS /////////////////////////
    public void keyTyped(KeyEvent e)
    {
        // do nothing
    }

    public void keyPressed(KeyEvent e)
    {
        // transfer focus
        if (e.getSource() == chatControl && !e.isControlDown())
        {
            txtInput.requestFocus();
        }

        // process enters differently from standard text input
//        if (e.getKeyChar() == '\n')
//        {
//            // new line if shift is down
//            if (e.isShiftDown())
//            {
//                txtInput.insertEnter();
//                e.consume();
//                return;
//            }
//
//            // otherwise send text
//            sendMessage();
//            e.consume();
//            return;
//        }
        // process tab changes like firefox browser CTRL+TAB or CTRL+SHIFT+TAB
//        else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_TAB)
//        {
//            synchronized (parentTabs)
//            {
//                int idx = parentTabs.getSelectedIndex();
//                int max = parentTabs.getTabCount() - 1;
//
//                if (e.isShiftDown())
//                {
//                    parentTabs.setSelectedIndex(idx != 0 ? idx - 1 : max);
//                }
//                else
//                {
//                    parentTabs.setSelectedIndex(idx != max ? idx + 1 : 0);
//                }
//            }
//
//        }
        if (!aiMemory.isEmpty() && e.getKeyCode() == KeyEvent.VK_SPACE && e.isControlDown())
        {
            Component comp = (Component)e.getSource();

            // toggle popup
            Point p = comp.getLocation();
            SwingUtilities.convertPointToScreen(p, e.getComponent());
            LinkedHashMap<Object, Icon> map = new LinkedHashMap<Object, Icon>();
            for(String aiString : aiMemory)
                map.put(aiString, null);
            PopupList.setPrefix("");

            int yPos = p.y + comp.getHeight() + 2;
            if (Toolkit.getDefaultToolkit().getScreenSize().height - 50 < yPos)
                yPos = p.y - comp.getHeight() * 2;

            PopupList.ShowList(this, p.x-2, yPos, this.getWidth(), map);
        }
        else if (txtInput.getText().isEmpty() && e.getKeyChar() == '.' && contact.Type == MXitContactType.ChatZone)
        {
            // todo - make this a setting
            // toggle popup
            Component comp = (Component)e.getSource();
            Point p = comp.getLocation();
            SwingUtilities.convertPointToScreen(p, e.getComponent());
            LinkedHashMap<Object, Icon> map = new LinkedHashMap<Object, Icon>();
            for(String aiString : aiMemory)
                map.put(aiString, null);
            PopupList.setPrefix(".");

            int yPos = p.y + comp.getHeight() + 2;
            if (Toolkit.getDefaultToolkit().getScreenSize().height - 50 < yPos)
                yPos = p.y - comp.getHeight() * 2;

            PopupList.ShowList(this, p.x-2, yPos, this.getWidth(), map);
        }
        else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
        {
            Main.frmChat.setVisible(false);
        }
        else if (e.getKeyCode() == KeyEvent.VK_F4)
        {
            chatControl.ClearScreen();
        }
        else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_F)
        {
            chatControl.ShowSearch();
        }
        else if (e.getKeyCode() == KeyEvent.VK_F3)
        {
            // search
            if (txtInput.getText().length() > 0)
                FindInChat(txtInput.getText());
        }
        else if ((contact.Type == MXitContactType.Gallery || contact.Type == MXitContactType.Service || contact.Type == MXitContactType.Info || contact.Type == MXitContactType.ChatZone || contact.Type == MXitContactType.MultiMX) && e.getKeyCode() == KeyEvent.VK_F5)
        {
            Reload();
        }
    }

    /** A search of the chat screen */
    private void FindInChat(String text)
    {
//        String text = JOptionPane.showInputDialog(this, "Enter text: ", "Find...", JOptionPane.INFORMATION_MESSAGE);

//        if (text!=null)
//        {
            // find the text using the from index only if its a legitimate previous search
            chatControl.FindText(text, chatControl.getSelectedText() != null && chatControl.getSelectedText().equals(text) ? chatControl.getSelectionStart() : 0, true);
//        }
    }

    /** The action used for transferring focus to the input on chat panes */
    public final Action transferFocusAction = new AbstractAction()
    {
        public void actionPerformed(ActionEvent e)
        {
            // if ctrl is pressed
            if (e.getModifiers() == KeyEvent.CTRL_DOWN_MASK)
            {
                return;
            }
        }
    };

    public void keyReleased(KeyEvent e)
    {
    }

    ///////////////////////////// VIEWPORT CHANGE LISTENER ////////////////////////////
    public void stateChanged(ChangeEvent e)
    {
        /*
         *  This is part of workaround code for the strange issue with JScrollPane and
         *  the way it forces its contents to expand to its own preferred size. The layout
         *  code for the txtInput control is closely linked with the southPane maximum
         *  size to provide the expanding box effect - this was finally solved by repainting
         *  the screen to simulate an expanding GTalk like chat input box.
         * 
         */

        //txtInput.scrollRectToVisible(new Rectangle(0,txtInput.getHeight()-2,1,1));
        refreshScreen();
    }

    /** resizes and repaints the entire chat screen's components */
    public void refreshScreen()
    {
        // only paint if visible
        //if (!this.isVisible())
        //{
        //    return;        // detach documents
        //}
        //Document docChat = chatControl.getDocument();
        //Document docInput = txtInput.getDocument();

        int scr_height = this.getHeight();
        int scr_width = this.getWidth();
        int nMinHeight = 82;

        // calculate the southpane y position
        int sPaneY = scr_height - 6 - (int) txtInput.getSize().getHeight();

        // if the southPane is too tall, force north pane minimum height
        if (sPaneY < nMinHeight)
        {
            sPaneY = nMinHeight;
        }

        int sPaneHeight = 6 + (int) txtInput.getMinimumSize().getHeight();

        // set the positions
        southPane.setBounds(0, sPaneY, scr_width, ((sPaneHeight + sPaneY) > scr_height ? scr_height - sPaneY : sPaneHeight));
        northPane.setBounds(0, 0, scr_width, sPaneY);

        // re-attach documents
        //chatControl.setDocument(docChat);
        //txtInput.setDocument(docInput);
        txtInputScrollPane.revalidate();

        // finally repaint
        this.repaint();
    }
    ////////////////////////// FOCUS LISTENER ////////////////////////////

    public void focusGained(FocusEvent e)
    {
        if (e.getSource() == chatControl)
        {
            txtInput.requestFocus(true);
            return;
        }

        synchronized (parentTabs)
        {
            if (parentTabs.getTabCount() < 1)
            {
                parentTabs.getFrmChat().setVisible(false);
                return;
            }

            // always set the focus on the input box
            if (((DTabbedPane) e.getComponent()).getSelectedComponent().equals(this))
            {
                txtInput.requestFocus();        // make sure we refresh to fix paint issues with other windows
            }
            parentTabs.getFrmChat().repaint();
            refreshScreen();
        }
    }

    public void focusLost(FocusEvent e)
    {
        // do nothing
    }

    public void componentResized(ComponentEvent e)
    {
        refreshScreen();
    }

    public void componentMoved(ComponentEvent e)
    {
        // do nothing
    }

    public void componentShown(ComponentEvent e)
    {
        Main.frmContacts.ReceivedAttention(GetChatId());
        mxitNetwork.contactScreen.UpdateMXitMessageIcons(this, contact, false);
        refreshScreen();
        revalidate();
    }

    public void componentHidden(ComponentEvent e)
    {
        // do nothing
    }
    //////////////////////////////////////////////////////////////////////

    @Override
    public boolean isShowing()
    {
        synchronized (parentTabs)
        {
            boolean val = super.isShowing();

            // this is to avoid the first message coming in while frmchat is minimized to always show as unread
            if (parentTabs.getFrmChat().getState() == Frame.ICONIFIED)
            {
                val = false;
            }
            return val;
        }
    }
}
