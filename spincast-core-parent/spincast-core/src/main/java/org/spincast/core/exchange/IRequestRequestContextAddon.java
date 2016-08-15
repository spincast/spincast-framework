package org.spincast.core.exchange;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.spincast.core.json.IJsonObject;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.IETag;
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
     * that <code>Json</code> is the most appropriate format 
     * to return.
     */
    public boolean isJsonShouldBeReturn();

    /**
     * Will return <code>true</code> if the request specifies
     * that <code>HTML</code> is the most appropriate format 
     * to return.
     */
    public boolean isHTMLShouldBeReturn();

    /**
     * Will return <code>true</code> if the request specifies
     * that <code>XML</code> is the most appropriate format 
     * to return.
     */
    public boolean isXMLShouldBeReturn();

    /**
     * Will return <code>true</code> if the request specifies
     * that <code>plain-text</code> is the most appropriate format 
     * to return.
     */
    public boolean isPlainTextShouldBeReturn();

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
     * The Content-Type of the request, if any.
     * 
     * @return the <code>Content-Type</code> of the request or <code>null</code>
     * if none was specified.
     */
    public String getContentType();

    /**
     * Returns the current full URL, including the queryString, if any.
     * Cache buster codes are removed, if there were any.
     * <p>
     * In case the request has been forwarded, this will be the *new*,
     * the current URL. Use {@link #getFullUrlOriginal() getFullUrlOriginal()}
     * to get the original URL, before the request was forwarded.
     * </p>
     * <p>
     * If a reverse proxy has been used, this URL will contain the 
     * <code>scheme</code>, <code>host</code> and <code>port</code> from the 
     * <em>original</em> URL, as seen by the user. 
     * In general, this is what you want to use in your application.
     * </p>
     */
    public String getFullUrl();

    /**
     * Returns the current full URL, including the queryString, if any.
     * <p>
     * In case the request has been forwarded, this will be the *new*,
     * the current URL. Use {@link #getFullUrlOriginal() getFullUrlOriginal()}
     * to get the original URL, before the request was forwarded.
     * </p>
     * <p>
     * If a reverse proxy has been used, this URL will contain the 
     * <code>scheme</code>, <code>host</code> and <code>port</code> from the 
     * <em>original</em> URL, as seen by the user. 
     * In general, this is what you want to use in your application.
     * </p>
     * 
     * @param keepCacheBusters if <code>true</code>, the returned URL will contain
     * the cache buster codes, if there were any. The default behavior is to 
     * automatically remove them.
     */
    public String getFullUrl(boolean keepCacheBusters);

    /**
     * If the request has been forwarded, this is going to return the original
     * URL, not the current, forwarded, one.
     * Cache buster codes are removed, if there were any.
     * <p>
     * Use {@link #getFullUrl() getFullUrl()} to get the current, potentially
     * forwarded URL.
     * </p>
     * <p>
     * If a reverse proxy has been used, this URL will contain the 
     * <code>scheme</code>, <code>host</code> and <code>port</code> from the 
     * <em>original</em> URL, as seen by the user. 
     * In general, this is what you want to use in your application.
     * </p>
     */
    public String getFullUrlOriginal();

    /**
     * If the request has been forwarded, this is going to return the original
     * URL, not the current, forwarded, one.
     * <p>
     * Use {@link #getFullUrl() getFullUrl()} to get the current, potentially
     * forwarded URL.
     * </p>
     * <p>
     * If a reverse proxy has been used, this URL will contain the 
     * <code>scheme</code>, <code>host</code> and <code>port</code> from the 
     * <em>original</em> URL, as seen by the user. 
     * In general, this is what you want to use in your application.
     * </p>
     * 
     * @param keepCacheBusters if <code>true</code>, the returned URL will contain
     * the cache buster codes, if there were any. The default behavior is to 
     * automatically remove them.
     */
    public String getFullUrlOriginal(boolean keepCacheBusters);

    /**
     * If a reverse proxy has been used, this URL will contain the 
     * <code>scheme</code>, <code>host</code> and <code>port</code> 
     * as forwarded by the reserve proxy, <em>not as seen by the user</em>.
     * Cache buster codes are removed, if there were any.
     * <p>
     * If the request has been forwarded, this is going to return the original
     * URL, not the current, forwarded, one.
     * </p>
     * <p>
     * In general, you should probably use {@link #getFullUrl() getFullUrl()}
     * or {@link #getFullUrlOriginal() getFullUrlOriginal()} instead of this
     * one.
     * </p>
     */
    public String getFullUrlProxied();

    /**
     * If a reverse proxy has been used, this URL will contain the 
     * <code>scheme</code>, <code>host</code> and <code>port</code> 
     * as forwarded by the reserve proxy, <em>not as seen by the user</em>.
     * Cache buster codes are removed, if there were any.
     * <p>
     * If the request has been forwarded, this is going to return the original
     * URL, not the current, forwarded, one.
     * </p>
     * <p>
     * In general, you should probably use {@link #getFullUrl() getFullUrl()}
     * or {@link #getFullUrlOriginal() getFullUrlOriginal()} instead of this
     * one.
     * </p>
     * 
     * @param keepCacheBusters if <code>true</code>, the returned URL will contain
     * the cache buster codes, if there were any. The default behavior is to 
     * automatically remove them.
     */
    public String getFullUrlProxied(boolean keepCacheBusters);

    /**
     * The path of the request (no querystring).
     */
    public String getRequestPath();

    /**
     * The path of the request (no querystring).
     * 
     * @param keepCacheBusters if <code>true</code>, the returned path will contain
     * the cache buster codes, if there were any. The default behavior is to 
     * automatically remove them.
     */
    public String getRequestPath(boolean keepCacheBusters);

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
     * The queryString of the request.
     * Returns an empty String if there is no queryString.
     * 
     * @param withQuestionMark if <code>true</code> and the queryString
     * is not empty, the result will be prefixed with "?".
     */
    public String getQueryString(boolean withQuestionMark);

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
     * The datas submitted from a <code>FORM</code> via a <code>POST</code> method, as
     * a <code>Map</code>.
     * More than one value with the same <code>key</code> is possible.
     * The names are <em>case sensitive</em>.
     * <p>
     * This returns an <em>immutable</em> object! You won't
     * be able to add or remove elements.
     * </p>
     */
    public Map<String, List<String>> getFormDatasAsMap();

    /**
     * The datas submitted from a <code>FORM</code> via a <code>POST</code> method.
     * More than one value with the same <code>key</code> is possible.
     * The names are <em>case sensitive</em>.
     * <p>
     * This returns a new instance of <code>IJsonObject</code>. Any modification
     * to the object or to one of its <code>IJsonArray</code> property won't affect the
     * original form datas.
     * </p>
     */
    public IJsonObject getFormDatas();

    /**
     * A specific parameter submitted from a <code>FORM</code> via a <code>POST</code> method.
     * More than one value with the same <code>name</code> is possible.
     * The <code>name</code> is <i>case sensitive</i>.
     * <p>
     * This returns a new instance of <code>List</code>. Any modification
     * to the list won't affect the original form datas.
     * </p>
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

    /**
     * Is the request a secure HTTPS one?
     */
    public boolean isHttps();

    /**
     * Returns the <code>ETags</code> from
     * the <code>If-None-Match</code> header, if any.
     * 
     * @return the <code>If-None-Match</code> ETags or an empty
     * list if there is none.
     */
    public List<IETag> getEtagsFromIfNoneMatchHeader();

    /**
     * Returns the <code>ETags</code> from
     * the <code>If-Match</code> header, if any.
     * 
     * @return the <code>If-Match</code> ETags or an empty list
     * if there is none.
     */
    public List<IETag> getEtagsFromIfMatchHeader();

    /**
     * Return the value of the <code>If-Modified-Since</code>
     * header as a Date or <code>null</code> if it doesn't
     * exist.
     */
    public Date getDateFromIfModifiedSinceHeader();

    /**
     * Return the value of the <code>If-Unmodified-Since</code>
     * header as a Date or <code>null</code> if it doesn't
     * exist.
     */
    public Date getDateFromIfUnmodifiedSinceHeader();

}
