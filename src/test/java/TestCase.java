/*
 * Copyright (c) 2025 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import vavi.speech.Phonemizer;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * TestCase.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2025-03-07 nsano initial version <br>
 */
@PropsEntity(url = "file://${user.dir}/local.properties")
class TestCase {

    static boolean localPropertiesExists() {
        return Files.exists(Paths.get("local.properties"));
    }

    @Property(name = "testcase.file")
    String file;

    @BeforeEach
    void setup() throws Exception {
        if (localPropertiesExists()) {
            PropsEntity.Util.bind(this);
        }
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "YAHOOJAPAN_API_KEY", matches = ".*")
    void test1() throws Exception {
        Path path = Path.of(file);
        List<String> lines = Files.readAllLines(path);
        Phonemizer phonemizer = new vavi.speech.phonemizer.YahooJapanJaPhonemizer();
        lines.forEach(line -> {
            if (!line.trim().isEmpty()) {
                String result = phonemizer.phoneme(line);
//Debug.println(result);
                char[] chars = result.toCharArray();
                IntStream.range(0, chars.length).mapToObj(c -> chars[c]).forEach(c -> {
                    if (!Character.isWhitespace(c))
                        System.out.printf("%c%n", c);
                });
            }
        });
    }
}
