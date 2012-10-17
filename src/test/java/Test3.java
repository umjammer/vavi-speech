/*
 * Copyright (c) 2008 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import com.sun.jna.Pointer;

import vavi.speech.aquestalk.jna.AquesTalk;
import vavi.speech.aquestalk.jna.AquesTalkDa;


/**
 * Test3. (JNA)
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 070202 initial version <br>
 */
public class Test3 {

    /** */
    public static void main(String[] args) throws Exception {
        Test3 test3 = new Test3();
        test3.test01();
        test3.test02();
    }

    public void test01() throws Exception {
        AquesTalkDa aquesTalkDa = AquesTalkDa.INSTANCE;
        int h = aquesTalkDa.AquesTalkDa_Create();
        String koe = "ゆっくりしていってね";
        aquesTalkDa.AquesTalkDa_PlaySync(koe, 100);
        aquesTalkDa.AquesTalkDa_Release(h);
    }

    public void test02() throws Exception {
        AquesTalk aquesTalk = AquesTalk.INSTANCE;
        String koe = "こんにちは、えぬりんくです";
        int[] result = new int[1];
        Pointer wave = aquesTalk.AquesTalk_Synthe(koe, 100, result);
        aquesTalk.AquesTalk_FreeWave(wave);
    }
}

/* */
