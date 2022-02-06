/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.ByteArrayInputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.SourceDataLine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import vavi.speech.aquestalk.AquesTalk;
import vavi.speech.aquestalk.AquesTalkDa;


/**
 * Test4. (JNI)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
@EnabledIfSystemProperty(named = "os.arch", matches = "x86")
class Test4 {

    /** */
    public static void main(String[] args) throws Exception {
        Test4 test4 = new Test4();
//        test4.test01();
//        test4.test02();
        test4.test03();
    }

    @Test
    void test01() throws Exception {
        AquesTalkDa aquesTalkDa = new AquesTalkDa();
        String koe = "こんにちは、あくえすとーくです";
        aquesTalkDa.play(koe);
        while (aquesTalkDa.isPlay()) {
            Thread.sleep(20);
        }
        aquesTalkDa.play(koe, true);
    }

    @Test
    void test02() throws Exception {
        AquesTalk aquesTalk = new AquesTalk();
        String koe = "こんにちわ、あくえすとーくです";
        byte[] wave = aquesTalk.synthesize(koe, 100);
        AudioInputStream ais = AudioSystem.getAudioInputStream(new ByteArrayInputStream(wave));

System.err.println(ais.getFormat());
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, ais.getFormat());
        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(ais.getFormat());
        line.addLineListener(new LineListener() {
            public void update(LineEvent ev) {
                if (LineEvent.Type.STOP == ev.getType()) {
System.err.println("stoped");
                }
            }
        });
        line.start();
        byte[] buf = new byte[1024];
        int l = 0;
        while (ais.available() > 0) {
            l = ais.read(buf, 0, 1024);
            line.write(buf, 0, l);
        }
        line.drain();
        line.stop();
        line.close();
    }

    @Test
    void test03() throws Exception {
        AquesTalkDa aquesTalkDa = new AquesTalkDa();

        Properties props = new Properties();
        props.load(Test4.class.getResourceAsStream("table.properties"));
        Enumeration<?> e = props.propertyNames();
        while (e.hasMoreElements()) {
            String name = String.class.cast(e.nextElement());
System.err.println("Japanese: " + name);
            aquesTalkDa.play(props.getProperty(name), true);
        }
    }
}

/* */
