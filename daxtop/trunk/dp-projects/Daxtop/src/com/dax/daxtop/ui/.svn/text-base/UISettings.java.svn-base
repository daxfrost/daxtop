/*
 * This class contains all the settings for the client's UI scheme settings.
 */

package com.dax.daxtop.ui;

import java.awt.Font;
import javax.swing.UIManager;

/**
 * @author Dax Booysen
 */
public final class UISettings
{
    /** The font of the chat input box */
    public static Font inputBoxFont = new Font("Arial", Font.PLAIN, 12);
    
    /** The font of the conversation area */
    public static Font chatScreenFont = new Font("Arial", Font.PLAIN, 12);

    /** Gets all the look and feels available */
    public static UIManager.LookAndFeelInfo[] GetThemes()
    {
        return UIManager.getInstalledLookAndFeels();
    }

    /** Sets the look and feel */
    public static void SetTheme(UIManager.LookAndFeelInfo lookAndFeelInfo)
    {
        try
        {
            UIManager.setLookAndFeel(lookAndFeelInfo.getClassName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
