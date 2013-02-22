package com.mxit.core.protocol.command;

import com.mxit.core.model.MXitSubscription;
import com.mxit.core.model.type.MXitContactPresence;
import com.mxit.core.model.type.MXitContactType;
import com.mxit.core.model.type.MXitPresence;
import com.mxit.core.protocol.type.*;
import com.mxit.core.protocol.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/** GetNewSubscription Command */
public class CommandGetNewSubscription extends Command
{
    public ArrayList<MXitSubscription> subscriptions;

    /** Creates a new GetNewSubscription Command */
    public CommandGetNewSubscription()
    {
        Type = CommandType.GetNewSubscriptions;
    }

    @Override
    public void parseData(byte[] cmdData)
    {
        String data = decodeUTF8(cmdData);
        String dataRecords[] = data.split(String.valueOf(RECORD_SEP));

        subscriptions = new ArrayList<MXitSubscription>();

        MXitSubscription ms;

        for (int i = 0; i < dataRecords.length; i++)
        {
            ms = new MXitSubscription();

            String fields[] = dataRecords[i].split(String.valueOf(FIELD_SEP));

            ms.MXitID = fields[0];
            ms.Nickname = fields[1];
            ms.Type = MXitContactType.getMXitContactType(Integer.parseInt(fields[2]));
            ms.hiddenLoginName = Boolean.parseBoolean(fields[3]);
            if (fields.length > 4)
                ms.Msg = fields[4];
            if (fields.length > 5)
                ms.groupChatMod = fields[5];

            subscriptions.add(ms);
        }
    }

    @Override
    public void buildData(DataOutputStream sb) throws IOException
    {
        /*sb.append(PasswordHash);
        sb.append(FIELD_SEP);
        sb.append(Version);
        sb.append(FIELD_SEP);
        sb.append(GetContacts ? 1 : 0);
        sb.append(FIELD_SEP);
        if(Capabilities != null)
        {
        for(int i = 0; i < Capabilities.size(); i++)
        {
        sb.append(Capabilities.keySet().toArray()[i]);
        sb.append("=");
        sb.append(Capabilities.values().toArray()[i]);
        sb.append(";");
        }
        }
        sb.append(FIELD_SEP);
        sb.append(DistributionCode);
        sb.append(FIELD_SEP);
        sb.append(Features);
        sb.append(FIELD_SEP);
        sb.append(DialingCode);
        sb.append(FIELD_SEP);
        sb.append(Locale);
        sb.append(RECORD_SEP);
        sb.append(CUSTOM_RES);
        if(CustomResources != null)
        {
        for(int i = 0; i < CustomResources.length; i++)
        {
        sb.append(CustomResources[i]);
        sb.append(FIELD_SEP);
        }
        }
        //sb.append("7UhuSNbTp4i0Fsi5tKjz8aKMZzjAM4BocXIRj9e36iU=E-5.6.3-J-Windows Mobile1BE609896-8072-49B6-9699-B4C1DEFA8A4112876727en\0cr=");
         */
    }
}
