package com.mxit.core.model;

import com.dax.list.item.state.MessageState;
import com.mxit.core.model.type.MXitContactPresence;
import com.mxit.core.model.type.MXitContactSubType;
import com.mxit.core.model.type.MXitContactType;
import java.awt.Image;
import java.util.ArrayList;

/**
 * @author  - Dax Booysen
 * @company - MXit Lifestyle
 * 
 * The MXitContact is the basic building block of the contact list, it contains
 * various values relating to the contact, the attributes of the contacts status
 * and the messages recieved from and sent to this contact.
 */
public class MXitContact
{
    // Members
    // TODO - MXitGroup
    public String Group = null;
    public String MXitID = null;
    public String Nickname = null;
    public MXitContactType Type = null;
    public String Flags = null;
    public MXitContactSubType SubType = null;

    public String backupGroup = null;
    public String inviteMsg = null;

    public static final String INVITE_REJECTED = "Invites Rejected";
    public static final String INVITE_RECEIVED = "Invites Received";
    public static final String INVITE_SENT = "Invites Sent";
    public static final String INVITE_DELETED = "Contacts Who Deleted You";

    /** The presence of this MXitContact */
    public MXitContactPresence Presence = new MXitContactPresence();
    
    /** Stores whether a message has been received from this contact */
    public boolean messageReceived = false;
    
    /** Reference to message state here too for contact list updates */
    public MessageState messageState = MessageState.CLEAR;
    
    /** The MXitMessage's sent to and recieved from this contact */
    public ArrayList<MXitMessage> messages = new ArrayList();

    /** The avatar associated with this contact */
    public Image avatar = null;

    /** The status message */
    public String Status = "";

    public MXitContact()
    {
        
    }
}
