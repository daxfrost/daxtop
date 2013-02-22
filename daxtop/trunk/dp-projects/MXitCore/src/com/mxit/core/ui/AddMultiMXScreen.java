package com.mxit.core.ui;

import com.dax.Main;
import com.dax.daxtop.ui.handlers.FocusHandler;
import com.mxit.MXitNetwork;
import com.mxit.core.model.MXitContact;
import com.mxit.core.model.MXitContactList;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

/**
 * @author Dax Booysen
 * 
 * A screen used by the user to add a new MultiMX
 */
public class AddMultiMXScreen extends JPanel implements ActionListener, FocusListener
{
    public JPanel pnlFrame, pnlButtonFrame, splitPanel;
    public JLabel lblMultiMxName, lblContacts,  lblGroup;
    public JTextField txtMultiMXName;
    public JList lstContacts;
    public JComboBox cbGroup;
    public JButton btnAccept, btnCancel, btnMore;              // constant values

    /** The MXit Network */
    private MXitNetwork mxitNetwork = null;

    private ArrayList<MXitContact> contacts = null;

    /** Constructs a new AddMultiMXScreen */
    public AddMultiMXScreen(MXitNetwork network)
    {
        // set the network
        mxitNetwork = network;

        initComponents(this);
    }

    /** Sets up the components for this JPanel */
    private void initComponents(Container c)
    {
        // init components
        lblMultiMxName = new JLabel("MultiMX Name:");
        lblContacts = new JLabel("Invite Contacts:");
        lblGroup = new JLabel("Add To Group:");
        txtMultiMXName = new JTextField();
        cbGroup = new JComboBox();
        btnAccept = new JButton("Create...");
        btnCancel = new JButton("Cancel");
        btnMore = new JButton("More...");
        pnlFrame = new JPanel();
        pnlButtonFrame = new JPanel();
        splitPanel = new JPanel();
        // add contacts
        contacts = mxitNetwork.currentContactList.GetContacts(MXitContactList.MXit, true, true);
        DefaultListModel listModel = new DefaultListModel();
        for (MXitContact contact : contacts)
        {
            listModel.addElement(new CheckableItem(contact.Nickname));
        }
        JScrollPane scrollPane = new JScrollPane(lstContacts = new JList(listModel), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        lstContacts.setCellRenderer(new CheckListRenderer());
        lstContacts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstContacts.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e)
        {
            int index = lstContacts.locationToIndex(e.getPoint());
            CheckableItem item = (CheckableItem) lstContacts.getModel().getElementAt(index);
            item.setSelected(!item.isSelected());
            Rectangle rect = lstContacts.getCellBounds(index, index);
            lstContacts.repaint(rect);
        }});

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
        pnlFrame.add(lblMultiMxName);
        pnlFrame.add(lblContacts);
        pnlFrame.add(lblGroup);
        pnlFrame.add(txtMultiMXName);
        pnlFrame.add(scrollPane);
        pnlFrame.add(cbGroup);
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
        lblMultiMxName.setBounds(10, 15, 150, 20);
        lblContacts.setBounds(10, 70, 150, 20);
        lblGroup.setBounds(10, 200, 150, 20);
        txtMultiMXName.setBounds(10, 40, 240, 25);
        scrollPane.setBounds(10, 95, 240, 100);
        cbGroup.setBounds(10, 220, 240, 20);
        btnAccept.setBounds(10, 10, 80, 25);
        btnCancel.setBounds(95, 10, 65, 25);
        btnMore.setBounds(185, 10, 65, 25);
        pnlFrame.setPreferredSize(new Dimension(100, 70));
        pnlButtonFrame.setPreferredSize(new Dimension(100, 25));

        // tab index order
        Vector<Component> tabOrder = new Vector<Component>();
        tabOrder.add(txtMultiMXName);
        tabOrder.add(lstContacts);
        tabOrder.add(cbGroup);
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
        lstContacts.addFocusListener(this);
        KeyListener l = new KeyListener();
        txtMultiMXName.addKeyListener(l);

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
            ArrayList<String> invitesToSend = new ArrayList<String>();

            for (Object item : lstContacts.getSelectedValues())
            {
                CheckableItem cItem = (CheckableItem)item;

                for (MXitContact contact : contacts)
                {
                    if (cItem.toString().equals(contact.Nickname))
                    {
                        invitesToSend.add(contact.MXitID);
                        break;
                    }
                }
            }

            String group = cbGroup.getSelectedIndex() != 0 ? cbGroup.getSelectedItem().toString() : "";

            if (group.equals("Add New Group..."))
            {
                group = JOptionPane.showInputDialog(Main.menuScreen, "Enter new group name: ", "Group Name...");
                group = group == null ? "" : group;
            }

            Main.waitScreen.Start();
            Main.menuScreen.setScreen(Main.waitScreen);
            Main.waitScreen.setVisible(true);

            // add the multimx
            mxitNetwork.userSession.AddMultiMX(txtMultiMXName.getText(), mxitNetwork.AddMultiMx(txtMultiMXName.getText(), invitesToSend, group));

            Main.waitScreen.Stop();
            Main.waitScreen.setVisible(false);

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
        if (!txtMultiMXName.getText().isEmpty())
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

class CheckListRenderer extends JCheckBox implements ListCellRenderer
{
    public CheckListRenderer()
    {
      setBackground(UIManager.getColor("List.textBackground"));
      setForeground(UIManager.getColor("List.textForeground"));
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus)
    {
      setEnabled(list.isEnabled());
      setSelected(((CheckableItem) value).isSelected());
      setFont(list.getFont());
      setText(value.toString());
      return this;
    }
}

class CheckableItem {
    private String str;

    private boolean isSelected;

    public CheckableItem(String str) {
      this.str = str;
      isSelected = false;
    }

    public void setSelected(boolean b) {
      isSelected = b;
    }

    public boolean isSelected() {
      return isSelected;
    }

    public String toString() {
      return str;
    }
  }