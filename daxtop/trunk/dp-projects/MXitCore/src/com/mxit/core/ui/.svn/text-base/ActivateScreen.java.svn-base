package com.mxit.core.ui;

import com.dax.Main;
import com.dax.Profiles;
import com.dax.daxtop.ui.WaitScreen;
import com.mxit.core.activation.MXitActivation;
import com.mxit.core.activation.MXitActivationChallenge;
import com.mxit.core.activation.MXitActivationRequest;
import com.mxit.core.activation.MXitActivationResult;
import com.dax.daxtop.ui.components.JPictureBox;
import com.dax.daxtop.ui.handlers.FocusHandler;
import com.dax.daxtop.utils.Utils;
import com.mxit.MXitNetwork;
import com.mxit.core.model.UserProfile;
import com.mxit.core.protocol.command.CommandRegister;
import com.toedter.calendar.JDateChooser;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * @author Dax Booysen
 * 
 * A screen used by the user to activate and enter a captcha challenge
 */
public class ActivateScreen extends JPanel implements ActionListener, FocusListener
{

    public JPanel pnlFrame;
    public JLabel lblMsisdn, lblPassword,  lblCountry,  lblLanguage,  lblCaptcha, lblNickname, lblDateOfBirth;
    public JTextField txtMsisdn,  txtCaptcha, txtNickname;
    JPasswordField txtPassword;
    public JComboBox cbCountry,  cbLanguage;
    public JPictureBox pbCaptcha;
    public JButton btnAccept, btnRegister, btnCancel;    // constant values
    public ButtonGroup bgGender;
    public JRadioButton rbGenderMale, rbGenderFemale;
    public JDateChooser birthDate;

    private final short maxCaptchaInput = 10;
    private final Color ErrorColour = new Color(140, 0, 0);    // activation objects
    MXitActivationChallenge mac;
    FocusHandler focusHandler1, focusHandler2;

    /** a flag to say the user has cancelled the request */
    private boolean cancelled = false;

    /** The MXitNetwork Instance */
    private MXitNetwork mxitNetwork = null;

    /** Constructs a new ActivateScreen */
    public ActivateScreen(MXitNetwork network)
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
        lblNickname = new JLabel("Nickname:");
        lblPassword = new JLabel("Pin:");
        lblCountry = new JLabel("Select your country:");
        lblLanguage = new JLabel("Select your language:");
        lblCaptcha = new JLabel("Verification code:");
        txtMsisdn = new JTextField();
        txtCaptcha = new JTextField();
        txtPassword = new JPasswordField();
        txtNickname = new JTextField();
        cbCountry = new JComboBox();
        cbLanguage = new JComboBox();

        rbGenderMale = new JRadioButton("Male");
        rbGenderFemale = new JRadioButton("Female");
        bgGender = new ButtonGroup();
        bgGender.add(rbGenderFemale);
        bgGender.add(rbGenderMale);
        pbCaptcha = new JPictureBox();
        btnAccept = new JButton("Login");
        btnRegister = new JButton("Register");
        btnCancel = new JButton("Back");

        pnlFrame = new JPanel();

        // stage 2
        lblDateOfBirth = new JLabel("Date of birth:");
        birthDate = new JDateChooser(new Date());
        birthDate.getJCalendar().setPreferredSize(new Dimension(350, 250));
        rbGenderMale.setSelected(true);

        // set invisible
        lblDateOfBirth.setVisible(false);
        birthDate.setVisible(false);
        rbGenderMale.setVisible(false);
        rbGenderFemale.setVisible(false);
        lblNickname.setVisible(false);
        txtNickname.setVisible(false);

        // remove layout manager (absolute)
        pnlFrame.setLayout(null);
        c.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;

        // add components
        pnlFrame.add(lblMsisdn);
        pnlFrame.add(lblPassword);
        pnlFrame.add(lblCountry);
        pnlFrame.add(lblLanguage);
        pnlFrame.add(lblCaptcha);
        pnlFrame.add(lblNickname);
        pnlFrame.add(txtMsisdn);
        pnlFrame.add(txtPassword);
        pnlFrame.add(cbCountry);
        pnlFrame.add(cbLanguage);
        pnlFrame.add(txtCaptcha);
        pnlFrame.add(pbCaptcha);
        pnlFrame.add(btnAccept);
        pnlFrame.add(btnRegister);
        pnlFrame.add(btnCancel);
        pnlFrame.add(lblDateOfBirth);
        pnlFrame.add(birthDate);
        pnlFrame.add(txtNickname);
        pnlFrame.add(rbGenderMale);
        pnlFrame.add(rbGenderFemale);
        c.add(pnlFrame, gbc);

        // wait screen
        c.add(Main.waitScreen, gbc);
        Main.waitScreen.setVisible(false);

        // layout components
        lblMsisdn.setBounds(20, 15, 150, 20);
        lblPassword.setBounds(20, 65, 150, 20);
        lblLanguage.setBounds(20, 120, 150, 20);
        lblCountry.setBounds(20, 170, 150, 20);
        txtMsisdn.setBounds(18, 35, 240, 25);
        txtPassword.setBounds(18, 85, 240, 25);
        cbLanguage.setBounds(20, 140, 240, 20);
        cbCountry.setBounds(20, 190, 240, 20);
        pbCaptcha.setBounds(127, 244, 80, 20);
        lblCaptcha.setBounds(22, 220, 155, 20);
        txtCaptcha.setBounds(22, 242, 85, 25);
        btnAccept.setBounds(22, 285, 77, 25);
        btnRegister.setBounds(102, 285, 77, 25);
        btnCancel.setBounds(182, 285, 77, 25);
        // stage 2
        lblNickname.setBounds(20, 15, 150, 20);
        txtNickname.setBounds(18, 35, 240, 25);
        rbGenderMale.setBounds(20, 65, 100, 20);
        rbGenderFemale.setBounds(125, 65, 100, 20);
        lblDateOfBirth.setBounds(20, 90, 150, 20);
        birthDate.setBounds(18, 115, 240, 25);


        txtMsisdn.addFocusListener(this);
        txtCaptcha.addFocusListener(this);

        // tab index order
        Vector<Component> tabOrder = new Vector<Component>();
        tabOrder.add(txtMsisdn);
        tabOrder.add(txtPassword);
        tabOrder.add(cbLanguage);
        tabOrder.add(cbCountry);
        tabOrder.add(txtCaptcha);
        tabOrder.add(btnAccept);
        tabOrder.add(btnRegister);
        Vector<Component> tabOrder2 = new Vector<Component>();
        tabOrder2.add(txtNickname);
        tabOrder2.add(rbGenderMale);
        tabOrder2.add(rbGenderFemale);
        tabOrder2.add(birthDate);
        focusHandler1 = new FocusHandler(tabOrder);
        focusHandler2 = new FocusHandler(tabOrder2);
        pnlFrame.setFocusTraversalPolicy(focusHandler1);
        pnlFrame.setFocusTraversalKeysEnabled(true);
        pnlFrame.setFocusCycleRoot(true);

        // attach action listeners
        btnAccept.addActionListener(this);
        btnRegister.addActionListener(this);
        btnCancel.addActionListener(this);
        KeyListener keyListener = new KeyListener();
        txtCaptcha.addKeyListener(keyListener);
        txtPassword.addKeyListener(keyListener);
        txtMsisdn.addKeyListener(keyListener);

        // decorate
        this.setBorder(BorderFactory.createBevelBorder(0));
        pnlFrame.setBorder(BorderFactory.createBevelBorder(0));

        // default states
        btnAccept.setEnabled(false);
        btnAccept.setFocusable(false);
    }

    /** Starts the captcha challenge */
    public void getStartChallenge()
    {
        final SwingWorker worker = new SwingWorker()
        {
            @Override
            protected Object doInBackground() throws Exception
            {
                ChallengeThread activationChallenge = new ChallengeThread();

                // execute challenge
                do
                {
                    try
                    {
                        activationChallenge.run();
                    }
                    catch (Exception e)
                    {
                        SwingUtilities.invokeAndWait(new Runnable() {
                            public void run()
                            {
                                if (JOptionPane.showConfirmDialog(Main.menuScreen, "Connection to MXit failed, please check your connection... Try again?", "MXit Connection Failed!", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                                {
                                    mac = null;
                                }
                                else
                                {
                                    Main.menuScreen.setVisible(false);
                                }
                            }
                        });
                    }
                }
                while (mac == null);

                SwingUtilities.invokeAndWait(new Runnable() {
                public void run()
                {
                    try
                    {
                        // create new model with values for languages
                        Object[] langs = mac.getLanguages().values().toArray();
                        java.util.Arrays.sort(langs);
                        ComboBoxModel cbModel = new DefaultComboBoxModel(langs);
                        cbLanguage.setModel(cbModel);
                        // set default to english
                        for (int i = 0; i < cbLanguage.getItemCount(); i++)
                        {
                            if (cbLanguage.getItemAt(i).equals("English"))
                            {
                                cbLanguage.setSelectedIndex(i);
                                break;
                            }
                        }

                        // create new model with values for countries
                        Object[] cntries = mac.getCountries().values().toArray();
                        java.util.Arrays.sort(cntries);
                        cbModel = new DefaultComboBoxModel(cntries);
                        cbCountry.setModel(cbModel);
                        // set default to english
                        for (int i = 0; i < cbCountry.getItemCount(); i++)
                        {
                            if (cbCountry.getItemAt(i).equals(mac.getDefaultCountryName()))
                            {
                                cbCountry.setSelectedIndex(i);
                                break;
                            }
                        }

                        // setup captcha image
                        pbCaptcha.setImage(mac.getCaptcha());
                        repaint();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    // done processing, return to screen
                    WaitScreen.DynamicText = "";
                    Main.waitScreen.setVisible(false);
                    pnlFrame.setVisible(true);
                    Main.waitScreen.Stop();
                    txtMsisdn.requestFocusInWindow();
                }
                });

                return null;
            }
        };

        WaitScreen.setCancelVisible(true);
        WaitScreen.setCancelListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                worker.cancel(true);
                mac = null;
                Main.waitScreen.Stop();
                Main.waitScreen.setVisible(false);
                Main.menuScreen.setVisible(false);
            }
        });

        worker.execute();

        // show processing screen
        pnlFrame.setVisible(false);
        WaitScreen.DynamicText = "Retrieving Activation Info";
        Main.waitScreen.setVisible(true);
        Main.waitScreen.Start();
    }

    // button clicked
    public void actionPerformed(ActionEvent e)
    {
        // "Accept" clicked
        if (e.getSource() == btnAccept)
        {
            final SwingWorker worker = new SwingWorker()
            {
                @Override
                protected Object doInBackground() throws Exception
                {
                    // login
                    if (lblMsisdn.isVisible())
                    {
                        // show processing screen
                        pnlFrame.setVisible(false);
                        WaitScreen.DynamicText = "Activating...";
                        Main.waitScreen.setVisible(true);
                        Main.waitScreen.Start();

                        Activate();
                    }
                    // register
                    else
                    {
                        // if fields are valid we can attempt to register
                        if (validateFields())
                        {
                            // show processing screen
                            pnlFrame.setVisible(false);
                            WaitScreen.DynamicText = "Activating...";
                            Main.waitScreen.setVisible(true);
                            Main.waitScreen.Start();

                            // create the request
                            MXitActivationRequest mar = new MXitActivationRequest(mac, txtMsisdn.getText(), txtCaptcha.getText(),
                                                                                  Utils.getKeyFromValue(mac.getCountries(), (String) cbCountry.getSelectedItem()),
                                                                                  Utils.getKeyFromValue(mac.getLanguages(), (String) cbLanguage.getSelectedItem()),
                                                                                  (String) cbLanguage.getSelectedItem(), false);

                            // activate
                            try
                            {
                                MXitActivationResult result = MXitActivation.Activate(mar);
                                result.setHashPin(new String(txtPassword.getPassword()));

                                switch (result.getErrorCode())
                                {
                                    case 0:
                                    {
                                        // no error
                                        WaitScreen.DynamicText = "Registering...";

                                        // start mxit lib
                                        String MXitID = result.getMsisdn().isEmpty() ? txtMsisdn.getText() : result.getMsisdn() ;
                                        UserProfile pfile = new UserProfile(MXitID);
                                        pfile.MXitID = MXitID;
                                        pfile.Nickname = txtNickname.getText();
                                        pfile.DialingCode = result.getDialingCode();
                                        pfile.Locale = result.request.getLocaleLanguage();
                                        pfile.ProductId = result.getPID();
                                        pfile.CountryCode = result.getCountryCode();
                                        pfile.SetSocket(result.getSocket1());
                                        pfile.SetSecondSocket(result.getSocket2());
                                        pfile.SetHttp(result.getHttp1());
                                        pfile.SetSecondHttp(result.getHttp2());
                                        pfile.HashPin(result.getHashPin());

                                        // set as the current userProfile
                                        mxitNetwork.CurrentProfile = pfile;

                                        // connect to and register on MXit
                                        Thread t = new Thread(new RegisterThread());
                                        t.start();
                                        break;
                                    }
                                    // Wrong Captcha
                                    case 1:
                                    {
                                        // done processing, return to screen
                                        Main.waitScreen.Stop();
                                        Main.waitScreen.setVisible(false);

                                        showStepTwo(false);

                                        // incorrect captcha
                                        pbCaptcha.setImage(result.getCaptcha());
                                        txtCaptcha.setBackground(ErrorColour);

                                        // done processing, return to screen
                                        pnlFrame.setVisible(true);

                                        txtCaptcha.requestFocusInWindow();
                                        txtCaptcha.selectAll();
                                        break;
                                    }
                                    case 2:
                                    {
                                        // done processing, return to screen
                                        Main.waitScreen.Stop();
                                        Main.waitScreen.setVisible(false);

                                        showStepTwo(false);

                                        // session expired
                                        pbCaptcha.setImage(result.getCaptcha());

                                        if (result.getSessionId() != null)
                                        {
                                            mac.setSessionId(result.getSessionId());                    // done processing, return to screen
                                        }
                                        pnlFrame.setVisible(true);

                                        txtCaptcha.setText("");
                                        txtCaptcha.requestFocusInWindow();

                                        break;
                                    }
                                    case 3:
                                    {
                                        // done processing, return to screen
                                        Main.waitScreen.Stop();
                                        Main.waitScreen.setVisible(false);

                                        showStepTwo(false);

                                        // undefined error - clear fields and retry entire procedure
                                        int resp = JOptionPane.showConfirmDialog(Main.menuScreen, "Undefined error occured with MXit activation, retry activation?", "Undefined Activation Error", JOptionPane.YES_NO_OPTION);

                                        if (resp == JOptionPane.YES_OPTION)
                                        {
                                            Main.waitScreen.Start();

                                            getStartChallenge();

                                            txtMsisdn.setText("");
                                            txtCaptcha.setText("");

                                            txtMsisdn.requestFocusInWindow();
                                        }
                                    }
                                }
                            }
                            catch (Exception ex)
                            {
                                ex.printStackTrace();
                            }
                        }
                    }

                    return null;
                }
            };

            WaitScreen.setCancelVisible(true);
            WaitScreen.setCancelListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    cancelled = true;
                    if (worker != null)
                        worker.cancel(true);
                    Main.waitScreen.setVisible(false);
                    Main.waitScreen.Stop();
                    Main.menuScreen.setVisible(false);
                }
            });

            // execute the worker thread
            worker.execute();
        }
        // "Register" clicked
        else if (btnRegister == e.getSource())
        {
            showStepTwo(lblMsisdn.isVisible());
            
        }
        // back clicked
        else
        {
            Main.menuScreen.setVisible(false);
        }
    }

    /** true is sent if we must display registration screen */
    private void showStepTwo(boolean registrationOn)
    {
        if (registrationOn)
        {
            lblMsisdn.setVisible(false);
                lblPassword.setVisible(false);
                lblCountry.setVisible(false);
                lblLanguage.setVisible(false);
                lblCaptcha.setVisible(false);
                txtMsisdn.setVisible(false);
                txtCaptcha.setVisible(false);
                txtPassword.setVisible(false);
                cbCountry.setVisible(false);
                cbLanguage.setVisible(false);
                pbCaptcha.setVisible(false);
                // set visible for stage 2
                btnRegister.setText("Previous");
                btnAccept.setText("Register");
                birthDate.setVisible(true);
                lblDateOfBirth.setVisible(true);
                rbGenderMale.setVisible(true);
                rbGenderFemale.setVisible(true);
                lblNickname.setVisible(true);
                txtNickname.setVisible(true);
                pnlFrame.setFocusTraversalPolicy(focusHandler2);
        }
        else
        {
                lblMsisdn.setVisible(true);
                lblPassword.setVisible(true);
                lblCountry.setVisible(true);
                lblLanguage.setVisible(true);
                lblCaptcha.setVisible(true);
                txtMsisdn.setVisible(true);
                txtCaptcha.setVisible(true);
                txtPassword.setVisible(true);
                cbCountry.setVisible(true);
                cbLanguage.setVisible(true);
                pbCaptcha.setVisible(true);
                // set visible for stage 2
                btnRegister.setText("Register");
                btnAccept.setText("Login");
                birthDate.setVisible(false);
                lblDateOfBirth.setVisible(false);
                rbGenderMale.setVisible(false);
                rbGenderFemale.setVisible(false);
                lblNickname.setVisible(false);
                txtNickname.setVisible(false);
                pnlFrame.setFocusTraversalPolicy(focusHandler1);
        }
    }

    /** Activation */
    public void Activate()
    {
        // create the request
        MXitActivationRequest mar = new MXitActivationRequest(mac, txtMsisdn.getText(), txtCaptcha.getText(),
                Utils.getKeyFromValue(mac.getCountries(), (String) cbCountry.getSelectedItem()),
                Utils.getKeyFromValue(mac.getLanguages(), (String) cbLanguage.getSelectedItem()),
                (String) cbLanguage.getSelectedItem(), true);

        // activate
        try
        {
            MXitActivationResult result = MXitActivation.Activate(mar);

            // if the user cancelled
            if (cancelled)
                return;

            result.setHashPin(new String(txtPassword.getPassword()));

            // check error code
            switch (result.getErrorCode())
            {
                // No Error
                case 0:
                {
                    WaitScreen.DynamicText = "Logging in...";
                    // show login screen with data
                    mxitNetwork.ActivationLogin(result);
                    // done processing, return to screen
                    //pnlFrame.setVisible(true);
                    
                    // if this is the only profile for MXit we make it the default profile
                    if (Main.profile.GetProfilesForNetwork(MXitNetwork.networkId).size() == 1)
                        Main.profile.SetDefaultProfileForNetwork(MXitNetwork.networkId, mxitNetwork.CurrentProfile.profile.AccountId);

                    break;
                }
                // Wrong Captcha
                case 1:
                {
                    // done processing, return to screen
                    Main.waitScreen.Stop();
                    Main.waitScreen.setVisible(false);

                    // incorrect captcha
                    pbCaptcha.setImage(result.getCaptcha());
                    txtCaptcha.setBackground(ErrorColour);

                    // done processing, return to screen
                    pnlFrame.setVisible(true);

                    txtCaptcha.requestFocusInWindow();
                    txtCaptcha.selectAll();
                    break;
                }
                case 2:
                {
                    // done processing, return to screen
                    Main.waitScreen.Stop();
                    Main.waitScreen.setVisible(false);

                    // session expired
                    pbCaptcha.setImage(result.getCaptcha());

                    if (result.getSessionId() != null)
                    {
                        mac.setSessionId(result.getSessionId());                    // done processing, return to screen
                    }
                    pnlFrame.setVisible(true);

                    txtCaptcha.setText("");
                    txtCaptcha.requestFocusInWindow();

                    break;
                }
                case 3:
                {
                    // done processing, return to screen
                    Main.waitScreen.Stop();
                    Main.waitScreen.setVisible(false);

                    // undefined error - clear fields and retry entire procedure
                    int resp = JOptionPane.showConfirmDialog(this, "Undefined error occured with MXit activation, retry activation?", "Undefined Activation Error", JOptionPane.YES_NO_OPTION);

                    if (resp == JOptionPane.YES_OPTION)
                    {
                        Main.waitScreen.Start();

                        getStartChallenge();

                        txtMsisdn.setText("");
                        txtCaptcha.setText("");

                        txtMsisdn.requestFocusInWindow();
                    }
                    else
                    {
                        // stop activation procedure
                        this.setVisible(false);
                    }

                    break;
                }
                case 4:
                {
                    // done processing, return to screen
                    Main.waitScreen.Stop();
                    Main.waitScreen.setVisible(false);

                    // critical error - clear fields and retry entire procedure
                    int resp = JOptionPane.showConfirmDialog(this, "Critical error occured with MXit activation for " + result.getDomain() + ", retry activation?", "Critical Activation Error", JOptionPane.YES_NO_OPTION);

                    if (resp == JOptionPane.YES_OPTION)
                    {
                        Main.waitScreen.Start();

                        getStartChallenge();

                        txtMsisdn.setText("");
                        txtCaptcha.setText("");

                        txtMsisdn.requestFocusInWindow();
                    }
                    else
                    {
                        // stop activation procedure
                        this.setVisible(false);
                    }

                    break;
                }
                case 5:
                {
                    // done processing, return to screen
                    Main.waitScreen.Stop();
                    Main.waitScreen.setVisible(false);

                    // internal error - country code not available
                    JOptionPane.showMessageDialog(this, "Internal MXit activation error, country code not available, please select another country code.", "Internal Activation Error", JOptionPane.ERROR_MESSAGE);

                    cbCountry.setForeground(Color.RED);
                    cbCountry.setBackground(ErrorColour);
                    cbCountry.requestFocusInWindow();

                    // done processing, return to screen
                    pnlFrame.setVisible(true);

                    break;
                }
                case 6:
                {
                    // done processing, return to screen
                    Main.waitScreen.Stop();
                    Main.waitScreen.setVisible(false);

                    final Component t = this;

                    try
                    {
                        SwingUtilities.invokeLater(new Runnable()
                        {
                            public void run()
                            {
                                // user isnt registered (and path 1 was specified)
                                JOptionPane.showMessageDialog(t, "MXit activation error, " + txtMsisdn.getText() + " is not a registered user, please try again.", "User Not Registered", JOptionPane.ERROR_MESSAGE);
                            }
                        });
                    }
                    catch (Exception e) {}

                    pbCaptcha.setImage(result.getCaptcha());
                    mac.setSessionId(result.getSessionId());

                    if (!SwingUtilities.isEventDispatchThread())
                    {
                        try
                        {
                            SwingUtilities.invokeAndWait(new Runnable() {

                            public void run()
                            {
                                pnlFrame.setVisible(true);
                            }
                        });
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        // done processing, return to screen
                        pnlFrame.setVisible(true);
                    }

                    txtMsisdn.setText("");
                    txtCaptcha.setText("");
                    txtMsisdn.requestFocusInWindow();

                    break;
                }
                case 7:
                {
                    // done processing, return to screen
                    Main.waitScreen.Stop();
                    Main.waitScreen.setVisible(false);

                    // user is already registered (and path 0 was specified)
                    JOptionPane.showMessageDialog(this, "MXit activation error, " + txtMsisdn.getText() + " is an already registered user, please try again.", "User Already Registered", JOptionPane.ERROR_MESSAGE);

                    pbCaptcha.setImage(result.getCaptcha());
                    mac.setSessionId(result.getSessionId());

                    // done processing, return to screen
                    pnlFrame.setVisible(true);

                    txtMsisdn.setText("");
                    txtCaptcha.setText("");
                    txtMsisdn.requestFocusInWindow();

                    break;
                }
            }
        }
        catch (IOException ioe)
        {
            // todo - deal with failed request
        }

        WaitScreen.DynamicText = "";
    }

    /** Check that the fields are entered correctly */
    private boolean validateFields()
    {
        // get 14 years ago
        Calendar MinimumAge = Calendar.getInstance();
        MinimumAge.add(Calendar.YEAR, -14);

        if (txtNickname.getText().isEmpty())
        {
            JOptionPane.showMessageDialog(this, "You must enter a nickname!", "MXit Registration", JOptionPane.WARNING_MESSAGE);
        }
        else if (!(rbGenderMale.isSelected() || rbGenderFemale.isSelected()))
        {
            JOptionPane.showMessageDialog(this, "You must select a gender!", "MXit Registration", JOptionPane.WARNING_MESSAGE);
        }
        else if (birthDate.getCalendar().after(MinimumAge))
        {
            JOptionPane.showMessageDialog(this, "You must be atleast 14 years of age to use MXit!", "MXit Registration", JOptionPane.WARNING_MESSAGE);
        }
        else
        {
            return true;
        }

        return false;
    }

    public void focusGained(FocusEvent e)
    {
        Change();
    }

    public void focusLost(FocusEvent e)
    {
        Change();
    }

    /** Inner class to http fetch challenge on a seperate thread */
    class ChallengeThread
    {
        public void run() throws Exception
        {
            mac = MXitActivation.GetChallenge();
        }
    }

    public void Change()
    {
        if (!txtCaptcha.getText().isEmpty() && !txtMsisdn.getText().isEmpty())
        {
            btnAccept.setFocusable(true);
            btnAccept.setEnabled(true);
            btnRegister.setFocusable(true);
            btnRegister.setEnabled(true);
        }
        else
        {
            btnAccept.setEnabled(false);
            btnAccept.setFocusable(false);
            btnRegister.setFocusable(false);
            btnRegister.setEnabled(false);
        }
    }

    /** Used for changes to the text fields */
    class KeyListener implements java.awt.event.KeyListener
    {

        public void keyTyped(KeyEvent e)
        {
            if (String.valueOf(e.getKeyChar()).equals(" ") && e.getSource() == txtMsisdn)
            {
                e.consume();
            }

            // we always accept a backspace
            if (!String.valueOf(e.getKeyChar()).equals("\b") && e.getSource() != txtMsisdn)
            {
                // accept only numbers in text fields
                if (!Character.isDigit(e.getKeyChar()))
                {
                    e.consume();
                }
                else if (e.getSource() == txtCaptcha && txtCaptcha.getText().length() == maxCaptchaInput)
                {
                    e.consume();
                }
            }

            if (Character.isDigit(e.getKeyChar()) || String.valueOf(e.getKeyChar()).equals("\b"))
            {
                if (txtCaptcha.getBackground() == ErrorColour)
                {
                    txtCaptcha.setBackground(Color.WHITE);
                }
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
    }

    /** Registration thread runner */
    class RegisterThread implements Runnable
    {
        /** Register thread */
        public void run()
        {
            try
            {
                mxitNetwork.Start();

                // connect to MXit
                mxitNetwork.Connect();

                // once connected register
                CommandRegister cmd = mxitNetwork.Register(birthDate.getCalendar(), rbGenderMale.isSelected());
                //MXitNetwork.CurrentProfile.MXitID = cmd.MXitId;

                WaitScreen.DynamicText = "";
                Main.waitScreen.setVisible(false);
                Main.waitScreen.Stop();

                // no errors
                if (cmd.ErrorCode == 0)
                {
                    SwingUtilities.invokeAndWait(new Runnable()
                    {
                        public void run()
                        {
                            mxitNetwork.setLoggedInState();
                            Main.menuScreen.setVisible(false);
                            mxitNetwork.SetupContactsScreen();
                            mxitNetwork.AfterLogin();
                            Main.profile.AddProfile(mxitNetwork.CurrentProfile.SaveProfile());
                            
                            // if this is the only profile for MXit we make it the default profile
                            if (Main.profile.GetProfilesForNetwork(MXitNetwork.networkId).size() == 1)
                                Main.profile.SetDefaultProfileForNetwork(MXitNetwork.networkId, mxitNetwork.CurrentProfile.profile.AccountId);

                            Profiles.SaveProfiles();
                        }
                    });
                }
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
}
