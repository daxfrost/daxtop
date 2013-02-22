/*
 * Inline input edit box for chatting
 */
package com.mxit.core.ui;

import com.mxit.MXitNetwork;
import com.mxit.core.model.MXitMessageBuilder;
import com.mxit.core.model.type.MXitContactType;
import java.awt.Color;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;

/**
 * @author Dax Booysen
 */
public class DInputField extends JTextPane implements DocumentListener
{

    /** The parent holding ChatScreen for this input box */
    private ChatScreen _parentChat;
    /** The Editor Kit used for the DInputField */
    public StyledEditorKit kit;
    /** Maximum character length size of */
    public static int maxSize = 32767;
    /** Built in memory of 10 last typed messages (max length 1000 characters each) */
    private ArrayList<String> wordMemory = new ArrayList<String>();
    int pos = 0;
    boolean edited = false;
    /** Default font settings */
    private static int DefaultFontSize = 12;
    private static int FontSizeMultiplier = 3;

    /** MXit Network */
    MXitNetwork mxitNetwork = null;

    /** Constructor */
    public DInputField(MXitNetwork network, ChatScreen cs)
    {
        // set the network
        mxitNetwork = network;

        // set parent chat
        _parentChat = cs;

        // needed for line wrap
        kit = new StyledEditorKit();
        setEditorKit(kit);

        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
        this.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);

        InputMap im = getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap am = getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), pressedEnterAction);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_DOWN_MASK), pressedEnterWithShiftAction);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), memoryFetchAction);
        am.put(im.get(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0)), pressedEnterAction);
        am.put(im.get(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_DOWN_MASK)), pressedEnterWithShiftAction);
        am.put(im.get(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0)), memoryFetchAction);

        this.setContentType("text/plain");

        // this line of code is crucial for fixing the issue of font not being set properly
        this.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);

        this.getDocument().addDocumentListener(this);

        // setup styles
        Style style = this.addStyle("Plain", null);
        StyleConstants.setBold(style, false);
        StyleConstants.setItalic(style, false);
        StyleConstants.setUnderline(style, false);
        StyleConstants.setFontSize(style, DefaultFontSize);
        //StyleConstants.setForeground(style, DefaultColor.getForeground());
        style = this.addStyle("Bold", null);
        StyleConstants.setBold(style, true);
        style = this.addStyle("Italic", null);
        StyleConstants.setItalic(style, true);
        style = this.addStyle("Underline", null);
        StyleConstants.setUnderline(style, true);
    }

    /** Inserts a new line where the current caret is indexed */
    public void insertEnter()
    {
        try
        {
            int i = getCaretPosition();
            getDocument().insertString(getCaretPosition(), "\n", null);
            setCaretPosition(i + 1);
            _parentChat.refreshScreen();
        }
        catch (BadLocationException ex)
        {
            ex.printStackTrace();
        }
    }

    ///////// WORKAROUND FOR ISSUE (SEE ChatScreen.java) ////////////
    public void insertUpdate(DocumentEvent e)
    {
        controlMaximumCharacters();
        edited = true;
        _parentChat.refreshScreen();
    }

    void insertText(String toString)
    {
        try
        {
            Document d = this.getDocument();
            d.insertString(this.getCaretPosition(), toString, null);
        }
        catch (BadLocationException ex)
        {
            ex.printStackTrace();
        }
    }

    public void removeUpdate(DocumentEvent e)
    {
        edited = true;
        _parentChat.refreshScreen();
    }

    public void changedUpdate(DocumentEvent e)
    {
        _parentChat.refreshScreen();
    }

    /** Control the maximum characters entered into this editor */
    private void controlMaximumCharacters()
    {
        try
        {
            final Document doc = this.getDocument();

            SwingUtilities.invokeLater(new Runnable()
            {

                public void run()
                {
                    try
                    {
                        int length = doc.getLength();
                        int difference = length - maxSize;

                        if (difference > 0)
                        {
                            doc.remove(length - difference, difference);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    //////////////////////// KEY ACTIONS ////////////////////////////

    /** The action used for enter being pressed on the input field */
    private final Action pressedEnterAction = new AbstractAction()
    {
        public void actionPerformed(ActionEvent e)
        {
            DInputField source = ((DInputField)e.getSource());

            edited = false;

            int location = 0;

            if (wordMemory.size() < 10)
            {
                location = wordMemory.size();
            }
            else
            {
                wordMemory.remove(0);
                location = 9;
            }

//            try
//            {
                String t = source.getText();

                source.wordMemory.add(location, t.substring(0, (t.length() > 1000 ? 1000 : t.length())));
                pos = location;
//            }
//            catch (BadLocationException ble)
//            {
//                System.out.println(ble.offsetRequested());
//                ble.printStackTrace();
//            }

            source._parentChat.SendMessage();
        }
    };

    /** The action used for enter being pressed with shift on the input field */
    private final Action pressedEnterWithShiftAction = new AbstractAction()
    {
        public void actionPerformed(ActionEvent e)
        {
            DInputField source = ((DInputField)e.getSource());

            // new line if shift is down
            source.insertEnter();
        }
    };

    /** The action used for inserting the last message sent in the input field */
    private final Action memoryFetchAction = new AbstractAction()
    {
        public void actionPerformed(ActionEvent e)
        {
            DInputField source = ((DInputField)e.getSource());

            if (source.getText().isEmpty() || edited == false)
            {
                if(!(pos < 0 || wordMemory.isEmpty()))
                {
                    source.setText(wordMemory.get(pos--));
                    source.setCaretPosition(source.getDocument().getLength());
                }

                edited = false;
            }
            else
            {
                pos = wordMemory.size()-1;
                edited = true;
            }
        }
    };

    /** This retreives the text */
    public String fetchText()
    {
        // the string to return
        String text = "";

        // the styled document of this textpane
        StyledDocument doc = this.getStyledDocument();

        // previous element states
        boolean boldState = false;
        boolean italicState = false;
        boolean underlinedState = false;
        int fontSizeState = DefaultFontSize;
        Color colorState = new Color(this.getForeground().getRGB()); //DefaultColor.getForeground().getRGB());

        Element lines = doc.getDefaultRootElement();
        int lineCount = lines.getElementCount();

        for (int j=0; j<lineCount; j++)
        {
            Element section = lines.getElement(j);

            // Get number of paragraphs.
            // In a text pane, a span of characters terminated by single
            // newline is typically called a paragraph.
            int paraCount = section.getElementCount();

            // Get index ranges for each paragraph
            for (int i=0; i<paraCount; i++)
            {
                Element e = section.getElement(i);

                if (e==null)
                    continue;

                int rangeStart = e.getStartOffset();
                int rangeEnd = e.getEndOffset();

                try
                {
                    // retrieve the text and attributes of this run of characters
                    String para = doc.getText(rangeStart, rangeEnd-rangeStart);

                    // if this is not a chat zone, escape (filter) the possible user markup && ONLY if markup filtering is on
                    if (_parentChat.contact.Type != MXitContactType.ChatZone && !mxitNetwork.CurrentProfile.InlineMarkup)
                    {
                        para = MXitMessageBuilder.EscapeMessage(para);
                    }

                    AttributeSet set = e.getAttributes();

                    // toggle bold text
                    if (StyleConstants.isBold(set) && !boldState)
                    {
                        boldState = true;
                        para = "*" + para;
                    }
                    else if (!StyleConstants.isBold(set) && boldState)
                    {
                        boldState = false;
                        para = "*" + para;
                    }

                    // toggle italic text
                    if (StyleConstants.isItalic(set) && !italicState)
                    {
                        italicState = true;
                        para = "/" + para;
                    }
                    else if (!StyleConstants.isItalic(set) && italicState)
                    {
                        italicState = false;
                        para = "/" + para;
                    }

                    // toggle underlined text
                    if (StyleConstants.isUnderline(set) && !underlinedState)
                    {
                        underlinedState = true;
                        para = "_" + para;
                    }
                    else if (!StyleConstants.isUnderline(set) && underlinedState)
                    {
                        underlinedState = false;
                        para = "_" + para;
                    }

                    Color color = StyleConstants.getForeground(set);

                    // toggle text color
                    if (color.getRGB() != colorState.getRGB())
                    {
                        if (color.getRGB() == this.getForeground().getRGB())
                        {
                            para = "#??????" + para;
                        }
                        else
                        {
                            String red = Integer.toHexString(color.getRed());
                            String green = Integer.toHexString(color.getGreen());
                            String blue = Integer.toHexString(color.getBlue());

                            // make sure each hex value is 2 characters long
                            if (red.length() == 1) red = "0" + red;
                            if (green.length() == 1) green = "0" + green;
                            if (blue.length() == 1) blue = "0" + blue;

                            // create hex string
                            String hexColor = "#" + red + green + blue;
                            para = hexColor + para;
                        }

                        colorState = new Color(color.getRGB());
                    }

                    int fontSize = StyleConstants.getFontSize(set);

                    // toggle font size
                    if (fontSize != fontSizeState)
                    {
                        // handle size
                        if (fontSize > fontSizeState)
                        {
                            while (fontSizeState < fontSize)
                            {
                                para = ".+" + para;
                                fontSizeState += FontSizeMultiplier;
                            }
                        }
                        else
                        {
                            while (fontSizeState > fontSize)
                            {
                                para = ".-" + para;
                                fontSizeState -= FontSizeMultiplier;
                            }
                        }

                        // store the last size
                        fontSizeState = fontSize;
                    }

                    // add to final string
                    text += para;
                }
                catch (BadLocationException ex)
                {
                    ex.printStackTrace();
                }
            }
        }

        try
        {
            this.setText(" ");
            this.setCharacterAttributes(this.getStyle("Plain"), true);
            this.repaint();
            this.setText("");
        }
        catch (Exception e) {}
        

        return text.substring(0, text.length()-1);
    }
}