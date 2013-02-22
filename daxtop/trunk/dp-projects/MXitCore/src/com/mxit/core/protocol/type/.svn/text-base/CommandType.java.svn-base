package com.mxit.core.protocol.type;

/**
 * @author Dax Booysen
 * @company MXit Lifestyle
 * 
 * The type of the command used in the MXit command protocol.
 */
public enum CommandType
{

    Login,
    Logout,
    GetContacts,
    GetNewMessages,
    UpdateContact,
    RemoveContact,
    SendMessage,
    GetPresence,
    AddNewContact,
    GetNewSubscriptions,
    AddNewGroupChat,
    InviteGroupChatMembers,
    AllowSubscription,
    BlockSubscription,
    DenySubscription,
    KeepAlive,
    RemoveGroup,
    RenameGroup,
    GetProfile,
    Register,
    Multimedia,
    SetPresence,
    GetExtendedProfile,
    SetExtendedProfile,
    SetMood,
    UpdateProfile,
    UnknownError,
    UnknownCommand;

    public static CommandType GetCommandType(int type)
    {
        switch (type)
        {
            case 1:
                return Login;
            case 2:
                return Logout;
            case 3:
                return GetContacts;
            case 4:
                break;
            case 5:
                return UpdateContact;
            case 6:
                return AddNewContact;
            case 7:
                return GetPresence;
            case 8:
                return RemoveContact;
            case 9:
                return GetNewMessages;
            case 10:
                break;
            case 11:
                return Register;
            case 12:
                break;
            case 27:
                return Multimedia;
            case 29:
                return RenameGroup;
            case 30:
                return RemoveGroup;
            case 32:
                return SetPresence;
            case 33:
                return BlockSubscription;
            case 41:
                return SetMood;
            case 44:
                return AddNewGroupChat;
            case 45:
                return InviteGroupChatMembers;
            case 51:
                return GetNewSubscriptions;
            case 52:
                return AllowSubscription;
            case 55:
                return DenySubscription;
            case 57:
                return GetExtendedProfile;
            case 58:
                return SetExtendedProfile;
            case 99:
                return UnknownError;
            case 1000:
                return KeepAlive;
            default:
                return UnknownCommand;
        }

        return UnknownCommand;
    }

    public static int GetNumericFromCommand(CommandType ct)
    {
        switch (ct)
        {
            case Login:
                return 1;
            case Logout:
                return 2;
            case GetContacts:
                return 3;
            case UpdateContact:
                return 5;
            case AddNewContact:
                return 6;
            case SendMessage:
                return 10;
            case RemoveContact:
                return 8;
            case Multimedia:
                return 27;
            case RenameGroup:
                return 29;
            case RemoveGroup:
                return 30;
            case SetPresence:
                return 32;
            case BlockSubscription:
                return 33;
            case SetMood:
                return 41;
            case AddNewGroupChat:
                return 44;
            case InviteGroupChatMembers:
                return 45;
            case GetNewSubscriptions:
                return 51;
            case AllowSubscription:
                return 52;
            case DenySubscription:
                return 55;
            case GetExtendedProfile:
                return 57;
            case SetExtendedProfile:
                return 58;
            case KeepAlive:
                return 1000;
            case Register:
                return 11;
        }

        return 99;
    }
}
