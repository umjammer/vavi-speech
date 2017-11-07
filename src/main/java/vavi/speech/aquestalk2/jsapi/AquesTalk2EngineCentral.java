/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.aquestalk2.jsapi;

import javax.speech.EngineCentral;
import javax.speech.EngineList;
import javax.speech.EngineModeDesc;


/**
 * Supports the EngineCentral JSAPI 1.0 interface for the AquesTalkSynthesizer.
 * To use a AquesTalkSynthesizer, you should place a line into the
 * speech.properties file as so:
 *
 * <pre>
 * AquesTalkSynthEngineCentral = speech.aquesTalk.jsapi.AquesTalkEngineCentral
 * </pre>
 *
 */
public class AquesTalk2EngineCentral implements EngineCentral {

//  private String engineName = "AquesTalk Synthesizer";

    /**
     * Creates a AquesTalkEngineCentral
     */
    public AquesTalk2EngineCentral() throws Exception {
        // Note that the JSAPI layer currently is silent
        // about any exceptions thrown from here, so we are noisy here
    }

    /**
     * Returns a list containing references to all matching synthesizers. The
     * mapping of AquesTalk VoiceDirectories and Voices to JSAPI Synthesizers
     * and Voices is as follows:
     *
     * <p>
     * <ul>
     * <li>Each AquesTalk VoiceDirectory specifies the list of AquesTalk Voices
     * supported by that directory. Each Voice in that directory specifies its
     * name (e.g., "kevin" "kevin16" "alan"), domain (e.g., "general" or "time")
     * and locale (e.g., Locale.US).
     * <li>For all AquesTalk Voices from all VoiceDirectories discovered by the
     * VoiceManager, this method will group the Voices according to those that
     * have both a common locale and domain (e.g, all "general" domain voices
     * for the US local will be grouped together).
     * <li>For each group of voices that shares a common locale and domain,
     * this method generates a new JSAPI SynthesizerModeDesc with the following
     * attributes:
     * <ul>
     * <li>The engine name is of the form: "AquesTalk &lt;locale> &lt;domain>
     * synthesizer" For example, "AquesTalk en_us general synthesizer"
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
    public EngineList createEngineList(EngineModeDesc require) {
        EngineList el = new EngineList();
        el.add(this);
        return el;
    }
}

/* */
