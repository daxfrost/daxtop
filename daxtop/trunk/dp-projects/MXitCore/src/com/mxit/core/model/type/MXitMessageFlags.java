package com.mxit.core.model.type;

/**
 * @author Dax Booysen
 * Holds the bitset flags for the message Features
 */
public class MXitMessageFlags
{
    public static final int REQUEST_DELIVERY_NOTIFICATION           = 0x2;
    public static final int REQUEST_DISPLAY_NOTIFICATION            = 0x4;
    public static final int RECEIVED_ENCRYPTED_WITH_PASSWORD        = 0x10;
    public static final int RECEIVED_TRANSPORT_LAYER_ENCRYPTION     = 0x20;
    public static final int REPLY_PASSWORD_ENCRYPTED                = 0x40;
    public static final int REPLY_TRANSPORT_ENCRYPTED               = 0x80;
    public static final int MARKUP                                  = 0x200;
    public static final int CUSTOM_EMOTICONS                        = 0x400;
}
