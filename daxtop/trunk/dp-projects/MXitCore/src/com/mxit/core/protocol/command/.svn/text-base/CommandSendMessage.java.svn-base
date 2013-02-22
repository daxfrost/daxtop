package com.mxit.core.protocol.command;

import com.mxit.core.protocol.type.*;
import com.mxit.core.protocol.*;
import java.io.DataOutputStream;
import java.io.IOException;

/** SendMessage Command */
public class CommandSendMessage extends Command
{
    // Members:
    public String MXitID   = null;
    public String Msg      = null;
    public String MsgType  = null;
    public String Flags    = null;
    
    /** Creates a new Login Command */
    public CommandSendMessage()
    {
        Type = CommandType.SendMessage;
    }

    @Override
    public void parseData(byte[] cmdData)
    {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void buildData(DataOutputStream sb) throws IOException
    {
         sb.write(MXitID.getBytes("UTF-8"));
         sb.write(FIELD_SEP);
         sb.write(Msg.getBytes("UTF-8"));
         sb.write(FIELD_SEP);
         sb.write(MsgType.getBytes("UTF-8"));
         sb.write(FIELD_SEP);
         sb.write(Flags.getBytes("UTF-8"));
    }
}