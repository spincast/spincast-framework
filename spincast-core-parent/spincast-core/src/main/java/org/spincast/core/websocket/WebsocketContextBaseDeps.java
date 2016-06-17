package org.spincast.core.websocket;

import org.spincast.core.json.IJsonManager;
import org.spincast.core.locale.ILocaleResolver;
import org.spincast.core.templating.ITemplatingEngine;
import org.spincast.core.xml.IXmlManager;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

/**
 * A wrapper object for the dependencies required by WebsocketContextBase.
 * We inject this wrapper instead of injecting each individual dependency.
 * We do this because the WebsocketContextBase is made to be extended frequently
 * by developers and :
 * <ul>
 *     <li> 
 *     We want it to be easily extended without having to inject too many
 *     dependencies in the child class.
 *     </li>
 *     <li> 
 *     We want to keep using constructor injection instead of setter and field
 *     injection.
 *     </li>  
 *     <li> 
 *     By using a wrapper, we can add new dependencies to WebsocketContextBase
 *     without breaking the client classes.
 *     </li>  
 * </ul>
 */
public class WebsocketContextBaseDeps<W extends IWebsocketContext<?>> {

    private final ILocaleResolver localeResolver;
    private final IJsonManager jsonManager;
    private final IXmlManager xmlManager;
    private final ITemplatingEngine templatingEngine;
    private final Provider<Injector> injectorProvider;

    /**
     * Constructor
     */
    @Inject
    public WebsocketContextBaseDeps(ILocaleResolver localeResolver,
                                    IJsonManager jsonManager,
                                    IXmlManager xmlManager,
                                    ITemplatingEngine templatingEngine,
                                    Provider<Injector> injectorProvider) {
        this.localeResolver = localeResolver;
        this.jsonManager = jsonManager;
        this.xmlManager = xmlManager;
        this.templatingEngine = templatingEngine;
        this.injectorProvider = injectorProvider;
    }

    public ILocaleResolver getLocaleResolver() {
        return this.localeResolver;
    }

    public IJsonManager getJsonManager() {
        return this.jsonManager;
    }

    public IXmlManager getXmlManager() {
        return this.xmlManager;
    }

    public ITemplatingEngine getTemplatingEngine() {
        return this.templatingEngine;
    }

    public Provider<Injector> getInjectorProvider() {
        return this.injectorProvider;
    }

}
