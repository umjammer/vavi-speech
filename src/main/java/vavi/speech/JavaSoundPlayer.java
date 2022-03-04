/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.speech.EngineStateError;

import static vavi.sound.SoundUtil.volume;


/**
 * JavaSoundPlayer.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/09/19 umjammer initial version <br>
 */
public class JavaSoundPlayer implements Player {

    @Override
    public void play(byte[] data) throws IOException {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new ByteArrayInputStream(data));
            AudioFormat format = ais.getFormat();

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);

            line.start();

            volume(line, .2d);

            int r = 0;
            byte[] buf = new byte[8192];
            while (r != -1) {
                r = ais.read(buf, 0, buf.length);
                if (r >= 0) {
                    line.write(buf, 0, r);
                }
            }

            line.drain();
            line.close();
        } catch (UnsupportedAudioFileException | LineUnavailableException e) {
            throw (EngineStateError) new EngineStateError().initCause(e);
        }
    }
}
/* */
