/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.aquestalk10.jna;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.HashMap;
import java.util.Map;

import com.sun.jna.Pointer;
import vavi.speech.aquestalk10.jna.AquesTalk10.AQTK_VOICE;
import vavi.util.CharNormalizerJa;
import vavi.util.properties.FormattedPropertiesFactory;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import static java.lang.System.getLogger;


/**
 * AquesTalk10Wrapper.
 * <p>
 * property file
 * <ul>
 *  <li>local.properties ... "aquesTalk10DevKey" aquestalk dev key, locate at "user.dir" directory</li>
 *  <li>aquestalk10.properties ... set phonemizer</li>
 *  <li>aquestalk_normalizer.properties ... definitions for replacing not supported char by aquestalk (internal)</li>
 * </ul>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/09/18 umjammer initial version <br>
 */
@PropsEntity(url = "file://${user.dir}/local.properties")
public class AquesTalk10Wrapper {

    private static final Logger logger = getLogger(AquesTalk10Wrapper.class.getName());

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
    public static final Map<String, AQTK_VOICE> voices = new HashMap<>() {
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

    /** text replacement table before speaking */
    private static final FormattedPropertiesFactory.Basic replaceMap =
            new FormattedPropertiesFactory.Basic("/aquestalk_normalizer.properties", "text.replace.%s");

    /**
     * @return PCM wave format
     * @throws IllegalArgumentException with error message
     * @see "https://docs.google.com/viewer?url=https%3A%2F%2Fwww.a-quest.com%2Farchive%2Fmanual%2Fsiyo_onseikigou.pdf"
     */
    public byte[] synthesize(String text) {
        free();

        text = CharNormalizerJa.ToHiragana.normalize(text);

        // omit unsupported chars.
        int i = 1;
        while (replaceMap.get(i + ".src") != null) {
logger.log(Level.TRACE, "\"" + replaceMap.get(i + ".src") + "\"->\"" + replaceMap.get(i + ".dest") + "\"");
            text = text.replaceAll(replaceMap.get(i + ".src"), replaceMap.get(i + ".dest"));
            i++;
        }
logger.log(Level.TRACE, "text: " + text);
        int[] size = new int[1];
        wav = AquesTalk10.INSTANCE.AquesTalk_Synthe_Utf8(voice, text, size);
        if (wav == null) {
logger.log(Level.ERROR, "wave: " + AquesTalk10.errors.get(size[0]) + "\n" + text);
            StringBuilder sb = new StringBuilder();
            for (char c : text.toCharArray()) {
                wav = AquesTalk10.INSTANCE.AquesTalk_Synthe_Utf8(voice, String.valueOf(c), size);
                if (wav == null) {
                    sb.append(c);
                } else {
                    sb.append(Character.isIdeographic(c) ? "　" : " ");
                }
            }
            throw new IllegalArgumentException(AquesTalk10.errors.get(size[0]) + "\n" + text + "\n" + sb);
        }
logger.log(Level.TRACE, "wave: " + size[0]);
        return wav.getByteArray(0, size[0]);
    }

    /** free allocation for {@link #wav} */
    private void free() {
        if (wav != null) {
            AquesTalk10.INSTANCE.AquesTalk_FreeWave(wav);
        }
    }
}
