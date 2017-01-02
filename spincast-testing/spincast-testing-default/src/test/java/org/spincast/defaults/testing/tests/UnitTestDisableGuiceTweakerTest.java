package org.spincast.defaults.testing.tests;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.spincast.core.guice.GuiceTweaker;
import org.spincast.defaults.testing.UnitTestDefaultContextsBase;

public class UnitTestDisableGuiceTweakerTest extends UnitTestDefaultContextsBase {

    @Override
    protected boolean isEnableGuiceTweaker() {
        return false;
    }

    @Test
    public void test() throws Exception {
        // ok
    }

    /**
     * Shouldn't be used!
     */
    @Override
    protected GuiceTweaker createGuiceTweaker() {
        fail();
        return null;
    }

}
