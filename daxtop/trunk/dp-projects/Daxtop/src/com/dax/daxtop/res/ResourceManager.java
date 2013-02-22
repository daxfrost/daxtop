package com.dax.daxtop.res;

import com.dax.lib.audio.AudioPlayer;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.net.URL;
import java.util.HashMap;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * @author Dax Booysen
 * @company MXit Lifestyle
 * ResourceManager will manage and load all media resources.
 */
public final class ResourceManager extends ResourceLoader
{
    /** The audio player used for notifications */
    public static AudioPlayer audioPlayer = new AudioPlayer();

    /** The generic list that holds all the language strings for the client */
    private static HashMap langPack = new HashMap();
    
    // All The Language Pack Key Values
    public static String STR_CONTACTS_TITLE = "contacts_title";
    public static String STR_CHATS_TITLE = "chat_screen_title";
    
    /** daxtop Application Images */
    public static Image IMG_DAXTOP_ICON_16;
    public static Image IMG_DAXTOP_ICON;
    public static Image IMG_DAXTOP_LOGO;
    public static Image IMG_NOTIFICATION_NORMAL, IMG_NOTIFICATION_SMALL;

    /** daxtop loader images */
    public static Image[] loader_images;

    /** daxtop Main Menu Options */
    public static Image IMG_MAIN_MENU_DAXTOP;

    /** Daxtop Application Audio */
    private static URL AUDIO_NEW_MESSAGE;
    private static URL AUDIO_NEW_MULTIMEDIA_MESSAGE;
    private static URL AUDIO_CONTACT_ONLINE;
    private static URL AUDIO_CONTACT_OFFLINE;

    /** Audio Clips */
    public static Clip SOUND_NEW_MESSAGE;
    public static Clip SOUND_NEW_MULTIMEDIA_MESSAGE;
    public static Clip SOUND_CONTACT_ONLINE;
    public static Clip SOUND_CONTACT_OFFLINE;

    /** default font for chat screen */
    public static Font FONT_CHAT_SCREEN;
    
    /** default chat screen text color */
    public static Color COLOR_CHAT_TEXT;
    
    /** Singleton class instance */
    public static final ResourceManager Instance = new ResourceManager();

    /** Constructs a new ResourceManager */
    private ResourceManager()
    {
    }

    /** Load default daxtop resources */
    public void LoadResources()
    {
        // TEMP CODE UNTIL LANG PACK HAS BEEN MADE
        langPack.put(STR_CONTACTS_TITLE, "Contacts - daxtop");
        langPack.put(STR_CHATS_TITLE, "Chats - daxtop");

        // load application icon and logo
        IMG_DAXTOP_ICON_16 = LoadImage("", "app_icon_small.png");
        IMG_DAXTOP_ICON = LoadImage("", "app_icon.png");
        IMG_DAXTOP_LOGO = LoadImage("", "app_logo.png");

        // load the notification image
        IMG_NOTIFICATION_SMALL  = LoadImage("", "app_icon_small_notification.png");
        IMG_NOTIFICATION_NORMAL = LoadImage("", "app_icon_notification.png");

        // load font
        FONT_CHAT_SCREEN = new Font("Verdana", Font.PLAIN, 12);

        // load colors
        COLOR_CHAT_TEXT = Color.BLACK;

        // default standard images
        LoadStandardIcons("");

        // default standard audio
        LoadStandardAudio("");

        // default loader icons
        LoadDefaultLoaderImages();
    }

    /** Gets the value of the text using the key */
    public String GetTextFor(String key)
    {
        return String.valueOf(langPack.get(key));
    }

    /** Loads the default audio files */
    private void LoadStandardAudio(String rootDir)
    {
        // load file urls
        AUDIO_CONTACT_OFFLINE = LoadFile(this.getClass(), rootDir, "notifications/contact_offline.wav");
        AUDIO_CONTACT_ONLINE = LoadFile(this.getClass(), rootDir, "notifications/contact_online.wav");
        AUDIO_NEW_MESSAGE = LoadFile(this.getClass(), rootDir, "notifications/new_message.wav");
        AUDIO_NEW_MULTIMEDIA_MESSAGE = LoadFile(this.getClass(), rootDir, "notifications/multimedia_message.wav");

        // load clips
        SOUND_NEW_MESSAGE = audioPlayer.GetClip(AUDIO_NEW_MESSAGE);
        SOUND_NEW_MULTIMEDIA_MESSAGE = audioPlayer.GetClip(AUDIO_NEW_MULTIMEDIA_MESSAGE);
        SOUND_CONTACT_ONLINE = audioPlayer.GetClip(AUDIO_CONTACT_ONLINE);
        SOUND_CONTACT_OFFLINE = audioPlayer.GetClip(AUDIO_CONTACT_OFFLINE);
    }
    
    /** Loads a specific input stream of audio */
    public static Clip LoadAudio(java.io.InputStream stream) throws Exception
    {
        return audioPlayer.GetClip(stream);
    }

    /** Loads the standard icons into memory */
    private void LoadStandardIcons(String rootDir)
    {
        // load main menu images
        IMG_MAIN_MENU_DAXTOP = IMG_DAXTOP_ICON;
    }

    private void LoadDefaultLoaderImages()
    {
        loader_images = new Image[] { LoadImage("","loader/load1.png"),
                               LoadImage("","loader/load2.png"),
                               LoadImage("","loader/load3.png"),
                               LoadImage("","loader/load4.png"),
                               LoadImage("","loader/load5.png"),
                               LoadImage("","loader/load6.png"),
                               LoadImage("","loader/load7.png"),
                               LoadImage("","loader/load8.png"),
                               LoadImage("","loader/load9.png"),
                               LoadImage("","loader/load10.png")
                             };
    }
}


