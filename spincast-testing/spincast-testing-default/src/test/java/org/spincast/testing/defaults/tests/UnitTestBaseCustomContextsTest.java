package org.spincast.testing.defaults.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.routing.Router;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.testing.defaults.NoAppStartHttpServerCustomContextTypesTestingBase;
import org.spincast.testing.defaults.tests.utils.RequestContextTesting;
import org.spincast.testing.defaults.tests.utils.RequestContextTestingDefault;
import org.spincast.testing.defaults.tests.utils.WebsocketContextTesting;
import org.spincast.testing.defaults.tests.utils.WebsocketContextTestingDefault;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

/**
 * <strong>Note</strong> that, by default, all routes are cleared
 * before each test! Even routes added by plugins...
 * To change this behavior, override the
 * {@link NoAppStartHttpServerCustomContextTypesTestingBase#clearRoutes() clearRoutes()}
 * method.
 */
public class UnitTestBaseCustomContextsTest extends
                                            NoAppStartHttpServerCustomContextTypesTestingBase<RequestContextTesting, WebsocketContextTesting> {

    @Inject
    protected Router<RequestContextTesting, WebsocketContextTesting> customRouter;

    @Inject
    protected @Named("testing") String testing;

    @Override
    protected Module getExtraOverridingModule() {
        return new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(String.class).annotatedWith(Names.named("testing")).toInstance("42");
            }
        };
    }

    @Override
    protected Class<? extends RequestContext<?>> getRequestContextImplementationClass() {
        return RequestContextTestingDefault.class;
    }


    @Override
    protected Class<? extends WebsocketContext<?>> getWebsocketContextImplementationClass() {
        return WebsocketContextTestingDefault.class;
    }

    @Test
    public void test() throws Exception {
        assertNotNull(this.customRouter);
    }

    @Test
    public void testOverridingModule() throws Exception {
        assertNotNull(this.testing);
        assertEquals("42", this.testing);
    }

}
