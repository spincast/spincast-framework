package org.spincast.core.exchange;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.guice.SpincastGuiceScopes;
import org.spincast.core.guice.SpincastRequestScoped;
import org.spincast.core.json.JsonManager;
import org.spincast.core.locale.LocaleResolver;
import org.spincast.core.routing.RoutingRequestContextAddon;
import org.spincast.core.templating.TemplatingRequestContextAddon;
import org.spincast.core.xml.XmlManager;

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
 */
public abstract class RequestContextBase<R extends RequestContext<R>> {

    protected final Logger logger = LoggerFactory.getLogger(RequestContextBase.class);

    private final Object exchange;
    private final Provider<Injector> injectorProvider;
    private final LocaleResolver localeResolver;
    private final JsonManager jsonManager;
    private final XmlManager xmlManager;

    private final Provider<VariablesRequestContextAddon<R>> variablesRequestContextAddonProvider;
    private final Provider<RequestRequestContextAddon<R>> requestRequestContextAddonProvider;
    private final Provider<ResponseRequestContextAddon<R>> responseRequestContextAddonProvider;
    private final Provider<RoutingRequestContextAddon<R>> routingRequestContextAddonProvider;
    private final Provider<TemplatingRequestContextAddon<R>> templatingRequestContextAddonProvider;
    private final Provider<CacheHeadersRequestContextAddon<R>> cacheHeadersRequestContextAddonProvider;

    private VariablesRequestContextAddon<R> variablesRequestContextAddon;
    private ResponseRequestContextAddon<R> responseRequestContextAddon;
    private RoutingRequestContextAddon<R> routingRequestContextAddon;
    private RequestRequestContextAddon<R> requestRequestContextRequestAddon;
    private TemplatingRequestContextAddon<R> templatingRequestContextAddon;
    private CacheHeadersRequestContextAddon<R> cacheHeadersRequestContextAddon;

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
        this.variablesRequestContextAddonProvider = requestContextBaseDeps.getVariablesRequestContextAddonProvider();
        this.requestRequestContextAddonProvider = requestContextBaseDeps.getRequestRequestContextAddonProvider();
        this.responseRequestContextAddonProvider = requestContextBaseDeps.getResponseRequestContextAddonProvider();
        this.routingRequestContextAddonProvider = requestContextBaseDeps.getRoutingRequestContextAddonProvider();
        this.templatingRequestContextAddonProvider = requestContextBaseDeps.getTemplatingRequestContextAddonProvider();
        this.cacheHeadersRequestContextAddonProvider = requestContextBaseDeps.getCacheHeadersRequestContextAddonProvider();
    }

    public Injector guice() {
        return this.injectorProvider.get();
    }

    public Object exchange() {
        return this.exchange;
    }

    protected LocaleResolver getLocaleResolver() {
        return this.localeResolver;
    }

    public Locale getLocaleToUse() {
        return getLocaleResolver().getLocaleToUse();
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected XmlManager getXmlManager() {
        return this.xmlManager;
    }

    protected Provider<RequestRequestContextAddon<R>> getRequestRequestContextAddonProvider() {
        return this.requestRequestContextAddonProvider;
    }

    protected Provider<RoutingRequestContextAddon<R>> getRoutingRequestContextAddonProvider() {
        return this.routingRequestContextAddonProvider;
    }

    protected Provider<ResponseRequestContextAddon<R>> getResponseRequestContextAddonProvider() {
        return this.responseRequestContextAddonProvider;
    }

    protected Provider<VariablesRequestContextAddon<R>> getVariablesRequestContextAddonProvider() {
        return this.variablesRequestContextAddonProvider;
    }

    protected Provider<TemplatingRequestContextAddon<R>> getTemplatingRequestContextAddonProvider() {
        return this.templatingRequestContextAddonProvider;
    }

    protected Provider<CacheHeadersRequestContextAddon<R>> getCacheHeadersRequestContextAddonProvider() {
        return this.cacheHeadersRequestContextAddonProvider;
    }

    protected Map<Key<?>, Object> getInstanceFromGuiceCache() {
        if (this.instanceFromGuiceCache == null) {
            this.instanceFromGuiceCache = new HashMap<Key<?>, Object>();
        }
        return this.instanceFromGuiceCache;
    }

    public VariablesRequestContextAddon<R> variables() {
        if (this.variablesRequestContextAddon == null) {
            this.variablesRequestContextAddon = getVariablesRequestContextAddonProvider().get();
        }
        return this.variablesRequestContextAddon;
    }

    public RequestRequestContextAddon<R> request() {
        if (this.requestRequestContextRequestAddon == null) {
            this.requestRequestContextRequestAddon = getRequestRequestContextAddonProvider().get();
        }
        return this.requestRequestContextRequestAddon;
    }

    public ResponseRequestContextAddon<R> response() {
        if (this.responseRequestContextAddon == null) {
            this.responseRequestContextAddon = getResponseRequestContextAddonProvider().get();
        }
        return this.responseRequestContextAddon;
    }

    public RoutingRequestContextAddon<R> routing() {
        if (this.routingRequestContextAddon == null) {
            this.routingRequestContextAddon = getRoutingRequestContextAddonProvider().get();
        }
        return this.routingRequestContextAddon;
    }

    public TemplatingRequestContextAddon<R> templating() {
        if (this.templatingRequestContextAddon == null) {
            this.templatingRequestContextAddon = getTemplatingRequestContextAddonProvider().get();
        }
        return this.templatingRequestContextAddon;
    }

    public CacheHeadersRequestContextAddon<R> cacheHeaders() {
        if (this.cacheHeadersRequestContextAddon == null) {
            this.cacheHeadersRequestContextAddon = getCacheHeadersRequestContextAddonProvider().get();
        }
        return this.cacheHeadersRequestContextAddon;
    }

    public JsonManager json() {
        return getJsonManager();
    }

    public XmlManager xml() {
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
        if (!cache.containsKey(key)) {

            obj = guice().getInstance(key);

            //==========================================
            // We only cache the request scoped objects, and the 
            // singletons!
            //==========================================
            Binding<T> binding = guice().getBinding(key);
            if (Scopes.isScoped(binding, SpincastGuiceScopes.REQUEST, SpincastRequestScoped.class) ||
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

    @Override
    public String toString() {

        String msg = "[" + request().getHttpMethod() + "] " + request().getFullUrl();
        if (routing().isForwarded()) {
            msg += " ( forwarded from : " + request().getFullUrlOriginal() + " )";
        }

        return msg;
    }

}
