/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.speech;

import java.io.IOException;


/**
 * Player.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/09/19 umjammer initial version <br>
 */
public interface Player {

    /** */
    void play(byte[] data) throws IOException;
}

/* */
