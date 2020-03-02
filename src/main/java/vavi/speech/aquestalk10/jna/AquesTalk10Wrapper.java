/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.aquestalk10.jna;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.sun.jna.Pointer;

import vavi.speech.aquestalk10.jna.AquesTalk10.AQTK_VOICE;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * AquesTalk10Wrapper.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/09/18 umjammer initial version <br>
 */
@PropsEntity(url = "file://${user.dir}/local.properties")
public class AquesTalk10Wrapper {

    /* */
    static {
        com.sun.jna.NativeLibrary.addSearchPath("AquesTalk10", System.getProperty("java.library.path"));
    }

    /** */
    @Property
    String aquesTalk10DevKey;

    /** */
    private static AquesTalk10Wrapper instance;

    /** */
    public static AquesTalk10Wrapper getInstance() {
        if (instance == null) {
            try {
                instance = new AquesTalk10Wrapper();
                AquesTalk10Wrapper bean = new AquesTalk10Wrapper();
                PropsEntity.Util.bind(bean);
                int r = AquesTalk10.INSTANCE.AquesTalk_SetDevKey(bean.aquesTalk10DevKey.getBytes());
                if (r != 0) {
                    throw new IllegalStateException("wrong aquesTalk10DevKey");
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        return instance;
    }

    /* */
    private AquesTalk10Wrapper() {}

    /** */
    public static final Map<String, AQTK_VOICE> voices = new HashMap<String, AQTK_VOICE>() {
        {
            put("F1", AquesTalk10.gVoice_F1);
            put("F2", AquesTalk10.gVoice_F2);
            put("F3", AquesTalk10.gVoice_F3);
            put("M1", AquesTalk10.gVoice_M1);
            put("M2", AquesTalk10.gVoice_M2);
            put("R1", AquesTalk10.gVoice_R1);
            put("R2", AquesTalk10.gVoice_R2);
        }
    };

    /** */
    private AQTK_VOICE voice = AquesTalk10.gVoice_F1;

    /** */
    public void setVoice(AQTK_VOICE voice) {
        this.voice = voice;
    }

    /** PCM wave pointer */
    private Pointer wav = null;

    /**
     * @return PCM wave format
     * @throws IllegalArgumentException with error message
     * @see "https://docs.google.com/viewer?url=https%3A%2F%2Fwww.a-quest.com%2Farchive%2Fmanual%2Fsiyo_onseikigou.pdf"
     */
    public byte[] synthe(String text) {
        free();

        int[] size = new int[1];
        // omit unsupported chars.
        text = text.replaceAll("[!！　・\\ \\\n\\\r\\t]", ",");
        text = text.replaceAll("(\\d+)", "<NUM VAL=$1>");
        wav = AquesTalk10.INSTANCE.AquesTalk_Synthe_Utf8(voice, text, size);
        if (wav == null) {
            throw new IllegalArgumentException(AquesTalk10.errors.get(size[0]) + "\n" + text);
        }
        return wav.getByteArray(0, size[0]);
    }

    /* */
    private void free() {
        if (wav != null) {
            AquesTalk10.INSTANCE.AquesTalk_FreeWave(wav);
        }
    }

    /* */
    protected void finalize() throws Throwable {
        free();
    }
}

/* */
