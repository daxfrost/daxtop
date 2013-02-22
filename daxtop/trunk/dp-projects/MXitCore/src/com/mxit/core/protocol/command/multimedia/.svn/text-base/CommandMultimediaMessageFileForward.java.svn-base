package com.mxit.core.protocol.command.multimedia;

import com.mxit.core.protocol.command.CommandMultimediaMessage;
import com.mxit.core.protocol.type.MultimediaType;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/** Multimedia Message Command */
public class CommandMultimediaMessageFileForward extends CommandMultimediaMessage
{
    /** Id of the file */
    public long Id;

    /** The recipients */
    public ArrayList<String> recipients;

    /** The name of the file */
    public String Name;

    /** The description of the file */
    public String Description;

    /** Creates a new File Forward Multimedia Message Command */
    public CommandMultimediaMessageFileForward(CommandMultimediaMessage cmd)
    {
        multimediaType = cmd.multimediaType;
        dataLength = cmd.dataLength;
    }

    /** Creates a new Send File Multimedia Message Command */
    public CommandMultimediaMessageFileForward()
    {
        super();
        
        multimediaType = MultimediaType.ForwardFileDirect;
    }

    @Override
    public void parseData(byte[] cmdData)
    {
    }

    @Override
    public void buildData(DataOutputStream sb) throws IOException
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            // first build up data
            dos.writeLong(Id);
            dos.writeShort(recipients.size());
            for(String recipient : recipients)
            {
                dos.writeUTF(recipient);
            }
            dos.writeUTF(Name);
            dos.writeUTF(Description);

            // withdraw byte array
            _data = baos.toByteArray();

            // now build header and append data
            super.buildData(sb);
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
}
