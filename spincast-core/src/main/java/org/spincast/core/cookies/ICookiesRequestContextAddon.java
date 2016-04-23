package org.spincast.core.cookies;

import java.util.Date;
import java.util.Map;

import org.spincast.core.exchange.IRequestContext;

/**
 * Request context add-on to work with cookies.
 */
public interface ICookiesRequestContextAddon<R extends IRequestContext<?>> {

    /**
     * Gets the current cookies in a Map, using their names as the keys.
     */
    public Map<String, ICookie> getCookies();

    /**
     * Gets a cookie.
     * 
     * @return the cookie or <code>null</code> if not found.
     */
    public ICookie getCookie(String name);

    /**
     * Adds a cookie.
     */
    public void addCookie(ICookie cookie);

    /**
     * Adds a cookie using the specified name and value.
     */
    public void addCookie(String name, String value);

    /**
     * Adds a cookie, using all possible configurations.
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

}
