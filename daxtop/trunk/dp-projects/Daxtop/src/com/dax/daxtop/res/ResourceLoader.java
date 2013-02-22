package com.dax.daxtop.res;

import com.dax.daxtop.interfaces.NetworkProfile;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 * @author Dax Booysen
 *
 * ResourceLoader to load resources
 */
public abstract class ResourceLoader extends Frame
{
    /** Load image from file, use wait for secure load completion */
    public Image LoadImage(String rootDirectory, String fileName)
    {
        try
        {
            String path = (rootDirectory.isEmpty() ? "images/" : rootDirectory) + fileName;

            // load resource
            URL myurl = this.getClass().getResource(path);

            // load normally
            Toolkit tk = this.getToolkit();

            return new ImageIcon(tk.getImage(myurl)).getImage();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /** Load audio from file, use wait for secure load completion */
    public URL LoadFile(Class<?> Class, String rootDirectory, String fileName)
    {
        try
        {
            String path = (rootDirectory.isEmpty() ? "audio/" : rootDirectory) + fileName;

            URL myurl = Class.getResource(path);

            return myurl;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /** Use this to load data files you wish to store, returns null if file does not exist */
    public static FileReader OpenLocalDataFileReader(NetworkProfile profile, String filename)
    {
        FileReader fr = null;
        
        try
        {
            fr = new FileReader("./networks/" + profile.GetNetworkId() + "_" + filename);
        }
        catch(FileNotFoundException fnfe)
        {
            return null;
        }

        return fr;
    }
}
