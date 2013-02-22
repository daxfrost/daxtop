package com.dax.daxtop.utils;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileFilter;
import java.util.Map.Entry;
import javax.swing.JFrame;

/**
 * @author Dax Booysen
 * 
 * A controller class with various useful utilities
 */
public final class Utils
{
    /** Tests whether or not a flag is set */
    public static boolean isSet(int flag, int inFlags)
    {
        return (flag & inFlags) != 0;
    }

    /** Adds snapping to the JFrame */
    public static void AddWindowSnapping(JFrame frame)
    {
        frame.addComponentListener(new WindowSnapper());
    }

    static class WindowSnapper extends ComponentAdapter
    {

        private boolean locked = false;
        private int sd = 10;
        private long lastEvent = 1000;

        @Override
        public void componentMoved(ComponentEvent evt)
        {
            if (System.currentTimeMillis() - 1000 > lastEvent)
            {
                lastEvent = System.currentTimeMillis();
            }
            else
            {
                return;
            }

            if (locked)
            {
                return;
            }
            Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
            int nx = evt.getComponent().getX();
            int ny = evt.getComponent().getY();

            // top
            if (ny < 0 + sd)
            {
                ny = 0;
            }
            // left
            if (nx < 0 + sd)
            {
                nx = 0;
            }
            // right
            if (nx > size.getWidth() - evt.getComponent().getWidth() - sd)
            {
                nx = (int) size.getWidth() - evt.getComponent().getWidth();
            }
            // bottom
            if (ny > size.getHeight() - evt.getComponent().getHeight() - sd)
            {
                ny = (int) size.getHeight() - evt.getComponent().getHeight();
            }

            // make sure we don't get into a recursive loop when the
            // set location generates more events
            evt.getComponent().setEnabled(false);
            locked = true;
            evt.getComponent().setLocation(nx, ny);
            locked = false;
            evt.getComponent().setEnabled(true);
            evt.setSource(null);
        }
    }

    /** Centers the JFrame */
    public static void setWindowCentered(Window frame)
    {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        frame.setLocation((screenWidth / 2)-(frame.getWidth()/2), (screenHeight / 2)-(frame.getHeight()/2));
    }

    /** Returns the key using a value out of this collection */
    public static String getKeyFromValue(java.util.HashMap<String, String> m, String value)
    {
        for(Entry e : m.entrySet())
        {
            if(e.getValue().equals(value))
                return (String)e.getKey();
        }

        return null;
    }

    /** Returns the key using a value out of this collection */
    public static Object getKeyFromValue(java.util.HashMap<Object, Object> m, Object value)
    {
        for(Entry e : m.entrySet())
        {
            if(e.getValue().equals(value))
                return e.getKey();
        }

        return null;
    }

    /** Escapes the text's html body parts - guidance on this can be found at http://www.javapractices.com/topic/TopicAction.do?Id=96 */
    public static String escapeHtml(String htmlText)
    {
        htmlText = htmlText.replaceAll("&", "&amp;");
        htmlText = htmlText.replaceAll("<","&lt;");
        htmlText = htmlText.replaceAll(">","&gt;");
        htmlText = htmlText.replaceAll("/","&#047;");

        return htmlText;
    }
}
