package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.testing.core.utils.SpincastTestingUtils;

public class SpincastStaticsTest {

    @Test
    public void testGetStackTrace() throws Exception {

        try {
            throw new Exception(SpincastTestingUtils.TEST_STRING);
        } catch (Exception ex) {

            String stackTrace = SpincastStatics.getStackTrace(ex);
            assertNotNull(stackTrace);
            assertTrue(stackTrace.startsWith("java.lang.Exception: " + SpincastTestingUtils.TEST_STRING));
        }
    }

    @Test
    public void testChangeInstance() throws Exception {

        // We change the instance!
        SpincastStatics.setInstance(new SpincastStatics() {

            @Override
            protected String getStackTraceInstance(Throwable ex) {
                return "modified";
            }
        });
        try {
            Exception theException = new Exception(SpincastTestingUtils.TEST_STRING);
            try {
                throw theException;
            } catch (Exception ex) {

                String stackTrace = SpincastStatics.getStackTrace(ex);
                assertNotNull(stackTrace);
                assertEquals("modified", stackTrace);
            }
        } finally {
            SpincastStatics.setInstance(null);
        }
    }

    @Test
    public void testRuntimize() throws Exception {

        Exception theException = new Exception(SpincastTestingUtils.TEST_STRING);
        try {
            throw theException;
        } catch (Exception ex) {

            assertFalse(ex instanceof RuntimeException);
            assertTrue(ex == theException);

            Exception runtimized = SpincastStatics.runtimize(ex);
            assertNotNull(runtimized);
            assertTrue(runtimized instanceof RuntimeException);
            assertEquals(SpincastTestingUtils.TEST_STRING, runtimized.getMessage());
            assertTrue(theException != runtimized);
            Throwable cause = runtimized.getCause();
            assertTrue(cause == theException);

        }
    }

    @Test
    public void testRuntimizeInvocationTargetException() throws Exception {

        Exception theException = new Exception(SpincastTestingUtils.TEST_STRING);
        InvocationTargetException invocationTargetException = new InvocationTargetException(theException);

        try {
            throw invocationTargetException;
        } catch (Exception ex) {

            assertFalse(ex instanceof RuntimeException);
            assertTrue(ex == invocationTargetException);
            assertTrue(theException == invocationTargetException.getTargetException());

            Exception runtimized = SpincastStatics.runtimize(ex);
            assertNotNull(runtimized);
            assertTrue(runtimized instanceof RuntimeException);
            assertEquals(SpincastTestingUtils.TEST_STRING, runtimized.getMessage());
            assertTrue(theException != runtimized);
            Throwable cause = runtimized.getCause();
            assertTrue(cause == theException);

        }
    }

    @Test
    public void testRuntimizeRuntimeException() throws Exception {

        RuntimeException theException = new RuntimeException(SpincastTestingUtils.TEST_STRING);
        try {
            throw theException;
        } catch (Exception ex) {

            assertTrue(ex instanceof RuntimeException);

            Exception runtimized = SpincastStatics.runtimize(ex);
            assertNotNull(runtimized);
            assertTrue(runtimized instanceof RuntimeException);
            assertEquals(SpincastTestingUtils.TEST_STRING, runtimized.getMessage());
            assertTrue(theException == runtimized);
            Throwable cause = runtimized.getCause();
            assertNull(cause);

        }
    }

    //==========================================
    // To be run alone, interupting the thread makes
    // the other tests fail!
    //==========================================
    @Ignore
    @Test
    public void testRuntimizeInterruptedException() throws Exception {

        InterruptedException theException = new InterruptedException(SpincastTestingUtils.TEST_STRING);
        try {
            throw theException;
        } catch (Exception ex) {

            assertFalse(Thread.currentThread().isInterrupted());

            Exception runtimized = SpincastStatics.runtimize(ex);

            assertTrue(Thread.currentThread().isInterrupted());

            assertNotNull(runtimized);
            assertTrue(runtimized instanceof RuntimeException);
            assertEquals(SpincastTestingUtils.TEST_STRING, runtimized.getMessage());
            assertTrue(theException != runtimized);
            Throwable cause = runtimized.getCause();
            assertTrue(cause == theException);

        }
    }

    @Test
    public void createMap1() throws Exception {

        Map<String, String> map = SpincastStatics.map("k1", "v1");
        assertNotNull(map);
        assertEquals(1, map.size());
        assertNotNull(map.get("k1"));
        assertEquals("v1", map.get("k1"));
    }

    @Test
    public void createMap1Object() throws Exception {

        Map<String, Date> map = SpincastStatics.map("k1", new Date());
        assertNotNull(map);
        assertEquals(1, map.size());
        assertNotNull(map.get("k1"));
        assertTrue(map.get("k1") instanceof Date);
    }

    @Test
    public void createMap1NullKey() throws Exception {

        try {
            SpincastStatics.map(null, "v1");
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void createMap2() throws Exception {

        Map<String, String> map = SpincastStatics.map("k1", "v1", "k2", "v2");
        assertNotNull(map);
        assertEquals(2, map.size());
        assertNotNull(map.get("k1"));
        assertEquals("v1", map.get("k1"));
        assertNotNull(map.get("k2"));
        assertEquals("v2", map.get("k2"));
    }

    @Test
    public void createMap2Object() throws Exception {

        Map<String, Date> map = SpincastStatics.map("k1", new Date(), "k2", new Date());
        assertNotNull(map);
        assertEquals(2, map.size());
        assertNotNull(map.get("k1"));
        assertTrue(map.get("k1") instanceof Date);
        assertNotNull(map.get("k2"));
        assertTrue(map.get("k2") instanceof Date);

    }

    @Test
    public void createMap2NullKey() throws Exception {

        try {
            SpincastStatics.map("k1", "v1", null, "v2");
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void createMap3() throws Exception {

        Map<String, String> map = SpincastStatics.map("k1", "v1", "k2", "v2", "k3", "v3");
        assertNotNull(map);
        assertEquals(3, map.size());
        assertNotNull(map.get("k1"));
        assertEquals("v1", map.get("k1"));
        assertNotNull(map.get("k2"));
        assertEquals("v2", map.get("k2"));
        assertNotNull(map.get("k3"));
        assertEquals("v3", map.get("k3"));
    }

    @Test
    public void createMap3Object() throws Exception {

        Map<String, Date> map = SpincastStatics.map("k1", new Date(), "k2", new Date(), "k3", new Date());
        assertNotNull(map);
        assertEquals(3, map.size());
        assertNotNull(map.get("k1"));
        assertTrue(map.get("k1") instanceof Date);
        assertNotNull(map.get("k2"));
        assertTrue(map.get("k2") instanceof Date);
        assertNotNull(map.get("k3"));
        assertTrue(map.get("k3") instanceof Date);
    }

    @Test
    public void createMap3NullKey() throws Exception {

        try {
            SpincastStatics.map("k1", "v1", "k2", "v2", null, "v3");
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void createMap4() throws Exception {

        Map<String, String> map = SpincastStatics.map("k1", "v1", "k2", "v2", "k3", "v3", "k4", "v4");
        assertNotNull(map);
        assertEquals(4, map.size());
        assertNotNull(map.get("k1"));
        assertEquals("v1", map.get("k1"));
        assertNotNull(map.get("k2"));
        assertEquals("v2", map.get("k2"));
        assertNotNull(map.get("k3"));
        assertEquals("v3", map.get("k3"));
        assertNotNull(map.get("k4"));
        assertEquals("v4", map.get("k4"));
    }

    @Test
    public void createMap4Object() throws Exception {

        Map<String, Date> map = SpincastStatics.map("k1", new Date(), "k2", new Date(), "k3", new Date(), "k4", new Date());
        assertNotNull(map);
        assertEquals(4, map.size());
        assertNotNull(map.get("k1"));
        assertTrue(map.get("k1") instanceof Date);
        assertNotNull(map.get("k2"));
        assertTrue(map.get("k2") instanceof Date);
        assertNotNull(map.get("k3"));
        assertTrue(map.get("k3") instanceof Date);
        assertNotNull(map.get("k4"));
        assertTrue(map.get("k4") instanceof Date);
    }

    @Test
    public void createMap4NullKey() throws Exception {

        try {
            SpincastStatics.map("k1", "v1", "k2", "v2", "k3", "v3", null, "v4");
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void createMap5() throws Exception {

        Map<String, String> map = SpincastStatics.map("k1", "v1", "k2", "v2", "k3", "v3", "k4", "v4", "k5", "v5");
        assertNotNull(map);
        assertEquals(5, map.size());
        assertNotNull(map.get("k1"));
        assertEquals("v1", map.get("k1"));
        assertNotNull(map.get("k2"));
        assertEquals("v2", map.get("k2"));
        assertNotNull(map.get("k3"));
        assertEquals("v3", map.get("k3"));
        assertNotNull(map.get("k4"));
        assertEquals("v4", map.get("k4"));
        assertNotNull(map.get("k5"));
        assertEquals("v5", map.get("k5"));
    }

    @Test
    public void createMap5Object() throws Exception {

        Map<String, Date> map =
                SpincastStatics.map("k1",
                                    new Date(),
                                    "k2",
                                    new Date(),
                                    "k3",
                                    new Date(),
                                    "k4",
                                    new Date(),
                                    "k5",
                                    new Date());
        assertNotNull(map);
        assertEquals(5, map.size());
        assertNotNull(map.get("k1"));
        assertTrue(map.get("k1") instanceof Date);
        assertNotNull(map.get("k2"));
        assertTrue(map.get("k2") instanceof Date);
        assertNotNull(map.get("k3"));
        assertTrue(map.get("k3") instanceof Date);
        assertNotNull(map.get("k4"));
        assertTrue(map.get("k4") instanceof Date);
        assertNotNull(map.get("k5"));
        assertTrue(map.get("k5") instanceof Date);
    }

    @Test
    public void createMap5NullKey() throws Exception {

        try {
            SpincastStatics.map("k1", "v1", "k2", "v2", "k3", "v3", "k4", "v4", null, "v5");
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void arrayToList() throws Exception {

        String[] array = new String[]{"aaa", "bbb", "ccc"};
        List<String> list = SpincastStatics.toList(array, false);
        assertNotNull(list);
        assertEquals(3, list.size());
        assertTrue(list.contains("aaa"));
        assertTrue(list.contains("bbb"));
        assertTrue(list.contains("ccc"));

        list.add("ddd");
        assertEquals(4, list.size());
    }

    @Test
    public void arrayToListNullToNull() throws Exception {
        List<String> list = SpincastStatics.toList(null, false);
        assertNull(list);
    }

    @Test
    public void arrayToListNullToEmpty() throws Exception {
        List<String> list = SpincastStatics.toList(null, true);
        assertNotNull(list);
        assertEquals(0, list.size());

        list.add("ddd");
        assertEquals(1, list.size());
    }

}
