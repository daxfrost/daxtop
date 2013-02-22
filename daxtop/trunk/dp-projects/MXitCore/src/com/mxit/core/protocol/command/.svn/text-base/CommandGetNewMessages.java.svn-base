package com.mxit.core.protocol.command;

import com.mxit.core.model.MXitMessage;
import com.mxit.core.model.type.MXitMessageType;
import com.mxit.core.model.type.MessageTxType;
import com.mxit.core.protocol.type.*;
import com.mxit.core.protocol.*;
import java.io.DataOutputStream;
import java.io.IOException;

/** GetNewMessages Command */
public class CommandGetNewMessages extends Command
{
    // Members
    private String  MXitID      = null;
    private long    DateTime    = 0;
    private int     MsgType     = 0;
    private String  MsgId       = "";
    private int     Flags       = 0;
    private String  Msg         = null;
    
    // public objects
    public MXitMessage message = null;
    
    /** Creates a new Login Command */
    public CommandGetNewMessages()
    {
        Type = CommandType.GetNewMessages;
    }

    @Override
    public void parseData(byte[] cmdData)
    {        
        String data = decodeUTF8(cmdData);
        String dataRecords[] = data.split(String.valueOf(RECORD_SEP));
        
        if(!(dataRecords.length>0))
            return;
        
        String[] msgFields = dataRecords[0].split(String.valueOf(FIELD_SEP));
        
        MXitID = msgFields[0];
        DateTime = Long.parseLong(msgFields[1]) * 1000;
        MsgType = Integer.valueOf(msgFields[2]);
        if(msgFields.length>3)
            MsgId = msgFields[3].equals("") ? "-1" : msgFields[3].toString();
        if(msgFields.length>4)
            Flags = Integer.valueOf(msgFields[4]);

        // actual message data
        Msg = dataRecords.length < 2 ? "" : dataRecords[1];

        // create the message object to be returned
        message = new MXitMessage(MXitID, Msg, MXitMessageType.GetMXitMessageType(MsgType), Flags, MessageTxType.Received, DateTime);
    }

    @Override
    public void buildData(DataOutputStream sb) throws IOException
    {
        // throw new UnsupportedOperationException("Not supported yet.");
    }
}