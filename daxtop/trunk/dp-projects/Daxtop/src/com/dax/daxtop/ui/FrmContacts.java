package com.dax.daxtop.ui;

import com.dax.Main;
import com.dax.Profiles;
import com.dax.daxtop.interfaces.NetworkProfile;
import com.dax.daxtop.res.ResourceManager;
import com.dax.daxtop.ui.components.DScrollDockMenu;
import com.dax.daxtop.ui.components.DaxtopPanel;
import com.dax.daxtop.ui.components.interfaces.DScrollDockMenuListener;
import com.dax.daxtop.utils.Utils;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

/**
 * @author Dax Booysen
 *
 * This is the main frame that holds the contacts in a list to be viewed.
 */
public class FrmContacts extends JFrame implements ActionListener, DScrollDockMenuListener, MouseListener, Runnable {

    /** Contact List as a DaxList */
    static JPanel ContactListControl;
    /** JPanel holding selected Network Contacts Screen */
    static JPanel mainPane;
    /** Menu containing main sections */
    public static DScrollDockMenu MainMenuOptions;
    /** daxtop panel */
    public static DaxtopPanel daxtopPanel;
    /** Dax Tray Icon */
    static TrayIcon trayIcon;    // tray menus
    static PopupMenu popupMenu;
    static MenuItem mnuExit;

    /** Constructor */
    public FrmContacts() {
        super("daxtop");

        // initialize the components
        _init(this.getContentPane());

        // center window
        if (Main.profile.contactsScreen == null)
            Utils.setWindowCentered(this);
        else
            this.setBounds(Main.profile.contactsScreen);

        if (trayIcon == null)
        {
            // add the tray icon
            buildTray();
        }

        //Utils.AddWindowSnapping(this);

        // display daxtop panel
        mainPane.removeAll();
        mainPane.add(daxtopPanel, BorderLayout.CENTER);

        mainPane.revalidate();
        mainPane.repaint();
    }

    /** Builds up the tray icon and menus */
    private void buildTray()
    {
        try
        {
            // new instance or update menu
            if (popupMenu == null)
            {
                popupMenu = new PopupMenu("daxtop");
            }
            else
            {
                popupMenu.removeAll();
            }
            mnuExit = new MenuItem("Exit");
            for(NetworkProfile p : Main.NetworkManagerInstance.networks)
                popupMenu.add(p.GetTrayMenu());
            popupMenu.add("-");
            popupMenu.add(mnuExit);
            SystemTray tray = SystemTray.getSystemTray();
            // only add tray icon if not there
            if (tray.getTrayIcons().length < 1)
            {
                trayIcon = new TrayIcon(ResourceManager.IMG_DAXTOP_ICON_16, "daxtop", popupMenu);
                tray.add(trayIcon);

                // add the listener for the tray icon click
                trayIcon.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        Main.frmContacts.setVisible(true);

                        Main.ShowChatScreen();
                    }
                });
            }
            
            // add action listeners
            mnuExit.addActionListener(this);
            
        } catch (AWTException ex) {
            ex.printStackTrace();
        }
    }

    /** Initialize this frame and its components */
    private void _init(Container c) {
        // init frame
        setSize(300, 400);
        setMinimumSize(new Dimension(240, 400));
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLayout(new BorderLayout());
        setIconImage(ResourceManager.IMG_DAXTOP_ICON);
        GridBagConstraints gbc = new GridBagConstraints();

        daxtopPanel = new DaxtopPanel();

        // add menu at the top
        MainMenuOptions = new DScrollDockMenu(this);
        MainMenuOptions.setMaximumSize(new Dimension(48,48));
        //MainMenuOptions.setPreferredSize(new Dimension(getWidth(), 50));
        MainMenuOptions.AddItem("daxtop", new ImageIcon(ResourceManager.IMG_MAIN_MENU_DAXTOP), null, "daxtop");

        // load networks
        for (NetworkProfile networkProfile : Main.NetworkManager.networks) {
            MainMenuOptions.AddItem(networkProfile.GetNetworkName(), networkProfile.GetNetworkLogo(), null, networkProfile.GetNetworkCaption());
        }

        c.add(MainMenuOptions, BorderLayout.NORTH);

        // add menu options listener
        MainMenuOptions.addDScrollDockMenuListener(this);

        // adds the window listener
        this.addMouseListener(this);

        // setup contacts screen components
        mainPane = new JPanel();
        mainPane.setLayout(new GridLayout(1, 1));
        mainPane.setOpaque(false);
        c.add(mainPane, BorderLayout.CENTER);
    }

    /** Adds a specific network profile to the list */
    public void AddNetworkProfile(NetworkProfile np)
    {
        MainMenuOptions.AddItem(np.GetNetworkName(), np.GetNetworkLogo(), null, np.GetNetworkCaption());

        buildTray();
    }

    /** Removes a specific network profile */
    public void RemoveNetworkProfile(NetworkProfile np)
    {
        MainMenuOptions.RemoveItem(np.GetNetworkName());
        MainMenuOptions.revalidate();

        buildTray();
    }

    /** Actions performed */
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        // user clicked tray exit "Log out" menu item
        if (src == mnuExit) {
            Main.Logout();
            Main.Exit();
        }/* else if (src == mnuPresenceOnline) {
            if (!Main.LoggedIn) {
                Main.ShowLogin(Main.profile);
            } else {
                mnuPresenceOnline.setEnabled(false);
                mnuPresenceDND.setEnabled(true);
                mnuPresenceAway.setEnabled(true);
                mnuPresenceOffline.setEnabled(true);
            }

        } else if (src == mnuPresenceAway) {
            if (Main.LoggedIn) {
                mnuPresenceOnline.setEnabled(true);
                mnuPresenceDND.setEnabled(true);
                mnuPresenceAway.setEnabled(false);
                mnuPresenceOffline.setEnabled(true);
            }

        } else if (src == mnuPresenceDND) {
            if (Main.LoggedIn) {
                mnuPresenceOnline.setEnabled(true);
                mnuPresenceDND.setEnabled(false);
                mnuPresenceAway.setEnabled(true);
                mnuPresenceOffline.setEnabled(true);
            }

        } else if (src == mnuPresenceOffline) {
            if (Main.LoggedIn) {
                // strangely without this line an error is caused from the jdic native library trying to process Main.Logout() [java.lang.NoSuchMethodException]
                System.out.println("Logging out of MXit...");

                // logout
                Main.Logout();

                Main.ClearChats();
                //this.ContactListControl.ClearItems();
                //Main.currentContactList.Clear();

                mnuPresenceOffline.setEnabled(false);
                mnuPresenceOnline.setEnabled(true);
                mnuPresenceDND.setEnabled(true);
                mnuPresenceAway.setEnabled(true);

                // refresh
                ContactListControl.revalidate();
            }

        }*/
    }

    /** Sets the current network profile */
    public static void SetCurrentNetworkProfile(NetworkProfile p)
    {
        // add to main panel
        mainPane.removeAll();

        if (p != null && p.GetContactScreenPanel() != null)
        {
            Main.frmContacts.setTitle("daxtop - " + p.GetNetworkCaption());
            JComponent jc = p.GetContactScreenPanel();

            mainPane.add(jc, BorderLayout.CENTER);
        }
        else
        {
            // display daxtop panel
            mainPane.removeAll();
            mainPane.add(daxtopPanel, BorderLayout.CENTER);
            Main.frmContacts.setTitle("daxtop");
        }
        
        mainPane.revalidate();
        mainPane.repaint();
    }

    /** Gets the current network contacts screen, returns null if no network screen is available */
    public static Object GetCurrentNetworkContactsScreen()
    {
        if (mainPane.getComponentCount() != 0)
           return mainPane.getComponent(0);

        return null;
    }

    public void ItemClicked(JButton c, boolean secondClick) {
        NetworkProfile p = Main.NetworkManager.GetNetworkProfileByName(c.getName());

        /** NetworkProfile exists */
        if (p != null) {
            // execute
            p.NetworkExecuted(Main.profile);

            // display the network on the contacts screen
            SetCurrentNetworkProfile(p);
        }
        else if (c.getName().equals("daxtop"))
        {
            // display daxtop panel
            mainPane.removeAll();
            mainPane.add(daxtopPanel, BorderLayout.CENTER);
            Main.frmContacts.setTitle("daxtop");

            mainPane.revalidate();
            mainPane.repaint();
        }
    }

    public void ItemRightClicked(JButton c)
    {
        if (c.getName().equals("daxtop"))
        {
            Point pos = c.getMousePosition();
            JPopupMenu menu = GetDaxtopMenu();
            if (c != null && menu != null)
                menu.show(c, pos.x, pos.y);
        }
        else
        {
            NetworkProfile p = Main.NetworkManager.GetNetworkProfileByName(c.getName());
            Point pos = c.getMousePosition();
            JPopupMenu menu = p.GetMenu();
            if (p!=null && c != null && menu != null)
                menu.show(c, pos.x, pos.y);
        }
    }

    public void mouseClicked(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e)
    {        
    }

    public void mouseEntered(MouseEvent e)
    {
        // prevention of cursor getting stuck
        if (this.getCursor().getType() != Cursor.getDefaultCursor().getType())
            this.setCursor(Cursor.getDefaultCursor());
    }

    public void mouseExited(MouseEvent e) {

    }

    /** Chat tab id requests attention */
    public void RequestAttention(String chatId)
    {
        if(!AttentionList.contains(chatId))
            AttentionList.add(chatId);

        if (!ThreadRunning)
        {
            ThreadRunning = true;
            new Thread(this).start();
        }
    }

    /** Chat tab id received attention */
    public void ReceivedAttention(String chatId)
    {
        AttentionList.remove(chatId);
    }

    public static ArrayList<String> AttentionList = new ArrayList<String>();
    public static boolean ThreadRunning = false;

    /** attention thread */
    public void run()
    {
        trayIcon.setToolTip("daxtop - You have new messages!");

        while (ThreadRunning)
        {
            if (AttentionList.isEmpty())
            {
                ThreadRunning = false;
                break;
            }

            try
            {
                Image img = this.getIconImage() != ResourceManager.IMG_DAXTOP_ICON ? ResourceManager.IMG_DAXTOP_ICON : ResourceManager.IMG_NOTIFICATION_NORMAL;
                Main.frmChat.setIconImage(img);
                this.setIconImage(img);
                trayIcon.setImage(trayIcon.getImage() != ResourceManager.IMG_DAXTOP_ICON_16 ? ResourceManager.IMG_DAXTOP_ICON_16 : ResourceManager.IMG_NOTIFICATION_SMALL);
                Thread.sleep(800);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        Main.frmChat.setIconImage(ResourceManager.IMG_DAXTOP_ICON);
        this.setIconImage(ResourceManager.IMG_DAXTOP_ICON);
        trayIcon.setImage(ResourceManager.IMG_DAXTOP_ICON_16);
        trayIcon.setToolTip("daxtop");
    }

    /** Get the daxtop menu */
    private JPopupMenu GetDaxtopMenu()
    {
        JPopupMenu menu = new JPopupMenu("daxtop");
        JMenuItem mnuExitDaxtop = new JMenuItem("Exit");
        menu.add(mnuExitDaxtop);

        mnuExitDaxtop.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                Main.Logout();
                Main.Exit();
            }
        });

        return menu;
    }
}
