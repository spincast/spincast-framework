package org.spincast.tests.https;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.TestingMode;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.config.SpincastConfigPluginConfig;
import org.spincast.testing.core.utils.SpincastConfigTestingDefault;
import org.spincast.testing.core.utils.SpincastTestingUtils;
import org.spincast.testing.utils.ExpectingBeforeClassException;

import com.google.inject.Inject;

@ExpectingBeforeClassException
public class HttpsTestInvalidKeyStorePassword extends NoAppStartHttpServerTestingBase {

    @Override
    protected Class<? extends SpincastConfig> getTestingConfigImplementationClass2() {
        return HttpsTestConfig.class;
    }

    protected static class HttpsTestConfig extends SpincastConfigTestingDefault {

        private int httpsServerPort = -1;

        /**
         * Constructor
         */
        @Inject
        protected HttpsTestConfig(SpincastConfigPluginConfig spincastConfigPluginConfig, @TestingMode boolean testingMode) {
            super(spincastConfigPluginConfig, testingMode);
        }

        @Override
        public int getHttpServerPort() {
            return -1;
        }

        @Override
        public int getHttpsServerPort() {
            if (this.httpsServerPort < 0) {
                this.httpsServerPort = SpincastTestingUtils.findFreePort();
            }
            return this.httpsServerPort;
        }

        @Override
        public String getHttpsKeyStorePath() {
            return "/self-signed-certificate.jks";
        }

        @Override
        public String getHttpsKeyStoreType() {
            return "nope";
        }

        @Override
        public String getHttpsKeyStoreStorePass() {
            return "secret";
        }

        @Override
        public String getHttpsKeyStoreKeyPass() {
            return "secret";
        }
    }

}
