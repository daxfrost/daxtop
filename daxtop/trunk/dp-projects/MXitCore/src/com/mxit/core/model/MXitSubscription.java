package com.mxit.core.model;

import com.mxit.core.model.type.MXitContactType;

/**
 * @author  - Dax Booysen
 * @company - MXit Lifestyle
 * 
 * The MXitSubscription to hold a subscription from a new contact
 */
public class MXitSubscription
{
    // Members
    public String MXitID = null;
    public String Nickname = null;
    public MXitContactType Type = null;
    public boolean hiddenLoginName = false;
    public String Msg = "";
    public String groupChatMod = "";

    public MXitSubscription()
    {
        
    }
}
