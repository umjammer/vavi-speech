/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.googlecloud.jsapi;

import javax.speech.EngineCentral;
import javax.speech.EngineList;
import javax.speech.EngineModeDesc;


/**
 * Supports the EngineCentral JSAPI 1.0 interface for the GoogleCloudTextToSpeechSynthesizer.
 * To use a GoogleCloudTextToSpeechSynthesizer, you should place a line into the
 * speech.properties file as so:
 *
 * <p>
 * specify the environment variable "GOOGLE_APPLICATION_CREDENTIALS"
 * for google credential json.
 * </p>
 *
 * <pre>
 * GoogleCloudEngineCentral = vavi.speech.googlectts.jsapi.GoogleCloudEngineCentral
 * </pre>
 */
public class GoogleCloudEngineCentral implements EngineCentral {

//  private String engineName = "GoogleCloudTextToSpeech Synthesizer";

    /**
     * Creates a GoogleCloudEngineCentral
     */
    public GoogleCloudEngineCentral() throws Exception {
        // Note that the JSAPI layer currently is silent
        // about any exceptions thrown from here, so we are noisy here
    }

    /**
     * Returns a list containing references to all matching synthesizers. The
     * mapping of GoogleCloud VoiceDirectories and Voices to JSAPI Synthesizers
     * and Voices is as follows:
     * 
     * <p>
     * <ul>
     * <li>Each Google Cloud Speech VoiceDirectory specifies the list of GoogleCloud Voices
     * supported by that directory. Each Voice in that directory specifies its
     * name (e.g., "kevin" "kevin16" "alan"), domain (e.g., "general" or "time")
     * and locale (e.g., Locale.US).
     * <li>For all Google Cloud Voices from all VoiceDirectories discovered by the
     * VoiceManager, this method will group the Voices according to those that
     * have both a common locale and domain (e.g, all "general" domain voices
     * for the US local will be grouped together).
     * <li>For each group of voices that shares a common locale and domain,
     * this method generates a new JSAPI SynthesizerModeDesc with the following
     * attributes:
     * <ul>
     * <li>The engine name is of the form: "GoogleCloudSpeech &lt;locale> &lt;domain>
     * synthesizer" For example, "GoogleCloud en_us general synthesizer"
     * <li>The locale is the locale shared by all the voices (e.g., Locale.US)
     * <li>The mode name is the domain shared by all the voices (e.g.,
     * "general").
     * </ul>
     * <li>The JSAPI Voices for each resulting Synthesizer will have the name
     * of the AquesTalk Voice (e.g. "kevin" "kevin16").
     * </ul>
     * 
     * @param require an engine mode that describes the desired synthesizer
     * 
     * @return an engineList containing matching engines, or null if no matching
     *         engines are found
     */
    @SuppressWarnings("unchecked")
    @Override
    public EngineList createEngineList(EngineModeDesc require) {
        EngineList el = new EngineList();
        el.add(this);
        return el;
    }
}

/* */
