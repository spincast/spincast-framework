package org.spincast.defaults.tests;

import org.spincast.testing.core.SpincastGuiceModuleBasedTestBase;

import com.google.inject.Module;

public class DefaultTestingBase extends SpincastGuiceModuleBasedTestBase {

    @Override
    public Module getTestingModule() {
        return new DefaultTestingModule();
    }

}
