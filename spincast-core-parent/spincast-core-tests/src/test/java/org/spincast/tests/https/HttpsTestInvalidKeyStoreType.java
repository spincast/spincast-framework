package org.spincast.tests.https;

import org.spincast.core.config.ISpincastConfig;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.testing.core.SpincastTestConfig;
import org.spincast.testing.core.utils.SpincastTestUtils;
import org.spincast.testing.utils.ExpectingBeforeClassException;

import com.google.inject.Module;

@ExpectingBeforeClassException
public class HttpsTestInvalidKeyStoreType extends SpincastDefaultNoAppIntegrationTestBase {

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
            return "/self-signed-certificate.jks";
        }

        @Override
        public String getHttpsKeyStoreType() {
            return "JKS";
        }

        @Override
        public String getHttpsKeyStoreStorePass() {
            return "nope";
        }

        @Override
        public String getHttpsKeyStoreKeypass() {
            return "secret";
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

}
