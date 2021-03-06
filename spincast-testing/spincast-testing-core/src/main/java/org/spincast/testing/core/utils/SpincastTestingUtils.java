package org.spincast.testing.core.utils;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

/**
 * Spincast testing utilities.
 */
public class SpincastTestingUtils {

    public static final String TEST_STRING = "‛'ïœ𣎴𠀋ᚡŠšÈÆæÐð𝅘𝅥𝅯’";
    public static final String TEST_STRING_LONG =
            "‛'ïœ𣎴𠀋ᚡŠšÈÆæÐð𝅘𝅥𝅯’0123456789asasdnalfh23uio4y4213ralksfan394u2348902ursdfjsdfj2534tuuegjdfgjdfgdgjmelfj234i2jsdjfsdjgdlkgjdlkfgjdgj9dgh09fgdhfdgksdjfasdfkasdf858656";

    private static Date testDate;

    public static final Handler<DefaultRequestContext> dummyRouteHandler = new Handler<DefaultRequestContext>() {

        @Override
        public void handle(DefaultRequestContext exchange) {
            //...
        }
    };

    protected static InputStream getThisClassFileInputStream() {
        String s = SpincastTestingUtils.class.getName();
        int i = s.lastIndexOf(".");
        if (i > -1) {
            s = s.substring(i + 1);
        }
        s = s + ".class";
        return SpincastTestingUtils.class.getResourceAsStream(s);
    }

    public static File generateTempClassFile(File writableDir) {

        InputStream fileOriInputStream = null;
        try {
            File dir = new File(writableDir + "/TestUtils");

            fileOriInputStream = getThisClassFileInputStream();
            File fileTarget = new File(dir.getAbsolutePath() + "/file" + UUID.randomUUID().toString() + ".class");
            FileUtils.copyInputStreamToFile(fileOriInputStream, fileTarget);

            return fileTarget;
        } catch (Exception ex) {
            SpincastStatics.closeQuietly(fileOriInputStream);
            throw SpincastStatics.runtimize(ex);
        }
    }

    /**
     * Finds a free port.
     */
    public static int findFreePort() {

        int port = -1;
        try {
            ServerSocket s = null;
            try {
                s = new ServerSocket(0);
                port = s.getLocalPort();
            } finally {
                s.close();
            }
            return port;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    /**
     * Is the specified port available?
     *
     * Note that this will be true at the time the method run,
     * but may be false a couple of milliseconds after...
     */
    public static boolean isPortAvailable(String host, int port) {
        try {
            (new Socket(host, port)).close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Since Websockets are async, it's hard to wait for a
     * specific period of time before validating if an event
     * occured.
     *
     * This method checks frequently if the TrueChecker returns true
     * and when it does, it returns too. Il also waits
     * for a maximum of 5 seconds.
     */
    public static boolean waitForTrue(TrueChecker trueChecker) {
        return waitForTrue(trueChecker, 5000);
    }

    /**
     * Since Websockets are async, it's hard to wait for a
     * specific period of time before validating if an event
     * occured.
     *
     * This method checks frequently if the TrueChecker returns true
     * and when it does, it returns too. Il also waits
     * for the maximum number of milliseconds specified.
     */
    public static boolean waitForTrue(TrueChecker trueChecker, int maxMillisecToWait) {

        if (trueChecker == null) {
            throw new RuntimeException("The true checker can't be null");
        }

        if (maxMillisecToWait < 0) {
            maxMillisecToWait = 0;
        }

        try {
            int waitTimeTotal = 0;
            while (true) {

                if (trueChecker.check()) {
                    return true;
                }

                if (waitTimeTotal >= maxMillisecToWait) {
                    return false;
                }

                Thread.sleep(waitForTrueLoopInterval());
                waitTimeTotal += waitForTrueLoopInterval();
            }
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected static int waitForTrueLoopInterval() {
        return 50;
    }

    /**
     * This method checks frequently if the size of the specified collection
     * has reached the expected size. If so, it returns. Always returns
     * after 5 seconds.
     */
    public static boolean waitForSize(Collection<?> collection, int expected) {
        return waitForSize(collection, expected, 5000);
    }

    /**
     * This method checks frequently if the size of the specified collection
     * has reached the expected size. If so, it returns.
     */
    public static boolean waitForSize(final Collection<?> collection,
                                      final int expected,
                                      int maxMillisecToWait) {

        Objects.requireNonNull(collection, "The collection can't be NULL");

        if (expected <= 0) {
            throw new RuntimeException("The expected size must be at least 1.");
        }

        return waitForTrue(new TrueChecker() {

            @Override
            public boolean check() {
                return collection.size() >= expected;
            }

        }, maxMillisecToWait);
    }

    /**
     * This method checks frequently if the size of the specified collection
     * is under the speficied max size. If so, it returns. Always returns
     * after 5 seconds.
     */
    public static boolean waitForMaxSize(Collection<?> collection, int maxSize) {
        return waitForMaxSize(collection, maxSize, 5000);
    }

    /**
     * This method checks frequently if the size of the specified collection
     * is under the speficied max size. If so, it returns.
     */
    public static boolean waitForMaxSize(final Collection<?> collection,
                                         final int maxSize,
                                         int maxMillisecToWait) {

        Objects.requireNonNull(collection, "The collection can't be NULL");

        return waitForTrue(new TrueChecker() {

            @Override
            public boolean check() {
                return collection.size() <= maxSize;
            }

        }, maxMillisecToWait);
    }

    /**
     * This method checks frequently if the number of the first element
     * of the specified int[] has reached the expected value. If so, it returns.
     * Always returns after 5 seconds.
     */
    public static boolean waitForNumber(int[] oneIntArray, int expected) {
        return waitForNumber(oneIntArray, expected, 5000);
    }

    /**
     * This method checks frequently if the number of the first element
     * of the specified int[] has reached the expected value. If so, it returns.
     */
    public static boolean waitForNumber(final int[] oneIntArray,
                                        final int expected,
                                        int maxMillisecToWait) {

        if (oneIntArray == null || oneIntArray.length == 0) {
            throw new RuntimeException("The array can't be null and must have at least (and probably only) one element.");
        }

        return waitForTrue(new TrueChecker() {

            @Override
            public boolean check() {
                return oneIntArray[0] >= expected;
            }

        }, maxMillisecToWait);
    }

    /**
     * Get a test date without time.
     */
    public static Date getTestDateNoTime() {

        if (testDate == null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            testDate = cal.getTime();
        }

        return testDate;
    }

    /**
     * This method can be called to log some information about
     * an exception wihch is hard to debug. It will log it in
     * a directory which won't be deleted when the tests are done.
     */
    public static void logException(String fileName, Exception ex, String info) {

        try {
            File baseDir = new File(System.getProperty("java.io.tmpdir"));
            if (!baseDir.isDirectory()) {
                throw new RuntimeException("Temporary directory doesn't exist : " + baseDir.getAbsolutePath());
            }

            File dir = new File(baseDir, "/spincast_testing_exceptions");
            if (!dir.isDirectory()) {
                boolean mkdirs = dir.mkdirs();
                assertTrue(mkdirs);
            }
            assertTrue(dir.canWrite());

            File file = new File(dir.getAbsolutePath() + "/" + fileName);

            FileUtils.write(file, info + "\n" + ex.getMessage() + "\n" + SpincastStatics.getStackTrace(ex), "UTF-8");
        } catch (Exception ex2) {
            System.err.println("Error logging testing exception: " + ex2);
        }
    }

    /**
     * Gets a File from a classpath path (only works
     * when the application si not running from an executable
     * .jar).
     */
    public static File getClasspathFileNotInJar(String relativePath) {
        if (relativePath == null) {
            return null;
        }

        try {
            relativePath = StringUtils.stripStart(relativePath, "/");
            URL url = ClassLoader.getSystemResource(relativePath);
            if (url == null) {
                return null;
            }
            return Paths.get(url.toURI()).toFile();
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }


}
