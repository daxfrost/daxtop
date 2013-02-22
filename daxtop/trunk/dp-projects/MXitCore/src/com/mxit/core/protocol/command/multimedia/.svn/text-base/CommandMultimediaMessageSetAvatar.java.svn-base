package com.mxit.core.protocol.command.multimedia;

import com.mxit.core.protocol.command.CommandMultimediaMessage;
import com.mxit.core.protocol.type.MultimediaType;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;

/** Multimedia Message Command */
public class CommandMultimediaMessageSetAvatar extends CommandMultimediaMessage
{
    /** Store id - anything other than 0 will signify ignoring the avatar data bytes */
    public long storeId;

    /** crc */
    public int crc;

    /** the avatar image */
    public BufferedImage avatar;

    /** The status returned by the server after setting */
    public int Status;

    /** Creates a new Set Avatar Multimedia Message Command */
    public CommandMultimediaMessageSetAvatar(CommandMultimediaMessage cmd)
    {
        multimediaType = cmd.multimediaType;
        dataLength = cmd.dataLength;
    }

    /** Creates a new Set Avatar Multimedia Message Command */
    public CommandMultimediaMessageSetAvatar()
    {
        super();
        
        multimediaType = MultimediaType.SetAvatar;
    }

    @Override
    public void parseData(byte[] cmdData)
    {
        // only start parsing at index 5
        try
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(Arrays.copyOfRange(cmdData, 5,cmdData.length));
            DataInputStream dis = new DataInputStream(bais);

            Status = dis.readInt();
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

            // get the image data
            ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
            ImageIO.write(avatar, "PNG", imageStream);
            byte[] data = imageStream.toByteArray();

            // first build up data
            dos.writeLong(storeId);
            dos.writeInt(data.length); // file size
            dos.writeInt(crc);
            dos.write(data);

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
