package org.spincast.core.guice;

import java.lang.reflect.Type;

/**
 * Base class for Spincast plugins' Guice modules.
 */
public abstract class SpincastPluginGuiceModuleBase extends SpincastGuiceModuleBase {

    private final Type requestContextType;
    private final Type websocketContextType;

    /**
     * Constructor.
     */
    public SpincastPluginGuiceModuleBase(Type requestContextType,
                                         Type websocketContextType) {
        this.requestContextType = requestContextType;
        this.websocketContextType = websocketContextType;
    }

    @Override
    protected Type getRequestContextType() {
        return this.requestContextType;
    }

    @Override
    protected Type getWebsocketContextType() {
        return this.websocketContextType;
    }
}
