package org.spincast.demos.sum;

import org.spincast.core.guice.SpincastGuiceModuleBase;

import com.google.inject.Scopes;

public class AppModule extends SpincastGuiceModuleBase {

    @Override
    protected void configure() {
        bind(AppController.class).to(AppControllerDefault.class).in(Scopes.SINGLETON);
    }

}
