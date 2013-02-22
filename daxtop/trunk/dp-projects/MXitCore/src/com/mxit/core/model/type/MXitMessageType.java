package com.mxit.core.model.type;

/**
 * @author Dax Booysen
 * MXitMessageType enum to signify what type of message is being sent
 */
public enum MXitMessageType
{
    Normal,
    Chat,
    CustomForm,
    Command;
            
    public static MXitMessageType GetMXitMessageType(int type)
    {
        switch(type)
        {
            case 1:
                return Normal;
            case 2:
                return Chat;
            case 6:
                return CustomForm;
            case 7:
                return Command;
        }

        return Normal;
    }

    public static int GetMXitMessageType(MXitMessageType type)
    {
        switch(type)
        {
            case Normal:
                return 1;
            case Chat:
                return 2;
            case CustomForm:
                return 6;
            case Command:
                return 7;
        }

        return 1;
    }
}
