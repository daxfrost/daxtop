package com.mxit.core.model;

import com.dax.list.item.DaxListGroupItem;
import com.mxit.MXitNetwork;
import com.mxit.core.MXitManager;
import com.mxit.core.model.type.MXitContactPresence;
import com.mxit.core.model.type.MXitContactSubType;
import com.mxit.core.model.type.MXitContactType;
import com.mxit.core.model.type.MXitPresence;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;

/**
 * @author  - Dax Booysen
 * @company - MXit Lifestyle
 * 
 * The MXitContactList is the list of contacts that is updated from time to time and
 * refreshed when a status or change is made in MXit.
 */
public class MXitContactList
{
    // Members
    private ArrayList<MXitContact> ContactList;    // contact type filter bitsets
    public static final int MXit = 0x4;
    public static final int Services = 0x8;
    public static final int Gateways = 0x10;
    public static final int Chatrooms = 0x20;
    public static final int All = 0x40;
    public static final int Info = 0x1000;
    public static final int Gallery = 0x2000;
    public static final int Jabber = 0x4000;

    /** Constructs a new MXitContactList for usage */
    public MXitContactList()
    {
        ContactList = new ArrayList<MXitContact>();
    }

    /** Constructs a new MXitContactList copying other contact list */
    public MXitContactList(MXitContactList list)
    {
        //ArrayList<MXitContact> contactsToCopy = list.GetContacts();

        // TODO = make a copy of the contacts list
    }

    /** Removes all contacts from the contact list */
    public void Clear()
    {
        synchronized (ContactList)
        {
            ContactList.clear();
        }
    }

    /** Used by the core libraries to update the contact list */
    public synchronized void UpdateList(MXitManager manager, MXitContact[] contactList)
    {
        synchronized (ContactList)
        {
            boolean found;

            // check if the list is completely empty (first getcontacts)
            if (ContactList.isEmpty())
            {
                for (MXitContact mc : contactList)
                {
                    // strangely the backend sends empty first contact, so skip...
                    if (mc == null)
                    {
                        continue;
                    }

                    // update groups
                    if (mc.SubType == MXitContactSubType.A)
                    {
                        mc.backupGroup = mc.Group;
                        mc.Group = MXitContact.INVITE_RECEIVED;
                    }
                    else if (mc.SubType == MXitContactSubType.P)
                    {
                        mc.backupGroup = mc.Group;
                        mc.Group = MXitContact.INVITE_SENT;
                    }
                    else if (mc.SubType == MXitContactSubType.R)
                    {
                        mc.backupGroup = mc.Group;
                        mc.Group = MXitContact.INVITE_REJECTED;
                    }
                    else if (mc.SubType == MXitContactSubType.D)
                    {
                        mc.backupGroup = mc.Group;
                        mc.Group = MXitContact.INVITE_DELETED;
                    }

                    ContactList.add(mc);
                }

                return;
            }

            // otherwise loop through contacts and add
            for (MXitContact newContact : contactList)
            {
                // strangely the backend sends empty first contact, so skip...
                if (newContact == null)
                {
                    continue;
                }
                found = false;

                // update groups
                if (newContact.SubType == MXitContactSubType.A)
                {
                    newContact.backupGroup = newContact.Group;
                    newContact.Group = MXitContact.INVITE_RECEIVED;
                }
                else if (newContact.SubType == MXitContactSubType.P)
                {
                    newContact.backupGroup = newContact.Group;
                    newContact.Group = MXitContact.INVITE_SENT;
                }
                else if (newContact.SubType == MXitContactSubType.R)
                {
                    newContact.backupGroup = newContact.Group;
                    newContact.Group = MXitContact.INVITE_REJECTED;
                }
                else if (newContact.SubType == MXitContactSubType.D)
                {
                    newContact.backupGroup = newContact.Group;
                    newContact.Group = MXitContact.INVITE_DELETED;;
                }

                for (MXitContact existingContact : ContactList)
                {
                    if (newContact.MXitID.equalsIgnoreCase(existingContact.MXitID))
                    {
                        found = true;

                        // update existing
                        existingContact.Flags = newContact.Flags;
                        existingContact.Group = newContact.Group;
                        existingContact.Presence = new MXitContactPresence(manager, newContact.Presence);
                        existingContact.Nickname = newContact.Nickname;
                        existingContact.Presence = newContact.Presence;
                        existingContact.Type = newContact.Type;
                    }
                }

                // add new
                if (!found)
                {
                    ContactList.add(newContact);
                }
            }
        }
    }

    /** Gets a specific contact */
    public synchronized MXitContact GetContact(String MXitID)
    {
        synchronized (ContactList)
        {
            for(MXitContact c : ContactList)
            {
                if(c.MXitID.equals(MXitID))
                    return c;
            }

            return null;
        }
    }

    /** This method sorts the contacts in the traditional mxit contact list fashion */
    public synchronized void SortContactListTraditionalMXitStyle()
    {
        synchronized (ContactList)
        {
            ArrayList<MXitContact> unsorted = new ArrayList<MXitContact>(ContactList);
            ArrayList<MXitContact> onlineList = new ArrayList<MXitContact>(),
                    offlineList = new ArrayList<MXitContact>(),
                    awayList = new ArrayList<MXitContact>(),
                    dndList = new ArrayList<MXitContact>(),
                    groupList = new ArrayList<MXitContact>();

            // first seperate into online, away, dnd and offline
            for (MXitContact c : unsorted)
            {
                // if in a group order seperately
                if (!c.Group.isEmpty())
                {
                    groupList.add(c);
                    continue;
                }
                
                // else order as normal
                switch (c.Presence.presence)
                {
                    case Online:
                        onlineList.add(c);
                        break;
                    case Away:
                        awayList.add(c);
                        break;
                    case Offline:
                        offlineList.add(c);
                        break;
                    case DND:
                        dndList.add(c);
                        break;
                }
            }

            // sort each alphabetically
            onlineList = SortContactListAlphabetically(onlineList);
            awayList = SortContactListAlphabetically(awayList);
            dndList = SortContactListAlphabetically(dndList);
            offlineList = SortContactListAlphabetically(offlineList);

            this.ContactList.clear();

            this.ContactList.addAll(onlineList);
            this.ContactList.addAll(awayList);
            this.ContactList.addAll(dndList);
            this.ContactList.addAll(offlineList);

//            // groups
            ArrayList<MXitContact> group;
//            // sort group list on its own
            while (!groupList.isEmpty())
            {
                group = new ArrayList<MXitContact>();
                String currentGroup = "";

                for (MXitContact c : groupList)
                {
                    if (currentGroup.length() < 1)
                    {
                        currentGroup = c.Group;
                    }
                    if (currentGroup.equals(c.Group))
                    {
                        group.add(c);
                    }
                }
                
                groupList.removeAll(group);

                group = SortGroupListTraditionalMXitStyle(group);
                
                this.ContactList.addAll(group);
                group.clear();
            }
        }
    }

    /** This method sorts the contacts in the traditional mxit contact list fashion */
    public static ArrayList<MXitContact> SortGroupListTraditionalMXitStyle(ArrayList<MXitContact> contacts)
    {
        ArrayList<MXitContact> unsorted = new ArrayList<MXitContact>(contacts);
        ArrayList<MXitContact> onlineList = new ArrayList<MXitContact>(),
                offlineList = new ArrayList<MXitContact>(),
                awayList = new ArrayList<MXitContact>(),
                dndList = new ArrayList<MXitContact>();

        // first seperate into online, away, dnd and offline
        for (MXitContact c : unsorted)
        {
            switch (c.Presence.presence)
            {
                case Online:
                    onlineList.add(c);
                    break;
                case Away:
                    awayList.add(c);
                    break;
                case Offline:
                    offlineList.add(c);
                    break;
                case DND:
                    dndList.add(c);
                    break;
            }
        }

        // sort each alphabetically
        onlineList = SortContactListAlphabetically(onlineList);
        awayList = SortContactListAlphabetically(awayList);
        dndList = SortContactListAlphabetically(dndList);
        offlineList = SortContactListAlphabetically(offlineList);

        contacts.clear();

        contacts.addAll(onlineList);
        contacts.addAll(awayList);
        contacts.addAll(dndList);
        contacts.addAll(offlineList);

        return contacts;
    }

    /** Sorts this MXitContactList alphabetically */
    public static ArrayList<MXitContact> SortContactListAlphabetically(ArrayList<MXitContact> unsorted)
    {
        ArrayList<MXitContact> sorted = new ArrayList<MXitContact>();

        String[] nicknames = new String[unsorted.size()];

        int i = 0;

        for (MXitContact c : unsorted)
        {
            nicknames[i++] = c.Nickname;
        }
        Arrays.sort(nicknames, String.CASE_INSENSITIVE_ORDER);

        for (i = 0; i < nicknames.length; i++)
        {
            for (MXitContact c : unsorted)
            {
                if (nicknames[i].equals(c.Nickname))
                {
                    sorted.add(c);
                    unsorted.remove(c);
                    break;
                }
            }
        }

        return sorted;
    }

    /** Correctly order groups */
    public static LinkedHashMap<String, DaxListGroupItem> SortGroups(LinkedHashMap<String, DaxListGroupItem> unsorted)
    {
        LinkedHashMap<String, DaxListGroupItem> sorted = new LinkedHashMap<String, DaxListGroupItem>();

        DaxListGroupItem received, sent, rejected, deleted, blocked;
        received = unsorted.remove(MXitContact.INVITE_RECEIVED);
        sent = unsorted.remove(MXitContact.INVITE_SENT);
        rejected = unsorted.remove(MXitContact.INVITE_REJECTED);
        deleted = unsorted.remove(MXitContact.INVITE_DELETED);

        String[] groups = new String[unsorted.size()];

        int i = 0;

        for (String c : unsorted.keySet())
        {
            groups[i++] = c;
        }

        // sort
        Arrays.sort(groups, String.CASE_INSENSITIVE_ORDER);

        // add system groups at the top
        if (received!=null)
            sorted.put(MXitContact.INVITE_RECEIVED, received);
        if (sent!=null)
            sorted.put(MXitContact.INVITE_SENT, sent);
        if (rejected!=null)
            sorted.put(MXitContact.INVITE_REJECTED, rejected);
        if (deleted!=null)
            sorted.put(MXitContact.INVITE_DELETED, deleted);

        for (String group : groups)
        {
            sorted.put(group, unsorted.get(group));
        }

        return sorted;
    }

    /** Used by the core libraries to update the contact list */
    public synchronized void UpdateList(MXitManager manager, MXitContactList contacts)
    {
        synchronized (ContactList)
        {
            outer:
            for (MXitContact c : contacts.GetContacts())
            {
                if (c == null)
                {
                    continue;                // check if contact already exists
                }
                for (MXitContact cOld : ContactList)
                {
                    if (c.MXitID.equalsIgnoreCase(cOld.MXitID))
                    {
                        cOld.MXitID = c.MXitID;
                        cOld.Nickname = c.Nickname;
                        cOld.Group = c.Group;
                        cOld.Type = c.Type;
                        cOld.Presence = new MXitContactPresence(manager, c.Presence);
                        cOld.Presence = c.Presence;
                        cOld.Flags = c.Flags;
                        cOld.SubType = c.SubType;
                        continue outer;
                    }                // otherwise add contact
                }
                ContactList.add(c);
            }
        }
    }

    /** Will return false if no contact with the message's MXitID */
    public boolean AppendMessage(MXitMessage message)
    {
        synchronized (ContactList)
        {
            for (MXitContact c : ContactList)
            {
                if (message.MXitID.equalsIgnoreCase(c.MXitID))
                {
                    c.messages.add(message);
                    return true;
                }
            }

            return false;
        }
    }

    /** Returns all contacts in the list as an ArrayList */
    public ArrayList<MXitContact> GetContacts()
    {
        return ContactList;
    }

    /** Returns a filtered set of contacts in the list as an ArrayList */
    public synchronized ArrayList<MXitContact> GetContacts(int contactFilter, boolean hideOffline, boolean hideInvites)
    {
        synchronized (ContactList)
        {
            ArrayList<MXitContact> contacts = new ArrayList<MXitContact>(ContactList);
            ArrayList<MXitContact> newContacts = new ArrayList<MXitContact>();

            if ((contactFilter & All) == 0)
            {
                for (MXitContact c : contacts)
                {
                    if ((MXitContactType.getMXitContactType(c.Type) & contactFilter) != 0)
                    {
                        newContacts.add(c);
                    }
                }

                // use newly generated list
                contacts = newContacts;
            }

            // hide offline
            if (hideOffline)
            {
                ArrayList<MXitContact> onlineList = new ArrayList<MXitContact>();

                for(MXitContact c : contacts)
                {
                    if (c.Presence.presence != MXitPresence.Offline || (!hideInvites && c.SubType != MXitContactSubType.B))
                    {
                        onlineList.add(c);
                    }
                }

                return onlineList;
            }

            return contacts;
        }
    }

    /** Returns the available groups */
    public synchronized Object[] GetGroups(boolean allowNewGroup, boolean allowNoGroup)
    {
        ArrayList<String> groups = new ArrayList<String>();

        synchronized (ContactList)
        {
            for(MXitContact c : ContactList)
            {
                if (!c.Group.isEmpty() && !c.Group.equals(MXitContact.INVITE_RECEIVED) && !c.Group.equals(MXitContact.INVITE_DELETED) && !c.Group.equals(MXitContact.INVITE_REJECTED) && !c.Group.equals( MXitContact.INVITE_SENT))
                    if (!groups.contains(c.Group))
                        groups.add(c.Group);
            }
        }

        // sort alphabetically
        Collections.sort(groups, String.CASE_INSENSITIVE_ORDER);

        if (allowNewGroup)
            groups.add(0, "Add New Group...");
        
        if (allowNoGroup)
            groups.add(0, "No Group");

        return groups.toArray();
    }

    /** Removes a specific contact from the contact list */
    public synchronized void RemoveContact(String MXitID)
    {
        synchronized (ContactList)
        {
            for(MXitContact c : ContactList)
            {
                if (c.MXitID.equals(MXitID))
                {
                    ContactList.remove(c);
                    return;
                }
            }
        }
    }
}
