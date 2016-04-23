package org.spincast.plugins.cookies;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import org.spincast.core.cookies.ICookie;
import org.spincast.core.cookies.ICookieFactory;
import org.spincast.core.cookies.ICookiesRequestContextAddon;
import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.server.IServer;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.commons.lang3.time.DateUtils;

import com.google.inject.Inject;

public class SpincastCookiesRequestContextAddon<R extends IRequestContext<?>>
                                               implements ICookiesRequestContextAddon<R> {

    private final R requestContext;
    private final IServer server;
    private final ICookieFactory cookieFactory;

    private Map<String, ICookie> cookies;

    @Inject
    public SpincastCookiesRequestContextAddon(R requestContext,
                                              IServer server,
                                              ICookieFactory cookieFactory) {
        this.requestContext = requestContext;
        this.server = server;
        this.cookieFactory = cookieFactory;
    }

    protected R getRequestContext() {
        return this.requestContext;
    }

    protected IServer getServer() {
        return this.server;
    }

    protected ICookieFactory getCookieFactory() {
        return this.cookieFactory;
    }

    @Override
    public ICookie getCookie(String name) {
        return getCookies().get(name);
    }

    @Override
    public void addCookie(ICookie cookie) {

        boolean valid = validateCookie(cookie);
        if(!valid) {
            return;
        }

        getCookies().put(cookie.getName(), cookie);
    }

    @Override
    public void addCookie(String name, String value) {
        ICookie cookie = getCookieFactory().createCookie(name, value);
        addCookie(cookie);
    }

    @Override
    public void addCookie(String name, String value, String path, String domain, Date expires, boolean secure,
                          boolean httpOnly, boolean discard, int version) {
        ICookie cookie = getCookieFactory().createCookie(name, value, path, domain, expires, secure, httpOnly, discard, version);
        addCookie(cookie);
    }

    protected boolean validateCookie(ICookie cookie) {
        Objects.requireNonNull(cookie, "Can't add a NULL cookie");

        String name = cookie.getName();
        if(StringUtils.isBlank(name)) {
            throw new RuntimeException("A cookie can't have an empty name");
        }
        return true;
    }

    @Override
    public void deleteCookie(String name) {
        ICookie cookie = getCookies().get(name);
        deleteCookie(cookie);
    }

    @Override
    public void deleteAllCookies() {
        for(ICookie cookie : getCookies().values()) {
            deleteCookie(cookie);
        }
    }

    protected void deleteCookie(ICookie cookie) {
        if(cookie != null) {
            cookie.setExpires(DateUtils.addYears(new Date(), -1));
        }
    }

    @Override
    public Map<String, ICookie> getCookies() {
        if(this.cookies == null) {
            this.cookies = getServer().getCookies(getRequestContext().exchange());
        }
        return this.cookies;
    }

    @Override
    public void resetCookies() {
        this.cookies = null;
    }

}
