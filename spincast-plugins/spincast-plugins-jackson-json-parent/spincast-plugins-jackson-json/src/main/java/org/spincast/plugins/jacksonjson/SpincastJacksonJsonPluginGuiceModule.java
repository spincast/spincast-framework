package org.spincast.plugins.jacksonjson;

import java.lang.reflect.Type;

import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.json.IJsonManager;

import com.google.inject.Scopes;

public class SpincastJacksonJsonPluginGuiceModule extends SpincastGuiceModuleBase {

    private final Type requestContextType;

    /**
     * Constructor.
     */
    public SpincastJacksonJsonPluginGuiceModule(Type requestContextType) {
        this.requestContextType = requestContextType;
    }

    @Override
    protected Type getRequestContextType() {
        return this.requestContextType;
    }

    @Override
    protected void configure() {

        bind(IJsonManager.class).to(SpincastJsonManager.class).in(Scopes.SINGLETON);
    }

}
