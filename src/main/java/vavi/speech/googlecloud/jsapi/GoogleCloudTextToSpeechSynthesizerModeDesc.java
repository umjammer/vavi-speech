/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.googlecloud.jsapi;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.speech.Engine;
import javax.speech.EngineCreate;
import javax.speech.EngineException;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;

import com.google.cloud.texttospeech.v1.ListVoicesRequest;
import com.google.cloud.texttospeech.v1.ListVoicesResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;


/**
 * Represents a SynthesizerModeDesc for the GoogleCloudSpeechSynthesizer. A
 * GoogleCloudTextToSpeechSynthesizerModeDesc adds an audio player to the
 * standard mode items.
 */
public class GoogleCloudTextToSpeechSynthesizerModeDesc extends SynthesizerModeDesc implements EngineCreate {

    /**
     * Creates a fully-specified descriptor. Any of the features may be
     * <code>null</code>.
     *
     * @param engineName the name of the engine
     * @param modeName the name of the mode
     * @param locale the locale associated with this mode
     */
    public GoogleCloudTextToSpeechSynthesizerModeDesc(String engineName, String modeName, Locale locale) {
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
        for (com.google.cloud.texttospeech.v1.Voice nativeVoice : listAllSupportedVoices()) {
            Voice voice = new Voice(nativeVoice.getName(),
                                    nativeVoice.getSsmlGenderValue(),
                                    Voice.AGE_DONT_CARE,
                                    nativeVoice.getName());
            voiceList.add(voice);
            count++;
        }
        Voice[] validVoices = new javax.speech.synthesis.Voice[count];
        voiceList.toArray(validVoices);

        return validVoices;
    }

    /** */
    private static List<com.google.cloud.texttospeech.v1.Voice> listAllSupportedVoices() {
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {

            ListVoicesRequest request = ListVoicesRequest.getDefaultInstance();

            ListVoicesResponse response = textToSpeechClient.listVoices(request);
            List<com.google.cloud.texttospeech.v1.Voice> voices = response.getVoicesList();

//            for (com.google.cloud.texttospeech.v1.Voice voice : voices) {
//System.out.format("Name: %s\n", voice.getName());
//
//                List<ByteString> languageCodes = voice.getLanguageCodesList().asByteStringList();
//for (ByteString languageCode : languageCodes) {
// System.out.format("Supported Language: %s\n", languageCode.toStringUtf8());
//}
//
// System.out.format("SSML Voice Gender: %s\n", voice.getSsmlGender());
//
// System.out.format("Natural Sample Rate Hertz: %s\n\n", voice.getNaturalSampleRateHertz());
//            }

            return voices;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Constructs a GoogleCloudTextToSpeechSynthesizer with the properties of
     * this mode
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
        GoogleCloudTextToSpeechSynthesizer s = new GoogleCloudTextToSpeechSynthesizer(this);
        return s;
    }
}

/* */
