/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.aquestalk10.jsapi;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.speech.Engine;
import javax.speech.EngineCreate;
import javax.speech.EngineException;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;

import vavi.speech.aquestalk10.jna.AquesTalk10.AQTK_VOICE;
import vavi.speech.aquestalk10.jna.AquesTalk10Wrapper;


/**
 * Represents a SynthesizerModeDesc for the AquesTalkSynthesizer. A
 * AquesTalkSynthesizerModeDesc adds an audio player to the standard mode items.
 */
public class AquesTalk10SynthesizerModeDesc extends SynthesizerModeDesc implements EngineCreate {

    /**
     * Creates a fully-specified descriptor. Any of the features may be
     * <code>null</code>.
     *
     * @param engineName the name of the engine
     * @param modeName the name of the mode
     * @param locale the locale associated with this mode
     */
    public AquesTalk10SynthesizerModeDesc(String engineName, String modeName, Locale locale) {
        super(engineName, modeName, locale, Boolean.FALSE, null);
    }

    /**
     * Returns the valid voices in this synthesizer mode.
     *
     * @return an array of valid voices, if no valid voices, it will return an
     *         array of size 0
     */
    @Override
    public Voice[] getVoices() {
        List<Voice> voiceList = new LinkedList<>();
        int count = 0;
        for (Map.Entry<String, AQTK_VOICE> aquesTalkVoice : AquesTalk10Wrapper.voices.entrySet()) {
            Voice voice = new Voice(aquesTalkVoice.getKey(),
                                    toGender(aquesTalkVoice.getValue()),
                                    Voice.AGE_DONT_CARE,
                                    aquesTalkVoice.getKey());
            voiceList.add(voice);
            count++;
        }
        Voice[] validVoices = new Voice[count];
        voiceList.toArray(validVoices);

        return validVoices;
    }

    /** */
    private static int toGender(AQTK_VOICE voice) {
        return switch (voice.bas) {
            case 0 -> Voice.GENDER_FEMALE;
            case 1 -> Voice.GENDER_FEMALE;
            case 2 -> Voice.GENDER_MALE;
            default -> Voice.GENDER_DONT_CARE;
        };
    }

    /**
     * Constructs a AquesTalkSynthesizer with the properties of this mode
     * descriptor.
     *
     * @return a synthesizer that matches the mode
     *
     * @throws IllegalArgumentException if the properties of this descriptor do
     *             not match any known engine or mode
     * @throws EngineException if the engine could not be created
     * @throws SecurityException if the caller does not have permission to use
     *             the speech engine
     */
    @Override
    public Engine createEngine() throws IllegalArgumentException, EngineException, SecurityException {
        AquesTalk10Synthesizer s = new AquesTalk10Synthesizer(this);
        return s;
    }
}
