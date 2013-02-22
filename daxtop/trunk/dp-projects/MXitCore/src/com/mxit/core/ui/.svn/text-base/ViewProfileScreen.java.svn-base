package com.mxit.core.ui;

import com.dax.Main;
import com.dax.daxtop.ui.components.JPictureBox;
import com.mxit.core.model.MXitContact;
import com.mxit.core.model.UserSession;
import com.mxit.core.res.MXitRes;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

/**
 * @author Dax Booysen
 * 
 * A screen used by the user to view other user's profiles
 */
public class ViewProfileScreen extends JPanel implements ActionListener
{
    public JPictureBox pbAvatarImage;
    public JLabel lblNickname;
    public JTextPane lblProfileData;
    public JButton btnClose;              // constant values

    static final String empty = "(Empty)";
    static final String hidden = "(Hidden)";

    /** Constructs a new SettingsScreen */
    public ViewProfileScreen(MXitContact contact, UserSession profile, java.awt.Image avatar)
    {
        // init components
        lblNickname = new JLabel("<html><b><u>" + (contact.Nickname.trim().length() > 23 ? contact.Nickname.trim().substring(0, 20) + "..." : contact.Nickname.trim()) + "'s Profile</u></b></html>");
        lblNickname.setHorizontalAlignment(SwingConstants.CENTER);
        pbAvatarImage = new JPictureBox(avatar == null ? MXitRes.IMG_DEFAULT_AVATAR.getImage() : avatar);
        pbAvatarImage.centered = true;
        pbAvatarImage.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        lblProfileData = new JTextPane();
        lblProfileData.setContentType("text/html");
        lblProfileData.setEditable(false);
        lblProfileData.setText("<html><font face=\"Arial\" size=\"3\"><b>Name:</b> " + (profile.fullName.isEmpty() ? empty : profile.fullName)
                               + "<br/><br/><b>Status:</b> " + (profile.StatusMessage.isEmpty() ? (contact.Status.isEmpty() ? "(Empty)" : contact.Status) : profile.StatusMessage)
                               + "<br/><br/><b>User ID:</b> " + (profile.hideMXitID.equals("1") ? hidden : contact.MXitID)
                               + "<br/><br/><b>Gender:</b> " + (profile.gender.equals("1") ? "Male" : "Female")
                               + "<br/><br/><b>Birthdate:</b> " + (profile.birthDate.isEmpty() ? empty : profile.birthDate)
                               + "</font></html>");
        lblProfileData.setCaretPosition(0);
        JScrollPane scrollPane = new JScrollPane(lblProfileData, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        btnClose = new JButton("Done");

        // remove layout manager (absolute)
        setLayout(null);

        // add components
        add(lblNickname);
        add(pbAvatarImage);
        add(scrollPane);
        add(btnClose);

        // layout components
        lblNickname.setBounds(10, 15, 265, 20);
        pbAvatarImage.setBounds(95, 40, 96, 96);
        lblProfileData.setSize(new Dimension(238, 10000));
        scrollPane.setBounds(15, 140, 250, 155);
        btnClose.setBounds(100, 305, 80, 25);

        // attach action listeners
        btnClose.addActionListener(this);

        // decorate
        this.setBorder(BorderFactory.createBevelBorder(0));
    }

    // button clicked
    public void actionPerformed(ActionEvent e)
    {
        // "Close" clicked
        if (e.getSource() == btnClose)
        {
            Main.menuScreen.setVisible(false);
        }
    }
}
