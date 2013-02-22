package com.mxit.core.protocol.command;

import com.mxit.core.model.Mood;
import com.mxit.core.protocol.type.*;
import com.mxit.core.protocol.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/** Show Status Command */
public class CommandShowPresence extends Command
{
    // Members:
    public int Presence = 1;
    public String Status = "";
    public ArrayList<String> PrevStatusMessages = new ArrayList<String>();
    
    /** Creates a new Login Command */
    public CommandShowPresence()
    {
        Type = CommandType.SetPresence;
    }

    @Override
    public void parseData(byte[] cmdData)
    {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void buildData(DataOutputStream sb) throws IOException
    {
         sb.write(String.valueOf(Presence).getBytes());
         sb.write(FIELD_SEP);
         sb.write(Status.getBytes());
         for(String oldStatus : PrevStatusMessages)
         {
             sb.write(FIELD_SEP);
             sb.write(oldStatus.getBytes());
         }
    }
}