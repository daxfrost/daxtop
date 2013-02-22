package com.mxit.core.activation;

import com.dax.Main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author Dax Booysen
 * 
 * This class has the sole purpose of activating a user on the MXit service
 */
public final class MXitActivation
{
    // dev:  "http://196.211.215.27/res/?type=challenge&getcountries=true&getlanguage=true&getimage=true&ts=";
    // prod: "http://www.mxit.com/res/?type=challenge&getcountries=true&getlanguage=true&getimage=true&ts=";
    private static final String CHALLENGE_URL = "http://www.mxit.com/res/?type=challenge&getcountries=true&getlanguage=true&clientid=DP&getimage=true&ts=";
    private static final String ACTIVATION_URL = "<URL>/?type=getpid&sessionid=<SESSION_ID>&ver=5.8.2&clientid=DP&login=<MSISDN>&cat=<CATEGORY>&chalresp=<CAPTCHA>&cc=<COUNTRY>&loc=<LOCALE>&path=<LOGIN>";
    
    /** Gets the activation challenge for activating a MXit user PID, returns null if challenge could not be fetched! */
    public static MXitActivationChallenge GetChallenge() throws IOException
    {
        URL url;

        // check for activation url override
        if (Main.OverrideArguments.containsKey("mxit:activationurl"))
        {
            String address = Main.OverrideArguments.get("mxit:activationurl");

            if (!address.isEmpty())
            {
                // make sure the address starts with "http://"
                if (!address.startsWith("http://"))
                    address = "http://" + address;

                // make sure the address has parameters
                if (address.endsWith("/res/"))
                    address = address + "?type=challenge&getcountries=true&getlanguage=true&clientid=DP&getimage=true&ts=" + System.currentTimeMillis();
                else if (address.endsWith("/"))
                    address = address + "res/?type=challenge&getcountries=true&getlanguage=true&clientid=DP&getimage=true&ts=" + System.currentTimeMillis();
                else if (!address.endsWith("&ts="))
                    address = address + "/res/?type=challenge&getcountries=true&getlanguage=true&clientid=DP&getimage=true&ts=" + System.currentTimeMillis();

                url = new URL(address);
            }
            else
            {
                // fall back to standard url
                url = new URL(CHALLENGE_URL + System.currentTimeMillis());
            }
        }
        else
            url = new URL(CHALLENGE_URL + System.currentTimeMillis());


        // do request
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
        String resp;

        if((resp = in.readLine()) != null) 
        {
            return _parseChallenge(resp);
        }
        else
            return null;
    }

    /** parse the response of the challenge request */
    private static MXitActivationChallenge _parseChallenge(String response)
    {
        // if there was an error return null
        if(!response.startsWith("0"))
            return null;
        
        MXitActivationChallenge activation = new MXitActivationChallenge();

        String[] fields = response.split(";");

        // retrieve the values from the fields
        activation.setUrl(fields[1]);
        activation.setSessionId(fields[2]);
        activation.setCaptcha(fields[3]);
        activation.setCountries(fields[4]);
        activation.setLanguages(fields[5]);
        activation.setDefaultCountryName(fields[6]);
        activation.setDefaultCountryCode(fields[7]);
        activation.setRegions(fields[8]);
        activation.setDefaultDialingCode(fields[9]);
        activation.setDefaultRegion(fields[10]);
        activation.setDefaultNPF(fields[11]);
        activation.setDefaultIPF(fields[12]);
        if(fields.length > 14)
        {
            activation.setCities(fields[13]);         // cities not implemented yet
            activation.setDefaultCity(fields[14]);
        }
        
        return activation;
    }

    /** Attempt the challenge */
    public static MXitActivationResult Activate(MXitActivationRequest request) throws IOException
    {
        MXitActivationChallenge mac = request.getChallenge();

        // build http request url for activation
        String address = ACTIVATION_URL.replace("<URL>", mac.getUrl());
        address = address.replace("<SESSION_ID>", mac.getSessionId());
        address = address.replace("<MSISDN>", request.getMsisdn());
        address = address.replace("<CATEGORY>", request.getCategory());
        address = address.replace("<CAPTCHA>", request.getCaptcha());
        address = address.replace("<COUNTRY>", request.getCountry());
        address = address.replace("<LOCALE>", request.getLocaleLanguage());
        address = address.replace("<LOGIN>", request.getIsLogin() ? "1" : "0");

        // send request
        URL url = new URL(address);
        System.out.println("url = " + url);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
        String resp;
        
        if((resp = in.readLine()) != null)
        {
            MXitActivationResult result = _parseActivation(resp);
            result.request = request;
            return result;
        }
        else
            return null;
    }

    /** parse the response of the getpid activation request */
    private static MXitActivationResult _parseActivation(String response)
    {
        MXitActivationResult mar = new MXitActivationResult();

        // if there was an error return null
        if(!response.startsWith("0"))
            mar.setSuccess(false);

        String[] fields = response.split(";");

        // set the error code
        mar.setErrorCode(Short.parseShort(fields[0]));
        
        switch(Integer.parseInt(fields[0]))
        {
            case 1:
            {
                // new captcha answer
                mar.setCaptcha(fields[1]);
                break;
            }
            case 2:
            {
                // new session id and captcha cause expired
                if(fields.length>2)
                {
                    mar.setSessionId(fields[1]);
                    mar.setCaptcha(fields[2]);
                }
                else
                    mar.setCaptcha(fields[1]);
                 
                break;
            }
            case 4:
            {
                // critical error
                mar.setDomain(fields[1]);
                break;
            }
            case 6:
            {
                // user not registered and login was specified
                mar.setSessionId(fields[1]);
                mar.setCaptcha(fields[2]);
                break;
            }
            case 7:
            {
                // user registered and register was specified
                mar.setSessionId(fields[1]);
                mar.setCaptcha(fields[2]);
                break;
            }
            case 0:
            {
                // retrieve the values from the fields successfully
                mar.setPID(fields[1]);
                mar.setSocket1(fields[2].substring(9));
                mar.setHttp1(fields[3]);
                mar.setDialingCode(fields[4]);
                mar.setNationalCountryPrefix(fields[5]);
                mar.setInternationalCountryPrefix(fields[6]);
                mar.setSocket2(fields[7].substring(9));
                mar.setHttp2(fields[8]);
                mar.setKeepAliveTime(fields[9].length() < 1 ? -1 : Integer.parseInt(fields[9]));
                mar.setMsisdn(fields[10]);
                mar.setCountryCode(fields[11]);
                mar.setRegion(fields[12]);
                mar.setUtf8Disable(Boolean.parseBoolean(fields[13]));

                break;
            }
        }

        return mar;
    }
}
