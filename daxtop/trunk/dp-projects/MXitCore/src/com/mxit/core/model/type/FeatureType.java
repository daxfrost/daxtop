package com.mxit.core.model.type;

/**
 * @author Dax Booysen
 * Holds the bitset types for the client Features
 */
public class FeatureType
{
    public static final int FORMS             = 0x1;
    public static final int FILE_TRANSFER     = 0x2;
    public static final int CAMERA            = 0x4;
    public static final int COMMANDS          = 0x8;
    public static final int SEND_SMS          = 0x10;
    public static final int FILE_ACCESS       = 0x20;
    public static final int MIDP2             = 0x40;
    public static final int SKINS             = 0x80;
    public static final int AUDIO             = 0x100;
    public static final int ENCRYPTION        = 0x200;
    public static final int VOICE_RECORDER    = 0x400;
    public static final int SVG               = 0x800;
    public static final int INLINE_IMAGES     = 0x1000;
    public static final int MARKUP            = 0x2000;
    public static final int VIBES             = 0x4000;
    public static final int SELECT_CONTACT    = 0x8000;
    public static final int CUSTOM_EMOTICONS  = 0x10000;
    public static final int ALERT_PROFILES    = 0x20000;
    public static final int EXTENDED_MARKUP   = 0x40000;
}
