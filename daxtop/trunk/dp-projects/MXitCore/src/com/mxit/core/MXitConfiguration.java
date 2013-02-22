package com.mxit.core;

import com.dax.daxtop.config.*;

/**
 * @author Dax Booysen
 * @company daxtop
 * 
 * This class is used to store various MXit connection settings
 */
public final class MXitConfiguration
{
    // member variables
    public static int    CONN   = 1;
    public static String DIST   = "X";
    public static String VER    = "1.0.0";
    public static String MX_VER = "5.9.0";
    public static String CAT    = "Y";
    public static String PLAT   = "daxtop";
    
    // mxit configuration file path
    private static final String FILE_PATH = "./mxit.conf";
    
    // constants for config
    private static final String _CONN = "CONN";
    private static final String _DIST = "DIST";
    private static final String _MX_VER = "MX_VER";
    private static final String _VER = "VER";
    private static final String _CAT = "CAT";
    private static final String _PLAT = "PLAT";
 
    /** Used to reload the profile data from file, returns false if no data file found */
    public static boolean Load()
    {
        ConfigManager config = null;
        
        if((config = ConfigManager.LoadConfiguration(FILE_PATH)) != null)
        {
            CONN = (Integer) config.GetEntity(_CONN);
            DIST = (String) config.GetEntity(_DIST);
            VER = (String) config.GetEntity(_VER);
            MX_VER = (String) config.GetEntity(_MX_VER);
            CAT = (String) config.GetEntity(_CAT);
            PLAT = (String) config.GetEntity(_PLAT);
            
            return true;
        }
        else
            return false;
    }
    
    /** Used to save the profile data to file */
    public static void Save()
    {
        ConfigManager mxitConfig = new ConfigManager();
        mxitConfig.AddEntity(new ConfigEntity(_CONN, CONN));
        mxitConfig.AddEntity(new ConfigEntity(_DIST, DIST));
        mxitConfig.AddEntity(new ConfigEntity(_VER, VER));
        mxitConfig.AddEntity(new ConfigEntity(_MX_VER, MX_VER));
        mxitConfig.AddEntity(new ConfigEntity(_CAT, CAT));
        mxitConfig.AddEntity(new ConfigEntity(_PLAT, PLAT));
        ConfigManager.SaveConfiguration(FILE_PATH, mxitConfig);
    }
}
