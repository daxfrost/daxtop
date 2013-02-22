package com.mxit.core.ui;

import com.dax.Main;
import com.dax.Profiles;
import com.dax.daxtop.ui.FrmContacts;
import com.dax.daxtop.ui.WaitScreen;
import com.mxit.core.exception.MXitConnectionFailedException;
import com.mxit.core.exception.MXitLoginFailedException;
import com.mxit.core.protocol.command.CommandLogin;
import com.dax.daxtop.ui.handlers.FocusHandler;
import com.mxit.MXitNetwork;
import com.mxit.core.res.MXitRes;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * @author Dax Booysen
 * 
 * This is the login screen
 */
public class LoginScreen extends JPanel implements ActionListener
{
    // components

    JPanel pnlFrame;
    JLabel lblMsisdn, lblPassword;
    public JTextField txtMsisdn;
    JCheckBox chkAutoLogin;
    JPasswordField txtPassword;
    public JButton btnLogin, btnBack;    // constants
    private final Color ErrorColour = new Color(140, 0, 0);
    /** Set this if the user has logged in before */
    private boolean firstLogin = true;
    /** Login thread */
    Thread loginThread = null;

    /** MXit Network */
    private MXitNetwork mxitNetwork = null;

    /** Constructor */
    public LoginScreen(MXitNetwork network)
    {
        // set the network
        mxitNetwork = network;

        initComponents(this);
    }

    /** Sets up the components for this JPanel */
    private void initComponents(Container c)
    {
        // init components
        lblMsisdn = new JLabel("User ID:");
        lblPassword = new JLabel("Pin:");
        txtMsisdn = new JTextField();
        txtPassword = new JPasswordField();
        btnLogin = new JButton("Login");
        btnBack = new JButton("Back");
        chkAutoLogin = new JCheckBox("Auto Login");
        pnlFrame = new JPanel();

        // remove layout manager (absolute)
        pnlFrame.setLayout(null);
        c.setLayout(new BorderLayout());

        // add components
        pnlFrame.add(lblMsisdn);
        pnlFrame.add(txtMsisdn);
        pnlFrame.add(lblPassword);
        pnlFrame.add(txtPassword);
        pnlFrame.add(chkAutoLogin);
        pnlFrame.add(btnLogin);
        pnlFrame.add(btnBack);
        c.add(pnlFrame, BorderLayout.CENTER);

        // wait screen
        //c.add(Main.waitScreen, gbc);
        //Main.waitScreen.setVisible(false);

        // layout components
        lblMsisdn.setBounds(20, 25, 150, 20);
        lblPassword.setBounds(20, 90, 150, 20);
        txtMsisdn.setBounds(18, 50, 240, 25);
        txtPassword.setBounds(20, 115, 240, 25);
        chkAutoLogin.setBounds(20, 145, 240, 25);
        btnLogin.setBounds(20, 275, 100, 25);
        btnBack.setBounds(125, 275, 100, 25);

        // tab index order
        Vector<Component> tabOrder = new Vector<Component>();
        tabOrder.add(txtMsisdn);
        tabOrder.add(txtPassword);
        tabOrder.add(chkAutoLogin);
        tabOrder.add(btnLogin);
        tabOrder.add(btnBack);
        FocusHandler focusHandler = new FocusHandler(tabOrder);
        pnlFrame.setFocusTraversalPolicy(focusHandler);
        pnlFrame.setFocusTraversalKeysEnabled(true);
        pnlFrame.setFocusCycleRoot(true);

        // attach action listeners
        btnLogin.addActionListener(this);
        btnBack.addActionListener(this);
        chkAutoLogin.addActionListener(this);
        txtMsisdn.addKeyListener(new KeyListener());
        txtPassword.addKeyListener(new KeyListener());
        
        // decorate
        pnlFrame.setBorder(BorderFactory.createBevelBorder(0));

        // default states
        btnLogin.setEnabled(false);
        chkAutoLogin.setSelected(mxitNetwork.CurrentProfile.RememberPassword);
    }

    /** Sets the text of the msisdn for the user */
    public void setMsisdn(String msisdn)
    {
        txtMsisdn.setEnabled(false);
        txtMsisdn.setText(msisdn);
        txtPassword.requestFocusInWindow();
    }

    /** Used for changes to the text fields */
    class KeyListener implements java.awt.event.KeyListener
    {

        public void keyTyped(KeyEvent e)
        {
            if (txtPassword == e.getSource())
            {
                if (!String.valueOf(e.getKeyChar()).equals("\b") && !Character.isDigit(e.getKeyChar()))
                {
                    e.consume();
                }
            }

            if (txtMsisdn.getBackground() == ErrorColour)
            {
                txtMsisdn.setBackground(Color.WHITE);
            }
            
            Change();
        }

        public void keyPressed(KeyEvent e)
        {
            Change();
        }

        public void keyReleased(KeyEvent e)
        {
            Change();
        }

        public void Change()
        {
            if (!txtMsisdn.getText().isEmpty() && txtPassword.getPassword().length > 0)
            {
                btnLogin.setFocusable(true);
                btnLogin.setEnabled(true);
            }
            else
            {
                btnLogin.setEnabled(false);
                btnLogin.setFocusable(false);
            }
        }
    }

    // action events
    public void actionPerformed(ActionEvent e)
    {
        // "Login" clicked
        if (e.getSource() == btnLogin)
        {
            pnlFrame.setVisible(false);
            Main.menuScreen.setScreen(Main.waitScreen);
            Main.waitScreen.DynamicText = "Logging In...";
            Main.waitScreen.Start();
            Main.waitScreen.setVisible(true);

            mxitNetwork.CurrentProfile.HashPin(new String(txtPassword.getPassword()));

            // show processing screen
            WaitScreen.setCancelVisible(true);
            WaitScreen.setCancelListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    if (loginThread != null)
                        loginThread.interrupt();
                    Main.waitScreen.setVisible(false);
                    Main.waitScreen.Stop();
                    Main.menuScreen.setVisible(false);

                    mxitNetwork.Disconnect();
                    mxitNetwork.CurrentProfile = null;
                    Main.NetworkManager.RemoveNetwork(mxitNetwork);
                    Main.frmContacts.SetCurrentNetworkProfile(null);
                }
            });

            Login();
        }
        else if (e.getSource() == chkAutoLogin)
        {
            mxitNetwork.CurrentProfile.RememberPassword = chkAutoLogin.isSelected();
        }
        else
        {
            Main.menuScreen.setVisible(false);
        }
    }

    /** User login */
    public void Login()
    {
        // show splash - TODO show loading screen or MXit splash
        //Main.splashFrame.ShowSplash();
        loginThread = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    LoginRequest();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        // do login
        loginThread.start();
    }

    public void LoginRequest()
    {
        // start mxit lib
        mxitNetwork.Start();

        try
        {
            if (mxitNetwork.Connected)
            {
                mxitNetwork.Disconnect();
            }

            // connect to MXit
            mxitNetwork.Connect();

            // Login to MXit
            final CommandLogin cmdLogin = mxitNetwork.Login();

            // we have most likely cancelled the request
            if (cmdLogin == null)
            {
                return;
            }

            // Check response
            switch (cmdLogin.ErrorCode)
            {
                case MXitNetwork.NO_ERROR:
                {
                    firstLogin = false;
                    Main.profile.UpdateProfile(mxitNetwork.CurrentProfile.SaveProfile());
                    Profiles.SaveProfiles();
                    mxitNetwork.AfterLogin();
                    FrmContacts.SetCurrentNetworkProfile(mxitNetwork);
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            Main.menuScreen.setVisible(false);
                        }
                    });
                    Main.waitScreen.Stop();
                    break;
                }
                case CommandLogin.ErrorInvalidPassword:
                {
                        SwingUtilities.invokeLater(new Runnable()
                        {

                            public void run()
                            {
                                try
                                {
                                    Main.waitScreen.Stop();
                                    Main.waitScreen.setVisible(false);
                                    txtPassword.setText("");
                                    txtMsisdn.setText(mxitNetwork.CurrentProfile.MXitID);
                                    pnlFrame.setVisible(true);
                                    Main.menuScreen.setScreen(pnlFrame);
                                    JOptionPane.showMessageDialog(pnlFrame, cmdLogin.ErrorMessage, "Your MXit pin did not match...", JOptionPane.ERROR_MESSAGE);
                                    btnLogin.setEnabled(false);
                                    txtPassword.requestFocus();
                                    Main.menuScreen.setVisible(true);
                                    Main.menuScreen.getRootPane().setDefaultButton(btnLogin);
                                }
                                catch (Exception e)
                                {
                                    // if something goes horribly wrong or user clicked cancel or something just close window
                                    Main.menuScreen.setVisible(false);
                                }
                            }
                        });

                    break;
                }
                case CommandLogin.ErrorRedirectToProxy:
                {
                    System.out.println("Redirect not implemented!");
                    break;
                }
                default:
                {
                        SwingUtilities.invokeLater(new Runnable()
                        {

                            public void run()
                            {
                                try
                                {
                                    Main.waitScreen.Stop();
                                    Main.waitScreen.setVisible(false);
                                    txtPassword.setText("");
                                    btnLogin.setEnabled(false);
                                    pnlFrame.setVisible(true);
                                    txtMsisdn.setText(mxitNetwork.CurrentProfile.MXitID);
                                    Main.menuScreen.setScreen(pnlFrame);
                                    // any other error that occurs will result in login failure
                                    JOptionPane.showMessageDialog(pnlFrame, cmdLogin.ErrorMessage, "MXit login failed...", JOptionPane.ERROR_MESSAGE);
                                    // return user to userProfile menu
                                    btnBack.requestFocus();
                                    Main.menuScreen.setVisible(true);
                                    Main.menuScreen.getRootPane().setDefaultButton(btnLogin);
                                }
                                catch (Exception e)
                                {
                                    // if something goes horribly wrong or user clicked cancel or something just close window
                                    Main.menuScreen.setVisible(false);
                                }
                            }
                        });
                }
            }
        }
        catch (MXitConnectionFailedException ex)
        {
        }
        catch (MXitLoginFailedException ex)
        {
        }
    }
}
