/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.aquestalk.jsapi;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.speech.Engine;
import javax.speech.EngineCreate;
import javax.speech.EngineException;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;


/**
 * Represents a SynthesizerModeDesc for the AquesTalkSynthesizer. A
 * AquesTalkSynthesizerModeDesc adds an audio player to the standard mode items.
 */
public class AquesTalkSynthesizerModeDesc extends SynthesizerModeDesc implements EngineCreate {

    /**
     * Creates a fully-specified descriptor. Any of the features may be
     * <code>null</code>.
     *
     * @param engineName the name of the engine
     * @param modeName the name of the mode
     * @param locale the locale associated with this mode
     */
    public AquesTalkSynthesizerModeDesc(String engineName, String modeName, Locale locale) {
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
        javax.speech.synthesis.Voice[] voices = super.getVoices();
        int count = 0;
        for (Voice aquesTalkVoice : voices) {
            try {
                voiceList.add(aquesTalkVoice);
                count++;
            } catch (Exception ve) {
                // don't do anything here if a AquesTalkVoice is invalid
            }
        }
        javax.speech.synthesis.Voice[] validVoices = new javax.speech.synthesis.Voice[count];
        voiceList.toArray(validVoices);

        return validVoices;
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
        AquesTalkSynthesizer s = new AquesTalkSynthesizer(this);
        return s;
    }
}

/* */