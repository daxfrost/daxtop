package com.mxit.core.model;

import com.mxit.core.model.type.MXitMessageFlags;
import com.mxit.core.model.type.MXitMessageType;
import com.mxit.core.model.type.MessageTxType;

/**
 * @author  - Dax Booysen
 * @company - MXit Lifestyle
 * 
 * The MXitMessage is the basic building block of a conversation, it contains
 * various values relating to the contact it was sent To or From, the attributes
 * of the message and ofcourse the message data which is made up of a collection
 * of objects each with their own types and may be used to format and order 
 * the message as content.
 */
public class MXitMessage
{
    // constants
    public static final int NORMAL_TYPE = 1;
    public static final int CHAT_TYPE = 2;
    public static final int CUSTOM_FORM_TYPE = 6;
    public static final int COMMAND_TYPE = 7;
    
    // variables
    public String  MXitID = null;
    public String  Body = null;
    public MXitMessageType Type = MXitMessageType.Normal;
    public int     Flags = 0;
    public long    TimeStamp = 0;
    
    public MessageTxType TxType;
    
    // settings
    private boolean _markupEnabled = false;
    private boolean _vibesEnabled = false;
    private boolean _customEmoticonsEnabled = false;
    
    // message id (received only)
    public String  MsgId = null;
    
    /** private - no empty constructor for a mxit message */
    private MXitMessage() { }
    
    /** Constructs a MXitMessage with the minimum message requirements */
    public MXitMessage(String jabberId, String messageBody, MXitMessageType messageType, int messageFlags, MessageTxType txType)
    {
        MXitID = jabberId;
        Body = messageBody;
        Type = messageType;
        Flags = messageFlags;
        if (TimeStamp == 0)
            TimeStamp = System.currentTimeMillis();

        // flags contain markup
        if((messageFlags & MXitMessageFlags.MARKUP) == MXitMessageFlags.MARKUP)
            _markupEnabled = true;
        // flags contain custom emoticons
        if((messageFlags & MXitMessageFlags.CUSTOM_EMOTICONS) == MXitMessageFlags.CUSTOM_EMOTICONS)
            _customEmoticonsEnabled = true;
    }

    /** Constructs a MXitMessage with the minimum message requirements and a timestamp */
    public MXitMessage(String jabberId, String messageBody, MXitMessageType messageType, int messageFlags, MessageTxType txType, long time)
    {
        this(jabberId, messageBody, messageType, messageFlags, txType);

        // set the time
        TimeStamp = time;
    }
}
