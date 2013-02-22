package com.mxit.core.protocol.command;

import com.mxit.core.protocol.type.*;
import com.mxit.core.protocol.*;
import java.io.DataOutputStream;
import java.io.IOException;

/** GetContacts Command */
public class CommandRemoveContact extends Command
{
    // Members:
    public String MXitID = "";

    /** Creates a new Login Command */
    public CommandRemoveContact()
    {
        Type = CommandType.RemoveContact;
    }

    @Override
    public void parseData(byte[] cmdData)
    {
    }
    
    @Override
    public void buildData(DataOutputStream sb) throws IOException
    {
        sb.write(MXitID.getBytes());
    }
}