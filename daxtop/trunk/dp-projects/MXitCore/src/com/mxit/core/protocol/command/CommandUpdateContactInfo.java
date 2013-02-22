package com.mxit.core.protocol.command;

import com.mxit.core.protocol.type.*;
import com.mxit.core.protocol.*;
import java.io.DataOutputStream;
import java.io.IOException;

/** GetContacts Command */
public class CommandUpdateContactInfo extends Command
{
    // Members:
    public String MXitID = "";
    public String Nickname = "";
    public String Group = "";

    /** Creates a new Login Command */
    public CommandUpdateContactInfo()
    {
        Type = CommandType.UpdateContact;
    }

    @Override
    public void parseData(byte[] cmdData)
    {
    }
    
    @Override
    public void buildData(DataOutputStream sb) throws IOException
    {
        sb.write(encodeUTF8(Group));
        sb.write(FIELD_SEP);
        sb.write(MXitID.getBytes());
        sb.write(FIELD_SEP);
        sb.write(encodeUTF8(Nickname));
    }
}