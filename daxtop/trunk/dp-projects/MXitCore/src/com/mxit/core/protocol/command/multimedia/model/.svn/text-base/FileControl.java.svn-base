package com.mxit.core.protocol.command.multimedia.model;

import com.dax.Main;
import com.dax.control.ChatMessage;
import com.dax.control.item.ItemCommand;
import com.dax.control.item.ItemLoadingBar;
import com.dax.daxtop.res.ResourceManager;
import com.dax.daxtop.ui.components.ConversationViewer;
import com.dax.daxtop.ui.components.ImagePanel;
import com.mxit.MXitNetwork;
import com.mxit.core.interfaces.DownloadListener;
import com.mxit.core.model.MXitContact;
import com.mxit.core.model.MXitContactList;
import com.mxit.core.model.MXitMessageBuilder;
import com.mxit.core.protocol.command.multimedia.CommandMultimediaMessageFileGet;
import com.mxit.core.protocol.command.multimedia.CommandMultimediaMessageFileOffer;
import com.mxit.core.res.MXitRes;
import com.mxit.core.ui.ChatScreen;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import javax.sound.sampled.Clip;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * @author Dax Frost
 *
 * A control that will handle file functions to and from the client within
 * a chat screen.
 */
public class FileControl implements ActionListener, DownloadListener
{
    /** The internal file offer command */
    private CommandMultimediaMessageFileOffer command = null;

    /** The maximum size of a chunk to download */
    protected static final int downloadChunkSize = 1024;

    /** The MXitNetwork to send commands with */
    private final MXitNetwork network;

    /** The Chat Screen to display message on */
    private final ChatScreen cscreen;

    /** The progress bar */
    private ItemLoadingBar ilb = null;

    /** A flag for if this object is now in a dead state */
    private boolean disabled = false;
    
    /** A flag to keep state whether the file was rejected or not */
    private boolean rejected = false;

    /** The file data */
    private byte[] data;

    /** Constructs a new file offer control */
    public FileControl(CommandMultimediaMessageFileOffer offerCommand, MXitNetwork network, ChatScreen cs)
    {
        this.command = offerCommand;
        this.network = network;
        this.cscreen = cs;
    }

    /** The action listener for file click events */
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() instanceof ItemCommand)
        {
            ItemCommand cmd = (ItemCommand)e.getSource();

            if (!disabled)
            {
                // download (get file)
                if (cmd.replyMsg.equals("Download"))
                {
                    disabled = true;
                    // add download control
                    ChatMessage downloadMessage = new ChatMessage("File downloading...\n");
                    ilb = new ItemLoadingBar(command.Size, "Open", "Save");
                    ilb.btnOpen.setEnabled(false);
                    ilb.btnOpen.addActionListener(this);
                    ilb.btnSave.setEnabled(false);
                    ilb.btnSave.addActionListener(this);
                    downloadMessage.AppendItem(ilb);

                    cscreen.chatControl.AppendMessage(downloadMessage);

                    // calculate the first chunk size
                    int downloadSize = command.Size;
                    if (downloadSize > downloadChunkSize)
                    {
                        downloadSize = downloadChunkSize;
                    }
                    // attach this as a download listener
                    network.userSession.AddFileDownload(command.Id, this);
                    // send the first file get request
                    network.SendGetFile(command.Id, 0, downloadSize);
                }
                // reject (reject file)
                else if (cmd.replyMsg.equals("Reject"))
                {
                    disabled = true;
                    rejected = true;
                    // reject the file
                    network.SendRejectFile(command.Id, FileRejectReasons.UserRejected, "");
                    cscreen.chatControl.AppendMessage(new ChatMessage("File rejected."));
                }
            }

            // forward (forward file)
            if (!rejected && cmd.replyMsg.equals("Forward"))
            {
                ArrayList<MXitContact> contacts = network.currentContactList.GetContacts(MXitContactList.MXit | MXitContactList.Gallery, true, true);

                if (contacts.size() == 0)
                {
                    JOptionPane.showMessageDialog(Main.frmChat, "Sorry, there are no contacts to forward this to!");
                }

                Object[] nicknames = new Object[contacts.size()];
                int i = 0;
                for (MXitContact c : contacts)
                {
                    nicknames[i++] = c.Nickname;
                }

                String recipient = (String)JOptionPane.showInputDialog(Main.frmChat, "Select a recipient: ", "Forward to...", JOptionPane.INFORMATION_MESSAGE, null, nicknames, nicknames[0]);

                if (recipient != null)
                {
                    for (MXitContact c : contacts)
                    {
                        if (c.Nickname.equals(recipient))
                        {
                            ArrayList<String> mxitIds = new ArrayList<String>();
                            mxitIds.add(c.MXitID);
                            network.ForwardFile(command.Id, command.Name, command.Description, mxitIds);
                            cscreen.chatControl.AppendMessage(new ChatMessage("File forwarded."));
                            break;
                        }
                    }
                }
            }
        }
        else if (ilb != null && ilb.btnOpen == e.getSource())
        {
            // handle button click
            openFile();
        }
        else if (ilb != null && ilb.btnSave == e.getSource())
        {
            // handle button click
            saveFile();
        }
    }

    @Override
    /** Bytes received from download */
    public void receivedBytes(CommandMultimediaMessageFileGet cmd)
    {
        final int pos = cmd.Offset + cmd.Length;

        if (data == null)
        {
            data = new byte[command.Size];
        }

        System.arraycopy(cmd.Data, 0, data, cmd.Offset, cmd.Length);

        // update progress bar
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                // update progress
                ilb.progressBar.setValue(pos);
            }
        });

        // request more content if needed
        if (command.Size > pos)
        {
            int downloadSize = command.Size - pos;

            if (downloadSize > downloadChunkSize)
            {
                downloadSize = downloadChunkSize;
            }

            // send the next file get request
            network.SendGetFile(command.Id, pos, downloadSize);
        }
        else
        {
            // update progress bar
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    // download completed
                    ilb.btnOpen.setEnabled(true);
                    ilb.btnSave.setEnabled(true);
                    ilb.progressBar.setString("Download Complete");
                }
            });

            // if we must auto open file
            if ((command.Flags & FileOfferFlags.AutoOpenAlways) != 0)
            {
                // force file open
                openFile();
            }
        }
    }

    /** Saves the file to disk */
    private void saveFile()
    {
        // update progress bar
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setSelectedFile(new File(fileChooser.getCurrentDirectory() + "/" + command.Name));

                    // if yes was chosen
                    if (fileChooser.showSaveDialog(cscreen) == JFileChooser.APPROVE_OPTION)
                    {
                        File f = fileChooser.getSelectedFile();

                        if (!f.exists())
                        {
                            FileOutputStream fos = new FileOutputStream(f);
                            fos.write(data);
                            fos.close();

                            java.awt.Desktop.getDesktop().open(f.getParentFile());
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    /** Opens the file */
    private void openFile()
    {
        // update progress bar
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                // if this is an image
                if (command.MimeType.equals("image/png") || command.MimeType.equals("image/jpeg"))
                {
                    Image img = Toolkit.getDefaultToolkit().createImage(data);

                    ImagePanel iPanel = new ImagePanel(command.Name, network.networkName, network.networkName + command.Id, img);
                    Main.ShowChat(network.networkName + command.Id, command.Name.length() >= 13 ? command.Name.substring(0, 10) + "..." : command.Name, command.ContactAddress, MXitRes.IMG_STATUS_ONLINE, iPanel, true);
                }
                else if (command.MimeType.equals("application/mxit-skin") || command.MimeType.equals("application/mxit-emo") || command.MimeType.equals("application/mxit-emof"))
                {
                    if (JOptionPane.showConfirmDialog(Main.frmChat, "Sorry, daxtop will only support this\nfile type in the next version...\nWould you like to save to your Gallery?", "Save to Gallery...", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                    {
                        // TODO - forward to gallery
                    }
                }
                else if(command.MimeType.equals("audio/wav") || command.MimeType.equals("audio/midi") || command.MimeType.equals("audio/aac") || command.MimeType.equals("audio/mp3") || command.MimeType.equals("audio/amr") || command.MimeType.equals("audio/amr-wb") || command.MimeType.equals("audio/mp4"))
                {
                    try
                    {
                        Clip c = ResourceManager.LoadAudio(new ByteArrayInputStream(data));
                        MXitRes.PlaySound(network, c);
                    }
                    catch (Exception e)
                    {
                        String tmpPath = System.getProperty("java.io.tmpdir") + "daxtop/";

                        try
                        {
                            String filePath = tmpPath + command.Name;

                            // make sure the directory exists first
                            File dir = new File(tmpPath);
                            if (!dir.exists())
                                dir.mkdir();

                            FileOutputStream fos = new FileOutputStream(filePath);
                            fos.write(data);
                            fos.close();

                            java.awt.Desktop.getDesktop().open(new File(filePath));
                        }
                        catch (Exception ex)
                        {
                            try
                            {
                                java.awt.Desktop.getDesktop().open(new File(tmpPath));
                            }
                            catch(Exception exc)
                            {
                                exc.printStackTrace();
                            }
                        }
                    }
                }
                else if (command.MimeType.equals("application/mxit-msgs") || command.Name.endsWith(".mxm"))
                {
                    ConversationViewer viewer = new ConversationViewer(command.Name, network.networkName, network.networkName + command.Id, MXitMessageBuilder.DecodeMXitMessageFormat(network, data));

                    // decoding the message failed
                    if (viewer.messages == null)
                        return;

                    Main.ShowChat(network.networkName + command.Id, command.Name.length() >= 13 ? command.Name.substring(0, 10) + "..." : command.Name, command.ContactAddress, MXitRes.IMG_STATUS_ONLINE, viewer, true);
                }
                else
                {
                    String tmpPath = System.getProperty("java.io.tmpdir") + "daxtop/";

                    try
                    {
                        String filePath = tmpPath + command.Name;

                        // make sure the directory exists first
                        File dir = new File(tmpPath);
                        if (!dir.exists())
                            dir.mkdir();

                        FileOutputStream fos = new FileOutputStream(filePath);
                        fos.write(data);
                        fos.close();

                        java.awt.Desktop.getDesktop().open(new File(filePath));
                    }
                    catch (Exception ex)
                    {
                        try
                        {
                            java.awt.Desktop.getDesktop().open(new File(tmpPath));
                        }
                        catch(Exception exc)
                        {
                            exc.printStackTrace();
                        }
                    }
                }
            }
        });
    }
}
