package com.mxit.core;

import com.dax.Main;
import com.dax.daxtop.ui.FrmContacts;
import com.mxit.core.model.*;
import com.mxit.core.protocol.*;
import com.mxit.core.protocol.command.*;
import com.mxit.core.conn.SocketConnection;
import com.mxit.core.exception.MXitConnectionFailedException;
import com.mxit.core.exception.MXitLoginFailedException;
import com.mxit.core.exception.MXitProfileNotActivatedException;
import com.mxit.core.exception.MXitProfileNotLoadedException;
import com.mxit.core.model.UserProfile;
import com.mxit.core.model.type.FeatureType;
import com.mxit.core.model.type.MXitContactPresence;
import com.mxit.core.model.type.MXitMessageType;
import com.mxit.core.model.type.MXitPresence;
import com.mxit.core.protocol.command.multimedia.CommandMultimediaMessageCustomResource;
import com.mxit.core.protocol.command.multimedia.CommandMultimediaMessageFileForward;
import com.mxit.core.protocol.command.multimedia.CommandMultimediaMessageFileGet;
import com.mxit.core.protocol.command.multimedia.CommandMultimediaMessageFileOffer;
import com.mxit.core.protocol.command.multimedia.CommandMultimediaMessageFileReject;
import com.mxit.core.protocol.command.multimedia.CommandMultimediaMessageFileSend;

import com.mxit.core.protocol.command.multimedia.CommandMultimediaMessageGetAvatars;
import com.mxit.core.protocol.command.multimedia.CommandMultimediaMessageSetAvatar;
import com.mxit.core.protocol.command.multimedia.model.AvatarRequest;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TreeMap;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * @author Dax Booysen
 * The main class to extend then access and manage MXit
 */
public abstract class MXitManager
{
    // constants
    public final static int NO_ERROR = 0;
    public final static int ERROR = -1;    // private objects
    private SocketConnection _conn;    // Forms
    /** The currently running MXit presence */
    public MXitPresence currentPresence = MXitPresence.Online;
    /** The currently running MXit mood */
    public Mood currentMood = Mood.None;
    /** The currently running contact list */
    public MXitContactList currentContactList;
    /** The list of currently unread messages */
    public boolean unreadMessages = false;
    /** The current user profile */
    public UserProfile CurrentProfile = null;
    /** Login status to MXit */
    public boolean LoggedIn = false;
    /** Connection Status to MXit */
    public boolean Connected = false;
    /** Thread that keeps your connection to MXit open */
    private Thread keepAliveThread;

    /** The method to launch mxit from application's entry point */
    public int StartMXit(UserProfile profile) throws MXitProfileNotActivatedException, MXitProfileNotLoadedException
    {
        // if profile not loaded
        if (profile.MXitID.equals(""))
        {
            // must have a loaded profile to start mxit
            throw new MXitProfileNotLoadedException();
        }

        // set the current profile instance
        CurrentProfile = profile;

        // init contact list
        currentContactList = new MXitContactList();

        // try load application configuration
        if (!MXitConfiguration.Load())
        {
            // todo - file does not exist, display linked on dialog for redownload
        }

        return NO_ERROR;
    }

    /** Attempt to Connect to MXit */
    public int Connect() throws MXitConnectionFailedException
    {
        if (_conn == null)
        {
            _conn = new SocketConnection(this);
        }

        while (_conn.connectionAttempts < 3)
        {
            System.out.println("Attempt " + _conn.connectionAttempts + " to connect to MXit...");

            if (_connect())
            {
                System.out.println("Connection Successful.");
                Connected = true;
                return NO_ERROR;
            }
            else
            {
                System.out.println("Connection Failed.");
                Connected = false;
            }
        }
        System.out.println("Given Up.");
        // could not connect
        return ERROR;
    }
    
    /** Login to MXit with the currently started user profile */
    public CommandLogin Login() throws MXitConnectionFailedException, MXitLoginFailedException
    {
        // connected successfully, now login
        return _login();
    }

    /** Register your MXit with the currently started user profile */
    public CommandRegister SendRegistration(Calendar birthdate, boolean isMale)
    {
        // registration
        CommandRegister cmd = new CommandRegister();

        cmd.Nickname = CurrentProfile.Nickname;
        cmd.Password = CurrentProfile.HashPin;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        cmd.Birthdate = sdf.format(birthdate.getTime());
        cmd.GenderMale = isMale;
        cmd.DistributionCode = CurrentProfile.getDistributionCode();
        cmd.Capabilities = getCapabilities();
        cmd.Locale = CurrentProfile.Locale;
        cmd.DialingCode = CurrentProfile.DialingCode;
        cmd.Version = getProtocolVersion();
        cmd.Features = getFeatures();
        cmd.location = "";
        cmd.ProtocolVer = "61";

        return (CommandRegister)_conn.SendAndWait(cmd);
    }

    /** Connect over socket */
    private boolean _connect()
    {
        //_conn.Connect(MXitConfiguration.SOCK, MXitConfiguration.PORT);
        if (_conn.Connect(CurrentProfile.GetSocket().split(":")[0], new Integer(CurrentProfile.GetSocket().split(":")[1])))
        {
            return true;
        }
        return false;
    }

    /** Disconnect MXit from socket */
    public boolean Disconnect()
    {
        return _disconnect();
    }

    /** Disconnect from socket */
    private boolean _disconnect()
    {
        currentPresence = MXitPresence.Offline;

        FrmContacts.SetCurrentNetworkProfile(null);

        // disconnected
        MXitDisconnected();

        if(keepAliveThread != null && keepAliveThread.isAlive())
        {
            keepAliveThread.interrupt();
            keepAliveThread = null;
        }

        return _conn.Disconnect();
    }

    /** Login to MXit */
    private CommandLogin _login() throws MXitLoginFailedException
    {
        CommandLogin cmd = new CommandLogin();

        try
        {
            // setup the command before sending
            cmd.PasswordHash = CurrentProfile.HashPin;
            cmd.DistributionCode = CurrentProfile.getDistributionCode();
            cmd.Locale = CurrentProfile.Locale;
            cmd.DialingCode = CurrentProfile.DialingCode;
            cmd.Version = getProtocolVersion();
            cmd.Capabilities = getCapabilities();
            cmd.Features = getFeatures();
            cmd.GetContacts = true;
            cmd.CustomResources = CurrentProfile.GetSplashIds();

            // await the reply by halting here with this thread
            CommandLogin reply = (CommandLogin) _conn.SendAndWait(cmd);

            if (reply != null && reply.ErrorCode == 0)
            {
                setLoggedInState();
            }

            return reply;
        }
        catch (Exception ioe)
        {
            ioe.printStackTrace();

            return null;
        }
    }

    /** Set logged in state */
    public void setLoggedInState()
    {
        LoggedIn = true;
        keepAliveThread = new Thread(new KeepAlive());
        keepAliveThread.start();
    }

    /** Logout of MXit */
    public void Logout()
    {
        if (LoggedIn)
            _logout();
    }
    
    /** Logout of MXit */
    private void _logout()
    {
        CommandLogout logout = (CommandLogout)_conn.SendAndWait(new CommandLogout());

        Disconnect();
    }

    /** Send a standard MXitMessage to a MXitContact */
    public void SendMXitMessage(MXitMessage message)
    {
        CommandSendMessage cmd = new CommandSendMessage();

        cmd.MXitID = message.MXitID;
        cmd.Msg = message.Body;
        cmd.MsgType = String.valueOf(MXitMessageType.GetMXitMessageType(message.Type));
        cmd.Flags = String.valueOf(message.Flags);

        _conn.Send(cmd);
    }

    /** Fetches a users extended profile data */
    public CommandGetExtenededProfile GetExtendedProfile(String MXitID)
    {
        CommandGetExtenededProfile cmd = new CommandGetExtenededProfile();
        cmd.MXitID = MXitID;

        cmd = (CommandGetExtenededProfile)_conn.SendAndWait(cmd);

        return cmd;
    }

    /** Sets the attributes specified */
    public void SetExtendedProfile(String password, HashMap<String, String> attr)
    {
        CommandSetExtenededProfile cmd = new CommandSetExtenededProfile();
        cmd.attributes = attr;

        _conn.Send(cmd);
    }

    /** Sets the current presence */
    public void setPresence(MXitPresence presence, String StatusMessage)
    {
        CommandShowPresence cmd = new CommandShowPresence();
        cmd.Presence = MXitPresence.getIndex(presence);
        cmd.Status = StatusMessage;

        _conn.Send(cmd);
    }

    /** Sets the user's mood */
    public void SetMood(Mood mood)
    {
        CommandSetMood cmd = new CommandSetMood();

        cmd.Mood = mood.getIndex();

        _conn.Send(cmd);
    }

    /** Issues a command to the server to change the user avatar */
    protected void SendSetAvatarRequest(BufferedImage newImage)
    {
        CommandMultimediaMessageSetAvatar cmdSetAvatar = new CommandMultimediaMessageSetAvatar();
        
        cmdSetAvatar.avatar = newImage;

        _conn.Send(cmdSetAvatar);
    }

    /** Issues a command to the server to reject a specified file */
    public void SendRejectFile(long fileId, byte reason, String mimeType)
    {
        CommandMultimediaMessageFileReject cmdRejectFile = new CommandMultimediaMessageFileReject();

        cmdRejectFile.Id = fileId;
        cmdRejectFile.Reason = reason;
        cmdRejectFile.MimeType = mimeType;

        _conn.Send(cmdRejectFile);
    }

    /** Issues a command to the server to get a specified part of a file */
    public void SendGetFile(long fileId, int offset, int length)
    {
        CommandMultimediaMessageFileGet cmdGetFile = new CommandMultimediaMessageFileGet();

        cmdGetFile.Id = fileId;
        cmdGetFile.Offset = offset;
        cmdGetFile.Length = length;

        _conn.Send(cmdGetFile);
    }

    /** Sends a data file to the specified contacts */
    public void SendFile(String name, String mimeType, String description, ArrayList<String> contacts, byte[] fileData)
    {
        CommandMultimediaMessageFileSend cmdSendFile = new CommandMultimediaMessageFileSend();

        cmdSendFile.Name = name;
        cmdSendFile.MimeType = mimeType;
        cmdSendFile.Description = description;
        cmdSendFile.recipients = contacts;
        cmdSendFile.Data = fileData;

        _conn.Send(cmdSendFile);
    }

    /** Forwards a file to the specified contacts */
    public void ForwardFile(long id, String name, String description, ArrayList<String> contacts)
    {
        CommandMultimediaMessageFileForward cmdForwardFile = new CommandMultimediaMessageFileForward();

        cmdForwardFile.Id = id;
        cmdForwardFile.Name = name;
        cmdForwardFile.Description = description;
        cmdForwardFile.recipients = contacts;

        _conn.Send(cmdForwardFile);
    }

    public void SendGetAvatarsRequest(ArrayList<AvatarRequest> requests)
    {
        CommandMultimediaMessageGetAvatars getAvatarsRequest = new CommandMultimediaMessageGetAvatars();
        getAvatarsRequest.requests = requests;

        _conn.Send(getAvatarsRequest);
    }

    public java.awt.Image SendAndWaitAvatarRequest(AvatarRequest request)
    {
        CommandMultimediaMessageGetAvatars getAvatarsRequest = new CommandMultimediaMessageGetAvatars();
        getAvatarsRequest.requests = new ArrayList<AvatarRequest>();
        getAvatarsRequest.requests.add(request);

        // wait to get the message
        CommandMultimediaMessage cmd = (CommandMultimediaMessage)_conn.SendAndWait(getAvatarsRequest);
        CommandMultimediaMessageGetAvatars response = new CommandMultimediaMessageGetAvatars(cmd);

        response.parseData(cmd.GetMultimediaData());

        return java.awt.Toolkit.getDefaultToolkit().createImage(response.requests.get(0).fileData);
    }

    /** Updates a contacts information */
    public void UpdateContactInfo(MXitContact contact)
    {
        CommandUpdateContactInfo cmdUpdateInfo = new CommandUpdateContactInfo();

        cmdUpdateInfo.MXitID = contact.MXitID;
        cmdUpdateInfo.Nickname = contact.Nickname;

        // update groups
        switch(contact.SubType)
        {
            case A:
            case P:
            case R:
            case D:
                cmdUpdateInfo.Group = contact.backupGroup;
                break;
            default:
                cmdUpdateInfo.Group = contact.Group;
                break;
        }

        _conn.Send(cmdUpdateInfo);
    }
    
    /** Renames the group for the specified contacts */
    public void RenameGroup(String groupName, TreeMap<String, String> contacts)
    {
        CommandRenameGroup cmdRenameGroup = new CommandRenameGroup();
        
        cmdRenameGroup.Group = groupName;
        cmdRenameGroup.Contacts = contacts;
        
        _conn.Send(cmdRenameGroup);
    }

    /** Removes the group and optionally the contacts of the specified contacts */
    public void RemoveGroup(boolean deleteContacts, TreeMap<String, String> contacts)
    {
        CommandRemoveGroup cmdRemoveGroup = new CommandRemoveGroup();

        cmdRemoveGroup.deleteContacts = deleteContacts;
        cmdRemoveGroup.Contacts = contacts;

        _conn.Send(cmdRemoveGroup);
    }

    /** Gets the pending subscriptions */
    public void GetSubscriptions()
    {
        CommandGetNewSubscription cmd = new CommandGetNewSubscription();

        _conn.Send(cmd);
    }

    /** Adds a contact */
    public void AddContact(String loginName, MXitContact contact, String group, String InviteMessage)
    {
        CommandInviteContact cmdAddContact = new CommandInviteContact();

        cmdAddContact.Group = group;
        cmdAddContact.LoginName = loginName;
        cmdAddContact.Nickname = contact.Nickname;
        cmdAddContact.ContactType = contact.Type;
        cmdAddContact.InviteMessage = InviteMessage;
        
        _conn.Send(cmdAddContact);
    }

    /** Allows a contact invite */
    public void AllowSubscription(String MXitID, String Nickname)
    {
        CommandAllowSubscription cmdAllowSubscription = new CommandAllowSubscription();

        cmdAllowSubscription.MXitID = MXitID;
        cmdAllowSubscription.Nickname = Nickname;

        _conn.Send(cmdAllowSubscription);
    }

    /** Deny a contact invite */
    public void DenySubscription(String MXitID)
    {
        CommandDenySubscription cmdDenySubscription = new CommandDenySubscription();

        cmdDenySubscription.MXitID = MXitID;
        currentContactList.RemoveContact(MXitID);
        MXitContactListUpdated(currentContactList);

        _conn.Send(cmdDenySubscription);
    }

    /** Block a contact invite */
    public void BlockSubscription(String MXitID)
    {
        CommandBlockSubscription cmdBlockSubscription = new CommandBlockSubscription();

        cmdBlockSubscription.MXitID = MXitID;
        currentContactList.RemoveContact(MXitID);
        MXitContactListUpdated(currentContactList);

        _conn.Send(cmdBlockSubscription);
    }

    /** Removes a contact */
    public void RemoveContact(String MXitID)
    {
        CommandRemoveContact cmdRemoveContact = new CommandRemoveContact();

        cmdRemoveContact.MXitID = MXitID;

        _conn.Send(cmdRemoveContact);
    }

    /** Add new MultiMX */
    public String AddMultiMx(String name, ArrayList<String> invitedContacts, String groupName)
    {
        CommandNewMultiMX cmdNewMultiMX = new CommandNewMultiMX();

        cmdNewMultiMX.RoomName = name;
        cmdNewMultiMX.Contacts = invitedContacts;
        cmdNewMultiMX.GroupName = groupName == null ? "" : groupName;

        return ((CommandNewMultiMX)_conn.SendAndWait(cmdNewMultiMX)).RoomId;
    }

    /** Adds a member to the multimx */
    public void AddMultiMxMember(String roomId, ArrayList<String> invitedContacts)
    {
        CommandMultiMXAddMember cmdAddMemberMultiMX = new CommandMultiMXAddMember();

        cmdAddMemberMultiMX.RoomId = roomId;
        cmdAddMemberMultiMX.Contacts = invitedContacts;

        _conn.Send(cmdAddMemberMultiMX);

        //return ((CommandMultiMXAddMember)_conn.SendAndWait(cmdAddMemberMultiMX)).RoomId;
    }

    /** incoming commands */
    public Command IncomingCommand(final Command cmd)
    {
        if (cmd instanceof CommandUnknown)
        {
            System.out.println("Unknown Command Received");
            return null;
        }

        if (cmd == null || cmd.Type == null)
        {
            return null;
        }
        else
        {
            System.out.println("Command Recieved - " + cmd.Type + "\n");        // handle the commands according to type
        }

        // handle errors
        if (cmd.ErrorCode != 0)
        {
            try
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        JOptionPane.showMessageDialog(Main.menuScreen, cmd.ErrorMessage, "MXit Warning...", JOptionPane.ERROR_MESSAGE);
                    }
                });
            }catch (Exception e)
            {

            }
        }
        else
        {
            switch (cmd.Type)
            {
                case GetContacts:
                {
                    MXitContactList incomingList = new MXitContactList();
                    incomingList.UpdateList(this, ((CommandGetContacts) cmd).contacts);
                    currentContactList.UpdateList(this, incomingList);
                    MXitContactListUpdated(incomingList);
                    break;
                }
                case GetNewMessages:
                {
                    // get new message
                    MXitMessage m = ((CommandGetNewMessages) cmd).message;
                    // add to contact in contact list
                    if (currentContactList.AppendMessage(m))
                    {
                        // unread messages = true
                        unreadMessages = true;
                        // send through to instance of MXitManager
                        MXitMessageRecieved(m);
                    }
                    break;
                }
                case GetPresence:
                {
                    // update the current mxit contacts presence
                    MXitContactPresenceUpdated(((CommandGetPresence)cmd).contacts);
                    break;
                }
                case Multimedia:
                {
                    CommandMultimediaMessage mCmd = (CommandMultimediaMessage)cmd;

                    // deal with incoming multimedia messages in specific ways
                    switch(mCmd.multimediaType)
                    {
                        case GetAvatars:
                        {
                            CommandMultimediaMessageGetAvatars aCmd = new CommandMultimediaMessageGetAvatars(mCmd);
                            aCmd.parseData(mCmd.GetMultimediaData());

                            MXitAvatarReceived(aCmd.requests);
                        }
                        break;
                        case CustomResource:
                        {
                            CommandMultimediaMessageCustomResource aCmd = new CommandMultimediaMessageCustomResource(mCmd);
                            aCmd.parseData(mCmd.GetMultimediaData());

                            MXitSplashReceived(aCmd);
                        }
                        break;
                        case OfferFile:
                        {
                            CommandMultimediaMessageFileOffer aCmd = new CommandMultimediaMessageFileOffer(mCmd);
                            aCmd.parseData(mCmd.GetMultimediaData());

                            MXitFileOfferReceived(aCmd);
                        }
                        break;
                        case GetFile:
                        {
                            CommandMultimediaMessageFileGet aCmd = new CommandMultimediaMessageFileGet(mCmd);
                            aCmd.parseData(mCmd.GetMultimediaData());

                            MXitFileGetReceived(aCmd);
                        }
                        break;
                    }
                    break;
                }
                case GetNewSubscriptions:
                {
                    CommandGetNewSubscription sCmd = (CommandGetNewSubscription)cmd;

                    MXitSubscriptionsReceived(sCmd.subscriptions);

                    break;
                }
            }
        }

        return cmd;
    }

    /** Returns the distributor code */
    private static String getProtocolVersion()
    {
        return (MXitConfiguration.DIST + "-" + MXitConfiguration.MX_VER + "-" + MXitConfiguration.CAT + "-" + MXitConfiguration.PLAT);
    }

    /** Returns the bitset of features for MXit client */
    private static int getFeatures()
    {
        return FeatureType.ALERT_PROFILES | FeatureType.AUDIO | FeatureType.CAMERA |
               FeatureType.COMMANDS | FeatureType.CUSTOM_EMOTICONS | FeatureType.ENCRYPTION |
               FeatureType.EXTENDED_MARKUP | FeatureType.FILE_ACCESS | FeatureType.FILE_TRANSFER |
               FeatureType.FORMS | FeatureType.INLINE_IMAGES | FeatureType.MARKUP | FeatureType.MIDP2 |
               FeatureType.SELECT_CONTACT | FeatureType.SKINS | FeatureType.VIBES | FeatureType.VOICE_RECORDER;
    }

    /** Returns the HashMap of capabilities for the client */
    private static HashMap<String, String> getCapabilities()
    {
        HashMap<String, String> capabilities = new HashMap<String, String>();

        capabilities.put("cid" ,"DP");
        capabilities.put("utf8", "true");

        return capabilities;
    }

    /** Get a MXitContact using a mxitID */
    public synchronized MXitContact getContactFromMXitID(String mxitID)
    {
        MXitContact c = null;

        synchronized (currentContactList)
        {
            ArrayList<MXitContact> contacts = currentContactList.GetContacts();

            for (MXitContact contact : contacts)
            {
                if (contact.MXitID.equals(mxitID))
                {
                    return contact;
                }
            }
        }

        return c;
    }

    /** The Class That runs the KeepAlive thread */
    private class KeepAlive implements Runnable
    {

        @Override
        /** This method is a thread that runs to keep this session to MXit alive */
        public void run()
        {
            System.out.println("Started KeepAlive thread to MXit.");
            
            while (LoggedIn)
            {
                try
                {
                    // sleep for 4 minutes
                    Thread.sleep(240000);

                    CommandKeepAlive cmdKeepAlive = new CommandKeepAlive();
                    cmdKeepAlive.MXitID = CurrentProfile.MXitID;

                    _conn.Send(cmdKeepAlive);
                }
                catch (InterruptedException ie)
                {
                    System.out.println("Killed KeepAlive thread to MXit.");
                    //ie.printStackTrace();
                    return;
                }
            }
        }
    }

    /** Incoming MXit message recieved */
    public abstract void MXitMessageRecieved(MXitMessage message);

    /** Contact list updated, refresh accordingly */
    public abstract void MXitContactListUpdated(MXitContactList contacts);

    /** Contact avatars updated, refresh accordingly */
    public abstract void MXitAvatarReceived(ArrayList<AvatarRequest> avatars);

    /** Splash custom resource packet received */
    public abstract void MXitSplashReceived(CommandMultimediaMessageCustomResource cmd);

    /** File offer packet was received, handle accordingly */
    public abstract void MXitFileOfferReceived(CommandMultimediaMessageFileOffer cmd);

    /** File get packet was received, handle accordingly */
    public abstract void MXitFileGetReceived(CommandMultimediaMessageFileGet cmd);

    /** Subscription requests received */
    public abstract void MXitSubscriptionsReceived(ArrayList<MXitSubscription> subscriptions);

    /** Contact presences have updated */
    public abstract void MXitContactPresenceUpdated(ArrayList<MXitContactPresence> presences);

    /** MXit Disconnect Occured */
    public abstract void MXitDisconnected();
}