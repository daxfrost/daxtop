package com.mxit.core.protocol.command;

import com.mxit.core.protocol.type.*;
import com.mxit.core.protocol.*;
import java.io.DataOutputStream;
import java.io.IOException;

/** SendMessage Command */
public class CommandSetMood extends Command
{
    // Members:
    public int Mood = 0;
    
    /** Creates a new Login Command */
    public CommandSetMood()
    {
        Type = CommandType.SetMood;
    }

    @Override
    public void parseData(byte[] cmdData)
    {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void buildData(DataOutputStream sb) throws IOException
    {
         sb.write(String.valueOf(Mood).getBytes());
    }
}