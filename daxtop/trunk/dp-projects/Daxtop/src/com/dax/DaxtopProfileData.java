package com.dax;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Dax Frost
 *
 * This class is the generic profile data class
 */
public class DaxtopProfileData implements Serializable
{
    /** Configuration data with key and value */
    public HashMap<String, Object> configurationData = new HashMap<String, Object>();

    /** Get daxtop profile attribute - returns null if no profile attribute is found or set */
    public Object GetAttribute(String attributeName, Object defaultValue)
    {
        if (configurationData.containsKey(attributeName))
        {
            return configurationData.get(attributeName);
        }
        else
            return defaultValue;
    }
}
