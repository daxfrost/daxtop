package com.mxit.core.model.type;

import com.mxit.core.MXitManager;
import com.mxit.core.model.AvatarFetcher;
import com.mxit.core.model.MXitContact;
import com.mxit.core.model.Mood;
import com.mxit.core.protocol.command.multimedia.model.AvatarRequest;
import java.util.ArrayList;

/**
 * @author Dax Booysen
 * Holds a contacts presence values
 */
public class MXitContactPresence
{
    public String MXitID = "";

    /** The contact presence */
    public MXitPresence presence = MXitPresence.Offline;
    
    /** The contact mood */
    public Mood mood = null;

    /** The avatar Id */
    public String AvatarID = "";

    /** The status message */
    public String StatusMessage;

    /** The custom mood */
    public String CustomMood;

    /** Hidden Constructor */
    public MXitContactPresence() { }

    /** Constructor */
    public MXitContactPresence(MXitManager manager, MXitContactPresence p)
    {
        MXitID = p.MXitID;
        presence = p.presence;
        StatusMessage = p.StatusMessage;
        CustomMood = p.CustomMood;

        if (manager.CurrentProfile.avatars.containsKey(p.AvatarID.toLowerCase() + "|" + MXitID))
        {
            MXitContact con = manager.currentContactList.GetContact(MXitID);
            if (!con.Presence.AvatarID.toLowerCase().equals(p.AvatarID.toLowerCase()))
                manager.CurrentProfile.avatars.remove(con.Presence.AvatarID.toLowerCase() + "|" + con.MXitID);
            con.avatar = manager.CurrentProfile.avatars.get(p.AvatarID.toLowerCase() + "|" + MXitID).getImage();
        }
        else if(!AvatarID.toLowerCase().equals(p.AvatarID.toLowerCase()))
        {
            AvatarFetcher af = new AvatarFetcher();

            ArrayList<AvatarRequest> requests = new ArrayList<AvatarRequest>();
            AvatarRequest r = new AvatarRequest();
            r.AvatarID = p.AvatarID;
            r.MXitID = MXitID;
            r.Format = "png";
            r.BitDepth = 24;
            r.sizes = new int[] { 32 };
            requests.add(r);

            af.requests = requests;
            af.manager = manager;

            af.start();
        }
        AvatarID = p.AvatarID;
        mood = p.mood;
    }
}
