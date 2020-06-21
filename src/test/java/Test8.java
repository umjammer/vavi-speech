/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import org.junit.jupiter.api.Test;

import com.sun.jna.Pointer;

import vavi.speech.JavaSoundPlayer;
import vavi.speech.Player;
import vavi.speech.aquestalk10.jna.AquesTalk10;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * Test8. (aquestalk10, jna)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
@PropsEntity(url = "file://${user.dir}/local.properties")
public class Test8 {

    static AquesTalk10 instance;

    @Property
    String aquesTalk10DevKey;
    @Property
    String aqKanji2KoeDevKey;

    static {
        instance = AquesTalk10.INSTANCE;
    }

    public void test01(String text) throws Exception {
        int[] size = new int[1];

        int result = instance.AquesTalk_SetDevKey(aquesTalk10DevKey.getBytes());
System.err.println(result);

        Pointer wav = instance.AquesTalk_Synthe_Utf8(AquesTalk10.gVoice_F1, text, size);

        Player player = new JavaSoundPlayer();
        player.play(wav.getByteArray(0, size[0]));
    }

    public static void main(String[] args) throws Exception {
        Test8 app = new Test8();
        PropsEntity.Util.bind(app);
        app.test01(args[0]);
    }

    @Test
    void test02() throws Exception {
        int[] size = new int[1];
        assertNotNull(AquesTalk10.INSTANCE.AquesTalk_Synthe_Utf8(AquesTalk10.gVoice_F1, "あいうえお", size));
        assertNull(AquesTalk10.INSTANCE.AquesTalk_Synthe_Utf8(AquesTalk10.gVoice_F1, "　", size));
        assertNull(AquesTalk10.INSTANCE.AquesTalk_Synthe_Utf8(AquesTalk10.gVoice_F1, " ", size));
        assertNull(AquesTalk10.INSTANCE.AquesTalk_Synthe_Utf8(AquesTalk10.gVoice_F1, "！", size));
        assertNotNull(AquesTalk10.INSTANCE.AquesTalk_Synthe_Utf8(AquesTalk10.gVoice_F1, "、", size));
        assertNotNull(AquesTalk10.INSTANCE.AquesTalk_Synthe_Utf8(AquesTalk10.gVoice_F1, "？", size));
        assertNotNull(AquesTalk10.INSTANCE.AquesTalk_Synthe_Utf8(AquesTalk10.gVoice_F1, "。", size));
        assertNull(AquesTalk10.INSTANCE.AquesTalk_Synthe_Utf8(AquesTalk10.gVoice_F1, "abc", size));
        assertNull(AquesTalk10.INSTANCE.AquesTalk_Synthe_Utf8(AquesTalk10.gVoice_F1, "30", size));
    }

    @Test
    void test03() throws Exception {
        String text = "あいう-1.234えお";
        String result = text.replaceAll("([\\d\\.-]+)", "<NUM VAL=$1>");
        assertEquals("あいう<NUM VAL=-1.234>えお", result);
        int[] size = new int[1];
        assertNotNull(AquesTalk10.INSTANCE.AquesTalk_Synthe_Utf8(AquesTalk10.gVoice_F1, result, size));
    }
}

/* */
