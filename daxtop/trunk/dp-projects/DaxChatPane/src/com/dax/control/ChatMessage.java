package com.dax.control;

import com.dax.control.item.Item;
import com.dax.control.item.ItemString;
import com.dax.control.type.MessageTxType;
import java.awt.Color;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Dax Booysen
 */
public class ChatMessage
{
    /** The text printed before messages the user sends */
    private final String YOU = "You: ";
    
    /** The text seen who sent this message i.e "You:/Info: etc..." */
    private ItemString YouString;

    /** Static used for date formatting */
    public static SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");

    /** The text seen before a received message (the time) */
    private ItemString timeStampString;

    /** Who the message is from */
    public String From = null;
    
    /** The default font */
    public static final Font DefaultFont = new Font("Arial", Font.BOLD, 12);
    
    /** The default text color */
    public final Color DefaultColor = new Color(0x000000);
    
    /** The default sent color */
    public static final Color DefaultSentColor = new Color(0x0000CC);
    
    /** The default recieved color */
    public static final Color DefaultRecievedColor = new Color(0xA70000);
    
    /** Colon used in nickname chatroom messages */
    private static ItemString colonItem;
    
    /** The default info color */
    public static final Color DefaultInfoColor = new Color(0xBBBBBB);
    
    /** The timestamp of this message's creation time */
    public long timeStamp;
    
    /** The contents of this message */
    public ArrayList<Item> items;
    
    /** The origin type of this message */
    MessageTxType txType = MessageTxType.Recieved;
    
    /** Inaccessable constructor */
    private ChatMessage() { }
    
    /** Constructs a new ChatMessage */
    private ChatMessage(String from, MessageTxType type, long time)
    {        
        timeStamp = time;
        items = new ArrayList<Item>();
        
        switch(type)
        {
            case Sent:
            {
                if(YouString == null)
                    YouString = new ItemString(YOU, DefaultFont, DefaultSentColor, false);
                
                items.add(YouString);
                
                break;
            }
            case Recieved:
            {
                // add the time stamp
                if(timeStampString == null)
                {
                    timeStampString = new ItemString("(" + sdf.format(new Date(timeStamp)) + ") ", DefaultFont, DefaultInfoColor, false);
                }
                items.add(0, timeStampString);

                // add nickname text etc...
                from = from + ": ";

                if(YouString == null)
                    YouString = new ItemString(from, DefaultFont, DefaultRecievedColor, false);
                
                items.add(YouString);
                
                break;
            }
        }
        
        From = from;
        txType = type;
    }
    
    /** Constructs a new ChatMessage */
    public ChatMessage(String from, MessageTxType type, ArrayList<Item> i, long time)
    {
        this(from, type, time);
        
        items.addAll(i);
    }
    
    /** Constructs a new ChatMessage with custom from Items */
    private ChatMessage(ArrayList<Item> from, String fromText, MessageTxType type, long time)
    {
        timeStamp = time;
        items = new ArrayList<Item>();
        
        switch(type)
        {
            case Sent:
            {
                if(YouString == null)
                    YouString = new ItemString(YOU, DefaultFont, DefaultSentColor, false);
                
                items.add(YouString);
                
                break;
            }
            case Recieved:
            {
                // add the time stamp
                if(timeStampString == null)
                {
                    timeStampString = new ItemString("(" + sdf.format(new Date(timeStamp)) + ") ", DefaultFont, DefaultInfoColor, false);
                }
                from.add(0, timeStampString);

                // add the colon
                if(colonItem==null)
                {
                    colonItem = new ItemString(": ");
                    colonItem.font = DefaultFont;
                    colonItem.color = DefaultRecievedColor;
                }

                from.add(colonItem);
                
                for(Item i : from)
                    items.add(i);
                
                break;
            }
        }
        
        From = fromText;
        txType = type;
    }
    
    /** Constructs a new ChatMessage */
    public ChatMessage(ArrayList<Item> nickitems, String from, MessageTxType type, ArrayList<Item> i, long time)
    {
        this(nickitems, from, type, time);
        
        items.addAll(i);
    }
    
    /** Create a new chat message used for info messages */
    public ChatMessage(String info)
    {        
        if(YouString == null)
            YouString = new ItemString(info, DefaultFont, DefaultInfoColor, false);

        items = new ArrayList<Item>();
        
        items.add(YouString);
    }
    
    /** Appends a new Item to the end of this ChatMessage */
    public void AppendItem(Item item)
    {
        items.add(item);
    }
    
    /** Appends a new ItemString of plain text to the end of this ChatMessage */
    public void AppendText(String text)
    {
        items.add(new ItemString(text));
    }
    
    /** Appends a new ItemString to the end of this ChatMessage with formatting */
    public void AppendText(String text, Font f, Color c, boolean underlined)
    {
        items.add(new ItemString(text, f, c, underlined));
    }
}