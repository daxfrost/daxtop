package com.mxit.core.ui;

import com.dax.Main;
import com.dax.daxtop.ui.handlers.FocusHandler;
import com.mxit.MXitNetwork;
import com.mxit.core.model.MXitContact;
import com.mxit.core.model.type.MXitContactType;
import java.awt.BorderLayout;
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
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Dax Booysen
 * 
 * A screen used by the user to add a new contact
 */
public class AddContactScreen extends JPanel implements ActionListener, FocusListener
{
    public JPanel pnlFrame, pnlButtonFrame, splitPanel;
    public JLabel lblLoginName, lblNickname,  lblGroup, lblInviteMessage;
    public JTextField txtLoginName, txtNickname, txtInviteMessage;
    public JComboBox cbGroup;
    public JButton btnAccept,  btnCancel, btnMore;              // constant values
    private final Color ErrorColour = new Color(140, 0, 0);     // activation objects

    /** The MXit Network */
    private MXitNetwork mxitNetwork = null;

    /** Constructs a new AddContactScreen */
    public AddContactScreen(MXitNetwork network)
    {
        // set the network
        mxitNetwork = network;

        initComponents(this);
    }

    /** Sets up the components for this JPanel */
    private void initComponents(Container c)
    {
        // init components
        lblLoginName = new JLabel("MXit ID:");
        lblNickname = new JLabel("Nickname:");
        lblGroup = new JLabel("Add To Group:");
        lblInviteMessage = new JLabel("Invite Message:");
        txtLoginName = new JTextField();
        txtNickname = new JTextField();
        txtInviteMessage = new JTextField();
        cbGroup = new JComboBox();
        btnAccept = new JButton("Invite");
        btnCancel = new JButton("Cancel");
        btnMore = new JButton("More...");
        pnlFrame = new JPanel();
        pnlButtonFrame = new JPanel();
        splitPanel = new JPanel();

        // remove layout manager (absolute)
        pnlFrame.setLayout(null);
        pnlButtonFrame.setLayout(null);
        splitPanel.setLayout(new BorderLayout());
        c.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;

        // add components
        pnlFrame.add(lblLoginName);
        pnlFrame.add(lblNickname);
        pnlFrame.add(lblGroup);
        pnlFrame.add(lblInviteMessage);
        pnlFrame.add(txtLoginName);
        pnlFrame.add(txtNickname);
        pnlFrame.add(cbGroup);
        pnlFrame.add(txtInviteMessage);
        pnlButtonFrame.add(btnAccept);
        pnlButtonFrame.add(btnCancel);
        pnlButtonFrame.add(btnMore);
        c.add(splitPanel, gbc);
        splitPanel.add(pnlFrame, BorderLayout.NORTH);
        splitPanel.add(pnlButtonFrame, BorderLayout.CENTER);

        // wait screen
        c.add(Main.waitScreen, gbc);
        Main.waitScreen.setVisible(false);

        // layout components
        lblLoginName.setBounds(10, 15, 150, 20);
        lblNickname.setBounds(10, 70, 150, 20);
        lblGroup.setBounds(10, 125, 150, 20);
        txtLoginName.setBounds(10, 40, 240, 25);
        txtNickname.setBounds(10, 95, 240, 25);
        cbGroup.setBounds(10, 150, 240, 20);
        lblInviteMessage.setBounds(10, 175, 155, 20);
        txtInviteMessage.setBounds(10, 200, 240, 25);
        btnAccept.setBounds(10, 10, 80, 25);
        btnCancel.setBounds(95, 10, 65, 25);
        btnMore.setBounds(185, 10, 65, 25);
        pnlFrame.setPreferredSize(new Dimension(100, 70));
        pnlButtonFrame.setPreferredSize(new Dimension(100, 25));

        // tab index order
        Vector<Component> tabOrder = new Vector<Component>();
        tabOrder.add(txtLoginName);
        tabOrder.add(txtNickname);
        tabOrder.add(cbGroup);
        tabOrder.add(txtInviteMessage);
        tabOrder.add(btnAccept);
        tabOrder.add(btnCancel);
        FocusHandler focusHandler = new FocusHandler(tabOrder);
        splitPanel.setFocusTraversalPolicy(focusHandler);
        splitPanel.setFocusTraversalKeysEnabled(true);
        splitPanel.setFocusCycleRoot(true);

        Object[] groups = mxitNetwork.currentContactList.GetGroups(true, true);
        for(Object g : groups)
            cbGroup.addItem(g);

        // attach action listeners
        btnAccept.addActionListener(this);
        btnCancel.addActionListener(this);
        btnMore.addActionListener(this);
        txtNickname.addFocusListener(this);
        KeyListener l = new KeyListener();
        txtLoginName.addKeyListener(l);
        txtNickname.addKeyListener(l);

        // decorate
        this.setBorder(BorderFactory.createBevelBorder(0));
        splitPanel.setBorder(BorderFactory.createBevelBorder(0));

        // default states
        btnAccept.setEnabled(false);
        btnAccept.setFocusable(false);
    }

    // button clicked
    public void actionPerformed(ActionEvent e)
    {
        // "Accept" clicked
        if (e.getSource() == btnAccept)
        {
            // show processing screen
            MXitContact c = new MXitContact();
            c.Nickname = txtNickname.getText().isEmpty() ? txtLoginName.getText() : txtNickname.getText();

            String group = cbGroup.getSelectedItem().toString().equals("No Group") ? "" : cbGroup.getSelectedItem().toString();

            if (group.equals("Add New Group..."))
            {
                group = JOptionPane.showInputDialog(this, "Enter new group name: ", "Group Name...");
                group = group == null ? "" : group;
            }

            c.Group = group;
            c.Type = MXitContactType.MXit;
            mxitNetwork.AddContact(txtLoginName.getText(), c, c.Group, txtInviteMessage.getText());
            Main.menuScreen.setVisible(false);
        }
        else if (e.getSource() == btnMore)
        {
            // show more detail
            btnMore.setVisible(false);
            pnlFrame.setPreferredSize(new Dimension(pnlFrame.getWidth(), 270));
            pnlFrame.setSize(new Dimension(pnlFrame.getWidth(), 270));
            splitPanel.validate();
            Change();
        }
        // "Cancel" clicked
        else
        {
            Main.menuScreen.setVisible(false);
        }
    }

    public void focusGained(FocusEvent e)
    {
        if (btnMore.isVisible())
        {
            // expand options
            btnMore.setVisible(false);
            pnlFrame.setPreferredSize(new Dimension(pnlFrame.getWidth(), 270));
            pnlFrame.setSize(new Dimension(pnlFrame.getWidth(), 270));
            splitPanel.validate();
            Change();
        }
    }

    public void focusLost(FocusEvent e) {
        // do nothing
    }

    /** Used for changes to the text fields */
    class KeyListener implements java.awt.event.KeyListener
    {

        public void keyTyped(KeyEvent e)
        {
            if (String.valueOf(e.getKeyChar()).equals("\b"))
            {
                if (txtInviteMessage.getBackground() == ErrorColour)
                {
                    txtInviteMessage.setBackground(Color.WHITE);
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

    public void Change()
    {
        if (!txtNickname.getText().isEmpty() || btnMore.isVisible() && !txtLoginName.getText().isEmpty())
        {
            btnAccept.setFocusable(true);
            btnAccept.setEnabled(true);
        }
        else
        {
            btnAccept.setEnabled(false);
            btnAccept.setFocusable(false);
        }
    }
}
