package com.dax;

import java.awt.Rectangle;
import java.security.MessageDigest;
import java.util.ArrayList;

/**
 * @author Dax Booysen
 *
 * A holding profile for other profiles
 */
public class DaxtopProfile
{
    /** The username for this profile */
    public String Username = "";

    /** Password */
    private String Password = null;

    /** The last skin used by this profile */
    public String lastUsedSkin = "Hydro Tank";

    /** Sets the daxtop profile to auto login if set as default profile */
    public boolean RememberPassword = false;

    /** 0 = toaster off, 1 = toaster on, 2 = toaster permanent */
    public int toasterSetting = 1;

    /** Global mute of sound on daxtop */
    public boolean MuteSound = false;

    /** Check for updates after startup */
    public boolean checkForUpdates = true;

    /** The default network */
    public String defaultNetwork = null;

    /** The tab overview setting */
    public String tabOverview = "Carousel";

    /** the tab placement */
    public byte tabPlacement = 0;

    /** The chat screen and contacts screen location and size */
    public Rectangle contactsScreen = null, chatScreen = null;

    /** The profiles associated with this dp profile */
    public ArrayList<Profile> profiles = new ArrayList<Profile>();

    public synchronized static DaxtopProfile LoadProfile(DaxtopProfileData daxtopProfileData)
    {
        if (daxtopProfileData == null)
            return null;

        DaxtopProfile profile = new DaxtopProfile();

        try
        {
            profile.Username = (String) daxtopProfileData.GetAttribute("Username", profile.Username);
            profile.Password = (String) daxtopProfileData.GetAttribute("Password", profile.Password);
            profile.lastUsedSkin = (String) daxtopProfileData.GetAttribute("LastUsedSkin", profile.lastUsedSkin);
            profile.RememberPassword = (Boolean) daxtopProfileData.GetAttribute("RememberPassword", profile.RememberPassword);
            profile.toasterSetting = (Integer) daxtopProfileData.GetAttribute("ToasterSetting", profile.toasterSetting);
            profile.MuteSound = (Boolean) daxtopProfileData.GetAttribute("MuteSound", profile.MuteSound);
            profile.checkForUpdates = (Boolean) daxtopProfileData.GetAttribute("CheckForUpdate", profile.checkForUpdates);
            profile.defaultNetwork = (String) daxtopProfileData.GetAttribute("DefaultNetwork", profile.defaultNetwork);
            profile.tabOverview = (String) daxtopProfileData.GetAttribute("TabOverview", profile.tabOverview);
            profile.tabPlacement = (Byte) daxtopProfileData.GetAttribute("TabPlacement", profile.tabPlacement);
            profile.contactsScreen = (Rectangle) daxtopProfileData.GetAttribute("ContactsScreen", profile.contactsScreen);
            profile.chatScreen = (Rectangle) daxtopProfileData.GetAttribute("ChatScreen", profile.chatScreen);
            profile.profiles = (ArrayList<Profile>) daxtopProfileData.GetAttribute("NetworkProfiles", profile.profiles);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return profile;
    }

    /** Returns the saveable profile data */
    public synchronized DaxtopProfileData SaveProfile()
    {
        DaxtopProfileData daxtopProfileData = new DaxtopProfileData();

        daxtopProfileData.configurationData.put("Username", Username);
        daxtopProfileData.configurationData.put("Password", Password);
        daxtopProfileData.configurationData.put("LastUsedSkin", lastUsedSkin);
        daxtopProfileData.configurationData.put("RememberPassword", RememberPassword);
        daxtopProfileData.configurationData.put("ToasterSetting", toasterSetting);
        daxtopProfileData.configurationData.put("MuteSound", MuteSound);
        daxtopProfileData.configurationData.put("CheckForUpdate", checkForUpdates);
        daxtopProfileData.configurationData.put("DefaultNetwork", defaultNetwork);
        daxtopProfileData.configurationData.put("TabOverview", tabOverview);
        daxtopProfileData.configurationData.put("TabPlacement", tabPlacement);
        daxtopProfileData.configurationData.put("ContactsScreen", contactsScreen);
        daxtopProfileData.configurationData.put("ChatScreen", chatScreen);
        daxtopProfileData.configurationData.put("NetworkProfiles", profiles);

        return daxtopProfileData;
    }

    /** Adds a profile to the DaxtopProfile set */
    public void AddProfile(Profile p)
    {
        profiles.add(p);
    }

    /** Attempts to find the profile, if it cant be found, returns null */
    public Profile GetProfileForNetwork(String networkId, String accountId)
    {
        for(Profile p : profiles)
            if(p.Network.equals(networkId) && p.AccountId.equals(accountId))
                return p;

        return null;
    }

    /** Attempts to find all profiles for the network */
    public ArrayList<Profile> GetProfilesForNetwork(String networkId)
    {
        ArrayList<Profile> result = new ArrayList<Profile>();

        for(Profile p : profiles)
            if(p.Network.equals(networkId))
                result.add(p);

        return result;
    }

    /** Sets the profile of a network if it is found, returns true if network found */
    public boolean UpdateProfile(Profile profile)
    {
        for(Profile p : profiles)
            if(p.Network.equals(profile.Network) && p.AccountId.equals(profile.AccountId))
            {
                p = profile;
                return true;
            }

        return false;
    }

    /** Compares the password of the user to the stored password */
    public boolean comparePassword(String password, boolean hashPassword)
    {
        if (hashPassword)
        {
            byte[] md5hash = new byte[32];

            try
            {
                MessageDigest md;
                md = MessageDigest.getInstance("MD5");

                md.update(password.getBytes("iso-8859-1"), 0, password.length());
                md5hash = md.digest();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            password = convertToHex(md5hash);
        }

        return password.equals(Password);
    }

    /** Returns the password */
    public String getPassword()
    {
        return Password;
    }

    /** Sets the password of the user */
    public void setPassword(String password)
    {
        byte[] md5hash = new byte[32];

        try
        {
            MessageDigest md;
            md = MessageDigest.getInstance("MD5");

            md.update(password.getBytes("iso-8859-1"), 0, password.length());
            md5hash = md.digest();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Password = convertToHex(md5hash);
    }

    /** Converts to hex values */
    private static String convertToHex(byte[] data)
    {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        }
        return buf.toString();
    }

    /** Removes the profile and sets it to null */
    public void RemoveProfile(String networkId, String accountId)
    {
        for(Profile p : profiles)
            if(p.Network.equals(networkId) && p.AccountId.equals(accountId))
            {
                profiles.remove(p);
                break;
            }
    }

    /** Gets the default profile for the network */
    public Profile GetDefaultProfileForNetwork(String networkId)
    {
        for(Profile p : profiles)
        {
            if (p.Network.equals(networkId) && p.isDefault())
                return p;
        }

        // no default, try return the first in the list otherwise null
        return null;
    }

    /** Sets the default profile for the network */
    public void SetDefaultProfileForNetwork(String networkId, String accountId)
    {
        for(Profile p : profiles)
            if(p.Network.equals(networkId) && p.AccountId.equals(accountId))
            {
                p.defaultProfile = true;
            }
            else
            {
                p.defaultProfile = false;
            }
    }
}
