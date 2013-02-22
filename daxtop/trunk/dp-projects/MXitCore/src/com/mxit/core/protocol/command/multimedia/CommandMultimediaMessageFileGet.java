package com.mxit.core.protocol.command.multimedia;

import com.mxit.core.protocol.command.CommandMultimediaMessage;
import com.mxit.core.protocol.type.MultimediaType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

/** Multimedia Message Command */
public class CommandMultimediaMessageFileGet extends CommandMultimediaMessage
{
    /** The file get id */
    public long Id;

    /** The position to fetch from */
    public int Offset;

    /** The length to fetch for */
    public int Length;

    /** The CRC of the file */
    public int CRC = 0;

    /** The byte data of the file */
    public byte[] Data;

    /** Creates a new File Get Multimedia Message Command */
    public CommandMultimediaMessageFileGet(CommandMultimediaMessage cmd)
    {
        multimediaType = cmd.multimediaType;
        dataLength = cmd.dataLength;
    }

    /** Creates a new Custom Resource Multimedia Message Command */
    public CommandMultimediaMessageFileGet()
    {
        super();
        
        multimediaType = MultimediaType.GetFile;
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
            Offset = dis.readInt();
            Length = dis.readInt();
            CRC = dis.readInt();
            Data = new byte[dis.available()];
            dis.readFully(Data);
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
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
            dos.writeInt(Offset); // position to start from
            dos.writeInt(Length); // position to end at

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
