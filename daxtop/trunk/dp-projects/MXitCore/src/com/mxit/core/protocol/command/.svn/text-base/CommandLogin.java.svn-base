package com.mxit.core.protocol.command;

import com.mxit.core.protocol.type.*;
import com.mxit.core.protocol.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

/** Login Command */
public class CommandLogin extends Command
{
    protected static final String CUSTOM_RES = "cr=";
    
    // Members:
    public String                   PasswordHash = "";
    public String                   Version = "";
    public boolean                  GetContacts = false;
    public HashMap<String, String>  Capabilities;
    public String                   DistributionCode = "";
    public int                      Features;
    public String                   DialingCode;
    public String                   Locale = "";
    public String[]                 CustomResources;
    
    // const login errors
    public static final int ErrorInvalidPassword = 3;
    public static final int ErrorRedirectToProxy = 16;
    
    /** Creates a new Login Command */
    public CommandLogin()
    {
        Type = CommandType.Login;
    }

    @Override
    public void parseData(byte[] cmdData)
    {
        
    }

    @Override
    public void buildData(DataOutputStream sb) throws IOException
    {
         sb.write(PasswordHash.getBytes());
         sb.write(FIELD_SEP);
         sb.write(Version.getBytes());
         sb.write(FIELD_SEP);
         sb.write(String.valueOf(GetContacts ? 1 : 0).getBytes());
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
         sb.write(CUSTOM_RES.getBytes());
         if(CustomResources != null)
         {
             for(int i = 0; i < CustomResources.length; i++)
             {
                sb.write(CustomResources[i].getBytes());
                sb.write(FIELD_SEP);
             }
         }
    }
}