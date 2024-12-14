/*
 * https://github.com/ruche7/MMDLipTools/blob/master/MMDLipCore/src/mmd/morph/lip/converters/DigitKanaConverter.cs
 */

package vavi.speech.phonemizer;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vavi.util.CharNormalizerJa;
import vavi.util.Locales;

import static java.lang.System.getLogger;


/**
 * A class that converts a string of numbers into kana readings.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/02/28 umjammer initial version <br>
 */
@Locales(languages = "Japanese")
public class DigitJaPhonemizer implements JaPhonemizer {

    private static final Logger logger = getLogger(DigitJaPhonemizer.class.getName());

    /**
     * The phonetic component of the integer part.
     */
    private enum NumberPhoneType {

        None {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                return getDefaultNumberKana(t);
            }
        },

        Point {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                return getDefaultNumberKana(t);
            }
        },

        Digit0 {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                if (!prev.isNone() || !next.isNoneOrPoint()) {
                    return "";
                }
                if (next.isPoint()) {
                    return "れー";
                }
                return getDefaultNumberKana(t);
            }
        },
        Digit1 {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                if (next.isBase()) {
                    return "";
                }
                if (next.isPoint() || next == NumberPhoneType.Cyou || next == NumberPhoneType.Kei) {
                    return "いっ";
                }
                return getDefaultNumberKana(t);
            }
        },
        Digit2 {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                if (next.isPoint()) {
                    return "にー";
                }
                return getDefaultNumberKana(t);
            }
        },
        Digit3 {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                return getDefaultNumberKana(t);
            }
        },
        Digit4 {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                return getDefaultNumberKana(t);
            }
        },
        Digit5 {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                if (next.isPoint()) {
                    return "ごー";
                }
                return getDefaultNumberKana(t);
            }
        },
        Digit6 {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                if (next == NumberPhoneType.Hundred || next == NumberPhoneType.Kei) {
                    return "ろっ";
                }
                return getDefaultNumberKana(t);
            }
        },
        Digit7 {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                return getDefaultNumberKana(t);
            }
        },
        Digit8 {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                if (next.isPoint() || next == NumberPhoneType.Hundred || next == NumberPhoneType.Thousand ||
                    next == NumberPhoneType.Cyou || next == NumberPhoneType.Kei) {
                    return "はっ";
                }
                return getDefaultNumberKana(t);
            }
        },
        Digit9 {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                return getDefaultNumberKana(t);
            }
        },

        Ten {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                if (next.isPoint() || next == NumberPhoneType.Cyou || next == NumberPhoneType.Kei ||
                    next == NumberPhoneType.Kou || next == NumberPhoneType.Kan || next == NumberPhoneType.Sei ||
                    next == NumberPhoneType.Sai) {
                    return "じゅっ";
                }
                return getDefaultNumberKana(t);
            }
        },
        Hundred {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                if (prev == NumberPhoneType.Digit3) {
                    return "びゃく";
                }
                if (prev == NumberPhoneType.Digit6 || prev == NumberPhoneType.Digit8) {
                    return "ぴゃく";
                }
                return getDefaultNumberKana(t);
            }
        },
        Thousand {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                if (prev == NumberPhoneType.Digit3) {
                    return "ぜん";
                }
                return getDefaultNumberKana(t);
            }
        },
        Man {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                return getDefaultNumberKana(t);
            }
        },
        Oku {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                return getDefaultNumberKana(t);
            }
        },
        Cyou {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                return getDefaultNumberKana(t);
            }
        },
        Kei {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                return getDefaultNumberKana(t);
            }
        },
        Gai {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                return getDefaultNumberKana(t);
            }
        },
        Jo {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                return getDefaultNumberKana(t);
            }
        },
        Jou {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                return getDefaultNumberKana(t);
            }
        },
        Kou {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                return getDefaultNumberKana(t);
            }
        },
        Kan {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                return getDefaultNumberKana(t);
            }
        },
        Sei {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                return getDefaultNumberKana(t);
            }
        },
        Sai {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                return getDefaultNumberKana(t);
            }
        },
        Goku {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                return getDefaultNumberKana(t);
            }
        },
        Gougasya {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                return getDefaultNumberKana(t);
            }
        },
        Asougi {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                return getDefaultNumberKana(t);
            }
        },
        Nayuta {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                return getDefaultNumberKana(t);
            }
        },
        Fukashigi {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                return getDefaultNumberKana(t);
            }
        },
        Infinity {
            String numberKanaDecider(NumberPhoneType t, NumberPhoneType prev, NumberPhoneType next) {
                return getDefaultNumberKana(t);
            }
        };

        static final int Begin = None.ordinal();

        static final int DigitBegin = Digit0.ordinal();

        static final int DigitEnd = Ten.ordinal();

        static final int BaseBegin = Ten.ordinal();

        static final int BaseEnd = Man.ordinal();

        static final int BigBegin = Man.ordinal();

        static final int BigEnd = Infinity.ordinal();

        static final int End = Infinity.ordinal();

        /**
         * Gets whether or not this value is valid.
         *
         * @return true if the value is valid.
         */
        public boolean isDefined() {
            return ordinal() >= NumberPhoneType.Begin && ordinal() < NumberPhoneType.End;
        }

        /**
         * Checks whether this represents None.
         *
         * @return None ならば true 。
         */
        public boolean isNone() {
            return this == NumberPhoneType.None;
        }

        /**
         * Gets whether this represents a Point.
         *
         * @return Returns true if it is a Point.
         */
        public boolean isPoint() {
            return this == NumberPhoneType.Point;
        }

        /**
         * Gets whether this represents None or a Point.
         *
         * @return true if None or Point.
         */
        public boolean isNoneOrPoint() {
            return isNone() || isPoint();
        }

        /**
         * An extension method to get whether or not this represents a digit between 0 and 9.
         *
         * @return True if Digit0 through Digit9.
         */
        @SuppressWarnings("unused")
        public boolean isDigit() {
            return ordinal() >= NumberPhoneType.DigitBegin && ordinal() < NumberPhoneType.DigitEnd;
        }

        /**
         * An extension method to get whether the number represents the tens to thousands digits.
         *
         * @return True if the result is Ten, Hundred, or Thousand.
         */
        public boolean isBase() {
            return ordinal() >= NumberPhoneType.BaseBegin && ordinal() < NumberPhoneType.BaseEnd;
        }

        /**
         * An extension method that gets whether this represents a multiplication digit of 10000.
         *
         * @return True if the value is one of Man through Infinity.
         */
        public boolean isBig() {
            return ordinal() >= NumberPhoneType.BigBegin && ordinal() < NumberPhoneType.BigEnd;
        }

        /**
         * A delegate that determines the pronunciation of the integer component.
         *
         * @param type Type of target.
         * @param prevType The type of the preceding element.
         * @param nextType The type of the following element.
         * @return Pronunciation kana.
         */
        abstract String numberKanaDecider(NumberPhoneType type, NumberPhoneType prevType, NumberPhoneType nextType);
    }

    /**
     * The default pronunciation for the NumberPhoneType.
     */
    private static final String[] DefaultNumberKanas = {
        "", "てん",

        "ぜろ", "いち", "に", "さん", "よん", "ご", "ろく", "なな", "はち", "きゅう",

        "じゅう", "ひゃく", "せん",

        "まん", "おく", "ちょう", "けい", "がい", "じょ", "じょう", "こう", "かん", "せい", "さい", "ごく", "ごうがしゃ", "あそうぎ", "なゆた", "ふかしぎ", "むりょうたいすう",
    };

    /**
     * Gets the default reading of the integer component.
     * 
     * @param type Type of target.
     * @return Pronunciation kana.
     */
    private static String getDefaultNumberKana(NumberPhoneType type) {
        return DefaultNumberKanas[type.ordinal()];
    }

    /**
     * The pronunciation of each digit in the decimal part (except the last digit).
     */
    private static final String[] RemainKanas = {
        "ぜろ", "いち", "にー", "さん", "よん", "ごー", "ろく", "なな", "はち", "きゅう",
    };

    /**
     * The pronunciation of the last digit in the decimal place. Not used when there are exactly two digits in the decimal place.
     */
    private static final String[] LastRemainKanas = {
        "ぜろ", "いち", "に", "さん", "よん", "ご", "ろく", "なな", "はち", "きゅう",
    };

    /**
     * A regular expression that matches numbers.
     */
    private static final Pattern rexNumber = Pattern.compile("([1-9１-９][0-9０-９]*|[0０])([\\.\\．][0-9０-９]+)?");

    /**
     * Determine the pronunciation of the integer part element.
     *
     * @param type Target type.
     * @param prevType The type of the preceding element.
     * @param nextType The type of the following element.
     * @return Pronunciation kana.
     */
    private static String decideNumberKana(NumberPhoneType type, NumberPhoneType prevType, NumberPhoneType nextType) {
        if (!type.isDefined()) {
            return "";
        }
        return type.numberKanaDecider(type, prevType, nextType);
    }

    /**
     * Converts a string of numbers into kana readings.
     *
     * @param src A string.
     * @return The converted string.
     */
    public String convertFrom(String src) {
        Matcher matcher = rexNumber.matcher(src);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, phoneme(matcher.group(1)));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Gets the pronunciation of a string that represents a number.
     *
     * @param src A string representing a number.
     * @return The phonetic pronunciation of the string.
     */
    public String phoneme(String src) {
        StringBuilder dest = new StringBuilder();

        // Get the integer and decimal part of the half-width string
        String[] numParts = getNumberParts(src);

        // With or without decimal point
        boolean remainExist = numParts.length >= 2 && !numParts[1].isEmpty();

        // Add the reading of the integer part to the buffer
        appendNumberPhonetic(numParts[0], remainExist, dest);

        if (remainExist) {
            // Add the reading of the decimal point to the buffer
            appednRemainPhonetic(numParts[1], dest);
        }

        return dest.toString();
    }

    /**
     * The string representing a number is converted to half-width characters, commas are removed,
     * and the string is divided into an integer part and a decimal part.
     *
     * @param src A string representing a number.
     * @return An array of integer and fractional strings.
     */
    private static String[] getNumberParts(String src) {
        // Convert full-width to half-width
        String text = CharNormalizerJa.ToHalf.normalize(src);

        // Remove comma
        text = text.replace(",", "");

        // Separated by a period
        return text.split("\\.");
    }

    /** The type of the last added element to be set to. If nothing was added, prevType is set. */
    private NumberPhoneType lastType;

    /**
     * The pronunciation of the string representing the integer part is added to the buffer.
     *
     * @param src A string of digits.
     * @param remainExist True if a fraction follows.
     * @param dest The string buffer to append to.
     * @return The length of the added string.
     */
    private int appendNumberPhonetic(String src, boolean remainExist, StringBuilder dest) {
        int appendLen = 0;

        int len = src.length();
        NumberPhoneType prevType = NumberPhoneType.None;

        for (int i = 0; i < len;) {
            // Calculate the position to 4 digits
            int bigPos = (len - 1 - i) / 4;

            // Obtained in 4-digit units
            String part = src.substring(i, len - bigPos * 4);

            // Advance the position by the number of characters obtained
            i += part.length();

            // Determine the 4-digit unit type
            NumberPhoneType baseType;
            if (bigPos == 0) {
                baseType = remainExist ? NumberPhoneType.Point : NumberPhoneType.None;
            } else {
                if (NumberPhoneType.BigBegin + bigPos - 1 > NumberPhoneType.BigEnd) {
                    // Skip because it's too big
logger.log(Level.DEBUG, "too big: " + src);
                    continue;
                }
                baseType = NumberPhoneType.values()[NumberPhoneType.BigBegin + bigPos - 1];
                if (!baseType.isBig()) {
                    // Skip because it's too big
logger.log(Level.DEBUG, "too big: " + src);
                    continue;
                }
            }

            // Add reading
            lastType = NumberPhoneType.None;
            appendLen += appendThousandPartPhonetic(part,
                                                    prevType,
                                                    baseType,
                                                    dest);

            // Update the leading type
            prevType = lastType;
        }

        return appendLen;
    }

    /**
     * The pronunciation of the string representing the fractional part is added to the buffer.
     *
     * @param src A string of digits.
     * @param dest The string buffer to append to.
     * @return The length of the added string.
     */
    private int appednRemainPhonetic(String src, StringBuilder dest) {
        int oldLen = dest.length();

        int len = src.length();
        for (int i = 0; i < len; ++i) {
            // The final digit is pronounced differently except for the two digits.
            // 0.2 … れーてんに
            // 0.22 … れーてんにーにー
            // 0.222 … れーてんにーにーに
            String[] kanas = (len != 2 && i == len - 1) ? LastRemainKanas : RemainKanas;

            int d = parseChar(src.charAt(i));
            dest.append(kanas[d]);
        }

        return dest.length() - oldLen;
    }

    /**
     * Add the reading in four-digit units to the buffer.
     *
     * @param src A string of numbers. 1 to 4 characters.
     * @param prevType The type of the preceding element.
     * @param baseType Type of 4-digit unit element.
     * @param dest The string buffer to append to.
     *
     * @return The length of the added string.
     */
    private int appendThousandPartPhonetic(String src,
                                           NumberPhoneType prevType,
                                           NumberPhoneType baseType,
                                           StringBuilder dest) {
        int oldLen = dest.length();

        // For now, set prevType
        lastType = prevType;

        // Remove leading "0"
        String text = src.replaceFirst("^0*", "");

        // Calculate the length
        int len = text.length();

        if (len == 0) {
            // If there is only "0"
            String zero = decideNumberKana(NumberPhoneType.Digit0, lastType, baseType);
            if (!zero.isEmpty()) {
                dest.append(zero);
                lastType = NumberPhoneType.Digit0;
            }
        } else {
            // Create a type array (does not include baseType)
            List<NumberPhoneType> types = new ArrayList<>();
            for (int i = 0; i < len; ++i) {
                // Numbers
                int d = parseChar(text.charAt(i));
                if (d == 0) {
                    // If "0", skip including the following
                    continue;
                }
                types.add(NumberPhoneType.values()[NumberPhoneType.DigitBegin + d]);

                // Trailing digits
                int pos = (len - 1) - i - 1; // 0:十, 1:百, 2:千
                if (pos >= 0) {
                    types.add(NumberPhoneType.values()[NumberPhoneType.BaseBegin + pos]);
                }
            }

            // Process in order
            for (int i = 0; i < types.size(); ++i) {
                NumberPhoneType t = types.get(i);
                NumberPhoneType next = i + 1 < types.size() ? types.get(i + 1) : baseType;
                String s = decideNumberKana(t, lastType, next);
                if (!s.isEmpty()) {
                    dest.append(s);
                    lastType = t;
                }
            }
        }

        // Process baseType if either
        // - baseType == NumberPhoneType.Point
        // - One or more characters were added
        if (baseType.isPoint() || dest.length() > oldLen) {
            String baseText = decideNumberKana(baseType, lastType, NumberPhoneType.None);
            if (!baseText.isEmpty()) {
                dest.append(baseText);
                lastType = baseType;
            }
        }

        return dest.length() - oldLen;
    }

    /**
     * Converts characters to numbers.
     *
     * @param c character.
     * @return A number, or 0 if conversion is not possible.
     */
    private static int parseChar(char c) {
        return Character.isDigit(c) ? Integer.parseInt(String.valueOf(c)) : 0;
    }
}
