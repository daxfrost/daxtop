package com.mxit.core.exception;

/**
 * @author Dax Booysen
 * 
 * MXit custom Exception class.
 */
public class MXitProfileNotLoadedException extends Exception
{
    public MXitProfileNotLoadedException()
    {
        super("This MXitProfile is not loaded properly, please use a loaded profile and try again.");
    }
}
