package org.spincast.defaults.testing.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.defaults.testing.UnitTestDefaultContextsBase;
import org.spincast.testing.core.SpincastConfigTesting;
import org.spincast.testing.core.utils.SpincastConfigTestingDefault;

public class UnitTestCustomConfigClassTest extends UnitTestDefaultContextsBase {

    /**
     * Custom Spincast Config class
     */
    public static class SpincastConfigCustom extends SpincastConfigTestingDefault
                                             implements SpincastConfigTesting {

        @Override
        public int getHttpServerPort() {
            return 42;
        }
    }

    @Override
    protected Class<? extends SpincastConfigTesting> getSpincastConfigTestingImplementation() {
        return SpincastConfigCustom.class;
    }

    @Test
    public void test() throws Exception {
        assertEquals(42, getSpincastConfig().getHttpServerPort());
    }
}
