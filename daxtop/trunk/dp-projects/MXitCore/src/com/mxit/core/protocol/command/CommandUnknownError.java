package com.mxit.core.protocol.command;

import com.mxit.core.protocol.type.*;
import com.mxit.core.protocol.*;
import java.io.DataOutputStream;
import java.io.IOException;

/** Unknown Command */
public class CommandUnknownError extends Command
{
    /** Creates a new Unknown Command */
    public CommandUnknownError()
    {
        Type = CommandType.UnknownError;
    }

    @Override
    public void parseData(byte[] cmdData)
    {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void buildData(DataOutputStream sb) throws IOException
    {
    }
}