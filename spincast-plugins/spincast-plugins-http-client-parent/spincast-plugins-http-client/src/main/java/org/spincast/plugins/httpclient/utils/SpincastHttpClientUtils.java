package org.spincast.plugins.httpclient.utils;

import java.net.HttpCookie;
import java.util.Date;
import java.util.Objects;

import org.spincast.shaded.org.apache.commons.codec.binary.Base64;
import org.spincast.shaded.org.apache.commons.codec.digest.DigestUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.cookie.Cookie;

public class SpincastHttpClientUtils implements ISpincastHttpClientUtils {

    @Override
    public String generateExpectedWebsocketV13AcceptHeaderValue(String secWebSocketKey) {

        String expected = secWebSocketKey + WEBSOCKET_V13_MAGIC_NUMBER;

        byte[] expectedBytes = DigestUtils.sha1(expected);
        expected = Base64.encodeBase64String(expectedBytes);

        return expected;
    }

    @Override
    public String apacheCookieToHttpHeaderValue(Cookie cookie) {

        Objects.requireNonNull(cookie, "The cookie can't be NULL");

        HttpCookie httpCookie = convertApacheCookieToHttpCookie(cookie);
        return httpCookie.toString();
    }

    protected HttpCookie convertApacheCookieToHttpCookie(Cookie cookie) {

        HttpCookie httpCookie = new HttpCookie(cookie.getName(), cookie.getValue());

        httpCookie.setVersion(cookie.getVersion());
        httpCookie.setComment(cookie.getComment());
        httpCookie.setCommentURL(cookie.getCommentURL());
        httpCookie.setDiscard(!cookie.isPersistent());
        httpCookie.setDomain(cookie.getDomain());

        if(cookie.getExpiryDate() != null) {
            httpCookie.setMaxAge((cookie.getExpiryDate().getTime() - new Date().getTime()) / 1000);
        }
        httpCookie.setPath(cookie.getPath());

        if(cookie.getPorts() != null) {
            httpCookie.setPortlist(StringUtils.join(cookie.getPorts(), ","));
        }

        httpCookie.setSecure(cookie.isSecure());

        return httpCookie;

    }

}
