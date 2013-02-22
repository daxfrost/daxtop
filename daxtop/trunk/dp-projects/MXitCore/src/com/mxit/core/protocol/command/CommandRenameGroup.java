package com.mxit.core.protocol.command;

import com.mxit.core.protocol.type.*;
import com.mxit.core.protocol.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.TreeMap;

/** Renames the group to which a contact or set of contacts belong */
public class CommandRenameGroup extends Command
{
    // The new name of the group
    public String Group;
    // Map of the contact's address and alias. The map is MXitId - Alias
    public TreeMap<String, String> Contacts = new TreeMap<String, String>();

    /** Creates a new RenameGroup Command */
    public CommandRenameGroup()
    {
        Type = CommandType.RenameGroup;
    }

    @Override
    public void parseData(byte[] cmdData)
    {
    }

    @Override
    public void buildData(DataOutputStream sb) throws IOException
    {
        // add the name of the group
        sb.write(encodeUTF8(Group));
        sb.write(FIELD_SEP);

        // add the number of contacts
        sb.write(String.valueOf(Contacts.size()).getBytes());

        // loop through each contact and add the MXitId and the alias
        for (Entry kvp : Contacts.entrySet())
        {
            sb.write(FIELD_SEP);
            sb.write(String.valueOf(kvp.getKey()).getBytes());
            sb.write(FIELD_SEP);
            sb.write(String.valueOf(kvp.getValue()).getBytes());
        }
    }
}
