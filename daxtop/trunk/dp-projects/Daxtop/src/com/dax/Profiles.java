package com.dax;

import com.dax.daxtop.config.ConfigEntity;
import com.dax.daxtop.config.ConfigManager;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Dax Booysen
 * 
 * This class contains the profiles
 */
public final class Profiles
{
    private static ArrayList<DaxtopProfile> profiles = new ArrayList<DaxtopProfile>();
    
    private static DaxtopProfile autoProfile = null;
    private static String profileDataFile = System.getProperty("user.home") + "/AppData/Local/daxtop/profiles.dat";
    public static String lastSkinUsed = "Hydro Tank";
    public static DaxtopProfile lastProfile = null;

    private static String _PROFILE_COUNT = "profileCount";
    private static String _LAST_PROFILE_USED = "lastProfileUsed";
    private static String _DEFAULT_PROFILE = "defaultProfile";
    
    /** Loads all the saved profiles */
    public synchronized static void LoadProfiles()
    {
        // make sure the directory exists
        File dir = new File(System.getProperty("user.home") + "/AppData/Local/daxtop/");
        if (!dir.exists())
        {
            if (!dir.mkdir())
            {
                // it failed, we are on windows xp
                profileDataFile = "./profiles.dat";
            }
        }

        ConfigManager cfgManager = ConfigManager.LoadConfiguration(profileDataFile);
        
        // file doesnt exist (first run)
        if(cfgManager==null)
            return;
            
        int count = (Integer)(cfgManager.GetEntity(_PROFILE_COUNT));
        lastProfile = DaxtopProfile.LoadProfile((DaxtopProfileData)cfgManager.GetEntity(_LAST_PROFILE_USED));
        autoProfile = DaxtopProfile.LoadProfile((DaxtopProfileData)cfgManager.GetEntity(_DEFAULT_PROFILE));

        lastSkinUsed = lastProfile.lastUsedSkin;

        for(int i = 3; i < count; i++)
        {
            profiles.add(DaxtopProfile.LoadProfile((DaxtopProfileData)cfgManager.GetEntity(""+i)));
        }
    }
    
    /** Saves all the profiles */
    public static synchronized void SaveProfiles()
    {
        ConfigEntity[] values = new ConfigEntity[profiles.size()+3];
        int i = 0;
        
        // first entity holding count
        ConfigEntity countEntity = new ConfigEntity(_PROFILE_COUNT, profiles.size()+3);
        values[i++] = countEntity;
        ConfigEntity lastSkinEntity = new ConfigEntity(_LAST_PROFILE_USED, lastProfile.SaveProfile());
        values[i++] = lastSkinEntity;
        ConfigEntity defaultEntity = new ConfigEntity(_DEFAULT_PROFILE, autoProfile == null ? null : autoProfile.SaveProfile());
        values[i++] = defaultEntity;
        
        for(DaxtopProfile p : profiles)
        {
            ConfigEntity ce = new ConfigEntity(""+i, p.SaveProfile());
            values[i++] = ce;
        }

        ConfigManager cfgManager = new ConfigManager(values);
        ConfigManager.SaveConfiguration(profileDataFile, cfgManager);
    }
    
    public synchronized static void AddProfile(DaxtopProfile profile)
    {
        for(DaxtopProfile p : profiles)
        {
            if(profile.Username.equals(p.Username))
            {
                System.out.println("Replacing previous profile!");
                profiles.remove(p);
            }
        }
        
        profiles.add(profile);
    }
    
    public synchronized static DaxtopProfile GetAutoProfile()
    {
        return autoProfile;
    }
    
    /** Returns the profile by Msisdn */
    public synchronized static DaxtopProfile GetProfile(String Username)
    {
        for(DaxtopProfile p : profiles)
        {
            if(p.Username.equals(Username))
                return p;
        }
        
        return null;
    }
    
    /* Returns all the profiles **/
    public static Collection<DaxtopProfile> GetProfiles()
    {
        return profiles;
    }
    
    public static int Size()
    {
        return profiles.size();
    }

    /** Sets the autoprofile */
    public static void SetAutoProfile(DaxtopProfile profile)
    {
        autoProfile = profile;
    }

    /** Removes a profile if its exists, and removes it as an autoprofile too */
    public static synchronized void RemoveProfile(DaxtopProfile profile)
    {
        for(DaxtopProfile p : profiles)
        {
            if(p.Username.equals(profile.Username))
            {
                if (autoProfile != null && p.Username.equals(autoProfile.Username))
                {
                    autoProfile = null;
                }

                profiles.remove(p);

                return;
            }
        }
    }
}
