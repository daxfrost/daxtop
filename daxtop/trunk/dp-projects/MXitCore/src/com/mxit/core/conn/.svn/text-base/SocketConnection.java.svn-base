package com.mxit.core.conn;

import com.dax.Main;
import com.mxit.core.MXitManager;
import java.io.*;
import java.net.*;

import com.mxit.core.protocol.*;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * @author Dax Booysen
 * @company MXit Lifestyle
 * 
 * The socket connection class used to maintain a connection to the backend.
 * Two thread classes are used to handle incoming and outgoing data asynchronously.
 */
public final class SocketConnection
{
    // socket connection handler objects
    private Socket _socket;
    private SocketOut _socketOut;
    private SocketIn _socketIn;

    // protocol constants
    private static final char RECORD_SEP = (char)0;
    private static final char  FIELD_SEP = (char)1;
    private static final char PACKET_SEP = (char)2;		
    private static final String LENGTH_REC = "ln=";
    
    // queue for sending
    private Queue<Command> waitingCommands = new LinkedList<Command>();
    
    // synchronous sending objects
    private Command _pendingTx = null;
    private Command _replyTx = null;
    private boolean _txComplete = true;
    
    /** connection attempts */
    public int connectionAttempts = 0;
    
    /** MXitManager instance */
    private MXitManager manager = null;

    /** Constructor */
    public SocketConnection(MXitManager manager)
    {
        this.manager = manager;
    }
    
    /** Connect with socket, return false if connection fails */
    public boolean Connect(String host, int port)
    {
        boolean connectionSuccess = false;
        
        try
        {
            // setup default vars
            _pendingTx = null;
            _replyTx = null;
            _txComplete = true;
            waitingCommands.clear();
            // connect to socket
            _socket = new Socket();
            _socket.bind(null);
            _socket.connect(new InetSocketAddress(host, port));
            // open streams of socket with incoming, outgoing thread classes
            _socketIn = new SocketIn();
            _socketOut = new SocketOut();
            _socketOut.start();			
            _socketIn.start();
            connectionSuccess = true;
        }
        catch(ConnectException ce)
        {
            System.out.println(ce.getMessage());
            // attempt to reconnect
            connectionAttempts++;
        }
        catch(UnknownHostException uhe)
        {
            uhe.printStackTrace();
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return connectionSuccess;
        }
    }
    
    /** Send command (asynchronous) */
    public void Send(Command cmd)
    {
        // send command
        waitingCommands.add(cmd);
    }
    
    /** Send command and wait for a reply (synchronous) */
    public synchronized Command SendAndWait(Command cmd)
    {
        Command resp = null;

        try
        {
            // set command as the awaited pending reply
            _pendingTx = cmd;
            // send command
            waitingCommands.add(cmd);

            // loop and check until the reply has returned
            while(!_txComplete || _replyTx == null || _replyTx.Type != cmd.Type)
                Thread.sleep(100);

            // reset transaction completed boolean
            resp = _replyTx;
            _txComplete = false;
            _replyTx = null;
        }
        catch(InterruptedException ie) { }
        
        // return reply or timeout
        return resp;
    }
    
    /** Disconnects the socket, will return false if disconnection fails */
    public boolean Disconnect()
    {
        try
        {
            manager.LoggedIn = false;
            manager.Connected = false;

            _socketIn.close();
            _socketOut.close();
            _socket.close();
            
            System.out.println("Socket connection closed.");
            
            return true;
        }
        catch(IOException ioe)
        {
            System.out.println("IO Exception!");
            ioe.printStackTrace();
            return false;
        }
    }
    
    /** Called when data is received from the input socket from mxit server */
    private void DataReceived(byte[] cmdData)
    {
        System.out.println("\n Command Received \n");
        System.out.println("DATA = ");

        Command cmd = Command.ReadPacketData(cmdData, 0);
        
        // synchronous
        if(cmd != null && _pendingTx != null && cmd.Type == _pendingTx.Type)
        {
            _replyTx = cmd;//manager.IncomingCommand(cmd);
            _txComplete = true;
        }
        // asynchronous
        else
            manager.IncomingCommand(cmd);
    }
    
    /** Handles sending of all outgoing data on the socket */
    class SocketOut extends Thread
    {
        // objects
        private BufferedOutputStream _out;

        SocketOut() throws IOException
        {
            _out = new BufferedOutputStream(_socket.getOutputStream());
            waitingCommands.clear();
        }

        @Override
        public void run()
        {
            //waitingCommands.add(new CommandLogin());
            
            while(_out!=null)
            {
                try
                {
                    Command cmd = null;
                    
                    if((cmd = waitingCommands.poll()) != null)
                    {    
                        // synchronized method
                        SendCommand(cmd);
                    }

                    sleep(500);
                }
                catch (NoSuchElementException e)
                {
                    // strange error... try start again?
                    run();
                }
                catch(SocketException se)
                {
                    if (manager.LoggedIn)
                    {
                        manager.Disconnect();
                        se.printStackTrace();
                    }
                }
                catch (InterruptedException ie)
                {
                    ie.printStackTrace();
                }
                catch (IOException ioe)
                {
                    System.out.println("IO Exception!");
                    ioe.printStackTrace();
                }
            }
        }
        
        /** Internal sending of Command bytes */
        private synchronized void SendCommand(Command cmd) throws IOException
        {
            byte[] data = cmd.BuildPacketData(manager.CurrentProfile.MXitID);

            // LOG BLOCK - TODO <- remove this block
            System.out.print("\n\nclient> ");
            for(byte b : data)
                System.out.print((char)b);
            //  <- till here
            
            _out.write(data);

            if (manager.Connected)
                _out.flush();
        }
        
        void close() throws IOException
        {
            if (_out != null)
                _out.close();
            _out = null;
        }
    }
    
    /** Handles receiving of all incoming data on the socket */
    class SocketIn extends Thread
    {
        // objects
        private InputStream _in;
        private BufferedInputStream _dis;

        SocketIn() throws IOException
        {
            _dis = new BufferedInputStream(_in = _socket.getInputStream());
        }

        @Override
        public void run()
        {
            try
            {
                // while connected
                while(_in!=null)
                {
                    // we need to read the length (ln=..\0) one byte at a time
                    byte b;
                    // this will block until data is available
                    b = (byte)_dis.read();
                    // remote server closed connection
                    if (b == -1)
                    {
                        manager.Disconnect();
                        break;
                    }
                    // data is available
                    StringBuilder sb = new StringBuilder("");
                    while ((char)b != RECORD_SEP)
                    {
                        // get the length of incoming command
                        sb.append((char)b);
                        b = (byte)_dis.read();
                    }
                    // parse length from the record
                    if (sb.length() > 0)
                    {
                        try
                        {
                            Thread.sleep(0);
                        }
                        catch(InterruptedException ie)
                        {
                        }
                        
                        // length of incoming command
                        int len = Integer.parseInt(sb.replace(sb.indexOf(LENGTH_REC), 3, "").toString());
                        byte[] data = new byte[len];
                        byte[] chunk = new byte[Math.min(_dis.available(), len)];
                        int readPos = 0;
                        
                        while(readPos < len)
                        {
                            // read chunk of incoming data
                            int read = 0;
                            read += _dis.read(chunk, 0, chunk.length);
                            System.arraycopy(chunk, 0, data, readPos, chunk.length);
                            readPos += read;
                            
                            // bytes left are less than the chunk length
                            if(readPos+chunk.length > len)
                            {
                                chunk = new byte[len-readPos];
                            }
                        }
                        
                        // send data off for processing
                        DataReceived(data);
                    }
                }
            }
            catch (SocketException se)
            {
                if (manager.LoggedIn)
                {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            JOptionPane.showMessageDialog(Main.frmContacts, "MXit has dropped one of your connections.");
                        }
                    });
                    
                    manager.Disconnect();
                    se.printStackTrace();
                }
            }
            catch(IOException ioe)
            {
                ioe.printStackTrace();
            }
        }

        void close() throws IOException
        {
            if (_in != null)
                _in.close();
            _in = null;
        }
    }
}