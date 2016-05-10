package org.spincast.plugins.dictionary;

import java.lang.reflect.Type;

import org.spincast.core.config.ISpincastDictionary;
import org.spincast.core.guice.SpincastPluginGuiceModuleBase;

import com.google.inject.Scopes;

/**
 * Guice module for the Spincast Dictionary plugin.
 */
public class SpincastDictionaryPluginGuiceModule extends SpincastPluginGuiceModuleBase {

    /**
     * Constructor.
     */
    public SpincastDictionaryPluginGuiceModule(Type requestContextType) {
        super(requestContextType);
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
