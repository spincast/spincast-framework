package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.server.Server;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.tests.varia.CustomServer;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

public class PluginNewServerTest {

    /**
     * The "plugin", which is a module declaring implementations
     * for a custom server.
     */
    protected class CustomServerModule extends AbstractModule {

        @Override
        protected void configure() {
            bind(Server.class).to(CustomServer.class).in(Scopes.SINGLETON);
        }
    }

    /**
     * In this test, we use the default Spincast module,
     * which binds a default server, but we replace the
     * server using a plugin (another module).
     */
    @Test
    public void testCustomServerPlugin() throws Exception {

        Injector guice = Guice.createInjector(Modules.override(Spincast.getDefaultModule(true))
                                                     .with(new CustomServerModule()));

        Server serverRaw = guice.getInstance(Server.class);
        assertNotNull(serverRaw);
        assertTrue(serverRaw instanceof CustomServer);

        CustomServer server = (CustomServer)serverRaw;
        server.start();

        assertEquals("constructorstart", server.serverFlag);

        server.stop();

        assertEquals("constructorstartstop", server.serverFlag);
    }

}
