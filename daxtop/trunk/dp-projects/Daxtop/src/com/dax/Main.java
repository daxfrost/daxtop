package com.dax;

import com.dax.daxtop.interfaces.ChatPanel;
import com.dax.daxtop.interfaces.NetworkProfile;
import com.dax.daxtop.interfaces.ToasterItem;
import com.dax.daxtop.res.ResourceManager;
import com.dax.daxtop.ui.DaxToaster;
import com.dax.daxtop.ui.DaxtopLoginScreen;
import com.dax.daxtop.ui.FrmChat;
import com.dax.daxtop.ui.FrmContacts;
import com.dax.daxtop.ui.MenuScreen;
import com.dax.daxtop.ui.WaitScreen;
import com.dax.daxtop.ui.components.DaxtopPanel;
import com.dax.daxtop.ui.components.PopupList;
import com.dax.lib.jar.ClassPathHacker;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel;


/**
 * @author Dax Booysen
 * @company daxtop
 * @version 1.0.0
 */
public class Main
{
    // Screens and objects
    public static FrmContacts frmContacts;
    public static FrmChat frmChat;
    public static MenuScreen menuScreen;
    public static WaitScreen waitScreen;
    public static Main DaxtopInstance;
    public static boolean _initialContactListLoad = true;
    /** Resource Manager */
    private static ResourceManager _resourceManager = ResourceManager.Instance;
    /** Open ChatPanels */
    public static HashMap<String, ChatPanel> Chats = new HashMap<String, ChatPanel>();
    /** daxtop logged in status */
    public static boolean LoggedIn = false;
    /** NetworkManager object for addon networks */
    public static NetworkManager NetworkManagerInstance;
    /** daxtop profile */
    public static DaxtopProfile profile;
    /** daxtop version */
    public static char[] dpVersion = new char[] { '1', '3', '1', '0' };
    /** command line override arguments */
    public static HashMap<String, String> OverrideArguments;

    public Main()
    {
        Profiles.LoadProfiles();

        // Turn on skin
        SetSkin(Profiles.lastSkinUsed.isEmpty() ? new SubstanceBusinessBlueSteelLookAndFeel() : DaxtopPanel.GetSkinByName(Profiles.lastSkinUsed), false);

        //System.setProperty("sun.awt.noerasebackground","true");

        // Start all needed application frames and resources
        init(this);

        new Thread(new Runnable()
        {
            public void run()
            {
                // load network manager
                NetworkManagerInstance = new NetworkManager();
            }
        }).start();

        // show the login screen
        ShowLogin();
    }

    /**
     * main entry point
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        // load arguments
        OverrideArguments = new HashMap<String, String>();

        // load arguments
        for(String arg : args)
        {
            String[] kvp = arg.split("=");
            if (kvp.length==2)
                OverrideArguments.put(kvp[0], kvp[1]);
        }

        DaxtopInstance = new Main();
    }

    /** Updates the chat with the new chat id */
    public static void UpdateChat(String chatId, String nickname, String tooltip)
    {
        if (Chats.containsKey(chatId))
        {
            ChatPanel cp = Chats.get(chatId);
            if (frmChat != null)
            {
                frmChat.UpdateChat(chatId, nickname, tooltip);
            }
        }
    }

    /** Focuses the chat screen, returns true if focused already */
    public static synchronized boolean RequestChatFocus()
    {
        // if just started up
        if (!frmChat.isActive() && !PopupList._instance.isActive())
        {
            // if form is not visible, alert via utils
            if (!frmChat.isVisible())
            {
                frmChat.setExtendedState(JFrame.ICONIFIED);
                frmChat.setVisible(true);

                // if current window is frmContacts
                if (Main.frmContacts.isActive())
                {
                    return true;
                }

                //frmChat.toFront();

                return false;
            }
            else
            {
                // is visible but contacts or menu screen is active, dont bring to front
                if (Main.frmContacts.isActive() || Main.menuScreen.isActive())
                    return true;
                
                //frmChat.toFront();

                return false;
            }
        }
        else
        {
            // if form is not visible
            if (!frmChat.isVisible())
            {
                frmChat.setExtendedState(JFrame.ICONIFIED);
                frmChat.setVisible(true);
                //frmChat.toFront();
                return false;
            }
        }

        return true;
    }

    /** Show and update the ChatScreen for this contact */
    public static void ShowChat(final String chatId, final String nickname, final String tooltip, final ImageIcon icon, final ChatPanel cp, final boolean focusChat)
    {
        try
        {
            // this code has been removed from the below segment as its CRITICAL
            // that the chatId is added as a race condition occurs with ED thread
            if (!Chats.containsKey(chatId) || !FrmChat.isOpen(chatId))
            {
                // add a new chat
                AddChatPanel(chatId, frmChat.AddChat(chatId, nickname, tooltip, icon, cp));
            }

            SwingUtilities.invokeLater(new Runnable()
            {

                public void run()
                {
                    synchronized (Chats)
                    {
                        if (frmChat == null)
                        {
                            frmChat = new FrmChat(Main.DaxtopInstance);
                        }
                        if (!FrmChat.isOpen(chatId))
                        {
                            int selIdx = FrmChat.ChatTabs.getSelectedIndex();

                            if (focusChat)
                                FrmChat.SetTab(FrmChat.ChatTabs.getTabCount()-1, selIdx);
                        }
                        else
                        {
                            frmChat.UpdateChat(chatId, nickname, tooltip, icon, focusChat);
                        }

                        if (focusChat)
                        {
                            if (frmChat.getState() == Frame.ICONIFIED)
                            {
                                frmChat.setState(Frame.NORMAL);
                            }
                            //FrmChat.ChatTabs.setSelectedIndex(FrmChat.ChatTabs.getTabCount()-1);//.indexOfComponent((Component)cp));

                            // finally show chat
                            frmChat.setVisible(true);
                            Chats.get(chatId).GetFocus();
                            RequestChatFocus();
                        }

                        Chats.get(chatId).refreshScreen();
                    }
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /** Adds a chat to the internal storage */
    public static void AddChatPanel(String chatId, ChatPanel cPanel)
    {
        Chats.put(chatId, cPanel);
    }

    /** Fetch a chat panel by its id */
    public static ChatPanel GetChatPanel(String chatId)
    {
        return Chats.get(chatId);
    }

    /** Fetch a list of all the chat panels for a specific network */
    public static ArrayList<ChatPanel> GetNetworkChatPanels(String networkName)
    {
        ArrayList<ChatPanel> chatPanels = new ArrayList<ChatPanel>();

        for (ChatPanel panel : Chats.values())
        {
            if (panel.GetChatNetworkName().equals(networkName))
                chatPanels.add(panel);
        }

        return chatPanels;
    }

    /** Shows the chat screen */
    public static void ShowChatScreen()
    {
        try
        {
            SwingUtilities.invokeLater(new Runnable()
            {

                public void run()
                {
                    if (FrmChat.ChatTabs.getTabCount() < 1)
                        return;

                    if (frmChat == null)
                    {
                        frmChat = new FrmChat(DaxtopInstance);        // only hide window if first login
                    }

                    // change to normal to show
                    if (frmChat.getExtendedState() == JFrame.ICONIFIED)
                        frmChat.setExtendedState(JFrame.NORMAL);

                    frmChat.setVisible(true);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /** Shows the contacts screen */
    public static void ShowContactsScreen()
    {
        try
        {
            SwingUtilities.invokeLater(new Runnable()
            {

                public void run()
                {
                    if (frmContacts == null)
                    {
                        frmContacts = new FrmContacts();        // only hide window if first login
                        if (!Profiles.lastSkinUsed.isEmpty())
                            FrmContacts.daxtopPanel.SetSkinName(profile.lastUsedSkin);

                        FrmChat.setTabOverviewKind(Main.profile.tabOverview);
                        Main.AfterLogin();
                    }

                    // change to normal to show
                    if (frmContacts.getExtendedState() == JFrame.ICONIFIED)
                        frmContacts.setExtendedState(JFrame.NORMAL);

                    frmContacts.setVisible(true);

                    // if logged in
                    if(LoggedIn)
                    {
                        for(NetworkProfile np : NetworkManager.networks)
                            np.NetworkInitialized(profile);


                        if (Main.profile.defaultNetwork != null)
                        {
                            NetworkProfile np = NetworkManager.GetNetworkProfileByName(Main.profile.defaultNetwork);
                            FrmContacts.SetCurrentNetworkProfile(np);
                            
                            if (np!=null)
                            {
                                np.DefaultProfileExecuted(profile);
                            }
                        }
                    }
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /** Initializes any necessary resources */
    private void init(final Main main)
    {
        // load default daxtop resources
        _resourceManager.LoadResources();

        try
        {
            SwingUtilities.invokeAndWait(new Runnable()
            {

                public void run()
                {
                    JFrame.setDefaultLookAndFeelDecorated(false);

                    // start needed JFrames
                    waitScreen = new WaitScreen();
                    menuScreen = new MenuScreen();

                    System.setProperty("sun.awt.noerasebackground","true");
                    System.setProperty("sun.java2d.d3d","false");
                    System.setProperty("sun.java2d.noddraw", "true");
                    System.setProperty("swing.bufferPerWindow", "false");
                    System.setProperty("swing.useflipBufferStrategy", "true");

                    JFrame.setDefaultLookAndFeelDecorated(true);
                    JDialog.setDefaultLookAndFeelDecorated(true);

                    System.setProperty("sun.awt.noerasebackground","true");
                    System.setProperty("sun.java2d.d3d","false");
                    System.setProperty("sun.java2d.noddraw", "true");
                    System.setProperty("swing.bufferPerWindow", "false");
                    System.setProperty("swing.useflipBufferStrategy", "true");

                    frmChat = new FrmChat(main);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /** Reskin the whole application */
    public static void SetSkin(final SubstanceLookAndFeel landf, boolean invokeAndWait)
    {
        // turn on temporary look and feel
        try
        {
            if (invokeAndWait)
            {
                try
                {
                    //new NapkinLookAndFeel());
                    javax.swing.UIManager.setLookAndFeel(landf);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                SwingUtilities.invokeLater(new Runnable()
                {

                    public void run()
                    {
                        try
                        {
                            javax.swing.UIManager.setLookAndFeel(landf);
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        // ends here
    }

    public static void reloadUiTrees()
    {
        // turn on temporary look and feel
        try
        {
            try
            {
                if (frmContacts!=null)
                    SwingUtilities.updateComponentTreeUI(frmContacts);
                if (frmChat!=null)
                {
                    SwingUtilities.updateComponentTreeUI(frmChat);

                    for (ChatPanel cp : Chats.values())
                    {
                        SwingUtilities.updateComponentTreeUI((java.awt.Component)cp);
                    }
                }
                if (menuScreen!=null)
                    SwingUtilities.updateComponentTreeUI(menuScreen);
                if (waitScreen!=null)
                    SwingUtilities.updateComponentTreeUI(waitScreen);

                // update toaster
                if (DaxToaster.Toaster!=null)
                    SwingUtilities.updateComponentTreeUI(DaxToaster.Toaster);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /** Closes clears and removes all Chats */
    public static void ClearChats()
    {
        if (frmChat != null)
        {
            frmChat.ClearChats(Chats.values());
        }

        Chats.clear();
    }

    /** Closes clears and removes all Chats for a specific network */
    public static void ClearChats(String networkName)
    {
        Collection<ChatPanel> chatsToClose = new ArrayList<ChatPanel>();

        for (ChatPanel panel : Chats.values())
        {
            if (panel.GetChatNetworkName().equals(networkName))
            {
                chatsToClose.add(panel);
            }
        }

        for (ChatPanel panel : chatsToClose)
        {
            Chats.remove(panel.GetChatId());
        }

        if (frmChat != null)
        {
            frmChat.ClearChats(chatsToClose);
        }
    }

    /** Logout of daxtop  */
    public static void Logout()
    {
        System.out.println("logout");

        for (NetworkProfile p : NetworkManager.networks)
        {
            p.NetworkDisconnect();
        }
    }

    /** Exit of daxtop  */
    public static void Exit()
    {
        if (profile != null)
        {
            // save configurations
            if (frmContacts != null)
                profile.contactsScreen = frmContacts.getBounds();
            if (frmChat != null)
                profile.chatScreen = frmChat.getBounds();

            Profiles.SaveProfiles();
        }

        // kill daxtop process
        System.exit(0);
    }

    /** Login to daxtop, check for default profile */
    public static void ShowLogin()
    {
        // wait for the load to complete
        while (!NetworkManager.LoadComplete)
        {
            try
            {
                Thread.sleep(250);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        // Determine if there is an existing user userProfile
        // and if it should be automatically be loaded
        if ((profile = Profiles.GetAutoProfile()) != null)
        {
            // dont show login screen, just autologin
            LoggedIn = true;
            menuScreen.setVisible(false);
            ShowContactsScreen();
        }
        else if (Profiles.Size() > 0)
        {
            // show user accounts screen
            ShowLogin(Profiles.GetProfiles());
        }
        else
        {
            // no exisiting profiles, show registration (future idea)
            Collection<DaxtopProfile> daxtopProfiles = new ArrayList<DaxtopProfile>();
            ShowLogin(daxtopProfiles);
        }
    }

    /** Login to daxtop */
    private static void ShowLogin(final Collection<DaxtopProfile> daxtopProfiles)
    {
        try
        {
            if (SwingUtilities.isEventDispatchThread())
            {
                DaxtopLoginScreen dlScreen = new DaxtopLoginScreen();
                dlScreen.setProfiles(daxtopProfiles);
                menuScreen.setScreen(dlScreen);
                menuScreen.setTitle("Login - daxtop");
                menuScreen.getRootPane().setDefaultButton(dlScreen.btnLogin);
                if (!daxtopProfiles.isEmpty())
                    dlScreen.GivePasswordFocus();
                else
                    dlScreen.GiveLoginNameFocus();
                menuScreen.setVisible(true);
            }
            else
            {
                SwingUtilities.invokeAndWait(new Runnable()
                {

                    public void run()
                    {
                        DaxtopLoginScreen dlScreen = new DaxtopLoginScreen();
                        dlScreen.setProfiles(daxtopProfiles);
                        menuScreen.setScreen(dlScreen);
                        menuScreen.setTitle("Login - daxtop");
                        menuScreen.getRootPane().setDefaultButton(dlScreen.btnLogin);
                        if (!daxtopProfiles.isEmpty())
                            dlScreen.GivePasswordFocus();
                        else
                            dlScreen.GiveLoginNameFocus();
                        menuScreen.setVisible(true);
                    }
                });
            }
        }
        catch (Exception ex)
        {
            ex.getCause().printStackTrace();
        }
    }

    public static void AfterLogin()
    {
        FrmChat.ChatTabs.setTabPlacement(Main.profile.tabPlacement+1);

        if (Main.profile.checkForUpdates)
        {
            CheckForUpdates();
        }
    }

    public static void CheckForUpdates()
    {
        // check for update of daxtop
        new Thread(new Runnable() {
            public void run() {
                URL updateUrl;
                try
                {
                    updateUrl = new URL("http://www.daxtop.com/version.html");
                    char[] version = new char[4];
                    java.io.DataInputStream updateInput = new DataInputStream(updateUrl.openStream());
                    version[0] = (char) updateInput.read();
                    version[1] = (char) updateInput.read();
                    version[2] = (char) updateInput.read();
                    version[3] = (char) updateInput.read();
                    updateInput.close();

                    // checking
                    if(dpVersion[0] < version[0])
                    {
                        // new version
                        ShowUpdateAvailable(version);
                    }
                    else if (dpVersion[0] == version[0])
                    {
                        if(dpVersion[1] < version[1])
                        {
                            // new version
                            ShowUpdateAvailable(version);
                        }
                        else if (dpVersion[1] == version[1])
                        {
                            if(dpVersion[2] < version[2])
                            {
                                // new version
                                ShowUpdateAvailable(version);
                            }
                            else if (dpVersion[2] == version[2])
                            {
                                if(dpVersion[3] < version[3])
                                {
                                    // new version
                                    ShowUpdateAvailable(version);
                                }
                            }
                        }
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    /** Display update */
    private static void ShowUpdateAvailable(final char[] version)
    {
        SwingUtilities.invokeLater(new Runnable()
        {

            public void run() {
                DaxToaster.AddToasterItem(new ToasterItem() {
                    public String getNetworkName() {
                        return "daxtop";
                    }

                    public String getToasterItemCaption() {
                        return "New daxtop " + version[0] + "." + version[1] + "." + version[2] + "." + version[3] + "!";
                    }

                    public Image getToasterItemNetworkLogo() {
                        return ResourceManager.IMG_DAXTOP_ICON;
                    }

                    public String getToasterItemTextDescription() {
                        return "A new daxtop is available at daxtop.com! Click here to upgrade!";
                    }

                    public Image getToasterItemImageDescription() {
                        return ResourceManager.IMG_DAXTOP_ICON;
                    }

                    public void toasterItemClicked() {
                        try
                        {
                            Desktop.getDesktop().browse(new java.net.URI("http://www.daxtop.com/"));
                        }
                        catch (Exception e) { }
                    }

                    public long getDisplayLength()
                    {
                        return 10000;
                    }
                });
            }
        });
    }

    // jna
//
//    public static void setCurrentProcessExplicitAppUserModelID(final String appID)
//    {
//        if (SetCurrentProcessExplicitAppUserModelID(new WString(appID)).longValue() != 0)
//          throw new RuntimeException("unable to set current process explicit AppUserModelID to: " + appID);
//    }

    // DO NOT DO THIS, IT'S JUST FOR TESTING PURPOSE AS I'M NOT FREEING THE MEMORY
  // AS REQUESTED BY THE DOCUMENTATION:
  //
  // http://msdn.microsoft.com/en-us/library/dd378419%28VS.85%29.aspx
  //
  // "The caller is responsible for freeing this string with CoTaskMemFree when
  // it is no longer needed"
//  public static String getCurrentProcessExplicitAppUserModelID()
//  {
//    final PointerByReference r = new PointerByReference();
//
//    if (GetCurrentProcessExplicitAppUserModelID(r).longValue() == 0)
//    {
//      final Pointer p = r.getValue();
//
//
//      return p.getString(0, true); // here we leak native memory by lazyness
//    }
//    return "N/A";
//  }
//
//
//    private static native NativeLong GetCurrentProcessExplicitAppUserModelID(PointerByReference appID);
//    private static native NativeLong SetCurrentProcessExplicitAppUserModelID(WString appID);
//
//
//    static
//    {
//        Native.register("shell32");
//    }
//    // end

    /** Add Network Profile */
    public static NetworkProfile SpawnNetworkProfile(String networkName) throws Exception
    {
        NetworkProfile np = NetworkManagerInstance.SpawnNetwork(networkName);

        // add to top bar
        frmContacts.AddNetworkProfile(np);

        return np;
    }

    /** Network Manager for addon networks control */
    public static class NetworkManager
    {

        /** Loaded networks */
        public static ArrayList<NetworkProfile> networks = new ArrayList<NetworkProfile>();

        /** The network name and associated class name */
        public static HashMap<String, String> networkNameClassName = new HashMap<String, String>();

        /** Loaded status of networks */
        public static boolean LoadComplete = false;

        /** NetworkManager */
        public NetworkManager()
        {
            LoadNetworks();
        }

        public static NetworkProfile GetNetworkProfileByName(String name)
        {
            for (NetworkProfile p : networks)
            {
                if (p.GetNetworkName().equals(name))
                {
                    return p;
                }
            }

            return null;
        }

        public static void RemoveNetwork(NetworkProfile np)
        {
            // only allow duplications to be deleted
            if (!np.GetNetworkName().equals(np.GetNetworkId()))
            {
                networks.remove(np);
                frmContacts.RemoveNetworkProfile(np);
            }
        }

        /** Gets the network profiles for the specific network id */
        public static ArrayList<NetworkProfile> GetNetworkProfilesByNetworkId(String networkId)
        {
            ArrayList<NetworkProfile> profiles = new ArrayList<NetworkProfile>();

            for (NetworkProfile p : networks)
            {
                if (p.GetNetworkId().equals(networkId))
                {
                    profiles.add(p);
                }
            }

            return profiles;
        }

        private void LoadNetworks()
        {
            try
            {
                // load the networks config file
                FileReader fr = new FileReader("./networks/networks.dat");
                BufferedReader br = new BufferedReader(fr);

                String network = "";

                while ((network = br.readLine()) != null && !network.isEmpty())
                {
                    // Name|Filename|ClassName
                    String[] networkParts = network.split("\\|");

                    // load a network
                    networks.add(LoadNetwork("./networks/" + networkParts[1], networkParts[2], networkParts[0]));
                }

                br.close();
            }
            catch (FileNotFoundException ex)
            {
                ex.printStackTrace();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                LoadComplete = true;
            }
        }

        // Loads a configured network
        private NetworkProfile LoadNetwork(String jarFile, String className, String networkName) throws Exception
        {
            ClassPathHacker.addFile(jarFile);
            NetworkProfile instance = (NetworkProfile) Class.forName(className).newInstance();
            instance.SetNetworkId(networkName);
            instance.SetNetworkName(networkName);

            // add to storage
            networkNameClassName.put(networkName, className);

            // finally return instance
            return instance;
        }

        int id = 1;

        /** Spawn a new network instance */
        private NetworkProfile SpawnNetwork(String networkName) throws Exception
        {
            NetworkProfile instance = (NetworkProfile) Class.forName(networkNameClassName.get(networkName)).newInstance();
            instance.SetNetworkName(networkName + id++);

            // add to the current networks
            networks.add(instance);

            // finally return instance
            return instance;
        }
    }
}
