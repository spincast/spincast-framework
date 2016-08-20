package org.spincast.plugins.response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.exchange.IResponseRequestContextAddon;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.routing.IETagFactory;
import org.spincast.core.server.IServer;
import org.spincast.core.utils.Bool;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.GzipOption;
import org.spincast.core.utils.ISpincastUtils;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.xml.IXmlManager;
import org.spincast.shaded.org.apache.commons.io.IOUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;

public class SpincastResponseRequestContextAddon<R extends IRequestContext<?>>
                                                implements IResponseRequestContextAddon<R> {

    protected final Logger logger = LoggerFactory.getLogger(SpincastResponseRequestContextAddon.class);

    protected static final boolean IS_RESPONSE_CHARACTERS_BASED_BY_DEFAULT = false;

    private final R requestContext;
    private final IServer server;
    private final IJsonManager jsonManager;
    private final IXmlManager xmlManager;
    private final ISpincastConfig spincastConfig;
    private final ISpincastUtils spincastUtils;
    private final IETagFactory etagFactory;

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

    private List<String> globalErrors;
    private List<String> globalConfirms;
    private List<String> globalWarnings;

    private Map<String, List<IFieldMessage>> fieldsErrors;
    private Map<String, List<IFieldMessage>> fieldsConfirms;
    private Map<String, List<IFieldMessage>> fieldsWarnings;

    @Inject
    public SpincastResponseRequestContextAddon(R requestContext,
                                               IServer server,
                                               IJsonManager jsonManager,
                                               IXmlManager xmlManager,
                                               ISpincastConfig spincastConfig,
                                               ISpincastUtils spincastUtils,
                                               IETagFactory etagFactory) {
        this.requestContext = requestContext;
        this.server = server;
        this.jsonManager = jsonManager;
        this.xmlManager = xmlManager;
        this.spincastConfig = spincastConfig;
        this.spincastUtils = spincastUtils;
        this.etagFactory = etagFactory;
    }

    protected R getRequestContext() {
        return this.requestContext;
    }

    protected IServer getServer() {
        return this.server;
    }

    protected Object getExchange() {
        return getRequestContext().exchange();
    }

    protected IJsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected IXmlManager getXmlManager() {
        return this.xmlManager;
    }

    protected ISpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected ISpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected IETagFactory getEtagFactory() {
        return this.etagFactory;
    }

    protected ByteArrayOutputStream getBuffer() {
        return this.byteArrayOutputStreamIn;
    }

    protected ByteArrayOutputStream getOut() {
        return this.byteArrayOutputStreamOut;
    }

    protected List<String> getGlobalErrors() {
        if(this.globalErrors == null) {
            this.globalErrors = new ArrayList<String>();
        }
        return this.globalErrors;
    }

    protected List<String> getGlobalConfirms() {

        if(this.globalConfirms == null) {
            this.globalConfirms = new ArrayList<String>();
        }
        return this.globalConfirms;
    }

    protected List<String> getGlobalWarnings() {

        if(this.globalWarnings == null) {
            this.globalWarnings = new ArrayList<String>();
        }
        return this.globalWarnings;
    }

    protected Map<String, List<IFieldMessage>> getFieldsErrors() {
        if(this.fieldsErrors == null) {
            this.fieldsErrors = new HashMap<String, List<IFieldMessage>>();
        }
        return this.fieldsErrors;
    }

    protected Map<String, List<IFieldMessage>> getFieldsConfirms() {
        if(this.fieldsConfirms == null) {
            this.fieldsConfirms = new HashMap<String, List<IFieldMessage>>();
        }
        return this.fieldsConfirms;
    }

    protected Map<String, List<IFieldMessage>> getFieldsWarnings() {
        if(this.fieldsWarnings == null) {
            this.fieldsWarnings = new HashMap<String, List<IFieldMessage>>();
        }
        return this.fieldsWarnings;
    }

    public GZIPOutputStream getGzipBuffer() {
        try {
            if(this.gzipOutputStream == null) {
                this.gzipOutputStream = new GZIPOutputStream(getOut(), true);
            }
            return this.gzipOutputStream;
        } catch(Exception ex) {
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
    public IResponseRequestContextAddon<R> setGzipOption(GzipOption gzipOption) {

        if(gzipOption == null) {
            gzipOption = GzipOption.DEFAULT;
        }

        if(isHeadersSent() && gzipOption != getGzipOption()) {
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
    public IResponseRequestContextAddon<R> setStatusCode(int responseStatusCode) {

        if(isHeadersSent()) {
            if(responseStatusCode != getStatusCode()) {
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
    public IResponseRequestContextAddon<R> setContentType(String responseContentType) {

        if(isHeadersSent()) {
            if(responseContentType != null && !responseContentType.equals(getContentType())) {
                this.logger.warn("Response headers already sent, the content-type can't be changed...");
            }
        } else {

            if((responseContentType != null && !responseContentType.equals(getContentType())) ||
               (responseContentType == null && getContentType() != null)) {

                //==========================================
                // isShouldGzip will be revalidated since it can
                // change depending of the content-type.
                //==========================================
                GzipOption gzipOption = getGzipOption();
                if(gzipOption == GzipOption.DEFAULT) {
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
    public void redirect(String newUrl, boolean permanently) {
        if(permanently) {
            redirect(newUrl, HttpStatus.SC_MOVED_PERMANENTLY);
        } else {
            redirect(newUrl, HttpStatus.SC_MOVED_TEMPORARILY);
        }
    }

    @Override
    public void redirect(String newUrl, int specific3xxCode) {

        try {
            if(isHeadersSent()) {
                throw new RuntimeException("Can't set redirect, the headers are already sent.");
            }

            setStatusCode(specific3xxCode);

            if(StringUtils.isBlank(newUrl)) {
                newUrl = "/";
            } else {
                newUrl = newUrl.trim();

                // @see http://stackoverflow.com/a/12840255/843699
                if(newUrl.startsWith("//")) {
                    newUrl = getServer().getRequestScheme(getExchange()) + ":" + newUrl;
                }
            }

            URI uri = new URI(newUrl);
            if(!uri.isAbsolute()) {

                URI currentUri = new URI(getRequestContext().request().getFullUrl());

                String path = newUrl;

                //==========================================
                // Relative path?
                //==========================================
                if(!path.startsWith("/")) {

                    String currentPath = currentUri.getPath();
                    currentPath = StringUtils.strip(currentPath, "/");

                    int lastSlashPos = currentPath.lastIndexOf("/");
                    if(lastSlashPos < 0) {
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
                              null,
                              null);

                newUrl = uri.toString();
            }

            setHeader(HttpHeaders.LOCATION, newUrl);

        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
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

        if(isClosed()) {
            this.logger.debug("The response is closed, nothing more can be sent!");
            return;
        }

        if(isHeadersSent()) {
            if(contentType != null && !contentType.equals(getContentType())) {
                this.logger.warn("Response headers are already sent, the content-type won't be changed...");
            }
        } else {
            if(contentType != null) {
                if(getContentType() != null && !contentType.equals(getContentType())) {
                    this.logger.warn("The content-type is changed from " + getContentType() + " to " + contentType);
                }
                setContentType(contentType);
            }
        }

        if(bytes != null) {
            try {
                getBuffer().write(bytes);
            } catch(Exception ex) {
                throw SpincastStatics.runtimize(ex);
            }
        }

        if(flush) {
            flush();
        }
    }

    @Override
    public void sendCharacters(String content) {
        sendCharacters(content, null, false);
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
            if(content != null) {
                bytes = content.getBytes(getCharactersCharsetName());
            }
            send(bytes, contentType, flush);
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public String getCharactersCharsetName() {
        return this.charactersCharsetName;
    }

    @Override
    public IResponseRequestContextAddon<R> setCharactersCharsetName(String charactersCharsetName) {

        Objects.requireNonNull(charactersCharsetName, "charactersCharsetName can't be NULL");

        if(isHeadersSent() && !charactersCharsetName.equalsIgnoreCase(getCharactersCharsetName())) {
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
    public void sendParseHtml(String html, Map<String, Object> params) {
        sendParseHtml(html, params, false);
    }

    @Override
    public void sendParseHtml(String html, Map<String, Object> params, boolean flush) {

        IJsonObject model = getJsonManager().create(params);
        sendParseHtml(html, model, flush);
    }

    @Override
    public void sendParseHtml(String html, IJsonObject model) {
        sendParseHtml(html, model, false);
    }

    @Override
    public void sendParseHtml(String html, IJsonObject model, boolean flush) {
        String evaluated = getRequestContext().templating().evaluate(html, model);
        sendHtml(evaluated, flush);
    }

    @Override
    public void sendTemplateHtml(String templatePath, IJsonObject model) {
        sendTemplateHtml(templatePath, true, model, false);
    }

    @Override
    public void sendTemplateHtml(String templatePath, IJsonObject model, boolean flush) {
        sendTemplateHtml(templatePath, true, model, flush);
    }

    @Override
    public void sendTemplateHtml(String templatePath, boolean isClasspathPath, IJsonObject model) {
        sendTemplateHtml(templatePath, isClasspathPath, model, false);
    }

    @Override
    public void sendTemplateHtml(String templatePath, boolean isClasspathPath, IJsonObject model, boolean flush) {
        String evaluated = getRequestContext().templating().fromTemplate(templatePath, isClasspathPath, model);
        sendHtml(evaluated, flush);
    }

    @Override
    public void sendParse(String content, String contentType, Map<String, Object> params) {
        sendParse(content, contentType, params, false);
    }

    @Override
    public void sendParse(String content, String contentType, Map<String, Object> params, boolean flush) {

        if(params == null) {
            params = new HashMap<String, Object>();
        }
        IJsonObject model = getJsonManager().create(params);

        sendParse(content, contentType, model, flush);
    }

    @Override
    public void sendParse(String content, String contentType, IJsonObject model) {
        sendParse(content, contentType, model, false);
    }

    @Override
    public void sendParse(String content, String contentType, IJsonObject model, boolean flush) {

        if(StringUtils.isBlank(contentType)) {
            sendParseHtml(content, model, flush);
            return;
        }

        String evaluated = getRequestContext().templating().evaluate(content, model);
        sendCharacters(evaluated, contentType, flush);
    }

    /*
    @Override
    public void sendTemplateHtml(String templatePath) {
        IJsonObject responseJsonObject = generateJsonObjectResponse();
        sendTemplateHtml(templatePath, true, responseJsonObject, false);
    }
    */

    @Override
    public void sendTemplateHtml(String templatePath, Map<String, Object> params) {
        sendTemplateHtml(templatePath, true, params, false);
    }

    @Override
    public void sendTemplateHtml(String templatePath, boolean isClasspathPath, Map<String, Object> params) {
        sendTemplateHtml(templatePath, isClasspathPath, params, false);
    }

    @Override
    public void sendTemplateHtml(String templatePath, Map<String, Object> params, boolean flush) {
        sendTemplateHtml(templatePath, true, params, flush);
    }

    @Override
    public void sendTemplateHtml(String templatePath, boolean isClasspathPath, Map<String, Object> params, boolean flush) {
        String evaluated = getRequestContext().templating().fromTemplate(templatePath, isClasspathPath, params);
        sendHtml(evaluated, flush);
    }

    @Override
    public void sendTemplate(String templatePath, String contentType, Map<String, Object> params) {
        sendTemplate(templatePath, true, contentType, params, false);
    }

    @Override
    public void sendTemplate(String templatePath, boolean isClasspathPath, String contentType, Map<String, Object> params) {
        sendTemplate(templatePath, isClasspathPath, contentType, params, false);
    }

    @Override
    public void sendTemplate(String templatePath, String contentType, Map<String, Object> params, boolean flush) {
        sendTemplate(templatePath, true, contentType, params, flush);
    }

    @Override
    public void sendTemplate(String templatePath, boolean isClasspathPath, String contentType, Map<String, Object> params,
                             boolean flush) {

        if(params == null) {
            params = new HashMap<String, Object>();
        }
        IJsonObject model = getJsonManager().create(params);

        sendTemplate(templatePath, isClasspathPath, contentType, model, flush);
    }

    @Override
    public void sendTemplate(String templatePath, String contentType, IJsonObject model) {
        sendTemplate(templatePath, true, contentType, model, false);
    }

    @Override
    public void sendTemplate(String templatePath, String contentType, IJsonObject model, boolean flush) {
        sendTemplate(templatePath, true, contentType, model, flush);
    }

    @Override
    public void sendTemplate(String templatePath, boolean isClasspathPath, String contentType, IJsonObject model) {
        sendTemplate(templatePath, isClasspathPath, contentType, model, false);
    }

    @Override
    public void sendTemplate(String templatePath, boolean isClasspathPath, String contentType, IJsonObject model, boolean flush) {

        if(StringUtils.isBlank(contentType)) {

            contentType = getSpincastUtils().getMimeTypeFromPath(templatePath);
            if(contentType == null) {
                this.logger.warn("The Content-Type was not specified and can't be determined from the template path '" +
                                 templatePath +
                                 "': 'text/plain' will be used");
                contentType = ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset();
            }
        }

        String evaluated = getRequestContext().templating().fromTemplate(templatePath, isClasspathPath, model);
        sendCharacters(evaluated, contentType, flush);
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
    public void sendJsonObj(Object obj) {
        sendJsonObj(obj, false);
    }

    @Override
    public void sendJsonObj(Object obj, boolean flush) {
        String json = getJsonManager().toJsonString(obj);
        sendCharacters(json, ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(), flush);
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
    public void sendXmlObj(Object obj) {
        sendXmlObj(obj, false);
    }

    @Override
    public void sendXmlObj(Object obj, boolean flush) {
        String xml = getXmlManager().toXml(obj);
        sendCharacters(xml, ContentTypeDefaults.XML.getMainVariationWithUtf8Charset(), flush);
    }

    @Override
    public IResponseRequestContextAddon<R> resetBuffer() {

        try {
            getBuffer().reset();

            if(!isHeadersSent()) {
                this.isResponseCharactersBased = IS_RESPONSE_CHARACTERS_BASED_BY_DEFAULT;
            }
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }

        return this;
    }

    @Override
    public IResponseRequestContextAddon<R> resetEverything() {

        resetBuffer();

        if(isHeadersSent()) {
            this.logger.warn("Response headers are already sent, the cookies, headers and status code won't be reset...");
        } else {
            getRequestContext().cookies().resetCookies();
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
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public IResponseRequestContextAddon<R> removeHeader(String name) {

        if(isHeadersSent()) {
            this.logger.warn("Response headers are already sent, can't change them...");
            return this;
        }

        getHeaders().remove(name);

        return this;
    }

    @Override
    public IResponseRequestContextAddon<R> setHeader(String name, String value) {

        if(isHeadersSent()) {
            this.logger.warn("Response headers are already sent, can't change them...");
            return this;
        }

        if(value == null) {
            removeHeader(name);
            return this;
        }
        setHeader(name, Arrays.asList(value));

        return this;
    }

    @Override
    public IResponseRequestContextAddon<R> setHeader(String name, List<String> values) {

        if(isHeadersSent()) {
            this.logger.warn("Response headers are already sent, can't change them...");
            return this;
        }

        if(values == null) {
            removeHeader(name);
            return this;
        }
        getHeaders().put(name, values);

        return this;
    }

    @Override
    public IResponseRequestContextAddon<R> addHeaderValue(String name, String value) {

        if(isHeadersSent()) {
            this.logger.warn("Response headers are already sent, can't change them...");
            return this;
        }

        if(value == null) {
            return this;
        }
        addHeaderValues(name, Arrays.asList(value));

        return this;
    }

    @Override
    public IResponseRequestContextAddon<R> addHeaderValues(String name, List<String> values) {

        if(isHeadersSent()) {
            this.logger.warn("Response headers are already sent, can't change them...");
            return this;
        }

        if(values == null) {
            return this;
        }
        List<String> currentValues = getHeaders().get(name);
        if(currentValues == null) {
            currentValues = new ArrayList<String>();
            getHeaders().put(name, currentValues);
        }
        currentValues.addAll(values);

        return this;
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        if(this.headers == null) {
            Map<String, List<String>> headersFromServer = getServer().getResponseHeaders(getExchange());

            // We use a TreeMap with String.CASE_INSENSITIVE_ORDER so the
            // "get" is case insensitive!
            TreeMap<String, List<String>> treeMap = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);
            if(headersFromServer != null) {
                treeMap.putAll(headersFromServer);
            }

            this.headers = treeMap;
        }
        return this.headers;
    }

    @Override
    public List<String> getHeader(String name) {
        if(StringUtils.isBlank(name)) {
            return new LinkedList<String>();
        }
        // This get is case insensitive.
        List<String> values = getHeaders().get(name);
        if(values == null) {
            values = new LinkedList<String>();
        }
        return values;
    }

    @Override
    public String getHeaderFirst(String name) {
        List<String> values = getHeader(name);
        if(values != null && values.size() > 0) {
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
        if(gzipOption != GzipOption.DEFAULT) {
            this.logger.warn("Can't turn on/off the gzip feature since the GzipOption is " + gzipOption);
            return;
        }

        try {
            if(isHeadersSent()) {
                if(this.isShouldGzip != null && this.isShouldGzip.getBoolean() != isShouldGzip) {
                    this.logger.warn("Can't turn on/off the gzip feature since headers are already sent.");
                }
                return;
            }

            //==========================================
            // If we turn off the gziping, we have to
            // change the buffer.
            //==========================================
            if(this.isShouldGzip != null && this.isShouldGzip.getBoolean() && !isShouldGzip) {

                this.byteArrayOutputStreamIn.reset();

                getGzipBuffer().close();
                ByteArrayOutputStream buffer = getBuffer();
                if(buffer.size() > 0) {
                    GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(buffer.toByteArray()));
                    byte[] ungzipedBytes = IOUtils.toByteArray(gzipInputStream);
                    this.byteArrayOutputStreamIn.write(ungzipedBytes);
                }
                this.gzipOutputStream = null;
            }

            this.isShouldGzip = Bool.from(isShouldGzip);
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected boolean isShouldGzip() {

        //==========================================
        // The GzipOption has priority.
        //==========================================
        GzipOption gzipOption = getGzipOption();
        if(gzipOption == GzipOption.FORCE) {
            return true;
        } else if(gzipOption == GzipOption.DISABLE) {
            return false;
        } else if(gzipOption != GzipOption.DEFAULT) {
            throw new RuntimeException("Unimplemented : " + gzipOption);
        }

        if(this.isShouldGzip == null) {

            //==========================================
            // Check if there is a gzip 'Accept-Encoding' header 
            //==========================================
            boolean hasGzipAcceptHeader = false;
            List<String> acceptEncodings = getRequestContext().request().getHeader(HttpHeaders.ACCEPT_ENCODING);
            if(acceptEncodings != null) {
                for(String acceptEncoding : acceptEncodings) {
                    if(acceptEncoding == null) {
                        continue;
                    }
                    if(acceptEncoding.contains("gzip")) {
                        hasGzipAcceptHeader = true;
                        break;
                    }
                }
            }

            String responseContentType = getContentType();

            //==========================================
            // Try to guess the content-type
            //==========================================
            if(responseContentType == null) {
                String path = getRequestContext().request().getRequestPath();
                responseContentType = getSpincastUtils().getMimeTypeFromPath(path);
            }

            //==========================================
            // Check if its a content-type for which we
            // shouldn't use gzip.
            //==========================================
            if(responseContentType != null) {
                if(!getSpincastUtils().isContentTypeToSkipGziping(responseContentType)) {
                    if(!hasGzipAcceptHeader) {
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

        if(this.isShouldGzip == null) {
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
    public void flush(boolean end) {

        try {
            if(isClosed()) {
                return;
            }

            //==========================================
            // If we haven't read the request body yet, the
            // request size may not have been checked. So we
            // read it now to be able to send a 
            // "413 - Request entity too large" status if
            // required.
            //==========================================
            if(!isRequestSizeValidated() && !isHeadersSent()) {
                setRequestSizeValidated(true);
                boolean requestSizeOk = getServer().forceRequestSizeValidation(getExchange());
                if(!requestSizeOk) {
                    resetEverything();
                    setStatusCode(HttpStatus.SC_REQUEST_TOO_LONG);
                }
            }

            ByteArrayOutputStream buffer = getBuffer();

            //==========================================
            // Send the Headers!
            //==========================================
            if(!isHeadersSent()) {

                getServer().setResponseStatusCode(getExchange(), getStatusCode());

                String responseContentType = getContentType();
                if(responseContentType == null) {

                    String mimeType = getSpincastUtils().getMimeTypeFromPath(getRequestContext().request()
                                                                                                .getRequestPath());
                    if(mimeType != null) {
                        responseContentType = mimeType;
                    } else {
                        if(isResponseCharactersBased() || buffer.size() == 0) {
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
                if(end && !isShouldGzip()) {
                    setHeader(HttpHeaders.CONTENT_LENGTH, "" + buffer.size());
                }

                //==========================================
                // Add Cookies
                //==========================================
                getServer().addCookies(getExchange(), getRequestContext().cookies().getCookies());

                //==========================================
                // Gzip headers
                //==========================================
                if(isShouldGzip()) {
                    setHeader(HttpHeaders.CONTENT_ENCODING, "gzip");
                    setHeader(HttpHeaders.TRANSFER_ENCODING, "chunked");
                }

                //==========================================
                // Add the headers to the server
                //==========================================
                getServer().setResponseHeaders(getExchange(), getHeaders());
            }

            byte[] bytesToFlush;
            if(isShouldGzip()) {

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
                if(end) {
                    getGzipBuffer().close();
                }

                bytesToFlush = getOut().toByteArray();
                getOut().reset();

            } else {
                bytesToFlush = buffer.toByteArray();
            }
            buffer.reset();

            getServer().flushBytes(getExchange(), bytesToFlush, end);

        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public IResponseRequestContextAddon<R> setCacheSeconds(int cacheSeconds) {
        return setCacheSeconds(cacheSeconds, false);
    }

    @Override
    public IResponseRequestContextAddon<R> setCacheSeconds(int cacheSeconds, boolean isPrivateCache) {

        if(cacheSeconds <= 0) {
            this.logger.warn("A number of seconds below 1 doesn't send any cache headers: " + cacheSeconds);
            return this;
        }

        if(isHeadersSent()) {
            this.logger.error("The headers are sent, you can't add cache headers.");
            return this;
        }

        String cacheControl = "max-age=" + cacheSeconds;
        if(isPrivateCache) {
            cacheControl = "private, " + cacheControl;
        } else {
            cacheControl = "public, " + cacheControl;
        }

        setHeader(HttpHeaders.CACHE_CONTROL, cacheControl);

        return this;
    }

    @Override
    public void sendTemplateHtml(String templatePath) {
        // TODO Auto-generated method stub
        sendTemplateHtml(templatePath, (IJsonObject)null);
    }

    /*
    @Override
    public void addErrorGlobal(String globalErrorMessage) {
    
        if(StringUtils.isBlank(globalErrorMessage)) {
            throw new RuntimeException("The error message can't be empty.");
        }
    
        getGlobalErrors().add(globalErrorMessage);
    }
    
    @Override
    public void addErrorField(String fieldName, String fieldErrorMessage) {
        addErrorField(fieldName, null, null, fieldErrorMessage);
    }
    
    @Override
    public void addErrorField(String fieldName, int valuePosition, String fieldErrorMessage) {
        addErrorField(fieldName, null, valuePosition, fieldErrorMessage);
    }
    
    @Override
    public void addErrorField(String fieldName, String value, String fieldErrorMessage) {
        addErrorField(fieldName, value, null, fieldErrorMessage);
    }
    
    protected void addErrorField(String fieldName, String value, Integer valuePosition, String fieldErrorMessage) {
    
        if(StringUtils.isBlank(fieldName)) {
            throw new RuntimeException("The field name can't be empty.");
        }
        if(StringUtils.isBlank(fieldErrorMessage)) {
            throw new RuntimeException("The error message can't be empty.");
        }
    
        List<IFieldMessage> errors = getFieldsErrors().get(fieldName);
        if(errors == null) {
            errors = new ArrayList<IFieldMessage>();
            getFieldsErrors().put(fieldName, errors);
        }
        errors.add(createFieldMessage(FieldMessageType.ERROR, fieldName, value, valuePosition, fieldErrorMessage));
    }
    
    protected IFieldMessage createFieldMessage(FieldMessageType messageType, String fieldName, String value,
                                               Integer valuePosition, String fieldErrorMessage) {
        return new FieldMessage(messageType, fieldName, value, valuePosition, fieldErrorMessage);
    }
    
    @Override
    public void addWarningGlobal(String globalWarningMessage) {
    
        if(StringUtils.isBlank(globalWarningMessage)) {
            throw new RuntimeException("The warning message can't be empty.");
        }
    
        getGlobalWarnings().add(globalWarningMessage);
    }
    
    @Override
    public void addWarningField(String fieldName, String fieldWarningMessage) {
    
        if(StringUtils.isBlank(fieldName)) {
            throw new RuntimeException("The field name can't be empty.");
        }
        if(StringUtils.isBlank(fieldWarningMessage)) {
            throw new RuntimeException("The warning message can't be empty.");
        }
    
        List<IFieldMessage> warnings = getFieldsWarnings().get(fieldName);
        if(warnings == null) {
            warnings = new ArrayList<IFieldMessage>();
            getFieldsWarnings().put(fieldName, warnings);
        }
        warnings.add(createFieldMessage(FieldMessageType.WARNING, fieldName, null, null, fieldWarningMessage));
    }
    
    @Override
    public void addConfirmGlobal(String globalConfirmationMessage) {
    
        if(StringUtils.isBlank(globalConfirmationMessage)) {
            throw new RuntimeException("The confirmation message can't be empty.");
        }
    
        getGlobalConfirms().add(globalConfirmationMessage);
    }
    
    @Override
    public void addConfirmField(String fieldName, String fieldConfirmationMessage) {
    
        if(StringUtils.isBlank(fieldName)) {
            throw new RuntimeException("The field name can't be empty.");
        }
        if(StringUtils.isBlank(fieldConfirmationMessage)) {
            throw new RuntimeException("The confirmation message can't be empty.");
        }
    
        List<IFieldMessage> confirms = getFieldsConfirms().get(fieldName);
        if(confirms == null) {
            confirms = new ArrayList<IFieldMessage>();
            getFieldsConfirms().put(fieldName, confirms);
        }
        confirms.add(createFieldMessage(FieldMessageType.CONFIRM, fieldName, null, null, fieldConfirmationMessage));
    }
    
    protected IJsonObject generateJsonObjectResponse() {
    
        IJsonObject jsonObj = getJsonManager().create();
    
    
        IJsonObject fields = getJsonManager().create();
        
        Map<String, List<String>> formDatas = getRequestContext().request().getFormDatas();
        if(formDatas != null) {
            for(Entry<String, List<String>> entry : formDatas.entrySet()) {
        
                String fieldName = entry.getKey().trim();
        
                IJsonObject field = getJsonManager().create();
                fields.put(fieldName, field);
        
                //==========================================
                // Field level messages
                //==========================================
                List<IFieldMessage> allFieldErrors = getFieldsErrors().get(fieldName);
                if(allFieldErrors == null) {
                    allFieldErrors = new ArrayList<IFieldMessage>();
                }
        
                List<IFieldMessage> fieldLevelErrors = new ArrayList<IFieldMessage>();
                for(IFieldMessage error : allFieldErrors) {
                    if(error.getValuePosition() == null && error.getValue() == null) {
                        fieldLevelErrors.add(error);
                    }
                }
                field.put("errors", fieldLevelErrors);
                field.put("isError", allFieldErrors.size() > 0);
        
                List<IFieldMessage> allFieldWarnings = getFieldsWarnings().get(fieldName);
                if(allFieldWarnings == null) {
                    allFieldWarnings = new ArrayList<IFieldMessage>();
                }
                List<IFieldMessage> fieldLevelWarnings = new ArrayList<IFieldMessage>();
                for(IFieldMessage warning : allFieldWarnings) {
                    if(warning.getValuePosition() == null && warning.getValue() == null) {
                        fieldLevelWarnings.add(warning);
                    }
                }
                field.put("warnings", fieldLevelWarnings);
                field.put("isWarning", allFieldWarnings.size() > 0 && allFieldErrors.size() == 0);
        
                List<IFieldMessage> allFieldConfirms = getFieldsConfirms().get(fieldName);
                if(allFieldConfirms == null) {
                    allFieldConfirms = new ArrayList<IFieldMessage>();
                }
                List<IFieldMessage> fieldLevelConfirms = new ArrayList<IFieldMessage>();
                for(IFieldMessage confirm : allFieldConfirms) {
                    if(confirm.getValuePosition() == null && confirm.getValue() == null) {
                        fieldLevelConfirms.add(confirm);
                    }
                }
                field.put("confirms", fieldLevelConfirms);
                field.put("isConfirm", allFieldConfirms.size() > 0 && allFieldErrors.size() == 0 && allFieldWarnings.size() == 0);
        
                //==========================================
                // Field values
                //==========================================
                List<IJsonObject> fieldValues = new ArrayList<>();
                field.put("values", fieldValues);
        
                List<String> submittedValues = entry.getValue();
        
                for(int i = 0; i < submittedValues.size(); i++) {
        
                    IJsonObject fieldValue = getJsonManager().create();
                    fieldValues.add(fieldValue);
        
                    fieldValue.put("value", submittedValues.get(i));
        
                    //==========================================
                    // FieldValue level messages
                    //==========================================
                    fieldValue.put("errors", getJsonManager().createArray());
                    fieldValue.put("warnings", getJsonManager().createArray());
                    fieldValue.put("confirms", getJsonManager().createArray());
                }
            }
        }
        
        //==========================================
        // Some groups may need some messages without any fields
        // present in the request. For example a group of
        // checkbox with no option checked.
        //==========================================
        for(Entry<String, List<IFieldMessage>> entry : getFieldsErrors().entrySet()) {
        
            String fieldName = entry.getKey();
            if(!fields.isKeyExists(fieldName)) {
        
                IJsonObject field = getJsonManager().create();
        
                List<IFieldMessage> errors = entry.getValue();
                if(errors == null) {
                    errors = new ArrayList<IFieldMessage>();
                }
                field.put("errors", errors);
                field.put("isError", errors.size() > 0);
        
                List<IFieldMessage> warnings = getFieldsWarnings().get(fieldName);
                if(warnings == null) {
                    warnings = new ArrayList<IFieldMessage>();
                }
                field.put("warnings", warnings);
                field.put("isWarning", warnings.size() > 0 && errors.size() == 0);
        
                List<IFieldMessage> confirms = getFieldsConfirms().get(fieldName);
                if(confirms == null) {
                    confirms = new ArrayList<IFieldMessage>();
                }
                field.put("confirms", confirms);
                field.put("isConfirm", confirms.size() > 0 && errors.size() == 0 && warnings.size() == 0);
        
                fields.put(fieldName, field);
            }
        }
        
        for(Entry<String, List<IFieldMessage>> entry : getFieldsWarnings().entrySet()) {
        
            String fieldName = entry.getKey();
            if(!fields.isKeyExists(fieldName)) {
        
                IJsonObject field = getJsonManager().create();
        
                List<IFieldMessage> warnings = entry.getValue();
                if(warnings == null) {
                    warnings = new ArrayList<IFieldMessage>();
                }
                field.put("warnings", warnings);
                field.put("isWarning", warnings.size() > 0);
        
                List<IFieldMessage> confirms = getFieldsConfirms().get(fieldName);
                if(confirms == null) {
                    confirms = new ArrayList<IFieldMessage>();
                }
                field.put("confirms", confirms);
                field.put("isConfirm", confirms.size() > 0 && warnings.size() == 0);
        
                fields.put(fieldName, field);
            }
        }
        
        for(Entry<String, List<IFieldMessage>> entry : getFieldsConfirms().entrySet()) {
        
            String fieldName = entry.getKey();
            if(!fields.isKeyExists(fieldName)) {
        
                IJsonObject field = getJsonManager().create();
        
                List<IFieldMessage> confirms = entry.getValue();
                if(confirms == null) {
                    confirms = new ArrayList<IFieldMessage>();
                }
                field.put("confirms", confirms);
                field.put("isConfirm", confirms.size() > 0);
        
                fields.put(fieldName, field);
            }
        }
        
        jsonObj.put("fields", fields);
    
        return jsonObj;
    }
    
    */

}
