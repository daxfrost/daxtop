package com.dax.daxtop.ui.components;

import com.dax.control.ChatMessage;
import com.dax.control.DaxChatPane;
import com.dax.daxtop.interfaces.ChatPanel;
import java.awt.BorderLayout;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * @author Dax Frost
 */
public class ConversationViewer extends JPanel implements ChatPanel
{
    /** The network name */
    private String networkName;

    /** The chat id */
    private String chatId;

    /** The name */
    private String name;

    /** The chat pane to host the conversation being viewed */
    private DaxChatPane chatWindow = new DaxChatPane();

    /** The messages */
    public ArrayList<ChatMessage> messages = null;

    /** Creates a new instance of the conversation viewer */
    public ConversationViewer(String name, String networkName, String chatId, ArrayList<ChatMessage> messages)
    {
        this.networkName = networkName;
        this.chatId = chatId;
        this.name = name;
        this.messages = messages;

        if (this.messages != null)
        {
            for (ChatMessage message : this.messages)
                chatWindow.AppendMessage(message);
        }

        this.setLayout(new BorderLayout());

        this.add(new JScrollPane(chatWindow), BorderLayout.CENTER);
    }

    public String GetChatNetworkName()
    {
        return this.networkName;
    }

    public String GetChatNickname()
    {
        return this.name;
    }

    public String GetChatId()
    {
        return this.chatId;
    }

    public void ChatPanelGainedFocus()
    {

    }

    public void ChatPaneClosed()
    {

    }

    public void GetFocus()
    {

    }

    public void refreshScreen()
    {

    }
}
