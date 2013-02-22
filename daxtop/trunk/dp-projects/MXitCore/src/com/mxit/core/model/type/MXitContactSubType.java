package com.mxit.core.model.type;

/**
 * @author Dax Booysen
 * Holds the types for the contact sub types
 */
public enum MXitContactSubType
{
    /** Both users can see as online */
    B,
    /** Neither users can see each other as online */
    N,
    /** Pending outgoing invite to this contact */
    P,
    /** Asked incoming invite from this contact */
    A,
    /** Rejected invite by this contact */
    R,
    /** Deleted user from this contact's contact list  */
    D;
    
    public static MXitContactSubType getMXitContactType(char type)
    {
        switch(type)
        {
            case 'B':
                return B;
            case 'N':
                return N;
            case 'P':
                return P;
            case 'A':
                return A;
            case 'R':
                return R;
            case 'D':
                return D;
            default:
                return B;
        }
    }
}
