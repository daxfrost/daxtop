package com.dax.control;

import com.dax.control.item.Item;
import com.dax.control.item.ItemCommand;
import com.dax.control.item.ItemCommandClearScreen;
import com.dax.control.item.ItemEmoticon;
import com.dax.control.item.ItemInlineImage;
import com.dax.control.item.ItemLoadingBar;
import com.dax.control.item.ItemString;
import com.dax.control.util.FrmFind;
import com.dax.lib.components.JLinkLabel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.KeyboardFocusManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * @author Dax Booysen
 */
public class DaxChatPane extends JTextPane
{
    // messages
    private ArrayList<ChatMessage> messages = new ArrayList<ChatMessage>();
    private int _caretPos = 0;     
    private int caretPosBeforeAddition = 0;
    private boolean clearScreen = false;
    private StyledDocument doc;    // styles
    private static int MaximumMessages = 100;
    private static HashMap<String, Style> styles;
    private static Style StyleBold,  StyleItalic,  StylePlain,  StyleUnderlined, StyleEmoticon, StyleInlineImage, StyleButton, StyleProgressBar, StyleProgressButton;

    /** Constructs a new DaxChatPane */
    public DaxChatPane()
    {
        // initialize document
        doc = this.getStyledDocument();

        // settings
        this.setEditable(false);

        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
        this.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);

        // TODO figure out default border issue
        //this.setMargin(new Insets(1, 10, 1, 2));

        // styles havent been loaded
        if (styles == null)
        {
            // initialize style cache
            styles = new HashMap<String, Style>();

            // initialize "font-style" styles
            StyleBold = doc.addStyle("Bold", null);
            StyleConstants.setBold(StyleBold, true);
            StyleItalic = doc.addStyle("Italic", null);
            StyleConstants.setItalic(StyleItalic, true);
            StyleUnderlined = doc.addStyle("Underline", null);
            StyleConstants.setUnderline(StyleUnderlined, true);
            StylePlain = doc.addStyle("Plain", null);
            StyleConstants.setItalic(StylePlain, false);
            StyleConstants.setBold(StylePlain, false);
            StyleEmoticon = doc.addStyle("Emoticon", null);
            StyleInlineImage = doc.addStyle("InlineImage", null);
            StyleButton = doc.addStyle("Button", null);
            StyleProgressBar = doc.addStyle("Progress", null);
            StyleProgressButton = doc.addStyle("ProgressButton", null);
        }
    }

    /** Appends a new ChatMessage onto the DaxChatPane */
    public void AppendMessage(ChatMessage msg)
    {
        synchronized (doc)
        {
            boolean reloadNeeded = false;

            // only add a new line if this isnt the first message in the chat pane
            if (!messages.isEmpty())
            {
                // maximum lines in the chat control
                if (messages.size() > MaximumMessages + (MaximumMessages/4))
                {
                    int messageCount = 0;

                    while (messageCount++ < MaximumMessages/4)
                        messages.remove(0);

                    // cleared memory, reload is needed
                    reloadNeeded = true;
                }

                try
                {
                    doc.insertString(_caretPos, "\n", null);
                    _caretPos += 1;
                }
                catch (BadLocationException e)
                {
                    e.printStackTrace();
                }
            }

            // add to chat history
            messages.add(msg);

            // record the previous message length
            caretPosBeforeAddition = _caretPos;

            // add to end of the document
            for (Item i : msg.items)
            {
                appendItem(i);
            }

            if (clearScreen)
            {
                int idx = messages.size()-3;

                for(int i = 0; i < idx; i++)
                    messages.remove(0);

                reloadNeeded = false;
            }

            // we need to reload chat history
            if (reloadNeeded)
            {
                // clear memory
                this.setText("");
                _caretPos = 0;

                // reload all
                for(ChatMessage cm : messages)
                {
                    // keep new line between each message
                    if (_caretPos>0)
                    {
                        try
                        {
                            doc.insertString(_caretPos, "\n", null);
                            _caretPos += 1;
                        }
                        catch (BadLocationException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    // record the previous message length
                    caretPosBeforeAddition = _caretPos;
                    
                    for (Item i : cm.items)
                    {
                        appendItem(i);
                    }
                }
            }
        }
    }

    private void appendItem(com.dax.control.item.Item c)
    {
        try
        {
            // TEXT
            if (c instanceof ItemString)
            {
                ItemString is = (ItemString) c;

                // dont add empty strings
                if (is.text.isEmpty())
                {
                    return;                // do font size
                }
                Style fontStyle = getFontSizeStyle(is.font.getSize());
                doc.insertString(_caretPos, is.text, fontStyle);

                // do bold
                if (is.font.isBold())
                {
                    doc.setCharacterAttributes(_caretPos, is.text.length(), StyleBold, false);
                // do italic
                }
                if (is.font.isItalic())
                {
                    doc.setCharacterAttributes(_caretPos, is.text.length(), StyleItalic, false);
                // do italic
                }
                if (is.font.isPlain())
                {
                    doc.setCharacterAttributes(_caretPos, is.text.length(), StylePlain, false);                // do underline
                }
                if (is.isUnderlined())
                {
                    doc.setCharacterAttributes(_caretPos, is.text.length(), StyleUnderlined, false);                // do color
                }

                Style colorStyle = getColorStyle(is.color);
                doc.setCharacterAttributes(_caretPos, is.text.length(), colorStyle, false);

                _caretPos += is.text.length();

            }
            // EMOTICON
            else if (c instanceof ItemEmoticon)
            {
                ItemEmoticon ie = (ItemEmoticon) c;

                StyleConstants.setIcon(StyleEmoticon, new ImageIcon(ie.Icon));
                doc.insertString(_caretPos, " ", StyleEmoticon);

                //this.insertIcon(new ImageIcon(ie.Icon));
                _caretPos++;             // increment only 1 character for images
            }
            else if (c instanceof ItemCommand)
            {
                ItemCommand ic = (ItemCommand) c;
                
                JLinkLabel button = new JLinkLabel(ic.selMsg);
                button.addActionListener(ic);
                //JButton button = new JButton(ic.selMsg);
                //button.setMargin(new Insets(0,0,0,0));

                // get metrics from the graphics
                button.setFont(this.getFont());
                FontMetrics metrics = this.getFontMetrics(button.getFont());
                // get the height of a line of text in this font and render context
                int hgt = metrics.getHeight();
                // get the advance of my text in this font and render context
                int adv = metrics.stringWidth(ic.selMsg);
                // calculate the size of a box to hold the text with some padding for the borders.
                Dimension size = new Dimension(adv+2, hgt+2);

                button.setMaximumSize(new Dimension(size.width, hgt+200));
                button.setAlignmentY(0.8f);
                //button.setAlignmentY(0.7f);

                StyleConstants.setComponent(StyleButton, button);
                StyleConstants.setAlignment(StyleButton, StyleConstants.ALIGN_CENTER);
                StyleConstants.setAlignment(StyleButton, StyleConstants.ALIGN_CENTER);

                // add the button
                doc.insertString(_caretPos, " ", StyleButton);
                
                _caretPos++;
            }
            else if (c instanceof ItemCommandClearScreen)
            {
                ItemCommandClearScreen iccs = (ItemCommandClearScreen)c;

                // auto clear
                if (iccs.auto)
                {
                    // clear the message history
                    clearScreen = true;

                    // clear the text screen
                    doc.remove(0, caretPosBeforeAddition);
                    _caretPos = this.getText().length();
                }
            }
            else if (c instanceof ItemInlineImage)
            {
                final ItemInlineImage iii = (ItemInlineImage)c;
                
                if (iii.url != null)
                {
                    // fetch the image
                    try
                    {
                        if (iii.flow==3)
                        doc.insertString(_caretPos++, "\n", null);

                        final DaxChatPane pane = this;
                        final ImageIcon img = new ImageIcon();

                        new Thread(new Runnable() {
                            public void run()
                            {
                                try
                                {
                                    java.awt.Image imgLoading = java.awt.Toolkit.getDefaultToolkit().createImage(new java.net.URL(iii.url.trim()));
                                    img.setImage(imgLoading);

                                    SwingUtilities.invokeLater(new Runnable() {
                                        public void run() {
                                            pane.updateUI();
                                        }
                                    });
                                }
                                catch (Exception ex)
                                {
                                    ex.printStackTrace();
                                }
                            }
                        }).start();

                        StyleConstants.setIcon(StyleInlineImage, img);
                        doc.insertString(_caretPos++, " ", StyleInlineImage);

                        if (iii.flow==3)
                            doc.insertString(_caretPos++, "\n", null);
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
                else
                {
                    if (iii.flow==3)
                        doc.insertString(_caretPos++, "\n", null);

                    StyleConstants.setIcon(StyleInlineImage, new ImageIcon(iii.img));
                    doc.insertString(_caretPos++, " ", StyleInlineImage);

                    if (iii.flow==3)
                        doc.insertString(_caretPos++, "\n", null);
                }
            }
            else if (c instanceof ItemLoadingBar)
            {
                final ItemLoadingBar ilb = (ItemLoadingBar)c;

                StyleConstants.setComponent(StyleProgressBar, ilb.progressBar);
                doc.insertString(_caretPos++, " ", StyleProgressBar);

                StyleConstants.setComponent(StyleProgressButton, ilb.btnOpen);
                doc.insertString(_caretPos++, " ", StyleProgressButton);

                StyleConstants.setComponent(StyleProgressButton, ilb.btnSave);
                doc.insertString(_caretPos++, " ", StyleProgressButton);
            }
        }
        catch (BadLocationException ex)
        {
            ex.printStackTrace();
        }
    }

    /** Get the Color we need, return out of cache if exists */
    private Style getColorStyle(Color col)
    {
        String key = col.toString();

        Style s = doc.getStyle(key);

        if (s != null)
        {
            return s;
        }

        s = doc.addStyle(key, null);
        StyleConstants.setForeground(s, col);

        return s;
    }

    /** Get the Color we need, return out of cache if exists */
    private Style getFontSizeStyle(int size)
    {
        String key = "FontSize:" + size;

        Style s = doc.getStyle(key);

        if (s != null)
        {
            return s;
        }
        s = doc.addStyle(key, null);
        StyleConstants.setFontSize(s, size);

        return s;
    }

    /** Safely clears the screen */
    public void ClearScreen()
    {
        synchronized (doc)
        {
            try
            {
                // clear memory
                messages.clear();

                // clear the text screen
                this.setText("");
                _caretPos = doc.getLength();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /** Request a search box */
    public void ShowSearch()
    {
        FrmFind frmFind = new FrmFind(this);
        frmFind.setVisible(true);
    }

    /** Searches and selects the text if found, returns -1 if not found */
    public int FindText(String findText, int lastIndex, boolean loop)
    {
        // get from the next character onwards
        lastIndex = lastIndex == 0 ? 0 : lastIndex+1;

        String text = "";

        try
        {
            text = getDocument().getText(0, getDocument().getLength());
        }
        catch (Exception e) { e.printStackTrace(); }

        int idx = text.indexOf(findText, lastIndex);

        // select the text if we found
        if (idx != -1)
        {
            this.requestFocusInWindow(true);
            this.setSelectionStart(idx);
            this.setSelectionEnd(idx + findText.length());
        }
        else if (lastIndex != 0 && loop)
        {
            idx = text.indexOf(findText, 0);

            if (idx != -1)
            {
                this.requestFocusInWindow(true);
                this.setSelectionStart(idx);
                this.setSelectionEnd(idx + findText.length());
            }
        }

        // return index
        return idx;
    }
}
