package com.mxit.core.protocol.command.multimedia;

import com.mxit.core.protocol.command.CommandMultimediaMessage;
import com.mxit.core.protocol.type.MultimediaType;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/** Multimedia Message Command */
public class CommandMultimediaMessageFileSend extends CommandMultimediaMessage
{
    /** The recipients */
    public ArrayList<String> recipients;

    /** The name of the file */
    public String Name;

    /** The mime type of the file */
    public String MimeType;

    /** The description of the file */
    public String Description;

    /** The CRC of the file */
    public int CRC = 0;

    /** The byte data of the file */
    public byte[] Data;

    /** Creates a new File Send Multimedia Message Command */
    public CommandMultimediaMessageFileSend(CommandMultimediaMessage cmd)
    {
        multimediaType = cmd.multimediaType;
        dataLength = cmd.dataLength;
    }

    /** Creates a new Send File Multimedia Message Command */
    public CommandMultimediaMessageFileSend()
    {
        super();
        
        multimediaType = MultimediaType.SendFileDirect;
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
            dos.writeInt(Data.length);
            dos.writeShort(recipients.size());
            for(String recipient : recipients)
            {
                dos.writeUTF(recipient);
            }
            dos.writeUTF(Name);
            dos.writeUTF(MimeType);
            dos.writeUTF(Description);
            dos.writeInt(CRC); // CRC can be 0
            dos.write(Data);

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
