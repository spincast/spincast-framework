package org.spincast.core.exchange;

import org.spincast.core.cookies.ICookiesRequestContextAddon;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.locale.ILocaleResolver;
import org.spincast.core.routing.IRoutingRequestContextAddon;
import org.spincast.core.templating.ITemplatingRequestContextAddon;
import org.spincast.core.xml.IXmlManager;

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
public class RequestContextBaseDeps<R extends IRequestContext<R>> {

    private final ILocaleResolver localeResolver;
    private final IJsonManager jsonManager;
    private final IXmlManager xmlManager;
    private final Provider<ICookiesRequestContextAddon<R>> cookiesRequestContextAddonProvider;
    private final Provider<IRequestRequestContextAddon<R>> requestRequestContextAddonProvider;
    private final Provider<IRoutingRequestContextAddon<R>> routingRequestContextAddonProvider;
    private final Provider<IResponseRequestContextAddon<R>> responseRequestContextAddonProvider;
    private final Provider<IVariablesRequestContextAddon<R>> variablesRequestContextAddonProvider;
    private final Provider<ITemplatingRequestContextAddon<R>> templatingRequestContextAddonProvider;
    private final Provider<Injector> injectorProvider;

    /**
     * Constructor
     */
    @Inject
    public RequestContextBaseDeps(ILocaleResolver localeResolver, IJsonManager jsonManager, IXmlManager xmlManager,
                                  Provider<ICookiesRequestContextAddon<R>> cookiesRequestContextAddonProvider,
                                  Provider<IRequestRequestContextAddon<R>> requestRequestContextAddonProvider,
                                  Provider<IRoutingRequestContextAddon<R>> routingRequestContextAddonProvider,
                                  Provider<IResponseRequestContextAddon<R>> responseRequestContextAddonProvider,
                                  Provider<IVariablesRequestContextAddon<R>> variablesRequestContextAddonProvider,
                                  Provider<ITemplatingRequestContextAddon<R>> templatingRequestContextAddonProvider,
                                  Provider<Injector> injectorProvider) {

        this.localeResolver = localeResolver;
        this.jsonManager = jsonManager;
        this.xmlManager = xmlManager;
        this.cookiesRequestContextAddonProvider = cookiesRequestContextAddonProvider;
        this.requestRequestContextAddonProvider = requestRequestContextAddonProvider;
        this.routingRequestContextAddonProvider = routingRequestContextAddonProvider;
        this.responseRequestContextAddonProvider = responseRequestContextAddonProvider;
        this.variablesRequestContextAddonProvider = variablesRequestContextAddonProvider;
        this.templatingRequestContextAddonProvider = templatingRequestContextAddonProvider;
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

    public Provider<ICookiesRequestContextAddon<R>> getCookiesRequestContextAddonProvider() {
        return this.cookiesRequestContextAddonProvider;
    }

    public Provider<IRequestRequestContextAddon<R>> getRequestRequestContextAddonProvider() {
        return this.requestRequestContextAddonProvider;
    }

    public Provider<IRoutingRequestContextAddon<R>> getRoutingRequestContextAddonProvider() {
        return this.routingRequestContextAddonProvider;
    }

    public Provider<IResponseRequestContextAddon<R>> getResponseRequestContextAddonProvider() {
        return this.responseRequestContextAddonProvider;
    }

    public Provider<IVariablesRequestContextAddon<R>> getVariablesRequestContextAddonProvider() {
        return this.variablesRequestContextAddonProvider;
    }

    public Provider<ITemplatingRequestContextAddon<R>> getTemplatingRequestContextAddonProvider() {
        return this.templatingRequestContextAddonProvider;
    }

    public Provider<Injector> getInjectorProvider() {
        return this.injectorProvider;
    }

}
