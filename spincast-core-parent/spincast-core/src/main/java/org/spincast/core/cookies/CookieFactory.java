package org.spincast.core.cookies;

import java.util.Date;

import org.spincast.core.config.SpincastConfig;

import com.google.inject.assistedinject.Assisted;

/**
 * Factory to create cookies.
 */
public interface CookieFactory {

    /**
     * Creates a cookie using the given name (<code>null</code> value).
     * <p>
     * By default, the public host ({@link SpincastConfig#getPublicServerHost()}) 
     * is uses as the cookie's <code>domain</code>
     * and the cookie is valid for the time
     * of the session only.
     */
    public Cookie createCookie(@Assisted("name") String name);

    /**
     * Creates a cookie using the given name and value. 
     * <p>
     * By default, the public host ({@link SpincastConfig#getPublicServerHost()}) 
     * is uses as the cookie's <code>domain</code>
     * and the cookie is valid for the time
     * of the session only.
     */
    public Cookie createCookie(@Assisted("name") String name,
                               @Assisted("value") String value);

    /**
     * Creates a cookie using all available configurations.
     */
    public Cookie createCookie(@Assisted("name") String name,
                               @Assisted("value") String value,
                               @Assisted("path") String path,
                               @Assisted("domain") String domain,
                               @Assisted("expires") Date expires,
                               @Assisted("secure") boolean secure,
                               @Assisted("httpOnly") boolean httpOnly,
                               @Assisted("cookieSameSite") CookieSameSite cookieSameSite,
                               @Assisted("discard") boolean discard,
                               @Assisted("version") int version);
}
