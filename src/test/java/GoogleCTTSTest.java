/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import org.junit.jupiter.api.Test;

import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;

import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import vavi.speech.JavaSoundPlayer;
import vavi.speech.Player;


/**
 * GoogleCTTSTest. (google cloud text to speech)
 *
 * <p>
 * specify the environment variable "GOOGLE_APPLICATION_CREDENTIALS"
 * for google credential json.
 * </p>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
@EnabledIfEnvironmentVariable(named = "GOOGLE_APPLICATION_CREDENTIALS", matches = ".*")
class GoogleCTTSTest {

    @Test
    void test01() throws Exception {
        speak("すもももももももものうち");
    }

    /** */
    static void speak(String text) throws Exception {
        // Instantiates a client
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            // Set the text input to be synthesized
            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

            // Build the voice request
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("ja_JP")
//                    .setSsmlGender(SsmlVoiceGender.FEMALE)
                    .setName("ja-JP-Wavenet-B")
                    .build();

            // Select the type of audio file you want returned
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.LINEAR16)
                    .build();

            // Perform the text-to-speech request
            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // Get the audio contents from the response
            ByteString audioContents = response.getAudioContent();

            Player player = new JavaSoundPlayer();
            player.setVolume(0.03f);
            player.play(audioContents.toByteArray());
        }
    }

    /** */
    public static void main(String[] args) throws Exception {
        GoogleCTTSTest app = new GoogleCTTSTest();
        GoogleCTTSTest.speak(args[0]);
    }
}
