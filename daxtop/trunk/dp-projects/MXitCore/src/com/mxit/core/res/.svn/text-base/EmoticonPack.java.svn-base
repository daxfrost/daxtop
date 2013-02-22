package com.mxit.core.res;

import java.awt.Image;
import java.util.LinkedHashMap;

/**
 * @author Dax Booysen
 * 
 * A pack that stores a set of emoticons
 */
public class EmoticonPack
{
    /** EmoticonPack name */
    public String Name;

    /** Emoticons */
    public LinkedHashMap<String[], Image> emoticons;
    
    /** Constructs a new EmoticonPack */
    public EmoticonPack(String Name)
    {
        this.Name = Name;
        emoticons = new LinkedHashMap<String[], Image>();
    }
    
    /** Adds an emoticon to the pack */
    public void AddEmoticon(String[] texts, Image img)
    {
        emoticons.put(texts, img);
    }
    
    /** Returns an emoticon if its found in the pack */
    public Image ParseEmoticon(String text)
    {
        for(String[] emoTexts : emoticons.keySet())
        {
            for(String emoText : emoTexts)
            {
                if(emoText.equals(text))
                    return emoticons.get(emoTexts);
            }
        }
        
        return null;
    }
}
