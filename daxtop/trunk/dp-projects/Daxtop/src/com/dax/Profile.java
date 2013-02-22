package com.dax;

import com.dax.daxtop.config.ConfigManager;
import java.awt.Stroke;
import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Dax Booysen
 * @company daxtop
 * 
 * This class is used to store various profile attributes of for a user on a specific network
 * Call LoadProfile from a specific file if you would prefer to not save this profile in the default.
 */
public final class Profile implements Serializable
{
    /** The network name relating to this Profile */
    public String Network = "";

    /** The account identifier for this Profile */
    public String AccountId = "";

    /** Set this to true if this profile is the default profile for the network */
    boolean defaultProfile = false;

    /** Configuration data with key and value */
    public HashMap<String, Object> configurationData = new HashMap<String, Object>();

    /** Constructor */
    public Profile()
    {

    }

    /** Constructor */
    public Profile(String network, String accountId)
    {
        Network = network;
        AccountId = accountId;
    }

    /** Constructor */
    public Profile(String network, String accountId, HashMap<String, Object> configData)
    {
        Network = network;
        AccountId = accountId;
        configurationData = configData;
    }

    /** This profile is a default profile */
    public boolean isDefault()
    {
        return defaultProfile;
    }
}
