package org.spincast.plugins.dictionary;

import java.lang.reflect.Type;

import org.spincast.core.config.SpincastDictionary;
import org.spincast.core.guice.SpincastPluginGuiceModuleBase;

import com.google.inject.Scopes;

/**
 * Guice module for the Spincast Dictionary plugin.
 */
public class SpincastDictionaryPluginGuiceModule extends SpincastPluginGuiceModuleBase {

    /**
     * Constructor.
     */
    public SpincastDictionaryPluginGuiceModule(Type requestContextType, Type websocketContextType) {
        super(requestContextType, websocketContextType);
    }

    @Override
    protected void configure() {
        bindSpincastDictionary();
    }

    protected void bindSpincastDictionary() {
        bind(SpincastDictionary.class).to(bindSpincastDictionaryImplClass()).in(Scopes.SINGLETON);
    }

    protected Class<? extends SpincastDictionary> bindSpincastDictionaryImplClass() {
        return SpincastDictionaryDefault.class;
    }

}
