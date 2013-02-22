package com.mxit;

import com.dax.DaxtopProfile;
import com.dax.Main;
import com.dax.Profile;
import com.dax.Profiles;
import com.dax.control.ChatMessage;
import com.dax.control.item.Item;
import com.dax.control.item.ItemCommand;
import com.dax.control.item.ItemInlineImage;
import com.dax.control.item.ItemString;
import com.dax.control.type.MessageTxType;
import com.dax.daxtop.interfaces.ChatPanel;
import com.dax.daxtop.interfaces.NetworkProfile;
import com.dax.daxtop.interfaces.ToasterItem;
import com.dax.daxtop.res.ResourceManager;
import com.dax.daxtop.ui.DaxToaster;
import com.dax.daxtop.ui.FrmChat;
import com.dax.daxtop.ui.FrmContacts;
import com.dax.daxtop.ui.components.ImagePanel;
import com.dax.daxtop.utils.ImageFileFilter;
import com.dax.lib.components.JLinkLabel;
import com.dax.list.item.state.MessageState;
import com.mxit.core.MXitManager;
import com.mxit.core.activation.MXitActivationResult;
import com.mxit.core.encryption.AES;
import com.mxit.core.exception.MXitProfileNotActivatedException;
import com.mxit.core.exception.MXitProfileNotLoadedException;
import com.mxit.core.interfaces.DownloadListener;
import com.mxit.core.model.MXitSubscription;
import com.mxit.core.model.UserProfile;
import com.mxit.core.model.MXitContact;
import com.mxit.core.model.MXitContactList;
import com.mxit.core.model.MXitMessage;
import com.mxit.core.model.Mood;
import com.mxit.core.model.UserSession;
import com.mxit.core.model.type.MXitContactPresence;
import com.mxit.core.model.type.MXitContactSubType;
import com.mxit.core.model.type.MXitMessageFlags;
import com.mxit.core.model.type.MXitMessageType;
import com.mxit.core.model.type.MXitPresence;
import com.mxit.core.protocol.command.CommandGetExtenededProfile;
import com.mxit.core.protocol.command.CommandRegister;
import com.mxit.core.protocol.command.multimedia.CommandMultimediaMessageCustomResource;
import com.mxit.core.protocol.command.multimedia.CommandMultimediaMessageFileGet;
import com.mxit.core.protocol.command.multimedia.CommandMultimediaMessageFileOffer;
import com.mxit.core.protocol.command.multimedia.model.AvatarRequest;
import com.mxit.core.protocol.command.multimedia.model.FileOfferFlags;
import com.mxit.core.protocol.command.multimedia.model.FileRejectReasons;
import com.mxit.core.res.MXitRes;
import com.mxit.core.ui.ChatScreen;
import com.mxit.core.ui.ContactsScreen;
import com.mxit.core.ui.ActivateScreen;
import com.mxit.core.protocol.command.multimedia.model.FileControl;
import com.mxit.core.ui.LoginScreen;
import java.awt.CheckboxMenuItem;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map.Entry;
import java.util.TreeMap;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * @author Dax Booysen
 */
public final class MXitNetwork extends MXitManager implements NetworkProfile
{

    /** HashMap of current chats */
    //private static HashMap<String, ChatScreen> _chats = new HashMap<String, ChatScreen>();    // Objects
    /** TreeMap of unread conversations */
    public TreeMap<String, String> unreadConversations = new TreeMap<String, String>();
    /** network name */
    public String networkName;
    /** network id */
    public static String networkId;
    // UI Panels...
    public ContactsScreen contactScreen;
    public JScrollPane contactsPane;
    /** Tray menu */
    Menu trayMenu;
    public CheckboxMenuItem mnuOnline, mnuAway, mnuDND;
    MenuItem mnuLogin;
    /** MXit Resource Manager */
    public MXitRes Res = null;
    /** UserSession */
    public UserSession userSession = null;

    /** Constructor */
    public MXitNetwork()
    {
        // load the mxit resources
        Res = new MXitRes(this);

        System.setProperty("sun.awt.noerasebackground","true");
        System.setProperty("sun.java2d.d3d","false");
        System.setProperty("sun.java2d.noddraw", "true");
        System.setProperty("swing.bufferPerWindow", "false");
        System.setProperty("swing.useflipBufferStrategy", "true");

        SetupContactsScreen();
    }

    /** Shows a chat screen */
    public void ShowChat(MXitContact contact)
    {
        ChatScreen cs = GetChat(networkName + contact.MXitID);

        if (cs == null)
        {
            cs = new ChatScreen(this, contact, FrmChat.ChatTabs);
        }

        Main.ShowChat(networkName + contact.MXitID, contact.Nickname, contact.MXitID, GetPresenceIcon(contact), cs, true);
    }

    /** Shows a chat screen */
    public void ShowChat(String MXitId)
    {
        ShowChat(getContactFromMXitID(MXitId));
    }

    /** Creates a chat if it doesnt exists and returns it */
    public ChatScreen GetChatCreateIfNotExists(final MXitContact contact)
    {
        ChatScreen cs = GetChat(networkName + contact.MXitID);

        final MXitNetwork network = this;

        if (cs == null)
        {
            try
            {
                if (!SwingUtilities.isEventDispatchThread())
                {
                    SwingUtilities.invokeAndWait(new Runnable()
                    {
                        public void run()
                        {
                            ChatScreen newCs = new ChatScreen(network, contact, FrmChat.ChatTabs);
                            Main.AddChatPanel(newCs.GetChatId(), newCs);
                        }
                    });
                }
                else
                {
                    ChatScreen newCs = new ChatScreen(network, contact, FrmChat.ChatTabs);
                    Main.AddChatPanel(newCs.GetChatId(), newCs);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return GetChat(networkName + contact.MXitID);
    }

    public JComponent GetContactScreenPanel()
    {
        SetupContactsScreen();
        
        return contactsPane;
    }

    /** Setup the ContactsPanel */
    public void SetupContactsScreen()
    {
        if (contactsPane == null)
        {
            // if we are already on the event dispatch thread
            if (SwingUtilities.isEventDispatchThread())
            {
                contactScreen = new ContactsScreen(this);
                // add control to a scroll pane
                contactsPane = new JScrollPane(contactScreen, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                contactsPane.getVerticalScrollBar().setUnitIncrement(8);
                contactScreen.setUseAvatars(true);
                return;
            }

            final MXitNetwork network = this;

            try
            {
                SwingUtilities.invokeAndWait(new Runnable()
                {

                    public void run()
                    {
                        contactScreen = new ContactsScreen(network);
                        // add control to a scroll pane
                        contactsPane = new JScrollPane(contactScreen, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                        contactsPane.getVerticalScrollBar().setUnitIncrement(8);
                        contactScreen.setUseAvatars(true);
                    }
                });
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public ImageIcon GetNetworkLogo()
    {
        return MXitRes.IMG_MAIN_MENU_MXIT;
    }

    public void NetworkDisconnect()
    {
        System.out.println("disconnected");

        // update menu items
        trayMenu.setLabel(GetNetworkCaption());
        mnuLogin.setLabel("Login");

        Logout();

        Main.ClearChats(networkName);
    }

    /** MXit network was called */
    public void NetworkExecuted(DaxtopProfile dp)
    {
        if (!LoggedIn)
        {
            // if there is currently a profile on this network
            if (CurrentProfile != null)
            {
                if (CurrentProfile.RememberPassword && !CurrentProfile.HashPin.isEmpty())
                {
                    LoginScreen lScreen = new LoginScreen(this);
                    lScreen.LoginRequest();
                }
                else
                    ShowLogin(CurrentProfile);
            }
            // normal login
            else
            {
                Profile profile = dp.GetDefaultProfileForNetwork(networkId);

                // if there is a default profile and this is the initial mxit network created on startup
                if (profile != null && networkName.equals(networkId))
                {
                    // show login for the only account
                    CurrentProfile = new UserProfile(profile);

                    if (CurrentProfile.RememberPassword && !CurrentProfile.HashPin.isEmpty())
                    {
                        LoginScreen lScreen = new LoginScreen(this);
                        lScreen.LoginRequest();
                    }
                    else
                        ShowLogin(CurrentProfile);
                }
                else
                {
                    // no auto userProfile, show activation
                    ShowActivation();
                }
            }
        }
        else if (FrmContacts.GetCurrentNetworkContactsScreen() == contactsPane)
        {
            // toggle the status screen
            contactScreen.toggleStatusScreen();
        }
    }

    public void DefaultProfileExecuted(DaxtopProfile dp)
    {
        if (!LoggedIn)
        {
            Profile profile = dp.GetDefaultProfileForNetwork(networkId);

            if (profile != null)
            {
                // show login for the only account
                CurrentProfile = new UserProfile(profile);

                if (CurrentProfile.RememberPassword && !CurrentProfile.HashPin.isEmpty())
                {
                    LoginScreen lScreen = new LoginScreen(this);
                    lScreen.LoginRequest();
                }
                else
                    ShowLogin(CurrentProfile);
            }
            else
            {
                // no auto userProfile, show activation
                ShowActivation();
            }
        }
    }

    /** Starts the MXit client using the currently set userProfile */
    public void Start()
    {
        try
        {
            StartMXit(CurrentProfile);

            // do setup
            Res.LoadDefaultEmoticonPack("", CurrentProfile.DefaultEmoticonPack == 0 ? "emoNew.png" : "emoClassic.png");
        }
        catch (MXitProfileNotActivatedException ex)
        {
            // do nothing
        }
        catch (MXitProfileNotLoadedException ex)
        {
            // do nothing
        }
    }

    /** Show the activation screen */
    public void ShowActivation()
    {
        // setup the screen
        ActivateScreen activator = new ActivateScreen(this);
        Main.menuScreen.setScreen(activator);
        // set the default button
        Main.menuScreen.getRootPane().setDefaultButton(activator.btnAccept);
        // start the challenge
        activator.getStartChallenge();
        // display the frame
        Main.menuScreen.setVisible(true);
    }

    /** Login after activation */
    public void ActivationLogin(final MXitActivationResult result)
    {
        UserProfile pfile = new UserProfile(result.getMsisdn());
        pfile.DialingCode = result.getDialingCode();
        pfile.Locale = result.request.getLocaleLanguage();
        pfile.ProductId = result.getPID();
        pfile.CountryCode = result.getCountryCode();
        pfile.SetSocket(result.getSocket1());
        pfile.SetSecondSocket(result.getSocket2());
        pfile.SetHttp(result.getHttp1());
        pfile.SetSecondHttp(result.getHttp2());
        pfile.HashPin(result.getHashPin());

        // set as the current userProfile
        CurrentProfile = pfile;

        // add to userProfile list
        Main.profile.AddProfile(CurrentProfile.SaveProfile());
        Profiles.SaveProfiles();

        final MXitNetwork network = this;

        try
        {
            // now login
            SwingUtilities.invokeAndWait(new Runnable()
            {
                public void run()
                {
                    LoginScreen lScreen = new LoginScreen(network);
                    lScreen.LoginRequest();
                }
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /** Show the login screen using this userProfile */
    public void ShowLogin(final UserProfile profile)
    {
        final MXitNetwork network = this;

        try
        {
            SwingUtilities.invokeLater(new Runnable()
            {

                public void run()
                {
                    // show login
                    LoginScreen loginScreen = new LoginScreen(network);

                    Main.menuScreen.setScreen(loginScreen);
                    // set the default button
                    Main.menuScreen.getRootPane().setDefaultButton(loginScreen.btnLogin);

                    // set the msisdn in login screen if it was set
                    if (profile.MXitID.length() > 0)
                    {
                        loginScreen.setMsisdn(profile.MXitID);
                    }

                    Main.menuScreen.setVisible(true);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**  */
    public CommandRegister Register(Calendar birthDate, boolean isMale)
    {
        return SendRegistration(birthDate, isMale);
    }

    /** Returns the icon for the status of the user */
    public static ImageIcon GetPresenceIcon(MXitContact c)
    {
        ImageIcon presenceIcon = MXitRes.IMG_STATUS_OFFLINE;

        if (c.Presence.presence == MXitPresence.Online)
        {
            presenceIcon = MXitRes.IMG_STATUS_ONLINE;
        }
        else if (c.Presence.presence == MXitPresence.Away)
        {
            presenceIcon = MXitRes.IMG_STATUS_AWAY;
        }
        else if (c.Presence.presence == MXitPresence.DND)
        {
            presenceIcon = MXitRes.IMG_STATUS_DND;
        }
        else if (c.SubType == MXitContactSubType.A)
        {
            presenceIcon = MXitRes.IMG_STATUS_INVITE;
        }

        return presenceIcon;
    }

    /** Used to retrieve a chat screen from any location */
    public static ChatScreen GetChat(String chatId)
    {
        return (ChatScreen) Main.GetChatPanel(chatId);
    }

    /** Fetches all the chat screens for the MXitNetwork */
    public ArrayList<ChatScreen> GetAllChatPanels()
    {
        ArrayList<ChatPanel> screens = Main.GetNetworkChatPanels(networkName);
        ArrayList<ChatScreen> chatScreens = new ArrayList<ChatScreen>();

        for (ChatPanel cp : screens)
            chatScreens.add((ChatScreen)cp);

        return chatScreens;
    }

    /** Updates incoming contacts after ContactList update */
    public void UpdateContactChats(MXitContactList contactList)
    {
        final ArrayList<MXitContact> contacts = contactList.GetContacts();

        SwingUtilities.invokeLater(new Runnable()
        {

            public void run()
            {
                synchronized (contacts)
                {

                    for (MXitContact c : contacts)
                    {

                        ChatScreen cs = GetChat(networkName + c.MXitID);

                        if (cs != null)
                        {
                            cs.UpdatePresence(c.Presence);
                            Main.frmChat.UpdateChat(cs.GetChatId(), c.Nickname, c.MXitID, GetPresenceIcon(c), false);
                        }
                    }
                }
            }
        });
    }

    /** Updates contacts presence */
    public void UpdateContacts(final ArrayList<MXitContactPresence> presences)
    {
        final MXitNetwork network = this;

        SwingUtilities.invokeLater(new Runnable()
        {

            public void run()
            {
                synchronized (presences)
                {
                    for (MXitContactPresence c : presences)
                    {
                        // get mxit user
                        MXitContact contact = getContactFromMXitID(c.MXitID);

                        if (contact == null)
                            continue;

                        // check if contact went offline
                        if (contact.Presence.presence != MXitPresence.Offline && c.presence == MXitPresence.Offline)
                        {
                            // play offline contact notification
                            Res.PlaySound(network, ResourceManager.SOUND_CONTACT_OFFLINE);

                            // show toaster offline notification
                            if (CurrentProfile.PresenceToasterAlerts && (Main.profile.toasterSetting == 2 || (!(Main.frmChat.isFocused() || Main.frmContacts.isFocused()) && Main.profile.toasterSetting == 1)))
                            {
                                ShowToaster(contact.avatar == null ? MXitRes.IMG_MAIN_MENU_MXIT.getImage() : contact.avatar , contact.Nickname, c.MXitID, "is <b>Offline</b>...");
                            }
                        }
                        // check if contact came online
                        else if (contact.Presence.presence != MXitPresence.Online && c.presence == MXitPresence.Online)
                        {
                            // play online contact notification
                            Res.PlaySound(network, ResourceManager.SOUND_CONTACT_ONLINE);

                            // show toaster online notification
                            if (CurrentProfile.PresenceToasterAlerts && (Main.profile.toasterSetting == 2 || (!(Main.frmChat.isFocused() || Main.frmContacts.isFocused()) && Main.profile.toasterSetting == 1)))
                            {
                                ShowToaster(contact.avatar == null ? MXitRes.IMG_MAIN_MENU_MXIT.getImage() : contact.avatar , contact.Nickname, c.MXitID, "is <b>Online</b>...");
                            }
                        }
                        // check if contact came online
                        else if (contact.Presence.presence != MXitPresence.Away && c.presence == MXitPresence.Away)
                        {
                            // show toaster away notification
                            if (CurrentProfile.PresenceToasterAlerts && (Main.profile.toasterSetting == 2 || (!(Main.frmChat.isFocused() || Main.frmContacts.isFocused()) && Main.profile.toasterSetting == 1)))
                            {
                                ShowToaster(contact.avatar == null ? MXitRes.IMG_MAIN_MENU_MXIT.getImage() : contact.avatar , contact.Nickname, c.MXitID, "is <b>Away</b>...");
                            }
                        }
                        // check if contact came online
                        else if (contact.Presence.presence != MXitPresence.DND && c.presence == MXitPresence.DND)
                        {
                            // show toaster away notification
                            if (CurrentProfile.PresenceToasterAlerts && (Main.profile.toasterSetting == 2 || (!(Main.frmChat.isFocused() || Main.frmContacts.isFocused()) && Main.profile.toasterSetting == 1)))
                            {
                                ShowToaster(contact.avatar == null ? MXitRes.IMG_MAIN_MENU_MXIT.getImage() : contact.avatar , contact.Nickname, c.MXitID, "is <b>DND</b>...");
                            }
                        }

                        // set new presenece
                        contact.Presence = new MXitContactPresence(network, c);

                        // set status message if one is set
                        if (c.StatusMessage != null)
                        {
                            contact.Status = c.StatusMessage;
                        }
                        
                        contactScreen.UpdateMXitMessageIcons(null, contact, false);

                        ChatScreen cs = GetChat(networkName + contact.MXitID);

                        if (cs != null)
                        {
                            cs.UpdatePresence(c);
                        }
                    }

                    // sort the contact list after update
                    contactScreen.UpdateContacts(currentContactList);
                }
            }
        });
    }

    public String GetNetworkName()
    {
        return networkName;
    }

    public void SetNetworkName(String networkName)
    {
        this.networkName = networkName;
    }

    @Override
    public JPopupMenu GetMenu()
    {
        return contactScreen.GetMXitMenu();
    }

    @Override
    public void MXitMessageRecieved(final MXitMessage message)
    {
        try
        {
            final MXitNetwork network = this;

            SwingUtilities.invokeLater(new Runnable()
            {

                public void run()
                {
                    try
                    {
                        if (contactScreen == null && !contactScreen.loaded)
                        {
                            // wait for it to be created
                            while (contactScreen == null && !contactScreen.loaded)
                            {
                                Thread.sleep(400);
                            }
                        }
                    }
                    catch (InterruptedException ie)
                    {
                        ie.printStackTrace();
                    };

                    MXitContact c = getContactFromMXitID(message.MXitID);

                    ChatScreen cs = GetChat(networkName + c.MXitID);

                    // add to chat screen
                    if (cs == null)
                    {
                        cs = new ChatScreen(network, c, FrmChat.ChatTabs);
                    }

                    c.messageReceived = true;
                    String text = cs.AppendMessage(message, c.Nickname);

                    // get the message or presence icon
                    ImageIcon icon = null;
                    if (FrmChat.ChatTabs.getSelectedComponent() != cs && FrmChat.ChatTabs.getTabCount() > 0)
                    {
                        icon = MXitRes.IMG_UNREAD_MESSAGES;
                    }
                    else
                    {
                        icon = GetPresenceIcon(c);
                    }

                    // the chat screen isn't focused?
                    if (!Main.frmChat.isActive())
                    {
                        // make tray icon flash
                        Main.frmContacts.RequestAttention(cs.GetChatId());
                    }

                    // update/show chat
                    Main.ShowChat(networkName + c.MXitID, c.Nickname, c.MXitID, icon, cs, false);

                    // focus chat
                    boolean alreadyFocused = Main.RequestChatFocus();

                    // request chat focus
                    if (CurrentProfile.ToasterAlerts && (Main.profile.toasterSetting == 2 || !alreadyFocused && Main.profile.toasterSetting != 0))
                    {
                        // we didnt get focus
                        ShowToaster(c.avatar == null ? MXitRes.IMG_MAIN_MENU_MXIT.getImage() : c.avatar , c.Nickname, c.MXitID, text.isEmpty() ? "New messages received..." : text);
                    }
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public synchronized void MXitContactListUpdated(MXitContactList incomingContacts)
    {
        try
        {
            if (contactsPane == null)
            {
                // wait for it to be created
                while (contactsPane == null)
                {
                    Thread.sleep(300);
                }
            }
        }
        catch (InterruptedException ie)
        {
            ie.printStackTrace();
        }

        contactScreen.UpdateContacts(currentContactList);

        // update presence and status on open chat screens
        UpdateContactChats(currentContactList);

        contactsPane.revalidate();
    }

    /** Daxtop skin changed */
    public void DaxtopSkinChanged()
    {
        if (contactScreen != null)
            contactScreen.updateStatusScreen();

        // reload contacts for skin change
        if (LoggedIn)
        {
            MXitContactListUpdated(currentContactList);
            
            // if we have a status message
            if (userSession != null && !userSession.StatusMessage.equals(""))
            {
                contactScreen.setStatus(userSession.StatusMessage);
                if (CurrentProfile.Avatar != null)
                    contactScreen.SetAvatar(CurrentProfile.Avatar.getImage());
            }
        }
    }

    @Override
    public void MXitContactPresenceUpdated(ArrayList<MXitContactPresence> presences)
    {
        // deal with presences
        UpdateContacts(presences);
    }

    /** Tasks to be run straight after login */
    public void AfterLogin()
    {
        // get this users own profile
        CommandGetExtenededProfile cmd = GetExtendedProfile("");
        userSession = new UserSession(cmd.attributes);

        // set our profiles nickname
        CurrentProfile.Nickname = userSession.fullName;
        trayMenu.setLabel(GetNetworkCaption());

        // if we may need to retrieve the avatar
        if(!userSession.avatarId.equals(""))
        {
            // use existing avatar
            if (CurrentProfile.AvatarId.equals(userSession.avatarId) && CurrentProfile.Avatar != null)
            {
                contactScreen.SetAvatar(CurrentProfile.Avatar.getImage());
            }
            else
            {
                // otherwise request from server
                userSession.LoadAvatar(this, "");
            }
        }

        // if we have a status message
        if (!userSession.StatusMessage.equals("Set your status message here..."))
        {
            contactScreen.setStatus(userSession.StatusMessage);
        }

        // setting presence and mood and emoticons
        currentMood = Mood.indexOf(CurrentProfile.mood);
        currentPresence = MXitPresence.getMXitPresence(CurrentProfile.presence);
        contactScreen.setPresence(currentPresence);
        contactScreen.setMood(currentMood);
        // update menu items
        mnuLogin.setLabel("Logout");

        ArrayList<String> avatarsToRemove = null;
        // load existing avatars
        if (!CurrentProfile.avatars.isEmpty())
        {
            for (Entry kvp : CurrentProfile.avatars.entrySet())
            {
                try
                {
                    getContactFromMXitID(kvp.getKey().toString().split("\\|")[1]).avatar = ((ImageIcon)kvp.getValue()).getImage();
                }
                catch(Exception e)
                {
                    if (avatarsToRemove == null)
                        avatarsToRemove = new ArrayList<String>();
                    // remove contact if not found
                    avatarsToRemove.add(kvp.getKey().toString());
                }
            }

            // we found avatars that no longer exist
            if (avatarsToRemove != null)
            {
                for (String key : avatarsToRemove)
                    CurrentProfile.avatars.remove(key);

                Main.profile.UpdateProfile(CurrentProfile.SaveProfile());
                Profiles.SaveProfiles();
            }
        }

        // get pending subscriptions
        GetSubscriptions();

        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                if (contactScreen != null)
                {
                    contactScreen.setVisible(true);
                    contactsPane.repaint();
                    contactsPane.updateUI();
                }
            }
        });
    }

    @Override
    public void MXitSubscriptionsReceived(ArrayList<MXitSubscription> subscriptions)
    {
        MXitContact[] contactList = new MXitContact[subscriptions.size()];

        for(int i = 0; i < subscriptions.size(); i++)
        {
            MXitSubscription subscription = subscriptions.get(i);
            MXitContact contact = new MXitContact();
            contact.Nickname = subscription.Nickname;
            contact.MXitID = subscription.MXitID;
            contact.Type = subscription.Type;
            contact.Group = "";
            contact.SubType = MXitContactSubType.A;
            contact.inviteMsg = subscription.Msg;
            contactList[i] = contact;
        }

        currentContactList.UpdateList(this, contactList);

        MXitContactListUpdated(currentContactList);
    }

    @Override
    public void MXitAvatarReceived(ArrayList<AvatarRequest> avatars)
    {
        // received avatars
        for (AvatarRequest r : avatars)
        {
            if (r==null)
            {
                break;
            }

            // if this is our own profile pic
            if (r.AvatarID.equals(userSession.avatarId))
            {
                Image avatar = Toolkit.getDefaultToolkit().createImage(r.fileData);

                contactScreen.SetAvatar(avatar);
                CurrentProfile.AvatarId = r.AvatarID;
                CurrentProfile.Avatar = new ImageIcon(avatar);
            }
            else
            {
                // if we should ignore the avatar
                if (r.file_width != 32)
                    continue;

                Image avatar = Toolkit.getDefaultToolkit().createImage(r.fileData);

                synchronized (currentContactList)
                {
                    ArrayList<MXitContact> incomingList = currentContactList.GetContacts();

                    synchronized (incomingList)
                    {
                        MXitContact c = currentContactList.GetContact(r.MXitID);
                        c.avatar = avatar;
                        // remove the old avatar
                        for(String key : CurrentProfile.avatars.keySet())
                        {
                            if (key.endsWith("|" + r.MXitID))
                            {
                                CurrentProfile.avatars.remove(key);
                                break;
                            }
                        }
                        // add new avatar
                        CurrentProfile.avatars.put(r.AvatarID.toLowerCase() + "|" + r.MXitID, new ImageIcon(avatar));
                    }
                }
            }
        }

        Main.profile.UpdateProfile(CurrentProfile.SaveProfile());
        Profiles.SaveProfiles();

        contactScreen.UpdateContacts(currentContactList);
    }

    public void ShowToaster(final Image icon, final String Nickname, final String MXitId, final String MessageText)
    {
        DaxToaster.AddToasterItem(new ToasterItem() {

            public String getToasterItemCaption()
            {
                return Nickname;
            }

            public Image getToasterItemNetworkLogo()
            {
                return icon;
            }

            public String getToasterItemTextDescription()
            {
                return MessageText;
            }

            public Image getToasterItemImageDescription()
            {
                return MXitRes.IMG_AVATAR_EMPTY.getImage();
            }

            public void toasterItemClicked()
            {
                ShowChat(getContactFromMXitID(MXitId));
            }

            public String getNetworkName()
            {
                return networkId;
            }

            public long getDisplayLength()
            {
                return 2500;
            }
        });
    }

    /** Sets the avatar of the current user */
    public void SetAvatar(BufferedImage imageIcon)
    {
        contactScreen.SetAvatar(imageIcon);
        // set avatar on mxit server
        CurrentProfile.Avatar = new ImageIcon(imageIcon);
        SendSetAvatarRequest(imageIcon);
        // save changes
        Main.profile.UpdateProfile(CurrentProfile.SaveProfile());
        Profiles.SaveProfiles();
    }

    /** Delete the current MXit profile */
    public void DeleteAccount()
    {
        Main.profile.RemoveProfile(networkId, CurrentProfile.MXitID);
        Main.ClearChats(networkName);
        Disconnect();
        contactScreen.updateStatusScreen();
        CurrentProfile = null;
        Profiles.SaveProfiles();
        Main.NetworkManager.RemoveNetwork(this);
    }

    /** Delete an account */
    public static void DeleteAccount(Profile profile)
    {
        ArrayList<NetworkProfile> nps = DaxtopNetworkManager.GetNetworkProfilesByNetworkId(networkId);

        // check if we have a network profile logged in and delete it
        for (NetworkProfile np : nps)
        {
            MXitNetwork mn = ((MXitNetwork)np);

            // set it to null if its there
            if (mn.CurrentProfile != null && mn.CurrentProfile.profile.AccountId.equals(profile.AccountId))
            {
                mn.DeleteAccount();
                break;
            }
        }

        Main.profile.RemoveProfile(networkId, profile.AccountId);
        Profiles.SaveProfiles();
    }

    /** Try and display the account if a user selects it from the context menu */
    public static boolean TryDisplayAccount(Profile profile)
    {
        ArrayList<NetworkProfile> nps = DaxtopNetworkManager.GetNetworkProfilesByNetworkId(networkId);

        // check if there is already an allocated profile
        for (NetworkProfile np : nps)
        {
            MXitNetwork mn = ((MXitNetwork)np);

            // set it to null if its there
            if (mn.CurrentProfile != null && mn.CurrentProfile.profile.AccountId.equals(profile.AccountId))
            {
                // if logged in swap to it
                if (mn.LoggedIn)
                {
                    FrmContacts.SetCurrentNetworkProfile(np);
                }
                // otherwise execute it
                else
                {
                    np.NetworkExecuted(Main.profile);
                }

                return true;
            }
        }

        return false;
    }

    /** Try use an existing network rather than spawning another */
    public static MXitNetwork TryReuseNetworkProfileInstance(Profile profile)
    {
        ArrayList<NetworkProfile> nps = DaxtopNetworkManager.GetNetworkProfilesByNetworkId(networkId);

        // check if there is already an unallocated network profile
        for (NetworkProfile np : nps)
        {
            MXitNetwork mn = ((MXitNetwork)np);

            // return if not used
            if (mn.CurrentProfile == null)
            {
                return mn;
            }
        }

        return null;
    }

    public void NetworkInitialized(DaxtopProfile dp)
    {
        if (!LoggedIn)
        {   
            Profile profile = dp.GetDefaultProfileForNetwork(networkId);

            if (profile != null)
            {
                // show login for the only account
                CurrentProfile = new UserProfile(profile);
                if (CurrentProfile.RememberPassword && !CurrentProfile.HashPin.isEmpty())
                {
                    LoginScreen lScreen = new LoginScreen(this);
                    lScreen.LoginRequest();
                }
            }
        }
    }

    public static String CalculateMimeType(File f)
    {
        String mimeType = "";

        if (f.getName().endsWith(".jpg") || f.getName().endsWith(".jpeg"))
        {
            mimeType = "image/jpeg";
        }
        else if (f.getName().endsWith(".png"))
        {
            mimeType = "image/png";
        }
        else if (f.getName().endsWith(".mp3"))
        {
            mimeType = "audio/mp3";
        }
        else if (f.getName().endsWith(".mp4"))
        {
            mimeType = "audio/mp4";
        }
        else if (f.getName().endsWith(".wav"))
        {
            mimeType = "audio/wav";
        }
        else if (f.getName().endsWith(".midi"))
        {
            mimeType = "audio/midi";
        }
        else if (f.getName().endsWith(".aac"))
        {
            mimeType = "audio/aac";
        }
        else if (f.getName().endsWith(".amr"))
        {
            mimeType = "audio/amr";
        }
        else if (f.getName().endsWith(".mxm"))
        {
            mimeType = "application/mxit-msgs";
        }
        else if (f.getName().endsWith(".mxs"))
        {
            mimeType = "application/mxit-skin";
        }
        else if (f.getName().endsWith(".mxe"))
        {
            mimeType = "application/mxit-emo";
        }
        return mimeType;
    }

    public MenuItem GetTrayMenu()
    {
        trayMenu = new Menu(GetNetworkCaption());
        mnuOnline = new CheckboxMenuItem("Online", currentPresence.getIndex(currentPresence) == 1);
        mnuAway = new CheckboxMenuItem("Away", currentPresence.getIndex(currentPresence) == 2);
        mnuDND = new CheckboxMenuItem("DND", currentPresence.getIndex(currentPresence) == 4);
        mnuLogin = new  MenuItem(LoggedIn ? "Logout" : "Login");
        trayMenu.add(mnuOnline);
        trayMenu.add(mnuAway);
        trayMenu.add(mnuDND);
        trayMenu.add("-");
        trayMenu.add(mnuLogin);

        final MXitNetwork network = this;

        mnuOnline.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                contactScreen.setPresence(MXitPresence.Online);
            }
        });

        mnuAway.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                contactScreen.setPresence(MXitPresence.Away);
            }
        });

        mnuDND.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                contactScreen.setPresence(MXitPresence.DND);
            }
        });

        mnuLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!LoggedIn)
                {
                    NetworkExecuted(Main.profile);
                    // display the network on the contacts screen
                    FrmContacts.SetCurrentNetworkProfile(network);
                }
                else
                {
                    NetworkDisconnect();
                    FrmContacts.SetCurrentNetworkProfile(null);
                }
            }
        });

        return trayMenu;
    }

    @Override
    /** Called when the underlying MXitManager disconnects */
    public void MXitDisconnected()
    {
        Main.ClearChats(networkName);
        contactScreen.SetAvatar(MXitRes.IMG_DEFAULT_AVATAR.getImage());
        contactScreen.Clear();
    }

    /** Sets the unique id for this network */
    public void SetNetworkId(String networkId)
    {
        this.networkId = networkId;
    }

    @Override
    /** Gets the network profiles current caption */
    public String GetNetworkCaption()
    {
        String caption = networkName;

        if (LoggedIn)
        {
            caption = networkId + " (" + CurrentProfile.Nickname + ")";
        }

        return caption;
    }

    /** Gets the network identifier */
    public String GetNetworkId()
    {
        return networkId;
    }

    /** Sends an image to a recipient */
    public void SendInlineImage(Component cs, ArrayList<String> recipients)
    {
        try
        {
            JFileChooser fileChooser = new JFileChooser();

            fileChooser.setDialogTitle("Choose image to send...");
            fileChooser.setFileFilter(new ImageFileFilter());
            fileChooser.setAcceptAllFileFilterUsed(false);

            if (fileChooser.showOpenDialog(cs) == JFileChooser.APPROVE_OPTION)
            {
                File f = fileChooser.getSelectedFile();

                if (f.exists())
                {
                    FileInputStream fis = new FileInputStream(f);

                    if (fis.available() > 250000)
                    {
                        JOptionPane.showMessageDialog(Main.frmChat, "Sorry, it's unsafe to transfer images larger than 250kb!");
                        return;
                    }

                    byte[] data = new byte[fis.available()];
                    fis.read(data);

                    ArrayList<Item> itemsToSend = new ArrayList<Item>();
                    itemsToSend.add(new ItemString("\n"));
                    itemsToSend.add(new ItemInlineImage(null, Toolkit.getDefaultToolkit().createImage(data), null, null));

                    String messageToSend = "\n::op=img|dat=" + AES.base64_encode(data) + ":";

                    for (String recipient : recipients)
                    {
                        MXitContact contact = getContactFromMXitID(recipient);
                        contact.messageReceived = true;
                        contact.messageState = contact.messageState == MessageState.UNREAD ? MessageState.UNREAD : MessageState.READ ;

                        ChatScreen cScreen = GetChatCreateIfNotExists(contact);

                        // user not logged in, say not connected
                        if (!LoggedIn)
                        {
                            cScreen.chatControl.AppendMessage(new ChatMessage("You are not connected to MXit!"));
                        }
                        else
                        {
                            // append message sent to chat
                            ChatMessage m = new ChatMessage("", MessageTxType.Sent, itemsToSend, System.currentTimeMillis());
                            cScreen.chatControl.AppendMessage(m);

                            // actually send the message
                            SendMXitMessage(new MXitMessage(contact.MXitID, messageToSend, MXitMessageType.Command, MXitMessageFlags.MARKUP | MXitMessageFlags.CUSTOM_EMOTICONS, com.mxit.core.model.type.MessageTxType.Sent));
                        }

                        // force screen down
                        //chatControl.scrollRectToVisible(new Rectangle(0,chatControl.getBounds(null).height+1000,1,1));
                        cScreen.chatControl.setCaretPosition(cScreen.chatControl.getDocument().getLength());
                        contactScreen.UpdateMXitMessageIcons(cScreen, contact, false);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /** Sends a file to a recipient */
    public void SendFileDirect(java.awt.Component parent, ArrayList<String> recipients)
    {
        try
        {
            JFileChooser fileChooser = new JFileChooser();

            fileChooser.setDialogTitle("Choose file to send...");

            if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION)
            {
                File f = fileChooser.getSelectedFile();

                if (f.exists())
                {
                    FileInputStream fis = new FileInputStream(f);

                    if (fis.available() > 150000)
                    {
                        JOptionPane.showMessageDialog(Main.frmChat, "Sorry, MXit maximum file size is 146.48kb!");
                        return;
                    }

                    byte[] data = new byte[fis.available()];
                    fis.read(data);

                    String mimeType = CalculateMimeType(f);

                    SendFile(f.getName(), mimeType, "", recipients , data);

                    for (String recipient : recipients)
                    {
                        MXitContact contact = getContactFromMXitID(recipient);
                        contact.messageReceived = true;
                        contact.messageState = contact.messageState == MessageState.UNREAD ? MessageState.UNREAD : MessageState.READ ;

                        ChatScreen cScreen = GetChatCreateIfNotExists(contact);

                        // user not logged in, say not connected
                        if (!LoggedIn)
                        {
                            cScreen.chatControl.AppendMessage(new ChatMessage("You are not connected to MXit!"));
                        }
                        else
                        {
                            // append message sent to chat
                            ChatMessage m = new ChatMessage(f.getName() + " sent.");
                            cScreen.chatControl.AppendMessage(m);
                        }

                        // force screen down
                        //chatControl.scrollRectToVisible(new Rectangle(0,chatControl.getBounds(null).height+1000,1,1));
                        cScreen.chatControl.setCaretPosition(cScreen.chatControl.getDocument().getLength());
                        contactScreen.UpdateMXitMessageIcons(cScreen, contact, false);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void MXitSplashReceived(final CommandMultimediaMessageCustomResource cmd)
    {
        if (cmd.imgData.length > 0)
        {
            final Image splash = new ImageIcon(Toolkit.getDefaultToolkit().createImage(cmd.imgData)).getImage();

            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    ImagePanel imagePanel = new ImagePanel("MXit Splash", networkName, String.valueOf(System.currentTimeMillis()), splash);
                    //Main.ShowChat(imagePanel.GetChatId(), "MXit Splash", cmd.SplashNameHandle, new ImageIcon(MXitRes.IMG_BTN_MXIT), imagePanel, false);
                }
            });
        }
    }

    @Override
    public void MXitFileOfferReceived(final CommandMultimediaMessageFileOffer cmd)
    {
        try
        {
            // handle a file offer with respective contacts chat
            MXitContact c = getContactFromMXitID(cmd.ContactAddress);

            // contact was invalid, reject file with respective reason type
            if (c == null)
            {
                SendRejectFile(cmd.Id, FileRejectReasons.InvalidRecipient, "");
                return;
            }

            // get the chat screen if the contact was valid
            final ChatScreen cs = GetChatCreateIfNotExists(c);

            // file control
            final FileControl fc = new FileControl(cmd, this, cs);

            // auto download the file
            if ((cmd.Flags & FileOfferFlags.AutoFetch) != 0)
            {
                // auto download (force download override)
                ItemCommand iCmd = new ItemCommand(null, "Download", "Download", null);
                final java.awt.event.ActionEvent ae = new java.awt.event.ActionEvent(fc, 0, "Download");
                ae.setSource(iCmd);
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        fc.actionPerformed(ae);
                    }
                });
            }
            // display options
            else
            {
                // calculate the size in user friendly format
                DecimalFormat twoPlaces = new DecimalFormat("0.00");
                String size = twoPlaces.format((float) cmd.Size / 1024) + "kb";

                final ChatMessage m = new ChatMessage("New file received...");
                ItemString i1 = new ItemString("\nName: ");
                i1.font = i1.font.deriveFont(Font.BOLD);
                m.AppendItem(i1);
                m.AppendText(cmd.Name);
                ItemString i2 = new ItemString("\nSize: ");
                i2.font = i2.font.deriveFont(Font.BOLD);
                m.AppendItem(i2);
                m.AppendText(size);
                ItemString i3 = new ItemString("\nType: ");
                i3.font = i3.font.deriveFont(Font.BOLD);
                m.AppendItem(i3);
                m.AppendText(cmd.MimeType);
                if (!cmd.Description.isEmpty())
                {
                    ItemString i4 = new ItemString("\nDescription: ");
                    i4.font = i4.font.deriveFont(Font.BOLD);
                    m.AppendItem(i4);
                    m.AppendText(cmd.Description);
                }
                m.AppendText("\n\n");

                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        // saving is allowed
                        if ((cmd.Flags & (FileOfferFlags.MaySaveAll | FileOfferFlags.FileMaySavePhone | FileOfferFlags.FileMaySaveAlert | FileOfferFlags.FileMaySaveGallery | FileOfferFlags.OfferFileFree)) != 0)
                        {
                            ItemCommand i5 = new ItemCommand(new JLinkLabel("Download"), "Download", "Download", fc);
                            m.AppendItem(i5);
                            m.AppendText(" ");
                        }

                        // forwarding is allowed
                        if ((cmd.Flags & (FileOfferFlags.AllowForwardAll | FileOfferFlags.AllowForwardMXit | FileOfferFlags.AllowForwardBots | FileOfferFlags.AllowForwardChatrooms | FileOfferFlags.AllowForwardGateways)) != 0)
                        {
                            // allow forwarding to all contact types
                            ItemCommand i6 = new ItemCommand(new JLinkLabel("Forward"), "Forward", "Forward", fc);
                            m.AppendItem(i6);
                            m.AppendText(" ");
                        }

                        // allow rejection
                        ItemCommand i7 = new ItemCommand(new JLinkLabel("Reject"), "Reject", "Reject", fc);
                        m.AppendItem(i7);

                        // create and add a new MXit Message for incoming file offer
                        cs.chatControl.AppendMessage(m);
                        cs.chatControl.setCaretPosition(cs.chatControl.getDocument().getLength());
                    }
                });
            }

            // notifications...
            c.messageReceived = true;
            c.messageState = c.messageState == MessageState.UNREAD ? MessageState.UNREAD : MessageState.READ ;

            // force screen down
            //chatControl.scrollRectToVisible(new Rectangle(0,chatControl.getBounds(null).height+1000,1,1));
            cs.chatControl.setCaretPosition(cs.chatControl.getDocument().getLength());
            contactScreen.UpdateMXitMessageIcons(cs, c, false);

            ShowChat(c);

            // play sound
            MXitRes.PlaySound(this, ResourceManager.SOUND_NEW_MULTIMEDIA_MESSAGE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void MXitFileGetReceived(CommandMultimediaMessageFileGet cmd)
    {
        DownloadListener dl = userSession.GetFileDownload(cmd.Id);

        if (dl != null)
        {
            dl.receivedBytes(cmd);
        }
    }
}
