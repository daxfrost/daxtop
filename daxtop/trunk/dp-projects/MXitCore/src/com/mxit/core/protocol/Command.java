package com.mxit.core.protocol;

import com.mxit.core.protocol.command.*;
import com.mxit.core.protocol.type.CommandType;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * @author Dax Booysen
 * @company MXit Lifestyle
 */
/** MXit Command Base Class */
public abstract class Command
{
    // static values for commands

    private static String sessid = null;
    // protocol constants
    protected static final char RECORD_SEP = (char) 0;
    protected static final char FIELD_SEP = (char) 1;
    protected static final char PACKET_SEP = (char) 2;
    private static final String LENGTH_REC = "ln=";
    private static final String MSISDN_REC = "id=";
    private static final String COMMAND_REC = "cm=";
    private static final String MESSAGE_REC = "ms=";
    /** Universely error code for unknown */
    public final int ErrorSomethingWentWrong = 99;
    /** Command type of this command */
    public CommandType Type = null;
    // command variables
    public int ErrorCode = 0;
    public String ErrorMessage = "";

    /** The character set used to convert to utf-8 */
    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    /** Reads the packet data and returns a Command */
    public static Command ReadPacketData(byte[] data, int pos)
    {
        // discard empty data packets
        if (data == null)
        {
            return null;
        }

        // parse command data
        Command cmd = read(data, pos);

        return cmd;
    }

    /** Parse Command Data */
    private static Command read(byte[] data, int pos)
    {
        /* debug for all incoming data */
        System.out.println("\n\nReceived: [" + (new String(data)) + "]\n");

        // cater for multiple digits
        int charLength = 1;
        while (!(((data[pos + charLength])) == Command.RECORD_SEP))
        {
            charLength++;
        }

        // command type
        CommandType cmdType = CommandType.GetCommandType(Integer.parseInt(new String(Arrays.copyOfRange(data, pos, pos + charLength)).toString()));
        pos += ++charLength;

        // create command according to type
        Command cmd = createCommand(cmdType);

        if (cmd instanceof CommandUnknown)
        {
            return null;
        }

        // get error code data
        charLength = 1;
        do
        {
            if ((((data[pos + charLength])) == Command.PACKET_SEP) || (((data[pos + charLength])) == Command.RECORD_SEP))
            {
                break;
            }

            charLength++;
        }
        while (pos + charLength < data.length);

        // get error code and message
        String errorDataString = new String(Arrays.copyOfRange(data, pos, pos + charLength));
        String errorRecords[] = errorDataString.split(String.valueOf(FIELD_SEP));
        cmd.ErrorCode = Integer.parseInt(errorRecords[0]);
        cmd.ErrorMessage = errorRecords.length > 1 ? errorRecords[1] : "";
        pos += charLength;

        // create array for rest of command data
        byte[] cmdData = new byte[data.length - (pos == data.length ? data.length : ++pos)];
        System.arraycopy(data, pos, cmdData, 0, cmdData.length);

        System.out.println("after: " + new String(cmdData));

        if (cmd.ErrorCode == 0)
        {
            // parse the rest of the data if no error occured
            cmd.parseData(cmdData);
        }

        return cmd;
    }

    /** Build The Packet Data To Send For This Command */
    public byte[] BuildPacketData(String MXitId) throws IOException
    {
        // setup stream to add all the bytes together for the command being build
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // add command header data
        dos.write((MSISDN_REC + MXitId + RECORD_SEP).getBytes());
        dos.write((COMMAND_REC + CommandType.GetNumericFromCommand(Type) + RECORD_SEP + MESSAGE_REC).getBytes());
        // add command body specific data
        buildData(dos);
        // attach length by creating a new final data byte array
        int len = dos.size();
        byte[] lengthPrefix = (LENGTH_REC + len + RECORD_SEP).getBytes();
        
        // build the final packet data
        ByteArrayOutputStream baosFinal = new ByteArrayOutputStream();
        DataOutputStream dosFinal = new DataOutputStream(baosFinal);
        dosFinal.write(lengthPrefix);
        dosFinal.write(baos.toByteArray());

        // finally return data
        return baosFinal.toByteArray();
    }

    /** Centralized utf-8 conversion for MXit commands except multimedia messaging */
    protected static String decodeUTF8(byte[] bytes)
    {
        try {
            return UTF8_CHARSET.newDecoder().decode(ByteBuffer.wrap(bytes)).toString();
            //return new String(UTF8_CHARSET.decode(ByteBuffer.wrap(bytes)).array());
        } catch (CharacterCodingException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /** Centralized utf-8 conversion for MXit commands except multimedia messaging */
    protected static byte[] encodeUTF8(String string)
    {
        return string.getBytes(UTF8_CHARSET);
    }

    /** Returns the command Object according to command type. */
    private static Command createCommand(CommandType type)
    {
        switch (type)
        {
            case Login:
                return new CommandLogin();
            case GetContacts:
                return new CommandGetContacts();
            case GetNewMessages:
                return new CommandGetNewMessages();
            case GetPresence:
                return new CommandGetPresence();
            case GetExtendedProfile:
                return new CommandGetExtenededProfile();
            case Multimedia:
                return new CommandMultimediaMessage();
            case SendMessage:
                return new CommandSendMessage();
            case AddNewContact:
                return new CommandInviteContact();
            case GetNewSubscriptions:
                return new CommandGetNewSubscription();
            case Logout:
                return new CommandLogout();
            case AllowSubscription:
                return new CommandAllowSubscription();
            case DenySubscription:
                return new CommandDenySubscription();
            case BlockSubscription:
                return new CommandBlockSubscription();
            case Register:
                return new CommandRegister();
            case SetExtendedProfile:
                return new CommandSetExtenededProfile();
            case SetMood:
                return new CommandSetMood();
            case SetPresence:
                return new CommandShowPresence();
            case RenameGroup:
                return new CommandRenameGroup();
            case AddNewGroupChat:
                return new CommandNewMultiMX();
            case InviteGroupChatMembers:
                return new CommandMultiMXAddMember();
            case UpdateContact:
            case RemoveContact:
            case KeepAlive:
            case RemoveGroup:
            case GetProfile:
            case UpdateProfile:
            case UnknownCommand:
                return new CommandUnknown();
        }

        return null;
    }

    /** Override this method for each command type to deal with each type. */
    public abstract void parseData(byte[] cmdData);

    /** Override this method for each command type to deal with each type. */
    public abstract void buildData(DataOutputStream sb) throws IOException;
}
