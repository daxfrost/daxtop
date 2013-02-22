package com.mxit.core.protocol.command;

import com.mxit.core.model.MXitSubscription;
import com.mxit.core.model.type.MXitContactPresence;
import com.mxit.core.model.type.MXitContactType;
import com.mxit.core.model.type.MXitPresence;
import com.mxit.core.protocol.type.*;
import com.mxit.core.protocol.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/** GetNewSubscription Command */
public class CommandAllowSubscription extends Command
{
    public String MXitID = "";
    public String Group = "";
    public String Nickname = "";

    /** Creates a new AllowSubscription Command */
    public CommandAllowSubscription()
    {
        Type = CommandType.AllowSubscription;
    }

    @Override
    public void parseData(byte[] cmdData)
    {
    }

    @Override
    public void buildData(DataOutputStream sb) throws IOException
    {
        sb.write(MXitID.getBytes());
        sb.write(FIELD_SEP);
        sb.write(Group.getBytes());
        sb.write(FIELD_SEP);
        sb.write(Nickname.getBytes());

        /*sb.append(PasswordHash);
        sb.append(FIELD_SEP);
        sb.append(Version);
        sb.append(FIELD_SEP);
        sb.append(GetContacts ? 1 : 0);
        sb.append(FIELD_SEP);
        if(Capabilities != null)
        {
        for(int i = 0; i < Capabilities.size(); i++)
        {
        sb.append(Capabilities.keySet().toArray()[i]);
        sb.append("=");
        sb.append(Capabilities.values().toArray()[i]);
        sb.append(";");
        }
        }
        sb.append(FIELD_SEP);
        sb.append(DistributionCode);
        sb.append(FIELD_SEP);
        sb.append(Features);
        sb.append(FIELD_SEP);
        sb.append(DialingCode);
        sb.append(FIELD_SEP);
        sb.append(Locale);
        sb.append(RECORD_SEP);
        sb.append(CUSTOM_RES);
        if(CustomResources != null)
        {
        for(int i = 0; i < CustomResources.length; i++)
        {
        sb.append(CustomResources[i]);
        sb.append(FIELD_SEP);
        }
        }
        //sb.append("7UhuSNbTp4i0Fsi5tKjz8aKMZzjAM4BocXIRj9e36iU=E-5.6.3-J-Windows Mobile1BE609896-8072-49B6-9699-B4C1DEFA8A4112876727en\0cr=");
         */
    }
}
