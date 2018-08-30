package org.spincast.plugins.response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.config.SpincastConstants;
import org.spincast.core.config.SpincastConstants.ResponseModelVariables;
import org.spincast.core.cookies.Cookie;
import org.spincast.core.cookies.CookieFactory;
import org.spincast.core.cookies.CookieSameSite;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.exchange.ResponseRequestContextAddon;
import org.spincast.core.flash.FlashMessage;
import org.spincast.core.flash.FlashMessageFactory;
import org.spincast.core.flash.FlashMessageLevel;
import org.spincast.core.flash.FlashMessagesHolder;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.request.Form;
import org.spincast.core.response.Alert;
import org.spincast.core.response.AlertDefault;
import org.spincast.core.response.AlertLevel;
import org.spincast.core.routing.ETagFactory;
import org.spincast.core.server.Server;
import org.spincast.core.utils.Bool;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.GzipOption;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.core.xml.XmlManager;
import org.spincast.shaded.org.apache.commons.io.IOUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.commons.lang3.time.DateUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.shaded.org.apache.http.client.utils.URIBuilder;

import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;

public class SpincastResponseRequestContextAddon<R extends RequestContext<?>>
                                                implements ResponseRequestContextAddon<R> {

    protected final Logger logger = LoggerFactory.getLogger(SpincastResponseRequestContextAddon.class);

    protected static final boolean IS_RESPONSE_CHARACTERS_BASED_BY_DEFAULT = false;

    private final R requestContext;
    private final Server server;
    private final JsonManager jsonManager;
    private final XmlManager xmlManager;
    private final SpincastConfig spincastConfig;
    private final SpincastUtils spincastUtils;
    private final ETagFactory etagFactory;
    private final FlashMessagesHolder flashMessagesHolder;
    private final FlashMessageFactory flashMessageFactory;
    private final CookieFactory cookieFactory;

    private String responseContentType = null;
    private int responseStatusCode = HttpStatus.SC_OK;
    private final ByteArrayOutputStream byteArrayOutputStreamIn = new ByteArrayOutputStream(256);
    private final ByteArrayOutputStream byteArrayOutputStreamOut = new ByteArrayOutputStream(256);
    private GZIPOutputStream gzipOutputStream = null;
    private Bool isShouldGzip = null;

    private String charactersCharsetName = "UTF-8";
    private boolean isResponseCharactersBased = IS_RESPONSE_CHARACTERS_BASED_BY_DEFAULT;
    private boolean requestSizeValidated = false;
    private Map<String, List<String>> headers;
    private GzipOption gzipOption = GzipOption.DEFAULT;
    private Map<String, Cookie> cookies;

    private JsonObject responseModel;

    @Inject
    public SpincastResponseRequestContextAddon(R requestContext,
                                               Server server,
                                               JsonManager jsonManager,
                                               XmlManager xmlManager,
                                               SpincastConfig spincastConfig,
                                               SpincastUtils spincastUtils,
                                               ETagFactory etagFactory,
                                               FlashMessagesHolder flashMessagesHolder,
                                               FlashMessageFactory flashMessageFactory,
                                               CookieFactory cookieFactory) {
        this.requestContext = requestContext;
        this.server = server;
        this.jsonManager = jsonManager;
        this.xmlManager = xmlManager;
        this.spincastConfig = spincastConfig;
        this.spincastUtils = spincastUtils;
        this.etagFactory = etagFactory;
        this.flashMessagesHolder = flashMessagesHolder;
        this.flashMessageFactory = flashMessageFactory;
        this.cookieFactory = cookieFactory;
    }

    protected R getRequestContext() {
        return this.requestContext;
    }

    protected Server getServer() {
        return this.server;
    }

    protected Object getExchange() {
        return getRequestContext().exchange();
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected XmlManager getXmlManager() {
        return this.xmlManager;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected ETagFactory getEtagFactory() {
        return this.etagFactory;
    }

    protected FlashMessagesHolder getFlashMessagesHolder() {
        return this.flashMessagesHolder;
    }

    protected FlashMessageFactory getFlashMessageFactory() {
        return this.flashMessageFactory;
    }

    protected CookieFactory getCookieFactory() {
        return this.cookieFactory;
    }

    protected ByteArrayOutputStream getBuffer() {
        return this.byteArrayOutputStreamIn;
    }

    protected ByteArrayOutputStream getOut() {
        return this.byteArrayOutputStreamOut;
    }

    @Override
    public JsonObject getModel() {
        if (this.responseModel == null) {
            this.responseModel = getJsonManager().create();
        }
        return this.responseModel;
    }

    public GZIPOutputStream getGzipBuffer() {
        try {
            if (this.gzipOutputStream == null) {
                this.gzipOutputStream = new GZIPOutputStream(getOut(), true);
            }
            return this.gzipOutputStream;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected boolean isRequestSizeValidated() {
        return this.requestSizeValidated;
    }

    protected void setRequestSizeValidated(boolean requestSizeValidated) {
        this.requestSizeValidated = requestSizeValidated;
    }

    @Override
    public ResponseRequestContextAddon<R> setGzipOption(GzipOption gzipOption) {

        if (gzipOption == null) {
            gzipOption = GzipOption.DEFAULT;
        }

        if (isHeadersSent() && gzipOption != getGzipOption()) {
            this.logger.warn("The headers are sent, you can't change the gzip options.");
            return this;
        }

        this.gzipOption = gzipOption;

        return this;
    }

    @Override
    public GzipOption getGzipOption() {
        return this.gzipOption;
    }

    @Override
    public int getStatusCode() {
        return this.responseStatusCode;
    }

    @Override
    public ResponseRequestContextAddon<R> setStatusCode(int responseStatusCode) {

        if (isHeadersSent()) {
            if (responseStatusCode != getStatusCode()) {
                this.logger.warn("Response headers already sent, the http status code can't be changed...");
            }
        } else {
            this.responseStatusCode = responseStatusCode;
        }

        return this;
    }

    @Override
    public String getContentType() {
        return this.responseContentType;
    }

    @Override
    public ResponseRequestContextAddon<R> setContentType(String responseContentType) {

        if (isHeadersSent()) {
            if (responseContentType != null && !responseContentType.equals(getContentType())) {
                this.logger.warn("Response headers already sent, the content-type can't be changed...");
            }
        } else {

            if ((responseContentType != null && !responseContentType.equals(getContentType())) ||
                (responseContentType == null && getContentType() != null)) {

                //==========================================
                // isShouldGzip will be revalidated since it can
                // change depending of the content-type.
                //==========================================
                GzipOption gzipOption = getGzipOption();
                if (gzipOption == GzipOption.DEFAULT) {
                    this.isShouldGzip = null;
                }
            }
            this.responseContentType = responseContentType;
        }

        return this;
    }

    /**
     * Try to determine is the response is characters based or not.
     * This is allow us to use a default Content-Type header if none
     * was specified.
     */
    protected boolean isResponseCharactersBased() {
        return this.isResponseCharactersBased;
    }

    @Override
    public boolean isClosed() {
        return getServer().isResponseClosed(getExchange());
    }

    @Override
    public void redirect() {
        redirect("", false, null);
    }

    @Override
    public void redirect(FlashMessage flashMessage) {
        redirect("", false, flashMessage);
    }

    @Override
    public void redirect(FlashMessageLevel flashMessageType, String flashMessageText) {
        redirect(flashMessageType, flashMessageText, null);
    }

    @Override
    public void redirect(FlashMessageLevel flashMessageType, String flashMessageText, JsonObject flashMessageVariables) {
        redirect(getFlashMessageFactory().create(flashMessageType, flashMessageText, flashMessageVariables));
    }

    @Override
    public void redirect(String newUrl) {
        redirect(newUrl, false, null);
    }

    @Override
    public void redirect(String newUrl, FlashMessage flashMessage) {
        redirect(newUrl, false, flashMessage);
    }

    @Override
    public void redirect(String newUrl, FlashMessageLevel flashMessageType, String flashMessageText) {
        redirect(newUrl, flashMessageType, flashMessageText, null);
    }

    @Override
    public void redirect(String newUrl, FlashMessageLevel flashMessageType, String flashMessageText,
                         JsonObject flashMessageVariables) {
        redirect(newUrl, getFlashMessageFactory().create(flashMessageType, flashMessageText, flashMessageVariables));
    }

    @Override
    public void redirect(String newUrl, boolean permanently) {
        redirect(newUrl, permanently, null);
    }

    @Override
    public void redirect(String newUrl, boolean permanently, FlashMessage flashMessage) {
        if (permanently) {
            redirect(newUrl, HttpStatus.SC_MOVED_PERMANENTLY, flashMessage);
        } else {
            redirect(newUrl, HttpStatus.SC_MOVED_TEMPORARILY, flashMessage);
        }
    }

    @Override
    public void redirect(String newUrl, boolean permanently, FlashMessageLevel flashMessageType, String flashMessageText) {
        redirect(newUrl, permanently, getFlashMessageFactory().create(flashMessageType, flashMessageText));
    }

    @Override
    public void redirect(String newUrl, boolean permanently, FlashMessageLevel flashMessageType, String flashMessageText,
                         JsonObject flashMessageVariables) {
        redirect(newUrl, permanently, getFlashMessageFactory().create(flashMessageType, flashMessageText, flashMessageVariables));
    }

    @Override
    public void redirect(String newUrl, int specific3xxCode) {
        redirect(newUrl, specific3xxCode, null);
    }

    @Override
    public void redirect(String newUrl, int specific3xxCode, FlashMessageLevel flashMessageType, String flashMessageText) {
        redirect(newUrl, specific3xxCode, getFlashMessageFactory().create(flashMessageType, flashMessageText));
    }

    @Override
    public void redirect(String newUrl, int specific3xxCode, FlashMessageLevel flashMessageType, String flashMessageText,
                         JsonObject flashMessageVariables) {
        redirect(newUrl,
                 specific3xxCode,
                 getFlashMessageFactory().create(flashMessageType, flashMessageText, flashMessageVariables));
    }

    @Override
    public void redirect(String newUrl, int specific3xxCode, FlashMessage flashMessage) {

        try {
            if (isHeadersSent()) {
                throw new RuntimeException("Can't set redirect, the headers are already sent.");
            }

            setStatusCode(specific3xxCode);

            //==========================================
            // If the URL to redirect to is empty, we
            // redirect to the current page.
            //==========================================
            if (StringUtils.isBlank(newUrl)) {
                newUrl = getRequestContext().request().getFullUrlOriginal();
            } else {
                newUrl = newUrl.trim();

                // @see http://stackoverflow.com/a/12840255/843699
                if (newUrl.startsWith("//")) {
                    newUrl = getServer().getRequestScheme(getExchange()) + ":" + newUrl;
                }
            }

            URI uri = new URI(newUrl);
            if (!uri.isAbsolute()) {

                URI currentUri = new URI(getRequestContext().request().getFullUrl());

                String anchor = null;
                if (uri.getFragment() != null) {
                    anchor = uri.getFragment();
                }

                String queryString = null;
                if (uri.getQuery() != null) {
                    queryString = uri.getQuery();
                }

                //==========================================
                // Simple anchor or queryString?
                // We use the current path.
                //==========================================
                String path = uri.getPath();
                if (newUrl.startsWith("#") || newUrl.startsWith("?")) {
                    path = currentUri.getPath();
                }
                //==========================================
                // Relative path? 
                // We happen it to the current path.
                //==========================================
                else if (!newUrl.startsWith("/")) {

                    String currentPath = currentUri.getPath();
                    currentPath = StringUtils.strip(currentPath, "/");

                    int lastSlashPos = currentPath.lastIndexOf("/");
                    if (lastSlashPos < 0) {
                        path = "/" + path;
                    } else {
                        path = "/" + currentPath.substring(0, lastSlashPos) + "/" + path;
                    }
                }

                uri = new URI(currentUri.getScheme(),
                              currentUri.getUserInfo(),
                              currentUri.getHost(),
                              currentUri.getPort(),
                              path,
                              queryString,
                              anchor);

                newUrl = uri.toString();
            }

            //==========================================
            // Flash message to use?
            //==========================================
            if (flashMessage != null) {
                newUrl = saveFlashMessage(newUrl, flashMessage);
            }

            setHeader(HttpHeaders.LOCATION, newUrl);

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    /**
     * Saves a Flash message.
     * 
     * Returned a modified version of the final URL to redirect to,
     * if required.
     */
    protected String saveFlashMessage(String url, FlashMessage flashMessage) {

        if (flashMessage == null) {
            return url;
        }

        String flashMessageId = getFlashMessagesHolder().saveFlashMessage(flashMessage);

        //==========================================
        // TODO Maybe we should use a user session.
        //==========================================
        if (getRequestContext().request().isCookiesEnabledValidated()) {
            getRequestContext().response().setCookieSession(getSpincastConfig().getCookieNameFlashMessage(), flashMessageId);
            return url;

        //==========================================@formatter:off 
        // We add the id of the flash message to the
        // redirected URL since we don't know if cookies
        // are enabled or not.
        //==========================================@formatter:on
        } else {

            try {

                URIBuilder builder = new URIBuilder(url);
                builder.setParameter(getSpincastConfig().getQueryParamFlashMessageId(), flashMessageId);
                return builder.build().toString();

            } catch (Exception ex) {
                throw SpincastStatics.runtimize(ex);
            }
        }
    }

    @Override
    public void sendBytes(byte[] bytes) {
        sendBytes(bytes, null, false);
    }

    @Override
    public void sendBytes(byte[] bytes, String contentType) {
        sendBytes(bytes, contentType, false);
    }

    /**
     * Send some bytes + flush if specified.
     */
    @Override
    public void sendBytes(byte[] bytes, String contentType, boolean flush) {
        send(bytes, contentType, flush);
    }

    protected void send(byte[] bytes, String contentType, boolean flush) {

        if (isClosed()) {
            this.logger.debug("The response is closed, nothing more can be sent!");
            return;
        }

        if (isHeadersSent()) {
            if (contentType != null && !contentType.equals(getContentType())) {
                this.logger.warn("Response headers are already sent, the content-type won't be changed...");
            }
        } else {
            if (contentType != null) {
                if (getContentType() != null && !contentType.equals(getContentType())) {
                    this.logger.warn("The content-type is changed from " + getContentType() + " to " + contentType);
                }
                setContentType(contentType);
            }
        }

        if (bytes != null) {
            try {
                getBuffer().write(bytes);
            } catch (Exception ex) {
                throw SpincastStatics.runtimize(ex);
            }
        }

        if (flush) {
            flush();
        }
    }

    @Override
    public void sendCharacters(String content, String contentType) {
        sendCharacters(content, contentType, false);
    }

    @Override
    public void sendCharacters(String content, String contentType, boolean flush) {

        try {

            //==========================================
            // The response is probably characters based.
            // This will only help us set a default COntent-Type
            // header if none was set.
            //==========================================
            this.isResponseCharactersBased = true;

            byte[] bytes = null;
            if (content != null) {
                bytes = content.getBytes(getCharactersCharsetName());
            }
            send(bytes, contentType, flush);
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public String getCharactersCharsetName() {
        return this.charactersCharsetName;
    }

    @Override
    public ResponseRequestContextAddon<R> setCharactersCharsetName(String charactersCharsetName) {

        Objects.requireNonNull(charactersCharsetName, "charactersCharsetName can't be NULL");

        if (isHeadersSent() && !charactersCharsetName.equalsIgnoreCase(getCharactersCharsetName())) {
            this.logger.warn("Some data have already been send, it may not be a good idea to change the Charset now.");
        }
        this.charactersCharsetName = charactersCharsetName;

        return this;
    }

    @Override
    public void sendPlainText(String string) {
        sendPlainText(string, false);
    }

    @Override
    public void sendPlainText(String string, boolean flush) {
        sendCharacters(string, ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), flush);
    }

    @Override
    public void sendHtml(String html) {
        sendHtml(html, false);
    }

    @Override
    public void sendHtml(String string, boolean flush) {
        sendCharacters(string, ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), flush);
    }

    @Override
    public void sendParseHtml(String html) {
        sendParse(html, ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), false);
    }

    @Override
    public void sendParseHtml(String html, boolean flush) {
        sendParse(html, ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), flush);
    }

    @Override
    public void sendParse(String content, String contentType) {
        sendParse(content, contentType, false);
    }

    @Override
    public void sendParse(String content, String contentType, boolean flush) {

        if (StringUtils.isBlank(contentType)) {
            this.logger.warn("The Content-Type was not specified : 'text/html' will be used");
            contentType = ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset();
        }

        if (content == null) {
            content = "";
        }

        String evaluated = getRequestContext().templating().evaluate(content, getModel());
        sendCharacters(evaluated, contentType, flush);
    }

    @Override
    public void sendTemplateHtml(String templatePath) {
        sendTemplateHtml(templatePath, true, false);
    }

    @Override
    public void sendTemplateHtml(String templatePath, boolean isClasspathPath) {
        sendTemplateHtml(templatePath, isClasspathPath, false);
    }

    @Override
    public void sendTemplateHtml(String templatePath, boolean isClasspathPath, boolean flush) {
        sendTemplate(templatePath, isClasspathPath, ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), flush);
    }

    @Override
    public void sendTemplate(String templatePath, String contentType) {
        sendTemplate(templatePath, true, contentType, false);
    }

    @Override
    public void sendTemplate(String templatePath, String contentType, boolean flush) {
        sendTemplate(templatePath, true, contentType, flush);
    }

    @Override
    public void sendTemplate(String templatePath, boolean isClasspathPath, String contentType) {
        sendTemplate(templatePath, isClasspathPath, contentType, false);
    }

    @Override
    public void sendTemplate(String templatePath, boolean isClasspathPath, String contentType, boolean flush) {

        if (StringUtils.isBlank(contentType)) {

            contentType = getSpincastUtils().getMimeTypeFromPath(templatePath);
            if (contentType == null) {
                this.logger.warn("The Content-Type was not specified and can't be determined from the template path '" +
                                 templatePath +
                                 "': 'text/html' will be used");
                contentType = ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset();
            }
        }

        String evaluated = getRequestContext().templating().fromTemplate(templatePath, isClasspathPath, getModel());
        sendCharacters(evaluated, contentType, flush);
    }

    @Override
    public void sendJson() {
        sendJson(false);
    }

    @Override
    public void sendJson(boolean flush) {

        addAlertsToModel();

        String json = getJsonManager().toJsonString(getModel());
        sendCharacters(json, ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(), flush);
    }

    @Override
    public void sendJson(String jsonString) {
        sendJson(jsonString, false);
    }

    @Override
    public void sendJson(String jsonString, boolean flush) {
        sendCharacters(jsonString, ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(), flush);
    }

    @Override
    public void sendJson(Object obj) {
        sendJson(obj, false);
    }

    @Override
    public void sendJson(Object obj, boolean flush) {

        if (obj instanceof String) {
            sendJson((String)obj);
            return;
        }

        String json = getJsonManager().toJsonString(obj);
        sendCharacters(json, ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(), flush);
    }

    @Override
    public void sendXml() {
        sendXml(false);
    }

    @Override
    public void sendXml(boolean flush) {

        addAlertsToModel();

        String xml = getXmlManager().toXml(getModel());
        sendCharacters(xml, ContentTypeDefaults.XML.getMainVariationWithUtf8Charset(), flush);
    }

    @Override
    public void sendXml(String xml) {
        sendXml(xml, false);
    }

    @Override
    public void sendXml(String xml, boolean flush) {
        sendCharacters(xml, ContentTypeDefaults.XML.getMainVariationWithUtf8Charset(), flush);
    }

    @Override
    public void sendXml(Object obj) {
        sendXml(obj, false);
    }

    @Override
    public void sendXml(Object obj, boolean flush) {

        if (obj instanceof String) {
            sendXml((String)obj);
            return;
        }

        String xml = getXmlManager().toXml(obj);
        sendCharacters(xml, ContentTypeDefaults.XML.getMainVariationWithUtf8Charset(), flush);
    }

    protected void addAlertsToModel() {

        if (isAddAlertsToModel()) {

            Map<String, Object> map = getRequestContext().templating().getSpincastReservedMap();

            @SuppressWarnings("unchecked")
            List<Alert> alerts =
                    (List<Alert>)map.get(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_ALERTS);
            if (alerts != null && alerts.size() > 0) {

                JsonObject model = getModel();

                String spincastModelObjKey = getSpincastConfig().getSpincastModelRootVariableName();
                JsonObject spincastModelObj = model.getJsonObjectOrEmpty(spincastModelObjKey);
                model.set(spincastModelObjKey, spincastModelObj);

                JsonArray alertsArray =
                        spincastModelObj.getJsonArrayOrEmpty(ResponseModelVariables.DEFAULT_RESPONSE_MODEL_VAR_ALERTS);
                spincastModelObj.set(ResponseModelVariables.DEFAULT_RESPONSE_MODEL_VAR_ALERTS, alertsArray);

                for (Alert alert : alerts) {
                    alertsArray.add(alert);
                }
            }
        }
    }

    /**
     * Should Alert messages (and therefore Flash message)
     * be added to the model when sending this one as Json or XML?
     */
    protected boolean isAddAlertsToModel() {
        return true;
    }

    @Override
    public ResponseRequestContextAddon<R> resetBuffer() {

        try {
            getBuffer().reset();

            if (!isHeadersSent()) {
                this.isResponseCharactersBased = IS_RESPONSE_CHARACTERS_BASED_BY_DEFAULT;
            }
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }

        return this;
    }

    @Override
    public ResponseRequestContextAddon<R> resetEverything() {
        return resetEverything(true);
    }

    @Override
    public ResponseRequestContextAddon<R> resetEverything(boolean resetCookies) {

        resetBuffer();

        if (isHeadersSent()) {
            this.logger.warn("Response headers are already sent, the cookies, headers and status code won't be reset...");
        } else {
            if (resetCookies) {
                getCookiesAdded().clear();
            }

            getHeaders().clear();
            setContentType(null);
            setStatusCode(HttpStatus.SC_OK);
        }

        return this;
    }

    @Override
    public byte[] getUnsentBytes() {
        return getBuffer().toByteArray();
    }

    @Override
    public String getUnsentCharacters() {
        return getUnsentCharacters("UTF-8");
    }

    @Override
    public String getUnsentCharacters(String encoding) {
        try {
            byte[] unsentBytes = getUnsentBytes();
            return new String(unsentBytes, encoding);
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public ResponseRequestContextAddon<R> removeHeader(String name) {

        if (isHeadersSent()) {
            this.logger.warn("Response headers are already sent, can't change them...");
            return this;
        }

        getHeaders().remove(name);

        return this;
    }

    @Override
    public ResponseRequestContextAddon<R> setHeader(String name, String value) {

        if (isHeadersSent()) {
            this.logger.warn("Response headers are already sent, can't change them...");
            return this;
        }

        if (value == null) {
            removeHeader(name);
            return this;
        }
        setHeader(name, Arrays.asList(value));

        return this;
    }

    @Override
    public ResponseRequestContextAddon<R> setHeader(String name, List<String> values) {

        if (isHeadersSent()) {
            this.logger.warn("Response headers are already sent, can't change them...");
            return this;
        }

        if (values == null) {
            removeHeader(name);
            return this;
        }
        getHeaders().put(name, values);

        return this;
    }

    @Override
    public ResponseRequestContextAddon<R> addHeaderValue(String name, String value) {

        if (isHeadersSent()) {
            this.logger.warn("Response headers are already sent, can't change them...");
            return this;
        }

        if (value == null) {
            return this;
        }
        addHeaderValues(name, Arrays.asList(value));

        return this;
    }

    @Override
    public ResponseRequestContextAddon<R> addHeaderValues(String name, List<String> values) {

        if (isHeadersSent()) {
            this.logger.warn("Response headers are already sent, can't change them...");
            return this;
        }

        if (values == null) {
            return this;
        }
        List<String> currentValues = getHeaders().get(name);
        if (currentValues == null) {
            currentValues = new ArrayList<String>();
            getHeaders().put(name, currentValues);
        }
        currentValues.addAll(values);

        return this;
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        if (this.headers == null) {
            Map<String, List<String>> headersFromServer = getServer().getResponseHeaders(getExchange());

            // We use a TreeMap with String.CASE_INSENSITIVE_ORDER so the
            // "get" is case insensitive!
            TreeMap<String, List<String>> treeMap = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);
            if (headersFromServer != null) {
                treeMap.putAll(headersFromServer);
            }

            this.headers = treeMap;
        }
        return this.headers;
    }

    @Override
    public List<String> getHeader(String name) {
        if (StringUtils.isBlank(name)) {
            return new LinkedList<String>();
        }
        // This get is case insensitive.
        List<String> values = getHeaders().get(name);
        if (values == null) {
            values = new LinkedList<String>();
        }
        return values;
    }

    @Override
    public String getHeaderFirst(String name) {
        List<String> values = getHeader(name);
        if (values != null && values.size() > 0) {
            return values.get(0);
        }
        return null;
    }

    @Override
    public boolean isHeadersSent() {
        return getServer().isResponseHeadersSent(getExchange());
    }

    protected void setIsShouldGzip(boolean isShouldGzip) {

        GzipOption gzipOption = getGzipOption();
        if (gzipOption != GzipOption.DEFAULT) {
            this.logger.warn("Can't turn on/off the gzip feature since the GzipOption is " + gzipOption);
            return;
        }

        try {
            if (isHeadersSent()) {
                if (this.isShouldGzip != null && this.isShouldGzip.getBoolean() != isShouldGzip) {
                    this.logger.warn("Can't turn on/off the gzip feature since headers are already sent.");
                }
                return;
            }

            //==========================================
            // If we turn off the gziping, we have to
            // change the buffer.
            //==========================================
            if (this.isShouldGzip != null && this.isShouldGzip.getBoolean() && !isShouldGzip) {

                this.byteArrayOutputStreamIn.reset();

                getGzipBuffer().close();
                ByteArrayOutputStream buffer = getBuffer();
                if (buffer.size() > 0) {
                    GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(buffer.toByteArray()));
                    byte[] ungzipedBytes = IOUtils.toByteArray(gzipInputStream);
                    this.byteArrayOutputStreamIn.write(ungzipedBytes);
                }
                this.gzipOutputStream = null;
            }

            this.isShouldGzip = Bool.from(isShouldGzip);
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected boolean isShouldGzip() {

        //==========================================
        // The GzipOption has priority.
        //==========================================
        GzipOption gzipOption = getGzipOption();
        if (gzipOption == GzipOption.FORCE) {
            return true;
        } else if (gzipOption == GzipOption.DISABLE) {
            return false;
        } else if (gzipOption != GzipOption.DEFAULT) {
            throw new RuntimeException("Unimplemented : " + gzipOption);
        }

        if (this.isShouldGzip == null) {

            //==========================================
            // Check if there is a gzip 'Accept-Encoding' header 
            //==========================================
            boolean hasGzipAcceptHeader = false;
            List<String> acceptEncodings = getRequestContext().request().getHeader(HttpHeaders.ACCEPT_ENCODING);
            if (acceptEncodings != null) {
                for (String acceptEncoding : acceptEncodings) {
                    if (acceptEncoding == null) {
                        continue;
                    }
                    if (acceptEncoding.contains("gzip")) {
                        hasGzipAcceptHeader = true;
                        break;
                    }
                }
            }

            String responseContentType = getContentType();

            //==========================================
            // Try to guess the content-type
            //==========================================
            if (responseContentType == null) {
                String path = getRequestContext().request().getRequestPath();
                responseContentType = getSpincastUtils().getMimeTypeFromPath(path);
            }

            //==========================================
            // Check if its a content-type for which we
            // shouldn't use gzip.
            //==========================================
            if (responseContentType != null) {
                if (!getSpincastUtils().isContentTypeToSkipGziping(responseContentType)) {
                    if (!hasGzipAcceptHeader) {
                        this.logger.debug("No gzip 'Accept-Encoding' header, we won't gzip the response.");
                        setIsShouldGzip(false);
                    } else {
                        setIsShouldGzip(true);
                    }
                }
            } else {
                setIsShouldGzip(false);
            }
        }

        if (this.isShouldGzip == null) {
            return false;
        }
        return this.isShouldGzip.getBoolean();
    }

    @Override
    public void end() {
        flush(true);
    }

    @Override
    public void flush() {
        flush(false);
    }

    @Override
    public void flush(boolean close) {

        try {
            if (isClosed()) {
                return;
            }

            //==========================================
            // If we haven't read the request body yet, the
            // request size may not have been checked. So we
            // read it now to be able to send a 
            // "413 - Request entity too large" status if
            // required.
            //==========================================
            if (!isRequestSizeValidated() && !isHeadersSent()) {
                setRequestSizeValidated(true);
                boolean requestSizeOk = getServer().forceRequestSizeValidation(getExchange());
                if (!requestSizeOk) {
                    resetEverything();
                    setStatusCode(HttpStatus.SC_REQUEST_TOO_LONG);
                }
            }

            ByteArrayOutputStream buffer = getBuffer();

            //==========================================
            // Send the Headers!
            //==========================================
            if (!isHeadersSent()) {

                getServer().setResponseStatusCode(getExchange(), getStatusCode());

                String responseContentType = getContentType();
                if (responseContentType == null) {

                    String mimeType = getSpincastUtils().getMimeTypeFromPath(getRequestContext().request()
                                                                                                .getRequestPath());
                    if (mimeType != null) {
                        responseContentType = mimeType;
                    } else {
                        if (isResponseCharactersBased() || buffer.size() == 0) {
                            responseContentType = ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset();
                        } else {
                            responseContentType = ContentTypeDefaults.BINARY.getMainVariation();
                        }
                    }
                    setContentType(responseContentType);
                }

                setHeader(HttpHeaders.CONTENT_TYPE, responseContentType);

                //==========================================
                // Status code
                //==========================================
                getServer().setResponseStatusCode(getExchange(), getStatusCode());

                //==========================================
                // Nothing sent yet and we end the response, we can
                // send a Content-Length header!
                //==========================================
                if (close && !isShouldGzip()) {
                    setHeader(HttpHeaders.CONTENT_LENGTH, "" + buffer.size());
                }

                //==========================================
                // Add Cookies
                //==========================================

                //==========================================
                // Do we add the cookies validator?
                //==========================================
                if (getSpincastConfig().isEnableCookiesValidator()) {
                    setCookie(createCookiesValidatorCookie());
                }
                getServer().addCookies(getExchange(), getCookiesAdded());

                //==========================================
                // Gzip headers
                //==========================================
                if (isShouldGzip()) {
                    setHeader(HttpHeaders.CONTENT_ENCODING, "gzip");
                    setHeader(HttpHeaders.TRANSFER_ENCODING, "chunked");
                }

                //==========================================
                // Add the headers to the server
                //==========================================
                getServer().setResponseHeaders(getExchange(), getHeaders());
            }

            byte[] bytesToFlush;
            if (isShouldGzip()) {

                getGzipBuffer().write(buffer.toByteArray());

                //==========================================
                // Required for the bytes to reach the underlying
                // out.
                //==========================================
                getGzipBuffer().flush();

                //==========================================
                // We must close the GZIPOutputStream at the end
                // so the correct gzip "footer" is sent...
                //==========================================
                if (close) {
                    getGzipBuffer().close();
                }

                bytesToFlush = getOut().toByteArray();
                getOut().reset();

            } else {
                bytesToFlush = buffer.toByteArray();
            }
            buffer.reset();

            getServer().flushBytes(getExchange(), bytesToFlush, close);

        } catch (Exception ex) {
            this.logger.error("error with request " + getRequestContext().request().getFullUrl());
            throw SpincastStatics.runtimize(ex);
        }
    }

    private Cookie createCookiesValidatorCookie() {

        Cookie cookie = createCookie(getSpincastConfig().getCookiesValidatorCookieName());
        cookie.setValue("1");
        cookie.setExpiresUsingMaxAge(60 * 60 * 24 * 365);

        return cookie;
    }

    @Override
    public ResponseRequestContextAddon<R> setCacheSeconds(int cacheSeconds) {
        return setCacheSeconds(cacheSeconds, false);
    }

    @Override
    public ResponseRequestContextAddon<R> setCacheSeconds(int cacheSeconds, boolean isPrivateCache) {

        if (cacheSeconds <= 0) {
            this.logger.warn("A number of seconds below 1 doesn't send any cache headers: " + cacheSeconds);
            return this;
        }

        if (isHeadersSent()) {
            this.logger.error("The headers are sent, you can't add cache headers.");
            return this;
        }

        String cacheControl = "max-age=" + cacheSeconds;
        if (isPrivateCache) {
            cacheControl = "private, " + cacheControl;
        } else {
            cacheControl = "public, " + cacheControl;
        }

        setHeader(HttpHeaders.CACHE_CONTROL, cacheControl);

        return this;
    }

    @Override
    public void setModel(JsonObject model) {
        this.responseModel = model;
    }

    @Override
    public void addAlert(AlertLevel alertType, String alertText) {

        Map<String, Object> spincastMap = getRequestContext().templating().getSpincastReservedMap();

        @SuppressWarnings("unchecked")
        List<Alert> alerts =
                (List<Alert>)spincastMap.get(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_ALERTS);
        if (alerts == null) {
            alerts = new ArrayList<Alert>();
            spincastMap.put(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_ALERTS,
                            alerts);
        }

        alerts.add(createAlert(alertType, alertText));
    }

    protected Alert createAlert(AlertLevel alertType, String alertText) {
        return new AlertDefault(alertType, alertText);
    }

    @Override
    public Map<String, Cookie> getCookiesAdded() {
        if (this.cookies == null) {
            this.cookies = new HashMap<String, Cookie>();
        }
        return this.cookies;
    }

    @Override
    public Cookie getCookieAdded(String name) {
        return getCookiesAdded().get(name);
    }

    @Override
    public void setCookie(Cookie cookie) {

        boolean valid = validateCookie(cookie);
        if (!valid) {
            return;
        }

        getCookiesAdded().put(cookie.getName(), cookie);
    }

    @Override
    public void setCookieSession(String name, String value) {
        Cookie cookie = getCookieFactory().createCookie(name, value);
        setCookie(cookie);
    }

    @Override
    public void setCookieSessionSafe(String name, String value) {
        addCookieSafe(name, value, null);
    }

    @Override
    public void setCookie1year(String name, String value) {
        setCookie(name, value, 31536000);
    }

    @Override
    public void setCookie1yearSafe(String name, String value) {
        addCookieSafe(name, value, 31536000);
    }

    @Override
    public void setCookie10years(String name, String value) {
        setCookie(name, value, 315360000);
    }

    @Override
    public void setCookie10yearsSafe(String name, String value) {
        addCookieSafe(name, value, 315360000);
    }

    @Override
    public void setCookie(String name, String value, int nbrSecondsToLive) {
        Cookie cookie = getCookieFactory().createCookie(name, value);
        cookie.setExpiresUsingMaxAge(nbrSecondsToLive);
        setCookie(cookie);
    }

    @Override
    public void setCookie(String name, String value, int nbrSecondsToLive, boolean httpOnly) {
        Cookie cookie = getCookieFactory().createCookie(name, value);
        cookie.setExpiresUsingMaxAge(nbrSecondsToLive);
        cookie.setHttpOnly(httpOnly);
        setCookie(cookie);
    }

    protected void addCookieSafe(String name, String value, Integer nbrSecondsToLive) {
        Cookie cookie = getCookieFactory().createCookie(name, value);
        if (nbrSecondsToLive != null) {
            cookie.setExpiresUsingMaxAge(nbrSecondsToLive);
        }
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setSameSite(CookieSameSite.LAX);
        setCookie(cookie);
    }

    @Override
    public void setCookie(String name,
                          String value,
                          String path,
                          String domain,
                          Date expires,
                          boolean secure,
                          boolean httpOnly,
                          CookieSameSite cookieSameSite,
                          boolean discard,
                          int version) {
        Cookie cookie = getCookieFactory().createCookie(name,
                                                        value,
                                                        path,
                                                        domain,
                                                        expires,
                                                        secure,
                                                        httpOnly,
                                                        cookieSameSite,
                                                        discard,
                                                        version);
        setCookie(cookie);
    }

    protected boolean validateCookie(Cookie cookie) {
        Objects.requireNonNull(cookie, "Can't add a NULL cookie");

        String name = cookie.getName();
        if (StringUtils.isBlank(name)) {
            throw new RuntimeException("A cookie can't have an empty name");
        }
        return true;
    }

    @Override
    public void deleteCookie(String name) {
        if (name == null) {
            return;
        }

        Cookie cookie = createCookie(name);
        cookie.setExpires(DateUtils.addYears(new Date(), -1));
        setCookie(cookie);
    }

    @Override
    public void deleteAllCookiesUserHas() {

        getCookiesAdded().clear();

        for (String cookieName : getRequestContext().request().getCookiesValues().keySet()) {
            deleteCookie(cookieName);
        }
    }

    @Override
    public Cookie createCookie(String name) {
        return getCookieFactory().createCookie(name);
    }

    @Override
    public void addForm(Form form) {
        addForm(form, getSpincastConfig().getValidationElementDefaultName());
    }

    @Override
    public void addForm(Form form, String validationElementName) {
        getModel().set(form.getFormName(), form);

        Object validationElementObj = getModel().getObject(validationElementName);
        if (validationElementObj == null) {
            validationElementObj = getJsonManager().create();
            getModel().set(validationElementName, validationElementObj);
        } else if (!(validationElementObj instanceof JsonObject)) {
            throw new RuntimeException("The '" + validationElementName + "' element already exists on the response's model " +
                                       "but is not a JsonObject! It can't be used as the validation element : " +
                                       validationElementObj);
        }
        JsonObject validationElement = (JsonObject)validationElementObj;

        //==========================================
        // Tells the form to use that validation element
        // to strore validation messages.
        //==========================================
        form.setValidationObject(validationElement);

    }


}
