package com.mxit.core.model.type;

/**
 * @author Dax Booysen
 * Holds the types for the presence types
 */
public enum MXitPresence
{
    Online,
    Offline,
    Away,
    DND;
    
    public static MXitPresence getMXitPresence(int type)
    {
        switch(type)
        {
            case 0:
                return Offline;
            case 1:
                return Online;
            case 2:
                return Away;
            case 4:
                return DND;
            default:
                return Offline;				
        }
    }

    public static int getIndex(MXitPresence type)
    {
        switch(type)
        {
            case Offline:
                return 0;
            case Online:
                return 1;
            case Away:
                return 2;
            case DND:
                return 4;
            default:
                return 0;
        }
    }
}
