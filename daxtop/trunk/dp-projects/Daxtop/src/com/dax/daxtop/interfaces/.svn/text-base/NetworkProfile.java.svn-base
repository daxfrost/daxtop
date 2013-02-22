package com.dax.daxtop.interfaces;

import com.dax.DaxtopProfile;
import com.dax.Main;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

/**
 * @author Dax Frost
 */
public interface NetworkProfile
{
    /** The class name of the network profile */
    String className = "";

    /** Gets the name of the network */
    public abstract String GetNetworkName();

    /** Gets the network profiles current caption */
    public abstract String GetNetworkCaption();

    /** Sets the name of the network */
    public abstract void SetNetworkName(String networkName);

    /** Sets the identifier for this type of network */
    public abstract void SetNetworkId(String networkId);

    /** Gets the identifier for this type of network */
    public abstract String GetNetworkId();

    /** Access point to the daxtop client */
    final static Main.NetworkManager DaxtopNetworkManager = Main.NetworkManagerInstance;

    /** Returns the panel used in the contact screen */
    public abstract JComponent GetContactScreenPanel();

    /** Returns the main menu for the network */
    public abstract JPopupMenu GetMenu();

    /** Returns the emblem Image used in the network selection menu slider */
    public abstract ImageIcon GetNetworkLogo();

    /** Disconnecting from Networks */
    public abstract void NetworkDisconnect();

    /** The network was clicked on the contact screen */
    public abstract void NetworkExecuted(DaxtopProfile dp);

    /** The network was executed on startup of daxtop */
    public abstract void DefaultProfileExecuted(DaxtopProfile dp);

    /** The network was loaded and ready to execute */
    public abstract void NetworkInitialized(DaxtopProfile dp);

    /** The daxtop skin has changed and any custom control changes should now be made */
    public abstract void DaxtopSkinChanged();

    /** Returns the tray menu for this network */
    public abstract java.awt.MenuItem GetTrayMenu();
}
