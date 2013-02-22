package com.mxit.core.protocol.command;

import com.mxit.core.protocol.type.*;
import com.mxit.core.protocol.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/** New MultiMX Command */
public class CommandNewMultiMX extends Command
{
    // Members:
    public String RoomName = null;
    public ArrayList<String> Contacts = null;
    public String GroupName = "";

    public String RoomId;

    /** Creates a new Login Command */
    public CommandNewMultiMX()
    {
        Type = CommandType.AddNewGroupChat;
    }

    @Override
    public void parseData(byte[] cmdData)
    {
        RoomId = new String(cmdData);
    }
    
    @Override
    public void buildData(DataOutputStream sb) throws IOException
    {
        sb.write(RoomName.getBytes());
        sb.write(FIELD_SEP);
        sb.write(String.valueOf(Contacts.size()).getBytes());
        for (String MXitId : Contacts)
        {
            sb.write(FIELD_SEP);
            sb.write(MXitId.getBytes());
        }
        sb.write(FIELD_SEP);
        sb.write(GroupName.getBytes());
    }
}