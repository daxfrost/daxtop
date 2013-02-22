package com.mxit.core.model;

import com.mxit.core.model.type.MXitCommandType;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author  - Dax Booysen
 * @company - MXit Lifestyle
 * 
 * The MXitCommand can be one of several requests recieved. 
 * These requests are executed according to their type and 
 * value on the client.
 */
public class MXitCommand
{
    // Members
    public MXitCommandType Type = MXitCommandType.Undefined;
    public String selMsg, replyMsg, destination;
    boolean clearMsgScreen, auto;

    // members specific to inline images
    public String src, sid, dat, algn, flow, w, h, xo, yo, fw, fh, t, cap;

    /** Creates a new command */
    public MXitCommand(String commandData)
    {
        selMsg = "1";
        replyMsg = "1";
        destination = "";

        commandData = commandData.substring(0, commandData.length() - 1);
        String[] fields = commandData.split("\\|");

        // safety check that it is not corrupt
        if (fields.length > 1)
        {
            if (fields[0].endsWith("img"))
            {
                Type = MXitCommandType.InlineImage;

                for (String field : fields)
                {
                    String[] kvp = field.split("=", 2);

                    if (kvp[0].equals("src"))
                    {
                        src = kvp[1];
                    }
                    else if (kvp[0].equals("sid"))
                    {
                        sid = kvp[1];
                    }
                    else if (kvp[0].equals("dat"))
                    {
                        dat = kvp[1];
                    }
                    else if (kvp[0].equals("algn"))
                    {
                        algn = kvp[1];
                    }
                    else if (kvp[0].equals("flow"))
                    {
                        flow = kvp[1];
                    }
                    else if (kvp[0].equals("w"))
                    {
                        w = kvp[1];
                    }
                    else if (kvp[0].equals("h"))
                    {
                        h = kvp[1];
                    }
                    else if (kvp[0].equals("xo"))
                    {
                        xo = kvp[1];
                    }
                    else if (kvp[0].equals("yo"))
                    {
                        yo = kvp[1];
                    }
                    else if (kvp[0].equals("fw"))
                    {
                        fw = kvp[1];
                    }
                    else if (kvp[0].equals("fh"))
                    {
                        fh = kvp[1];
                    }
                    else if (kvp[0].equals("t"))
                    {
                        t = kvp[1];
                    }
                    else if (kvp[0].equals("cap"))
                    {
                        cap = kvp[1];
                    }
                }
            }
            else
            {
                for (String field : fields)
                {
                    String[] kvp = field.split("=", 2);

                    if (kvp[0].equals("replymsg"))
                    {
                        try
                        {
                            replyMsg = URLDecoder.decode(kvp.length == 1 ? "" : kvp[1] , "UTF-8");
                        }
                        catch (UnsupportedEncodingException ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                    else if (kvp[0].equals("selmsg"))
                    {
                        try
                        {
                            selMsg = URLDecoder.decode(kvp[1], "UTF-8");
                        }
                        catch (UnsupportedEncodingException ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                    else if (kvp[0].equals("type"))
                    {
                        // this is a reply command
                        if(kvp[1].equals("reply"))
                            Type = MXitCommandType.Reply;
                        else if(kvp[1].equals("clear"))
                            Type = MXitCommandType.Clear_Screen;
                        else if (kvp[1].equals("platreq"))
                            Type = MXitCommandType.Platform_Request;
                    }
                    else if (kvp[0].equals("dest"))
                    {
                        try
                        {
                            destination = URLDecoder.decode(kvp[1], "UTF-8");
                        }
                        catch (UnsupportedEncodingException ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                    else if (kvp[0].equals("clearmsgscreen"))
                    {
                        clearMsgScreen = Boolean.parseBoolean(kvp[1]);
                    }
                    else if (kvp[0].equals("auto"))
                    {
                        auto = Boolean.parseBoolean(kvp[1]);
                    }
                }
            }
        }
        else if (commandData.startsWith("$") && commandData.endsWith("$"))
        {
                replyMsg = commandData.substring(1, commandData.length()-1);
                selMsg = commandData.substring(1, commandData.length()-1);
                Type = MXitCommandType.Reply;
        }
    }

    /** A generic method for all commands */
    public void Process()
    {
        // ::op=cmd|type=reply|replymsg=1|selmsg=1):
        // response = ::type=reply|res=back|err=0:
    }
}
