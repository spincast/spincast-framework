package org.spincast.plugins.httpclient;

import java.util.List;
import java.util.Map;

import org.spincast.core.cookies.ICookie;

public interface IHttpResponse {

    /**
     * Gets the HTTP status.
     */
    public int getStatus();

    /**
     * Gets the Content-Type.
     */
    public String getContentType();

    /**
     * Gets the headers.
     */
    public Map<String, List<String>> getHeaders();

    /**
     * Gets an header. An header can have more than 
     * one value.
     */
    public List<String> getHeader(String name);

    /**
     * Gets the first value of an header.
     */
    public String getHeaderFirst(String name);

    /**
     * Gets the cookies.
     */
    public Map<String, ICookie> getCookies();

    /**
     * Gets a cookie.
     */
    public ICookie getCookie(String name);

    /**
     * Is the response gzipped?
     */
    public boolean isGzipped();

    /**
     * Gets the content as a <code>UTF-8</code> String.
     */
    public String getContentAsString();

    /**
     * Gets the content as a String using the specified
     * encoding.
     */
    public String getContentAsString(String encoding);

    /**
     * Get the content as <code>byte[]</code>.
     */
    public byte[] getContentAsByteArray();

}
