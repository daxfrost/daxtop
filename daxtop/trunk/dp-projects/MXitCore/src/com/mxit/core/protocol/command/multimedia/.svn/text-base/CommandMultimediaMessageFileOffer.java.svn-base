package com.mxit.core.protocol.command.multimedia;

import com.mxit.core.protocol.command.CommandMultimediaMessage;
import com.mxit.core.protocol.type.MultimediaType;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

/** Multimedia Message Command */
public class CommandMultimediaMessageFileOffer extends CommandMultimediaMessage
{
    /** The file offer id */
    public long Id;

    /** The sender address */
    public String ContactAddress;

    /** Size of the file in bytes */
    public int Size;

    /** The name of the offered file */
    public String Name;

    /** The mime type of the file offer */
    public String MimeType;

    /** Timestamp of when the file offer was sent */
    public long TimeSent;

    /** The description of the file offer */
    public String Description;

    /** The alternative method of downloading this file */
    public String AlternateDownloadRoute;

    /** The file offer flags */
    public int Flags;

    /** Creates a new Custom Resource Multimedia Message Command */
    public CommandMultimediaMessageFileOffer(CommandMultimediaMessage cmd)
    {
        multimediaType = cmd.multimediaType;
        dataLength = cmd.dataLength;
    }

    /** Creates a new Custom Resource Multimedia Message Command */
    public CommandMultimediaMessageFileOffer()
    {
        super();
        
        multimediaType = MultimediaType.OfferFile;
    }

    @Override
    public void parseData(byte[] cmdData)
    {
        // only start parsing at index 5
        try
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(Arrays.copyOfRange(cmdData, 5,cmdData.length));
            DataInputStream dis = new DataInputStream(bais);

            Id = dis.readLong();
            ContactAddress = dis.readUTF();
            Size = dis.readInt();
            Name = dis.readUTF();
            MimeType = dis.readUTF();
            TimeSent = dis.readLong();
            Description = dis.readUTF();
            AlternateDownloadRoute = dis.readUTF();
            Flags = dis.readInt();
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    @Override
    public void buildData(DataOutputStream sb) throws IOException
    {
    }
}
