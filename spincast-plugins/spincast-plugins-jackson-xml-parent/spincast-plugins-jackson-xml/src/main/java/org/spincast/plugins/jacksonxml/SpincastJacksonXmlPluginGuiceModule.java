package org.spincast.plugins.jacksonxml;

import java.lang.reflect.Type;

import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.xml.IXmlManager;

import com.google.inject.Scopes;

public class SpincastJacksonXmlPluginGuiceModule extends SpincastGuiceModuleBase {

    private final Type requestContextType;

    /**
     * Constructor.
     */
    public SpincastJacksonXmlPluginGuiceModule(Type requestContextType) {
        this.requestContextType = requestContextType;
    }

    @Override
    protected Type getRequestContextType() {
        return this.requestContextType;
    }

    @Override
    protected void configure() {
        bind(IXmlManager.class).to(SpincastXmlManager.class).in(Scopes.SINGLETON);
    }

}
