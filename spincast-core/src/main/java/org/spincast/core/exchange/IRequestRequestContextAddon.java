package org.spincast.core.exchange;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.spincast.core.json.IJsonObject;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.utils.ContentTypeDefaults;

/**
 * Methods related to the current <code>request</code>.
 */
public interface IRequestRequestContextAddon<R extends IRequestContext<?>> {

    /**
     * Gets the request's <code>HTTP method</code>. 
     */
    public HttpMethod getHttpMethod();

    /**
     * Finds the best <code>Content-Type</code> to use for a response 
     * using the <code>"Accept"</code> header of the request. It will
     * fallback to <code>ContentTypeDefaults.TEXT</code> if nothing more specific
     * is found.
     */
    public ContentTypeDefaults getContentTypeBestMatch();

    /**
     * Will return <code>true</code> if the request specifies
     * that <code>Json</code> should be returned.
     */
    public boolean isJsonRequest();

    /**
     * Find the best <code>Locale</code> to use for a response 
     * using the <code>"Accept-Language"</code> header of
     * the request.
     * Returns the default <code>Locale</code> (taken from the configurations) 
     * if nothing more specific is found.
     */
    public Locale getLocaleBestMatch();

    /**
     * Returns all headers of the current request. A single header can have multiple values. 
     * The implementation is a <code>TreeMap</code> which is<i>case insensitive</i> for the keys. 
     * The map is immutable.
     */
    public Map<String, List<String>> getHeaders();

    /**
     * Returns the values of the specified header from the current request or 
     * an empty list if not found. 
     * The <code>name</code> is <i>case insensitive</i>. 
     * The list is immutable.
     */
    public List<String> getHeader(String name);

    /**
     * The first value of the specified header from the current request. 
     * The <code>name</code> is <i>case insensitive</i>. 
     * Returns <code>null</code> if the header is not found.
     */
    public String getHeaderFirst(String name);

    /**
     * Returns the full URL, including the queryString.
     */
    public String getFullUrl();

    /**
     * If the request has been forwarded, {@link #getFullUrl() getFullUrl()} is
     * going to return the new, forwarded, url. 
     * 
     * This method will always return the <i>original</i> url.
     */
    public String getOriginalFullUrl();

    /**
     * The path of the request (no querystring).
     */
    public String getRequestPath();

    /**
     * The values parsed from the dynamic parameters of the route path. 
     * The map is immutable.
     */
    public Map<String, String> getPathParams();

    /**
     * A specific value parsed from a dynamic parameter of the route path.
     * The <code>name</code> is <i>case sensitive</i>, since you have easy control over it.
     */
    public String getPathParam(String name);

    /**
     * The queryString of the request, without the "?".
     * Returns an empty String if there is no querystring.
     */
    public String getQueryString();

    /**
     * The parameters taken from the queryString of the request. 
     * A queryString parameter may have multiple values.
     * Returns an empty list if there is no queryString. 
     * The map is immutable.
     */
    public Map<String, List<String>> getQueryStringParams();

    /**
     * A specific parameter taken from the queryString of the request.
     * A queryString parameter may have multiple values.
     * Returns an empty list if the parameter doesn't exist.
     * The list is immutable.
     */
    public List<String> getQueryStringParam(String name);

    /**
     * The first (and often only) value of a specific parameter taken from the 
     * queryString of the request.
     * Returns <code>null</code> if the parameter doesn't exist.
     */
    public String getQueryStringParamFirst(String name);

    /**
     * The raw InputStream of the request's body.
     * Note that what's read from the InputStream cannot be read again!
     */
    public InputStream getBodyAsInputStream();

    /**
     * The bytes of the request's body.
     * Note that the bytes read cannot be read again!
     */
    public byte[] getBodyAsByteArray();

    /**
     * The request's body as a String, using the <code>UTF-8</code> encoding.
     * Note that the characters read cannot be read again!
     */
    public String getBodyAsString();

    /**
     * The request's body as a String, using the specified encoding.
     * Note that the characters read cannot be read again!
     */
    public String getBodyAsString(String encoding);

    /**
     * The request's body deserialized to a <code>IJsonObject</code>. A valid <code>Json</code> body
     * is expected.
     * Note that this can only be called once.
     */
    public IJsonObject getJsonBodyAsJsonObject();

    /**
     * The request's body deserialized to a <code>Map&lt;String, Object&gt;</code>. A valid <code>Json</code> body
     * is expected.
     * Note that this can only be called once.
     */
    public Map<String, Object> getJsonBodyAsMap();

    /**
     * The request's body deserialized to the specified <code>class</code>. A valid <code>Json</code> body
     * is expected.
     * Note that this can only be called once.
     */
    public <T> T getJsonBody(Class<T> clazz);

    /**
     * The request's body deserialized to a <code>IJsonObject</code>. A valid <code>XML</code> body
     * is expected.
     * Note that this can only be called once.
     */
    public IJsonObject getXmlBodyAsJsonObject();

    /**
     * The request's body deserialized to a <code>Map&lt;String, Object&gt;</code>. A valid <code>XML</code> body
     * is expected.
     * Note that this can only be called once.
     */
    public Map<String, Object> getXmlBodyAsMap();

    /**
     * The request's body deserialized to the specified <code>class</code>. A valid <code>XML</code> body
     * is expected.
     * Note that this can only be called once.
     */
    public <T> T getXmlBody(Class<T> clazz);

    /**
     * The parameters submitted from a <code>FORM</code> via a <code>POST</code> method.
     * More than one value with the same <code>key</code> is possible.
     * The names are <i>case sensitive</i>.
     * The map is immutable.
     */
    public Map<String, List<String>> getFormDatas();

    /**
     * A specific parameter submitted from a <code>FORM</code> via a <code>POST</code> method.
     * More than one value with the same <code>name</code> is possible.
     * The <code>name</code> is <i>case sensitive</i>.
     * The list is immutable.
     */
    public List<String> getFormData(String name);

    /**
     * The first (and often only) value of a specific parameter submitted from a <code>FORM</code> via a <code>POST</code> method.
     * The <code>name</code> is <i>case sensitive</i>.
     * Returns <code>null</code> if the parameter doesn't exist.
     */
    public String getFormDataFirst(String name);

    /**
     * The uploaded files, with their names as the keys.
     * More than one uploaded file with the same name is possible.
     * The map is immutable.
     * Returns an empty map if there are no uploadded file.
     */
    public Map<String, List<File>> getUploadedFiles();

    /**
     * The uploaded files of the specified name.
     * More than one uploaded file with the same <code>name</code> is possible.
     * The list is immutable.
     * Returns an empty list if no uploaded files of this name exists.
     */
    public List<File> getUploadedFiles(String name);

    /**
     * The first (and often only) uploaded file of the specified name.
     * Returns <code>null</code> if no uploaded file of this name exists.
     */
    public File getUploadedFileFirst(String name);

}
