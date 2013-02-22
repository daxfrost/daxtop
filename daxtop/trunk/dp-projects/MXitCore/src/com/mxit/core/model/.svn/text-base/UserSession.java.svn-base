package com.mxit.core.model;

import com.mxit.core.MXitManager;
import com.mxit.core.interfaces.DownloadListener;
import com.mxit.core.protocol.command.multimedia.model.AvatarRequest;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;


/**
 * @author Dax Booysen
 *
 * A class used to store session data
 */
public final class UserSession
{
    /** Attributes */
    public String fullName;
    public String gender;
    public String hideMXitID;
    public String birthDate;
    public String StatusMessage;
    public String prevStatusMessages;
    public String avatarId;
    public String lastModified;

    /** Internal listing of MultiMX's */
    private HashMap<String, String> OwnedMultiMX = null;

    /** Internal listing of download listeners */
    private HashMap<Long, DownloadListener> FileDownloads = null;

    /** The current user avatar */
    public Image Avatar  = null;

    /** Sets the current session profile data */
    public UserSession(HashMap<String, String> attributes)
    {
        prevStatusMessages = attributes.get("prevstatusmsg");
        gender = attributes.get("gender");
        fullName = attributes.get("fullname");
        StatusMessage = attributes.get("statusmsg");
        avatarId = attributes.get("avatarid");
        lastModified = attributes.get("lastmodified");
        birthDate = attributes.get("birthdate");
        hideMXitID = attributes.get("hidenumber");
    }

    /** Starts a thread to fetch the avatar */
    public void LoadAvatar(MXitManager manager, String MXitID)
    {
        AvatarFetcher af = new AvatarFetcher();

        ArrayList<AvatarRequest> requests = new ArrayList<AvatarRequest>();
        AvatarRequest r = new AvatarRequest();
        r.AvatarID = avatarId;
        r.MXitID = MXitID;
        r.Format = "png";
        r.BitDepth = 24;
        r.sizes = new int[] { 48 };
        requests.add(r);

        af.requests = requests;
        af.manager = manager;

        af.start();
    }

    /** Adds a multimx owned by this user */
    public void AddMultiMX(String Name, String RoomId)
    {
        if (OwnedMultiMX == null)
        {
            OwnedMultiMX = new HashMap<String, String>();
        }

        OwnedMultiMX.put(Name, RoomId);
    }

    /** Checks if this MultiMX is owned */
    public String isMultiMXOwned(String Name)
    {
        if (OwnedMultiMX != null && OwnedMultiMX.containsKey(Name))
        {
            return OwnedMultiMX.get(Name);
        }

        return null;
    }

    /** Adds a file download */
    public void AddFileDownload(long Id, DownloadListener dl)
    {
        if (FileDownloads == null)
        {
            FileDownloads = new HashMap<Long, DownloadListener>();
        }

        FileDownloads.put(Id, dl);
    }

    /** Gets a file download */
    public DownloadListener GetFileDownload(long Id)
    {
        if (FileDownloads != null)
        {
            return FileDownloads.get(Long.valueOf(Id));
        }

        return null;
    }
}

