package cmri.utils.lang;

import junit.framework.TestCase;

import java.util.Date;

/**
 * Created by zhuyin on 3/12/15.
 */
public class TestDateUtils extends TestCase {
    public void testConvertToDateString() {
        String str = TimeHelper.toString(new Date(1), "yyyyMMddHHmmss.SSS");
        assertEquals("19700101080000.001", str);
    }
}
