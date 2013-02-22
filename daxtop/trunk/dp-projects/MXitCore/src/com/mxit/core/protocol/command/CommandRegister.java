package com.mxit.core.protocol.command;

import com.mxit.core.protocol.type.*;
import com.mxit.core.protocol.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

/** Register Command */
public class CommandRegister extends Command
{
    protected static final String CUSTOM_RES = "cr=";
    
    // Members:
    public String                   Password = "";
    public String                   Version = "";
    public int                      MaxReply = 170000;
    public String                   Nickname = "";
    public String                   Birthdate = "";
    public boolean                  GenderMale = false;
    public String                   location = "";
    public HashMap<String, String>  Capabilities;
    public String                   DistributionCode = "";
    public int                      Features;
    public String                   DialingCode;
    public String                   Locale = "";
    public String                   ProtocolVer = "59";
    
    /** Creates a new Register Command */
    public CommandRegister()
    {
        Type = CommandType.Register;
    }

    @Override
    public void parseData(byte[] cmdData)
    {
        
    }

    @Override
    public void buildData(DataOutputStream sb) throws IOException
    {
         sb.write(Password.getBytes());
         sb.write(FIELD_SEP);
         sb.write(Version.getBytes());
         sb.write(FIELD_SEP);
         sb.write(String.valueOf(MaxReply).getBytes());
         sb.write(FIELD_SEP);
         sb.write(Nickname.getBytes());
         sb.write(FIELD_SEP);
         sb.write(Birthdate.getBytes());
         sb.write(FIELD_SEP);
         sb.write(String.valueOf(GenderMale ? 1 : 0).getBytes());
         sb.write(FIELD_SEP);
         sb.write(location.getBytes());
         sb.write(FIELD_SEP);
         if(Capabilities != null)
         {
             for(int i = 0; i < Capabilities.size(); i++)
             {
                 sb.write(Capabilities.keySet().toArray()[i].toString().getBytes());
                 sb.write("=".getBytes());
                 sb.write(Capabilities.values().toArray()[i].toString().getBytes());
                 sb.write(";".getBytes());
             }
         }
         sb.write(FIELD_SEP);
         sb.write(DistributionCode.getBytes());
         sb.write(FIELD_SEP);
         sb.write(String.valueOf(Features).getBytes());
         sb.write(FIELD_SEP);
         sb.write(DialingCode.getBytes());
         sb.write(FIELD_SEP);
         sb.write(Locale.getBytes());
         sb.write(RECORD_SEP);
         sb.write(ProtocolVer.getBytes());
         sb.write(RECORD_SEP);
    }
}