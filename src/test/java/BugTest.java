import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.junit.jupiter.api.Test;
import vavi.util.Debug;


/**
 * BugTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2023-02-08 nsano initial version <br>
 */
public class BugTest {

    @Test
    void test1() throws Exception {
        IntBuffer ib = ByteBuffer.allocateDirect(72463209 * 4).asIntBuffer().asReadOnlyBuffer().duplicate();
Debug.println(ib.getClass().getName() + ", " + ib);
        ib.get(0);
    }
}
