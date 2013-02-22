package com.dax.daxtop.interfaces;

/**
 * @author Dax Booysen
 *
 * The base panel interface for all Chat window chat types
 */
public interface ChatPanel
{
    /** Returns the Network Name */
    public abstract String GetChatNetworkName();
    
    /** Returns the Chat Nickname */
    public abstract String GetChatNickname();

    /** Returns the Chat Id */
    public abstract String GetChatId();

    /** Chat Panel gained focus */
    public abstract void ChatPanelGainedFocus();

    /** Chat Panel was closed */
    public abstract void ChatPaneClosed();

    /** Forces the ChatPanel to get focus */
    public abstract void GetFocus();

    /** Request that this ChatPanel be refreshed */
    public abstract void refreshScreen();
}
