package com.mxit.core.res;

import com.dax.daxtop.res.ResourceLoader;
import com.dax.daxtop.res.ResourceManager;
import com.mxit.MXitNetwork;
import com.mxit.core.model.Mood;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;

/**
 * @author Dax Booysen
 */
public final class MXitRes extends ResourceLoader
{
    /** MXit resources have all been loaded */
    public boolean loadComplete = false;

    public MXitNetwork mxitNetwork = null;

    /** Constructor */
    public MXitRes(MXitNetwork network)
    {
        // set the network
        mxitNetwork = network;

        // load the statuses
        LoadImages("images/");

        // load completed
        loadComplete = true;
    }
    // MXit Icon
    public static ImageIcon IMG_MAIN_MENU_MXIT;
    /** Emoticon Packs */
    public LinkedHashMap<String, EmoticonPack> emoticons;
    /** Moods */
    public TreeMap<Mood, Image> Moods;
    // chat screen images
    public Image IMG_BTN_EMOTICON;
    public static Image IMG_BTN_FONT;
    public static Image IMG_BTN_MXIT;
    // All The Status Images
    public static ImageIcon IMG_STATUS_ONLINE;
    public static ImageIcon IMG_STATUS_OFFLINE;
    public static ImageIcon IMG_STATUS_AWAY;
    public static ImageIcon IMG_STATUS_DND;
    public static ImageIcon IMG_STATUS_INVITE;
    // All The Messaging Images
    public static ImageIcon IMG_UNREAD_MESSAGES;
    public static ImageIcon IMG_READ_MESSAGES;
    // unset avatar image
    public static ImageIcon IMG_AVATAR_EMPTY;
    public static ImageIcon IMG_AVATAR_MULTIMX;
    // default avatar
    public static ImageIcon IMG_DEFAULT_AVATAR;
    // The chat zone commands
    public static ArrayList<String> chatZoneCommands;

    /** Loads the image icons into memory */
    public void LoadImages(String rootDir)
    {
        // only load once
        if (IMG_STATUS_ONLINE==null)
        {
            // status
            IMG_STATUS_ONLINE = new ImageIcon(LoadImage(rootDir, "status/status_online.png"));
            IMG_STATUS_OFFLINE = new ImageIcon(LoadImage(rootDir, "status/status_offline.png"));
            IMG_STATUS_AWAY = new ImageIcon(LoadImage(rootDir, "status/status_away.png"));
            IMG_STATUS_DND = new ImageIcon(LoadImage(rootDir, "status/status_dnd.png"));
            IMG_STATUS_INVITE = new ImageIcon(LoadImage(rootDir, "status/status_invite.png"));

            // messaging
            IMG_UNREAD_MESSAGES = new ImageIcon(LoadImage(rootDir, "standard/message_icon_unread.png"));
            IMG_READ_MESSAGES = new ImageIcon(LoadImage(rootDir, "standard/message_icon_read.png"));
            IMG_DEFAULT_AVATAR = new ImageIcon(LoadImage(rootDir, "standard/defaultavatar.png"));
            IMG_MAIN_MENU_MXIT = new ImageIcon(LoadImage(rootDir, "menu/menu_mxit.png"));

            // unset avatar image
            IMG_AVATAR_EMPTY = new ImageIcon(LoadImage(rootDir, "standard/empty_avatar.png"));
            IMG_AVATAR_MULTIMX = new ImageIcon(LoadImage(rootDir, "standard/multimx_avatar.png"));

            // load chat screen button images
            IMG_BTN_FONT = LoadImage(rootDir, "standard/button_font.png");
            IMG_BTN_MXIT = LoadImage(rootDir, "standard/button_mxit.png");
        }

        // only load once
        if (emoticons==null)
        {
            // emoticons
            emoticons = new LinkedHashMap<String, EmoticonPack>();
            // default emoticon pack
            LoadDefaultEmoticonPack("", "emoNew.png");
        }
    }

    /** Loads the latest chat zone commands */
    public static void LoadChatZoneCommands(MXitNetwork network)
    {
        try
        {
            // already loaded
            if (chatZoneCommands != null)
                return;

            chatZoneCommands = new ArrayList<String>();

            // load the chat zone data file
            FileReader fr = OpenLocalDataFileReader(network, "CZD.dat");

            // the file does not exist, don't load
            if(fr == null)
                return;

            BufferedReader br = new BufferedReader(fr);

            String command = "";

            while ((command = br.readLine()) != null && !command.isEmpty())
            {
                // load a command
                chatZoneCommands.add(command);
            }

            // sort alphabetically
            Collections.sort(chatZoneCommands);

            br.close();
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /** Loads an emoticon pack */
    public void LoadDefaultEmoticonPack(String rootDir, String fileName)
    {
        Image pack = LoadImage(rootDir, "emo/" + fileName);

        int emotiSize = pack.getHeight(null);
        int count = pack.getWidth(null) / emotiSize;

        ArrayList<String[]> emoText = new ArrayList<String[]>();

        String[] happy = new String[] { ":)", ":-)" };
        String[] unhappy = new String[] { ":(", ":-(" };
        String[] winking = new String[] { ";)", ";-)" };
        String[] excited = new String[] { ":D", ":-D", ":>", ":->" };
        String[] shocked = new String[] { ":|", ":-|" };
        String[] surprised = new String[] { ":O", ":-O" };
        String[] smileTongue = new String[] { ":P", ":-P" };
        String[] embarrased = new String[] { ":$", ":-$" };
        String[] cool = new String[] { "8)", "8-)" };
        String[] heart = new String[] { "(H)" };
        String[] flower = new String[] { "(F)" };
        String[] male = new String[] { "(m)" };
        String[] female = new String[] { "(f)" };
        String[] star = new String[] { "(*)" };
        String[] hot = new String[] { "(c)" };
        String[] kiss = new String[] { "(x)" };
        String[] idea = new String[] { "(i)" };
        String[] angry = new String[] { ":e", ":-e" };
        String[] censored = new String[] { ":x", ":-x" };
        String[] grumpy = new String[] { "(z)" };
        String[] coffee = new String[] { "(U)" };
        String[] mrgreen = new String[] { "(G)" };
        String[] sick = new String[] { ":o(" };
        String[] astonished = new String[] { ":{", ":-{" };
        String[] inlove = new String[] { ":}", ":-}" };
        String[] rollingeyes = new String[] { "8o", "8-o" };
        String[] crying = new String[] { ":'(" };
        String[] thinking = new String[] { ":?", ":-?" };
        String[] drooling = new String[] { ":~", ":-~" };
        String[] sleepy = new String[] { ":z", ":-z" };
        String[] liar = new String[] { ":L)" };
        String[] nerdy = new String[] { "8|", "8-|" };
        String[] pirate = new String[] { "P-)" };

        emoText.add(happy);
        emoText.add(unhappy);
        emoText.add(winking);
        emoText.add(excited);
        emoText.add(shocked);
        emoText.add(surprised);
        emoText.add(smileTongue);
        emoText.add(embarrased);
        emoText.add(cool);
        emoText.add(heart);
        emoText.add(flower);
        emoText.add(male);
        emoText.add(female);
        emoText.add(star);
        emoText.add(hot);
        emoText.add(kiss);
        emoText.add(idea);
        emoText.add(angry);
        emoText.add(censored);
        emoText.add(grumpy);
        emoText.add(coffee);
        emoText.add(mrgreen);
        emoText.add(sick);
        emoText.add(astonished);
        emoText.add(inlove);
        emoText.add(rollingeyes);
        emoText.add(crying);
        emoText.add(thinking);
        emoText.add(drooling);
        emoText.add(sleepy);
        emoText.add(liar);
        emoText.add(nerdy);
        emoText.add(pirate);

        EmoticonPack emopack = null;

        if (emoticons.containsKey("default"))
        {
            emopack = emoticons.get("default");
            emopack.emoticons.clear();
        }
        else
        {
            emopack = new EmoticonPack("default");
        }

        for(int i = 0; i < count; i++)
        {
            Image img = new BufferedImage(emotiSize, emotiSize, BufferedImage.TYPE_INT_ARGB);
            Graphics g = img.getGraphics();
            g.drawImage(pack, (i*emotiSize)*-1, 0, null);

            emopack.AddEmoticon(emoText.get(i), img);
        }

        emoticons.put("default", emopack);

        IMG_BTN_EMOTICON = emoticons.get("default").ParseEmoticon(":D");

        // load the moods
        LoadMoods();
    }

    private void LoadMoods()
    {
        Moods = new TreeMap<Mood, Image>();

        EmoticonPack pack = emoticons.get("default");

        // moods
        Moods.put(Mood.None, pack.ParseEmoticon("(i)"));
        Moods.put(Mood.Angry, pack.ParseEmoticon(":e"));
        Moods.put(Mood.Excited, pack.ParseEmoticon(":D"));
        Moods.put(Mood.Grumpy, pack.ParseEmoticon("(z)"));
        Moods.put(Mood.Happy, pack.ParseEmoticon(":)"));
        Moods.put(Mood.InLove, pack.ParseEmoticon(":}"));
        Moods.put(Mood.Invincible, pack.ParseEmoticon("8)"));
        Moods.put(Mood.Sad, pack.ParseEmoticon(":("));
        Moods.put(Mood.Hot, pack.ParseEmoticon("(c)"));
        Moods.put(Mood.Sick, pack.ParseEmoticon(":o("));
        Moods.put(Mood.Sleepy, pack.ParseEmoticon(":z"));
    }

    /** Play MXit sound */
    public static synchronized void PlaySound(MXitNetwork network, Clip clip)
    {
        if (!network.CurrentProfile.MuteSound)
        {
            ResourceManager.audioPlayer.Play(clip);
        }
    }
}
