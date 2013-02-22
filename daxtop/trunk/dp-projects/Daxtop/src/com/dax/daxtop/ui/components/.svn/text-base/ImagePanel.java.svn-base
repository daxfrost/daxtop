package com.dax.daxtop.ui.components;

import com.dax.daxtop.interfaces.ChatPanel;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.util.Collections;
import javax.swing.JPanel;

/**
 * @author Dax Frost
 *
 * A generic image display panel for dynamic viewing of images from
 * any network.
 */
public class ImagePanel extends JPanel implements ChatPanel
{
    /** The image */
    public Image img;

    /** The network name */
    private String networkName;

    /** The chat id */
    private String chatId;

    /** The image name */
    private String imageName;

    /** picture box */
    JPictureBox picBox;

    /** Constructs a new ImagePanel with the specified settings */
    public ImagePanel(String imageName, String networkName, String chatId, Image img)
    {
        this.img = img;
        this.networkName = networkName;
        this.chatId = chatId;
        this.imageName = imageName;

        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
        this.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);

        this.setLayout(new BorderLayout());

        picBox = new JPictureBox(img);
        add(picBox, BorderLayout.CENTER);
    }

    public String GetChatNetworkName()
    {
        return this.networkName;
    }

    public String GetChatNickname()
    {
        return this.imageName;
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
