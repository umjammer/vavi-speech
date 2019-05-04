/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import org.junit.jupiter.api.Test;

import com.sun.jna.Pointer;

import vavi.speech.aquestalk.jna.AquesTalk;
import vavi.speech.aquestalk.jna.AquesTalkDa;


/**
 * Test3. (JNA)
 * 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
public class Test3 {

    /** */
    public static void main(String[] args) throws Exception {
        Test3 app = new Test3();
        app.test01();
        app.test02();
    }

    @Test
    public void test01() throws Exception {
        AquesTalkDa aquesTalkDa = AquesTalkDa.INSTANCE;
        int h = aquesTalkDa.AquesTalkDa_Create();
        String koe = "ゆっくりしていってね";
        aquesTalkDa.AquesTalkDa_PlaySync(koe, 100);
        aquesTalkDa.AquesTalkDa_Release(h);
    }

    @Test
    public void test02() throws Exception {
        AquesTalk aquesTalk = AquesTalk.INSTANCE;
        String koe = "こんにちは、えぬりんくです";
        int[] result = new int[1];
        Pointer wave = aquesTalk.AquesTalk_Synthe(koe, 100, result);
        aquesTalk.AquesTalk_FreeWave(wave);
    }
}

/* */
