package com.mxit.core.protocol.command;

import com.mxit.core.model.type.MXitContactType;
import com.mxit.core.protocol.type.*;
import com.mxit.core.protocol.*;
import java.io.DataOutputStream;
import java.io.IOException;

/** GetContacts Command */
public class CommandInviteContact extends Command
{
    // Members:
    public String MXitID = "";
    public String LoginName = "";
    public String Group = "";
    public String Nickname = "";
    public MXitContactType ContactType = MXitContactType.MXit;
    public String InviteMessage = "";

    /** Creates a new Login Command */
    public CommandInviteContact()
    {
        Type = CommandType.AddNewContact;
    }

    @Override
    public void parseData(byte[] cmdData)
    {
        String data = new String(cmdData);
        String dataRecords[] = data.split(String.valueOf(RECORD_SEP));

        MXitID = dataRecords[0];
    }
    
    @Override
    public void buildData(DataOutputStream sb) throws IOException
    {
        sb.write(Group.getBytes());
        sb.write(FIELD_SEP);
        sb.write(LoginName.getBytes());
        sb.write(FIELD_SEP);
        sb.write(Nickname.getBytes());
        sb.write(FIELD_SEP);
        sb.write(String.valueOf(MXitContactType.getMXitContactType(ContactType)).getBytes());
        sb.write(FIELD_SEP);
        sb.write(InviteMessage.getBytes());
    }
}