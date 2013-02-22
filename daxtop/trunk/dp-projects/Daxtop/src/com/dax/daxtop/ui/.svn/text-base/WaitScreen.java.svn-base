package com.dax.daxtop.ui;

import com.dax.daxtop.res.ResourceManager;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

/**
 * @author Dax Booysen
 * 
 * This screen is displayed while the user waits for a request to return from the backend.
 */
public final class WaitScreen extends JPanel implements Runnable
{
    private Image offScrImg;
    private Graphics offScrGfx;
    private boolean running = false;
    private Thread runner;
    
    // drawing values
    boolean goingRight;
    Image[] images;
    Image backGround;
    int idx = 0;

    /** A cancel button that may or may not be visible */
    private static JButton btnCancel = new JButton("Cancel");

    // dynamic text changed externally
    public static String DynamicText = "";
    
    /** Constructor */
    public WaitScreen()
    {
        setLayout(null);
        setOpaque(true);
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        
        // load images
        images = ResourceManager.loader_images;

        // add btncancel
        add(btnCancel);

        // set location and size
        btnCancel.setBounds(100,280, 75, 25);
    }

    @Override
    public void paint(Graphics g)
    {
        offScrImg = this.createImage(this.getWidth(), this.getHeight());
        offScrGfx = offScrImg.getGraphics();
        
        super.paint(offScrGfx);
        
        if(idx == 10)
            idx = 0;
        
        // deal with text on screen
        if(DynamicText.length() > 0)
        {
            FontMetrics fm = offScrGfx.getFontMetrics();
            int strWidth = fm.stringWidth(DynamicText);
            offScrGfx.drawString(DynamicText, (this.getWidth() - strWidth) / 2, this.getWidth() / 2 + images[idx].getHeight(this));
        }
        
        offScrGfx.drawImage(images[idx], getWidth()/2 - (images[idx].getWidth(this)/2), getHeight()/2 - (images[idx].getHeight(this)/2), this);
        
        idx++;
        
        g.drawImage(offScrImg, 0, 0, this);
    }

    /** Start the animation */
    public void Start()
    {
        runner = new Thread(this);
        goingRight = true;
        running = true;
        runner.start();
    }
    
    /** Stop the animation */
    public void Stop()
    {
        running = false;
    }
    
    public void run()
    {
        while(running)
        {
            repaint();
            
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException ex)
            {
                // do nothing
            }
        }
    }

    public boolean isRunning()
    {
        return running;
    }

    /** Sets the current cancel listener */
    public static void setCancelListener(ActionListener al)
    {
        for (ActionListener listener : btnCancel.getActionListeners())
            btnCancel.removeActionListener(listener);

        btnCancel.addActionListener(al);
    }

    /** Sets the visibility of the cancel button */
    public static void setCancelVisible(boolean visible)
    {
        btnCancel.setVisible(visible);
    }
}
