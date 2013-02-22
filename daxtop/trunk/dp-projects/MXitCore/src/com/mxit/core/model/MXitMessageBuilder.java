package com.mxit.core.model;

import com.dax.Main;
import com.dax.control.ChatMessage;
import com.dax.control.item.Item;
import com.dax.control.item.ItemCommand;
import com.dax.control.item.ItemCommandClearScreen;
import com.dax.control.item.ItemEmoticon;
import com.dax.control.item.ItemInlineImage;
import com.dax.control.item.ItemString;
import com.dax.daxtop.res.ResourceManager;
import com.dax.daxtop.utils.Utils;
import com.mxit.MXitNetwork;
import com.mxit.core.encryption.AES;
import com.mxit.core.model.type.MXitCommandType;
import com.mxit.core.model.type.MXitContactType;
import com.mxit.core.model.type.MXitMessageFlags;
import com.mxit.core.model.type.MXitMessageType;
import com.mxit.core.model.type.MessageTxType;
import com.mxit.core.res.EmoticonPack;
import com.mxit.core.res.MXitRes;
import com.mxit.core.ui.ChatScreen;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * @author Dax Booysen
 * 
 * This class is designed to construct and descontruct Chat Screen message specifics for the MXit protocol
 */
public final class MXitMessageBuilder
{
    /** Processes a nickname dealing with all MXit related data */
    public static ArrayList<Item> ProcessChatNick(MXitNetwork mxitNetwork, String nickname)
    {
        ArrayList<Item> items = new ArrayList<Item>();
        
        ItemString item = new ItemString("");
        item.color = ChatMessage.DefaultRecievedColor;
        item.font = ChatMessage.DefaultFont;
        
        char[] body = nickname.toCharArray();
        
        // loop through each character
        for (int i = 0; i < body.length; i++)
        {
            char c = body[i];

            // deal with the current character
            switch (c)
            {
                case ':':
                    {
                        // if end of text body just add normally
                        if (i == body.length-1)
                        {
                            // just text
                            item.text += c;

                            // add last item
                            items = AddItemString(items, item, ChatMessage.DefaultFont, false, true);
                        }
                        // check for emoticon
                        if (i + 1 > body.length-1)
                        {
                            break;
                        }
                        EmoticonDataHolder emoDataHolder = MXitEmoticonType.ProcessEmoticon(mxitNetwork.Res, new EmoticonDataHolder(c, i, item, items), body);
                        if (emoDataHolder.emo != null)
                        {
                            // deal with emoticon
                            Image emo = emoDataHolder.emo;
                            i = emoDataHolder.index;

                            ItemEmoticon emoItem = new ItemEmoticon(new ImageIcon(emo));

                            item.font = ChatMessage.DefaultFont;
                            items.add(item);
                            items.add(emoItem);
                            item = new ItemString("");
                            item.color = ChatMessage.DefaultRecievedColor;
                        }
                        else
                        {
                            // wasnt an emoticon
                            item.text += c;
                        }
                    }
                    break;
                case ';':
                case '(':
                case '8':
                case 'P':
                    {
                        // also possible emoticons
                        // if end of text body just add normally
                        if (i == body.length-1)
                        {
                            // just text
                            item.text += c;

                            items = AddItemString(items, item, ChatMessage.DefaultFont, false, true);
                        }

                        // check for emoticon
                        if (i + 1 > body.length-1)
                        {
                            break;
                        }
                        EmoticonDataHolder emoDataHolder = MXitEmoticonType.ProcessEmoticon(mxitNetwork.Res, new EmoticonDataHolder(c, i, item, items), body);
                        if (emoDataHolder.emo != null)
                        {
                            // deal with emoticon
                            Image emo = emoDataHolder.emo;
                            i = emoDataHolder.index;

                            ItemEmoticon emoItem = new ItemEmoticon(new ImageIcon(emo));

                            item.font = ChatMessage.DefaultFont;
                            items.add(item);
                            items.add(emoItem);
                            item = new ItemString("");
                            item.color = ChatMessage.DefaultRecievedColor;
                        }
                        else
                        {
                            // wasnt an emoticon
                            item.text += c;
                        }
                    }
                    break;
                default:
                {
                    // just text
                    item.text += c;

                    // add last item
                    if (i == body.length-1)
                    {
                        items = AddItemString(items, item, ChatMessage.DefaultFont, false, true);
                    }
                }
            }
        }

        return items;
    }

    /** Processes a message dealing with all MXit related data */
    public static ArrayList<Item> ProcessMessage(MXitNetwork mxitNetwork, String messageBody, MXitMessageType messageType, int messageFlags, MessageTxType txType, MXitContactType contactType, ChatScreen cScreen)
    {
        boolean received = txType == MessageTxType.Received ? true : false;

        ArrayList<Item> items = new ArrayList<Item>();

        // current font (used for keeping markup state)
        Font currentFont = new Font(ResourceManager.FONT_CHAT_SCREEN.getAttributes());

        // current underlined status
        boolean underlined = false;

        char[] body = messageBody.toCharArray();

        ItemString item = new ItemString("");

        // loop through each character
        for (int i = 0; i < body.length; i++)
        {
            char c = body[i];

            // deal with the current character
            switch (c)
            {
                
                case ':':
                    {
                        // ":" colon can be many different things...
                        // if end of text body just add normally
                        if (i == body.length-1)
                        {
                            // just text
                            item.text += c;

                            // add last item
                            if (i == body.length-1)
                            {
                                items = AddItemString(items, item, currentFont, underlined, received);
                            }
                        }

                        // check for command or custom form
                        if(messageType == MXitMessageType.Command && body.length > i+8 && body[i+1] == ':')
                        {
                            int endIdx = -1;

                            for(int t = i+2; t <= body.length; t++)
                            {
                                if(body[t] == ':')
                                {
                                    endIdx = t+1;
                                    break;
                                }
                            }

                            if(endIdx != -1)
                            {
                                items.add(item);
                                String commandStr = new String(Arrays.copyOfRange(body, i, endIdx));

                                // if is a command
                                System.out.println(commandStr);
                                MXitCommand cmd = new MXitCommand(commandStr);

                                if(cmd.Type == MXitCommandType.Reply)
                                {
                                    ItemCommand ic = new ItemCommand(cmd, cmd.replyMsg, cmd.selMsg, cScreen);
                                    items.add(ic);
                                }
                                else if(cmd.Type == MXitCommandType.Clear_Screen)
                                {
                                    ItemCommandClearScreen iccs = new ItemCommandClearScreen(cmd, cmd.auto);
                                    items.add(iccs);
                                }
                                else if(cmd.Type == MXitCommandType.InlineImage)
                                {
                                    ItemInlineImage iii = null;

                                    // if we have the data
                                    if (cmd.dat != null)
                                    {
                                        try
                                        {
                                            // get the image from the data received
                                            iii = new ItemInlineImage(cmd, Toolkit.getDefaultToolkit().createImage(AES.base64_decode(cmd.dat)), cmd.algn, cmd.flow);
                                        }
                                        catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                    else if (cmd.src != null && !cmd.src.isEmpty())
                                    {
                                        // decode url
                                        try
                                        {
                                            iii = new ItemInlineImage(cmd, URLDecoder.decode(cmd.src, "UTF-8"));
                                        }
                                        catch (UnsupportedEncodingException ex)
                                        {
                                            ex.printStackTrace();
                                        }
                                    }
                                    
                                    items.add(iii);
                                }
                                else if(cmd.Type == MXitCommandType.Platform_Request)
                                {
                                    ItemCommand ic = new ItemCommand(cmd, cmd.replyMsg, cmd.selMsg, cScreen, cmd.destination);
                                    items.add(ic);
                                }

                                i = endIdx-1;
                                item = new ItemString("");

                                break;
                            }
                        }

                        // check for vibes (TODO)

                        // check for emoticon
                        if (i + 1 > body.length-1)
                        {
                            break;
                        }
                        EmoticonDataHolder emoDataHolder = MXitEmoticonType.ProcessEmoticon(mxitNetwork.Res, new EmoticonDataHolder(c, i, item, items), body);
                        if (emoDataHolder.emo != null)
                        {
                            // deal with emoticon
                            Image emo = emoDataHolder.emo;
                            i = emoDataHolder.index;

                            ItemEmoticon emoItem = new ItemEmoticon(new ImageIcon(emo));

                            items.add(item);
                            Color oldCol = item.color;
                            items.add(emoItem);
                            item = new ItemString("");
                            item.color = oldCol;
                        }
                        else
                        {
                            // wasnt an emoticon
                            item.text += c;
                        }
                    }
                    break;
                case ';':
                case '(':
                case '8':
                case 'P':
                    {
                        // also possible emoticons
                        // if end of text body just add normally
                        if (i == body.length-1)
                        {
                            // just text
                            item.text += c;

                            items = AddItemString(items, item, currentFont, underlined, received);
                        }

                        // check for emoticon
                        if (i + 1 > body.length-1)
                        {
                            break;
                        }
                        EmoticonDataHolder emoDataHolder = MXitEmoticonType.ProcessEmoticon(mxitNetwork.Res, new EmoticonDataHolder(c, i, item, items), body);
                        if (emoDataHolder.emo != null)
                        {
                            // deal with emoticon
                            Image emo = emoDataHolder.emo;
                            i = emoDataHolder.index;

                            ItemEmoticon emoItem = new ItemEmoticon(new ImageIcon(emo));

                            items.add(item);
                            Color oldCol = item.color;
                            items.add(emoItem);
                            item = new ItemString("");
                            item.color = oldCol;
                        }
                        else
                        {
                            // wasnt an emoticon
                            item.text += c;

                            // add last item
                            if (i == body.length-1)
                            {
                                items = AddItemString(items, item, currentFont, underlined, received);
                            }
                        }
                    }
                    break;
                case MXitMarkup.LINK:
                {
                    // if doesnt include markup or we are in a chat zone dont apply                                      // if the markup is "escaped" just append the text
                    if (!Utils.isSet(messageFlags, MXitMessageFlags.MARKUP) || contactType == MXitContactType.ChatZone || (i != 0 && body[i - 1] == '\\'))
                    {
                        // just text
                        item.text += c;

                        // add last item
                        if (i == body.length-1)
                        {
                            items = AddItemString(items, item, currentFont, underlined, received);
                        }

                        break;
                    }


                    if (i == 0 || i > 0)
                    {
                        // check for command
                        if(body.length > i+1)
                        {
                            int endIdx = -1;

                            for(int t = i+1; t < body.length; t++)
                            {
                                if(body[t] == MXitMarkup.LINK)
                                {
                                    endIdx = t+1;
                                    break;
                                }
                            }

                            if(endIdx != -1)
                            {
                                items.add(item);
                                String commandStr = new String(Arrays.copyOfRange(body, i, endIdx+1));

                                // if is a command
                                System.out.println(commandStr);
                                MXitCommand cmd = new MXitCommand(commandStr);

                                if(cmd.Type == MXitCommandType.Reply)
                                {
                                    ItemCommand ic = new ItemCommand(cmd, cmd.replyMsg, cmd.selMsg, cScreen);
                                    items.add(ic);
                                }

                                i = endIdx-1;
                                item = new ItemString("");

                                break;
                            }
                        }
                    }

                    // wasnt an emoticon
                    item.text += c;

                    // if end of text body just add normally
                    if (i == body.length-1)
                    {
                        // add last item
                        items = AddItemString(items, item, currentFont, underlined, received);
                    }
                }
                break;
                case MXitMarkup.BOLD:
                case MXitMarkup.ITALIC:
                case MXitMarkup.UNDERLINE:
                case MXitMarkup.COLOR:
                case MXitMarkup.TEXT_SIZE_CHANGE:
                    {
                        // if doesnt include markup or we are in a chat zone dont apply                                      // if the markup is "escaped" just append the text
                        if (!Utils.isSet(messageFlags, MXitMessageFlags.MARKUP) || contactType == MXitContactType.ChatZone || (i != 0 && body[i - 1] == '\\'))
                        {
                            // just text
                            item.text += c;

                            // add last item
                            if (i == body.length-1)
                            {
                                items = AddItemString(items, item, currentFont, underlined, received);
                            }
                            
                            break;
                        }

                        // if its a . only apply if theres a + or - afterwards
                        if (c == MXitMarkup.TEXT_SIZE_CHANGE && (i >= body.length-1 || body[i+1] != MXitMarkup.TEXT_BIGGER && body[i+1] != MXitMarkup.TEXT_SMALLER))
                        {
                            // just text
                            item.text += c;

                            // add last item
                            if (i == body.length-1)
                            {
                                items = AddItemString(items, item, currentFont, underlined, received);
                            }

                            break;
                        }

                        MarkupDataHolder dh = MXitMarkup.ProcessMarkup(new MarkupDataHolder(c, currentFont, underlined, item, items, i), body, received);
                        currentFont = dh.font;
                        item = dh.item;
                        items = dh.items;
                        item.font = currentFont;
                        underlined = dh.underlined;
                        i = dh.index;
                    }
                    break;
                case '\\':
                    {
                        // if end of text body just add normally
                        if (i == body.length-1)
                        {
                            item.text += c;
                            items = AddItemString(items, item, currentFont, underlined, received);
                        }
                        // if next character is also '\'
                        else if(body[i+1] == '\\')
                        {
                            // just text
                            item.text += c;
                        }

                    }
                    break;
                default:
                {
                    if(i > 0 && body[i-1] == '\\')
                    {
                        // just text
                        item.text += '\\';
                    }

                    // just text
                    item.text += c;

                    // add last item
                    if (i == body.length-1)
                    {
                        items = AddItemString(items, item, currentFont, underlined, received);
                    }
                }
            }
        }
        
        return items;

    }

    /** Adds a string to the arraylist */
    private static ArrayList<Item> AddItemString(ArrayList<Item> items, ItemString item, Font font, boolean underlined, boolean received)
    {
        item.font = font;
        item.setUnderlined(underlined);
        // only URL decode incoming messages
        if (received)
        {
            try
            {
                item.text = URLDecoder.decode(item.text, "UTF-8");
            }
            catch (UnsupportedEncodingException ex)
            {
                ex.printStackTrace();
            }
        }
        items.add(item);
        return items;
    }

    /** Escape text for formatting */
    public static String EscapeMessage(String message)
    {
        message = message.replace(".+", "\\.+");
        message = message.replace(".-", "\\.-");
        message = message.replace("*", "\\*");
        message = message.replace("(\\*)", "(*)");
        message = message.replace("/", "\\/");
        message = message.replace("_", "\\_");
        message = message.replace("#", "\\#");

        return message;
    }

    /** Markup Types */
    static class MXitMarkup
    {

        /** Color pattern for markup text color */
        static final Pattern textColorPattern = Pattern.compile("[a-fA-F0-9]{6}");
        static final char BOLD = '*';
        static final char ITALIC = '/';
        static final char UNDERLINE = '_';
        static final char COLOR = '#';
        static final char LINK = '$';
        static final char TEXT_SIZE_CHANGE = '.';
        static final char TEXT_BIGGER = '+';
        static final char TEXT_SMALLER = '-';

        /** Parse possible markup text and append item string data */
        private static MarkupDataHolder ProcessMarkup(MarkupDataHolder dh, char[] body, boolean received)
        {
            char c = dh.c;
            Font currentFont = dh.font;
            int index = dh.index;

            dh.items = AddItemString(dh.items, dh.item, currentFont, dh.underlined, received);
            Color oldCol = dh.item.color;
            dh.item = new ItemString("");
            dh.item.color = oldCol;

            if (c == ITALIC)
            {
                // if italics must be turned off, remove from bitset
                if (currentFont.isItalic())
                {
                    currentFont = currentFont.deriveFont(currentFont.getStyle() & Font.BOLD);
                }
                else
                {
                    currentFont = currentFont.deriveFont(Font.ITALIC | currentFont.getStyle());
                }
            }
            else if (c == BOLD)
            {
                // if italics must be turned off, remove from bitset
                if (currentFont.isBold())
                {
                    currentFont = currentFont.deriveFont(currentFont.getStyle() & Font.ITALIC);
                }
                else
                {
                    currentFont = currentFont.deriveFont(Font.BOLD | currentFont.getStyle());
                }
            }
            else if (c == UNDERLINE)
            {
                // toggle underlined state
                dh.underlined = !dh.underlined;
            }
            else if (c == COLOR)
            {
                // check for color change
                char[] possibleColor = Arrays.copyOfRange(body, index + 1, index + 7);
                String test = new String(possibleColor);

                // restore to original color
                if (test.equals("??????"))
                {
                    dh.item.color = ResourceManager.COLOR_CHAT_TEXT;
                    index += 6;
                }
                else
                {
                    Matcher m = textColorPattern.matcher(test);
                    if (m.matches())
                    {
                        dh.item.color = new Color(Integer.parseInt(test, 16));
                        index += 6;
                    }
                    else
                    {
                        dh.item.text += c;
                    }
                }
            }
            else if (c == TEXT_SIZE_CHANGE && body.length-1 >= index+1 &&  body[dh.index+1] == TEXT_BIGGER)
            {
                index++;
                
                if (!(currentFont.getSize() >= 32))
                    currentFont = currentFont.deriveFont(currentFont.getSize2D()+4);
            }
            else if (c == TEXT_SIZE_CHANGE && body.length-1 >= index+1 && body[dh.index+1] == TEXT_SMALLER)
            {
                index++;

                if (!(currentFont.getSize() <= 8))
                    currentFont = currentFont.deriveFont(currentFont.getSize2D()-4);
            }

            // set values again
            dh.font = currentFont;
            dh.index = index;

            return dh;
        }
    }

    /** Emoticon Types */
    static class MXitEmoticonType
    {

        static final char BOLD = '*';
        static final char ITALIC = '/';
        static final char UNDERLINE = '_';
        static final char COLOR = '#';
        static final char LINK = '$';
        static final char TEXT_BIGGER = '+';
        static final char TEXT_SMALLER = '-';

        /** Check if emoticon can be found */
        private static EmoticonDataHolder ProcessEmoticon(MXitRes Res, EmoticonDataHolder dh, char[] body)
        {
            LinkedHashMap<String, EmoticonPack> packs = Res.emoticons;

            String text = new String(new char[]
                    {
                        dh.c, body[dh.index + 1]
                    });

            String text2 = null;

            if (dh.index + 2 <= body.length - 1)
            {
                text2 = new String(new char[]
                        {
                            dh.c, body[dh.index + 1], body[dh.index + 2]
                        });            // cycle through emoticons
            }
            for (EmoticonPack pack : packs.values())
            {
                if (text2 != null)
                {
                    dh.emo = pack.ParseEmoticon(text2);

                    if (dh.emo != null)
                    {
                        dh.index += 2;
                        break;
                    }
                }
                
                if(dh.emo == null)
                {
                    dh.emo = pack.ParseEmoticon(text);

                    if (dh.emo != null)
                    {


                        dh.index += 1;
                        break;
                    }
                }
            }
            
            return dh;
        }
    }

    /** Encodes the conversation to the MXit Message format */
    public static byte[] EncodeMXitMessageFormat()
    {
        return null;
    }

    /** Decodes the MXit Message format to MXit Messages */
    public static ArrayList<ChatMessage> DecodeMXitMessageFormat(MXitNetwork network, byte[] data)
    {
        // messages to return
        ArrayList<ChatMessage> messages = new ArrayList<ChatMessage>();

        try
        {
            // handle the data
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
            // check the MXit Message Format version
            int magic = dis.readInt();

            switch(magic)
            {
                // Version 1 Message Bundle
                case 1:
                case 0x4d584d01:
                {
                    // the number of messages in the bundle
                    int msgCount = dis.readInt();

                    String redName = null;

                    // cycle through each message
                    for (int i = 0; i < msgCount; i++)
                    {
                        long timestamp = dis.readLong();
                        String fromAlias = dis.readUTF();
                        String toAlias = dis.readUTF();
                        String body = dis.readUTF();

                        if (redName==null)
                        {
                            redName = fromAlias;
                        }

                        ChatMessage msg = new ChatMessage("");

                        msg.AppendItem(new ItemString("(" + ChatMessage.sdf.format(new Date(timestamp)) + ") ", ChatMessage.DefaultFont, ChatMessage.DefaultInfoColor, false));
                        msg.AppendItem(new ItemString(fromAlias, ChatMessage.DefaultFont.deriveFont(Font.BOLD), fromAlias.equals(redName) ? ChatMessage.DefaultRecievedColor : ChatMessage.DefaultSentColor , false));
                        msg.AppendItem(new ItemString(" -> "));
                        msg.AppendItem(new ItemString(toAlias, ChatMessage.DefaultFont.deriveFont(Font.BOLD), toAlias.equals(redName) ? ChatMessage.DefaultRecievedColor : ChatMessage.DefaultSentColor , false));
                        msg.AppendItem(new ItemString(": "));
                        ArrayList<Item> items = ProcessMessage(network, body, MXitMessageType.Normal, MXitMessageFlags.MARKUP | MXitMessageFlags.CUSTOM_EMOTICONS, MessageTxType.Received, MXitContactType.MXit, null);
                        for (Item item : items)
                            msg.AppendItem(item);

                        // store the chat message
                        messages.add(msg);
                    }
                }
                break;
                // Version 2 Encrypted Message Bundle
                case 0x4d584d04:
                {
                    // show popup for password entry
                    String password = JOptionPane.showInputDialog(Main.frmChat, "Enter password: ", "Encrypted message...", JOptionPane.QUESTION_MESSAGE);

                    try
                    {
                        data = AES.aes_process(false, password, data, 4, dis.available());

                        // recreate with decrypted data
                        dis = new DataInputStream(new ByteArrayInputStream(data));
                        // read past the magic value and let fall through
                        dis.readInt();
                    }
                    catch (Exception e)
                    {
                        // the password was incorrect
                        JOptionPane.showMessageDialog(Main.frmChat, "Sorry, the password was incorrect!");
                        return null;
                    }
                }
                // Version 2 Message Bundle
                case 0x4d584d02:
                default:
                {
                    // try actually process the message
                    int hdrlen = dis.readInt();
                    
                    // get the num of messages
                    int nummsgs = dis.readInt();

                    // make sure we moved past any new unsupported headers
                    while (hdrlen > 4)
                    {
                        dis.read();
                        hdrlen--;
                    }

                    String redName = null;

                    for (int i = 0; i < nummsgs; i++)
                    {
                        int len = dis.readInt();
                        int supportedLength = dis.available();
                        long timestamp = dis.readLong();
                        String fromalias = dis.readUTF();
                        String toalias = dis.readUTF();
                        String body = dis.readUTF();
                        int flags = dis.readInt();

                        // calculate actual supported length
                        supportedLength = supportedLength - dis.available();

                        // make sure we moved past any new unsupported message content
                        while (len > supportedLength)
                        {
                            dis.read();
                            len--;
                        }

                        if (redName==null)
                        {
                            redName = fromalias;
                        }

                        ChatMessage msg = new ChatMessage("");

                        msg.AppendItem(new ItemString("(" + ChatMessage.sdf.format(new Date(timestamp)) + ") ", ChatMessage.DefaultFont, ChatMessage.DefaultInfoColor, false));
                        msg.AppendItem(new ItemString(fromalias, ChatMessage.DefaultFont.deriveFont(Font.BOLD), fromalias.equals(redName) ? ChatMessage.DefaultRecievedColor : ChatMessage.DefaultSentColor , false));
                        msg.AppendItem(new ItemString(" -> "));
                        msg.AppendItem(new ItemString(toalias, ChatMessage.DefaultFont.deriveFont(Font.BOLD), toalias.equals(redName) ? ChatMessage.DefaultRecievedColor : ChatMessage.DefaultSentColor , false));
                        msg.AppendItem(new ItemString(": "));
                        ArrayList<Item> items = ProcessMessage(network, body, MXitMessageType.Normal, MXitMessageFlags.MARKUP | MXitMessageFlags.CUSTOM_EMOTICONS, MessageTxType.Received, MXitContactType.MXit, null);
                        for (Item item : items)
                            msg.AppendItem(item);

                        // store the chat message
                        messages.add(msg);
                    }
                }
                break;
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            messages.add(new ChatMessage("Unfortunately this message is corrupted and failed to decode."));
        }

        return messages;
    }
}

/** Designed to work with emoticon data */
class EmoticonDataHolder
{
    char c;
    Image emo;
    ItemString item;
    ArrayList<Item> items;
    int index;

    EmoticonDataHolder(char c, int index, ItemString item, ArrayList<Item> items)
    {
        this.c = c;
        this.index = index;
        this.item = item;
        this.items = items;
    }
}

/** Designed to work with markup data */
class MarkupDataHolder
{
    char c;
    Font font;
    boolean underlined;
    ItemString item;
    ArrayList<Item> items;
    int index;

    MarkupDataHolder(char c, Font font, boolean underlined, ItemString item, ArrayList<Item> items, int index)
    {
        this.c = c;
        this.font = font;
        this.underlined = underlined;
        this.item = item;
        this.items = items;
        this.index = index;
    }
}
