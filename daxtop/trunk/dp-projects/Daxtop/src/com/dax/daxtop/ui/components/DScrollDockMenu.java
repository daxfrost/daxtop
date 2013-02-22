package com.dax.daxtop.ui.components;

import com.dax.daxtop.ui.components.interfaces.DScrollDockMenuListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

/**
 * @author Dax Booysen
 *
 * DScrollDockMenu is a mouse auto scrollable docking menu
 */
public class DScrollDockMenu extends JPanel implements MouseMotionListener, MouseListener, ActionListener
{

    JToolBar mainPanel = new JToolBar("daxtop");
    JScrollPane scroller;
    JFrame parentJFrame;
    scrollThread scrollT = new scrollThread();
    JButton btnExpander;
    /** Remember the height when changing size */
    private int memHeight;
    /** Dock Item Click Listeners */
    private ArrayList<DScrollDockMenuListener> _listeners = new ArrayList<DScrollDockMenuListener>();

    /** Constructs a new DScrollDockMenu */
    public DScrollDockMenu(JFrame parent)
    {
        setLayout(new BorderLayout());

        parentJFrame = parent;

        //mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

        mainPanel.setFloatable(false);
        mainPanel.addMouseListener(this);

        scroller = new JScrollPane(mainPanel);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        add(scroller, BorderLayout.CENTER);

        btnExpander = new JButton("-");
        btnExpander.setPreferredSize(new Dimension(getWidth(), 8));
        add(btnExpander, BorderLayout.SOUTH);
        btnExpander.addActionListener(this);
    }

    public void AddItem(String name, ImageIcon image, ImageIcon selectedImage, String caption)
    {
        JButton b = new JButton(image);
        b.setName(name);
        b.setToolTipText(caption);
        b.setFocusable(false);

        b.setBorder(BorderFactory.createEmptyBorder());

        if (selectedImage != null)
        {
            b.setSelectedIcon(selectedImage);
        }

        mainPanel.add(b);
        b.addMouseMotionListener(this);
        b.addMouseListener(this);
        b.addActionListener(this);

        b.setBorderPainted(false);

        memHeight = scroller.getHeight();
    }

    public void RemoveItem(String name)
    {
        for (Component c : mainPanel.getComponents())
        {
            if (c.getName().equals(name))
            {
                mainPanel.remove(c);
                break;
            }
        }
    }

    public void mouseDragged(MouseEvent e)
    {
    }

    public void mouseMoved(MouseEvent e)
    {
        Rectangle visRect = mainPanel.getVisibleRect();

        if (scrollT.scroll == false)
        {
            scrollT = new scrollThread();
        }

        int mousePosX = (e.getXOnScreen() - parentJFrame.getX());

        if (mousePosX > visRect.getWidth() - 30 && mousePosX < parentJFrame.getWidth() - 8)
        {
            if (scrollT.scroll == false)
            {
                scrollT.scroll = true;
                scrollT.right = true;

                scrollT.start();
            }
        }
        else if (mousePosX < 30 && mousePosX > 8)
        {
            if (scrollT.scroll == false)
            {
                scrollT.scroll = true;
                scrollT.right = false;
                scrollT.start();
            }
        }
        else
        {
            scrollT.scroll = false;
        }
    }

    public void mouseClicked(MouseEvent e)
    {
        if (e.getSource() instanceof JButton && e.getButton() == MouseEvent.BUTTON3)
            dockItemRightClicked((JButton)e.getSource());
    }

    public void mousePressed(MouseEvent e)
    {
    }

    public void mouseReleased(MouseEvent e)
    {
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
        scrollT.scroll = false;
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == btnExpander)
        {
            int height = 0;

            if (scroller.getHeight() != 0)
            {
                memHeight = scroller.getHeight();
                btnExpander.setText("+");
            }
            else
            {
                height = memHeight;
                btnExpander.setText("-");
            }

            scroller.setPreferredSize(new Dimension(getWidth(), height));

            revalidate();
        }
        else if(e.getSource() instanceof JButton && (mainPanel.getComponentIndex((Component)e.getSource())) != -1)
        {
            JButton b = (JButton)(e.getSource());
            boolean secondClick = false;

            if(b.isSelected())
            {
               secondClick = true;
            }
            
            dockItemClicked(b, secondClick);
        }
    }

    /** Add a DScrollDockMenuListener */
    public void addDScrollDockMenuListener(DScrollDockMenuListener l)
    {
        _listeners.add(l);
    }

    /** A Dock Item was clicked */
    private void dockItemClicked(JButton b, boolean secondClick)
    {
        for(DScrollDockMenuListener listener : _listeners)
            listener.ItemClicked(b, secondClick);
    }

    private void dockItemRightClicked(JButton b)
    {
        for(DScrollDockMenuListener listener : _listeners)
            listener.ItemRightClicked(b);
    }

    class scrollThread extends Thread
    {

        boolean scroll = false;
        boolean right = true;

        @Override
        public void run()
        {
            while (scroll)
            {
                try
                {
                    SwingUtilities.invokeAndWait(new Runnable()
                    {

                        public void run()
                        {
                            JScrollBar bar = scroller.getHorizontalScrollBar();
                            int value = bar.getUnitIncrement() + 12;
                            value = (right ? Math.abs(value) : value * -1);
                            bar.setValue(bar.getValue() + value);
                            if (scroller.getWidth() == bar.getValue() || 0 == bar.getValue())
                            {
                                scroll = false;
                            }
                        }
                    });

                    Thread.sleep(100);
                }
                catch (Exception ie)
                {
                    // do nothing
                }
            }
        }
    }
}
