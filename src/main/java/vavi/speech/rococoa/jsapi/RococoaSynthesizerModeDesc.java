/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.rococoa.jsapi;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.speech.Engine;
import javax.speech.EngineCreate;
import javax.speech.EngineException;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;

import org.rococoa.contrib.appkit.NSSpeechSynthesizer;
import org.rococoa.contrib.appkit.NSVoice;


/**
 * Represents a SynthesizerModeDesc for the AquesTalkSynthesizer. A
 * AquesTalkSynthesizerModeDesc adds an audio player to the standard mode items.
 */
public class RococoaSynthesizerModeDesc extends SynthesizerModeDesc implements EngineCreate {

    /**
     * Creates a fully-specified descriptor. Any of the features may be
     * <code>null</code>.
     *
     * @param engineName the name of the engine
     * @param modeName the name of the mode
     * @param locale the locale associated with this mode
     */
    public RococoaSynthesizerModeDesc(String engineName, String modeName, Locale locale) {
        super(engineName, modeName, locale, Boolean.FALSE, null);
    }

    /**
     * Returns the valid voices in this synthesizer mode.
     *
     * @return an array of valid voices, if no valid voices, it will return an
     *         array of size 0
     */
    public Voice[] getVoices() {
        List<Voice> voiceList = new LinkedList<>();
        int count = 0;
        for (NSVoice nativeVoice : NSSpeechSynthesizer.availableVoices()) {
//System.err.println(nativeVoice.getName() + ": " + nativeVoice.getIdentifier());
            Voice voice = new Voice(nativeVoice.getName(),
                                    toGenger(nativeVoice.getGender()),
                                    nativeVoice.getAge(),
                                    nativeVoice.getIdentifier());
            voiceList.add(voice);
            count++;
        }
        Voice[] validVoices = new Voice[count];
        voiceList.toArray(validVoices);

        return validVoices;
    }

    /** */
    private static int toGenger(NSVoice.VoiceGender gender) {
        switch (gender) {
        case Female: return Voice.GENDER_FEMALE;
        case Male: return Voice.GENDER_MALE;
        case Neuter: return Voice.GENDER_NEUTRAL;
        default: return Voice.GENDER_DONT_CARE;
        }
    }

    /**
     * Constructs a AquesTalkSynthesizer with the properties of this mode
     * descriptor.
     *
     * @return a synthesizer that mathes the mode
     *
     * @throws IllegalArgumentException if the properties of this descriptor do
     *             not match any known engine or mode
     * @throws EngineException if the engine could not be created
     * @throws SecurityException if the caller does not have permission to use
     *             the speech engine
     */
    public Engine createEngine() throws IllegalArgumentException, EngineException, SecurityException {
        RococoaSynthesizer s = new RococoaSynthesizer(this);
        return s;
    }
}

/* */