package org.spincast.tests.bootstrapping;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.defaults.bootstrapping.Spincast;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class NoCurrentClassBindingTest {

    private static boolean initCalled = false;

    @Test
    public void test() throws Exception {
        NoCurrentClassBindingTest.main(null);
    }

    public static void main(String[] args) {

        Injector guice = Spincast.configure()
                                 .bindCurrentClass(false)
                                 .init(args);
        assertNotNull(guice);
        assertFalse(initCalled);

        SpincastUtils spincastUtils = guice.getInstance(SpincastUtils.class);
        assertNotNull(spincastUtils);
    }

    @Inject
    protected void init() {
        initCalled = true;
    }
}
