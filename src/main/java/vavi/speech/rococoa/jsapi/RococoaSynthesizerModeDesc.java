/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.rococoa.jsapi;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.speech.Engine;
import javax.speech.EngineCreate;
import javax.speech.EngineException;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;

import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSObject;
import vavix.rococoa.avfoundation.AVSpeechSynthesisVoice;

import static java.lang.System.getLogger;


/**
 * Represents a SynthesizerModeDesc for the AquesTalkSynthesizer. A
 * AquesTalkSynthesizerModeDesc adds an audio player to the standard mode items.
 */
public class RococoaSynthesizerModeDesc extends SynthesizerModeDesc implements EngineCreate {

    private static final Logger logger = getLogger(RococoaSynthesizerModeDesc.class.getName());

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
    @Override
    public Voice[] getVoices() {
        List<Voice> voiceList = new LinkedList<>();
        int count = 0;
        for (NSObject o : AVSpeechSynthesisVoice.speechVoices()) {
            AVSpeechSynthesisVoice nativeVoice = Rococoa.cast(o, AVSpeechSynthesisVoice.class);
logger.log(Level.TRACE, nativeVoice.name() + ": " + nativeVoice.identifier());
            Voice voice = new Voice(nativeVoice.name(),
                                    toGenger(nativeVoice.gender()),
                                    Voice.AGE_DONT_CARE,
                                    nativeVoice.identifier());
            voiceList.add(voice);
            count++;
        }
        Voice[] validVoices = new Voice[count];
        voiceList.toArray(validVoices);

        return validVoices;
    }

    /** */
    private static int toGenger(int gender) {
        return switch (gender) {
            case 2 -> Voice.GENDER_FEMALE;
            case 1 -> Voice.GENDER_MALE;
            case 0 -> Voice.GENDER_NEUTRAL;
            default -> Voice.GENDER_DONT_CARE;
        };
    }

    /**
     * Constructs a RococaSynthesizer with the properties of this mode
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
        RococoaSynthesizer s = new RococoaSynthesizer(this);
        return s;
    }
}
