package vavi.speech.modifier.simple;

import java.util.Scanner;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class SimpleEosModifierTest {

    @Test
    void test1() throws Exception {
        SimpleEosModifier modifier = new SimpleEosModifier();
        String text = "きょうはいい天気ですね。";
        modifier.removeNe = true;
        String result = modifier.convert2joutai(text);
        assertEquals("きょうはいい天気だ。", result);

        String text2 = "きょうはいい天気だ。";
        String result2 = modifier.convert2keitai(text2);
        assertEquals("きょうはいい天気です。", result2);

        modifier.removeNe = false;
        String result3 = modifier.convert2joutai(text);
        assertEquals("きょうはいい天気だね。", result3);
    }

    // TODO doesn't work well
    @Test
    void test2() throws Exception {
        SimpleEosModifier modifier = new SimpleEosModifier("zundamon");
        modifier.removeNe = true;

        Scanner s = new Scanner(SimpleEosModifier.class.getResourceAsStream("/speech.txt"));
        while (s.hasNextLine()) {
            String line = s.nextLine();
            System.out.println(modifier.convert2joutai(line));
        }
    }
}