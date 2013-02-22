package com.mxit.core.model;

/**
 * @author Dax Frost
 *
 * MXit Moods
 */
public enum Mood
{
    None (0),
    Angry (1),
    Excited (2),
    Grumpy (3),
    Happy (4),
    InLove (5),
    Invincible (6),
    Sad (7),
    Hot (8),
    Sick (9),
    Sleepy (10);

    public static Mood indexOf(int index)
    {
        switch(index)
        {
            case 0:
                return None;
            case 1:
                return Angry;
            case 2:
                return Excited;
            case 3:
                return Grumpy;
            case 4:
                return Happy;
            case 5:
                return InLove;
            case 6:
                return Invincible;
            case 7:
                return Sad;
            case 8:
                return Hot;
            case 9:
                return Sick;
            case 10:
                return Sleepy;
            default:
                return None;
        }
    }

    private final int index;

    /** Mood */
    Mood(int index)
    {
        this.index = index;
    }

    public int getIndex()
    {
        return index;
    }

    /** Returns the UI text for this mood */
    public String GetText()
    {
        switch(index)
        {
            case 0:
                return "None";
            case 1:
                return "Angry";
            case 2:
                return "Excited";
            case 3:
                return "Grumpy";
            case 4:
                return "Happy";
            case 5:
                return "In Love";
            case 6:
                return "Invincible";
            case 7:
                return "Sad";
            case 8:
                return "Hot";
            case 9:
                return "Sick";
            case 10:
                return "Sleepy";
            default:
                return "None";
        }
    }
}
