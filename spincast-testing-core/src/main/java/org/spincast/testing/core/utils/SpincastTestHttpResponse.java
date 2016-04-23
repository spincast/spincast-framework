package org.spincast.testing.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.spincast.core.cookies.ICookie;

/**
 * Uses by Spincast tests to represent the response from a call 
 * made to a server.
 */
public class SpincastTestHttpResponse {

    private final int status;
    private final String content;
    private final String contentType;
    private final Map<String, List<String>> headers;
    private final Map<String, ICookie> cookies;

    public SpincastTestHttpResponse(int status,
                                    String contentType,
                                    String content,
                                    Map<String, List<String>> headers,
                                    Map<String, ICookie> cookies) {
        this.status = status;
        this.contentType = contentType;
        this.content = content;
        this.headers = headers;
        this.cookies = cookies;
    }

    public int getStatus() {
        return this.status;
    }

    public String getContentType() {
        return this.contentType;
    }

    public String getContent() {
        return this.content;
    }

    public Map<String, List<String>> getHeaders() {
        return this.headers;
    }

    public String getHeaderFirst(String name) {
        List<String> vals = getHeaders().get(name);
        if(vals != null && vals.size() > 0) {
            return vals.get(0);
        }
        return null;
    }

    public List<String> getHeader(String name) {
        List<String> vals = getHeaders().get(name);
        if(vals == null) {
            vals = new ArrayList<String>();
        }
        return vals;
    }

    public Map<String, ICookie> getCookies() {
        return this.cookies;
    }

    public ICookie getCookie(String name) {
        return getCookies().get(name);
    }

}
