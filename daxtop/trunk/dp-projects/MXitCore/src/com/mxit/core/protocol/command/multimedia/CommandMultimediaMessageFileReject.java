package com.mxit.core.protocol.command.multimedia;

import com.mxit.core.protocol.command.CommandMultimediaMessage;
import com.mxit.core.protocol.type.MultimediaType;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/** Multimedia Message Command */
public class CommandMultimediaMessageFileReject extends CommandMultimediaMessage
{
    /** The file offer id */
    public long Id;

    /** Reason the file was rejected */
    public byte Reason;

    /** A possible alternatively supported mime type  if not supported */
    public String MimeType;

    /** Creates a new File Reject Multimedia Message Command */
    public CommandMultimediaMessageFileReject(CommandMultimediaMessage cmd)
    {
        multimediaType = cmd.multimediaType;
        dataLength = cmd.dataLength;
    }

    /** Creates a new Custom Resource Multimedia Message Command */
    public CommandMultimediaMessageFileReject()
    {
        super();
        
        multimediaType = MultimediaType.OfferFile;
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
            dos.writeByte(Reason); // reason for rejection
            dos.writeUTF(MimeType);

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
