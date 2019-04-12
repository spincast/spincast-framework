package org.spincast.core.exchange;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.cookies.Cookie;
import org.spincast.core.cookies.CookieSameSite;
import org.spincast.core.flash.FlashMessage;
import org.spincast.core.flash.FlashMessageLevel;
import org.spincast.core.json.JsonObject;
import org.spincast.core.request.Form;
import org.spincast.core.response.AlertLevel;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.utils.GzipOption;

import com.google.inject.assistedinject.Assisted;

/**
 * Methods to manipulate the response.
 */
public interface ResponseRequestContextAddon<R extends RequestContext<?>> {

    /**
     * The placeholder to replace by the current cache buster when pushing
     * a resource on HTTP/2.
     */
    public static final String RESOURCE_TO_PUSH_PLACEHOLDERS_CACHE_BUSTER = "${cacheBuster}";

    /**
     * The <code>JsonObject</code> which serves as the model for
     * the response. This object will be used by the templating
     * engine or will be sent as is, often as an
     * <code>application/json</code> response.
     * <p>
     * This object is mutable.
     */
    public JsonObject getModel();

    /**
     * Replaces the current response model completely.
     * <p>
     * Use {@link #getModel()} instead to get the current
     * model instance and add properties to it.
     */
    public void setModel(JsonObject model);

    /**
     * Is the response closed? If so, nothing more can be sent...
     */
    public boolean isClosed();

    /**
     * Flushes everything and closes the response.
     * Nothing more can be sent after this (but the
     * remaining <code>route handlers</code> will still
     * be called).
     */
    public void end();

    /**
     * Are the response headers sent? If this is the case,
     * then the headers can't be changed anymore.
     */
    public boolean isHeadersSent();

    /**
     * Sends some <code>bytes</code>, without flushing.
     */
    public void sendBytes(byte[] bytes);

    /**
     * Sends some <code>bytes</code> using the specified <code>Content-Type</code>,
     * without flushing.
     */
    public void sendBytes(byte[] bytes, String contentType);

    /**
     * Sends some <code>bytes</code> using the specified <code>Content-Type</code> and flushes,
     * if specified.
     *
     * Note that once the response is flushed, no header can be added or changed anymore.
     */
    public void sendBytes(byte[] bytes, String contentType, boolean flush);

    /**
     * The charset to use to convert characters to bytes.
     * Defaults to <code>"UTF-8"</code>.
     */
    public String getCharactersCharsetName();

    /**
     * Sets the charset to use to convert characters to bytes.
     * Defaults to <code>"UTF-8"</code>.
     *
     * Make sure you use the same charset here
     * than the one that is sent as the <code>"Content-Type"</code> header (which
     * is also <code>"UTF-8"</code> by default, if you send data using
     * <code>sendPlainText()</code>, <code>sendHtml()</code> or <code>sendJson()</code>).
     */
    public ResponseRequestContextAddon<R> setCharactersCharsetName(String name);

    /**
     * Sends a String, using the encoding returned by {@link #getCharactersCharsetName() getCharactersCharsetName} and
     * using the specified <code>Content-Type</code>, without flushing.
     */
    public void sendCharacters(String content, String contentType);

    /**
     * Sends a String, using the encoding returned by {@link #getCharactersCharsetName() getCharactersCharsetName}
     * and using the specified <code>Content-Type</code>. Flushes the response, if specified.
     *
     * Note that once the response is flushed, no header can be added or changed anymore.
     */
    public void sendCharacters(String content, String contentType, boolean flush);

    /**
     * Sends a String as <code>text/plain</code>, <code>UTF-8</code> encoded, without flushing.
     */
    public void sendPlainText(String string);

    /**
     * Sends a String as <code>text/plain</code>, <code>UTF-8</code> encoded,
     * and flushes, if specified.
     *
     * Note that once the response is flushed, no header can be added or changed anymore.
     */
    public void sendPlainText(String string, boolean flush);

    /**
     * Sends the <em>model</em> as <code>application/json</code>, without flushing.
     */
    public void sendJson();

    /**
     * Sends the <em>model</em> as <code>application/json</code>,
     * and flushes, if specified.
     * <p>
     * Note that once the response is flushed, no header can be added or changed anymore.
     */
    public void sendJson(boolean flush);

    /**
     * Sends a <code>Json</code> String using the <code>application/json</code> Content-Type,
     * without flushing.
     *
     * <p>
     * Synonym of : <code>sendCharacters(jsonString, "application/json")</code>
     * </p>
     */
    public void sendJson(String jsonString);

    /**
     * Sends a <code>Json</code> String using the <code>application/json</code> Content-Type,
     * and flushes, if specified.
     *
     * <p>
     * Note that once the response is flushed, no header can be added or changed anymore.
     * </p>
     * <p>
     * Synonym of : <code>sendCharacters(jsonString, "application/json", flush)</code>
     * </p>
     */
    public void sendJson(String jsonString, boolean flush);

    /**
     * Serializes the object to <code>Json</code> and sends as
     * <code>application/json</code>, without flushing.
     * <p>
     * If the specified Object is a <code>String</code>, it will
     * be considered as already being <code>JSON</code> and will be
     * send as is.
     */
    public void sendJson(Object obj);

    /**
     * Serializes the object to <code>Json</code>, sends as <code>application/json</code>,
     * and flushes, if specified.
     * <p>
     * Note that once the response is flushed, no header can be added or changed anymore.
     * <p>
     * If the specified Object is a <code>String</code>, it will
     * be considered as already being <code>JSON</code> and will be
     * send as is.
     */
    public void sendJson(Object obj, boolean flush);

    /**
     * Sends the <em>model</em> as <code>application/xml</code>,
     * without flushing.
     */
    public void sendXml();

    /**
     * Sends the <em>model</em> as <code>application/xml</code>,
     * and flushes, if specified.
     * <p>
     * Note that once the response is flushed, no header can be added or changed anymore.
     */
    public void sendXml(boolean flush);

    /**
     * Sends some <code>XML</code> using the <code>application/xml</code> Content-Type,
     * without flushing.
     *
     * <p>
     * Synonym of : <code>sendCharacters(xml, "application/xml")</code>
     * </p>
     */
    public void sendXml(String xml);

    /**
     * Sends some <code>XML</code> using the <code>application/xml</code> Content-Type,
     * and flushes, if specified.
     *
     * <p>
     * Note that once the response is flushed, no header can be added or changed anymore.
     * </p>
     * <p>
     * Synonym of : <code>sendCharacters(xml, "application/xml", flush)</code>
     * </p>
     */
    public void sendXml(String xml, boolean flush);

    /**
     * Serializes the object to <code>XML</code> and sends
     * as <code>application/xml</code>, without flushing.
     * <p>
     * If the specified Object is a <code>String</code>, it will
     * be considered as already being <code>XML</code> and will be
     * send as is.
     */
    public void sendXml(Object obj);

    /**
     * Serializes the object to <code>XML</code>, sends as <code>application/xml</code>, and flushes, if specified.
     * <p>
     * Note that once the response is flushed, no header can be added or changed anymore.
     * <p>
     * If the specified Object is a <code>String</code>, it will
     * be considered as already being <code>XML</code> and will be
     * send as is.
     */
    public void sendXml(Object obj, boolean flush);

    /**
     * Sends a String as <code>text/html</code>, <code>UTF-8</code> encoded, without flushing.
     */
    public void sendHtml(String html);

    /**
     * Sends a String as <code>text/html</code>, <code>UTF-8</code> encoded,
     * and flushes, if specified.
     *
     * Note that once the response is flushed, no header can be added or changed anymore.
     */
    public void sendHtml(String html, boolean flush);

    /**
     * Parses the given String using the <code>TemplatingEngine</code>,
     * then sends the result using the specified
     * <code>Content-Type</code>, without flushing.
     */
    public void sendParse(String content, String contentType);

    /**
     * Parses the given String using the <code>TemplatingEngine</code>,
     * then sends the result using the specified
     * <code>Content-Type</code>, and flushes, if specified.
     *
     * Note that once the response is flushed, no header can be added or changed anymore.
     */
    public void sendParse(String content, String contentType, boolean flush);

    /**
     * Finds the <code>HTML</code> template using the <code>TemplatingEngine</code>,
     * evaluates it using the model that have been added to the response, then sends the
     * result as <code>text/html</code>, <code>UTF-8</code> encoded, without flushing.
     *
     * @param templatePath must be a classpath's relative path.
     */
    public void sendTemplateHtml(String templatePath);

    /**
     * Finds the <code>HTML</code> template using the <code>TemplatingEngine</code>, evaluates it using
     * the given parameters, then sends the
     * result as <code>text/html</code>, <code>UTF-8</code> encoded.
     *
     * @param isClasspathPath if <code>true</code>, the 'templatePath' is considered as
     * a classpath's relative path. If <code>false</code>, it is considered as an absolute file
     * system path.
     */
    public void sendTemplateHtml(String templatePath, boolean isClasspathPath);

    /**
     * Finds the <code>HTML</code> template using the <code>TemplatingEngine</code>, evaluates it using
     * the given parameters, then sends the
     * result as <code>text/html</code>, <code>UTF-8</code> encoded, and flushes, if specified.
     *
     * Note that once the response is flushed, no header can be added or changed anymore.
     *
     * @param isClasspathPath if <code>true</code>, the 'templatePath' is considered as
     * a classpath's relative path. If <code>false</code>, it is considered as an absolute file
     * system path.
     *
     */
    public void sendTemplateHtml(String templatePath, boolean isClasspathPath, boolean flush);

    /**
     * Finds the specified template using the <code>TemplatingEngine</code>, evaluates it using
     * the given parameters, then sends the
     * result using the given <code>contentType</code>, without flushing.
     *
     * @param templatePath must be a classpath's relative path.
     */
    public void sendTemplate(String templatePath, String contentType);

    /**
     * Finds the specified template using the <code>TemplatingEngine</code>, evaluates it using
     * the given parameters, then sends the
     * result using the given <code>contentType</code>, and flushes, if specified.
     *
     * Note that once the response is flushed, no header can be added or changed anymore.
     *
     * @param templatePath must be a classpath's relative path.
     */
    public void sendTemplate(String templatePath, String contentType, boolean flush);

    /**
     * Finds the specified template using the <code>TemplatingEngine</code>, evaluates it using
     * the given parameters, then sends the
     * result using the given <code>contentType</code>, without flushing.
     *
     * @param isClasspathPath if <code>true</code>, the 'templatePath' is considered as
     * a classpath's relative path. If <code>false</code>, it is considered as an absolute file
     * system path.
     */
    public void sendTemplate(String templatePath, boolean isClasspathPath, String contentType);

    /**
     * Finds the specified template using the <code>TemplatingEngine</code>, evaluates it using
     * the given parameters, then sends the
     * result using the given <code>contentType</code>, and flushes, if specified.
     *
     * Note that once the response is flushed, no header can be added or changed anymore.
     *
     * @param isClasspathPath if <code>true</code>, the 'templatePath' is considered as
     * a classpath's relative path. If <code>false</code>, it is considered as an absolute file
     * system path.
     */
    public void sendTemplate(String templatePath, boolean isClasspathPath, String contentType, boolean flush);

    /**
     * Parses the given String using the <code>TemplatingEngine</code> and
     * the given parameters, then sends the result as <code>text/html</code>,
     * <code>UTF-8</code> encoded.
     */
    public void sendParseHtml(String html);

    /**
     * Parses the given String using the <code>TemplatingEngine</code> and
     * the given parameters, then sends the result as <code>text/html</code>,
     * <code>UTF-8</code> encoded, and flushes, if specified.
     *
     * Note that once the response is flushed, no header can be added or changed anymore.
     */
    public void sendParseHtml(String html, boolean flush);

    /**
     * Flushes the current response. If not already specified,
     * a default <code>status code</code> and <code>Content-Type</code> will be added.
     *
     * Note that once the response is flushed, no header can be added or changed anymore.
     */
    public void flush();

    /**
     * Flushes the current response. If not already specified on the response,
     * a default <code>status code</code> and <code>content-type</code> will be added.
     *
     * Note that once the response is flushed, no header can be added or changed anymore.
     *
     * @param close If <code>true</code>, the response will be closed and no more data
     * can be sent. This has the same effect than calling {@link #end()}.
     */
    public void flush(boolean close);

    /**
     * Sets the response's <code>status code</code> to use. Uses <code>200</code>
     * by default.
     *
     * Note that this <code>status code</code> can be changed automatically
     * if an exception is thrown.
     */
    public ResponseRequestContextAddon<R> setStatusCode(int statusCode);

    /**
     * The current <code>status code</code> sent or to be send.
     */
    public int getStatusCode();

    /**
     * The <code>Content-Type</code> header to use for the response. Most
     * <code>sendXXX()</code> methods will set this automatically.
     */
    public ResponseRequestContextAddon<R> setContentType(String responseContentType);

    /**
     * The current <code>Content-Type</code> sent or to be send.
     */
    public String getContentType();

    /**
     * Sets a temporarily redirection header (302) to the
     * current page.
     * <p>
     * This will NOT close the response and will NOT skip the remaining handlers!
     * Throw a <code>RedirectException</code> instead if you want to redirect the user
     * immediately.
     */
    public void redirect();

    /**
     * Sets a temporarily redirection header (302) to the
     * current page, with a Flash message to display.
     * <p>
     * This will NOT close the response and will NOT skip the remaining handlers!
     * Throw a <code>RedirectException</code> instead if you want to redirect the user
     * immediately.
     */
    public void redirect(FlashMessage flashMessage);

    /**
     * Sets a temporarily redirection header (302) to the
     * current page, with a Flash message to display.
     * <p>
     * This will NOT close the response and will NOT skip the remaining handlers!
     * Throw a <code>RedirectException</code> instead if you want to redirect the user
     * immediately.
     */
    public void redirect(FlashMessageLevel flashMessageType, String flashMessageText);

    /**
     * Sets a temporarily redirection header (302) to the
     * current page, with a Flash message to display.
     * <p>
     * This will NOT close the response and will NOT skip the remaining handlers!
     * Throw a <code>RedirectException</code> instead if you want to redirect the user
     * immediately.
     */
    public void redirect(FlashMessageLevel flashMessageType, String flashMessageText, JsonObject flashMessageVariables);

    /**
     * Sets a temporarily redirection header (302).
     * <p>
     * This will NOT close the response and will NOT skip the remaining handlers!
     * Throw a <code>RedirectException</code> instead if you want to redirect the user
     * immediately.
     */
    public void redirect(String newUrl);

    /**
     * Sets a temporarily redirection header (302),
     * with a Flash message to display.
     * <p>
     * This will NOT close the response and will NOT skip the remaining handlers!
     * Throw a <code>RedirectException</code> instead if you want to redirect the user
     * immediately.
     *
     * @param flashMessage A Flash Message to pass to the target page.
     */
    public void redirect(String newUrl, FlashMessage flashMessage);

    /**
     * Sets a temporarily redirection header (302),
     * with a Flash message to display.
     * <p>
     * This will NOT close the response and will NOT skip the remaining handlers!
     * Throw a <code>RedirectException</code> instead if you want to redirect the user
     * immediately.
     *
     * @param flashMessageType The type of a Flash Message to pass to the target page.
     * @param flashMessageText The text of a Flash Message to pass to the target page.
     */
    public void redirect(String newUrl, FlashMessageLevel flashMessageType, String flashMessageText);

    /**
     * Sets a temporarily redirection header (302),
     * with a Flash message to display.
     * <p>
     * This will NOT close the response and will NOT skip the remaining handlers!
     * Throw a <code>RedirectException</code> instead if you want to redirect the user
     * immediately.
     *
     * @param flashMessageType The type of a Flash Message to pass to the target page.
     * @param flashMessageText The text of a Flash Message to pass to the target page.
     * @param flashMessageVariables The variables of a Flash Message to pass to the target page.
     */
    public void redirect(String newUrl,
                         FlashMessageLevel flashMessageType,
                         String flashMessageText,
                         JsonObject flashMessageVariables);

    /**
     * Sets a temporarily redirection header (302),
     * with a Flash variables.
     * <p>
     * This will NOT close the response and will NOT skip the remaining handlers!
     * Throw a <code>RedirectException</code> instead if you want to redirect the user
     * immediately.
     *
     * @param flashMessageVariables The variables of a Flash Message to pass to the target page.
     */
    public void redirect(String newUrl, JsonObject flashMessageVariables);

    /**
     * Sets a redirection header.
     * <p>
     * This will NOT close the response and will NOT skip the remaining handlers!
     * Throw a <code>RedirectException</code> instead if you want to redirect the user
     * immediately.
     *
     * @param permanently If <code>true</code>, a
     * <code>301</code> header will be sent. If <code>false</code>,
     * <code>302</code> will be sent.
     */
    public void redirect(String newUrl, boolean permanently);

    /**
     * Sets a redirection header, with a Flash message to display.
     * <p>
     * This will NOT close the response and will NOT skip the remaining handlers!
     * Throw a <code>RedirectException</code> instead if you want to redirect the user
     * immediately.
     *
     * @param permanently If <code>true</code>, a
     * <code>301</code> header will be sent. If <code>false</code>,
     * <code>302</code> will be sent.
     *
     * @param flashMessage A Flash Message to pass to the target page.
     */
    public void redirect(String newUrl, boolean permanently, FlashMessage flashMessage);

    /**
     * Sets a redirection header, with a Flash message to display.
     * <p>
     * This will NOT close the response and will NOT skip the remaining handlers!
     * Throw a <code>RedirectException</code> instead if you want to redirect the user
     * immediately.
     *
     * @param permanently If <code>true</code>, a
     * <code>301</code> header will be sent. If <code>false</code>,
     * <code>302</code> will be sent.
     *
     * @param flashMessageType The type of a Flash Message to pass to the target page.
     * @param flashMessageText The text of a Flash Message to pass to the target page.
     */
    public void redirect(String newUrl, boolean permanently, FlashMessageLevel flashMessageType, String flashMessageText);

    /**
     * Sets a redirection header, with a Flash message to display.
     * <p>
     * This will NOT close the response and will NOT skip the remaining handlers!
     * Throw a <code>RedirectException</code> instead if you want to redirect the user
     * immediately.
     *
     * @param permanently If <code>true</code>, a
     * <code>301</code> header will be sent. If <code>false</code>,
     * <code>302</code> will be sent.
     *
     * @param flashMessageType The type of a Flash Message to pass to the target page.
     * @param flashMessageText The text of a Flash Message to pass to the target page.
     * @param flashMessageVariables The variables of a Flash Message to pass to the target page.
     */
    public void redirect(String newUrl, boolean permanently, FlashMessageLevel flashMessageType, String flashMessageText,
                         JsonObject flashMessageVariables);

    /**
     * Sets the redirection headers with a specified <code>3xx</code> status code.
     * <p>
     * This will NOT close the response and will NOT skip the remaining handlers!
     * Throw a <code>RedirectException</code> instead if you want to redirect the user
     * immediately.
     */
    public void redirect(String newUrl, int specific3xxCode);

    /**
     * Sets the redirection headers with a specified <code>3xx</code> status code,
     * and a Flash message to display.
     * <p>
     * This will NOT close the response and will NOT skip the remaining handlers!
     * Throw a <code>RedirectException</code> instead if you want to redirect the user
     * immediately.
     *
     * @param flashMessage A Flash Message to pass to the target page.
     */
    public void redirect(String newUrl, int specific3xxCode, FlashMessage flashMessage);

    /**
     * Sets the redirection headers with a specified <code>3xx</code> status code,
     * and a Flash message to display.
     * <p>
     * This will NOT close the response and will NOT skip the remaining handlers!
     * Throw a <code>RedirectException</code> instead if you want to redirect the user
     * immediately.
     *
     * @param flashMessageType The type of a Flash Message to pass to the target page.
     * @param flashMessageText The text of a Flash Message to pass to the target page.
     */
    public void redirect(String newUrl, int specific3xxCode, FlashMessageLevel flashMessageType, String flashMessageText);

    /**
     * Sets the redirection headers with a specified <code>3xx</code> status code,
     * and a Flash message to display.
     * <p>
     * This will NOT close the response and will NOT skip the remaining handlers!
     * Throw a <code>RedirectException</code> instead if you want to redirect the user
     * immediately.
     *
     * @param flashMessageType The type of a Flash Message to pass to the target page.
     * @param flashMessageText The text of a Flash Message to pass to the target page.
     * @param flashMessageVariables The variables of a Flash Message to pass to the target page.
     */
    public void redirect(String newUrl, int specific3xxCode, FlashMessageLevel flashMessageType, String flashMessageText,
                         JsonObject flashMessageVariables);

    /**
     * Adds a value to a response header. If the header already
     * has values, the new one is added.
     *
     * If the value is <code>null</code>, it won't be added.
     */
    public ResponseRequestContextAddon<R> addHeaderValue(String name, String value);

    /**
     * Adds a list of values to a response header. If the header already
     * has values, the new ones are added.
     *
     * If the values are <code>null</code>, nothing will be added.
     */
    public ResponseRequestContextAddon<R> addHeaderValues(String name, List<String> values);

    /**
     * Set the value to a response header. If the header already
     * exists, its current values are <i>overwritten</i>.
     *
     * If the value is <code>null</code>, the header will be
     * removed (same behavior as {@link #removeHeader(String) removeHeader(String name)})
     */
    public ResponseRequestContextAddon<R> setHeader(String name, String value);

    /**
     * Set multiple values to a response header. If the header already
     * exists, its current values are <i>overwritten</i>.
     *
     * If the lists is <code>null</code> or empty, the header will be
     * removed (same behavior as {@link #removeHeader(String) removeHeader(String name)})
     */
    public ResponseRequestContextAddon<R> setHeader(String name, List<String> values);

    /**
     * Removes an header by its name.
     */
    public ResponseRequestContextAddon<R> removeHeader(String name);

    /**
     * The currently set response headers.
     *
     * The map is mutable! Also the returned implementation is a
     * <code>TreeMap</code> with <i>case insensitive</i> keys.
     */
    public Map<String, List<String>> getHeaders();

    /**
     * The values of a specific response header.
     *
     * The list is mutable!
     * The name is <i>case insensitive</i>.
     *
     * Returns an empty list if the header was not found.
     */
    public List<String> getHeader(String name);

    /**
     * The first value of a specific header.
     *
     * The name is <i>case insensitive</i>.
     *
     * Returns <code>null</code> if the header is not found.
     */
    public String getHeaderFirst(String name);

    /**
     * Clears the buffer (the <i>unsent</i> buffer, of course).
     */
    public ResponseRequestContextAddon<R> resetBuffer();

    /**
     * Clears the buffer (the <i>unsent</i> buffer, of course!), resets the cookies,
     * the headers, the <code>Content-Type</code> and
     * sets the <code>status code</code> back to <code>200</code>.
     */
    public ResponseRequestContextAddon<R> resetEverything();

    /**
     * Clears the buffer (the <i>unsent</i> buffer, of course),
     * the headers, the <code>Content-Type</code> and
     * sets the <code>status code</code> back to <code>200</code>.
     *
     * @param resetCookies if <code>true</code>, cookies that have been
     * added on the current response will be reset too.
     */
    public ResponseRequestContextAddon<R> resetEverything(boolean resetCookies);

    /**
     * Gets the current unsent bytes (AKA the buffer).
     */
    public byte[] getUnsentBytes();

    /**
     * Gets the current unsent characters (AKA the buffer), using the
     * <code>UTF-8</code> encoding.
     */
    public String getUnsentCharacters();

    /**
     * Gets the current unsent characters (AKA the buffer), using the
     * specified encoding.
     */
    public String getUnsentCharacters(String encoding);

    /**
     * Enable or disable gzipping of the response.
     * The default is <code>GzipOption.DEFAULT</code> which
     * will gzip the response only for some <code>Content-Types</code>
     * and only if a <code>gzip 'Accept-Encoding'</code> header
     * has been received from the client.
     */
    public ResponseRequestContextAddon<R> setGzipOption(GzipOption gzipOption);

    /**
     * The currently set gzip options.
     */
    public GzipOption getGzipOption();

    /**
     * Adds caching headers.
     *
     * @param seconds the number of seconds this response should be cached.
     */
    public ResponseRequestContextAddon<R> setCacheSeconds(int cacheSeconds);

    /**
     * Adds caching headers for the specified number
     * of seconds.
     *
     * Adds caching headers.
     *
     * @param seconds the number of seconds this response should be cached.
     * @param isPrivateCache if <code>true</code>, the cache will
     * be flagged as "private".
     */
    public ResponseRequestContextAddon<R> setCacheSeconds(int cacheSeconds, boolean isPrivateCache);

    /**
     * Adds an Alert that is, in general, displayed at the top
     * of the page to show the user an Error, a Warning or a
     * confirmation message.
     * <p>
     * Those alerts are available using the "spincast.alerts"
     * templating variable. If any, a <code>Flash Messages</code> is also
     * added to this variable.
     */
    public void addAlert(AlertLevel level, String message);

    /**
     * Gets the cookies already added to the response as a Map,
     * using their names as the keys.
     * <p>
     * NOTE : use the {@link RequestRequestContextAddon#getCookiesValues()}
     * from the <code>request()</code> add-on instead
     * to get the cookies sent by the user!
     */
    public Map<String, Cookie> getCookiesAdded();

    /**
     * Gets a cookie already added to the response, by its name.
     * <p>
     * NOTE : use the {@link RequestRequestContextAddon#getCookieValue(String)}
     * from the <code>request()</code> add-on instead
     * to get a cookie sent by the user!
     *
     * @return the cookie or <code>null</code> if not found.
     */
    public Cookie getCookieAdded(String name);

    /**
     * Creates a cookie using the given name (<code>null</code> value).
     * <p>
     * By default, the public host ({@link SpincastConfig#getPublicServerHost()})
     * is uses as the cookie's <code>domain</code>
     * and the cookie is valid for the time
     * of the session only.
     * <p>
     * You have to add the cookie using the
     * {@link #setCookie(Cookie)} method after it is
     * properly created.
     */
    public Cookie createCookie(@Assisted("name") String name);

    /**
     * Sets a cookie.
     */
    public void setCookie(Cookie cookie);

    /**
     * Sets a cookie using the specified name and value.
     * <p>
     * By default, the public host ({@link SpincastConfig#getPublicServerHost()})
     * is uses as the cookie's <code>domain</code>
     * and the cookie is valid for the time of the session only.
     */
    public void setCookieSession(String name, String value);

    /**
     * Sets a cookie using the specified name and value.
     * The cookie will be "secure", "httpOnly"
     * and a {@link CookieSameSite} value of {@link CookieSameSite#LAX}.
     * <p>
     * By default, the public host ({@link SpincastConfig#getPublicServerHost()})
     * is uses as the cookie's <code>domain</code>
     * and the cookie is valid for the time of the session only.
     */
    public void setCookieSessionSafe(String name, String value);

    /**
     * Sets a cookie using the specified name, value
     * and number of seconds to live.
     * <p>
     * By default, the public host ({@link SpincastConfig#getPublicServerHost()})
     * is uses as the cookie's <code>domain</code>.
     */
    public void setCookie(String name, String value, int nbrSecondsToLive);

    /**
     * Sets a cookie using the specified name, value,
     * number of seconds to live and if it's http only.
     * <p>
     * By default, the public host ({@link SpincastConfig#getPublicServerHost()})
     * is uses as the cookie's <code>domain</code>.
     */
    public void setCookie(String name, String value, int nbrSecondsToLive, boolean httpOnly);

    /**
     * Sets a permanent cookie (1 years) using the specified
     * name and value.
     * <p>
     * By default, the public host ({@link SpincastConfig#getPublicServerHost()})
     * is uses as the cookie's <code>domain</code>.
     */
    public void setCookie1year(String name, String value);

    /**
     * Sets a permanent cookie (1 years) using the specified
     * name and value. The cookie will be "secure", "httpOnly"
     * and a {@link CookieSameSite} value of {@link CookieSameSite#LAX}.
     * <p>
     * By default, the public host ({@link SpincastConfig#getPublicServerHost()})
     * is uses as the cookie's <code>domain</code>.
     */
    public void setCookie1yearSafe(String name, String value);

    /**
     * Sets a permanent cookie (10 years) using the specified
     * name and value.
     * <p>
     * By default, the public host ({@link SpincastConfig#getPublicServerHost()})
     * is uses as the cookie's <code>domain</code>.
     */
    public void setCookie10years(String name, String value);

    /**
     * Sets a permanent cookie (10 years) using the specified
     * name and value. The cookie will be "secure", "httpOnly"
     * and a {@link CookieSameSite} value of {@link CookieSameSite#LAX}.
     * <p>
     * By default, the public host ({@link SpincastConfig#getPublicServerHost()})
     * is uses as the cookie's <code>domain</code>.
     */
    public void setCookie10yearsSafe(String name, String value);

    /**
     * Sets a cookie, using all available configurations.
     */
    public void setCookie(String name,
                          String value,
                          String path,
                          String domain,
                          Date expires,
                          boolean secure,
                          boolean httpOnly,
                          CookieSameSite cookieSameSite,
                          boolean discard,
                          int version);

    /**
     * Deletes a cookie. In fact, this sets the cookie's <code>expires date</code> in the
     * past so the user's browser will remove it.
     * <code>isExpired()</code> will return <code>true</code> after you called
     * this method.
     */
    public void deleteCookie(String name);

    /**
     * Deletes <strong>all</strong> cookies! Not only
     * the new cookies that have been added to the response, but
     * also the cookies that have been received in the request!
     * <p>
     * In fact, this method sets the <code>expires date</code> of the
     * cookies in the past so the user's browser will remove them.
     */
    public void deleteAllCookiesUserHas();

    /**
     * Adds a {@link Form} object to the response's
     * model and links its validation messages to the
     * default "validation" root element.
     * <p>
     * In other words, adding a form will result in those
     * elements to the response's model :
     * <ul>
     * <li>
     * [formName] => the form itself
     * </li>
     * <li>
     * [validation] => validation messages for the form
     * with keys such as "[formName].something" and
     * "[formName].somethingElse".
     * </li>
     * </ul>
     */
    public void addForm(Form form);

    /**
     * Adds a {@link Form} object to the response's
     * model and links its validation messages to the
     * specified validation element.
     * <p>
     * In other words, adding a form will result in those
     * elements to the response's model :
     * <ul>
     * <li>
     * [formName] => the form itself
     * </li>
     * <li>
     * [validationElementName] => validation messages for the form
     * with keys such as "[formName].something" and
     * "[formName].somethingElse".
     * </li>
     * </ul>
     */
    public void addForm(Form form, String validationElementName);

    /**
     * If <code>HTTP/2</code> is used, you can <a href="https://en.wikipedia.org/wiki/HTTP/2_Server_Push">push extra resources</a>
     * at the same time you response to a request.
     * <p>
     * If the embedded server deals with a HTTTP/2 request,
     * it will push the extra resources by itself. If it
     * deals with an HTTP/1.X request (for example if it
     * is behind a reverse-proxy) it will send <code>Link</code>
     * headers to the potential proxy in front of it and it
     * is the proxy that will be in charge of doing the actual push.
     * <p>
     * Beware that pushing resources does not always result in
     * an increase of performance and may lead to wasted bandwidth
     * (the client may decide to not use those pushed resources).
     *
     * @param path The absolute path to the resource to push, starting
     *             with "/" (or it will be added). No scheme, host, port
     *             but may contain a querystring.
     * @param requestHeaders The headers for requesting the resource. May be <code>null</code>.
     *                       Those headers will only be used if the embedded server pushes the
     *                       resources by itself. If it is behind a reverse-proxy and ask this
     *                       proxy to push the resources, those headers won't be used but
     *                       a "<a href="https://www.w3.org/TR/preload/#as-attribute">as=</a>"
     *                       attribute may be added to help the proxy serve the proper content-type.
     */
    public ResponseRequestContextAddon<R> push(HttpMethod httpMethod,
                                               String path,
                                               Map<String, List<String>> requestHeaders);

}
