package com.mxit.core.activation;

import com.mxit.core.encryption.AES;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

/**
 * @author Dax Booysen
 * 
 * MXitActivationChallenge object will hold an activation challenge response
 */
public class MXitActivationChallenge
{
    private String url;
    private String sessionId;
    private Image captcha = null;
    private HashMap<String, String> countries = new HashMap<String, String>();
    private HashMap<String, String> languages = new HashMap<String, String>();
    private String defaultCountryName = "";
    private String defaultCountryCode = "";
    private String[] regions;
    private String defaultDialingCode = "";
    private String defaultRegion = "";
    private String defaultNPF = "";
    private String defaultIPF = "";
    private String[]  cities;
    private String defaultCity = "";

    /** Constructor */
    public MXitActivationChallenge()
    {
    }
    
    /** Sets the captcha image */
    public void setCaptcha(String data)
    {
        try
        {
            captcha = Toolkit.getDefaultToolkit().createImage(AES.base64_decode(data));
        }
        catch(IOException ioe)
        {
            System.out.println("io exception when decoding base64 encoded string");
        }
    }
    
    /** Gets the image for the challenge */
    public Image getCaptcha()
    {
        return captcha;
    }
    
    public String[] getCities()
    {
        return cities;
    }

    public HashMap<String, String> getCountries()
    {
        return countries;
    }

    public String getDefaultCity()
    {
        return defaultCity;
    }

    public String getDefaultCountryCode()
    {
        return defaultCountryCode;
    }

    public String getDefaultCountryName()
    {
        return defaultCountryName;
    }

    public String getDefaultDialingCode()
    {
        return defaultDialingCode;
    }

    public String getDefaultIPF()
    {
        return defaultIPF;
    }

    public String getDefaultNPF()
    {
        return defaultNPF;
    }

    public String getDefaultRegion()
    {
        return defaultRegion;
    }

    public HashMap<String, String> getLanguages()
    {
        return languages;
    }

    public String[] getRegions()
    {
        return regions;
    }

    public String getSessionId()
    {
        return sessionId;
    }

    public String getUrl()
    {
        return url;
    }
    
    public void setCities(String cities)
    {
        this.cities = cities.split("\\|");
    }

    public void setCountries(String countries)
    {
        String[] pair = countries.split("\\,");
        
        // cycle through key value pairs of "countrycode|country"
        for(String p : pair)
        {
            String[] kvp = p.split("\\|");

            this.countries.put(kvp[0], kvp[1]);
        }
    }

    public void setDefaultCity(String defaultCity)
    {
        this.defaultCity = defaultCity;
    }

    public void setDefaultCountryCode(String defaultCountryCode)
    {
        this.defaultCountryCode = defaultCountryCode;
    }

    public void setDefaultCountryName(String defaultCountryName)
    {
        this.defaultCountryName = defaultCountryName;
    }

    public void setDefaultDialingCode(String defaultDialingCode)
    {
        this.defaultDialingCode = defaultDialingCode;
    }

    public void setDefaultIPF(String defaultIPF)
    {
        this.defaultIPF = defaultIPF;
    }

    public void setDefaultNPF(String defaultNPF)
    {
        this.defaultNPF = defaultNPF;
    }

    public void setDefaultRegion(String defaultRegion)
    {
        this.defaultRegion = defaultRegion;
    }

    public void setLanguages(String languages)
    {
        String[] pair = languages.split("\\,");
        
        // cycle through key value pairs of "countrycode|country"
        for(String p : pair)
        {
            String[] kvp = p.split("\\|");

            this.languages.put(kvp[0], kvp[1]);
        }
    }

    public void setRegions(String regions)
    {
        this.regions = regions.split("\\|");
    }

    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }
}
