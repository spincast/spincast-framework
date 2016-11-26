package org.spincast.core.exchange;

import java.util.Locale;

import org.spincast.core.cookies.CookiesRequestContextAddon;
import org.spincast.core.json.JsonManager;
import org.spincast.core.routing.RoutingRequestContextAddon;
import org.spincast.core.templating.TemplatingRequestContextAddon;
import org.spincast.core.xml.XmlManager;

import com.google.inject.Injector;
import com.google.inject.Key;

/**
 * The base interface for a request context.
 * 
 * This declares the default <code>add-ons</code> and default
 * utility methods.
 */
public interface RequestContext<R extends RequestContext<?>> {

    /**
     * Request related methods.
     */
    public RequestRequestContextAddon<R> request();

    /**
     * Response related methods.
     */
    public ResponseRequestContextAddon<R> response();

    /**
     * Cookies related methods.
     */
    public CookiesRequestContextAddon<R> cookies();

    /**
     * Routing related methods.
     */
    public RoutingRequestContextAddon<R> routing();

    /**
     * Templating methods.
     */
    public TemplatingRequestContextAddon<R> templating();

    /**
     * Request scoped variables related methods.
     */
    public VariablesRequestContextAddon<R> variables();

    /**
     * Cache headers related methods.
     */
    public CacheHeadersRequestContextAddon<R> cacheHeaders();

    /**
     * Easy access to the <code>JsonManager</code>,
     * Json related methods.
     */
    public JsonManager json();

    /**
     * Easy access to the <code>XmlManager</code>,
     * XML related methods.
     */
    public XmlManager xml();

    /**
     * Easy access to the Guice context.
     */
    public Injector guice();

    /**
     * Shortcut to get an instance from Guice. Will
     * also cache the instance (as long as it is
     * request scoped or a singleton).
     */
    public <T> T get(Class<T> clazz);

    /**
     * Shortcut to get an instance from Guice. Will
     * also cache the instance (as long as it is
     * request scoped or a singleton)
     */
    public <T> T get(Key<T> key);

    /**
     * The best Locale to use, as resolved by 
     * the <code>LocaleResolver</code>.
     */
    public Locale getLocaleToUse();

    /**
     * The underlying exchange object, as given by the
     * HTTP server.
     * 
     * If you know for sure what its implementation is,
     * you may cast it to access extra functionalities
     * not provided by Spincast out of the box.
     */
    public Object exchange();

}
