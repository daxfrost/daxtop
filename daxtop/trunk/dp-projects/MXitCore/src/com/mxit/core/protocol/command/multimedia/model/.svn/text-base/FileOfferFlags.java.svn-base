package com.mxit.core.protocol.command.multimedia.model;

/**
 * @author Dax Frost
 */
public class FileOfferFlags
{
    /** Allows the user to save the file to all possible targets.  */
    public static final int MaySaveAll = 0x1;

    /** Automatically fetch the file (if the client can handle the file) without user intervention. */
    public static final int AutoFetch = 0x2;

    /** The file may be forwarded to other MXit users  */
    public static final int AllowForwardMXit = 0x4;

    /** The file may be forwarded to bots */
    public static final int AllowForwardBots = 0x8;

    /** The file may be forwarded to contacts via gateways */
    public static final int AllowForwardGateways = 0x10;

    /** The file may be forwarded to chatrooms. Only used to distinguish from bots - DON'T USE */
    public static final int AllowForwardChatrooms = 0x20;

    /** The file may be forwarded to any contact */
    public static final int AllowForwardAll = 0x40;

    /** File is free (used for skin and previews) */
    public static final int OfferFileFree = 0x80;

    /** Open the item automatically after transfer if conversation screen is still open */
    public static final int OfferFileAutoOpen = 0x100;

    /** Open the item automatically even if conversation screen is not open anymore */
    public static final int AutoOpenAlways = 0x200;

    /** The file may be saved to Gallery */
    public static final int FileMaySaveGallery = 0x400;

    /** The file may be saved to the local file system */
    public static final int FileMaySavePhone = 0x800;

    /** The file may be saved as a custom alert */
    public static final int FileMaySaveAlert = 0x1000;
}
