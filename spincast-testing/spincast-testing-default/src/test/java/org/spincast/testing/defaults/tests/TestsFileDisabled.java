package org.spincast.testing.defaults.tests;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.spincast.testing.defaults.NoAppTestingBase;

public class TestsFileDisabled extends NoAppTestingBase {

    @Override
    public boolean isTestClassDisabledPreBeforeClass() {
        return true;
    }

    @Test
    public void test() throws Exception {
        fail();
    }
}
