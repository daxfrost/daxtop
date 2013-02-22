package com.dax.daxtop.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author Dax Booysen
 */
public final class ConfigManager implements Serializable
{
    /** Config entities to be serialized */
    ConfigEntity[] entities = new ConfigEntity[0];
    
    /** Default constructor */
    public ConfigManager()
    {
    }
    
    /** Constructor with entities */
    public ConfigManager(ConfigEntity[] values)
    {
        entities = values;
    }
    
    /** Append a new entity to the configuration set */
    public void AddEntity(ConfigEntity entity)
    {
        ConfigEntity[] replacement = new ConfigEntity[entities.length + 1];
        System.arraycopy(entities, 0, replacement, 0, entities.length);
        replacement[entities.length] = entity;
        entities = replacement;
    }
    
    /** Get ConfigEntity Value by Name */
    public Object GetEntity(String EntityName)
    {
        for(ConfigEntity entity : entities)
            if(entity.EntityName.equals(EntityName))
                return entity.EntityValue;
        
        return null;
    }
    
    /** Serialize a set of config entities to file */
    public static void SaveConfiguration(String configFilePath, ConfigManager configData)
    {
        try
        {
            // serialize data to file
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(configFilePath));
            oos.writeObject(configData);
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
    
    /** Load serialized set of config entities from file, returns null on no file found */
    public static ConfigManager LoadConfiguration(String configFilePath)
    {
        try
        {
            if(!new File(configFilePath).exists())
                return null;
            
            // deserialize data in file
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(configFilePath));
            return (ConfigManager) ois.readObject();
        }
        catch(ClassNotFoundException cnfe)
        {
            cnfe.printStackTrace();
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
        
        return null;
    }
}
