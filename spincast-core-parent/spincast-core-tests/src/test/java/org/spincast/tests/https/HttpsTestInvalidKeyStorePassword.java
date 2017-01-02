package org.spincast.tests.https;

import org.spincast.defaults.testing.IntegrationTestNoAppDefaultContextsBase;
import org.spincast.testing.core.SpincastConfigTesting;
import org.spincast.testing.core.utils.SpincastConfigTestingDefault;
import org.spincast.testing.core.utils.SpincastTestUtils;
import org.spincast.testing.utils.ExpectingBeforeClassException;

@ExpectingBeforeClassException
public class HttpsTestInvalidKeyStorePassword extends IntegrationTestNoAppDefaultContextsBase {

    @Override
    protected Class<? extends SpincastConfigTesting> getSpincastConfigTestingImplementation() {
        return HttpsTestConfig.class;
    }

    protected static class HttpsTestConfig extends SpincastConfigTestingDefault {

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
        public String getHttpsKeyStoreKeypass() {
            return "secret";
        }
    }

}
