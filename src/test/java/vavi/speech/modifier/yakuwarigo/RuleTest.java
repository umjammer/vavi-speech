/*
 * Copyright (c) 2023 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech.modifier.yakuwarigo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import vavi.speech.modifier.simple.SimpleEosModifier;
import vavi.speech.modifier.yakuwarigo.salome.SalomeProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static vavi.speech.modifier.yakuwarigo.YakuwarigoModifier.ConvertOption;


/**
 * RuleTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2023-01-31 nsano initial version <br>
 */
@SuppressWarnings("DataFlowIssue")
public class RuleTest {

    @Test
    void test1() throws Exception {
        Rule rule = Provider.gson.fromJson(new InputStreamReader(SalomeProvider.class.getResourceAsStream("rule.json")), Rule.class);
        assertEquals("名詞", rule.sentenceEndingParticleConvertRules[0].conditions1[0].feature.elements()[0]);
//System.out.println("---- sentenceEndingParticleConvertRules ----");
//Arrays.stream(rule.sentenceEndingParticleConvertRules).forEach(System.out::println);
        assertEquals(1, rule.sentenceEndingParticleConvertRules.length);
//System.out.println("---- continuousConditionsConvertRules ----");
//Arrays.stream(rule.continuousConditionsConvertRules).forEach(System.out::println);
        assertEquals(18, rule.continuousConditionsConvertRules.length);
//System.out.println("---- convertRules ----");
//Arrays.stream(rule.convertRules).forEach(System.out::println);
        assertEquals(72, rule.convertRules.length);
        assertEquals(70, Arrays.stream(rule.convertRules).filter(r -> !r.disabled).count());
//System.out.println("---- excludeRules ----");
//Arrays.stream(rule.excludeRules).forEach(System.out::println);
        assertEquals(2, rule.excludeRules.length);
//System.out.println(Provider.gson.toJson(rule));
//        System.out.println(gson.toJson(rule.convertRules[0].conditions[0], ConvertCondition.class));
//        System.out.println(gson.toJson(rule.convertRules[0].conditions, ConvertCondition[].class));
        Files.write(Paths.get("tmp", "out.json"), Provider.gson.toJson(rule).getBytes());
    }

    @Test
    void test2() throws IOException {
        ConvertOption option = new ConvertOption();
        option.name = "zundamon";
        option.disableLongNote = true;
        option.disablePrefix = true;
        option.disableKutenToExclamation = true;
        YakuwarigoModifier modifier = new YakuwarigoModifier(option);

        Scanner s = new Scanner(SimpleEosModifier.class.getResourceAsStream("/speech.txt"));
        while (s.hasNextLine()) {
            String line = s.nextLine();
            System.out.println(modifier.convert(line));
        }
    }
}
