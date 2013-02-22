package com.mxit.core.protocol.command;

import com.mxit.core.protocol.type.*;
import com.mxit.core.protocol.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.TreeMap;

/** Removes the group and optionally the contacts to which a contact or set of contacts belong */
public class CommandRemoveGroup extends Command
{
    // Should the contacts be deleted as well
    public boolean deleteContacts = false;
    // Map of the contact's address and alias. The map is MXitId - Alias
    public TreeMap<String, String> Contacts = new TreeMap<String, String>();

    /** Creates a new RemoveGroup Command */
    public CommandRemoveGroup()
    {
        Type = CommandType.RemoveGroup;
    }

    @Override
    public void parseData(byte[] cmdData)
    {
    }

    @Override
    public void buildData(DataOutputStream sb) throws IOException
    {
        // add whether or not we must delete the contacts with the group
        sb.write(String.valueOf(deleteContacts ? 1 : 0).getBytes());
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
