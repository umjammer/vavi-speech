/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.phonemizer;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.rococoa.Foundation;
import org.rococoa.ID;
import org.rococoa.cocoa.CFRange;
import org.rococoa.cocoa.foundation.NSString;
import org.rococoa.internal.FoundationLibrary;

import com.ibm.icu.text.Transliterator;
import vavi.util.Debug;
import vavi.util.Locales;


/**
 * RococoaJaPhonemizer.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2022/01/19 umjammer initial version <br>
 * @see "https://gist.github.com/doraTeX/3163b5aef70951ac8c541c4c77ac6293"
 */
@Locales(countries = "Japan", languages = "Japanese")
public class RococoaJaPhonemizer implements JaPhonemizer {

    @Override
    public String phoneme(String input) {

        CFRange range = CFRange.make(0, input.length());

        ID jaLocaleIdentifier = Foundation.cfLocaleCreateCanonicalLanguageIdentifierFromString(null, "ja");
        ID locale = Foundation.cfLocaleCreate(null, jaLocaleIdentifier);

        ID tokenizer = Foundation.cfStringTokenizerCreate(null, input, range, FoundationLibrary.kCFStringTokenizerUnitWordBoundary, locale);
        int tokenType = Foundation.cfStringTokenizerGoToTokenAtIndex(tokenizer, 0);

        List<String> result = new ArrayList<>();

        while (tokenType != FoundationLibrary.kCFStringTokenizerTokenNone) {
            ID latin = Foundation.cfStringTokenizerCopyCurrentTokenAttribute(tokenizer, FoundationLibrary.kCFStringTokenizerAttributeLatinTranscription);

Debug.println(Level.FINER, "latin: " + latin + ", " + latin.isNull());
            if (!latin.isNull()) {
                NSString roman = NSString.stringWithString(Foundation.toString(latin));
Debug.println(Level.FINER, "roman: " + roman);
                // TODO doesn't work, help me
//                NSString hiragana = roman.stringByApplyingTransform(NSStringTransform.NSStringTransformLatinToHiragana, false);
//Debug.println(Level.FINER, "hiragana: " + hiragana);
//
//                result.add(hiragana.toString());
                result.add(roman.toString());

                Foundation.cfRelease(latin);
            }

            tokenType = Foundation.cfStringTokenizerAdvanceToNextToken(tokenizer);
        }

        Foundation.cfRelease(tokenizer);
        Foundation.cfRelease(locale);
        Foundation.cfRelease(jaLocaleIdentifier);

        String romans = String.join("", result.toArray(new String[0]));

        // icu4j (#stringByApplyingTransform() uses icu)
        Transliterator transliterator = Transliterator.getInstance("Latin-Hiragana");
        String hiragana = transliterator.transliterate(romans);

        return hiragana;
    }
}

/* */
