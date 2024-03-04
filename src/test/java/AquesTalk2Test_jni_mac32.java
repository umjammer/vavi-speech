/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import vavi.speech.aquestalk2.AquesTalk2Da;
import vavi.util.Debug;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * AquesTalk2Test_jni_mac32. (JNI 2)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
@EnabledIf("localPropertiesExists")
@EnabledIfSystemProperty(named = "os.arch", matches = "x86")
@PropsEntity(url = "file:local.properties")
public class AquesTalk2Test_jni_mac32 {

    /** */
    public static void main(String[] args) throws Exception {
        AquesTalk2Test_jni_mac32 app = new AquesTalk2Test_jni_mac32();
        app.test01();
    }

    static boolean localPropertiesExists() {
        return Files.exists(Paths.get("local.properties"));
    }

    @Property
    String aquesTalk2font;

    @BeforeEach
    void setup() throws Exception {
        PropsEntity.Util.bind(this);
    }

    @Test
    void test01() throws Exception {
        AquesTalk2Da aquesTalkDa = new AquesTalk2Da();
        aquesTalkDa.setPhont(aquesTalk2font);

        Properties props = new Properties();
        props.load(AquesTalk2Test_jni_mac32.class.getResourceAsStream("table.properties"));
        Enumeration<?> e = props.propertyNames();
        while (e.hasMoreElements()) {
            String name = (String) e.nextElement();
Debug.println("Japanese: " + name);
            aquesTalkDa.play(props.getProperty(name), true);
        }
    }
}

/* */
