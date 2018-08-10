package org.spincast.plugins.dictionary;

import org.spincast.core.dictionary.Dictionary;
import org.spincast.core.dictionary.DictionaryEntries;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.websocket.WebsocketContext;

import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;

/**
 * Spincast Dictionary plugin module.
 */
public class SpincastDictionaryPluginModule extends SpincastGuiceModuleBase {

    private Class<? extends Dictionary> specificDictionaryImplClass;

    public SpincastDictionaryPluginModule() {
        this(null, null, null);
    }

    public SpincastDictionaryPluginModule(Class<? extends Dictionary> dictionaryImplClass) {
        this(null, null, dictionaryImplClass);
    }

    public SpincastDictionaryPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                          Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        this(requestContextImplementationClass, websocketContextImplementationClass, null);
    }

    /**
     * Constructor
     * 
     * @param dictionaryImplClass the specific
     * dictionary implementation class to use.
     */
    public SpincastDictionaryPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                          Class<? extends WebsocketContext<?>> websocketContextImplementationClass,
                                          Class<? extends Dictionary> specificDictionaryImplClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
        this.specificDictionaryImplClass = specificDictionaryImplClass;
    }

    protected Class<? extends Dictionary> getSpecificDictionaryImplClass() {
        return this.specificDictionaryImplClass;
    }

    @Override
    protected void configure() {
        bindSpincastDictionary();

        //==========================================
        // Declare the DictionaryEntries multibinder
        // so plugins can add messages to the Dictionary!
        //==========================================
        Multibinder.newSetBinder(binder(), DictionaryEntries.class);
    }

    protected void bindSpincastDictionary() {
        bind(Dictionary.class).to(getDictionaryImplClass()).in(Scopes.SINGLETON);
    }

    protected Class<? extends Dictionary> getDictionaryImplClass() {

        if (getSpecificDictionaryImplClass() != null) {
            return getSpecificDictionaryImplClass();
        }

        return getDefaultDictionaryImplClass();
    }

    protected Class<? extends Dictionary> getDefaultDictionaryImplClass() {
        return SpincastDictionaryDefault.class;
    }

}
