package com.mxit.core.protocol.command;

import com.mxit.core.protocol.type.*;
import com.mxit.core.protocol.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/** Multimedia Message Command */
public class CommandMultimediaMessage extends Command
{
    // Members:

    public MultimediaType multimediaType;
    public int dataLength;
    private byte _type = -1;
    protected byte[] _data;

    /** Creates a new Multimedia Command */
    public CommandMultimediaMessage()
    {
        Type = CommandType.Multimedia;
    }

    @Override
    public void parseData(byte[] cmdData)
    {
        try
        {
            _data = cmdData;

            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(cmdData));

            // process type
            multimediaType = MultimediaType.GetCommandType(dis.readByte());

            // convert size
            dataLength = dis.readInt();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    @Override
    public void buildData(DataOutputStream sb) throws IOException
    {
        try
        {
            sb.write(MultimediaType.GetNumericFromCommand(multimediaType));
            sb.writeInt(_data.length);
            sb.write(_data);
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    /** Converts integers to bytes */
    protected byte[] integerToBytes(int val) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(val);
        return baos.toByteArray();
    }

    /** Converts integer that is as small as a byte to a byte */
    protected byte[] integerToByte(int val) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeByte(val);
        return baos.toByteArray();
    }

    /** Converts short to bytes */
    protected byte[] shortToBytes(short val) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeShort(val);
        return baos.toByteArray();
    }

    /** Converts short to bytes */
    protected byte[] stringToUTF8(String val) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(val);
        return baos.toByteArray();
    }

    /** Gets the multimedia message data byte array */
    public byte[] GetMultimediaData()
    {
        return _data;
    }
}
