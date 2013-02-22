package com.mxit.core.protocol.command;

import com.mxit.core.protocol.type.*;
import com.mxit.core.protocol.*;
import java.io.DataOutputStream;
import java.io.IOException;

/** Login Command */
public class CommandLogout extends Command
{
    /** Creates a new Login Command */
    public CommandLogout()
    {
        Type = CommandType.Logout;
    }

    @Override
    public void parseData(byte[] cmdData)
    {
        
    }

    @Override
    public void buildData(DataOutputStream sb) throws IOException
    {
         sb.write("0".getBytes());
    }
}