package com.mxit.core.exception;

/**
 * @author Dax Booysen
 * 
 * MXit custom Exception class.
 */
public class MXitConfigurationException extends Exception
{
    public MXitConfigurationException()
    {
        super("Either the configuration files have been corrupted or cannot be found.");
    }
}
