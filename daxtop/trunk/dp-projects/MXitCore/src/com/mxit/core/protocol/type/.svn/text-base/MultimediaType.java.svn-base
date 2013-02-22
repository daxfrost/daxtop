package com.mxit.core.protocol.type;

/**
 * @author Dax Booysen
 * @company MXit Lifestyle
 * 
 * The type of the multimedia message command used in the MXit command protocol.
 */
public enum MultimediaType
{
    None,
    CustomResource,
    OfferFile,
    RejectFile,
    GetFile,
    ReceivedFile,
    SendFileDirect,
    ForwardFileDirect,
    Skin,
    End,
    GetAvatars,
    SetAvatar,
    ExtendedType;

    public static MultimediaType GetCommandType(int type)
    {
        switch (type)
        {
            case 0:
                return None;
            case 1:
                return CustomResource;
            case 6:
                return OfferFile;
            case 7:
                return RejectFile;
            case 8:
                return GetFile;
            case 9:
                return ReceivedFile;
            case 10:
                return SendFileDirect;
            case 11:
                return ForwardFileDirect;
            case 12:
                return Skin;
            case 13:
                return SetAvatar;
            case 14:
                return GetAvatars;
            case 126:
                return End;
            case 127:
                return ExtendedType;
        }

        return None;
    }

    public static int GetNumericFromCommand(MultimediaType ct)
    {
        switch (ct)
        {
            case None:
                return 0;
            case CustomResource:
                return 1;
            case OfferFile:
                return 6;
            case RejectFile:
                return 7;
            case GetFile:
                return 8;
            case ReceivedFile:
                return 9;
            case SendFileDirect:
                return 10;
            case ForwardFileDirect:
                return 11;
            case Skin:
                return 12;
            case SetAvatar:
                return 13;
            case GetAvatars:
                return 14;
            case End:
                return 126;
            case ExtendedType:
                return 127;
        }

        return 0;
    }
}
