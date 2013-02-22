package com.dax.daxtop.ui;

import com.dax.Main;
import com.dax.daxtop.res.ResourceManager;
import com.dax.daxtop.utils.Utils;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *  @author Dax Booysen
 * 
 *  This JFrame holds all the screens for navigating menus.
 */
public final class MenuScreen extends JDialog
{
    JPanel mainPane = new JPanel();

    public MenuScreen()
    {
        super(Main.frmContacts, "daxtop");
        
        setSize(285, 340);
        setResizable(false);
        setIconImage(ResourceManager.IMG_DAXTOP_ICON);
        setUndecorated(true);
        setAlwaysOnTop(true);
        //this.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
        this.setModalityType(ModalityType.TOOLKIT_MODAL);
        mainPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        mainPane.setLayout(new BorderLayout());
        this.setContentPane(mainPane);
        Utils.setWindowCentered(this);
    }
    
    /** Sets the current main panel */
    public void setScreen(JPanel screen)
    {
        this.mainPane.removeAll();
        this.mainPane.add(screen, BorderLayout.CENTER);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);

        if (b)
        {
            this.setModal(true);
        }
    }
}
