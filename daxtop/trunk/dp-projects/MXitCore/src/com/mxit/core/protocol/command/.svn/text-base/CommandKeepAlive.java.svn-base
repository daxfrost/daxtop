package com.mxit.core.protocol.command;

import com.mxit.core.protocol.type.*;
import com.mxit.core.protocol.*;
import java.io.DataOutputStream;
import java.io.IOException;

/** SendMessage Command */
public class CommandKeepAlive extends Command
{
    // Members:
    public String MXitID      = null;
    
    /** Creates a new Login Command */
    public CommandKeepAlive()
    {
        Type = CommandType.KeepAlive;
    }

    @Override
    public void parseData(byte[] cmdData)
    {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void buildData(DataOutputStream sb) throws IOException
    {
         sb.write(MXitID.getBytes());
    }
}