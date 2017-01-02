package org.spincast.defaults.testing.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.defaults.testing.UnitTestDefaultContextsBase;
import org.spincast.plugins.routing.DefaultRouter;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

public class UnitTestDefaultContextsBaseTest extends UnitTestDefaultContextsBase {

    @Override
    protected Module getExtraOverridingModule() {
        return new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(String.class).annotatedWith(Names.named("testing")).toInstance("42");
            }
        };
    }

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

    @Test
    public void testTestingConfigHMecanismInOn() throws Exception {
        assertNotNull(getSpincastConfig());
        assertNotEquals(44419, getSpincastConfig().getHttpServerPort());
    }

}
