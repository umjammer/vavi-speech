/*
 * https://github.com/jiro4989/ojosama/blob/main/ojosama.go
 */

package vavi.speech.modifier.yakuwarigo;


import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.java.sen.StringTagger;
import net.java.sen.Token;
import vavi.util.Debug;

import static vavi.speech.modifier.yakuwarigo.EQMark.findExclamationQuestionByStyleAndMeaning;
import static vavi.speech.modifier.yakuwarigo.EQMark.isExclamationQuestionMark;
import static vavi.speech.modifier.yakuwarigo.EQMark.sampleExclamationQuestionByValue;
import static vavi.speech.modifier.yakuwarigo.Feature.Feat;
import static vavi.speech.modifier.yakuwarigo.Feature.Pos;
import static vavi.speech.modifier.yakuwarigo.Feature.containsFeatures;
import static vavi.speech.modifier.yakuwarigo.Feature.containsString;
import static vavi.speech.modifier.yakuwarigo.Feature.equalsFeatures;
import static vavi.speech.modifier.yakuwarigo.Feature.newPos;
import static vavi.speech.modifier.yakuwarigo.Feature.slice;
import static vavi.speech.modifier.yakuwarigo.Rule.ContinuousConditionsConvertRule;
import static vavi.speech.modifier.yakuwarigo.Rule.ConvertRule;
import static vavi.speech.modifier.yakuwarigo.Rule.SentenceEndingParticleConvertRule;
import static vavi.speech.modifier.yakuwarigo.Rule.SentenceEndingParticleConvertRule.MeaningType;
import static vavi.speech.modifier.yakuwarigo.Rule.SentenceEndingParticleConvertRule.getMeaningType;


/**
 * YakuwarigoModifier.
 * <p>
 * 関数や構造体のアクセス範囲に関する方針
 * <p>
 * 基本的にライブラリ用途としては Convert 関数1つのみを公開する。
 * Convert関数の挙動の微調整はConvertOption構造体で制御する。
 * <p>
 * ユーザ側で独自に変換ルールを追加出来たほうが良いかもしれないが、
 * パッケージ構成や型名を変更する可能性が高いため、
 * 破壊的変更として影響しないように公開する機能を必要最小限にする。
 * <p>
 * 「ライブラリ呼び出し側で独自に変換ルールを追加したい」という人は
 * おそらく現れないと想定しているが、要望がきた時に改めて考える。
 * <p>
 * それまでは好き勝手パッケージ構成や名前を変更しまくれるように
 * 公開範囲を極小にする。
 * ゆえにConvert関数内の処理で関数を分割したくなった場合、
 * すべてプライベート関数として実装する。
 * <p>
 * 内部実装の都合でパッケージ等が必要になった場合は、
 * すべて internal パッケージ配下にパッケージを切って配置する。
 * <p>
 * 変換ルールの実装方針
 * <p>
 * 変換ルールは converter で定義する。
 * 変換ロジックと変換ルールが密結合してるので
 * 同じパッケージの方が自然にも感じるけれど分離している。
 * <p>
 * 理由は、変換ルールの生成にしか使わないプライベート関数や、
 * 変換条件の判定関数と、その内部実装用のプライベート関数などが増えてきたため、
 * それらのプライベート関数に変換ロジック関数からアクセスさせないためにパッケージを分けている。
 *
 * @see "https://ja.wikipedia.org/wiki/%E5%BD%B9%E5%89%B2%E8%AA%9E"
 */
public class YakuwarigoModifier {

    /** Settings */
    public static class ConvertOption {
        /**
         * 句点を！に変換する機能をOFFにする。
         * 句点を！に変換してしまうと変換元の文章のニュアンスを破壊する可能性があるため、
         * オプションパラメータで無効にできるようにする。
         */
        public boolean disableKutenToExclamation;

        /** disable adding "〜" randomly */
        public boolean disableLongNote;

        /**
         * disable adding the prefix "お"
         * TODO out source
         */
        public boolean disablePrefix;

        /** rule name */
        public String name;
    }

    /** alphabet */
    private static final Pattern alnumRegexp = Pattern.compile("^[a-zA-Z0-9]+$");

    /** */
    private static final String[] shuffleElementsKutenToExclamation = {"。", "。", "！", "❗"};

    /** */
    private static class StringResult {
        String str;
        int pos;

        public StringResult(String s, int pos) {
            this.str = s;
            this.pos = pos;
        }
    }

    /** input source */
    private Token[] tokens;

    /** settings */
    private ConvertOption opt;

    /** rule database */
    private Rule rule;

    /* gson: not serialize when false  */
    public static final JsonSerializer<Boolean> booleanJsonSerializer = (in, type, context) ->
            in ? new JsonPrimitive(true) : null;

    /* gson: regex to string */
    public static final JsonSerializer<Pattern> patternJsonSerializer = (in, type, context) ->
            context.serialize(in.pattern());

    /* gson: TODO why needed??? */
    public static final JsonSerializer<ConvertCondition[]> convertConditionArrayJsonSerializer = (in, type, context) -> {
        JsonArray result = new JsonArray();
        Arrays.stream(in).forEach(c -> result.add(context.serialize(c, ConvertCondition.class)));
        return result;
    };

    /* gson: special value "Pos." + name */
    static class FeatureConditionJsonSerDes implements JsonSerializer<Feature>, JsonDeserializer<Feature> {
        @Override
        public Feature deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.getAsJsonObject().get("pos") != null)
                return newPos(json.getAsJsonObject().get("pos").getAsString());
            return new Feat() {{
                if (json.getAsJsonObject().get("elements") != null)
                    elements = context.deserialize(json.getAsJsonObject().get("elements"), String[].class);
            }};
        }

        @Override
        public JsonElement serialize(Feature src, Type typeOfSrc, JsonSerializationContext context) {
            if (src instanceof Pos) {
                JsonObject result = new JsonObject();
                result.add("pos", new JsonPrimitive("Pos." + ((Pos) src).name()));
                return result;
            } else {
                JsonObject result = new JsonObject();
                result.add("elements", context.serialize(src.elements(), String[].class));
                return result;
            }
        }
    }

    /* gson: ConvertCondition TODO really needed??? */
    static class ConvertConditionJsonSerDes implements JsonSerializer<ConvertCondition>, JsonDeserializer<ConvertCondition> {

        @Override
        public ConvertCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new ConvertCondition() {{
                    if (json.getAsJsonObject().get("feature") != null)
                        feature = context.deserialize(json.getAsJsonObject().get("feature"), Feature.class);
                    if (json.getAsJsonObject().get("reading") != null)
                        reading = json.getAsJsonObject().get("reading").getAsString();
                    if (json.getAsJsonObject().get("readingRe") != null)
                        readingRe = Pattern.compile(json.getAsJsonObject().get("readingRe").getAsString());
                    if (json.getAsJsonObject().get("surface") != null)
                        surface = json.getAsJsonObject().get("surface").getAsString();
                    if (json.getAsJsonObject().get("surfaceRe") != null)
                        surfaceRe = Pattern.compile(json.getAsJsonObject().get("surfaceRe").getAsString());
                    if (json.getAsJsonObject().get("baseForm") != null)
                        baseForm = json.getAsJsonObject().get("baseForm").getAsString();
                    if (json.getAsJsonObject().get("baseFormRe") != null)
                        baseFormRe = Pattern.compile(json.getAsJsonObject().get("baseFormRe").getAsString());
                }};
        }

        @Override
        public JsonElement serialize(ConvertCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            if (src.feature != null)
                result.add("feature", context.serialize(src.feature, Feature.class));
            if (src.reading != null)
                result.add("reading", new JsonPrimitive(src.reading));
            if (src.surface != null)
                result.add("surface", new JsonPrimitive(src.surface));
            if (src.baseForm != null)
                result.add("baseForm", new JsonPrimitive(src.baseForm));
            if (src.surfaceRe != null)
                result.add("surfaceRe", context.serialize(src.surfaceRe, Pattern.class));
            if (src.readingRe != null)
                result.add("readingRe", context.serialize(src.readingRe, Pattern.class));
            if (src.baseFormRe != null)
                result.add("baseFormRe", context.serialize(src.baseFormRe, Pattern.class));
            return result;
        }
    }

    /** json serdes */
    static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(ConvertCondition[].class, convertConditionArrayJsonSerializer)
            .registerTypeAdapter(ConvertCondition.class, new ConvertConditionJsonSerDes())
            .registerTypeAdapter(Feature.class, new FeatureConditionJsonSerDes())
            .registerTypeAdapter(Pattern.class, patternJsonSerializer)
            .registerTypeAdapter(Boolean.class, booleanJsonSerializer)
            .create();

    /** load rule */
    private Rule getRule(String name) {
        return gson.fromJson(new InputStreamReader(YakuwarigoModifier.class.getResourceAsStream(name + ".json")), Rule.class);
    }

    /** japanese takenizer */
    private StringTagger tokenizer;

    /** w/o settings */
    public YakuwarigoModifier() throws IOException {
        this(null);
    }

    /**
     * @param opt は挙動を微調整するためのオプショナルなパラメータ。
     *            不要であれば null を渡せば良い。
     */
    public YakuwarigoModifier(ConvertOption opt) throws IOException {
        this.opt = opt;
        this.rule = getRule(opt != null && opt.name != null ? opt.name : "salome");

        this.tokenizer = StringTagger.getInstance(); // TODO OmitBosEos
    }

    /**
     * Convert はテキストを壱百満天原サロメお嬢様風の口調に変換して返却する。
     * <p>
     * 簡単に説明すると「ハーブですわ！」を「おハーブですわ～～！！！」と変換する。
     * それ以外にもいくつかバリエーションがある。
     * <p>
     * 一部変換の途中でランダムに要素を選択するため、
     * 呼び出し側で乱数のシードの初期化を行うこと。
     */
    public String convert(String src) throws IOException {
        // tokenize
        this.tokens = tokenizer.analyze(src);
        StringBuilder result = new StringBuilder();
        boolean nounKeep = false;
        for (int p = 0; p < tokens.length; p++) {
            TokenData data = new TokenData(tokens[p]);
            String buf = data.surface;

            // 英数字のみの単語の場合は何もしない
            if (alnumRegexp.matcher(buf).matches()) {
                result.append(buf);
                continue;
            }

            // 名詞＋動詞＋終助詞の組み合わせに対して変換する
            StringResult s = convertSentenceEndingParticle(p);
            if (s != null) {
                p = s.pos;
                result.append(s.str);
                continue;
            }

            // 連続する条件による変換を行う
            s = convertContinuousConditions(p);
            if (s != null) {
                p = s.pos;
                result.append(s.str);
                continue;
            }

            // 特定条件は優先して無視する
            if (matchExcludeRule(data)) {
                result.append(buf);
                continue;
            }

            // convert yakuwarigo
            ConversionResult cr = convert(data, p, buf, nounKeep);
            buf = cr.buf;
            nounKeep = cr.nounKeep;
            p = cr.pos;
            if (cr.kutenToEx) {
                s = randomKutenToExclamation(p);
                if (s != null) {
                    buf += s.str;
                    p = s.pos;
                }
            }
Debug.printf(Level.FINER, "token[%d] result: %s", p, buf);
            result.append(buf);
        }
        return result.toString();
    }

    /**
     * convertSentenceEndingParticle は名詞＋動詞（＋助動詞）＋終助詞の組み合わせすべてを満たす場合に変換する。
     * <p>
     * 終助詞は文の終わりに、文を完結させつつ、文に「希望」「禁止」「詠嘆」「強意」等の意味を添える効果がある。
     * <p>
     * 例えば「野球しようぜ」の場合、
     * 「名詞：野球」「動詞：しよ」「助動詞：う」「終助詞：ぜ」という分解がされる。
     * <p>
     * 終助詞の「ぜ」としては「希望」の意味合いが含まれるため、希望する意味合いのお嬢様言葉に変換する。
     * 例：お野球をいたしませんこと
     * <p>
     * その他にも「野球するな」だと「お野球をしてはいけませんわ」になる。
     */
    private StringResult convertSentenceEndingParticle(int tokenPos) {
        for (SentenceEndingParticleConvertRule r : rule.sentenceEndingParticleConvertRules) {
            StringBuilder result = new StringBuilder();
            int p = tokenPos;
            TokenData data = new TokenData(tokens[p]);

            // 先頭が一致するならば次の単語に進む
            if (!data.matchAnyTokenData(r.conditions1)) {
                continue;
            }
            if (tokens.length <= p + 1) {
                continue;
            }
            String s = data.surface;
            // TODO ベタ書きしててよくない
            if ((opt == null || !opt.disablePrefix) && (equalsFeatures(data.features, Pos.NounsGeneral) ||
                    equalsFeatures(slice(data.features, 0, 2), Pos.NounsSaDynamic))) {
                s = "お" + s;
            }
            result.append(s);
            p++;
            data = new TokenData(tokens[p]);

            // NOTE:
            // 2つ目以降は value の値で置き換えるため
            // result.append(data.Surface) を実行しない。

            // 2つ目は動詞のいずれかとマッチする。マッチしなければふりだしに戻る
            if (!data.matchAnyTokenData(r.conditions2)) {
                continue;
            }
            if (tokens.length <= p + 1) {
                continue;
            }
            p++;
            data = new TokenData(tokens[p]);

            // 助動詞があった場合は無視してトークンを進める。
            // 別に無くても良い。
            if (data.matchAllTokenData(r.auxiliaryVerb)) {
                if (tokens.length <= p + 1) {
                    continue;
                }
                p++;
                data = new TokenData(tokens[p]);
            }

            // 最後、終助詞がどの意味分類に該当するかを取得
            MeaningType mt = getMeaningType(r.sentenceEndingParticle, data);
            if (mt == MeaningType.Unknown) {
                continue;
            }

            // 意味分類に該当する変換候補の文字列を返す
            // TODO 現状1個だけなので決め打ちで最初の1つ目を返す。
            result.append(r.value.get(mt)[0]);
            return new StringResult(result.toString(), p);
        }
        return null;
    }

    /**
     * 連続する条件による変換ルールにマッチした変換結果を返す。
     * <p>
     * 例えば「壱百満天原サロメ」や「横断歩道」のように、複数のTokenがこの順序で連続
     * して初めて1つの意味になるような条件をすべて満たした時に結果を返す。
     * <p>
     * 連続する条件にマッチした場合は tokenPos をその分だけ進める必要があるため、進
     * めた後の tokenPos を返却する。
     * <p>
     * 第三引数は変換ルールにマッチしたかどうかを返す。
     */
    StringResult convertContinuousConditions(int tokenPos) {
        for (ContinuousConditionsConvertRule mc : rule.continuousConditionsConvertRules) {
            if (!matchContinuousConditions(tokenPos, mc.conditions)) {
                continue;
            }

            int p = tokenPos + mc.conditions.length - 1;
            String result = mc.value;

            // FIXME 書き方が汚い
            TokenData data = new TokenData(tokens[tokenPos]);
            String surface = data.surface;
            if ((opt == null || !opt.disablePrefix) && appendablePrefix(data)) {
                surface = "お" + surface;
            }
            result = result.replaceAll("@1", surface);

            // 句点と～が同時に発生することは無いので早期リターンで良い
            StringResult sr = randomKutenToExclamation(p);
            if (sr != null) {
                result += sr.str;
                p = sr.pos;
                return new StringResult(result, p);
            }

            if (mc.appendLongNote) {
                sr = newLongNote(p);
                if (sr != null) {
                    result += sr.str;
                    p = sr.pos;
                }
            }

            return new StringResult(result, p);
        }
        return null;
    }

    /**
     * tokens の tokenPos の位置からのトークンが、連続する条件にすべてマッチするかを判定する。
     * <p>
     * 次のトークンが存在しなかったり、1つでも条件が不一致になった場合 false を返す。
     */
    private boolean matchContinuousConditions(int tokenPos, ConvertCondition[] ccs) {
        int p = tokenPos;
        for (ConvertCondition conds : ccs) {
            if (tokens.length <= p) {
                return false;
            }
            TokenData data = new TokenData(tokens[p]);
            if (!conds.equalsTokenData(data)) {
                return false;
            }
            p++;
        }
        return true;
    }

    /** 除外ルールと一致するものが存在するかを判定する。 */
    private boolean matchExcludeRule(TokenData data) {
        for (ConvertRule c : rule.excludeRules) {
            if (!data.matchAllTokenData(c.conditions)) {
                continue;
            }
            return true;
        }
        return false;
    }

    /** */
    private static class ConversionResult {
        String buf;
        boolean nounKeep;
        int pos;
        boolean kutenToEx;

        public ConversionResult(String buf, boolean nounKeep, int pos, boolean kutenToEx) {
            this.buf = buf;
            this.nounKeep = nounKeep;
            this.pos = pos;
            this.kutenToEx = kutenToEx;
        }
    }

    /** 基本的な変換を行う。 */
    private ConversionResult convert(TokenData data, int p, String surface, boolean nounKeep) {
        ConvertRule c = matchConvertRule(data, p);
        if (c == null) {
            AppendResult ar = appendPrefix(data, p, surface, nounKeep);
            return new ConversionResult(ar.result, ar.nounKeep, p, false);
        }

        String result = c.value;
        int pos = p;
        result = result.replaceAll("@1", data.surface);

        // 波線伸ばしをランダムに追加する
        if (opt == null || !opt.disableLongNote && c.appendLongNote) {
            StringResult sr = newLongNote(pos);
            if (sr != null) {
                result += sr.str;
                pos = sr.pos;
            }
        }

        // 手前に「お」を付ける
        if (opt == null || !opt.disablePrefix && !c.disablePrefix) {
            AppendResult ar = appendPrefix(data, pos, result, nounKeep);
            result = ar.result;
            nounKeep = ar.nounKeep;
        }

        return new ConversionResult(result, nounKeep, pos, c.enableKutenToExclamation);
    }

    /** @return nullable */
    private ConvertRule matchConvertRule(TokenData data, int p) {
        TokenData beforeToken = null;
        boolean beforeTokenOK = false;
        if (0 < p) {
            beforeToken = new TokenData(tokens[p - 1]);
            beforeTokenOK = true;
        }

        TokenData afterToken = null;
        boolean afterTokenOK = false;
        if (p + 1 < tokens.length) {
            afterToken = new TokenData(tokens[p + 1]);
            afterTokenOK = true;
        }

        for (ConvertRule c : rule.convertRules) {
            if (!data.matchAllTokenData(c.conditions)) {
Debug.println(Level.FINER, "skipped: " + c.value);
                continue;
            }

            // 前に続く単語をみて変換を無視する
            if (beforeTokenOK && c.beforeIgnoreConditions != null && beforeToken.matchAnyTokenData(c.beforeIgnoreConditions)) {
Debug.println(Level.FINE, "break cause before token");
                break;
            }

            // 次に続く単語をみて変換を無視する
            if (afterTokenOK && c.afterIgnoreConditions != null && afterToken.matchAnyTokenData(c.afterIgnoreConditions)) {
Debug.println(Level.FINE, "break cause after token");
                break;
            }

            // 文の区切りか、文の終わりの時だけ有効にする。
            // 次のトークンが存在して、且つ次のトークンが文を区切るトークンでない時
            // は変換しない。
            if (c.enableWhenSentenceSeparation && afterTokenOK && !isSentenceSeparation(afterToken)) {
Debug.println(Level.FINE, "break cause sentence termination");
                break;
            }

Debug.println(Level.FINE, "select: " + c.value + " for surfece: " + tokens[p].getSurface());
            return c;
        }
        return null;
    }

    /** */
    private boolean appendablePrefix(TokenData data) {
        if (!equalsFeatures(data.features, new Feat().setElements("名詞", "一般")) &&
                !equalsFeatures(slice(data.features, 0, 2), new Feat().setElements("名詞", "固有名詞"))) {
            return false;
        }

        // 丁寧語の場合は「お」を付けない
        return !data.isPoliteWord();
    }

    /** */
    private static class AppendResult {
        String result;
        boolean nounKeep;

        public AppendResult(String result, boolean nounKeep) {
            this.result = result;
            this.nounKeep = nounKeep;
        }
    }

    /** surface の前に「お」を付ける。 */
    private AppendResult appendPrefix(TokenData data, int i, String surface, boolean nounKeep) {
        if (!appendablePrefix(data)) {
            return new AppendResult(surface, false);
        }

        // 次のトークンが動詞の場合は「お」を付けない。
        // 例: プレイする
        if (i + 1 < tokens.length) {
            data = new TokenData(tokens[i + 1]);
            if (equalsFeatures(data.features, new Feat().setElements("動詞", "自立"))) {
                return new AppendResult(surface, nounKeep);
            }
        }

        // すでに「お」を付与されているので、「お」を付与しない
        if (nounKeep) {
            return new AppendResult(surface, false);
        }

        if (0 < i) {
            data = new TokenData(tokens[i - 1]);

            // 手前のトークンが「お」の場合は付与しない
            if (equalsFeatures(data.features, new Feat().setElements("接頭詞", "名詞接続"))) {
                return new AppendResult(surface, false);
            }

            // サ変接続が来ても付与しない。
            // 例: 横断歩道、解体新書
            if (equalsFeatures(data.features, new Feat().setElements("名詞", "サ変接続"))) {
                return new AppendResult(surface, false);
            }
        }

        return new AppendResult((opt == null || !opt.disablePrefix ? "お" : "") + surface, true);
    }

    /** isSentenceSeparation は data が文の区切りに使われる token かどうかを判定する。 */
    private boolean isSentenceSeparation(TokenData data) {
        return containsFeatures(new Feature[] {Feature.Kuten, Feature.Toten}, data.features) ||
                containsString(new String[] {"！", "!", "？", "?"}, data.surface);
    }

    /** */
    static final Random random = new Random(System.currentTimeMillis());

    /** */
    public static class Randomizer {
        int wavyLineCount() {
            return random.nextInt(3);
        }
        int exclamationMarkCount() {
            return random.nextInt(3);
        }
    }

    /**
     * 乱数が絡むと単体テストがやりづらくなるので、
     * set mock sub class here for fixed fixture.
     */
    private static final Randomizer randomizer = new Randomizer();

    /**
     * newLongNote は次の token が感嘆符か疑問符の場合に波線、感嘆符、疑問符をランダムに生成する。
     *
     * @param p tokens index
     * @return nullable
     */
    private StringResult newLongNote(int p) {
        String s = creatableLongNote(p);
        if (s == null) {
            return null;
        }

        int w = randomizer.wavyLineCount();
        int e = randomizer.exclamationMarkCount();

        StringBuilder suffix = new StringBuilder();
        for (int i = 0; i < w; i++) {
            suffix.append("～");
        }

        // ！or？をどれかからランダムに選択する
        EQMark feq = sampleExclamationQuestionByValue(s);

        // 次の token は必ず感嘆符か疑問符のどちらかであることが確定しているため
        // -1 して数を調整している。
        for (int i = 0; i < e - 1; i++) {
            suffix.append(feq.value);
        }

        // 後ろに！や？が連続する場合、それらをすべて feq と同じ種類（半角、全角、
        // 絵文字）の！や？に置き換えて返却する。
        StringResult sr = getContinuousExclamationMark(p, feq);
        suffix.append(sr.str);
        return new StringResult(suffix.toString(), sr.pos);
    }

    /**
     * @param p tokens index
     * @return nullable
     */
    private String creatableLongNote(int p) {
        if (tokens.length <= p + 1) {
            return null;
        }

        Token data = tokens[p + 1];
        for (String s : new String[] {"！", "？", "!", "?"}) {
            if (!data.getSurface().equals(s)) {
                continue;
            }
            return s;
        }
        return null;
    }

    /**
     * @param p tokens index
     */
    private StringResult getContinuousExclamationMark(int p, EQMark feq) {
        StringBuilder result = new StringBuilder();
        int pos = p;

        for (int i = p + 1; i < tokens.length; i++) {
            TokenData token = new TokenData(tokens[i]);
            for (char r : token.surface.toCharArray()) {
                EQMark eq = isExclamationQuestionMark(String.valueOf(r));
                if (eq == null) {
                    return new StringResult(result.toString(), pos);
                } else {
                    // e は！か？のどちらかなので、同じスタイルの文字を取得して追加
                    EQMark got = findExclamationQuestionByStyleAndMeaning(feq.style, eq.meaning);
                    if (got != null) {
                        result.append(got.value);
                    }
                }
            }
            // トークンの位置を制御する変数なので、forループ内では変更しない
            pos = i;
        }

        return new StringResult(result.toString(), pos);
    }

    /**
     * randomKutenToExclamation はランダムで句点を！に変換する。
     *
     * @return nullable
     */
    private StringResult randomKutenToExclamation(int tokenPos) {
        if (opt != null && opt.disableKutenToExclamation) {
            return null;
        }

        int pos = tokenPos + 1;
        if (tokens.length <= pos) {
            return null;
        }

        TokenData data = new TokenData(tokens[pos]);
        if (!data.isKuten()) {
            return null;
        }

        List<String> l = Arrays.asList(shuffleElementsKutenToExclamation);
Debug.println(Level.FINER, "shuffleElementsKutenToExclamation: " + l);
        Collections.shuffle(l, random);
        return new StringResult(l.get(0), pos);
    }
}
