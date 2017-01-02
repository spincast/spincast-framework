package org.spincast.defaults.testing.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.defaults.testing.UnitTestDefaultContextsBase;
import org.spincast.plugins.routing.DefaultRouter;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

public class UnitTestCustomGuiceInjectorTest extends UnitTestDefaultContextsBase {

    @Inject
    protected DefaultRouter defaultRouter;

    @Inject
    protected @Named("testing") String testing;

    @Test
    public void test() throws Exception {
        assertNotNull(this.defaultRouter);
    }

    @Test
    public void testOverridingModule() throws Exception {
        assertNotNull(this.testing);
        assertEquals("42", this.testing);
    }

    @Override
    protected Injector createInjector() {
        return Spincast.configure()
                       .module(new SpincastGuiceModuleBase() {

                           @Override
                           protected void configure() {
                               bind(String.class).annotatedWith(Names.named("testing")).toInstance("42");
                           }
                       })
                       .init();
    }
}
