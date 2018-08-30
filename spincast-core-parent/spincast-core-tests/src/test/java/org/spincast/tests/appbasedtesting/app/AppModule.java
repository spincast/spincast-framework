package org.spincast.tests.appbasedtesting.app;

import org.spincast.core.guice.SpincastGuiceModuleBase;

import com.google.inject.Scopes;

public class AppModule extends SpincastGuiceModuleBase {

    @Override
    protected void configure() {
        bind(AppConfigs.class).to(AppConfigDefault.class).in(Scopes.SINGLETON);
    }



}
