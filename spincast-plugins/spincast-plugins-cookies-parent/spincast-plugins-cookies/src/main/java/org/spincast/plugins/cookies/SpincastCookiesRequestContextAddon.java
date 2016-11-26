package org.spincast.plugins.cookies;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import org.spincast.core.cookies.Cookie;
import org.spincast.core.cookies.CookieFactory;
import org.spincast.core.cookies.CookiesRequestContextAddon;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.server.Server;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.commons.lang3.time.DateUtils;

import com.google.inject.Inject;

public class SpincastCookiesRequestContextAddon<R extends RequestContext<?>>
                                               implements CookiesRequestContextAddon<R> {

    private final R requestContext;
    private final Server server;
    private final CookieFactory cookieFactory;

    private Map<String, Cookie> cookies;

    @Inject
    public SpincastCookiesRequestContextAddon(R requestContext,
                                              Server server,
                                              CookieFactory cookieFactory) {
        this.requestContext = requestContext;
        this.server = server;
        this.cookieFactory = cookieFactory;
    }

    protected R getRequestContext() {
        return this.requestContext;
    }

    protected Server getServer() {
        return this.server;
    }

    protected CookieFactory getCookieFactory() {
        return this.cookieFactory;
    }

    @Override
    public Cookie getCookie(String name) {
        return getCookies().get(name);
    }

    @Override
    public void addCookie(Cookie cookie) {

        boolean valid = validateCookie(cookie);
        if(!valid) {
            return;
        }

        getCookies().put(cookie.getName(), cookie);
    }

    @Override
    public Cookie createCookie(String name) {
        return getCookieFactory().createCookie(name);
    }

    @Override
    public void addCookie(String name, String value) {
        Cookie cookie = getCookieFactory().createCookie(name, value);
        addCookie(cookie);
    }

    @Override
    public void addCookie(String name, String value, String path, String domain, Date expires, boolean secure,
                          boolean httpOnly, boolean discard, int version) {
        Cookie cookie = getCookieFactory().createCookie(name, value, path, domain, expires, secure, httpOnly, discard, version);
        addCookie(cookie);
    }

    protected boolean validateCookie(Cookie cookie) {
        Objects.requireNonNull(cookie, "Can't add a NULL cookie");

        String name = cookie.getName();
        if(StringUtils.isBlank(name)) {
            throw new RuntimeException("A cookie can't have an empty name");
        }
        return true;
    }

    @Override
    public void deleteCookie(String name) {
        Cookie cookie = getCookies().get(name);
        deleteCookie(cookie);
    }

    @Override
    public void deleteAllCookies() {
        for(Cookie cookie : getCookies().values()) {
            deleteCookie(cookie);
        }
    }

    protected void deleteCookie(Cookie cookie) {
        if(cookie != null) {
            cookie.setExpires(DateUtils.addYears(new Date(), -1));
        }
    }

    @Override
    public Map<String, Cookie> getCookies() {
        if(this.cookies == null) {
            this.cookies = getServer().getCookies(getRequestContext().exchange());
        }
        return this.cookies;
    }

    @Override
    public void resetCookies() {
        this.cookies = null;
    }

    @Override
    public boolean isCookiesEnabledValidated() {
        return getCookies().size() > 0;
    }

}
