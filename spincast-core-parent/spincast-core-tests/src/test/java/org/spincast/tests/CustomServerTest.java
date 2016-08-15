package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.IHandler;
import org.spincast.core.server.IServer;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.tests.varia.CustomExchange;
import org.spincast.tests.varia.CustomServer;

import com.google.inject.Module;
import com.google.inject.Scopes;

/**
 * Creation of a custom server.
 */
public class CustomServerTest extends SpincastDefaultNoAppIntegrationTestBase {

    //==========================================
    // Remove all routes, even Spincast ones
    //==========================================
    @Override
    protected boolean removeSpincastRoutesToo() {
        return true;
    }

    protected static String testFlag = "";

    @Override
    public void afterTest() {
        super.afterTest();
        testFlag = "";
    }

    @Override
    public Module getTestingModule() {
        return new SpincastDefaultTestingModule() {

            @Override
            protected void configure() {
                super.configure();
            }

            //==========================================
            // Bind the custom server
            //==========================================
            @Override
            protected void bindServerPlugin() {

                bind(IServer.class).to(CustomServer.class).in(Scopes.SINGLETON);
            }
        };
    }

    protected CustomServer getCustomServer() {
        return (CustomServer)getServer();
    }

    @Test
    public void test1() throws Exception {
        assertEquals("constructorstart", getCustomServer().serverFlag);
    }

    @Test
    public void test2() throws Exception {
        assertEquals("constructorstart", getCustomServer().serverFlag);

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                assertTrue(context.exchange() instanceof CustomExchange);
                testFlag = "one";
            }
        });

        IServer serverRaw = getInjector().getInstance(IServer.class);
        assertNotNull(serverRaw);
        assertTrue(serverRaw instanceof CustomServer);

        CustomServer server = (CustomServer)serverRaw;

        CustomExchange exchange = new CustomExchange();
        exchange.httpMethod = HttpMethod.GET;
        exchange.fullUrl = "http://localhost/one";
        exchange.contentType = ContentTypeDefaults.TEXT.getMainVariation();

        // Simule a request reaching the server...
        server.handle(exchange);
        assertEquals("one", testFlag);
    }

    @Override
    public void afterClass() {
        super.afterClass();
        assertEquals("constructorstartstop", getCustomServer().serverFlag);
    }

}
