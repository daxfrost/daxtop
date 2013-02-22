package com.mxit.core.exception;

/**
 * @author Dax Booysen
 * 
 * MXit custom Exception class.
 */
public class MXitLoginFailedException extends Exception
{
    public MXitLoginFailedException()
    {
        super("MXit Login failed!");
    }
}
