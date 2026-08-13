/*
 * Copyright (c) 2023 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.modifier.yakuwarigo.zundamon;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import vavi.speech.modifier.yakuwarigo.YakuwarigoModifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static vavi.speech.modifier.yakuwarigo.YakuwarigoModifier.ConvertOption;


/**
 * ZundamonTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2023-04-23 nsano initial version <br>
 */
class ZundamonTest {

    static YakuwarigoModifier modifier;

    static {
        try {
            ConvertOption option = new ConvertOption();
            option.name = "zundamon";
            modifier = new YakuwarigoModifier(option);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    static Stream<Arguments> 断定() {
        return Stream.of(
                arguments("これはペンです。", "これはペンなのだ。"),
                arguments("これはペンだ。", "これはペンなのだ。"),
                arguments("今日はいい天気ですね。", "今日はいい天気なのだ。"),
                arguments("そうだね。", "そうなのだ。"),
                arguments("静かです。", "静かなのだ。"),
                arguments("明日は雨だろう。", "明日は雨なのだ。"),
                arguments("そうでしょう。", "そうなのだ。"),
                arguments("そうですか？", "そうなのだ？"),
                arguments("元気ですか。", "元気なのだ。"),
                arguments("これは本。", "これは本なのだ。"),
                arguments("30分の1以下である。", "30分の1以下であるのだ。")
        );
    }

    static Stream<Arguments> 丁寧語をやめる() {
        return Stream.of(
                arguments("わたしは学校へ行きます。", "ボクは学校へ行くのだ。"),
                arguments("帰ります。", "帰るのだ。"),
                arguments("違います。", "違うのだ。"),
                arguments("行きましょう。", "行くのだ。"),
                arguments("行きますか？", "行くのだ？"),
                arguments("食べています。", "食べているのだ。"),
                arguments("食べません。", "食べないのだ。"),
                arguments("できません。", "できないのだ。"),
                arguments("行きませんでした。", "行かなかったのだ。"),
                arguments("ございません。", "ないのだ。"),
                arguments("ありがとうございます。", "ありがとうなのだ。"),
                arguments("ありがとうございました。", "ありがとうなのだ。"),
                arguments("美しいです。", "美しいのだ。"),
                arguments("行きたいです。", "行きたいのだ。"),
                arguments("早かったです。", "早かったのだ。"),
                arguments("きれいでした。", "きれいだったのだ。"),
                arguments("元気ですから、大丈夫です。", "元気だから、大丈夫なのだ。"),
                arguments("雨ですので、やめます。", "雨なので、やめるのだ。")
        );
    }

    /** 五段活用は音便が活用型ごとに違う */
    static Stream<Arguments> 過去形に戻す() {
        return Stream.of(
                arguments("買いました。", "買ったのだ。"),      // 五段・ワ行促音便
                arguments("書きました。", "書いたのだ。"),      // 五段・カ行イ音便
                arguments("行きました。", "行ったのだ。"),      // 五段・カ行促音便
                arguments("泳ぎました。", "泳いだのだ。"),      // 五段・ガ行
                arguments("話しました。", "話したのだ。"),      // 五段・サ行
                arguments("読みました。", "読んだのだ。"),      // 五段・マ行
                arguments("帰りました。", "帰ったのだ。"),      // 五段・ラ行
                arguments("食べました。", "食べたのだ。"),      // 一段
                arguments("来ました。", "来たのだ。"),         // カ変・来ル
                arguments("しました。", "したのだ。")          // サ変・スル
        );
    }

    static Stream<Arguments> 用言で終わる文() {
        return Stream.of(
                arguments("とても嬉しい。", "とても嬉しいのだ。"),
                arguments("食べたい。", "食べたいのだ。"),
                arguments("そうではない。", "そうではないのだ。"),
                arguments("ならぬ。", "ならないのだ。"),
                arguments("本を読んだ。", "本を読んだのだ。"),
                arguments("彼は死んだ。", "彼は死んだのだ。"),
                arguments("我々は一人の英雄を失った。", "我々は一人の英雄を失ったのだ。")
        );
    }

    /** 基本形に戻すので命令形も「〜のだ」になる */
    static Stream<Arguments> 命令と依頼() {
        return Stream.of(
                arguments("早く走れ！", "早く走るのだ！"),
                arguments("やめろ！", "やめるのだ！"),
                arguments("そこに座りなさい。", "そこに座るのだ。"),
                arguments("見てください。", "見てほしいのだ。"),
                arguments("野球しようぜ。", "野球をしたいのだ。"),
                arguments("野球するな。", "野球をしちゃだめなのだ。")
        );
    }

    static Stream<Arguments> 人称() {
        return Stream.of(
                arguments("俺はお前を許さない。", "ボクはおまえを許さないのだ。"),
                arguments("私の弟。", "ボクの弟なのだ。")
        );
    }

    /** すでに「のだ」で終わっている文は変わらない */
    static Stream<Arguments> 冪等() {
        return Stream.of(
                arguments("行くのだ。", "行くのだ。"),
                arguments("行くのだよ。", "行くのだ。"),
                arguments("行くんだ。", "行くのだ。"),
                arguments("行くのか？", "行くのだ？"),
                arguments("ずんだもんなのだ。", "ずんだもんなのだ。"),
                arguments("ボクはずんだもんなのだ。", "ボクはずんだもんなのだ。")
        );
    }

    /** 文の途中を文末と間違えない */
    static Stream<Arguments> 文末でないところは変換しない() {
        return Stream.of(
                arguments("読んだ本を返す。", "読んだ本を返すのだ。"),
                arguments("国民よ立て！", "国民よ立つのだ！"),
                arguments("諸君！", "諸君！"),
                arguments("しかし、これは敗北を意味するのか？", "しかし、これは敗北を意味するのだ？"),
                arguments("にもかかわらず今日まで戦い抜いてこられたのは何故か？", "にもかかわらず今日まで戦い抜いてこられたのは何故か？"),
                arguments("我々は過酷な宇宙空間を生活の場としながらも共に苦悩し、錬磨してきた。",
                          "我々は過酷な宇宙空間を生活の場としながらも共に苦悩し、錬磨してきたのだ。")
        );
    }

    @ParameterizedTest
    @MethodSource({"断定", "丁寧語をやめる", "過去形に戻す", "用言で終わる文", "命令と依頼", "人称", "冪等", "文末でないところは変換しない"})
    void test(String in, String expected) throws IOException {
        assertEquals(expected, modifier.convert(in));
    }

    /** the result of a conversion must not be converted again */
    @ParameterizedTest
    @MethodSource({"断定", "丁寧語をやめる", "過去形に戻す", "用言で終わる文", "命令と依頼", "人称", "冪等", "文末でないところは変換しない"})
    void test2(String in, String expected) throws IOException {
        assertEquals(expected, modifier.convert(expected));
    }
}
