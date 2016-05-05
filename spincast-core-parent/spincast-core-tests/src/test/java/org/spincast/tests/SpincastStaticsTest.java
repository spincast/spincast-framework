package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;

import org.junit.Ignore;
import org.junit.Test;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.testing.core.utils.SpincastTestUtils;

public class SpincastStaticsTest {

    @Test
    public void testGetStackTrace() throws Exception {

        try {
            throw new Exception(SpincastTestUtils.TEST_STRING);
        } catch(Exception ex) {

            String stackTrace = SpincastStatics.getStackTrace(ex);
            assertNotNull(stackTrace);
            assertTrue(stackTrace.startsWith("java.lang.Exception: " + SpincastTestUtils.TEST_STRING));
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
            Exception theException = new Exception(SpincastTestUtils.TEST_STRING);
            try {
                throw theException;
            } catch(Exception ex) {

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

        Exception theException = new Exception(SpincastTestUtils.TEST_STRING);
        try {
            throw theException;
        } catch(Exception ex) {

            assertFalse(ex instanceof RuntimeException);
            assertTrue(ex == theException);

            Exception runtimized = SpincastStatics.runtimize(ex);
            assertNotNull(runtimized);
            assertTrue(runtimized instanceof RuntimeException);
            assertEquals(SpincastTestUtils.TEST_STRING, runtimized.getMessage());
            assertTrue(theException != runtimized);
            Throwable cause = runtimized.getCause();
            assertTrue(cause == theException);

        }
    }

    @Test
    public void testRuntimizeInvocationTargetException() throws Exception {

        Exception theException = new Exception(SpincastTestUtils.TEST_STRING);
        InvocationTargetException invocationTargetException = new InvocationTargetException(theException);

        try {
            throw invocationTargetException;
        } catch(Exception ex) {

            assertFalse(ex instanceof RuntimeException);
            assertTrue(ex == invocationTargetException);
            assertTrue(theException == invocationTargetException.getTargetException());

            Exception runtimized = SpincastStatics.runtimize(ex);
            assertNotNull(runtimized);
            assertTrue(runtimized instanceof RuntimeException);
            assertEquals(SpincastTestUtils.TEST_STRING, runtimized.getMessage());
            assertTrue(theException != runtimized);
            Throwable cause = runtimized.getCause();
            assertTrue(cause == theException);

        }
    }

    @Test
    public void testRuntimizeRuntimeException() throws Exception {

        RuntimeException theException = new RuntimeException(SpincastTestUtils.TEST_STRING);
        try {
            throw theException;
        } catch(Exception ex) {

            assertTrue(ex instanceof RuntimeException);

            Exception runtimized = SpincastStatics.runtimize(ex);
            assertNotNull(runtimized);
            assertTrue(runtimized instanceof RuntimeException);
            assertEquals(SpincastTestUtils.TEST_STRING, runtimized.getMessage());
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

        InterruptedException theException = new InterruptedException(SpincastTestUtils.TEST_STRING);
        try {
            throw theException;
        } catch(Exception ex) {

            assertFalse(Thread.currentThread().isInterrupted());

            Exception runtimized = SpincastStatics.runtimize(ex);

            assertTrue(Thread.currentThread().isInterrupted());

            assertNotNull(runtimized);
            assertTrue(runtimized instanceof RuntimeException);
            assertEquals(SpincastTestUtils.TEST_STRING, runtimized.getMessage());
            assertTrue(theException != runtimized);
            Throwable cause = runtimized.getCause();
            assertTrue(cause == theException);

        }
    }

}
