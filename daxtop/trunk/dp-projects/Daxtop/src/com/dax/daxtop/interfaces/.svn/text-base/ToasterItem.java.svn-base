package com.dax.daxtop.interfaces;

import java.awt.Image;

/**
 * @author Dax Frost
 *
 * An item to be placed in the dax toaster queue
 */
public interface ToasterItem
{
    /** This field lets the toaster manager know that you are using an image in your description */
    public boolean useImage = false;

    /** Get the network name */
    public String getNetworkName();

    /** Get the heading caption */
    public String getToasterItemCaption();

    /** Gets the logo for the network that generated the toaster item */
    public Image getToasterItemNetworkLogo();

    /** Get the toaster description in text */
    public String getToasterItemTextDescription();

    /** Get the toaster description in image */
    public Image getToasterItemImageDescription();

    /** The toaster item was clicked */
    public void toasterItemClicked();

    /** The length of time in milliseconds this toaster item is shown for */
    public long getDisplayLength();
}
