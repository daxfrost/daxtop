package com.mxit.core.ui;

import com.dax.Main;
import com.dax.Profile;
import com.dax.Profiles;
import com.dax.control.ChatMessage;
import com.dax.daxtop.interfaces.ContactsPanel;
import com.dax.daxtop.ui.FrmContacts;
import com.dax.daxtop.ui.WaitScreen;
import com.dax.daxtop.utils.Utils;
import com.dax.interfaces.ListItemListener;
import com.dax.list.DaxDynamicList;
import com.dax.list.item.DaxListGroupItem;
import com.dax.list.item.DaxListItem;
import com.dax.list.item.state.MessageState;
import com.mxit.MXitNetwork;
import com.mxit.core.model.MXitContact;
import com.mxit.core.model.MXitContactList;
import com.mxit.core.model.MXitMessage;
import com.mxit.core.model.MXitMessageBuilder;
import com.mxit.core.model.Mood;
import com.mxit.core.model.UserProfile;
import com.mxit.core.model.UserSession;
import com.mxit.core.model.type.MXitContactSubType;
import com.mxit.core.model.type.MXitContactType;
import com.mxit.core.model.type.MXitMessageFlags;
import com.mxit.core.model.type.MXitMessageType;
import com.mxit.core.model.type.MXitPresence;
import com.mxit.core.model.type.MessageTxType;
import com.mxit.core.protocol.command.multimedia.model.AvatarRequest;
import com.mxit.core.res.MXitRes;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * @author Dax Booysen
 *
 * The MXit Contacts screen
 */
public class ContactsScreen extends JPanel implements ContactsPanel, ListItemListener
{

    /** Contact List Control */
    private DaxDynamicList ContactListControl;
    /** User Status Panel */
    private StatusScreen statusScreen;
    /** Boolean value used to detect login state */
    public boolean loaded = false;
    /** Groups keeping state of expanded or collapsed */
    public HashMap<String, DaxListGroupItem> old_groups = new HashMap<String, DaxListGroupItem>();
    /** Current setting for contacts */
    private boolean showAvatars = true;
    /** The mxit menu */
    public JPopupMenu mxitMainContextMenu = new JPopupMenu("MXit");
    /** The contact context menu */
    private JPopupMenu mxitContactContextMenu = new JPopupMenu("MXit Contact");
    /** The group context menu */
    private JPopupMenu mxitGroupContextMenu = new JPopupMenu("MXit Group");
    /** MXitNetwork */
    MXitNetwork mxitNetwork = null;

    /** Constructor */
    public ContactsScreen(MXitNetwork network)
    {
        // set the instance
        mxitNetwork = network;

        setVisible(false);

        // set the current layout
        setLayout(new BorderLayout());

        // setup contact list - create control
        ContactListControl = new DaxDynamicList();

        // set the icons to be used on the contact list control
        DaxListItem.unreadMessageIcon = MXitRes.IMG_UNREAD_MESSAGES;
        DaxListItem.readMessageIcon = MXitRes.IMG_READ_MESSAGES;
        DaxListGroupItem.unreadMessageIcon = MXitRes.IMG_UNREAD_MESSAGES;

        // add ListItemListener
        ContactListControl.addListItemListener(this);

        // add the contact list control
        add(statusScreen = new StatusScreen(mxitNetwork), BorderLayout.NORTH);
        add(ContactListControl, BorderLayout.CENTER);
    }

    /** Update the status screen */
    public void updateStatusScreen()
    {
        //this.remove(statusScreen);
        //statusScreen = new StatusScreen(mxitNetwork);
        //add(statusScreen, BorderLayout.NORTH);
        statusScreen.refresh();
    }

    /** Sometimes we don't want to recreate the entire status screen as that seems inefficient
        so here we just refresh certain elements that need to be refreshed */
    public void refreshStatusScreen()
    {
        statusScreen.updateMoodIcon();
    }

    /** Update the status screen */
    public void updateStatusScreenUnreadConversations()
    {
        statusScreen.updateUnread();
    }

    /** Update the Contacts list for MXit client interface */
    public void UpdateContacts(MXitContactList contactList)
    {
        synchronized (contactList)//CurrentContacts)
        {
            contactList.SortContactListTraditionalMXitStyle();

            final ArrayList<MXitContact> incomingList = contactList.GetContacts(MXitContactList.All, mxitNetwork.CurrentProfile.HideOffline, false);

            try
            {
                SwingUtilities.invokeLater(new Runnable()
                {

                    public void run()
                    {
                        synchronized (incomingList)
                        {
                            // groups
                            LinkedHashMap<String, DaxListGroupItem> groups = new LinkedHashMap<String, DaxListGroupItem>();

                            // clear ui list
                            ContactListControl.ClearItems();

                            // loop through contacts and add to ui list
                            for (MXitContact c : incomingList)
                            {
                                // setup status
                                ImageIcon statusIcon = MXitRes.IMG_STATUS_OFFLINE;

                                if (c.Presence.presence == MXitPresence.Online)
                                {
                                    statusIcon = MXitRes.IMG_STATUS_ONLINE;
                                }
                                else if (c.Presence.presence == MXitPresence.Away)
                                {
                                    statusIcon = MXitRes.IMG_STATUS_AWAY;
                                }
                                else if (c.Presence.presence == MXitPresence.DND)
                                {
                                    statusIcon = MXitRes.IMG_STATUS_DND;
                                }
                                else if (c.SubType == MXitContactSubType.A)
                                {
                                    statusIcon = MXitRes.IMG_STATUS_INVITE;
                                }

                                ImageIcon avatarIcon = null;

                                // if using avatars
                                if (showAvatars)
                                {
                                    if (c.avatar != null)
                                    {
                                        avatarIcon = new ImageIcon(c.avatar);
                                    }
                                    else if (c.Type == MXitContactType.MultiMX)
                                    {
                                        avatarIcon = MXitRes.IMG_AVATAR_MULTIMX;
                                    }
                                    else
                                    {
                                        avatarIcon = MXitRes.IMG_AVATAR_EMPTY;
                                    }
                                }
                                else
                                {
                                    avatarIcon = statusIcon;
                                }

                                // create the dax list item
                                DaxListItem item = new DaxListItem(c.MXitID, Utils.escapeHtml(c.Nickname), avatarIcon, Utils.escapeHtml(c.Status));

                                String statusMsg = "";

                                if (c.Status.length() > 50)
                                {
                                    int i = 0;

                                    statusMsg = Utils.escapeHtml(c.Status.substring(i, i+50));
                                    i+=50;

                                    while (i < c.Status.length())
                                    {
                                        if (c.Status.length() - 50 > i)
                                        {
                                            statusMsg += "<br/>" + Utils.escapeHtml(c.Status.substring(i, i+50));
                                        }
                                        else
                                        {
                                            statusMsg += "<br/>" + Utils.escapeHtml(c.Status.substring(i, c.Status.length()));
                                            break;
                                        }

                                        i += 50;
                                    }
                                }
                                else
                                    statusMsg = Utils.escapeHtml(c.Status);

                                // if we should bother setting the mood
                                if (c.Presence.mood != Mood.None && c.Presence.mood != null)
                                {
                                    item.moodIcon = mxitNetwork.Res.Moods.get(c.Presence.mood);
                                }

                                // set some item state values
                                item.setToolTipText("<HTML>Nickname: " + Utils.escapeHtml(c.Nickname) + " (" + c.Type.name() + ")" + (item.moodIcon == null ? "" : "<br/>Mood: " + c.Presence.mood.GetText()) + (c.Status.isEmpty() ? "" : "<br/>Status: " + statusMsg) + "</HTML>");
                                item.messageState = c.messageState;
                                item.statusIcon = statusIcon.getImage();

                                // add to a group if in one
                                if (!c.Group.isEmpty())
                                {
                                    int onlineCount = c.Presence.presence == MXitPresence.Offline ? 0 : 1;

                                    DaxListGroupItem g = new DaxListGroupItem(c.Group);//"<html><b>" + c.Group + " (" + onlineCount + (MXitNetwork.CurrentProfile.HideOffline ? "" : "/1") + ")</b></html>");

                                    // if group doesnt exist yet
                                    if (!groups.containsKey(c.Group))
                                    {
                                        // set expanded status to same as group as it was before update
                                        g.setExpanded((old_groups.containsKey(c.Group) ? old_groups.get(c.Group).isExpanded() : false));
                                        g.onlineUsers += onlineCount;

                                        // setup the group text
                                        switch(c.SubType)
                                        {
                                            case B:
                                            {
                                                g.setText("<html><b>" +  Utils.escapeHtml(c.Group) + " (" + g.onlineUsers + (mxitNetwork.CurrentProfile.HideOffline ? "" : "/" + (g.getItems().size()+1)) + ")</b></html>");
                                            }
                                            break;
                                            case A:
                                            case P:
                                            case R:
                                            case D:
                                            {
                                                g.onlineUsers++;
                                                g.setText("<html><b>" +  Utils.escapeHtml(c.Group) + " (" + g.onlineUsers + ")</b></html>");
                                            }
                                            break;
                                            default:
                                            {
                                                g.setText("<html><b>" +  Utils.escapeHtml(c.Group) + " (" + g.onlineUsers + (mxitNetwork.CurrentProfile.HideOffline ? "" : "/" + (g.getItems().size()+1)) + ")</b></html>");
                                            }
                                        }

                                        g.setToolTipText(g.onlineUsers + " online contact" + (g.onlineUsers == 1 ? "" : "s" ) + ".");
                                        // add to new group
                                        g.addItem(item);
                                        // add group to group collection
                                        groups.put(c.Group, g);
                                    }
                                    else
                                    {
                                        // get existing group
                                        g = groups.get(c.Group);
                                        // set expanded status to previous group
                                        g.setExpanded((old_groups.containsKey(c.Group) ? old_groups.get(c.Group).isExpanded() : false));
                                        // set the name
                                        g.onlineUsers += onlineCount;

                                        // setup the group text
                                        switch(c.SubType)
                                        {
                                            case B:
                                            {
                                                g.setText("<html><b>" + c.Group + " (" + g.onlineUsers + (mxitNetwork.CurrentProfile.HideOffline ? "" : "/" + (g.getItems().size()+1)) + ")</b></html>");
                                            }
                                            break;
                                            case A:
                                            case P:
                                            case R:
                                            case D:
                                            case N:
                                            {
                                                g.onlineUsers++;
                                                g.setText("<html><b>" + c.Group + " (" + g.onlineUsers + ")</b></html>");
                                            }
                                            break;
                                            default:
                                            {
                                                g.setText("<html><b>" + c.Group + " (" + g.onlineUsers + (mxitNetwork.CurrentProfile.HideOffline ? "" : "/" + (g.getItems().size()+1)) + ")</b></html>");
                                            }
                                        }


                                        g.setToolTipText(g.onlineUsers + " online contact" + (g.onlineUsers == 1 ? "" : "s" ) + ".");
                                        // add to existing group
                                        g.addItem(item);
                                    }

                                    old_groups.put(c.Group, g);
                                }
                                else
                                {
                                    // finally append to contact list
                                    if(c.Nickname.equals("Info"))
                                        ContactListControl.add(item, 0);
                                    else
                                        ContactListControl.AppendItem(item);

                                    item.addMouseListener(ContactListControl);
                                }
                            }

                            // sort groups by their names
                            groups = MXitContactList.SortGroups(groups);


                            // add groups at the end
                            for (DaxListGroupItem g : groups.values())
                            {
                                ContactListControl.AppendItem(g);
                                g.addMouseListener(ContactListControl);

                                ArrayList<DaxListItem> groupItems = g.getItems();

                                // add items within this list
                                for (DaxListItem item : groupItems)
                                {
                                    if (g.isExpanded())
                                    {
                                        ContactListControl.AppendItem(item);
                                    }

                                    item.addMouseListener(ContactListControl);
                                }
                            }
                        }
                    }
                });


            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                SwingUtilities.invokeLater(new Runnable()
                {

                    public void run()
                    {
                        ContactListControl.revalidate();
                        mxitNetwork.contactsPane.updateUI();
                    }
                });
            }
        }
    }

    /** Repaints the contacts after adding a message icon to a contact */
    public void UpdateMXitMessageIcons(ChatScreen cs, MXitContact c, boolean newMessage)
    {
        DaxListItem item = (DaxListItem) ContactListControl.GetItem(c.MXitID);

        // may be in a group so check through groups as well
        if (item == null)
        {
            for (DaxListGroupItem group : old_groups.values())
            {
                item = group.getItem(c.MXitID);

                if (item != null)
                {
                    break;
                }
            }
        }

        UpdateMXitMessageIcons(cs, c, item, newMessage);
    }

    /** Repaints the contacts after adding a message icon to a contact */
    public void UpdateMXitMessageIcons(ChatScreen cs, MXitContact c, DaxListItem item, boolean newMessage)
    {
        if (item != null)
        {
            // presence
            ImageIcon icon;
            item.statusIcon = MXitNetwork.GetPresenceIcon(c).getImage();

            if (showAvatars)
            {
                if (c.Type == MXitContactType.MultiMX)
                {
                    icon = MXitRes.IMG_AVATAR_MULTIMX;
                }
                else
                {
                    icon = (c.avatar == null ? MXitRes.IMG_AVATAR_EMPTY : new ImageIcon(c.avatar));
                }
            }
            else
            {
                icon = new ImageIcon(item.statusIcon);
            }

            item.setIcon(icon);

            // messaging
            if (!c.messageReceived)
            {
                return;
            }

            // check if is on screen showing
            if (cs != null && cs.isShowing())
            {
                item.messageState = MessageState.READ;
                cs.contact.messageState = MessageState.READ;
                // update message icon on tab
                cs.updateIcon(true);
                // update conversation button
                if(mxitNetwork.unreadConversations.remove(c.Nickname)!=null)
                    updateStatusScreenUnreadConversations();
            }
            else if (newMessage)
            {
                item.messageState = MessageState.UNREAD;
                c.messageState = MessageState.UNREAD;
                // update message icon on tab
                if (cs != null)
                {
                    cs.updateIcon(false);
                }
                // update unread conversations
                if(mxitNetwork.unreadConversations.put(c.Nickname, c.MXitID)==null)
                    updateStatusScreenUnreadConversations();
            }
            else
            {
                item.messageState = c.messageState;

                if (cs != null)
                {
                    if (item.messageState == MessageState.UNREAD)
                    {
                        cs.updateIcon(false);
                        if(mxitNetwork.unreadConversations.put(c.Nickname, c.MXitID)==null)
                            updateStatusScreenUnreadConversations();
                    }
                    else if (item.messageState == MessageState.READ)
                    {
                        cs.updateIcon(true);
                        if(mxitNetwork.unreadConversations.remove(c.Nickname)!=null)
                            updateStatusScreenUnreadConversations();
                    }
                }
            }
        }
        // if there isnt a contact list item but a chat window is popping up for contact... (e.g hidden offline contact)
        else
        {
            // messaging
            if (!c.messageReceived)
            {
                return;
            }

            // check if is on screen showing
            if (cs != null && cs.isShowing())
            {
                cs.contact.messageState = MessageState.READ;
                // update message icon on tab
                cs.updateIcon(true);
                // update conversation button
                if(mxitNetwork.unreadConversations.remove(c.Nickname)!=null)
                    updateStatusScreenUnreadConversations();
            }
            else if (newMessage)
            {
                c.messageState = MessageState.UNREAD;
                // update message icon on tab
                if (cs != null)
                {
                    cs.updateIcon(false);
                    // update conversation button
                    if(mxitNetwork.unreadConversations.put(c.Nickname, c.MXitID)==null)
                        updateStatusScreenUnreadConversations();
                }
            }
            else
            {
                if (cs != null)
                {
                    if (c.messageState == MessageState.UNREAD)
                    {
                        cs.updateIcon(false);
                        // update conversation button
                        if(mxitNetwork.unreadConversations.put(c.Nickname, c.MXitID)==null)
                            updateStatusScreenUnreadConversations();
                    }
                    else if (c.messageState == MessageState.READ)
                    {
                        cs.updateIcon(true);

                        // update conversation button
                        if(mxitNetwork.unreadConversations.remove(c.Nickname)!=null)
                            updateStatusScreenUnreadConversations();
                    }
                }
            }
        }

        SwingUtilities.invokeLater(new Runnable()
        {

            public void run()
            {
                ContactListControl.revalidate();
                ContactListControl.repaint();
            }
        });
    }

    @Override
    /** single click on list item */
    public void ItemClicked(Object item)
    {
        // check if he is a contact
        if (item instanceof DaxListItem)
        {
            DaxListItem i = (DaxListItem) item;

            MXitContact contact = mxitNetwork.getContactFromMXitID(i.getId());

            // allow subscription
            if (contact.SubType == MXitContactSubType.A)
            {
                String inviteMsg = "";

                if (contact.inviteMsg != null && !contact.inviteMsg.isEmpty())
                    inviteMsg = "\"<b>" + contact.inviteMsg + "</b>\"<br/><br/>";

                if (JOptionPane.showConfirmDialog(Main.frmContacts, "<html>" + inviteMsg + "You have been invited by <b>" + contact.Nickname + "</b>, do you accept?</html>", "Accept Invitation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                {
                    mxitNetwork.AllowSubscription(contact.MXitID, contact.Nickname);
                }

                return;
            }

            // handle normal chats
            mxitNetwork.ShowChat(i.getId());

            if (contact.messageState == MessageState.CLEAR)
            {
                switch (contact.Type)
                {
                    case Service:
                    case ChatZone:
                    case Gallery:
                    case Info:
                    {
                        contact.messageState = MessageState.READ;
                        mxitNetwork.SendMXitMessage(new MXitMessage(contact.MXitID, "",MXitMessageType.Normal, 0, MessageTxType.Sent));
                    }
                    break;
                    default:
                    {
                        if (contact.Nickname.equals("Tradepost"))
                        {
                            contact.messageState = MessageState.READ;
                            mxitNetwork.SendMXitMessage(new MXitMessage(contact.MXitID, "",MXitMessageType.Normal, 0, MessageTxType.Sent));
                        }
                    }
                }
            }
        }
        // groups click to toggle
        else if (item instanceof DaxListGroupItem)
        {
            DaxListGroupItem group = ((DaxListGroupItem) item);
            group.toggleExpandCollapse();

            if (group.isExpanded())
            {
                for (DaxListItem gItem : group.getItems())
                {
                    MXitContact c = mxitNetwork.getContactFromMXitID(gItem.getId());

                    gItem.messageState = c.messageState;
                    gItem.revalidate();
                }
            }
        }
    }

    @Override
    /** single right click on list item */
    public void RightClicked(Object item)
    {
        // check if he is a contact
        if (item instanceof DaxListItem)
        {
            DaxListItem i = (DaxListItem) item;

            MXitContact c = mxitNetwork.getContactFromMXitID(i.getId());
            Point mouseLocation = i.getMousePosition();

            GetMXitContactMenu(c).show(i,  mouseLocation.x, mouseLocation.y);
        }
        // check if the user clicked on a group item
        else if (item instanceof DaxListGroupItem)
        {
            // get the clicked group item and show the context menu for it
            DaxListGroupItem group = ((DaxListGroupItem) item);

            // must check if we are allowed to perform any actions on this group or not
            if (!IsReservedGroup(group))
            {
                Point mouseLocation = group.getMousePosition();
                this.GetMXitGroupContactMenu(group).show(group,  mouseLocation.x, mouseLocation.y);
            }
        }
    }

    /** Returns true if the group name is reserved and false if it is not */
    private boolean IsReservedGroup(final DaxListGroupItem group)
    {
        boolean isReservedGroup = true;

        // the group doesnt store the actual type, seeing as though its a generic group item and not a MXit group item
        // so get the first item in the list and then get the SubType of that contact
        ArrayList<DaxListItem> groupItems = group.getItems();

        // loop through the items, it will break out when the first item is hit
        for (DaxListItem item : groupItems)
        {
            MXitContact contact = mxitNetwork.getContactFromMXitID(item.getId());

            switch (contact.SubType)
            {
                case A:
                case P:
                case R:
                case D:
                case N:
                    break;
                default:
                    isReservedGroup = false;
            }

            // important, break out of the loop
            break;
        }
        
        return isReservedGroup;
    }

    /** Returns the group name */
    private String GetGroupName(final DaxListGroupItem group)
    {
        String groupName = "";

        // the text in the group is wrapped in html, so to get the actual name of the group we must inspect the first item
        // in the group, from there we can check the group name
        ArrayList<DaxListItem> groupItems = group.getItems();

        // loop through the items, it will break out when the first item is hit
        for (DaxListItem item : groupItems)
        {
            MXitContact contact = mxitNetwork.getContactFromMXitID(item.getId());
            groupName = contact.Group;
            break;
        }

        return groupName;
    }

    @Override
    /** double click on list item */
    public void ItemDoubleClicked(Object item)
    {
    }

    /** Sets avatars to be used */
    public void setUseAvatars(boolean state)
    {
        DaxListItem.setUseStatus(state);
    }

    /** Gets the context menu for a MXit group */
    public JPopupMenu GetMXitGroupContactMenu(final DaxListGroupItem group)
    {
        // remove all the options that were originally there
        mxitGroupContextMenu.removeAll();

        JMenuItem mnuRenameOption = new JMenuItem("Rename");
        mnuRenameOption.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 String currentGroupName = GetGroupName(group);
                 String newGroupName = JOptionPane.showInputDialog(Main.frmContacts, "Enter new name: ", currentGroupName);

                 // we dont want to go through the whole process if the new name is the same as the old name, so do the following check
                 if (newGroupName != null && !newGroupName.isEmpty() && !newGroupName.equals(currentGroupName))
                 {
                     // build up a tree map of all the contacts, MXitId - Alias (nickname)
                     TreeMap<String, String> contacts = new TreeMap<String, String>();
                     ArrayList<MXitContact> cntcts = GetGroupContacts(group);

                     for (MXitContact c : cntcts)
                     {
                         contacts.put(c.MXitID, c.Nickname);
                         c.Group = newGroupName;
                     }

                     // refresh the contacts screen
                     UpdateContacts(mxitNetwork.currentContactList);

                     // now send the message to the backend
                     mxitNetwork.RenameGroup(newGroupName, contacts);
                 }
            } });

        JMenuItem mnuRemoveOption = new JMenuItem("Remove");
        mnuRemoveOption.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 if (JOptionPane.showConfirmDialog(Main.frmContacts, "Are you sure you want to remove this group?", "Remove Group...", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                 {
                     // build up a tree map of all the contacts, MXitId - Alias (nickname)
                     TreeMap<String, String> contacts = new TreeMap<String, String>();
                     
                     // get group contacts
                     ArrayList<MXitContact> cntcts = GetGroupContacts(group);

                     // remove the group
                     for (MXitContact c : cntcts)
                     {
                        contacts.put(c.MXitID, c.Nickname);
                        c.Group = "";
                     }

                     // refresh the contacts screen
                     UpdateContacts(mxitNetwork.currentContactList);

                     // now send the message to the backend
                     mxitNetwork.RemoveGroup(false, contacts);
                 }
            } });

            

        JMenuItem mnuBroadcastMessage = new JMenuItem("Broadcast...");
        mnuBroadcastMessage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                 String message = JOptionPane.showInputDialog(Main.frmContacts, "Please enter a broadcast...", "Broadcast...", JOptionPane.QUESTION_MESSAGE);

                 if (message != null && !message.isEmpty())
                 {
                     // get group contacts
                     ArrayList<MXitContact> cntcts = GetGroupContacts(group);

                     // remove the contacts added here
                     for (MXitContact c : cntcts)
                     {
                        if (c.Type == MXitContactType.MXit)
                        {
                            c.messageReceived = true;
                            c.messageState = c.messageState == MessageState.UNREAD ? MessageState.UNREAD : MessageState.READ ;

                            // create the message
                            MXitMessage toSend = new MXitMessage(c.MXitID, message, MXitMessageType.Normal, MXitMessageFlags.MARKUP | MXitMessageFlags.CUSTOM_EMOTICONS, com.mxit.core.model.type.MessageTxType.Sent);

                            // append message sent to chat
                            ChatScreen cScreen = mxitNetwork.GetChatCreateIfNotExists(c);
                            ChatMessage m = new ChatMessage("", com.dax.control.type.MessageTxType.Sent, MXitMessageBuilder.ProcessMessage(mxitNetwork, message, toSend.Type, toSend.Flags, toSend.TxType, c.Type, cScreen), System.currentTimeMillis());
                            cScreen.chatControl.AppendMessage(m);

                            // user not logged in, say not connected
                            if (!mxitNetwork.LoggedIn)
                            {
                                cScreen.chatControl.AppendMessage(new ChatMessage("You are not connected to MXit!"));
                            }

                            // actually send the message
                            mxitNetwork.SendMXitMessage(toSend);

                            // force screen down
                            //chatControl.scrollRectToVisible(new Rectangle(0,chatControl.getBounds(null).height+1000,1,1));
                            cScreen.chatControl.setCaretPosition(cScreen.chatControl.getDocument().getLength());
                            UpdateMXitMessageIcons(cScreen, c, false);
                        }
                     }
                 }
            } });

        // all the options above will be added to the context menu as they are all defaults
        mxitGroupContextMenu.add(mnuBroadcastMessage);
        mxitGroupContextMenu.add(new JSeparator());
        mxitGroupContextMenu.add(mnuRenameOption);
        mxitGroupContextMenu.add(mnuRemoveOption);

        return mxitGroupContextMenu;
    }

    /** Get the contacts in a group */
    public ArrayList<MXitContact> GetGroupContacts(DaxListGroupItem group)
    {
        ArrayList<MXitContact> contacts = new ArrayList<MXitContact>(mxitNetwork.currentContactList.GetContacts());

        for (int i = 0; i < contacts.size(); i++)
        {
            if (!contacts.get(i).Group.equals(group.getId()))
            {
                contacts.remove(i);
                i--;
            }
        }

        return contacts;
    }

    /** Gets the context menu for the MXit contact */
    public JPopupMenu GetMXitContactMenu(final MXitContact contact)
    {
        mxitContactContextMenu.removeAll();

        // a chat menu option
        JMenuItem mnuChatOption = new JMenuItem("Chat");
        mnuChatOption.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mxitNetwork.ShowChat(contact.MXitID);
                if (contact.messageState == MessageState.CLEAR)
                {
                    switch (contact.Type)
                    {
                        case Service:
                        case ChatZone:
                        case Gallery:
                        case Info:
                        {
                            contact.messageState = MessageState.READ;
                            mxitNetwork.SendMXitMessage(new MXitMessage(contact.MXitID, "",MXitMessageType.Normal, 0, MessageTxType.Sent));
                        }
                        break;
                        default:
                        {
                            if (contact.Nickname.equals("Tradepost"))
                            {
                                contact.messageState = MessageState.READ;
                                mxitNetwork.SendMXitMessage(new MXitMessage(contact.MXitID, "",MXitMessageType.Normal, 0, MessageTxType.Sent));
                            }
                        }
                    }
                }
            } });

        JMenuItem mnuRenameOption = new JMenuItem("Rename");
        mnuRenameOption.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 String newNick = JOptionPane.showInputDialog(Main.frmContacts, "Enter new name: ", contact.Nickname);

                 if (newNick != null && !newNick.isEmpty() && !newNick.equals(contact.Nickname))
                 {
                     Main.UpdateChat(mxitNetwork.networkName + contact.MXitID, newNick, contact.MXitID);
                     contact.Nickname = newNick;
                     // TODO - revist the below line!!!
                     mxitNetwork.MXitContactListUpdated(mxitNetwork.currentContactList);
                     mxitNetwork.UpdateContactInfo(contact);
                 }
            } });

        JMenuItem mnuRemoveContact = new JMenuItem("Remove");
        mnuRemoveContact.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 if (JOptionPane.showConfirmDialog(Main.frmContacts, "<html>Are you sure you want to remove <b>" + contact.Nickname + "</b>?</html>", "Remove Contact...", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                 {
                     mxitNetwork.RemoveContact(contact.MXitID);
                     mxitNetwork.currentContactList.RemoveContact(contact.MXitID);
                     mxitNetwork.MXitContactListUpdated(mxitNetwork.currentContactList);
                     mxitNetwork.RemoveContact(contact.MXitID);
                 }
            } });

        JMenuItem mnuInviteContact = new JMenuItem("Invite Again");
        mnuInviteContact.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 mxitNetwork.AddContact(contact.MXitID, contact, "", "");
            } });

            JMenuItem mnuAcceptInvite = new JMenuItem("Accept");
            mnuAcceptInvite.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 mxitNetwork.AllowSubscription(contact.MXitID, contact.Nickname);
            } });

        JMenuItem mnuDenyContact = new JMenuItem("Reject");
        mnuDenyContact.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 mxitNetwork.DenySubscription(contact.MXitID);
            } });

        JMenuItem mnuBlockContact = new JMenuItem("Block");
        mnuBlockContact.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 mxitNetwork.BlockSubscription(contact.MXitID);
            } });

        JMenuItem mnuRemoveFromGroupOption = new JMenuItem("<html>Remove From <b>" + contact.Group + "</b></html>");
        mnuRemoveFromGroupOption.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 if (JOptionPane.showConfirmDialog(Main.frmContacts, "<html>Are you sure you want to remove <b>" + contact.Nickname + "</b> from <b>" + contact.Group + "</b>?</html>", "Remove From Group...", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                 {
                     contact.Group = "";
                     mxitNetwork.MXitContactListUpdated(mxitNetwork.currentContactList);
                     mxitNetwork.UpdateContactInfo(contact);
                 }
            } });

        JMenuItem mnuMoveToGroupOption = new JMenuItem("Move To Group");
        mnuMoveToGroupOption.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Object[] groups = mxitNetwork.currentContactList.GetGroups(true, false);
                Object group = JOptionPane.showInputDialog(Main.frmContacts, "Select a group...", "Move To Group", JOptionPane.QUESTION_MESSAGE, null, groups, "Add New Group...");

                if (group != null)
                {
                    if (group.equals("Add New Group..."))
                    {
                        group = JOptionPane.showInputDialog(Main.frmContacts, "Enter new group name: ", "Group Name...");

                        // valid name
                        if (group != null && !group.toString().isEmpty() && !group.equals("Group Name..."))
                        {
                            contact.Group = group.toString();
                            mxitNetwork.MXitContactListUpdated(mxitNetwork.currentContactList);
                            mxitNetwork.UpdateContactInfo(contact);
                        }
                    }
                    else if (!group.toString().isEmpty())
                    {
                        // move to existing group
                        contact.Group = group.toString();
                        mxitNetwork.MXitContactListUpdated(mxitNetwork.currentContactList);
                        mxitNetwork.UpdateContactInfo(contact);
                    }
                }
            }
        });

        JMenuItem mnuSendFile = new JMenuItem("Send File...");
        mnuSendFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                ArrayList<String> recipients = new ArrayList<String>();
                recipients.add(contact.MXitID);

                mxitNetwork.SendFileDirect(mxitNetwork.contactScreen, recipients);
            }
        });

        JMenuItem mnuViewProfile = new JMenuItem("View Profile...");
        mnuViewProfile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {                
                final SwingWorker worker = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception
                    {
                        // get this users own profile
                        final UserSession contactsProfile = new UserSession(mxitNetwork.GetExtendedProfile(contact.MXitID).attributes);
                        //if (!Main.menuScreen.isVisible())
                        //    return null;

                        Image avtr = null;

                        if (contact.Presence.AvatarID != null && !contact.Presence.AvatarID.isEmpty())
                        {
                            AvatarRequest ar = new AvatarRequest();
                            ar.AvatarID = contact.Presence.AvatarID;
                            ar.MXitID = contact.MXitID;
                            ar.Format = "png";
                            ar.BitDepth = 24;
                            ar.sizes = new int[] { 96 };
                            avtr = mxitNetwork.SendAndWaitAvatarRequest(ar);
                        }
                        
                        final Image avatar = avtr;

                        // invoke on event dispatch
                        SwingUtilities.invokeAndWait(new Runnable() {
                            public void run()
                            {
                                ViewProfileScreen viewProfileScreen = new ViewProfileScreen(contact, contactsProfile, avatar);
                                Main.menuScreen.setScreen(viewProfileScreen);
                                Main.waitScreen.Stop();
                                Main.menuScreen.getRootPane().setDefaultButton(viewProfileScreen.btnClose);
                                Main.menuScreen.setVisible(true);
                            }
                        });

                        return null;
                    }
                };

                // display wait screen
                Main.menuScreen.setScreen(Main.waitScreen);
                WaitScreen.setCancelVisible(true);
                WaitScreen.setCancelListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        worker.cancel(true);
                        Main.menuScreen.setVisible(false);
                        Main.waitScreen.Stop();
                    }
                });
                WaitScreen.DynamicText = "Fetching Profile...";
                Main.waitScreen.Start();
                Main.menuScreen.setTitle("MXit View Profile - daxtop");

                worker.execute();

                // start thread
                //t.start();

                Main.menuScreen.setVisible(true);
            }
        });

        // setup menu
        switch (contact.Type)
        {
            case MXit:
            {
                switch (contact.SubType)
                {
                    // contact who has blocked the user options
                    case N:
                    {
                        mxitContactContextMenu.add(mnuBlockContact);
                        mxitContactContextMenu.add(mnuRemoveContact);
                        return mxitContactContextMenu;
                    }
                    // contact who has deleted the user options
                    case D:
                    {
                        mxitContactContextMenu.add(mnuInviteContact);
                        mxitContactContextMenu.add(mnuRemoveContact);
                        mxitContactContextMenu.add(mnuBlockContact);
                        return mxitContactContextMenu;
                    }
                    // contact who has invited you
                    case A:
                    {
                        mxitContactContextMenu.add(mnuAcceptInvite);
                        mxitContactContextMenu.add(mnuViewProfile);
                        mxitContactContextMenu.add(new JSeparator());
                        mxitContactContextMenu.add(mnuDenyContact);
                        mxitContactContextMenu.add(mnuBlockContact);
                        return mxitContactContextMenu;
                    }
                    // contact you have invited
                    case P:
                    {
                        mxitContactContextMenu.add(mnuRemoveContact);
                        mxitContactContextMenu.add(mnuBlockContact);
                        return mxitContactContextMenu;
                    }
                    // normal contact options
                    default:
                    {
                        mxitContactContextMenu.add(mnuChatOption);
                        mxitContactContextMenu.add(mnuSendFile);
                        mxitContactContextMenu.add(mnuViewProfile);
                        mxitContactContextMenu.add(new JSeparator());
                        mxitContactContextMenu.add(mnuRenameOption);
                        mxitContactContextMenu.add(mnuRemoveContact);
                        mxitContactContextMenu.add(mnuBlockContact);
                        mxitContactContextMenu.add(mnuMoveToGroupOption);
                    }
                }
            }
            break;
            case Service:
            {
                mxitContactContextMenu.add(mnuChatOption);
                if (!contact.MXitID.equals("tradepost@m") && !contact.MXitID.equals("joebanker@m"))
                {
                    mxitContactContextMenu.add(new JSeparator());
                    mxitContactContextMenu.add(mnuRenameOption);
                    mxitContactContextMenu.add(mnuRemoveContact);
                }
                mxitContactContextMenu.add(mnuMoveToGroupOption);
            }
            break;
            case Gallery:
            {
                mxitContactContextMenu.add(mnuChatOption);
                mxitContactContextMenu.add(mnuSendFile);
                mxitContactContextMenu.add(new JSeparator());
                mxitContactContextMenu.add(mnuMoveToGroupOption);
            }
            break;
            case Info:
            {
                mxitContactContextMenu.add(mnuChatOption);
            }
            break;
            case MultiMX:
            {
                // contact who has invited you
                if (contact.SubType == MXitContactSubType.A)
                {
                    mxitContactContextMenu.add(mnuAcceptInvite);
                    mxitContactContextMenu.add(mnuDenyContact);
                    return mxitContactContextMenu;
                }
                else
                {
                    mxitContactContextMenu.add(mnuChatOption);
                    mxitContactContextMenu.add(new JSeparator());
                    mxitContactContextMenu.add(mnuRenameOption);
                    mxitContactContextMenu.add(mnuRemoveContact);
                    mxitContactContextMenu.add(mnuMoveToGroupOption);
                }
            }
            break;
            default:
            {
                mxitContactContextMenu.add(mnuChatOption);
                mxitContactContextMenu.add(new JSeparator());
                mxitContactContextMenu.add(mnuRenameOption);
                mxitContactContextMenu.add(mnuRemoveContact);
                mxitContactContextMenu.add(mnuMoveToGroupOption);
            }
        }

        if (!contact.Group.isEmpty())
        {
            mxitContactContextMenu.add(mnuRemoveFromGroupOption);
        }

        return mxitContactContextMenu;
    }

    /** Gets the menu for the MXit contacts screen */
    public JPopupMenu GetMXitMenu()
    {
        mxitMainContextMenu.removeAll();
        JMenuItem mnuLoginLogout = new JMenuItem(mxitNetwork.LoggedIn ? "Logout" : "Login");
        mnuLoginLogout.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                // JOptionPane.askFIrst
                // MXitNetwork.DeleteAccount();
                if (!mxitNetwork.LoggedIn)
                {
                    mxitNetwork.NetworkExecuted(Main.profile);

                    // display the network on the contacts screen
                    FrmContacts.SetCurrentNetworkProfile(mxitNetwork);
                }
                else
                {
                    mxitNetwork.NetworkDisconnect();
                    FrmContacts.SetCurrentNetworkProfile(null);
                }
            }
        });

        // add account menu item and listener
        JMenuItem mnuAddAccount = new JMenuItem("New Account...");
        mnuAddAccount.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    MXitNetwork np = (MXitNetwork)Main.SpawnNetworkProfile(MXitNetwork.networkId);
                    np.ShowActivation();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        });

        // MXit accounts
        final ArrayList<Profile> profiles = Main.profile.GetProfilesForNetwork(MXitNetwork.networkId);

        JMenu mnuChangeAccount = new JMenu("MXit Accounts...");

        final JCheckBoxMenuItem mnuDefaultAccount = new JCheckBoxMenuItem("Default Account", mxitNetwork.CurrentProfile == null ? false : mxitNetwork.CurrentProfile.profile.isDefault());
        mnuDefaultAccount.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                if (mnuDefaultAccount.isSelected() == true)
                {
                    Main.profile.SetDefaultProfileForNetwork(MXitNetwork.networkId, mxitNetwork.CurrentProfile.profile.AccountId);
                }
                else
                {
                    Main.profile.SetDefaultProfileForNetwork(MXitNetwork.networkId, "");
                }
            }
        });

        // there is no account to make default
        if (mxitNetwork.CurrentProfile == null)
            mnuDefaultAccount.setEnabled(false);

        JMenuItem mnuRemoveAccount = new JMenuItem("Remove Account...");
        mnuRemoveAccount.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                Object[] names = new Object[profiles.size()];
                for (int i = 0; i < profiles.size(); i++)
                    names[i] = profiles.get(i).AccountId;

                String name = (String)JOptionPane.showInputDialog(Main.frmContacts, "Which account would you like to remove?", "Remove Account...", JOptionPane.QUESTION_MESSAGE, null, names, mxitNetwork.CurrentProfile != null ? mxitNetwork.CurrentProfile.MXitID : null);

                UserProfile userProfile = null;

                for (Profile p : profiles)
                    if (p.AccountId.equals(name))
                    {
                        userProfile = new UserProfile(p);
                        break;
                    }

                if (userProfile != null && JOptionPane.showConfirmDialog(Main.frmContacts, "This will delete all your settings and remove this MXit account!\nAre you sure you want to remove \"" + userProfile.getNickOrMXitID() + "\"?", "Remove Account...", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                {
                    MXitNetwork.DeleteAccount(userProfile.profile);
                }
            }
        });

        mnuChangeAccount.add(mnuAddAccount);
        mnuChangeAccount.add(mnuRemoveAccount);

        // separator & remove accounts
        if (!profiles.isEmpty())
        {
            mnuChangeAccount.add(new JSeparator());
        }
        else
        {
            mnuRemoveAccount.setEnabled(false);
            mnuDefaultAccount.setEnabled(false);
        }

        // cycle through accounts and add as options
        for(final Profile p : profiles)
        {
            JMenuItem mnuAccount = new JMenuItem(p.AccountId);
            mnuChangeAccount.add(mnuAccount);
            // action listener
            mnuAccount.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    if (!MXitNetwork.TryDisplayAccount(p))
                    {
                        if (mxitNetwork.LoggedIn)
                        {
                            try
                            {
                                // try find an empty network profile
                                MXitNetwork np = MXitNetwork.TryReuseNetworkProfileInstance(p);

                                if (np != null)
                                {
                                    np.CurrentProfile = new UserProfile(p);

                                    // auto login
                                    if (np.CurrentProfile.RememberPassword && !np.CurrentProfile.HashPin.isEmpty())
                                    {
                                        LoginScreen lScreen = new LoginScreen(np);
                                        lScreen.LoginRequest();
                                    }
                                    else
                                    {
                                        np.ShowLogin(np.CurrentProfile);
                                    }
                                }
                                else
                                {
                                    // spawn new network
                                    np = (MXitNetwork)Main.SpawnNetworkProfile(MXitNetwork.networkId);
                                    np.CurrentProfile = new UserProfile(p);

                                    // auto login
                                    if (np.CurrentProfile.RememberPassword && !np.CurrentProfile.HashPin.isEmpty())
                                    {
                                        LoginScreen lScreen = new LoginScreen(np);
                                        lScreen.LoginRequest();
                                    }
                                    else
                                    {
                                        np.ShowLogin(np.CurrentProfile);
                                    }
                                }
                            }
                            catch (Exception ex)
                            {
                                ex.printStackTrace();
                            }
                        }
                        else
                        {
                            // change account
                            mxitNetwork.CurrentProfile = new UserProfile(p);

                            // auto login?
                            if (mxitNetwork.CurrentProfile.RememberPassword && !mxitNetwork.CurrentProfile.HashPin.isEmpty())
                            {
                                LoginScreen lScreen = new LoginScreen(mxitNetwork);
                                lScreen.LoginRequest();
                            }
                            else
                            {
                                mxitNetwork.ShowLogin(mxitNetwork.CurrentProfile);
                            }
                        }
                    }
                }
            });
        }

        mxitMainContextMenu.add(mnuChangeAccount);
        mxitMainContextMenu.add(mnuDefaultAccount);

        // handle state
        if (mxitNetwork.LoggedIn)
        {
            JMenuItem mnuAddContact = new JMenuItem("Add Contact...");
            mnuAddContact.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    AddContactScreen addContactScreen = new AddContactScreen(mxitNetwork);
                    Main.menuScreen.setScreen(addContactScreen);
                    Main.menuScreen.setTitle("Add MXit Contact - daxtop");
                    Main.menuScreen.getRootPane().setDefaultButton(addContactScreen.btnAccept);
                    addContactScreen.txtLoginName.requestFocus(true);
                    Main.menuScreen.setVisible(true);
                }
            });
            
            mxitMainContextMenu.add(new JSeparator());
            mxitMainContextMenu.add(mnuAddContact);

            JMenuItem mnuAddMultiMX = new JMenuItem("Create MultiMx...");
            mnuAddMultiMX.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    AddMultiMXScreen addMultiMXScreen = new AddMultiMXScreen(mxitNetwork);

                    Main.menuScreen.setScreen(addMultiMXScreen);
                    Main.menuScreen.setTitle("Create MultiMx - daxtop");
                    Main.menuScreen.getRootPane().setDefaultButton(addMultiMXScreen.btnAccept);
                    addMultiMXScreen.txtMultiMXName.requestFocus(true);
                    Main.menuScreen.setVisible(true);
                }
            });

            mxitMainContextMenu.add(mnuAddMultiMX);

            final JCheckBoxMenuItem mnuAutoLogin = new JCheckBoxMenuItem("Auto Login", mxitNetwork.CurrentProfile.RememberPassword);
            mxitMainContextMenu.add(mnuAutoLogin);
            mnuAutoLogin.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    mxitNetwork.CurrentProfile.RememberPassword = !mxitNetwork.CurrentProfile.RememberPassword;
                    Main.profile.UpdateProfile(mxitNetwork.CurrentProfile.SaveProfile());
                    Profiles.SaveProfiles();
                }
            });
            final JCheckBoxMenuItem mnuHideOfflineContacts = new JCheckBoxMenuItem("Hide Offline", mxitNetwork.CurrentProfile.HideOffline);
            mxitMainContextMenu.add(mnuHideOfflineContacts);
            mnuHideOfflineContacts.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    mxitNetwork.CurrentProfile.HideOffline = !mxitNetwork.CurrentProfile.HideOffline;
                    mxitNetwork.MXitContactListUpdated(mxitNetwork.currentContactList);
                    Main.profile.UpdateProfile(mxitNetwork.CurrentProfile.SaveProfile());
                    Profiles.SaveProfiles();
                    mxitNetwork.contactsPane.updateUI();
                }
            });
            JMenuItem mnuSettings = new JMenuItem("Settings...");
            mxitMainContextMenu.add(mnuSettings);
            mnuSettings.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    SettingsScreen settingsScreen = new SettingsScreen(mxitNetwork);
                    Main.menuScreen.setScreen(settingsScreen);
                    Main.menuScreen.setTitle("MXit Settings - daxtop");
                    Main.menuScreen.getRootPane().setDefaultButton(settingsScreen.btnAccept);
                    Main.menuScreen.setVisible(true);
                }
            });

            mxitMainContextMenu.add(new JSeparator());
            mxitMainContextMenu.add(mnuLoginLogout);
        }
        else
        {
            if (mxitNetwork.CurrentProfile != null && Main.profile.GetProfileForNetwork(MXitNetwork.networkId, mxitNetwork.CurrentProfile.MXitID) != null)
            {
                mxitMainContextMenu.add(new JSeparator());
                mxitMainContextMenu.add(mnuLoginLogout);
            }
        }

        return mxitMainContextMenu;
    }

    /** Toggles the status screen */
    public void toggleStatusScreen()
    {
        statusScreen.setVisible(!statusScreen.isVisible());
    }

    /** Sets the users current avatar */
    public void SetAvatar(Image avatarImg)
    {
        showAvatars = true;
        statusScreen.setAvatar(avatarImg);
    }

    public void Clear()
    {
        ContactListControl.removeAll();
    }

    /** Set the status of the current status screen */
    public void setStatus(String statusMessage)
    {
        statusScreen.setStatus(statusMessage);
    }

    /** Set the presence of the current status screen */
    public void setPresence(MXitPresence presence)
    {
        statusScreen.setPresence(presence);
    }

    /** Set the mood of the current status screen */
    public void setMood(Mood mood)
    {
        statusScreen.setMood(mood);
    }
}
