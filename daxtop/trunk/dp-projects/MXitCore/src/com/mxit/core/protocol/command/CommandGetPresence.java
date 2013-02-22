package com.mxit.core.protocol.command;

import com.mxit.core.model.Mood;
import com.mxit.core.model.type.MXitContactPresence;
import com.mxit.core.model.type.MXitPresence;
import com.mxit.core.protocol.type.*;
import com.mxit.core.protocol.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/** GetPresence Command */
public class CommandGetPresence extends Command
{
    public ArrayList<MXitContactPresence> contacts;

    /** Creates a new Login Command */
    public CommandGetPresence()
    {
        Type = CommandType.GetPresence;
    }

    @Override
    public void parseData(byte[] cmdData)
    {
        String data = decodeUTF8(cmdData);
        String dataRecords[] = data.split(String.valueOf(RECORD_SEP));

        contacts = new ArrayList<MXitContactPresence>();

        MXitContactPresence c;

        for (int i = 0; i < dataRecords.length; i++)
        {
            c = new MXitContactPresence();

            String fields[] = dataRecords[i].split(String.valueOf(FIELD_SEP));

            if (fields.length < 3)
            {
                continue;
            }
            c.MXitID = fields[0];
            c.presence = MXitPresence.getMXitPresence(Integer.valueOf(fields[1]));
            c.mood = Mood.indexOf(Integer.valueOf(fields[2]));
            if(fields.length > 3)
                c.CustomMood = fields[3];
            if(fields.length > 4)
                c.StatusMessage = fields[4];
            if(fields.length > 5)
                c.AvatarID = fields[5];

            contacts.add(c);
        }
    }

    @Override
    public void buildData(DataOutputStream sb) throws IOException
    {
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
