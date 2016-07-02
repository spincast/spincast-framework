package org.spincast.tests.https;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

import org.junit.Test;
import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.SpincastTestConfig;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.inject.Module;

public class HttpsTestAbsoluteKeystorePath extends SpincastDefaultNoAppIntegrationTestBase {

    protected final String KEYSTORE_CLASSPATH = "self-signed-certificate.jks";

    protected static File keyStoreFile;

    @Override
    public void beforeClass() {
        createAbsolutePathKeyStore();
        super.beforeClass();
    }

    @Override
    public void afterClass() {
        FileUtils.deleteQuietly(keyStoreFile);
        super.afterClass();
    }

    protected void createAbsolutePathKeyStore() {

        try {
            InputStream keyStoreStream = this.getClass().getClassLoader().getResourceAsStream(this.KEYSTORE_CLASSPATH);
            assertNotNull(keyStoreStream);

            keyStoreFile = Files.createTempFile(HttpsTestAbsoluteKeystorePath.class.getName(), this.KEYSTORE_CLASSPATH).toFile();

            FileUtils.copyInputStreamToFile(keyStoreStream, keyStoreFile);
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected static class HttpsTestConfig extends SpincastTestConfig {

        private int httpsServerPort = -1;

        @Override
        public int getHttpServerPort() {
            return -1;
        }

        @Override
        public int getHttpsServerPort() {
            if(this.httpsServerPort < 0) {
                this.httpsServerPort = SpincastTestUtils.findFreePort();
            }
            return this.httpsServerPort;
        }

        @Override
        public String getHttpsKeyStorePath() {
            return HttpsTestAbsoluteKeystorePath.keyStoreFile.getAbsolutePath();
        }

        @Override
        public String getHttpsKeyStoreType() {
            return "JKS";
        }

        @Override
        public String getHttpsKeyStoreStorePass() {
            return "myStorePass";
        }

        @Override
        public String getHttpsKeyStoreKeypass() {
            return "myKeyPass";
        }
    }

    @Override
    public Module getTestingModule() {
        return new SpincastDefaultTestingModule() {

            @Override
            protected Class<? extends ISpincastConfig> getSpincastConfigClass() {
                return HttpsTestConfig.class;
            }
        };
    }

    @Test
    public void keyStoreAbsolutePath() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        IHttpResponse response = GET("/one", false, true).send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

}
