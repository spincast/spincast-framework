package org.spincast.plugins.undertow;

import java.lang.reflect.Type;

import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.server.IServer;

import com.google.inject.Scopes;

public class SpincastUndertowPluginGuiceModule extends SpincastGuiceModuleBase {

    private final Type requestContextType;

    /**
     * Constructor.
     */
    public SpincastUndertowPluginGuiceModule(Type requestContextType) {
        this.requestContextType = requestContextType;
    }

    @Override
    protected Type getRequestContextType() {
        return this.requestContextType;
    }

    @Override
    protected void configure() {

        bind(IServer.class).to(SpincastUndertowServer.class).in(Scopes.SINGLETON);

        bind(ISSLContextManager.class).to(SSLContextManager.class).in(Scopes.SINGLETON);

    }

}
