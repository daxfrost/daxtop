package com.mxit.core.protocol.command;

import com.mxit.core.protocol.type.*;
import com.mxit.core.protocol.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

/** GetContacts Command */
public class CommandGetExtenededProfile extends Command
{
    // Members:
    public String MXitID = "";
    public int attributesCount;
    public HashMap<String, String> attributes;

    private HashMap<String, String> profAttributes = new HashMap<String, String>();

    /** Creates a new Login Command */
    public CommandGetExtenededProfile()
    {
        Type = CommandType.GetExtendedProfile;

        if(profAttributes.isEmpty())
        {
            profAttributes.put("birthdate", "");
            profAttributes.put("gender", "");
            profAttributes.put("hidenumber", "");
            profAttributes.put("fullname", "");
            profAttributes.put("statusmsg", "");
            profAttributes.put("prevstatusmsgs", "");
            profAttributes.put("avatarid", "");
            profAttributes.put("lastmodified", "");
        }
    }

    @Override
    public void parseData(byte[] cmdData)
    {
        if(attributes==null)
            attributes = new HashMap<String, String>(profAttributes);

        String data = decodeUTF8(cmdData);
        String dataRecords[] = data.split(String.valueOf(FIELD_SEP));
        
        MXitID = dataRecords[0];

        attributesCount = Integer.parseInt(dataRecords[1]);

        for(int i = 2; i < dataRecords.length; i++)
        {
            String incomingAttribute = dataRecords[i];

            for(String attribute : attributes.keySet())
            {
                if(attribute.equals(incomingAttribute))
                {
                    attributes.put(attribute, dataRecords[i+1]);
                    i++;
                    break;
                }
            }
        }
    }
    
    @Override
    public void buildData(DataOutputStream sb) throws IOException
    {
        attributes = new HashMap<String, String>(profAttributes);

        sb.write(MXitID.getBytes());
        sb.write(FIELD_SEP);
        sb.write(String.valueOf(attributes.size()).getBytes());
        sb.write(FIELD_SEP);

        for(String attribute : attributes.keySet())
        {
            sb.write(attribute.getBytes());
            sb.write(FIELD_SEP);
        }
    }
}