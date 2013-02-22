package com.mxit.core.protocol.command.multimedia;

import com.mxit.core.protocol.command.CommandMultimediaMessage;
import com.mxit.core.protocol.type.MultimediaType;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

/** Multimedia Message Command */
public class CommandMultimediaMessageCustomResource extends CommandMultimediaMessage
{
    /** The resource id */
    public String ResourceId;

    /** The handle either plas1 or plas2 */
    public String SplashNameHandle;

    /** 0 = update, 1 = remove */
    public byte Operation;

    /** The total length of all chunks to follow */
    public int TotalChunksLength;

    /** 0 - TOPLEFT, 3 - HCENTER_VCENTER  */
    public byte Anchor;

    /** Time to show (in seconds), 0 - Forever  */
    public byte TimeToShow;

    /** The background colour of the image */
    public int bgColour;

    /** The image data */
    public byte[] imgData;

    /** Creates a new Custom Resource Multimedia Message Command */
    public CommandMultimediaMessageCustomResource(CommandMultimediaMessage cmd)
    {
        multimediaType = cmd.multimediaType;
        dataLength = cmd.dataLength;
    }

    /** Creates a new Custom Resource Multimedia Message Command */
    public CommandMultimediaMessageCustomResource()
    {
        super();
        
        multimediaType = MultimediaType.CustomResource;
    }

    @Override
    public void parseData(byte[] cmdData)
    {
        // only start parsing at index 5
        try
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(Arrays.copyOfRange(cmdData, 5,cmdData.length));
            DataInputStream dis = new DataInputStream(bais);

            ResourceId = dis.readUTF();
            SplashNameHandle = dis.readUTF();
            Operation = dis.readByte();
            TotalChunksLength = dis.readInt();

            /** If there are chunks to be processed */
            if (TotalChunksLength > 0)
            {
                Anchor = dis.readByte();
                TimeToShow = dis.readByte();
                bgColour = dis.readInt();
                imgData = new byte[dis.available()];
                dis.readFully(imgData);
            }
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
