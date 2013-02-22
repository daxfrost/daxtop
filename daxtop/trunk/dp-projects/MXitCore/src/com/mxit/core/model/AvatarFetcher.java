package com.mxit.core.model;

import com.mxit.core.MXitManager;
import com.mxit.core.protocol.command.multimedia.model.AvatarRequest;
import java.util.ArrayList;

/**
  * @author Dax Booysen
 */
public class AvatarFetcher extends Thread
{
    public ArrayList<AvatarRequest> requests;

    public MXitManager manager = null;

    /** runner to fetch an avatar */
    @Override
    public void run()
    {
        if(requests != null)
        {
            manager.SendGetAvatarsRequest(requests);
        }
    }
}
