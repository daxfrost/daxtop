package com.mxit.core.protocol.command;

import com.mxit.core.model.MXitContact;
import com.mxit.core.model.Mood;
import com.mxit.core.model.type.MXitContactSubType;
import com.mxit.core.model.type.MXitContactType;
import com.mxit.core.model.type.MXitPresence;
import com.mxit.core.protocol.type.*;
import com.mxit.core.protocol.*;
import java.io.DataOutputStream;
import java.io.IOException;

/** GetContacts Command */
public class CommandGetContacts extends Command
{
    // Members:
    public MXitContact[] contacts;
    
    /** Creates a new Login Command */
    public CommandGetContacts()
    {
        Type = CommandType.GetContacts;
    }

    @Override
    public void parseData(byte[] cmdData)
    {
        String data = decodeUTF8(cmdData);
        String dataRecords[] = data.split(String.valueOf(RECORD_SEP));
        
        contacts = new MXitContact[dataRecords.length];
       
        MXitContact c;
        
        for(int i = 0; i < dataRecords.length; i++)
        {
            c = new MXitContact();
            
            String fields[] = dataRecords[i].split(String.valueOf(FIELD_SEP));
            
            if(fields.length < 3)
                continue;
             c.Group = fields[0];
             c.MXitID = fields[1];
             c.Nickname = fields[2];
             c.Presence.presence = MXitPresence.getMXitPresence(Integer.valueOf(fields[3])); // fields[3] != null && !fields[3].isEmpty() ? fields[3] : null;//(PresenceType)Integer.parseInt(fields[3]) : PresenceType.None;
             c.Type = MXitContactType.getMXitContactType(Integer.parseInt(fields[4]));
             c.Presence.mood = Mood.indexOf(Integer.valueOf(fields[5]));//Integer.parseInt(fields[5]) : "NoMoodType";
             c.Flags = fields[6];//Integer.parseInt(fields[6]) : "NoFlags";
             c.SubType = MXitContactSubType.getMXitContactType(fields[7].charAt(0));

             contacts[i] = c;
        }
    }
    
    @Override
    public void buildData(DataOutputStream sb) throws IOException
    {
    }
}