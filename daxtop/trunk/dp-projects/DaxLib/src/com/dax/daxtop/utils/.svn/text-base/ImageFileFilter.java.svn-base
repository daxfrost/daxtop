package com.dax.daxtop.utils;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * A class that implements the Java FileFilter interface.
 */
public class ImageFileFilter extends FileFilter
{
    private final String[] okFileExtensions = new String[] {"jpg", "png", "gif"};

    @Override
    public boolean accept(File file)
    {
        if (file.isDirectory())
            return true;

        for (String extension : okFileExtensions)
        {
            if (file.getName().toLowerCase().endsWith(extension))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getDescription()
    {
        return "Image Files...";
    }
}