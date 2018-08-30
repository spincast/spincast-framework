package org.spincast.plugins.httpclient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.spincast.core.cookies.Cookie;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.utils.SpincastStatics;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Spincast Http Client's reponse implementation.
 */
public class HttpResponseDefault implements HttpResponse {

    private final int status;
    private final byte[] content;
    private String contentAsString;
    private String lastContentAsStringEncodingUsed;
    private final String contentType;
    private final Map<String, List<String>> headers;
    private final Map<String, Cookie> cookies;
    private final boolean isGzipped;
    private final JsonManager jsonManager;

    @AssistedInject
    public HttpResponseDefault(@Assisted("status") int status,
                               @Assisted("contentType") @Nullable String contentType,
                               @Assisted("content") @Nullable byte[] content,
                               @Assisted("headers") Map<String, List<String>> headers,
                               @Assisted("cookies") Map<String, Cookie> cookies,
                               @Assisted("wasZipped") boolean wasZipped,
                               JsonManager jsonManager) {
        this.status = status;
        this.contentType = contentType;
        this.headers = headers;
        this.cookies = cookies;
        this.isGzipped = wasZipped;

        if (content == null) {
            content = new byte[0];
        }
        this.content = content;

        this.jsonManager = jsonManager;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public String getContentAsString() {
        return getContentAsString("UTF-8");
    }

    @Override
    public String getContentAsString(String encoding) {

        try {
            if (encoding == null) {
                encoding = "UTF-8";
            }

            if (this.contentAsString == null || !encoding.equals(this.lastContentAsStringEncodingUsed)) {
                this.lastContentAsStringEncodingUsed = encoding;
                this.contentAsString = new String(getContentAsByteArray(), encoding);
            }

            return this.contentAsString;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public JsonObject getContentAsJsonObject() {

        String str = getContentAsString();
        return getJsonManager().fromString(str);
    }

    @Override
    public byte[] getContentAsByteArray() {
        return this.content;
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        return this.headers;
    }

    @Override
    public String getHeaderFirst(String name) {
        List<String> vals = getHeaders().get(name);
        if (vals != null && vals.size() > 0) {
            return vals.get(0);
        }
        return null;
    }

    @Override
    public List<String> getHeader(String name) {
        List<String> vals = getHeaders().get(name);
        if (vals == null) {
            vals = new ArrayList<String>();
        }
        return vals;
    }

    @Override
    public Map<String, Cookie> getCookies() {
        return this.cookies;
    }

    @Override
    public Cookie getCookie(String name) {
        return getCookies().get(name);
    }

    @Override
    public String getCookieValue(String name) {
        Cookie cookie = getCookie(name);
        if (cookie == null) {
            return null;
        }
        return cookie.getValue();
    }

    @Override
    public boolean isGzipped() {
        return this.isGzipped;
    }

    @Override
    public String toString() {
        return getStatus() + " - " + getContentType();
    }

}
