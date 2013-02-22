/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mxit.core.activation;

/**
 *
 * @author booysend
 */
public final class MXitActivationRequest
{
    private MXitActivationChallenge challenge;
    private String msisdn;
    private String category = "Y";
    private String captcha;
    private String countryCode;
    private String localeLanguage;
    private boolean isLogin = true;

    public MXitActivationRequest(MXitActivationChallenge c, String msisdn, String captchaText, String countrycode, String locale, String language, boolean isLogin)
    {
        challenge = c;
        this.msisdn = msisdn;
        this.captcha = captchaText;
        this.countryCode = countrycode;
        this.localeLanguage = locale;
        this.isLogin = isLogin;
    }

    /** Gets the challenge relating to this request */
    public MXitActivationChallenge getChallenge()
    {
        return challenge;
    }

    /** Gets the msisdn for this activation request */
    public String getMsisdn()
    {
        return msisdn;
    }

    /** Gets the category of this activation request. [Default is "Y" - PC Client] */
    public String getCategory()
    {
        return category;
    }

    /** Sets the category of this activation request. [Default is "Y" - PC Client] */
    public void setCategory(String category)
    {
        this.category = category;
    }

    /** Gets the answer supplied for the captcha challenge in this Activation request */
    public String getCaptcha()
    {
        return captcha;
    }

    /** Gets the locale and language relating to this request in the following format (e.g local_Language) */
    public String getLocaleLanguage()
    {
        return localeLanguage;
    }

    /** Gets the country code relating to this request */
    public String getCountry()
    {
        return countryCode;
    }

    boolean getIsLogin()
    {
        return isLogin;
    }
}
