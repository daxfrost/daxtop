package com.dax.daxtop.ui;

import com.dax.DaxtopProfile;
import com.dax.Main;
import com.dax.Main.NetworkManager;
import com.dax.Profiles;
import com.dax.daxtop.interfaces.NetworkProfile;
import com.dax.daxtop.res.ResourceManager;
import com.dax.daxtop.ui.components.DaxtopPanel;
import com.dax.daxtop.ui.components.JPictureBox;
import com.dax.daxtop.utils.Utils;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.BevelBorder;

/**
 * @author Dax Booysen
 * 
 * This screen is used to login to a daxtop account
 */
public final class DaxtopLoginScreen extends JPanel implements ActionListener, ItemListener
{

    /** Holding panel */
    JPanel pnlFrame;
    /** daxtop logo image */
    JPictureBox picBox;
    /** daxtop username */
    JComboBox cbUsername;
    /** daxtop password */
    JPasswordField pfPassword;
    // labels
    JLabel lbUsername, lbPassword;
    /** Auto Login checkbox */
    JCheckBox cbRememberPassword, cbDefaultProfile;
    // buttons
    public JButton btnLogin;
    JButton btnCancel;

    /** Constructor */
    public DaxtopLoginScreen()
    {
        setOpaque(true);
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        init();
    }

    /** load ui */
    private void init()
    {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;

        // holding panel
        pnlFrame = new JPanel();
        pnlFrame.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        pnlFrame.setLayout(null);

        // setup logo at the top
        picBox = new JPictureBox(ResourceManager.IMG_DAXTOP_LOGO);
        picBox.setBounds(60, 15, picBox.getImage().getWidth(this), picBox.getImage().getHeight(this));

        // labels
        lbUsername = new JLabel("Username");
        lbPassword = new JLabel("Password");
        lbUsername.setBounds(110, 140, 150, 25);
        lbPassword.setBounds(110, 185, 150, 25);

        // setup username combo
        cbUsername = new JComboBox();
        cbUsername.setEditable(true);
        cbUsername.setBounds(60, 160, 150, 25);
        cbUsername.addItemListener(this);

        // setup password
        pfPassword = new JPasswordField();
        pfPassword.setBounds(60, 205, 150, 25);

        // setup checkbox auto login
        cbRememberPassword = new JCheckBox("Remember Password");
        cbRememberPassword.setBounds(60, 230, 150, 20);
        cbRememberPassword.addActionListener(this);

        // setup checkbox default profile
        cbDefaultProfile = new JCheckBox("Auto Login Profile");
        cbDefaultProfile.setBounds(60, 255, 150, 20);

        // setup buttons
        btnLogin = new JButton("Login");
        btnCancel = new JButton("Exit");
        btnLogin.setBounds(45, 285, 80, 25);
        btnCancel.setBounds(145, 285, 80, 25);
        btnLogin.addActionListener(this);
        btnCancel.addActionListener(this);

        // add items in order
        pnlFrame.add(picBox);
        pnlFrame.add(cbUsername);
        pnlFrame.add(pfPassword);
        pnlFrame.add(lbUsername);
        pnlFrame.add(lbPassword);
        pnlFrame.add(cbRememberPassword);
        pnlFrame.add(cbDefaultProfile);
        pnlFrame.add(btnLogin);
        pnlFrame.add(btnCancel);
        add(pnlFrame, gbc);
    }

    /** Set the current profiles */
    public void setProfiles(Collection<DaxtopProfile> daxtopProfiles)
    {
        cbUsername.removeAllItems();

        for (DaxtopProfile dp : daxtopProfiles)
        {
            cbUsername.addItem(dp.Username);
        }

        // last used profile
        if (Profiles.lastProfile != null)
        {
            cbUsername.setSelectedItem(Profiles.lastProfile.Username);
            UpdateCheckboxStatus();
        }
    }

    /** Give Password field focus */
    public void GivePasswordFocus()
    {
        pfPassword.requestFocus();
    }

    /** Give Usernam field focus */
    public void GiveLoginNameFocus()
    {
        cbUsername.requestFocus();
    }

    /** a button was clicked */
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == btnLogin)
        {
            // first check that the username is valid
            String username = cbUsername.getSelectedItem().toString();

            // valid entered profile username
            if (username.length() < 3)
            {
                JOptionPane.showMessageDialog(this, "Usernames must be atleast 3 characters in length!", "Login - invalid username", JOptionPane.ERROR_MESSAGE);
                GiveLoginNameFocus();
            }
            else
            {
                String password = new String(pfPassword.getPassword());

                if (password.length() < 3)
                {
                    JOptionPane.showMessageDialog(this, "Passwords must be atleast 3 characters in length!", "Login - invalid password", JOptionPane.ERROR_MESSAGE);
                    GivePasswordFocus();
                }
                else
                {
                    Login(username, password);
                }
            }
        }
        else if (e.getSource() == cbRememberPassword)
        {
            if (cbRememberPassword.isSelected())
            {
                cbDefaultProfile.setEnabled(true);
            }
            else
            {
                cbDefaultProfile.setSelected(false);
                cbDefaultProfile.setEnabled(false);
            }
        }
        else if (e.getSource() == btnCancel)
        {
            // exit daxtop
            Main.Exit();
        }
    }

        /** Login user to a daxtop profile */
    public void Login(String username, String password)
    {
        DaxtopProfile profile = Profiles.GetProfile(username);

        // user doesnt exist, try create a new user
        if (profile == null)
        {
            // no profiles or account is new
            if (Profiles.Size() < 1 || JOptionPane.showConfirmDialog(this, "No account exists with these details, create this as a new account?", "Login - New Account", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
            {
                // create a new account and save it to profiles file
                profile = new DaxtopProfile();
                profile.Username = username;
                profile.setPassword(password);

                Profiles.AddProfile(profile);
                Main.LoggedIn = true;
            }
        }
        else
        {
            // check password
            if (profile.comparePassword(password, profile.getPassword().length() != password.length()))
            {
                // login success
                Main.LoggedIn = true;
            }
            else
            {
                // wrong password
                JOptionPane.showMessageDialog(this, "The password you entered is incorrect!", "Login - Incorrect Password", JOptionPane.ERROR_MESSAGE);
                pfPassword.selectAll();
                pfPassword.requestFocusInWindow();
                return;
            }
        }

        // if logged in
        if(Main.LoggedIn)
        {
            if (cbRememberPassword.isSelected())
            {
                profile.RememberPassword = true;
            }

            if (cbDefaultProfile.isSelected())
            {
                Profiles.SetAutoProfile(profile);
            }

            if (profile.contactsScreen == null)
                Utils.setWindowCentered(Main.frmChat);
            else
                Main.frmChat.setBounds(profile.chatScreen);

            Profiles.lastProfile = profile;
            Profiles.SaveProfiles();
            Main.profile = profile;
            Main.menuScreen.setVisible(false);
            Main.SetSkin(DaxtopPanel.GetSkinByName(Main.profile.lastUsedSkin), true);
            Main.reloadUiTrees();
            for (NetworkProfile np : NetworkManager.networks)
            {
                np.DaxtopSkinChanged();
            }
            Main.ShowContactsScreen();
        }
        else
        {
            Main.menuScreen.setScreen(this);
            Main.menuScreen.getRootPane().setDefaultButton(btnLogin);
            Main.menuScreen.requestFocus();
        }
    }

    /** Keeps the status of the checkboxes in sync with the profiles */
    public void UpdateCheckboxStatus()
    {
        DaxtopProfile profile = Profiles.GetProfile(cbUsername.getSelectedItem().toString());

        if (profile != null)
        {
            if (profile.RememberPassword)
            {
                cbRememberPassword.setSelected(true);
                pfPassword.setText(profile.getPassword());
            }
            else
            {
                cbRememberPassword.setSelected(false);
                pfPassword.setText("");
            }
        }
        else
        {
            cbRememberPassword.setSelected(false);
            pfPassword.setText("");
        }

        DaxtopProfile autoProfile = Profiles.GetAutoProfile();

        if (autoProfile != null && autoProfile.Username.equals(profile.Username))
            cbDefaultProfile.setSelected(true);
        else
            cbDefaultProfile.setSelected(false);

        // can only auto login if it is selected
        if (!cbRememberPassword.isSelected())
        {
            cbDefaultProfile.setSelected(false);
            cbDefaultProfile.setEnabled(false);
        }
        else
        {
            cbDefaultProfile.setEnabled(true);
        }
    }

    /** User selects a different item */
    public void itemStateChanged(ItemEvent e)
    {
        UpdateCheckboxStatus();
    }
}
