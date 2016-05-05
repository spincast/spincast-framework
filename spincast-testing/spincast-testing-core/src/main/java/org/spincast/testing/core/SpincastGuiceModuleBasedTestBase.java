package org.spincast.testing.core;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Base class for Spincast tests which creates
 * the Guice injector using a specified module.
 */
public abstract class SpincastGuiceModuleBasedTestBase extends SpincastGuiceBasedTestBase {

    @Override
    protected Injector createInjector() {
        return Guice.createInjector(getTestingModule());
    }

    /**
     * The testing module to use.
     */
    public abstract Module getTestingModule();

}
