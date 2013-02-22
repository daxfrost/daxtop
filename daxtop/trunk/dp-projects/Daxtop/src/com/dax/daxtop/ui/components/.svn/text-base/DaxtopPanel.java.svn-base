package com.dax.daxtop.ui.components;

import com.dax.Main;
import com.dax.Main.NetworkManager;
import com.dax.Profiles;
import com.dax.daxtop.interfaces.NetworkProfile;
import com.dax.daxtop.ui.FrmChat;
import com.dax.lib.audio.AudioPlayer;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceAutumnLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceBusinessBlackSteelLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceBusinessLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceChallengerDeepLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceCremeCoffeeLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceCremeLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceDustLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceEmeraldDuskLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceGeminiLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceGraphiteAquaLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceGraphiteGlassLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceMagellanLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceMistAquaLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceMistSilverLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceModerateLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceNebulaBrickWallLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceNebulaLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceOfficeBlue2007LookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceOfficeSilver2007LookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceRavenLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceSaharaLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceTwilightLookAndFeel;

/**
 * @author Dax Frost
 *
 * The JPanel for daxtop settings
 */
public class DaxtopPanel extends JPanel
{
    // members
    JPanel topPanel, botPanel;
    JLabel lbSkins, lbDefaultNetwork, lbTabOverview, lbToasterSetting, lbTabPlacement;
    JButton btnApplySkin;
    JComboBox cbDefaultNetwork, cbTabOverview, cbToasterSetting, cbTabPlacement;
    JCheckBox cbMuteSound, cbAutoLogin, cbRememberPassword, cbCheckForUpdates;
    static String[] tabOverview = new String[] { "None", "Carousel", "Grid", "Shuffle" };
    static String[] tabPlacement = new String[] { "Top", "Left", "Bottom", "Right" };
    static String[] skins = new String[] { "Aluminium Alloys", "Autumn", "Stealth Station", "Neon Velvet", "Silver Sword", "Chromium Cubicle", "Deep Ocean", "Graphite Tank", "Radiant Sentry", "Latté", "Polar Caps", "Deep Forest", "Dust", "Hydro Tank", "Fibre Grill", "Android", "Android Online", "The Facility", "Fudge", "Silver Office", "Blue Office", "Vanilla Plastic", "Algae", "Lamp Post"  };
    static SubstanceLookAndFeel[] landfs = new SubstanceLookAndFeel[] { new SubstanceModerateLookAndFeel(),
                                                                 new SubstanceAutumnLookAndFeel(),
                                                                 new SubstanceBusinessBlackSteelLookAndFeel(),
                                                                 new SubstanceChallengerDeepLookAndFeel(),
                                                                 new SubstanceBusinessBlueSteelLookAndFeel(),
                                                                 new SubstanceBusinessLookAndFeel(),
                                                                 new SubstanceMagellanLookAndFeel(),
                                                                 new SubstanceGraphiteGlassLookAndFeel(),
                                                                 new SubstanceGeminiLookAndFeel(),
                                                                 new SubstanceCremeCoffeeLookAndFeel(),
                                                                 new SubstanceCremeLookAndFeel(),
                                                                 new SubstanceEmeraldDuskLookAndFeel(),
                                                                 new SubstanceDustLookAndFeel(),
                                                                 new SubstanceGraphiteAquaLookAndFeel(),
                                                                 new SubstanceRavenLookAndFeel(),
                                                                 new SubstanceMistSilverLookAndFeel(),
                                                                 new SubstanceMistAquaLookAndFeel(),
                                                                 new SubstanceGraphiteLookAndFeel(),
                                                                 new SubstanceNebulaBrickWallLookAndFeel(),
                                                                 new SubstanceOfficeSilver2007LookAndFeel(),
                                                                 new SubstanceOfficeBlue2007LookAndFeel(),
                                                                 new SubstanceNebulaLookAndFeel(),
                                                                 new SubstanceSaharaLookAndFeel(),
                                                                 new SubstanceTwilightLookAndFeel()
                                                               };

    public DaxtopPanel()
    {
        // init components
        lbSkins = new JLabel("Current Skin: ");
        lbDefaultNetwork = new JLabel("Default View: ");
        lbTabOverview = new JLabel("Tab Overview: ");
        lbTabPlacement = new JLabel("Tab Placement: ");
        lbToasterSetting = new JLabel("Toaster Setting: ");
        lbSkins.setHorizontalAlignment(JLabel.RIGHT);
        lbDefaultNetwork.setHorizontalAlignment(JLabel.RIGHT);
        lbTabOverview.setHorizontalAlignment(JLabel.RIGHT);
        lbTabPlacement.setHorizontalAlignment(JLabel.RIGHT);
        lbToasterSetting.setHorizontalAlignment(JLabel.RIGHT);
        btnApplySkin = new JButton("Hydro Tank");
        cbDefaultNetwork = new JComboBox();
        cbToasterSetting = new JComboBox(new String[] { "Off", "Unfocused updates", "Every update" });
        cbToasterSetting.setSelectedIndex(Main.profile.toasterSetting);
        cbTabOverview = new JComboBox(tabOverview);
        cbTabOverview.setSelectedItem(Main.profile.tabOverview);
        cbTabPlacement = new JComboBox(tabPlacement);
        cbTabPlacement.setSelectedIndex(Main.profile.tabPlacement);
        cbMuteSound = new JCheckBox("Mute Sounds", Main.profile.MuteSound);
        cbAutoLogin = new JCheckBox("Auto Login", Profiles.GetAutoProfile() == Main.profile);
        cbCheckForUpdates = new JCheckBox("Check for updates", Main.profile.checkForUpdates);
        cbRememberPassword = new JCheckBox("Remember Password", Main.profile.RememberPassword);
        cbRememberPassword.setEnabled(!cbAutoLogin.isSelected());
        AudioPlayer.muted = cbMuteSound.isSelected();
        topPanel = new JPanel();
        botPanel = new JPanel();

        // set layout
        topPanel.setLayout(new GridBagLayout());
        this.setLayout(new BorderLayout());

        // setup constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx =  1;
        gbc.gridy =  1;
        gbc.weightx = 0;
        gbc.weighty = 1;
        
        topPanel.add(lbSkins, gbc);
        gbc.gridx = 2;
        gbc.weightx = 1;
        topPanel.add(btnApplySkin, gbc);

        gbc.gridy = 2;
        gbc.gridx = 1;
        gbc.weightx = 0;
        topPanel.add(lbDefaultNetwork, gbc);

        gbc.gridx = 2;
        gbc.weightx = 1;
        topPanel.add(cbDefaultNetwork, gbc);

        gbc.gridx = 2;
        gbc.gridy = 3;
        topPanel.add(cbMuteSound, gbc);

        gbc.gridx = 2;
        gbc.gridy = 4;
        topPanel.add(cbAutoLogin, gbc);

        gbc.gridx = 2;
        gbc.gridy = 5;
        topPanel.add(cbRememberPassword, gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        topPanel.add(lbTabOverview, gbc);

        gbc.gridx = 2;
        topPanel.add(cbTabOverview, gbc);

        gbc.gridx = 1;
        gbc.gridy = 7;
        topPanel.add(lbTabPlacement, gbc);

        gbc.gridx = 2;
        topPanel.add(cbTabPlacement, gbc);

        gbc.gridx = 1;
        gbc.gridy = 8;
        topPanel.add(lbToasterSetting, gbc);

        gbc.gridx = 2;
        topPanel.add(cbToasterSetting, gbc);

        gbc.gridy = 9;
        topPanel.add(cbCheckForUpdates, gbc);

        this.add(topPanel, BorderLayout.NORTH);
        this.add(botPanel, BorderLayout.CENTER);

        cbToasterSetting.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                Main.profile.toasterSetting = cbToasterSetting.getSelectedIndex();
                Profiles.SaveProfiles();
            }
        });

        cbTabPlacement.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                Main.profile.tabPlacement = (byte)cbTabPlacement.getSelectedIndex();
                FrmChat.ChatTabs.setTabPlacement(Main.profile.tabPlacement+1);
                Profiles.SaveProfiles();
            }
        });

        cbTabOverview.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                Main.profile.tabOverview = cbTabOverview.getSelectedItem().toString();
                if (Main.frmChat != null)
                {
                    FrmChat.setTabOverviewKind(Main.profile.tabOverview);
                    FrmChat.ChatTabs.updateUI();
                }
                Profiles.SaveProfiles();
            }
        });

        cbCheckForUpdates.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                Main.profile.checkForUpdates = cbCheckForUpdates.isSelected();
                Profiles.SaveProfiles();
            }
        });

        cbAutoLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                Profiles.SetAutoProfile(cbAutoLogin.isSelected() ? Main.profile : null);
                cbRememberPassword.setEnabled(!cbAutoLogin.isSelected());
                Profiles.SaveProfiles();
            }
        });

        cbRememberPassword.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                Main.profile.RememberPassword = cbRememberPassword.isSelected();
                Profiles.SaveProfiles();
            }
        });

        cbDefaultNetwork.addItem("None");
        for (NetworkProfile network : NetworkManager.networks)
        {
            cbDefaultNetwork.addItem(network.GetNetworkName());
        }
        // set default
        cbDefaultNetwork.setSelectedItem(Main.profile.defaultNetwork != null ? Main.profile.defaultNetwork : "None" );

        cbDefaultNetwork.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent e)
            {
                if (!e.getItem().equals("None"))
                    Main.profile.defaultNetwork = e.getItem().toString();
                else
                    Main.profile.defaultNetwork = null;
                
                Profiles.SaveProfiles();
            }
        });

        cbMuteSound.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                Main.profile.MuteSound = cbMuteSound.isSelected();
                AudioPlayer.muted = cbMuteSound.isSelected();
                Profiles.SaveProfiles();
            }
        });

        // attach listeners
        btnApplySkin.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JPopupMenu cbSkins = new JPopupMenu();

                // setup skin menu
                int i = 0;
                for(final String skin : skins)
                {
                    final int j = i++;

                    JMenuItem item = new JMenuItem(skin);
                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            Main.SetSkin(landfs[j], true);
                            for (NetworkProfile np : NetworkManager.networks)
                            {
                                np.DaxtopSkinChanged();
                            }
                            Main.reloadUiTrees();
                            btnApplySkin.setText(skin);
                            Profiles.lastSkinUsed = skin;
                            Main.profile.lastUsedSkin = skin;
                            Profiles.SaveProfiles();
                        }
                    });

                    cbSkins.add(item);
                }

                cbSkins.show(btnApplySkin, 0, btnApplySkin.getHeight());
            }
        });
    }

    /** Returns a skin */
    public static SubstanceLookAndFeel GetSkinByName(String skinName)
    {
        int i = 0;

        for (String skin : skins)
        {
            if (skinName.equals(skin))
            {
                return landfs[i];
            }
            i++;
        }

        return null;
    }

    /** Updates the skin by name */
    public void SetSkinName(String newName)
    {
        btnApplySkin.setText(newName);
    }
}