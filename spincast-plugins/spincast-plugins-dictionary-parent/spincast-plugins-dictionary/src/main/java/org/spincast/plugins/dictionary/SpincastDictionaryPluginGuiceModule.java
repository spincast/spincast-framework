package org.spincast.plugins.dictionary;

import java.lang.reflect.Type;

import org.spincast.core.config.ISpincastDictionary;
import org.spincast.core.guice.SpincastGuiceModuleBase;

import com.google.inject.Scopes;

/**
 * Guice module for the Spincast Dictionary plugin.
 */
public class SpincastDictionaryPluginGuiceModule extends SpincastGuiceModuleBase {

    private final Type requestContextType;

    /**
     * Constructor.
     */
    public SpincastDictionaryPluginGuiceModule(Type requestContextType) {
        this.requestContextType = requestContextType;
    }

    @Override
    protected Type getRequestContextType() {
        return this.requestContextType;
    }

    @Override
    protected void configure() {
        bindSpincastDictionary();
    }

    protected void bindSpincastDictionary() {
        bind(ISpincastDictionary.class).to(bindSpincastDictionaryImplClass()).in(Scopes.SINGLETON);
    }

    protected Class<? extends ISpincastDictionary> bindSpincastDictionaryImplClass() {
        return SpincastDictionary.class;
    }

}
