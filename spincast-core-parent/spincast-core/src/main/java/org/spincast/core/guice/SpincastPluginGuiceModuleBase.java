package org.spincast.core.guice;

import java.lang.reflect.Type;

/**
 * Base class for Spincast plugins' Guice modules.
 */
public abstract class SpincastPluginGuiceModuleBase extends SpincastGuiceModuleBase {

    private final Type requestContextType;

    /**
     * Constructor.
     */
    public SpincastPluginGuiceModuleBase(Type requestContextType) {
        this.requestContextType = requestContextType;
    }

    @Override
    protected Type getRequestContextType() {
        return this.requestContextType;
    }
}
