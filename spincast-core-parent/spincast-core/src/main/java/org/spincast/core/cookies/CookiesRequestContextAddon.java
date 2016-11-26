package org.spincast.core.cookies;

import java.util.Date;
import java.util.Map;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.RequestContext;

/**
 * Request context add-on to work with cookies.
 */
public interface CookiesRequestContextAddon<R extends RequestContext<?>> {

    /**
     * Gets the current cookies in a Map, using their names as the keys.
     */
    public Map<String, Cookie> getCookies();

    /**
     * Gets a cookie.
     * 
     * @return the cookie or <code>null</code> if not found.
     */
    public Cookie getCookie(String name);

    /**
     * Adds a cookie.
     */
    public void addCookie(Cookie cookie);

    /**
     * Creates a cookie. You then need to add it using
     * {@link #addCookie(Cookie)} if you want to
     * send it.
     * <p>
     * By default, the public host ({@link SpincastConfig#getPublicServerHost()}) 
     * is uses as the cookie's <code>domain</code>
     * and the cookie is valid for the time
     * of the session only.
     */
    public Cookie createCookie(String name);

    /**
     * Adds a cookie using the specified name and value.
     * <p>
     * By default, the public host ({@link SpincastConfig#getPublicServerHost()}) 
     * is uses as the cookie's <code>domain</code>
     * and the cookie is valid for the time
     * of the session only.
     */
    public void addCookie(String name, String value);

    /**
     * Adds a cookie, using all available configurations.
     */
    public void addCookie(String name, String value, String path, String domain, Date expires, boolean secure,
                          boolean httpOnly, boolean discard, int version);

    /**
     * Deletes a cookie. In fact, this sets the cookie's <code>expires date</code> in the
     * past so the user's browser will remove it.
     * <code>isExpired()</code> will return <code>true</code> after you called
     * this method.
     */
    public void deleteCookie(String name);

    /**
     * Deletes all cookies. In fact, this sets their <code>expires date</code> in the
     * past so the user's browser will remove them.
     */
    public void deleteAllCookies();

    /**
     * Resets the current cookies to the original ones
     * of the request.
     */
    public void resetCookies();

    /**
     * Did we validate that the current user has 
     * cookies enabled?
     */
    public boolean isCookiesEnabledValidated();

}
