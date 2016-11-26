package org.spincast.core.websocket;

import org.spincast.core.json.JsonManager;
import org.spincast.core.locale.LocaleResolver;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.xml.XmlManager;

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
public class WebsocketContextBaseDeps<W extends WebsocketContext<?>> {

    private final LocaleResolver localeResolver;
    private final JsonManager jsonManager;
    private final XmlManager xmlManager;
    private final TemplatingEngine templatingEngine;
    private final Provider<Injector> injectorProvider;

    /**
     * Constructor
     */
    @Inject
    public WebsocketContextBaseDeps(LocaleResolver localeResolver,
                                    JsonManager jsonManager,
                                    XmlManager xmlManager,
                                    TemplatingEngine templatingEngine,
                                    Provider<Injector> injectorProvider) {
        this.localeResolver = localeResolver;
        this.jsonManager = jsonManager;
        this.xmlManager = xmlManager;
        this.templatingEngine = templatingEngine;
        this.injectorProvider = injectorProvider;
    }

    public LocaleResolver getLocaleResolver() {
        return this.localeResolver;
    }

    public JsonManager getJsonManager() {
        return this.jsonManager;
    }

    public XmlManager getXmlManager() {
        return this.xmlManager;
    }

    public TemplatingEngine getTemplatingEngine() {
        return this.templatingEngine;
    }

    public Provider<Injector> getInjectorProvider() {
        return this.injectorProvider;
    }

}
