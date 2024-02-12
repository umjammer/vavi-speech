/*
 * https://github.com/jiro4989/ojosama/blob/ojosama_test.go
 */

package vavi.speech.modifier.yakuwarigo.salome;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import vavi.speech.modifier.yakuwarigo.YakuwarigoModifier;
import vavi.speech.modifier.yakuwarigo.YakuwarigoModifier.ConvertOption;
import vavi.util.Debug;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static vavi.speech.modifier.yakuwarigo.UtilTest.getFinalStatic;
import static vavi.speech.modifier.yakuwarigo.UtilTest.setFinalStatic;

/**
 * TODO some tests don't work
 *  because the dictionary is not the same as the original
 *  or miss porting
 */
class OjosamaTest {

    static String[] original;
    static SalomeProvider.Randomizer originalRandomizer;
    static EQMark.Shuffler originalShuffler;

    @BeforeAll
    static void setup() throws Exception {
        originalRandomizer = (SalomeProvider.Randomizer) getFinalStatic(SalomeProvider.class.getDeclaredField("randomizer"));
Debug.println(originalRandomizer);
        originalShuffler = (EQMark.Shuffler) getFinalStatic(EQMark.class.getDeclaredField("shuffler"));
Debug.println(originalRandomizer);
        original = ((String[]) getFinalStatic(SalomeProvider.class.getDeclaredField("shuffleElementsKutenToExclamation"))).clone();
    }

    @AfterEach
    void teardown() throws Exception {
        setFinalStatic(SalomeProvider.class.getDeclaredField("shuffleElementsKutenToExclamation"), original);
        setFinalStatic(SalomeProvider.class.getDeclaredField("randomizer"), originalRandomizer);
        setFinalStatic(EQMark.class.getDeclaredField("shuffler"), originalShuffler);
    }

    @Test
    void exampleConvert() throws IOException {
        String got = new YakuwarigoModifier().convert("ハーブです");

        assertEquals("おハーブですわ", got);
    }

    /**
     * TestRandomizer is a mock for
     * 強制的に波線や感嘆符や疑問符を任意の数追加するための設定。
     * <p>
     * 波線や感嘆符の付与には乱数が絡むため、単体テスト実行時に確実に等しい結果を得
     * ることが難しい。この問題を回避するために、このパラメータを差し込むことで乱数
     * の影響を受けないように制御する。
     */
    static class TestRandomizer extends SalomeProvider.Randomizer {
        int w;
        int e;
        TestRandomizer(int w, int e) {
            this.w = w;
            this.e = e;
        }
        @Override public int wavyLineCount() {
            return w;
        }
        @Override public int exclamationMarkCount() {
            return e;
        }
        @Override public String toString() {
            return "TestRandomizer{" + "w=" + w + ", e=" + e + '}';
        }
    }

    /** mock */
    static class TestShuffler extends EQMark.Shuffler {
        int pos;
        TestShuffler(int pos) {
            this.pos = pos;
        }
        @Override public EQMark getFirst(List<EQMark> s) {
            return s.get(pos);
        }
        @Override public String toString() {
            return "TestShuffler{" + "pos=" + pos + '}';
        }
    }

    /** */
    static class TestOption {
        /** 単体テスト用のパラメータ */
        TestRandomizer forceAppendLongNote;
        /** 単体テスト用のパラメータ */
        Integer forceCharsTestMode;
        /** KutenToExclamationで強制的に3番目の要素を選択する */
        boolean forceKutenToExclamation;

        TestOption setForceAppendLongNote(TestRandomizer forceAppendLongNote) {
            this.forceAppendLongNote = forceAppendLongNote;
            return this;
        }

        TestOption setForceCharsTestMode(int forceCharsTestMode) {
            this.forceCharsTestMode = forceCharsTestMode;
            return this;
        }

        TestOption setForceKutenToExclamation(boolean forceKutenToExclamation) {
            this.forceKutenToExclamation = forceKutenToExclamation;
            return this;
        }
    }

    static Stream<Arguments> sourceConvert() {
        return Stream.of(
                arguments(
                        "正常系: 名詞の手前には「お」をお付けいたしますわ",
                        "これはハーブです",
                        "こちらはおハーブですわ",
                        null,
                        false),
                arguments(
                        "正常系: 「名詞,一般」の後に「動詞,自立」が来た時は2Tokenで1つの動詞として解釈いたしますので、「お」を付けませんわ",
                        "プレイする",
                        "プレイいたしますわ",
                        null,
                        false),
                arguments(
                        "正常系: 「プレイする」の例文ですわ",
                        "〇〇をプレイする",
                        "〇〇をプレイいたしますわ",
                        null,
                        false),
                arguments(
                        "正常系: 二人称はすべて「貴方」に変換いたしますわ",
                        "あなたは。あんたは。おまえは。お前は。てめぇは。てめえは。貴様は。君は。",
                        "貴方は。貴方は。貴方は。貴方は。貴方は。貴方は。貴方は。貴方は。",
                        null,
                        false),
                arguments(
                        "正常系: わたしはわたくしに変換いたしますわ",
                        "わたし",
                        "わたくし",
                        null,
                        false),
                arguments(
                        "正常系: 「ですか」のときは「ですの」に変換いたしますわ",
                        "ビデオテープはどこで使うんですか",
                        "おビデオテープはどちらで使うんですの",
                        null,
                        false),
                arguments(
                        "正常系: 「汚い|きたない」のときは「きったねぇ」に変換いたしますわ",
                        "汚いです",
                        "きったねぇですわ",
                        null,
                        false),
                arguments(
                        "正常系: 「汚い|きたない」のときは「きったねぇ」に変換いたしますわ",
                        "きたないです",
                        "きったねぇですわ",
                        null,
                        false),
                arguments(
                        "正常系: 「臭い|くさい」のときは「くっせぇ」に変換いたしますわ",
                        "臭いです",
                        "くっせぇですわ",
                        null,
                        false),
                arguments(
                        "正常系: 「臭い|くさい」のときは「くっせぇ」に変換いたしますわ",
                        "くさいです",
                        "くっせぇですわ",
                        null,
                        false),
                arguments(
                        "正常系: 複数の文も処理できますわ",
                        "〇〇をプレイする。きたないわ。",
                        "〇〇をプレイいたしますわ。きったねぇですわ。",
                        null,
                        false),
                arguments(
                        "正常系: 「ました」は「おりました」ですの",
                        "わたしも使ってました",
                        "わたくしも使っておりましたわ",
                        null,
                        false),
                arguments( // TODO 13
                        "正常系: アルファベット単語の場合は「お」をつけませんの",
                        "これはgrassです。あれはabcdefg12345です",
                        "こちらはgrassですわ。あちらはabcdefg12345ですわ",
                        null,
                        true),
                arguments(
                        "正常系: 「します」は「いたしますわ」に変換しますわ",
                        "使用します",
                        "使用いたしますわ",
                        null,
                        false),
                arguments( // TODO 15
                        "正常系: 名詞が連続する場合は最初の1つ目にだけ「お」を付けますわ",
                        "一般女性。経年劣化。トップシークレット",
                        "お一般女性。お経年劣化。おトップシークレット",
                        null,
                        true),
                arguments(
                        "正常系: お嬢様は変更しませんわ",
                        "お嬢様",
                        "お嬢様",
                        null,
                        false),
                arguments(
                        "正常系: サロメお嬢様は固有名詞ですわ",
                        "わたしは壱百満天原サロメです",
                        "わたくしは壱百満天原サロメですわ",
                        null,
                        false),
                arguments(
                        "正常系: 名字も1単語として認識いたしますわ",
                        "わたしの名字は壱百満天原です",
                        "わたくしのお名字は壱百満天原ですわ",
                        null,
                        false),
                arguments(
                        "正常系: 壱百満点の笑顔を皆様方にお届けするのがわたくしの使命ですわ",
                        "壱百満点",
                        "壱百満点",
                        null,
                        false),
                arguments(
                        "正常系: 部分一致の場合は処理しませんわ",
                        "壱",
                        "お壱",
                        null,
                        false),
                arguments(
                        "正常系: 文の途中の「する」の場合は変換しませんわ",
                        "テキストファイルをまるごと変換する場合は、以下のように実行します。",
                        "おテキストファイルをまるごと変換する場合は、以下のように実行いたしますわ。",
                        null,
                        false),
                arguments(
                        "正常系: 文の途中の「する」の場合は変換しませんわ",
                        "以下のお嬢様を可能な限り再現するアルゴリズムを目指してます。",
                        "以下のお嬢様を可能な限り再現するおアルゴリズムを目指してますわ。",
                        null,
                        false),
                arguments(
                        "正常系: すでに直前に「お」が付与されている場合は、「お」を付与しませんわ",
                        "お寿司。おハーブ",
                        "お寿司。おハーブ",
                        null,
                        false),
                arguments(
                        "正常系: 「ください」は「くださいまし」に変換いたしますわ",
                        "お使いください",
                        "お使いくださいまし",
                        null,
                        false),
                arguments(
                        "正常系: 「ください」は「くださいまし」に変換いたしますわ",
                        "お使いください！",
                        "お使いくださいまし！",
                        null,
                        false),
                arguments(
                        "正常系: 波線のばしを付与する場合がありますわ",
                        "これはハーブです！",
                        "こちらはおハーブですわ～～！！！",
                        new TestOption()
                                .setForceAppendLongNote(new TestRandomizer(2, 3))
                                .setForceCharsTestMode(0),
                        false),
                arguments(
                        "正常系: 半角感嘆符にも反応しますわ！",
                        "これはハーブです!これもハーブです?",
                        "こちらはおハーブですわ～～!!!こちらもおハーブですわ～～???",
                        new TestOption()
                                .setForceAppendLongNote(new TestRandomizer(2, 3))
                                .setForceCharsTestMode(1),
                        false),
                arguments(
                        "正常系: 波線のばしを付与する場合がありますわ",
                        "プレイします！",
                        "プレイいたしますわ～～！！！",
                        new TestOption()
                                .setForceAppendLongNote(new TestRandomizer(2, 3))
                                .setForceCharsTestMode(0),
                        false),
                arguments(
                        "正常系: 波線のばしを付与する場合がありますわ",
                        "プレイする！",
                        "プレイいたしますわ～～！！！",
                        new TestOption()
                                .setForceAppendLongNote(new TestRandomizer(2, 3))
                                .setForceCharsTestMode(0),
                        false),
                arguments(
                        "正常系: （動詞）ないはそのままですわ",
                        "限らない、飾らない、数えない",
                        "限らない、飾らない、数えない",
                        null,
                        false),
                arguments(
                        "正常系: ありましたはありましたのままですわ",
                        "ハーブがありました！",
                        "おハーブがありましたわ～～！！！",
                        new TestOption()
                                .setForceAppendLongNote(new TestRandomizer(2, 3))
                                .setForceCharsTestMode(0),
                        false),
                arguments(
                        "正常系: 一人称はすべて「私」に変換いたしますわ",
                        "俺は。オレは。おれは。僕は。ボクは。ぼくは。あたしは。わたしは。",
                        "私は。ワタクシは。わたくしは。私は。ワタクシは。わたくしは。わたくしは。わたくしは。",
                        null,
                        false),
                // FIXME: 話しますわね、にしたいけれど「話す」で1単語と判定されているので変換が難しい
                arguments(
                        "正常系: 小説の一文をテストしますわ",
                        "俺の体験した怖い話、聞いてくれるか？ありがとう。じゃぁ、話すよ。",
                        "私の体験した怖い話、聞いてくれますの？ありがとうございますわ。それでは、話すよ。",
                        null,
                        false),
                arguments(
                        "正常系: 小説の一文をテストしますわ",
                        "俺の転職前の会社の頃の話だから、もう3年も前の話になる。",
                        "私の転職前のお会社の頃の話ですので、もう3年も前の話になりますわ。",
                        null,
                        false),
                arguments( // TODO 35
                        "正常系: 小説の一文をテストしますわ",
                        "通勤時に通る横断歩道の話なんだ。よくあるだろ、事故が多い横断歩道の話だよ。",
                        "通勤時に通る横断歩道の話なんですの。よくありますでしょう、お事故が多い横断歩道の話ですわ。",
                        null,
                        true),
                arguments( // TODO 36
                        "正常系: 小説の一文をテストしますわ",
                        "ただ内容は、偶然事故が多いって話じゃないから安心してくれ。実際に俺が体験した話だ。",
                        "ただお内容は、偶然お事故が多いって話ではありませんので安心してくださいまし。実際に私が体験した話ですわ。",
                        null,
                        true),
                // FIXME: 長いですので、にしたい
                arguments( // TODO 37
                        "正常系: 小説の一文をテストしますわ",
                        "信じてもいいし、信じなくても良い。まぁ、ゆったり聞いてくれ。夜は長いからな。",
                        "信じてもいいですし、信じなくても良いですわ。まぁ、ゆったり聞いてくださいまし。夜は長いのでね。",
                        null,
                        true),
                // FIXME: 戻る、の部分の処理が難しい
                arguments( // TODO 38
                        "正常系: 小説の一文をテストしますわ",
                        "それで、横断歩道の話に戻るぞ。通勤時に使う横断歩道だからな、当然毎日通るだろ？",
                        "それで、横断歩道の話に戻りますわね。通勤時に使う横断歩道ですのでね、当然毎日通るでしょう？",
                        null,
                        true),
                // FIXME: ですわけれどになってる
                arguments(
                        "正常系: 小説の一文をテストしますわ",
                        "入社直後は道も周囲の店も何もわからなくて新鮮に感じたもんだけどさ、一番楽に通勤できるルートが確立したら、すぐに通勤が退屈になるわけだ。",
                        "入社直後はお道もお周囲のお店も何もわからなくて新鮮に感じたものですわけれど、一番楽に通勤できるおルートが確立したら、すぐに通勤が退屈になるわけですわ。",
                        null,
                        false),
                arguments(
                        "正常系: お嬢様らしく短く笑いますわ",
                        "うふ",
                        "おほ",
                        null,
                        false),
                arguments(
                        "正常系: お嬢様らしく笑いますわ",
                        "うふふ",
                        "おほほ",
                        null,
                        false),
                arguments(
                        "正常系: お嬢様らしくそこそこ笑いますわ",
                        "うふふふふ",
                        "おほほほほ",
                        null,
                        false),
                arguments(
                        "正常系: お嬢様らしく長く笑いますわ",
                        "うふふふふふ",
                        "おほほほほほ",
                        null,
                        false),
                arguments(
                        "正常系: 切れ目があってもお嬢様らしく笑いますわ",
                        "うふうふふふふふふふふ",
                        "おほおほほほほほほほほ",
                        null,
                        false),
                arguments(
                        "正常系: パパはおパパ上、ママはおママ上とお呼びいたしますわ",
                        "パパ、ママ、父様、母様、パパ上、ママ上",
                        "おパパ上、おママ上、お父様、お母様、おパパ上、おママ上",
                        null,
                        false),
                arguments(
                        "正常系: パパはおパパ上、ママはおママ上とお呼びいたしますわ",
                        "皆、皆様",
                        "皆様方、皆様方",
                        null,
                        false),
                arguments(
                        "正常系: 罵倒には「お」を付けませんのよ",
                        "カス",
                        "カス",
                        null,
                        false),
                arguments( // TODO 48
                        "正常系: こそあど言葉（こ）にも対応しておりましてよ",
                        "これ、この、ここ、こちら、こう、こんな。",
                        "こちら、こちらの、こちら、こちら、こう、このような。",
                        null,
                        true),
                arguments(
                        "正常系: こそあど言葉（そ）にも対応しておりましてよ",
                        "それ、その、そこ、そちら、そう、そんな。",
                        "そちら、そちらの、そちら、そちら、そう、そのような。",
                        null,
                        false),
                arguments( // TODO 50
                        "正常系: こそあど言葉（あ）にも対応しておりましてよ",
                        "あれは、あの、あそこ、あちら、ああ、あんな。",
                        "あちらは、あちらの、あちら、あちら、ああ、あのような。",
                        null,
                        true),
                arguments(
                        "正常系: こそあど言葉（ど）にも対応しておりましてよ",
                        "どれ、どの、どこ、どちら、どう、どんな。",
                        "どちら、どちらの、どちら、どちら、どう、どのような。",
                        null,
                        false),
                arguments(
                        "正常系: 二重丁寧語にはしませんの",
                        "お嬢様、おにぎり、お腹、お寿司、おませさん、お利口さん",
                        "お嬢様、おにぎり、お腹、お寿司、おませさん、お利口さん",
                        null,
                        false),
                arguments(
                        "正常系: 読みが「お」で始まる言葉には「お」を付けませんの",
                        "追い剥ぎ、大型、大分、おろし金",
                        "追い剥ぎ、大型、大分、おろし金",
                        null,
                        false),
                arguments( // TODO 54
                        "正常系: サ辺接続が手前に来る単語には「お」を付けませんわ",
                        "横断歩道、解体新書、回覧板、回転寿司、駐車場",
                        "横断歩道、解体新書、回覧板、回転寿司、駐車場",
                        null,
                        true),
                arguments( // TODO 55
                        "正常系: 名詞＋動詞＋終助詞の組み合わせも変換いたしますわ～～！！この処理すっごく大変でしたの！！",
                        "野球しようぜ。サッカーやろうよ。バスケやるか。柔道やるな。陸上すんな。テニスするぞ。卓球やるべ。ゲームするの。",
                        "お野球をいたしませんこと。おサッカーをいたしませんこと。おバスケをいたしますわ。お柔道をしてはいけませんわ。お陸上をしてはいけませんわ。おテニスをいたしますわよ。お卓球をいたしませんこと。おゲームをいたしますわよ。",
                        null,
                        true),
                arguments( // TODO 56
                        "正常系: 名詞＋した（過去形）＋終助詞も変換いたしますわ！",
                        "野球したぜ。サッカーやったよ。バスケしたで。柔道やったわ。陸上したぞ。",
                        "お野球をいたしましたわ。おサッカーをいたしましたわ。おバスケをいたしましたわ。お柔道をいたしましたわ。お陸上をいたしましたわ。",
                        null,
                        true),
                arguments(
                        "正常系: 単純に「名詞＋した」だけでは変換いたしませんわ。まだ文の終わりではありませんので",
                        "野球した後。",
                        "野球した後。",
                        null,
                        false),
                arguments(
                        "正常系: 名詞＋動詞＋助動詞のみで終助詞がない場合でもエラーにはなりませんのよ",
                        "流鏑馬やろう",
                        "流鏑馬やろう",
                        null,
                        false),
                arguments(
                        "正常系: すべて全角文字に変換しますわ",
                        "です！？!?❗❓",
                        "ですわ～！？！？！？",
                        new TestOption()
                                .setForceAppendLongNote(new TestRandomizer(1, 1))
                                .setForceCharsTestMode(0),
                        false),
                arguments(
                        "正常系: すべて絵文字に変換しますわ",
                        "です！？!?❗❓",
                        "ですわ～❗❓❗❓❗❓",
                        new TestOption()
                                .setForceAppendLongNote(new TestRandomizer(1, 1))
                                .setForceCharsTestMode(2),
                        false),
                arguments(
                        "正常系: 絵文字を連続して付与もできますわ",
                        "です！寿司",
                        "ですわ～～❗❗❗お寿司",
                        new TestOption()
                                .setForceAppendLongNote(new TestRandomizer(2, 3))
                                .setForceCharsTestMode(2),
                        false),
                arguments( // TODO 62
                        "正常系: 意味のない文章のテストですわ",
                        "あ！い❓❗う",
                        "あ！い❓❗う",
                        null,
                        true),
                arguments(
                        "正常系: 「ー」の前に「お」は付きませんの",
                        "しようぜー。しようぜーー。しようぜ～。しようぜ～～。しようぜー～。",
                        "しようぜー。しようぜーー。しようぜ～。しようぜ～～。しようぜー～。",
                        null,
                        false),
                arguments( // TODO 64
                        "正常系: なん(じゃ|や|だ)は、いずれも「なんですの」に変換いたしますわ",
                        "なんじゃこれ。なんだこれ。なんやこれ。",
                        "なんですのこちら。なんですのこちら。なんですのこちら。",
                        null,
                        true),
                arguments( // TODO 65
                        "正常系: 名詞＋(じゃ|だ|ですの)ですの",
                        "アホだ。カラスや。バナナじゃ。これだ。それや。あれじゃ。",
                        "おアホですの。おカラスですの。おバナナですの。これですの。それですの。あれですの。",
                        null,
                        true),
                arguments(
                        // FIXME: 「幽霊が怖い」で終わるべきところを「幽霊が怖」で終わった場合も変換されてしまう
                        "正常系: 形容詞＋自立の後に「ですわ」を付与する場合、「。」をランダムに！に変換いたしますわ。変換記号の@1を誤って変換したりはしませんわ",
                        "@1悲しいことはとても悲しい。幽霊が怖い。幽霊が怖。",
                        "@1悲しいことはとても悲しいですわ❗お幽霊が怖いですわ❗お幽霊が怖ですわ❗",
                        new TestOption().setForceKutenToExclamation(true),
                        false),
                arguments( // TODO 67
                        "正常系: 「。」で終わる時に「！」に変換いたしますわ！",
                        "プレイする。ショットガンだ。",
                        "プレイいたしますわ❗おショットガンですの❗",
                        new TestOption().setForceKutenToExclamation(true),
                        true)
        );
    }

    @ParameterizedTest
    @MethodSource("sourceConvert")
    void TestConvert(
            String desc,
            String src,
            String want,
            TestOption topt,
            boolean wantErr
    ) throws Exception {

        if (topt != null && topt.forceCharsTestMode != null) {
            int pos = topt.forceCharsTestMode;
            EQMark.Shuffler mock = new OjosamaTest.TestShuffler(pos);
            setFinalStatic(EQMark.class.getDeclaredField("shuffler"), mock);
Debug.println(Level.FINE, "forceCharsTestMode: " + ((EQMark[]) getFinalStatic(EQMark.class.getDeclaredField("eqMarks")))[0]);
        }

        if (topt != null && topt.forceKutenToExclamation) {
            setFinalStatic(SalomeProvider.class.getDeclaredField("shuffleElementsKutenToExclamation"), new String[]{"❗", "❗"});
Debug.println(Level.FINE, "forceKutenToExclamation: " + Arrays.toString((String[]) getFinalStatic(SalomeProvider.class.getDeclaredField("shuffleElementsKutenToExclamation"))));
        }

        // forceAppendLongNote がある場合に限って任意の数付与できる。
        if (topt != null && topt.forceAppendLongNote != null) {
            setFinalStatic(SalomeProvider.class.getDeclaredField("randomizer"), topt.forceAppendLongNote);
Debug.println(Level.FINE, "forceAppendLongNote: " + getFinalStatic(SalomeProvider.class.getDeclaredField("randomizer")));
        }

        ConvertOption opt = new ConvertOption();
        opt.disableKutenToExclamation = topt == null;
        String got = new YakuwarigoModifier(opt).convert(src);
Debug.println((wantErr ? "\uD83D\uDD34" : "\uD83D\uDFE2"));
Debug.println("SOURCE:   " + src);
Debug.println("EXPECTED: " + want);
Debug.println("ACTUAL:   " + got);
        if (wantErr) {
            assertNotEquals(want, got);
        } else {
            assertEquals(want, got);
        }
    }
}