package org.spincast.defaults.tests;

import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.testing.core.SpincastGuiceModuleBasedIntegrationTestBase;

import com.google.inject.Module;

public class DefaultIntegrationTestingBase extends SpincastGuiceModuleBasedIntegrationTestBase<IDefaultRequestContext> {

    @Override
    public Module getTestingModule() {
        return new DefaultTestingModule();
    }
}
