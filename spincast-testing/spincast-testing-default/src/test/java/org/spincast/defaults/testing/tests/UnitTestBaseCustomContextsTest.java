package org.spincast.defaults.testing.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.routing.Router;
import org.spincast.defaults.testing.UnitTestBase;
import org.spincast.defaults.testing.tests.utils.RequestContextTesting;
import org.spincast.defaults.testing.tests.utils.RequestContextTestingDefault;
import org.spincast.defaults.testing.tests.utils.WebsocketContextTesting;
import org.spincast.defaults.testing.tests.utils.WebsocketContextTestingDefault;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

public class UnitTestBaseCustomContextsTest extends UnitTestBase {

    public UnitTestBaseCustomContextsTest() {
        super(RequestContextTestingDefault.class, WebsocketContextTestingDefault.class);
    }

    @Inject
    protected Router<RequestContextTesting, WebsocketContextTesting> customRouter;

    @Inject
    protected @Named("testing") String testing;

    @Test
    public void test() throws Exception {
        assertNotNull(this.customRouter);
    }

    @Test
    public void testOverridingModule() throws Exception {
        assertNotNull(this.testing);
        assertEquals("42", this.testing);
    }

    @Override
    protected Module getGuiceTweakerOverridingModule() {
        return new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(String.class).annotatedWith(Names.named("testing")).toInstance("42");
            }
        };
    }

}
