package com.mxit.core.ui;

import com.dax.Main;
import com.dax.Profiles;
import com.dax.daxtop.ui.handlers.FocusHandler;
import com.mxit.MXitNetwork;
import com.mxit.core.res.MXitRes;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Dax Booysen
 * 
 * A screen used by the user to change MXit settings
 */
public class SettingsScreen extends JPanel implements ActionListener
{
    public JLabel lblMXitSettings, lblDefaultEmoticonPack;
    public JCheckBox chkMuteSound, chkToasterEnabled, chkToasterPresence;
    public JComboBox cbDefaultEmoticonPack;
    public JButton btnAccept, btnCancel;              // constant values

    private String[] defaultPacks = new String[] { "MXit Alternative", "MXit Classic" };

    /** MXit Network */
    private MXitNetwork mxitNetwork = null;

    /** Constructs a new SettingsScreen */
    public SettingsScreen(MXitNetwork network)
    {
        // set the network
        mxitNetwork = network;

        initComponents(this);
    }

    /** Sets up the components for this JPanel */
    private void initComponents(Container c)
    {
        // init components
        lblMXitSettings = new JLabel("<html><b><u>MXit Settings</u></b></html>");
        lblDefaultEmoticonPack = new JLabel("Default Emoticon Pack: ");
        chkMuteSound = new JCheckBox("Mute Sound");
        chkToasterEnabled = new JCheckBox("Message Toaster Alerts");
        chkToasterPresence = new JCheckBox("Presence Toaster Alerts");
        cbDefaultEmoticonPack = new JComboBox(defaultPacks);
        btnAccept = new JButton("Accept");
        btnCancel = new JButton("Cancel");

        // set values
        cbDefaultEmoticonPack.setSelectedIndex(mxitNetwork.CurrentProfile.DefaultEmoticonPack);
        chkToasterEnabled.setSelected(mxitNetwork.CurrentProfile.ToasterAlerts);
        chkToasterPresence.setSelected(mxitNetwork.CurrentProfile.PresenceToasterAlerts);
        chkMuteSound.setSelected(mxitNetwork.CurrentProfile.MuteSound);

        // remove layout manager (absolute)
        c.setLayout(null);

        // add components
        c.add(lblMXitSettings);
        c.add(chkMuteSound);
        c.add(chkToasterEnabled);
        c.add(chkToasterPresence);
        c.add(lblDefaultEmoticonPack);
        c.add(cbDefaultEmoticonPack);
        c.add(btnAccept);
        c.add(btnCancel);

        // layout components
        lblMXitSettings.setBounds(100, 15, 80, 20);
        chkMuteSound.setBounds(15, 40, 150, 20);
        chkToasterEnabled.setBounds(15, 70, 150, 20);
        chkToasterPresence.setBounds(15, 100, 190, 20);
        lblDefaultEmoticonPack.setBounds(15, 120, 240, 25);
        cbDefaultEmoticonPack.setBounds(15, 145, 240, 20);
        btnAccept.setBounds(15, 300, 80, 25);
        btnCancel.setBounds(100, 300, 80, 25);

        // tab index order
        Vector<Component> tabOrder = new Vector<Component>();
        tabOrder.add(chkMuteSound);
        tabOrder.add(chkToasterEnabled);
        tabOrder.add(chkToasterPresence);
        tabOrder.add(cbDefaultEmoticonPack);
        tabOrder.add(btnAccept);
        tabOrder.add(btnCancel);
        FocusHandler focusHandler = new FocusHandler(tabOrder);
        c.setFocusTraversalPolicy(focusHandler);

        // attach action listeners
        btnAccept.addActionListener(this);
        btnCancel.addActionListener(this);

        // decorate
        this.setBorder(BorderFactory.createBevelBorder(0));
    }

    // button clicked
    public void actionPerformed(ActionEvent e)
    {
        // "Accept" clicked
        if (e.getSource() == btnAccept)
        {
            if (cbDefaultEmoticonPack.getSelectedIndex() != mxitNetwork.CurrentProfile.DefaultEmoticonPack)
            {
                if (cbDefaultEmoticonPack.getSelectedIndex() == 0)
                {
                    mxitNetwork.Res.LoadDefaultEmoticonPack("", "emoNew.png");
                    mxitNetwork.CurrentProfile.DefaultEmoticonPack = 0;
                }
                else
                {
                    mxitNetwork.Res.LoadDefaultEmoticonPack("", "emoClassic.png");
                    mxitNetwork.CurrentProfile.DefaultEmoticonPack = 1;
                }
                
                // update chat screen buttons
                ArrayList<ChatScreen> screens = mxitNetwork.GetAllChatPanels();

                // run through all the chat screens and update the icon showing on the emoticons button
                for (ChatScreen cs : screens)
                {
                    cs.UpdateIcons();
                }

                // refresh the status screen
                mxitNetwork.contactScreen.refreshStatusScreen();
                mxitNetwork.contactScreen.UpdateContacts(mxitNetwork.currentContactList);
            }

            // set the toaster alert state
            mxitNetwork.CurrentProfile.ToasterAlerts = chkToasterEnabled.isSelected();
            mxitNetwork.CurrentProfile.PresenceToasterAlerts = chkToasterPresence.isSelected();
            mxitNetwork.CurrentProfile.MuteSound = chkMuteSound.isSelected();

            // remove settings screen
            Main.menuScreen.setVisible(false);
            // save profiles
            Main.profile.UpdateProfile(mxitNetwork.CurrentProfile.SaveProfile());
            Profiles.SaveProfiles();
        }
        else
        {
            Main.menuScreen.setVisible(false);
        }
    }
}
