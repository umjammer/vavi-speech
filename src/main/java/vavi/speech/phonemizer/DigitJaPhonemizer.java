/*
 * https://github.com/ruche7/MMDLipTools/blob/master/MMDLipCore/src/mmd/morph/lip/converters/DigitKanaConverter.cs
 */

package vavi.speech.phonemizer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vavi.util.CharNormalizerJa;


/**
 * 文字列中の数字列を読み仮名に変換するクラス。
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/02/28 umjammer initial version <br>
 */
public class DigitJaPhonemizer implements JaPhonemizer {

    /**
     * 整数部の読み仮名の構成要素。
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

        static int Begin = None.ordinal();

        static int DigitBegin = Digit0.ordinal();

        static int DigitEnd = Ten.ordinal();

        static int BaseBegin = Ten.ordinal();

        static int BaseEnd = Man.ordinal();

        static int BigBegin = Man.ordinal();

        static int BigEnd = Infinity.ordinal();

        static int End = Infinity.ordinal();

        /**
         * 自身が有効な値であるか否かを取得する。
         *
         * @return 有効な値ならば true 。
         */
        public boolean isDefined() {
            return ordinal() >= NumberPhoneType.Begin && ordinal() < NumberPhoneType.End;
        }

        /**
         * 自身が None を表すか否かを取得する。
         *
         * @return None ならば true 。
         */
        public boolean isNone() {
            return this == NumberPhoneType.None;
        }

        /**
         * 自身が Point を表すか否かを取得する。
         *
         * @return Point ならば true 。
         */
        public boolean isPoint() {
            return this == NumberPhoneType.Point;
        }

        /**
         * 自身が None または Point を表すか否かを取得する。
         *
         * @return None または Point ならば true 。
         */
        public boolean isNoneOrPoint() {
            return isNone() || isPoint();
        }

        /**
         * 自身が 0 ～ 9 の数字を表すか否かを取得する拡張メソッド。
         *
         * @return Digit0 ～ Digit9 ならば true 。
         */
        @SuppressWarnings("unused")
        public boolean isDigit() {
            return ordinal() >= NumberPhoneType.DigitBegin && ordinal() < NumberPhoneType.DigitEnd;
        }

        /**
         * 自身が十の位～千の位を表すか否かを取得する拡張メソッド。
         *
         * @return Ten, Hundred, Thousand のいずれかならば true 。
         */
        public boolean isBase() {
            return ordinal() >= NumberPhoneType.BaseBegin && ordinal() < NumberPhoneType.BaseEnd;
        }

        /**
         * 自身が10000の乗算桁を表すか否かを取得する拡張メソッド。
         *
         * @return Man ～ Infinity のいずれかならば true 。
         */
        public boolean isBig() {
            return ordinal() >= NumberPhoneType.BigBegin && ordinal() < NumberPhoneType.BigEnd;
        }

        /**
         * 整数部の構成要素の読み仮名を決定するデリゲート。
         *
         * @param type 対象の種別。
         * @param prevType 先行要素の種別。
         * @param nextType 後続要素の種別。
         * @return 読み仮名。
         */
        abstract String numberKanaDecider(NumberPhoneType type, NumberPhoneType prevType, NumberPhoneType nextType);
    }

    /**
     * NumberPhoneType に対応する規定の読み仮名。
     */
    private static final String[] DefaultNumberKanas = {
        "", "てん",

        "ぜろ", "いち", "に", "さん", "よん", "ご", "ろく", "なな", "はち", "きゅう",

        "じゅう", "ひゃく", "せん",

        "まん", "おく", "ちょう", "けい", "がい", "じょ", "じょう", "こう", "かん", "せい", "さい", "ごく", "ごうがしゃ", "あそうぎ", "なゆた", "ふかしぎ", "むりょうたいすう",
    };

    /**
     * 整数部の構成要素の既定の読みを取得する。
     * 
     * @param type 対象の種別。
     * @return 読み仮名。
     */
    private static String getDefaultNumberKana(NumberPhoneType type) {
        return DefaultNumberKanas[type.ordinal()];
    }

    /**
     * 小数部の各桁数字(最終桁を除く)の読み仮名。
     */
    private static final String[] RemainKanas = {
        "ぜろ", "いち", "にー", "さん", "よん", "ごー", "ろく", "なな", "はち", "きゅう",
    };

    /**
     * 小数部の最終桁数字の読み仮名。小数部がちょうど2桁の場合は用いない。
     */
    private static final String[] LastRemainKanas = {
        "ぜろ", "いち", "に", "さん", "よん", "ご", "ろく", "なな", "はち", "きゅう",
    };

    /**
     * 数字にマッチする正規表現。
     */
    private static final Pattern rexNumber = Pattern.compile("([1-9１-９][0-9０-９]*|[0０])([\\.\\．][0-9０-９]+)?");

    /**
     * 整数部要素の読み仮名を決定する。
     *
     * @param type 対象種別。
     * @param prevType 先行要素の種別。
     * @param nextType 後続要素の種別。
     * @return 読み仮名。
     */
    private static String decideNumberKana(NumberPhoneType type, NumberPhoneType prevType, NumberPhoneType nextType) {
        if (!type.isDefined()) {
            return "";
        }
        return type.numberKanaDecider(type, prevType, nextType);
    }

    /**
     * 文字列中の数字列を読み仮名に変換する。
     *
     * @param src 文字列。
     * @return 変換された文字列。
     */
    public String convertFrom(String src) {
        Matcher matcher = rexNumber.matcher(src);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, phoneme(matcher.group(1)));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 数字を表す文字列の読み仮名を取得する。
     *
     * @param src 数字を表す文字列。
     * @return 文字列の読み仮名。
     */
    public String phoneme(String src) {
        StringBuilder dest = new StringBuilder();

        // 整数部と小数部の半角文字列を取得
        String[] numParts = getNumberParts(src);

        // 小数部の有無
        boolean remainExist = numParts.length >= 2 && numParts[1].length() > 0;

        // 整数部の読み仮名をバッファに追加
        appendNumberPhonetic(numParts[0], remainExist, dest);

        if (remainExist) {
            // 小数部の読み仮名をバッファに追加
            appednRemainPhonetic(numParts[1], dest);
        }

        return dest.toString();
    }

    /**
     * 数字を表す文字列を半角化、カンマ除去し、整数部と小数部に分ける。
     *
     * @param src 数字を表す文字列。
     * @return 整数部文字列と小数部文字列の配列。
     */
    private String[] getNumberParts(String src) {
        // 全角を半角に変換
        String text = CharNormalizerJa.ToHalf.normalize(src);

        // カンマを削除
        text = text.replace(",", "");

        // ピリオドで区切る
        return text.split("\\.");
    }

    /** 最後に追加された要素種別の設定先。 何も追加されなかった場合は prevType が設定される。 */
    private NumberPhoneType lastType;

    /**
     * 整数部を表す文字列の読み仮名をバッファに追加する。
     *
     * @param src 数字列。
     * @param remainExist 小数部に続くならば true 。
     * @param dest 追加先の文字列バッファ。
     * @return 追加された文字列長。
     */
    private int appendNumberPhonetic(String src, boolean remainExist, StringBuilder dest) {
        int appendLen = 0;

        int len = src.length();
        NumberPhoneType prevType = NumberPhoneType.None;

        for (int i = 0; i < len;) {
            // 4桁単位の位置を算出
            int bigPos = (len - 1 - i) / 4;

            // 4桁単位で区切って取得
            String part = src.substring(i, len - bigPos * 4);

            // 取得した文字数分だけ位置を進める
            i += part.length();

            // 4桁単位種別を決定
            NumberPhoneType baseType;
            if (bigPos == 0) {
                baseType = remainExist ? NumberPhoneType.Point : NumberPhoneType.None;
            } else {
                if (NumberPhoneType.BigBegin + bigPos - 1 > NumberPhoneType.BigEnd) {
                    // 大きすぎるのでスキップ
                    System.err.println("too big: " + src);
                    continue;
                }
                baseType = NumberPhoneType.values()[NumberPhoneType.BigBegin + bigPos - 1];
                if (!baseType.isBig()) {
                    // 大きすぎるのでスキップ
                    System.err.println("too big: " + src);
                    continue;
                }
            }

            // 読みを追加
            lastType = NumberPhoneType.None;
            appendLen += appendThousandPartPhonetic(part,
                                                    prevType,
                                                    baseType,
                                                    dest);

            // 先行種別を更新
            prevType = lastType;
        }

        return appendLen;
    }

    /**
     * 小数部を表す文字列の読み仮名をバッファに追加する。
     *
     * @param src 数字列。
     * @param dest 追加先の文字列バッファ。
     * @return 追加された文字列長。
     */
    private int appednRemainPhonetic(String src, StringBuilder dest) {
        int oldLen = dest.length();

        int len = src.length();
        for (int i = 0; i < len; ++i) {
            // 2桁以外の最終桁は読み方が異なる
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
     * 4桁単位での読み仮名をバッファに追加する。
     *
     * @param src 数字列。1～4文字。
     * @param prevType 先行要素の種別。
     * @param baseType 4桁単位要素の種別。
     * @param dest 追加先の文字列バッファ。
     *
     * @return 追加された文字列長。
     */
    private int appendThousandPartPhonetic(String src,
                                           NumberPhoneType prevType,
                                           NumberPhoneType baseType,
                                           StringBuilder dest) {
        int oldLen = dest.length();

        // とりあえず prevType を設定しておく
        lastType = prevType;

        // 頭の "0" を削除
        String text = src.replaceFirst("^0*", "");

        // 長さを算出
        int len = text.length();

        if (len == 0) {
            // "0" のみだった場合
            String zero = decideNumberKana(NumberPhoneType.Digit0, lastType, baseType);
            if (zero.length() > 0) {
                dest.append(zero);
                lastType = NumberPhoneType.Digit0;
            }
        } else {
            // 種別配列を作成(baseType は含めない)
            List<NumberPhoneType> types = new ArrayList<>();
            for (int i = 0; i < len; ++i) {
                // 数字
                int d = parseChar(text.charAt(i));
                if (d == 0) {
                    // "0" ならば後続も含めてスキップ
                    continue;
                }
                types.add(NumberPhoneType.values()[NumberPhoneType.DigitBegin + d]);

                // 数字の後続
                int pos = (len - 1) - i - 1; // 0:十, 1:百, 2:千
                if (pos >= 0) {
                    types.add(NumberPhoneType.values()[NumberPhoneType.BaseBegin + pos]);
                }
            }

            // 順番に処理
            for (int i = 0; i < types.size(); ++i) {
                NumberPhoneType t = types.get(i);
                NumberPhoneType next = i + 1 < types.size() ? types.get(i + 1) : baseType;
                String s = decideNumberKana(t, lastType, next);
                if (s.length() > 0) {
                    dest.append(s);
                    lastType = t;
                }
            }
        }

        // 以下のいずれかの場合は baseType を処理
        // - baseType == NumberPhoneType.Point
        // - 1文字以上追加があった
        if (baseType.isPoint() || dest.length() > oldLen) {
            String baseText = decideNumberKana(baseType, lastType, NumberPhoneType.None);
            if (baseText.length() > 0) {
                dest.append(baseText);
                lastType = baseType;
            }
        }

        return dest.length() - oldLen;
    }

    /**
     * 文字を数値に変換する。
     *
     * @param c 文字。
     * @return 数値。変換できなければ 0 。
     */
    private int parseChar(char c) {
        return Character.isDigit(c) ? Integer.parseInt(String.valueOf(c)) : 0;
    }
}
