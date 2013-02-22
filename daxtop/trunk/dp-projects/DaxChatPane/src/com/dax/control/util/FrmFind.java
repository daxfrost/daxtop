package com.dax.control.util;

import com.dax.control.DaxChatPane;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * @author Dax Frost
 */
public class FrmFind extends JDialog implements KeyListener, ActionListener
{
    /** index of last found location */
    private int lastIndex;

    /** If checked, loop the search if reaching the bottom */
    JCheckBox chkLoopSearch = new JCheckBox("Loop search");

    /** The find button */
    JButton btnFind = new JButton("Find");

    /** The text field to enter a search string */
    JTextField tfSearchCriteria = new JTextField();

    /** The label for the text field for search criteria */
    JLabel lblSearch = new JLabel("Find: ");

    /** The DaxChatPane controlling this find window */
    DaxChatPane pane = null;

    /** A utility frame for searching the contents of the DaxChatPane */
    public FrmFind(DaxChatPane owner)
    {
        this.setAlwaysOnTop(true);
        this.setLayout(null);
        this.setSize(300, 120);
        this.setLocation(600, 400);
        this.pane = owner;
        this.pane.requestFocusInWindow();
        this.getRootPane().setDefaultButton(btnFind);
        this.setTitle("Search for...");
        this.setResizable(false);

        this.add(lblSearch);
        this.add(tfSearchCriteria);
        this.add(chkLoopSearch);
        this.add(btnFind);

        btnFind.addActionListener(this);
        this.addKeyListener(this);
        tfSearchCriteria.addKeyListener(this);
        btnFind.addKeyListener(this);
        chkLoopSearch.addKeyListener(this);

        lblSearch.setBounds(10, 10, 50, 25);
        tfSearchCriteria.setBounds(40, 10, 240, 25);
        chkLoopSearch.setBounds(10, 45, 150, 25);
        btnFind.setBounds(190, 45, 90, 25);
    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e)
    {
        // escape pressed
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
        {
            this.setVisible(false);
        }
    }

    public void keyReleased(KeyEvent e) {

    }

    public void actionPerformed(ActionEvent e)
    {
        // clicked find
        if (pane != null)
        {
            lastIndex = pane.FindText(tfSearchCriteria.getText(), lastIndex, chkLoopSearch.isSelected());
            tfSearchCriteria.requestFocusInWindow();

            if (!chkLoopSearch.isSelected() && lastIndex == -1)
            {
                JOptionPane.showMessageDialog(this, "\"" + tfSearchCriteria.getText() +  "\" not found.", "Not found...", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
