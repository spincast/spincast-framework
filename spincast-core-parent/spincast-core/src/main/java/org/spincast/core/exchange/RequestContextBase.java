package org.spincast.core.exchange;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.cookies.ICookiesRequestContextAddon;
import org.spincast.core.guice.SpincastGuiceScopes;
import org.spincast.core.guice.SpincastRequestScoped;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.locale.ILocaleResolver;
import org.spincast.core.routing.IRoutingRequestContextAddon;
import org.spincast.core.templating.ITemplatingRequestContextAddon;
import org.spincast.core.xml.IXmlManager;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.Singleton;

/**
 * The base implementation for a request context object.
 * 
 * We need to inject *providers* for the add-ons because they depend on the
 * request context this class build!
 * {@link https://github.com/google/guice/wiki/CyclicDependencies#break-the-cycle-with-a-provider}
 * 
 * Note that we're exceptionally not injecting the dependencies in
 * the <em>constructor</em> here, but using setters! This is because this class
 * will most likely be extended and we want to make the process easier,
 * with all those required providers...
 */
public abstract class RequestContextBase<R extends IRequestContext<R>> {

    protected final Logger logger = LoggerFactory.getLogger(RequestContextBase.class);

    private final Object exchange;
    private final Provider<Injector> injectorProvider;
    private final ILocaleResolver localeResolver;
    private final IJsonManager jsonManager;
    private final IXmlManager xmlManager;

    private final Provider<ICookiesRequestContextAddon<R>> cookiesRequestContextAddonProvider;
    private final Provider<IVariablesRequestContextAddon<R>> variablesRequestContextAddonProvider;
    private final Provider<IRequestRequestContextAddon<R>> requestRequestContextAddonProvider;
    private final Provider<IResponseRequestContextAddon<R>> responseRequestContextAddonProvider;
    private final Provider<IRoutingRequestContextAddon<R>> routingRequestContextAddonProvider;
    private final Provider<ITemplatingRequestContextAddon<R>> templatingRequestContextAddonProvider;

    private ICookiesRequestContextAddon<R> cookiesRequestContextAddon;
    private IVariablesRequestContextAddon<R> variablesRequestContextAddon;
    private IResponseRequestContextAddon<R> responseRequestContextAddon;
    private IRoutingRequestContextAddon<R> routingRequestContextAddon;
    private IRequestRequestContextAddon<R> requestRequestContextRequestAddon;
    private ITemplatingRequestContextAddon<R> templatingRequestContextAddon;

    private Map<Key<?>, Object> instanceFromGuiceCache;

    /**
     * Constructor
     */
    public RequestContextBase(Object exchange,
                              RequestContextBaseDeps<R> requestContextBaseDeps) {
        this.exchange = exchange;
        this.injectorProvider = requestContextBaseDeps.getInjectorProvider();
        this.localeResolver = requestContextBaseDeps.getLocaleResolver();
        this.jsonManager = requestContextBaseDeps.getJsonManager();
        this.xmlManager = requestContextBaseDeps.getXmlManager();
        this.cookiesRequestContextAddonProvider = requestContextBaseDeps.getCookiesRequestContextAddonProvider();
        this.variablesRequestContextAddonProvider = requestContextBaseDeps.getVariablesRequestContextAddonProvider();
        this.requestRequestContextAddonProvider = requestContextBaseDeps.getRequestRequestContextAddonProvider();
        this.responseRequestContextAddonProvider = requestContextBaseDeps.getResponseRequestContextAddonProvider();
        this.routingRequestContextAddonProvider = requestContextBaseDeps.getRoutingRequestContextAddonProvider();
        this.templatingRequestContextAddonProvider = requestContextBaseDeps.getTemplatingRequestContextAddonProvider();
    }

    public Injector guice() {
        return this.injectorProvider.get();
    }

    public Object exchange() {
        return this.exchange;
    }

    protected ILocaleResolver getLocaleResolver() {
        return this.localeResolver;
    }

    public Locale getLocaleToUse() {
        return getLocaleResolver().getLocaleToUse();
    }

    protected IJsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected IXmlManager getXmlManager() {
        return this.xmlManager;
    }

    protected Provider<ICookiesRequestContextAddon<R>> getCookiesRequestContextAddonProvider() {
        return this.cookiesRequestContextAddonProvider;
    }

    protected Provider<IRequestRequestContextAddon<R>> getRequestRequestContextAddonProvider() {
        return this.requestRequestContextAddonProvider;
    }

    protected Provider<IRoutingRequestContextAddon<R>> getRoutingRequestContextAddonProvider() {
        return this.routingRequestContextAddonProvider;
    }

    protected Provider<IResponseRequestContextAddon<R>> getResponseRequestContextAddonProvider() {
        return this.responseRequestContextAddonProvider;
    }

    protected Provider<IVariablesRequestContextAddon<R>> getVariablesRequestContextAddonProvider() {
        return this.variablesRequestContextAddonProvider;
    }

    protected Provider<ITemplatingRequestContextAddon<R>> getTemplatingRequestContextAddonProvider() {
        return this.templatingRequestContextAddonProvider;
    }

    protected Map<Key<?>, Object> getInstanceFromGuiceCache() {
        if(this.instanceFromGuiceCache == null) {
            this.instanceFromGuiceCache = new HashMap<Key<?>, Object>();
        }
        return this.instanceFromGuiceCache;
    }

    public ICookiesRequestContextAddon<R> cookies() {
        if(this.cookiesRequestContextAddon == null) {
            this.cookiesRequestContextAddon = getCookiesRequestContextAddonProvider().get();
        }
        return this.cookiesRequestContextAddon;
    }

    public IVariablesRequestContextAddon<R> variables() {
        if(this.variablesRequestContextAddon == null) {
            this.variablesRequestContextAddon = getVariablesRequestContextAddonProvider().get();
        }
        return this.variablesRequestContextAddon;
    }

    public IRequestRequestContextAddon<R> request() {
        if(this.requestRequestContextRequestAddon == null) {
            this.requestRequestContextRequestAddon = getRequestRequestContextAddonProvider().get();
        }
        return this.requestRequestContextRequestAddon;
    }

    public IResponseRequestContextAddon<R> response() {
        if(this.responseRequestContextAddon == null) {
            this.responseRequestContextAddon = getResponseRequestContextAddonProvider().get();
        }
        return this.responseRequestContextAddon;
    }

    public IRoutingRequestContextAddon<R> routing() {
        if(this.routingRequestContextAddon == null) {
            this.routingRequestContextAddon = getRoutingRequestContextAddonProvider().get();
        }
        return this.routingRequestContextAddon;
    }

    public ITemplatingRequestContextAddon<R> templating() {
        if(this.templatingRequestContextAddon == null) {
            this.templatingRequestContextAddon = getTemplatingRequestContextAddonProvider().get();
        }
        return this.templatingRequestContextAddon;
    }

    public IJsonManager json() {
        return getJsonManager();
    }

    public IXmlManager xml() {
        return getXmlManager();
    }

    public <T> T get(Class<T> clazz) {
        Objects.requireNonNull(clazz, "clazz can't be NULL");
        return get(Key.get(clazz));
    }

    public <T> T get(Key<T> key) {
        Objects.requireNonNull(key, "key can't be NULL");

        T obj = null;
        Map<Key<?>, Object> cache = getInstanceFromGuiceCache();
        if(!cache.containsKey(key)) {

            obj = guice().getInstance(key);

            //==========================================
            // We only cache the request scoped objects, and the 
            // singletons!
            //==========================================
            Binding<T> binding = guice().getBinding(key);
            if(Scopes.isScoped(binding, SpincastGuiceScopes.REQUEST, SpincastRequestScoped.class) ||
               Scopes.isScoped(binding, Scopes.SINGLETON, Singleton.class)) {
                cache.put(key, obj);
            }
        } else {
            @SuppressWarnings("unchecked")
            T asT = (T)cache.get(key);
            obj = asT;
        }
        return obj;
    }

}
