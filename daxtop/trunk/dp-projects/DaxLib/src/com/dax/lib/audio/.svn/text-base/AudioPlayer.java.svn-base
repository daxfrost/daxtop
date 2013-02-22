package com.dax.lib.audio;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;

/**
 * @author Dax Booysen
 * 
 * This class plays various kinds of audio files
 */
public final class AudioPlayer implements Runnable, LineListener
{
    // the queue of files to play
    private static ConcurrentLinkedQueue<Clip> playQueue = new ConcurrentLinkedQueue<Clip>();

    private static Thread runner;

    public static boolean muted = false;

    /** Constructor */
    public AudioPlayer()
    {
        runner = new Thread(this);
        runner.start();
    }

    /** Plays a sound file */
    public void Play(Clip clip)
    {
        if (muted)
            return;
        
        if (clip == null)
            return;

        // add to queue
        playQueue.add(clip);
    }

    // main method for playing audio files
    public void run()
    {
        while (true)
        {
            try
            {

                    while (playQueue.isEmpty())
                        Thread.sleep(10);

                    Clip c = playQueue.remove();
                    c.start();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public Clip GetClip(URL url)
    {
        Clip soundClip = null;

        try
        {
            AudioInputStream stream = AudioSystem.getAudioInputStream(url);
            AudioFormat streamFormat = stream.getFormat( );
            DataLine.Info clipInfo = new DataLine.Info( Clip.class, streamFormat );
            soundClip = ( Clip ) AudioSystem.getLine( clipInfo );
            soundClip.addLineListener(this);
            soundClip.open(stream);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return soundClip;
    }

    public Clip GetClip(InputStream s) throws Exception
    {
        Clip soundClip = null;

        AudioInputStream stream = AudioSystem.getAudioInputStream(s);
        AudioFormat streamFormat = stream.getFormat( );
        DataLine.Info clipInfo = new DataLine.Info(Clip.class, streamFormat);
        soundClip = ( Clip ) AudioSystem.getLine( clipInfo );
        soundClip.addLineListener(this);
        soundClip.open(stream);

        return soundClip;
    }

    public void update(LineEvent event)
    {
        if (event.getType() == Type.STOP)
        {
            Clip c = ((Clip)event.getSource());
            c.stop();
            c.setFramePosition(0);
        }
    }
}
