package com.mxit.core.activation;

import com.mxit.core.encryption.AES;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;

/**
 * @author booysend
 *
 * The result of an activation challenge attempt is returned in this object
 */
public final class MXitActivationResult
{
    private boolean success = false;
    private short errorCode = 0;
    private String PID;
    private String socket1, socket2;
    private String http1, http2;
    private String dialingCode;
    private String nationalCountryPrefix;
    private String internationalCountryPrefix;
    private int keepAliveTime;
    private String msisdn;              // the normalized msisdn (checked against MXit database)
    private String CountryCode;         // the country code of the user if already registered, otherwise the country code passed during the request
    private String Region;              // the region of the user in the database, else the region passed during the request
    private boolean isUtf8Disable;      // whether UTF-8 should be disabled in the client
    private String hashPin;             // setup on activation, but not passed to the backend

    private String domain;
    private String sessionId;
    private Image alternateCaptcha;

    /** The request that generated this result */
    public MXitActivationRequest request;

    /** Sets the status of the activation attempt */
    public void setSuccess(boolean status)
    {
        success = status;
    }

    /** Was activation a success */
    public boolean isSuccess()
    {
        return success;
    }
    
    /** Sets the captcha image */
    public void setCaptcha(String data)
    {
        try
        {
            alternateCaptcha = Toolkit.getDefaultToolkit().createImage(AES.base64_decode(data));
        }
        catch(IOException ioe)
        {
            System.out.println("io exception when decoding base64 encoded string");
        }
    }
    
    /** Gets the image for the challenge */
    public Image getCaptcha()
    {
        return alternateCaptcha;
    }

    /** Sets the new session id to replace expired one */
    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }
    
    /** Gets the new session id that replaced the expired one */
    public String getSessionId()
    {
        return this.sessionId;
    }
    
    /** Gets the domain that was set in case of a critical error */
    public String getDomain()
    {
        return this.domain;
    }
    
    /** Sets the domain that was set in case of a critical error */
    public void setDomain(String domain)
    {
        this.domain = domain;
    }
    
    /** Gets the country code of the user if already registered, otherwise the country code passed during the request */
    public String getCountryCode()
    {
        return CountryCode;
    }

    /** Sets the country code of the user if already registered, otherwise the country code passed during the request */
    public void setCountryCode(String CountryCode)
    {
        this.CountryCode = CountryCode;
    }

    /** Gets the product id */
    public String getPID()
    {
        return PID;
    }

    /** Sets the product id */
    public void setPID(String PID)
    {
        this.PID = PID;
    }

    /** Gets the region of the user in the database, else the region passed during the request */
    public String getRegion()
    {
        return Region;
    }

    /** Sets the region of the user in the database, else the region passed during the request */
    public void setRegion(String Region)
    {
        this.Region = Region;
    }

    /** Gets the country dialing code */
    public String getDialingCode()
    {
        return dialingCode;
    }

    /** Sets the country dialing code */
    public void setDialingCode(String dialingCode)
    {
        this.dialingCode = dialingCode;
    }

    /** Gets the error code of this request */
    public short getErrorCode()
    {
        return errorCode;
    }

    /** Sets the error code of this request */
    public void setErrorCode(short errorCode)
    {
        this.errorCode = errorCode;
    }

    /** Gets the HTTP connection */
    public String getHttp1()
    {
        return http1;
    }

    /** Sets the HTTP connection */
    public void setHttp1(String http1)
    {
        this.http1 = http1;
    }

    /** Gets the HTTP fallback connection */
    public String getHttp2()
    {
        return http2;
    }

    /** Sets the HTTP connection */
    public void setHttp2(String http2)
    {
        this.http2 = http2;
    }

    public void setHashPin(String pin)
    {
        this.hashPin = pin;
    }

    public String getHashPin()
    {
        return this.hashPin;
    }

    /** Gets the country international prefix */
    public String getInternationalCountryPrefix()
    {
        return internationalCountryPrefix;
    }

    /** Sets the country international prefix */
    public void setInternationalCountryPrefix(String internationalCountryPrefix)
    {
        this.internationalCountryPrefix = internationalCountryPrefix;
    }

    /** Is UTF-8 disabled in the client */
    public boolean isUtf8Disable()
    {
        return isUtf8Disable;
    }

    /** Sets whether UTF-8 is disabled in the client */
    public void setUtf8Disable(boolean isUtf8Disable)
    {
        this.isUtf8Disable = isUtf8Disable;
    }

    /** Gets the socket keepalive time */
    public int getKeepAliveTime()
    {
        return keepAliveTime;
    }

    /** Sets the socket keepalive time */
    public void setKeepAliveTime(int keepAliveTime)
    {
        this.keepAliveTime = keepAliveTime;
    }

    /** Gets the normalized msisdn (checked against MXit database) */
    public String getMsisdn()
    {
        return msisdn;
    }

    /** Sets the normalized msisdn (checked against MXit database) */
    public void setMsisdn(String msisdn)
    {
        this.msisdn = msisdn;
    }

    /** Gets the national country prefix */
    public String getNationalCountryPrefix()
    {
        return nationalCountryPrefix;
    }

    /** Sets the national country prefix */
    public void setNationalCountryPrefix(String nationalCountryPrefix)
    {
        this.nationalCountryPrefix = nationalCountryPrefix;
    }

    /** Gets the socket connection string */
    public String getSocket1()
    {
        return socket1;
    }

    /** Sets the socket connection string */
    public void setSocket1(String socket1)
    {
        this.socket1 = socket1;
    }

    /** Gets the fallback socket connection string */
    public String getSocket2()
    {
        return socket2;
    }

    /** Sets the fallback socket connection string */
    public void setSocket2(String socket2)
    {
        this.socket2 = socket2;
    }

    /** Gets the error enumeration type relating to this error */
    public ErrorCodes getErrorState()
    {
        switch (errorCode)
        {
            case 0:
                return ErrorCodes.NoError;
            case 1:
                return ErrorCodes.WrongAnswer;
            case 2:
                return ErrorCodes.SessionExpired;
            case 3:
                return ErrorCodes.Undefined;
            case 4:
                return ErrorCodes.CriticalError;
            case 5:
                return ErrorCodes.CountryCodeNotAvailable;
            case 6:
                return ErrorCodes.UserNotRegistered;
            case 7:
                return ErrorCodes.UserAlreadyRegistered;
        }

        return ErrorCodes.NoError;
    }

    public enum ErrorCodes
    {
        NoError,
        WrongAnswer,
        SessionExpired,
        Undefined,
        CriticalError,
        CountryCodeNotAvailable,
        UserNotRegistered,
        UserAlreadyRegistered;
    }
}
