package com.mxit.core.ui;

import com.dax.Main;
import com.dax.Profiles;
import com.dax.daxtop.ui.components.ImagePreviewPanel;
import com.dax.daxtop.ui.components.JPictureBox;
import com.dax.list.item.state.MessageState;
import com.dax.control.type.MessageTxType;
import com.mxit.MXitNetwork;
import com.mxit.core.model.MXitContact;
import com.mxit.core.model.MXitMessage;
import com.mxit.core.model.Mood;
import com.mxit.core.model.UserSession;
import com.mxit.core.model.type.MXitMessageType;
import com.mxit.core.model.type.MXitPresence;
import com.mxit.core.res.MXitRes;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map.Entry;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @author Dax Booysen
 *
 * The main panel displayed for a user above the contact list
 */
public class StatusScreen extends JPanel implements KeyListener, MouseListener, FocusListener
{
    /** The profile picture box */
    JPictureBox profPic;
    /** The status message box */
    private JTextField txtStatus;

    private JButton btnPresence, btnMood, btnUnreadMessages;

    /** last browsed avatar path */
    private String lastBrowsedPath = null;

    // current status
    private String statusText = "Set your status message here...";

    // current avatar
    private Image avatar = MXitRes.IMG_DEFAULT_AVATAR.getImage();

    /** MXitNetwork Instance */
    private MXitNetwork mxitNetwork = null;

    /** Constructor */
    public StatusScreen(MXitNetwork network)
    {
        // set the network
        mxitNetwork = network;

        init();

        // setup listeners
        txtStatus.addKeyListener(this);
        txtStatus.addMouseListener(this);
        profPic.addMouseListener(this);
        this.addMouseListener(this);
        txtStatus.addFocusListener(this);
        txtStatus.setEnabled(false);
    }

    /** init components */
    private void init()
    {
        // setup for grid bag layout
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // setup profile pic box
        JPanel picPanel = new JPanel();
        profPic = new JPictureBox(avatar);
        profPic.setPreferredSize(new Dimension(48, 48));
        profPic.setMaximumSize(new Dimension(48, 48));
        picPanel.setPreferredSize(new Dimension(53, 53));
        picPanel.setMaximumSize(new Dimension(53, 53));
        picPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        picPanel.setLayout(new BorderLayout());
        picPanel.add(profPic, BorderLayout.CENTER);
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(2, 4, 1, 2);
        this.add(picPanel, gbc);

        // setup presence selection
        ImageIcon statusIcon = MXitRes.IMG_STATUS_OFFLINE;

        if (mxitNetwork.currentPresence == MXitPresence.Online)
        {
            statusIcon = MXitRes.IMG_STATUS_ONLINE;
        }
        else if (mxitNetwork.currentPresence == MXitPresence.Away)
        {
            statusIcon = MXitRes.IMG_STATUS_AWAY;
        }
        else if (mxitNetwork.currentPresence == MXitPresence.DND)
        {
            statusIcon = MXitRes.IMG_STATUS_DND;
        }
        btnPresence = new JButton(statusIcon);
        btnPresence.setToolTipText(mxitNetwork.currentPresence.name());
        btnPresence.setPreferredSize(new Dimension(22,22));
        btnPresence.setFocusable(false);
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 0;      //make this component tall
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        gbc.gridheight = 1;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(1, 1, 1, 1);
        this.add(btnPresence, gbc);

        btnUnreadMessages = new JButton(MXitRes.IMG_READ_MESSAGES);
        btnUnreadMessages.setPreferredSize(new Dimension(22,22));
        btnUnreadMessages.setHorizontalAlignment(SwingUtilities.CENTER);
        btnUnreadMessages.setToolTipText("No updates...");
        btnUnreadMessages.setFocusable(false);
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 0;      //make this component tall
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        //gbc.gridwidth = 2;
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.insets = new Insets(1, 1, 1, 1);
        this.add(btnUnreadMessages, gbc);

        // setup mood selection
        btnMood = new JButton(new ImageIcon(mxitNetwork.Res.Moods.get(mxitNetwork.currentMood)));
        btnMood.setPreferredSize(new Dimension(22,22));
        btnMood.setHorizontalAlignment(SwingUtilities.CENTER);
        btnMood.setToolTipText(mxitNetwork.currentMood.GetText());
        btnMood.setFocusable(false);
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 0;      //make this component tall
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        //gbc.gridheight = 1;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(1, 1, 1, 1);
        this.add(btnMood, gbc);

        // setup status
        txtStatus = new JTextField(1);
        txtStatus.setText(statusText);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipady = 0;      //make this component tall
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(1, 2, 2, 2);
        this.add(txtStatus, gbc);

        // action listeners...

        // clicked unread conversations button
        btnUnreadMessages.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (mxitNetwork.unreadConversations.isEmpty())
                    return;

                JPopupMenu popupMenu = new JPopupMenu();

                for(Entry<String,String> contact : mxitNetwork.unreadConversations.entrySet())
                {
                    final MXitContact c = mxitNetwork.getContactFromMXitID(contact.getValue());

                    JMenuItem item = new JMenuItem(contact.getKey(), MXitNetwork.GetPresenceIcon(c));
                    item.addActionListener(new ActionListener()
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            // open conversation
                            // handle normal chats
                            mxitNetwork.ShowChat(c);

                            if (c.messageState == MessageState.CLEAR)
                            {
                                switch (c.Type)
                                {
                                    case Service:
                                    case ChatZone:
                                    case Gallery:
                                    case Info:
                                    {
                                        c.messageState = MessageState.READ;
                                        mxitNetwork.SendMXitMessage(new MXitMessage(c.MXitID, "", MXitMessageType.Normal, 0, com.mxit.core.model.type.MessageTxType.Sent));
                                    }
                                    break;
                                    default:
                                    {
                                        if (c.Nickname.equals("Tradepost"))
                                        {
                                            c.messageState = MessageState.READ;
                                            mxitNetwork.SendMXitMessage(new MXitMessage(c.MXitID, "",MXitMessageType.Normal, 0, com.mxit.core.model.type.MessageTxType.Sent));
                                        }
                                    }
                                }
                            }
                        }
                    });

                    // add to menu
                    popupMenu.add(item);
                }

                popupMenu.show(btnUnreadMessages, btnUnreadMessages.getWidth(), 0);
            }
        });

        // clicked mood button
        btnMood.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JPopupMenu popupMenu = new JPopupMenu();

                for(Entry<Mood, Image> mood : mxitNetwork.Res.Moods.entrySet())
                {
                    final Entry<Mood, Image> entry = mood;
                    JMenuItem item = new JMenuItem(mood.getKey().GetText(), new ImageIcon(mood.getValue()));
                    item.addActionListener(new ActionListener()
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            // set mood here
                            Mood m = entry.getKey();
                            btnMood.setIcon(new ImageIcon(entry.getValue()));
                            btnMood.setToolTipText(m.GetText());
                            mxitNetwork.currentMood = m;
                            mxitNetwork.CurrentProfile.mood = (byte)m.getIndex();
                            mxitNetwork.SetMood(m);
                            Main.profile.UpdateProfile(mxitNetwork.CurrentProfile.SaveProfile());
                            Profiles.SaveProfiles();
                        }
                    });

                    // add to menu
                    popupMenu.add(item);
                }

                popupMenu.show(btnMood, btnMood.getWidth(), 0);
            }
        });

        // clicked presence button
        btnPresence.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JPopupMenu popupMenu = new JPopupMenu();

                for(MXitPresence presence : MXitPresence.values())
                {
                    final MXitPresence p = presence;
                    JMenuItem item = null;
                    switch(p)
                    {
                        case Online:
                        {
                            item = new JMenuItem(presence.name(), MXitRes.IMG_STATUS_ONLINE);
                        }
                        break;
                        case Away:
                        {
                            item = new JMenuItem(presence.name(), MXitRes.IMG_STATUS_AWAY);
                        }
                        break;
                        case DND:
                        {
                            item = new JMenuItem(presence.name(), MXitRes.IMG_STATUS_DND);
                        }
                        break;
                        default:
                        {
                            continue;
                        }
                    }
                    item.addActionListener(new ActionListener()
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            setPresence(p);
                        }
                    });

                    // add to menu
                    popupMenu.add(item);
                }

                popupMenu.show(btnPresence, btnPresence.getWidth(), 0);
            }
        });
    }

    /** Sets the presence on the ui */
    public void setPresence(MXitPresence p)
    {
        if (p != MXitPresence.Offline)
        {
            String statusMsg = txtStatus.getText();

            if (statusMsg.equals("Set your status message here..."))
                statusMsg = "";

            mxitNetwork.setPresence(p, statusMsg);
        }

        mxitNetwork.mnuOnline.setState(false);
        mxitNetwork.mnuAway.setState(false);
        mxitNetwork.mnuDND.setState(false);

        // set mood here
        switch(p)
        {
            case Online:
            {
                btnPresence.setIcon(MXitRes.IMG_STATUS_ONLINE);
                mxitNetwork.mnuOnline.setState(true);
            }
            break;
            case Away:
            {
                btnPresence.setIcon(MXitRes.IMG_STATUS_AWAY);
                mxitNetwork.mnuAway.setState(true);
            }
            break;
            case DND:
            {
                btnPresence.setIcon(MXitRes.IMG_STATUS_DND);
                mxitNetwork.mnuDND.setState(true);
            }
            break;
        }

        btnPresence.setToolTipText(p.name());

        mxitNetwork.currentPresence = p;
        mxitNetwork.CurrentProfile.presence = (byte)MXitPresence.getIndex(p);
        Main.profile.UpdateProfile(mxitNetwork.CurrentProfile.SaveProfile());
        Profiles.SaveProfiles();
    }

    /** Sets the mood on the ui */
    public void setMood(Mood m)
    {
        btnMood.setIcon(new ImageIcon(mxitNetwork.Res.Moods.get(m)));
        btnMood.setToolTipText(m.GetText());
        mxitNetwork.SetMood(m);
    }

    /** Updates the mood icon */
    public void updateMoodIcon()
    {
        btnMood.setIcon(new ImageIcon(mxitNetwork.Res.Moods.get(mxitNetwork.currentMood)));
    }

    public void keyTyped(KeyEvent e)
    {
    }

    public void keyPressed(KeyEvent e)
    {
        if (e.getSource() == txtStatus && e.getKeyCode() == KeyEvent.VK_ENTER)
        {
            txtStatus.setEnabled(false);
        }
    }

    public void keyReleased(KeyEvent e)
    {
    }

    public void mouseClicked(MouseEvent e)
    {
        if (!mxitNetwork.LoggedIn)
            return;

        // user selected profile picture
        if (e.getSource() == profPic)
        {
            // change profile picture
            JFileChooser fileChooser = new JFileChooser();
            if (lastBrowsedPath != null)
                fileChooser.setCurrentDirectory(new File(lastBrowsedPath));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "gif", "png", "jpeg", "bmp");
            fileChooser.setFileFilter(filter);
            ImagePreviewPanel preview = new ImagePreviewPanel();
            fileChooser.setAccessory(preview);
            fileChooser.addPropertyChangeListener(preview);
            int result = fileChooser.showOpenDialog(this);

            if (result == JFileChooser.APPROVE_OPTION)
            {
                try
                {
                    BufferedImage imgIcon = ImageIO.read(fileChooser.getSelectedFile());

                    BufferedImage image = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
                    Graphics g = image.getGraphics();
                    g.drawImage(imgIcon.getScaledInstance(48, -1, Image.SCALE_SMOOTH), 0, 0, 48, 48, this);
                    g.dispose();

                    mxitNetwork.SetAvatar(image);

                    lastBrowsedPath = fileChooser.getSelectedFile().getAbsolutePath();
                }
                catch (Exception ex)
                {
                    // show message that the file was not correct
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
            }
        }
    }

    public void mousePressed(MouseEvent e)
    {
        if (!mxitNetwork.LoggedIn)
            return;

        if (e.getSource() == txtStatus && !txtStatus.isEnabled())
        {
            if (mxitNetwork.userSession.StatusMessage.isEmpty())
                txtStatus.setText("");

            txtStatus.setEnabled(true);
            txtStatus.selectAll();
            txtStatus.requestFocusInWindow();
        }
        else
        {
            txtStatus.setEnabled(false);
        }
    }

    public void mouseReleased(MouseEvent e)
    {

    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }

    public void focusGained(FocusEvent e)
    {
        
    }

    public void focusLost(FocusEvent e)
    {
        if (e.getSource() == txtStatus)
        {
            txtStatus.setEnabled(false);
            String statusMsg = txtStatus.getText();

            if (statusMsg.equals(""))
                txtStatus.setText("Set your status message here...");

            if (!statusMsg.equals(mxitNetwork.userSession.StatusMessage))
            {
                if (mxitNetwork.LoggedIn)
                {
                    mxitNetwork.setPresence(mxitNetwork.currentPresence, statusMsg);
                    mxitNetwork.userSession.StatusMessage = statusMsg;
                }
            }
        }
    }

    /** Change the status message */
    public void setStatus(String StatusMessage)
    {
        if (StatusMessage.equals(""))
        {
            txtStatus.setText("Set your status message here...");
        }
        else
        {
            txtStatus.setText(StatusMessage);
        }
    }

    /** Change the avatar */
    public void setAvatar(Image img)
    {
        avatar = img;
        profPic.setImage(img);
    }

    synchronized void updateUnread()
    {
        if (!mxitNetwork.unreadConversations.isEmpty())
        {
            btnUnreadMessages.setIcon(MXitRes.IMG_UNREAD_MESSAGES);
            int i = mxitNetwork.unreadConversations.size();
            btnUnreadMessages.setToolTipText(i > 1 ? i + " updated conversations!" : "1 updated conversation!");
        }
        else
        {
            btnUnreadMessages.setIcon(MXitRes.IMG_READ_MESSAGES);
            btnUnreadMessages.setToolTipText("No updates...");
        }
    }

    /** Refreshes the ui on a skin change */
    void refresh()
    {
        profPic.updateUI();
        txtStatus.updateUI();
    }
}
