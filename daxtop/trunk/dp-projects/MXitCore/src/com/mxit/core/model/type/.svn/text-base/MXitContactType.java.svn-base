package com.mxit.core.model.type;

/**
 * @author Dax Booysen
 * Holds the types for the contact types
 */
public enum MXitContactType
{
    MXit,
    Jabber,
    MSN,
    Yahoo,
    ICQ,
    AIM,
    Service,
    ChatZone,
    Gallery,
    Info,
    MultiMX,
    GoogleTalk,
    None;
    
    public static MXitContactType getMXitContactType(int type)
    {
        switch(type)
        {
            case 0:
                return MXit;
            case 1:
                return Jabber;
            case 2:
                return MSN;
            case 3:
                return Yahoo;
            case 4:
                return ICQ;
            case 5:
                return AIM;
            case 8:
                return Service;
            case 9:
                return ChatZone;
            case 12:
                return Gallery;
            case 13:
                return Info;
            case 14:
                return MultiMX;
            default:
                return None;				
        }
    }

    public static byte getMXitContactType(MXitContactType type)
    {
        switch(type)
        {
            case MXit:
                return 0;
            case Jabber:
                return 1;
            case MSN:
                return 2;
            case Yahoo:
                return 3;
            case ICQ:
                return 4;
            case AIM:
                return 5;
            case Service:
                return 8;
            case ChatZone:
                return 9;
            case Gallery:
                return 12;
            case Info:
                return 13;
            case MultiMX:
                return 14;
            default:
                return 0;
        }
    }
}
