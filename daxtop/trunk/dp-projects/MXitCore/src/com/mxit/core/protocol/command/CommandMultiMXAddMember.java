package com.mxit.core.protocol.command;

import com.mxit.core.protocol.type.*;
import com.mxit.core.protocol.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/** New MultiMXAddMember Command */
public class CommandMultiMXAddMember extends Command
{
    // Members:
    public String RoomId = null;
    public ArrayList<String> Contacts = null;
    public String GroupName = "";

    /** Creates a new MultiMXAddMember Command */
    public CommandMultiMXAddMember()
    {
        Type = CommandType.InviteGroupChatMembers;
    }

    @Override
    public void parseData(byte[] cmdData)
    {
        RoomId = new String(cmdData);
    }
    
    @Override
    public void buildData(DataOutputStream sb) throws IOException
    {
        sb.write(RoomId.getBytes());
        sb.write(FIELD_SEP);
        sb.write(String.valueOf(Contacts.size()).getBytes());
        for (String MXitId : Contacts)
        {
            sb.write(FIELD_SEP);
            sb.write(MXitId.getBytes());
        }
    }
}