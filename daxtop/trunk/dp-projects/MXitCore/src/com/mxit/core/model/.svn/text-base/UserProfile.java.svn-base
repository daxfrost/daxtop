package com.mxit.core.model;

import com.dax.Profile;
import com.mxit.MXitNetwork;
import com.mxit.core.encryption.AES;
import com.mxit.core.model.type.MXitPresence;
import java.awt.Image;
import java.util.TreeMap;
import javax.swing.ImageIcon;

/**
 * @author Dax Booysen
 * 
 * This class is used to specify login details for MXit logins.
 * It can be saved and reloaded through MXitManager functions.
 */
public class UserProfile
{
    public Profile profile;

    // not accessible.
    public UserProfile(Profile prof)
    {
        profile = prof;
        LoadProfile();
    }

    /** Creates a UserProfile with username */
    public UserProfile(String username)
    {
        MXitID = username;
        profile = new Profile(MXitNetwork.networkId, MXitID);
    }

    /** Creates a UserProfile with username and password */
//    public UserProfile(String username, String password)
//    {
//        MXitID = username;
//        HashPin = HashPin(password);
//    }
    // member variables
    public String MXitID = "";
    public String HashPin = null;//"LC4T83zDjpyN3Dsw+Rivhbb4fK1wmMP5GVEEmVUJd6A=";//HashPin("741852963", MXitID);
    public String Nickname = null;
    public String Locale = "";
    public String DialingCode = "";
    public String ProductId = "";//"2416B7FC64-FD26-40EE-B1BE-6F4437525B7995927719";//"243469369D-81DC-4AEC-BBDF-DD93113D17E05B214976";
    public String CountryCode = "";
    public String NPF = "";
    public String IPF = "";
    public String AvatarId = "";
    public ImageIcon Avatar = null;
    public byte mood = 0;
    public byte presence = 1;
    public byte DefaultEmoticonPack = 0;
    /** The user avatars */
    public TreeMap<String, ImageIcon> avatars = new TreeMap<String, ImageIcon>();
    public boolean RememberPassword;
    public boolean HideOffline;
    public boolean ToasterAlerts = true;
    public boolean PresenceToasterAlerts = true;
    public boolean MuteSound = false;
    public boolean InlineMarkup = false;
    // private variables
    private String _distCode = null;
    private String _clientKey = null;
    private String _socket1 = null;     // address:port
    private String _socket2 = null;
    private String _httpConn1 = null;
    private String _httpConn2 = null;
    private String[] _splashIds = null;
    // constants
    private final int DIST_LENGTH = 2;
    // constants for config
    private static final String _MSISDN = "msisdn";
    private static final String _PIN = "pin";
    private static final String _AUTO_LOGIN = "autoLogin";
    private static final String _AUTO_AWAY = "autoAway";
    private static final String _REMEMBER_PIN = "remPin";
    private static final String _NICKNAME = "nickname";
    private static final String _HIDE_OFFLINE = "hideOffline";
    private static final String _LOCALE = "locale";
    private static final String _AVATAR_ID = "avatarId";
    private static final String _AVATAR_DATA = "avatar_data";
    private static final String _DIALING_CODE = "dialingCode";
    private static final String _PRODUCT_ID = "productId";
    private static final String _SOCKET1 = "socket1";
    private static final String _SOCKET2 = "socket2";
    private static final String _HTTP1 = "http1";
    private static final String _HTTP2 = "http2";
    private static final String _COUNTRY_CODE = "countryCode";
    private static final String _NPF = "npf";
    private static final String _IPF = "ipf";
    private static final String _AVATARS = "contact_avatars";
    private static final String _MOOD = "mood";
    private static final String _PRESENCE = "presence";
    private static final String _DEFAULT_EMOTICON_PACK = "defaultEmoPack";
    private static final String _TOASTER_ALERTS = "toasterAlert";
    private static final String _PRESENCE_TOASTER_ALERTS = "presenceToasterAlert";
    private static final String _MUTE_SOUND = "muteSound";
    private static final String _FILTER_MARKUP = "filterMarkup";
    private static final String _SPLASH_IDS = "splashIds";

    /** Used to reload the profile data from file, returns false if no data file found */
    public void LoadProfile()
    {
        MXitID = (String) profile.configurationData.get(_MSISDN);
        HashPin = (String) profile.configurationData.get(_PIN);
        Nickname = (String) profile.configurationData.get(_NICKNAME);
        Locale = (String) profile.configurationData.get(_LOCALE);
        DialingCode = (String) profile.configurationData.get(_DIALING_CODE);
        ProductId = (String) profile.configurationData.get(_PRODUCT_ID);
        _socket1 = (String) profile.configurationData.get(_SOCKET1);
        _socket2 = (String) profile.configurationData.get(_SOCKET2);
        _httpConn1 = (String) profile.configurationData.get(_HTTP1);
        _httpConn2 = (String) profile.configurationData.get(_HTTP2);
        CountryCode = (String) profile.configurationData.get(_COUNTRY_CODE);
        NPF = (String) profile.configurationData.get(_NPF);
        IPF = (String) profile.configurationData.get(_IPF);
        AvatarId = (String) profile.configurationData.get(_AVATAR_ID);
        Avatar = (ImageIcon) profile.configurationData.get(_AVATAR_DATA);
        RememberPassword = (Boolean) profile.configurationData.get(_REMEMBER_PIN);
        HideOffline = (Boolean) profile.configurationData.get(_HIDE_OFFLINE);
        avatars = ((TreeMap<String, ImageIcon>) profile.configurationData.get(_AVATARS));
        avatars = avatars == null ? new TreeMap<String,ImageIcon>() : avatars ;
        mood = ((Byte)profile.configurationData.get(_MOOD));
        presence = ((Byte)profile.configurationData.get(_PRESENCE));
        DefaultEmoticonPack = ((Byte)profile.configurationData.get(_DEFAULT_EMOTICON_PACK));
        ToasterAlerts = (Boolean) profile.configurationData.get(_TOASTER_ALERTS);
        MuteSound = (Boolean) profile.configurationData.get(_MUTE_SOUND);

        // all new parameters must be checked for
        if(profile.configurationData.containsKey(_FILTER_MARKUP))
            InlineMarkup = (Boolean) profile.configurationData.get(_FILTER_MARKUP);
        if(profile.configurationData.containsKey(_SPLASH_IDS))
            _splashIds = (String[]) profile.configurationData.get(_SPLASH_IDS);
        if(profile.configurationData.containsKey(_PRESENCE_TOASTER_ALERTS))
            PresenceToasterAlerts = (Boolean) profile.configurationData.get(_PRESENCE_TOASTER_ALERTS);
    }

    /** Used to save the data to the profile, and return it for saving */
    public synchronized Profile SaveProfile()
    {
        profile.configurationData.put(_MSISDN, MXitID);
        profile.configurationData.put(_PIN, HashPin);
        profile.configurationData.put(_NICKNAME, Nickname);
        profile.configurationData.put(_LOCALE, Locale);
        profile.configurationData.put(_DIALING_CODE, DialingCode);
        profile.configurationData.put(_PRODUCT_ID, ProductId);
        profile.configurationData.put(_SOCKET1, _socket1);
        profile.configurationData.put(_SOCKET2, _socket2);
        profile.configurationData.put(_HTTP1, _httpConn1);
        profile.configurationData.put(_HTTP2, _httpConn2);
        profile.configurationData.put(_COUNTRY_CODE, CountryCode);
        profile.configurationData.put(_NPF, NPF);
        profile.configurationData.put(_IPF, IPF);
        profile.configurationData.put(_AVATAR_ID, AvatarId);
        profile.configurationData.put(_AVATAR_DATA, Avatar);
        profile.configurationData.put(_REMEMBER_PIN, RememberPassword);
        profile.configurationData.put(_HIDE_OFFLINE, HideOffline);
        profile.configurationData.put(_AVATARS, avatars);
        profile.configurationData.put(_MOOD, mood);
        profile.configurationData.put(_PRESENCE, presence);
        profile.configurationData.put(_DEFAULT_EMOTICON_PACK, DefaultEmoticonPack);
        profile.configurationData.put(_TOASTER_ALERTS, ToasterAlerts);
        profile.configurationData.put(_MUTE_SOUND, MuteSound);
        profile.configurationData.put(_FILTER_MARKUP, InlineMarkup);
        profile.configurationData.put(_SPLASH_IDS, _splashIds);
        profile.configurationData.put(_PRESENCE_TOASTER_ALERTS, PresenceToasterAlerts);

        return profile;
    }

    /** Gets the nickname but if null falls back to user id */
    public String getNickOrMXitID()
    {
        return Nickname == null ? MXitID : Nickname;
    }

    /** Gets the Distribution Code */
    public String getDistributionCode()
    {
        if (_distCode == null)
        {
            int len = Integer.parseInt(ProductId.substring(0, DIST_LENGTH), 16);
            _distCode =
                    ProductId.substring(DIST_LENGTH, len + DIST_LENGTH);
        }

        return _distCode;
    }

    /** Gets the Client Key */
    public String getClientKey()
    {
        if (_clientKey == null)
        {
            int len = Integer.parseInt(ProductId.substring(0, DIST_LENGTH), 16);
            _clientKey =
                    ProductId.substring(DIST_LENGTH + len);
        }

        return _clientKey;
    }

    /** Encrypt the pin */
    public String HashPin(
            String pin)
    {
        try
        {
            HashPin = AES.aes_process(true, getClientKey(), pin);
            return HashPin;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /** Returns the main socket address */
    public String GetSocket()
    {
        return _socket1;
    }

    /** Returns the secondary socket address, usually a dns string */
    public String GetSecondSocket()
    {
        return _socket2;
    }

    /** Sets the main socket address */
    public void SetSocket(String value)
    {
        _socket1 = value;
    }

    /** Sets the secondary socket address, usually a dns string */
    public void SetSecondSocket(String value)
    {
        _socket2 = value;
    }

    /** Returns the main http address */
    public String GetHttp()
    {
        return _httpConn1;
    }

    /** Returns the secondary http address */
    public String GetSecondHttp()
    {
        return _httpConn2;
    }

    /** Sets the main http address */
    public void SetHttp(String value)
    {
        _httpConn1 = value;
    }

    /** Sets the secondary http address */
    public void SetSecondHttp(String value)
    {
        _httpConn2 = value;
    }

    /** Gets the splash ids */
    public String[] GetSplashIds()
    {
        return _splashIds == null ? new String[0] : _splashIds;
    }

    /** Sets the splash ids */
    public void SetSplashIds(String[] splashIds)
    {
        _splashIds = splashIds;
    }
}
