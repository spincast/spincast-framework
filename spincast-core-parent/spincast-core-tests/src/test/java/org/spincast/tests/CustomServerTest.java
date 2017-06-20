package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.server.Server;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.defaults.testing.IntegrationTestNoAppDefaultContextsBase;
import org.spincast.tests.varia.CustomExchange;
import org.spincast.tests.varia.CustomServer;

import com.google.inject.Injector;
import com.google.inject.Scopes;

/**
 * Creation of a custom server.
 */
public class CustomServerTest extends IntegrationTestNoAppDefaultContextsBase {

    @Override
    protected Injector createInjector() {

        return Spincast.configure()
                       .disableDefaultServerPlugin()
                       .module(new SpincastGuiceModuleBase() {

                           @Override
                           protected void configure() {
                               bind(Server.class).to(CustomServer.class).in(Scopes.SINGLETON);
                           }
                       })
                       .init(new String[]{});
    }

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

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                assertTrue(context.exchange() instanceof CustomExchange);
                testFlag = "one";
            }
        });

        Server serverRaw = getInjector().getInstance(Server.class);
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
