package com.mxit.core.ui;

import com.mxit.core.res.EmoticonPack;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;


/**
 * @author Dax Booysen
 *
 * A popup grid for various functions
 */
public final class PopupGrid extends JPopupMenu
{

    /** the panel */
    public JPanel panel = new JPanel();
    /** The main panel */
    public JScrollPane scrollPane;
    /** requesting Component */
    public Component c;

    /** Constructor */
    private PopupGrid()
    {
        this.setLayout(new BorderLayout());
    }

    /** The list */
    public static PopupGrid SetupGrid(final Component comp, int height, int gridSize, LinkedHashMap<String, EmoticonPack> packs)
    {
        final PopupGrid Instance = new PopupGrid();
        Instance.c = comp;
        Instance.setVisible(false);
        // remove panel
        Instance.removeAll();
        Instance.panel.removeAll();
        // set panels size
        Instance.panel.setLayout(new GridLayout(gridSize,packs.size()*15));

        for (EmoticonPack pack : packs.values())
        {
            for (Entry<String[], Image> i : pack.emoticons.entrySet())
            {
                JButton b = new JButton(new ImageIcon(i.getValue()));
                b.setToolTipText(i.getKey()[0]);
                b.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e)
                    {
                        // user clicks the button
                        ChatScreen cs = ((ChatScreen)comp);
                        cs.txtInput.insertText(((JButton)(e.getSource())).getToolTipText());
                        Instance.setVisible(false);
                        cs.txtInput.requestFocus(true);
                    }
                });
                Instance.panel.add(b);
            }
        }
        Instance.scrollPane = new JScrollPane(Instance.panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        Instance.add(Instance.scrollPane, BorderLayout.CENTER);
        // finally show list
        //Instance.setBounds(x, y, panel.getPreferredSize().width + scrollPane.getVerticalScrollBar().getPreferredSize().width+15, height);
        Instance.setSize(Instance.panel.getPreferredSize().width + Instance.scrollPane.getVerticalScrollBar().getPreferredSize().width+15, height);
        //Instance.setVisible(true);
        //_instance.setModal(true);
        //Instance.requestFocus();
        return Instance;
    }
}
