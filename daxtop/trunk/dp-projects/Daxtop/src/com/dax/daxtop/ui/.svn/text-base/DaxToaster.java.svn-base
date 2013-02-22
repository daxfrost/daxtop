package com.dax.daxtop.ui;

import com.dax.daxtop.interfaces.ToasterItem;
import com.dax.daxtop.ui.components.JPictureBox;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

/**
 * @author Dax Booysen
 * @company MXit Lifestyle
 * 
 * Toaster
 */
public final class DaxToaster extends JWindow implements Runnable, MouseListener
{
    /** The dax toaster */
    public static DaxToaster Toaster = new DaxToaster();

    private static ToasterItem item = null;

    /** The list of toaster items */
    private static List<ToasterItem> toasterItems;

    // components
    private static JPanel holder;
    private static JLabel lblCaption, lblDescription;
    private static JPictureBox pbNetwork, pbImage;

    /** If this is set to true the current toasteritems are killed and toaster is removed */
    private static boolean kill = false;

    /** Variable to detect hovering */
    private static boolean mouseOver = false;

    private static Thread toasterThread = null;

    /** Hidden Constructor - use singleton instance */
    private DaxToaster()
    {
        setLayout(new BorderLayout());
        setSize(240, 110);
        setAlwaysOnTop(true);

        // construct the toaster items
        toasterItems = Collections.synchronizedList(new LinkedList<ToasterItem>());

        // add listeners
        this.addMouseListener(this);

        // initialize components
        holder = new JPanel();
        JPanel networkImagePanel = new JPanel();
        holder.setLayout(null);
        networkImagePanel.setLayout(new BorderLayout());
        lblCaption = new JLabel("");
        lblDescription = new JLabel("");
        pbNetwork = new JPictureBox();
        pbImage = new JPictureBox();
        lblCaption.setHorizontalTextPosition(JLabel.LEFT);
        lblDescription.setHorizontalTextPosition(JLabel.CENTER);
        this.getRootPane().setBorder(BorderFactory.createRaisedBevelBorder());
        holder.setBorder(BorderFactory.createTitledBorder(""));
        networkImagePanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        networkImagePanel.add(pbNetwork, BorderLayout.CENTER);
        pbNetwork.centered = true;
        // setup ui
        add(holder, BorderLayout.CENTER);
        holder.add(networkImagePanel);
        holder.add(lblCaption);
        holder.add(pbImage);
        holder.add(lblDescription);

        networkImagePanel.setBounds(10, 18, 32, 32);
        lblCaption.setBounds(45, 25, 150, 20);
        pbImage.setBounds(170, 35, 60, 60);
        lblDescription.setBounds(15, 42, 210, 60);

        getRootPane().setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblDescription.setFont(lblDescription.getFont().deriveFont(11.0f));
        lblDescription.setMaximumSize(new Dimension(210, 60));
    }

    // the thread
    public void run()
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        setLocation(screenSize.width - this.getWidth() - 5, screenSize.height - this.getHeight() - insets.bottom - 5);

        try
        {
            SwingUtilities.invokeAndWait(new Runnable()
            {
                public void run()
                {
                    setVisible(true);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        // loop through each item
        while (!toasterItems.isEmpty() && !kill)
        {
            try
            {
                item = toasterItems.remove(0);

                ((TitledBorder)holder.getBorder()).setTitle(item.getNetworkName());
                String caption = item.getToasterItemCaption();
                caption = caption.length() >= 23 ? caption.substring(0,22) + "..." : caption ;
                lblCaption.setText("<html><b>" + caption + "</b></html>");
                pbNetwork.setImage(item.getToasterItemNetworkLogo());
                if (item.useImage)
                {
                    pbImage.setImage(item.getToasterItemImageDescription());
                    lblDescription.setVisible(false);
                    pbImage.setVisible(true);
                    pbImage.repaint();
                }
                else
                {
                    pbImage.setImage(null);
                    pbImage.setVisible(false);
                    lblDescription.setVisible(true);
                    String text = item.getToasterItemTextDescription();
                    text = text.length() > 100 ? text.substring(0,97) + "..." : text ;
                    lblDescription.setText("<html><p align=\"center\">" + text + "</p></html>");
                }

                pbNetwork.repaint();

                Thread.sleep(item.getDisplayLength());
                
                while(mouseOver)
                {
                    Thread.sleep(2500);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        kill = false;
        item = null;
        setVisible(false);
    }

    /** Starts or adds to the toaster with the item */
    public static void AddToasterItem(ToasterItem item)
    {
        toasterItems.add(item);

        if (toasterThread == null || !toasterThread.isAlive())
        {
            toasterThread = new Thread(Toaster);
            toasterThread.start();
        }
    }

    /** Stops the toaster */
    public static void KillToaster()
    {
        toasterItems.clear();
        kill = true;
    }

    public void mouseClicked(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e)
    {
        item.toasterItemClicked();
    }

    public void mouseEntered(MouseEvent e)
    {
        mouseOver = true;
    }

    public void mouseExited(MouseEvent e)
    {
        mouseOver = false;
    }
}
// use network name in border title and use etched border outside and titled border inside