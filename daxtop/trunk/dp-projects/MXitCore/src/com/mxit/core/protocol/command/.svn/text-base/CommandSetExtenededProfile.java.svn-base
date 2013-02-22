package com.mxit.core.protocol.command;

import com.mxit.core.protocol.type.*;
import com.mxit.core.protocol.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

/** GetContacts Command */
public class CommandSetExtenededProfile extends Command
{
    // Members:
    public String password = "";

    public HashMap<String, String> attributes = new HashMap<String, String>();

    public final String BIRTHDATE = "birthdate";
    public final String GENDER = "gender";
    public final String HIDENUMBER = "hidenumber";
    public final String FULLNAME = "fullname";
    public final String STATUSMSG = "statusmsg";
    public final String PREVSTATUSMSG = "prevstatusmsgs";
    public final String AVATARID = "avatarid";
    public final String LASTMODIFIED = "lastmodified";

    /** Creates a new Login Command */
    public CommandSetExtenededProfile()
    {
        Type = CommandType.SetExtendedProfile;
    }

    @Override
    public void parseData(byte[] cmdData)
    {
    }
    
    @Override
    public void buildData(DataOutputStream sb) throws IOException
    {
        sb.write(password.getBytes("UTF-8"));
        sb.write(FIELD_SEP);
        sb.write(String.valueOf(attributes.size()).getBytes());

        for(String attribute : attributes.keySet())
        {
            sb.write(FIELD_SEP);
            sb.write(attribute.getBytes("UTF-8"));
            sb.write(FIELD_SEP);
            if (attribute.equals(FULLNAME) || attribute.equals(STATUSMSG))
            {
                // string type = 10
                sb.write(String.valueOf(10).getBytes("UTF-8"));
            }
            else if (attribute.equals(GENDER) || attribute.equals(HIDENUMBER))
            {
                // boolean type = 2
                sb.write(String.valueOf(2).getBytes("UTF-8"));
            }
            else if (attribute.equals(BIRTHDATE))
            {
                // date time type = 18
                sb.write(String.valueOf(18).getBytes("UTF-8"));
            }
            else if (attribute.equals(LASTMODIFIED))
            {
                // date time type = 5
                sb.write(String.valueOf(5).getBytes("UTF-8"));
            }
            sb.write(FIELD_SEP);
            sb.write(attributes.get(attribute).getBytes("UTF-8"));
        }
    }
}