package org.spincast.core.exchange;

import org.spincast.core.json.JsonManager;
import org.spincast.core.locale.LocaleResolver;
import org.spincast.core.routing.RoutingRequestContextAddon;
import org.spincast.core.templating.TemplatingRequestContextAddon;
import org.spincast.core.xml.XmlManager;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

/**
 * A wrapper object for the dependencies required by RequestContextBase.
 * We inject this wrapper instead of injecting each individual dependency.
 * We do this because the RequestContextBase is made to be extended frequently
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
 *     By using a wrapper, we can add new dependencies to RequestContextBase
 *     without breaking the client classes.
 *     </li>  
 * </ul>
 */
public class RequestContextBaseDeps<R extends RequestContext<R>> {

    private final LocaleResolver localeResolver;
    private final JsonManager jsonManager;
    private final XmlManager xmlManager;
    private final Provider<RequestRequestContextAddon<R>> requestRequestContextAddonProvider;
    private final Provider<RoutingRequestContextAddon<R>> routingRequestContextAddonProvider;
    private final Provider<ResponseRequestContextAddon<R>> responseRequestContextAddonProvider;
    private final Provider<VariablesRequestContextAddon<R>> variablesRequestContextAddonProvider;
    private final Provider<TemplatingRequestContextAddon<R>> templatingRequestContextAddonProvider;
    private final Provider<CacheHeadersRequestContextAddon<R>> cacheHeadersRequestContextAddonProvider;
    private final Provider<Injector> injectorProvider;

    /**
     * Constructor
     */
    @Inject
    public RequestContextBaseDeps(LocaleResolver localeResolver, JsonManager jsonManager, XmlManager xmlManager,
                                  Provider<RequestRequestContextAddon<R>> requestRequestContextAddonProvider,
                                  Provider<RoutingRequestContextAddon<R>> routingRequestContextAddonProvider,
                                  Provider<ResponseRequestContextAddon<R>> responseRequestContextAddonProvider,
                                  Provider<VariablesRequestContextAddon<R>> variablesRequestContextAddonProvider,
                                  Provider<TemplatingRequestContextAddon<R>> templatingRequestContextAddonProvider,
                                  Provider<CacheHeadersRequestContextAddon<R>> cacheHeadersRequestContextAddonProvider,
                                  Provider<Injector> injectorProvider) {

        this.localeResolver = localeResolver;
        this.jsonManager = jsonManager;
        this.xmlManager = xmlManager;
        this.requestRequestContextAddonProvider = requestRequestContextAddonProvider;
        this.routingRequestContextAddonProvider = routingRequestContextAddonProvider;
        this.responseRequestContextAddonProvider = responseRequestContextAddonProvider;
        this.variablesRequestContextAddonProvider = variablesRequestContextAddonProvider;
        this.templatingRequestContextAddonProvider = templatingRequestContextAddonProvider;
        this.cacheHeadersRequestContextAddonProvider = cacheHeadersRequestContextAddonProvider;
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

    public Provider<RequestRequestContextAddon<R>> getRequestRequestContextAddonProvider() {
        return this.requestRequestContextAddonProvider;
    }

    public Provider<RoutingRequestContextAddon<R>> getRoutingRequestContextAddonProvider() {
        return this.routingRequestContextAddonProvider;
    }

    public Provider<ResponseRequestContextAddon<R>> getResponseRequestContextAddonProvider() {
        return this.responseRequestContextAddonProvider;
    }

    public Provider<VariablesRequestContextAddon<R>> getVariablesRequestContextAddonProvider() {
        return this.variablesRequestContextAddonProvider;
    }

    public Provider<TemplatingRequestContextAddon<R>> getTemplatingRequestContextAddonProvider() {
        return this.templatingRequestContextAddonProvider;
    }

    public Provider<CacheHeadersRequestContextAddon<R>> getCacheHeadersRequestContextAddonProvider() {
        return this.cacheHeadersRequestContextAddonProvider;
    }

    public Provider<Injector> getInjectorProvider() {
        return this.injectorProvider;
    }

}
