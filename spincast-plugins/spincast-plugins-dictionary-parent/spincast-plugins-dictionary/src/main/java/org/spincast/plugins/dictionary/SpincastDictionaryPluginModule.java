package org.spincast.plugins.dictionary;

import org.spincast.core.config.SpincastDictionary;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.websocket.WebsocketContext;

import com.google.inject.Scopes;

/**
 * Spincast Dictionary plugin module.
 */
public class SpincastDictionaryPluginModule extends SpincastGuiceModuleBase {

    private Class<? extends SpincastDictionary> specificSpincastDictionaryImplClass;

    public SpincastDictionaryPluginModule() {
        this(null, null, null);
    }

    public SpincastDictionaryPluginModule(Class<? extends SpincastDictionary> specificSpincastDictionaryImplClass) {
        this(null, null, specificSpincastDictionaryImplClass);
    }

    public SpincastDictionaryPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                          Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        this(requestContextImplementationClass, websocketContextImplementationClass, null);
    }

    /**
     * Constructor
     * 
     * @param specificSpincastConfigImplClass the specific
     * configuration implementation class to use.
     */
    public SpincastDictionaryPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                          Class<? extends WebsocketContext<?>> websocketContextImplementationClass,
                                          Class<? extends SpincastDictionary> specificSpincastDictionaryImplClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
        this.specificSpincastDictionaryImplClass = specificSpincastDictionaryImplClass;
    }

    protected Class<? extends SpincastDictionary> getSpecificSpincastDictionaryImplClass() {
        return this.specificSpincastDictionaryImplClass;
    }

    @Override
    protected void configure() {
        bindSpincastDictionary();
    }

    protected void bindSpincastDictionary() {
        bind(SpincastDictionary.class).to(getSpincastDictionaryImplClass()).in(Scopes.SINGLETON);
    }

    protected Class<? extends SpincastDictionary> getSpincastDictionaryImplClass() {

        if(getSpecificSpincastDictionaryImplClass() != null) {
            return getSpecificSpincastDictionaryImplClass();
        }

        return getDefaultSpincastDictionaryImplClass();
    }

    protected Class<? extends SpincastDictionary> getDefaultSpincastDictionaryImplClass() {
        return SpincastDictionaryDefault.class;
    }

}
