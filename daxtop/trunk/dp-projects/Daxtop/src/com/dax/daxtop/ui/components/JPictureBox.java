package com.dax.daxtop.ui.components;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

/**
 * @author Dax Booysen
 * 
 * Custom control for equivelant of a picture box in c#
 */
public class JPictureBox extends JPanel
{
    Image image;
    public boolean centered = false;

    public JPictureBox()
    {
    }
    
    public JPictureBox(Image img)
    {
        image = img;
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);

        if(image!= null)
        {
            if (centered)
            {
                g.drawImage(image, (this.getWidth() - image.getWidth(this))/2, (this.getHeight() - image.getHeight(this))/2, this);
            }
            else
            {
                g.drawImage(image, 0, 0, this);
            }
        }
    }
    
    /** Sets the image of this picture box */
    public void setImage(Image img)
    {
        image = img;
        invalidate();
    }

    /** Gets the image of this picture box */
    public Image getImage()
    {
        return image;
    }
}
