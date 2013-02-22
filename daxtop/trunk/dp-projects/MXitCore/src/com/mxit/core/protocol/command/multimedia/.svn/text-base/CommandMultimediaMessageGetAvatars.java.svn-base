package com.mxit.core.protocol.command.multimedia;

import com.mxit.core.protocol.command.CommandMultimediaMessage;
import com.mxit.core.protocol.command.multimedia.model.AvatarRequest;
import com.mxit.core.protocol.type.MultimediaType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

/** Multimedia Message Command */
public class CommandMultimediaMessageGetAvatars extends CommandMultimediaMessage
{
    /** Number of avatars */
    public ArrayList<AvatarRequest> requests;

    /** Creates a new Get Avatars Multimedia Message Command */
    public CommandMultimediaMessageGetAvatars(CommandMultimediaMessage cmd)
    {
        multimediaType = cmd.multimediaType;
        dataLength = cmd.dataLength;
    }

    /** Creates a new Get Avatars Multimedia Message Command */
    public CommandMultimediaMessageGetAvatars()
    {
        super();
        
        multimediaType = MultimediaType.GetAvatars;
    }

    @Override
    public void parseData(byte[] cmdData)
    {
        // only start parsing at index 5
        try
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(Arrays.copyOfRange(cmdData, 5,cmdData.length));
            DataInputStream dis = new DataInputStream(bais);

            int numFiles = dis.readInt();
            requests = new ArrayList<AvatarRequest>(numFiles);

            // cycle through incoming avatars
            for(int i = 0; i < numFiles; i++)
            {
                AvatarRequest ar = new AvatarRequest();
                ar.MXitID = dis.readUTF();
                ar.AvatarID = dis.readUTF();
                ar.Format = dis.readUTF();
                ar.BitDepth = dis.readByte();
                ar.crc = dis.readInt();
                ar.file_width = dis.readInt();
                ar.file_height = dis.readInt();
                ar.file_length = dis.readInt();
                ar.fileData = new byte[dis.available()];
                dis.readFully(ar.fileData);
                requests.add(ar);
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
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            // first build up data
            dos.writeInt(requests.size());

            // add for each avatar request
            for (AvatarRequest ar : requests)
            {
                try
                {
                    dos.writeUTF(ar.MXitID);
                    dos.writeUTF(ar.AvatarID);
                    dos.writeUTF(ar.Format);
                    dos.writeByte(ar.BitDepth);
                    dos.writeShort(ar.sizes.length);

                    for(int size : ar.sizes)
                        dos.writeInt(size);
                }
                catch (UnsupportedEncodingException ex)
                {
                    ex.printStackTrace();
                }
            }

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
