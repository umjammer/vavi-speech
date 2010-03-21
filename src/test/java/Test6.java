/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.util.Enumeration;
import java.util.Properties;

import vavi.speech.aquestalk2.AquesTalk2Da;


/**
 * Test6. (JNI 2)
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
public class Test6 {

    /** */
    public static void main(String[] args) throws Exception {
        Test6 app = new Test6();
        app.test01();
    }

    public void test01() throws Exception {
        AquesTalk2Da aquesTalkDa = new AquesTalk2Da();
        aquesTalkDa.setPhont("tmp/macosx/AquesTalk2_Mac/phont/aq_f1b.phont");

        Properties props = new Properties();
        props.load(Test6.class.getResourceAsStream("table.properties"));
        Enumeration<?> e = props.propertyNames();
        while (e.hasMoreElements()) {
            String name = String.class.cast(e.nextElement());
System.err.println("Japanese: " + name);
            aquesTalkDa.play(props.getProperty(name), true);
        }
    }
}

/* */
