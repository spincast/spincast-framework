package org.spincast.testing.core.utils;

import java.io.File;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.UUID;

import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.shaded.org.apache.commons.io.IOUtils;

/**
 * Spincast tests utilities.
 */
public class SpincastTestUtils {

    public static final String TEST_STRING = "‛'ïœ𣎴𠀋ᚡŠšÈÆæÐð𝅘𝅥𝅯’";
    public static final String TEST_STRING_LONG =
            "‛'ïœ𣎴𠀋ᚡŠšÈÆæÐð𝅘𝅥𝅯’0123456789asasdnalfh23uio4y4213ralksfan394u2348902ursdfjsdfj2534tuuegjdfgjdfgdgjmelfj234i2jsdjfsdjgdlkgjdlkfgjdgj9dgh09fgdhfdgksdjfasdfkasdf858656";

    public static final IHandler<IDefaultRequestContext> dummyRouteHandler = new IHandler<IDefaultRequestContext>() {

        @Override
        public void handle(IDefaultRequestContext exchange) {
            //...
        }
    };

    protected static InputStream getThisClassFileInputStream() {
        String s = SpincastTestUtils.class.getName();
        int i = s.lastIndexOf(".");
        if(i > -1) {
            s = s.substring(i + 1);
        }
        s = s + ".class";
        return SpincastTestUtils.class.getResourceAsStream(s);
    }

    public static File generateTempClassFile(File writableDir) {

        InputStream fileOriInputStream = null;
        try {
            File dir = new File(writableDir + "/TestUtils");

            fileOriInputStream = getThisClassFileInputStream();
            File fileTarget = new File(dir.getAbsolutePath() + "/file" + UUID.randomUUID().toString() + ".class");
            FileUtils.copyInputStreamToFile(fileOriInputStream, fileTarget);

            return fileTarget;
        } catch(Exception ex) {
            IOUtils.closeQuietly(fileOriInputStream);
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
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }

    }

}
