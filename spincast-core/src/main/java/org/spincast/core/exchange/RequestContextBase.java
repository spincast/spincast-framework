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
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * The base implementation for a request context object.
 * 
 * We need to inject *providers* for the add-ons because they depend on the
 * request context this class build!
 * @see https://github.com/google/guice/wiki/CyclicDependencies#break-the-cycle-with-a-provider
 * 
 * Note that we're exceptionally not injecting the dependencies in
 * the <em>constructor</em> here, but using setters! This is because this class
 * will most likely be extended and we want to make the process easier,
 * with all those required providers...
 */
public abstract class RequestContextBase<R extends IRequestContext<R>> {

    protected final Logger logger = LoggerFactory.getLogger(RequestContextBase.class);

    private final Object exchange;
    private Provider<Injector> injectorProvider;
    private ILocaleResolver localeResolver;
    private IJsonManager jsonManager;
    private IXmlManager xmlManager;

    private Provider<ICookiesRequestContextAddon<R>> cookiesRequestContextAddonProvider;
    private Provider<IVariablesRequestContextAddon<R>> variablesRequestContextAddonProvider;
    private Provider<IRequestRequestContextAddon<R>> requestRequestContextAddonProvider;
    private Provider<IResponseRequestContextAddon<R>> responseRequestContextAddonProvider;
    private Provider<IRoutingRequestContextAddon<R>> routingRequestContextAddonProvider;
    private Provider<ITemplatingRequestContextAddon<R>> templatingRequestContextAddonProvider;

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
    @AssistedInject
    public RequestContextBase(@Assisted Object exchange) {
        this.exchange = exchange;
    }

    @Inject
    protected void setLocaleResolver(ILocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    @Inject
    protected void setJsonManager(IJsonManager jsonManager) {
        this.jsonManager = jsonManager;
    }

    @Inject
    protected void setXmlManager(IXmlManager xmlManager) {
        this.xmlManager = xmlManager;
    }

    @Inject
    protected void
            setCookiesRequestContextAddonProvider(Provider<ICookiesRequestContextAddon<R>> cookiesRequestContextAddonProvider) {
        this.cookiesRequestContextAddonProvider = cookiesRequestContextAddonProvider;
    }

    @Inject
    protected void
            setRequestRequestContextAddonProvider(Provider<IRequestRequestContextAddon<R>> requestRequestContextAddonProvider) {
        this.requestRequestContextAddonProvider = requestRequestContextAddonProvider;
    }

    @Inject
    protected void
            setRoutingRequestContextAddonProvider(Provider<IRoutingRequestContextAddon<R>> routingRequestContextAddonProvider) {
        this.routingRequestContextAddonProvider = routingRequestContextAddonProvider;
    }

    @Inject
    protected void
            setResponseRequestContextAddonProvider(Provider<IResponseRequestContextAddon<R>> responseRequestContextAddonProvider) {
        this.responseRequestContextAddonProvider = responseRequestContextAddonProvider;
    }

    @Inject
    protected void
            setVariablesRequestContextAddonProvider(Provider<IVariablesRequestContextAddon<R>> variablesRequestContextAddonProvider) {
        this.variablesRequestContextAddonProvider = variablesRequestContextAddonProvider;
    }

    @Inject
    protected void
            setTemplatingRequestContextAddonProvider(Provider<ITemplatingRequestContextAddon<R>> templatingRequestContextAddonProvider) {
        this.templatingRequestContextAddonProvider = templatingRequestContextAddonProvider;
    }

    @Inject
    protected void setInjectorProvider(Provider<Injector> injectorProvider) {
        this.injectorProvider = injectorProvider;
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
