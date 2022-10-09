package org.spincast.core.exchange;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.spincast.core.flash.FlashMessage;
import org.spincast.core.json.JsonObject;
import org.spincast.core.locale.LocaleResolver;
import org.spincast.core.request.Form;
import org.spincast.core.routing.ETag;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.server.UploadedFile;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.validation.ValidationSet;

/**
 * Methods related to the current <code>request</code>.
 */
public interface RequestRequestContextAddon<R extends RequestContext<?>> {

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
     * Find what <strong>the request</strong> tells should be the best
     * <code>Locale</code> to use for a response, by looking
     * at the <code>"Accept-Language"</code> header.
     * <p>
     * Note that this will <em>not</em> necessarily be the absolute best
     * <code>Locale</code> to used for the user: it will not look at
     * cookies or at any other ways the user may have specified its
     * langague preference! It only look at the request itself.
     * <p>
     * Use {@link RequestContext#getLocaleToUse()} or
     * {@link LocaleResolver#getLocaleToUse()} directly if you want
     * to know the absolute best <code>Locale</code> to use for the user!
     *
     * @return the default <code>Locale</code> (taken from the configurations)
     * if nothing more specific is found on the request.
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
     * <p>
     * The <code>name</code> is <i>case insensitive</i>.
     * <p>
     * The list is immutable.
     */
    public List<String> getHeader(String name);

    /**
     * The first value of the specified header from the current request.
     * <p>
     * The <code>name</code> is <i>case insensitive</i>.
     * <p>
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
     *
     * @return the path param value or <code>null</code> if not found.
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
     * Note that once part of the InputStream is read, it can't be read again!
     */
    public InputStream getBodyAsInputStream();

    /**
     * The bytes of the request's body.
     * Note that once part of the InputStream is read, it can't be read again!
     */
    public byte[] getBodyAsByteArray();

    /**
     * The request's body as a String, using the <code>UTF-8</code> encoding.
     * Note that once part of the InputStream is read, it can't be read again!
     */
    public String getBodyAsString();

    /**
     * The request's body as a String, using the specified encoding.
     * Note that once part of the InputStream is read, it can't be read again!
     */
    public String getStringBody(String encoding);

    /**
     * The request's body deserialized to an immutable <code>JsonObject</code>.
     * A valid <code>Json</code> body
     * is expected.
     * Note that once part of the InputStream is read, it can't be read again!
     */
    public JsonObject getJsonBody();

    /**
     * The request's body deserialized to a <code>Map&lt;String, Object&gt;</code>. A valid <code>Json</code> body
     * is expected.
     * Note that once part of the InputStream is read, it can't be read again!
     */
    public Map<String, Object> getJsonBodyAsMap();

    /**
     * The request's body deserialized to the specified <code>class</code>. A valid <code>Json</code> body
     * is expected.
     * Note that once part of the InputStream is read, it can't be read again!
     */
    public <T> T getJsonBody(Class<T> clazz);

    /**
     * The request's body deserialized to an immutable <code>JsonObject</code>.
     * A valid <code>XML</code> body
     * is expected.
     * Note that once part of the InputStream is read, it can't be read again!
     */
    public JsonObject getXmlBodyAsJsonObject();

    /**
     * The request's body deserialized to a <code>Map&lt;String, Object&gt;</code>. A valid <code>XML</code> body
     * is expected.
     * Note that once part of the InputStream is read, it can't be read again!
     */
    public Map<String, Object> getXmlBodyAsMap();

    /**
     * The request's body deserialized to the specified <code>class</code>. A valid <code>XML</code> body
     * is expected.
     * Note that once part of the InputStream is read, it can't be read again!
     */
    public <T> T getXmlBody(Class<T> clazz);

    /**
     * The data submitted as a <code>FORM</code> body
     * (in general via a <code>POST</code> method),
     * as a <code>Map</code>.
     * <p>
     * The names are <em>case sensitive</em>.
     * <p>
     * This returns the keys/values as is, without trying to parse
     * the keys as <code>JsonPaths</code>.
     * </p>
     * <p>
     * This returns an <em>immutable</em> object! You won't
     * be able to add or remove elements.
     * </p>
     */
    public Map<String, List<String>> getFormBodyRaw();

    /**
     * The data submitted as a <code>FORM</code> body
     * (in general via a <code>POST</code> method),
     * as an immutable <code>JsonObject</code>.
     * <p>
     * The root keys of the field names and  are <em>case sensitive</em>.
     * <p>
     * The root keys will be parsed as <code>JsonPaths</code> to build the final
     * <code>JsonObject</code>. For example : <code>user.books[1].name</code>
     * will be converted to a "user" <code>JsonObject</code> with a "books" arrays
     * with one book at index "1" and this book
     * has a "name" property.
     *
     * @return an <em>immutable</em> instance of <code>JsonObject</code>.
     * If you want to get a mutable instance, you can call <code>.clone(true)</code>
     * on this object.
     */
    public JsonObject getFormBodyAsJsonObject();

    /**
     * Gets the part of the submitted <code>FORM</code> body
     * that is scoped by the specified <code>root key</code>.
     * <p>
     * When an HTML form is submitted, there may be utility fields
     * (such as a CSRF token, etc.) in addition to
     * business logic fields.
     * By using this {@link #getFormOrCreate(String)}
     * you only get the fields which names start with the specified
     * <code>root key</code>. For example, if this HTML form was submitted:
     * <pre>
     * &lt;form&gt;
     *  &lt;input type="text" name="csrfToken" value="12345" /&gt;
     *  &lt;input type="text" name="myUser.userName" value="Stromgol" /&gt;
     *  &lt;input type="text" name="myUser.lastNameName" value="LaPierre" /&gt;
     * ...
     * </pre>
     * <p>
     * ... then calling <code>getFormWithRootKey("myUser")</code> would
     * return a {@link Form} object containing the "userName" and the "userName"
     * fields, but not the "csrfToken".
     * </p>
     * <p>
     * A {@link Form} object is in fact a {@link JsonObject} containing the submitted
     * fields and a {@link ValidationSet} to store validations performed on it.
     * <p>
     * The same field is returned, everytime this method is called
     * with the same <code>name</code>.
     * <p>
     * If the root key is not found in the POSTed data, an empty
     * form will be created.
     * <p>
     * Never returns <code>null</code>.
     * <p>
     * The key are <em>case sensitive</em>.
     */
    public Form getFormOrCreate(String rootKey);

    /**
     * Get a <code>Form</code> that some of your previous
     * code already added. This is to be used when you want
     * to know if this is the case or not.
     * <p>
     * For example, When a Form is posted, you will use
     * {@link #getFormOrCreate(String)} to create the
     * <code>Form</code> to validate. For example:
     *
     * <code>
     * Form form = context.request().getFormOrCreate("form");
     * context.response().addForm(form);
     *
     * // perform validations...
     *
     * // add an error
     * form.addError("someFieldKey",
     *               "someFieldKey_tooLong",
     *               "The someFieldKey contains too many characters.");
     * </code>
     *
     * If there is an error, you may want to call the "GET"
     * version of the current POST one in order to reuse its
     * logic. But in that GET method you will then want to
     * <em>keep the submitted data</em>, not add some freshly
     * taken one from the database!
     * <p>
     * So the GET method will call
     * {@link #getFormAlreadyAdded(String)} and only
     * if it's <code>null</code> will it call
     * {@link #getFormOrCreate(String)}, add fresh values
     * taken from the database and add the form to the
     * response:
     *
     * <code>
     * Form form = context.request().getFormAlreadyAdded("yourFormName");
     * if (form == null) {
     *     form = context.request().getFormOrCreate("form");
     *     context.response().addForm(form);
     *     form.set("someFieldKey", valueFreshFromTheDatabase);
     * }
     * </code>
     *
     * The key are <em>case sensitive</em>.
     *
     * @return the form if it has already been added
     * by your application or <code>null</code> otherwise.
     */
    public Form getFormAlreadyAdded(String rootKey);

    /**
     * The key of the map if the HTML's <code>name</code> attribute.
     * <p>
     * More than one uploaded file with the same name is possible.
     * The map is immutable.
     * <p>
     * Returns an empty map if there are no uploadded file.
     */
    public Map<String, List<UploadedFile>> getUploadedFiles();

    /**
     * The uploaded files of the specified HTML's <code>name</code> attribute.
     * <p>
     * More than one uploaded file with the same <code>name</code> is possible.
     * The list is immutable.
     * Returns an empty list if no uploaded files of this name exists.
     */
    public List<UploadedFile> getUploadedFiles(String htmlName);

    /**
     * The uploaded files of the specified HTML's <code>name</code> attribute.
     * <p>
     * The first (and often only) uploaded file of the specified name.
     * <p>
     * Returns <code>null</code> if no uploaded file of this name exists.
     */
    public UploadedFile getUploadedFileFirst(String htmlName);

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
    public List<ETag> getEtagsFromIfNoneMatchHeader();

    /**
     * Returns the <code>ETags</code> from
     * the <code>If-Match</code> header, if any.
     *
     * @return the <code>If-Match</code> ETags or an empty list
     * if there is none.
     */
    public List<ETag> getEtagsFromIfMatchHeader();

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

    /**
     * Gets the Flash message, if any.
     *
     * @return the Flash message or <code>null</code> if there
     * is none.
     */
    public FlashMessage getFlashMessage();

    /**
     * Is there a Flash message?
     */
    public boolean isFlashMessageExists();

    /**
     * Gets the the request cookies values as a Map,
     * using the names of the cookies as the keys.
     */
    public Map<String, String> getCookiesValues();

    /**
     * Gets the value of a request cookie by name.
     *
     * @return the value or <code>null</code> if not found.
     */
    public String getCookieValue(String name);

    /**
     * Did we validate that the current user has
     * cookies enabled?
     */
    public boolean isCookiesEnabledValidated();

    /**
     * Gets the IP of the current request.
     */
    public String getIp();

}
