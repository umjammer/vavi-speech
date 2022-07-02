/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.phonemizer;

import java.util.ArrayList;
import java.util.List;

import org.rococoa.Foundation;
import org.rococoa.ID;
import org.rococoa.cocoa.CFRange;
import org.rococoa.cocoa.foundation.NSString;
import org.rococoa.internal.FoundationLibrary;

import com.ibm.icu.text.Transliterator;
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

        ID jaLocaleIdentifier = Foundation.cfLocaleCreateCanonicalLanguageIdentifierFromString(FoundationLibrary.kCFAllocatorDefault, "ja");
        ID locale = Foundation.cfLocaleCreate(FoundationLibrary.kCFAllocatorDefault, jaLocaleIdentifier);

        ID tokenizer = Foundation.cfStringTokenizerCreate(FoundationLibrary.kCFAllocatorDefault, input, range, FoundationLibrary.kCFStringTokenizerUnitWordBoundary, locale);
        int tokenType = Foundation.cfStringTokenizerGoToTokenAtIndex(tokenizer, 0);

        List<String> result = new ArrayList<String>();

        while (tokenType != FoundationLibrary.kCFStringTokenizerTokenNone) {
            ID latin = Foundation.cfStringTokenizerCopyCurrentTokenAttribute(tokenizer, FoundationLibrary.kCFStringTokenizerAttributeLatinTranscription);

//System.err.println("latin: " + latin + ", " + latin.isNull());
            if (!latin.isNull()) {
                NSString roman = NSString.stringWithString(Foundation.toString(latin));
//System.err.println("roman: " + roman);
                // TODO doesn't work, help me
//                NSString hiragana = roman.stringByApplyingTransform(NSStringTransform.NSStringTransformLatinToHiragana, false);
//System.err.println("hiragana: " + hiragana);
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

        String romans = String.join("", result.toArray(new String[result.size()]));

        // icu4j (#stringByApplyingTransform() uses icu)
        Transliterator transliterator = Transliterator.getInstance("Latin-Hiragana");
        String hiragana = transliterator.transliterate(romans);

        return hiragana;
    }
}

/* */
