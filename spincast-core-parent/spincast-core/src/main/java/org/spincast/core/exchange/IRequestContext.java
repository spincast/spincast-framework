package org.spincast.core.exchange;

import java.util.Locale;

import org.spincast.core.cookies.ICookiesRequestContextAddon;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.routing.IRoutingRequestContextAddon;
import org.spincast.core.templating.ITemplatingRequestContextAddon;
import org.spincast.core.xml.IXmlManager;

import com.google.inject.Injector;
import com.google.inject.Key;

/**
 * The base interface for a request context.
 * 
 * This declares the default <code>add-ons</code> and default
 * utility methods.
 */
public interface IRequestContext<R extends IRequestContext<?>> {

    /**
     * Request related methods.
     */
    public IRequestRequestContextAddon<R> request();

    /**
     * Response related methods.
     */
    public IResponseRequestContextAddon<R> response();

    /**
     * Cookies related methods.
     */
    public ICookiesRequestContextAddon<R> cookies();

    /**
     * Routing related methods.
     */
    public IRoutingRequestContextAddon<R> routing();

    /**
     * Templating methods.
     */
    public ITemplatingRequestContextAddon<R> templating();

    /**
     * Request scoped variables related methods.
     */
    public IVariablesRequestContextAddon<R> variables();

    /**
     * Cache headers related methods.
     */
    public ICacheHeadersRequestContextAddon<R> cacheHeaders();

    /**
     * Easy access to the <code>IJsonManager</code>,
     * Json related methods.
     */
    public IJsonManager json();

    /**
     * Easy access to the <code>IXmlManager</code>,
     * XML related methods.
     */
    public IXmlManager xml();

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
