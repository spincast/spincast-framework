package org.spincast.plugins.response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.spincast.core.server.IServer;
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

    private String responseContentType = null;
    private int responseStatusCode = HttpStatus.SC_OK;
    private final ByteArrayOutputStream byteArrayOutputStreamIn = new ByteArrayOutputStream(256);
    private final ByteArrayOutputStream byteArrayOutputStreamOut = new ByteArrayOutputStream(256);
    private GZIPOutputStream gzipOutputStream = null;
    private Boolean isShouldGzip = null;

    private String charactersCharsetName = "UTF-8";
    private boolean isResponseCharactersBased = IS_RESPONSE_CHARACTERS_BASED_BY_DEFAULT;
    private boolean requestSizeValidated = false;
    private Map<String, List<String>> headers;
    private GzipOption gzipOption = GzipOption.DEFAULT;

    @Inject
    public SpincastResponseRequestContextAddon(R requestContext,
                                               IServer server,
                                               IJsonManager jsonManager,
                                               IXmlManager xmlManager,
                                               ISpincastConfig spincastConfig,
                                               ISpincastUtils spincastUtils) {
        this.requestContext = requestContext;
        this.server = server;
        this.jsonManager = jsonManager;
        this.xmlManager = xmlManager;
        this.spincastConfig = spincastConfig;
        this.spincastUtils = spincastUtils;
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

    protected ByteArrayOutputStream getBuffer() {
        return this.byteArrayOutputStreamIn;
    }

    protected ByteArrayOutputStream getOut() {
        return this.byteArrayOutputStreamOut;
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
    public void setGzipOption(GzipOption gzipOption) {

        if(gzipOption == null) {
            gzipOption = GzipOption.DEFAULT;
        }

        if(isHeadersSent() && gzipOption != getGzipOption()) {
            this.logger.warn("The headers are sent, you can't change the gzip options.");
            return;
        }

        this.gzipOption = gzipOption;
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
    public void setStatusCode(int responseStatusCode) {

        if(isHeadersSent()) {
            if(responseStatusCode != getStatusCode()) {
                this.logger.warn("Response headers already sent, the http status code can't be changed...");
            }
        } else {
            this.responseStatusCode = responseStatusCode;
        }
    }

    @Override
    public String getContentType() {
        return this.responseContentType;
    }

    @Override
    public void setContentType(String responseContentType) {

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
                    setIsShouldGzip(null);
                }
            }
            this.responseContentType = responseContentType;
        }
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
    public void setCharactersCharsetName(String charactersCharsetName) {

        Objects.requireNonNull(charactersCharsetName, "charactersCharsetName can't be NULL");

        if(isHeadersSent() && !charactersCharsetName.equalsIgnoreCase(getCharactersCharsetName())) {
            this.logger.warn("Some data have already been send, it may not be a good idea to change the Charset now.");
        }
        this.charactersCharsetName = charactersCharsetName;
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
    public void sendHtmlParse(String html, Map<String, Object> params) {
        sendHtmlParse(html, params, false);
    }

    @Override
    public void sendHtmlParse(String html, Map<String, Object> params, boolean flush) {
        String evaluated = getRequestContext().templating().evaluate(html, params);
        sendHtml(evaluated, flush);
    }

    @Override
    public void sendParse(String content, String contentType, Map<String, Object> params) {
        sendParse(content, contentType, params, false);
    }

    @Override
    public void sendParse(String content, String contentType, Map<String, Object> params, boolean flush) {

        if(StringUtils.isBlank(contentType)) {
            sendHtmlParse(content, params, flush);
            return;
        }

        String evaluated = getRequestContext().templating().evaluate(content, params);
        sendCharacters(evaluated, contentType, flush);
    }

    @Override
    public void sendHtmlTemplate(String templatePath, Map<String, Object> params) {
        sendHtmlTemplate(templatePath, true, params, false);
    }

    @Override
    public void sendHtmlTemplate(String templatePath, boolean isClasspathPath, Map<String, Object> params) {
        sendHtmlTemplate(templatePath, isClasspathPath, params, false);
    }

    @Override
    public void sendHtmlTemplate(String templatePath, Map<String, Object> params, boolean flush) {
        sendHtmlTemplate(templatePath, true, params, flush);
    }

    @Override
    public void sendHtmlTemplate(String templatePath, boolean isClasspathPath, Map<String, Object> params, boolean flush) {
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

        if(StringUtils.isBlank(contentType)) {
            sendHtmlTemplate(templatePath, isClasspathPath, params, flush);
            return;
        }

        String evaluated = getRequestContext().templating().fromTemplate(templatePath, isClasspathPath, params);
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
    public void resetBuffer() {

        try {
            getBuffer().reset();

            if(!isHeadersSent()) {
                this.isResponseCharactersBased = IS_RESPONSE_CHARACTERS_BASED_BY_DEFAULT;
            }
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public void resetEverything() {

        resetBuffer();

        if(isHeadersSent()) {
            this.logger.warn("Response headers are already sent, the cookies, headers and status code won't be reset...");
        } else {
            getRequestContext().cookies().resetCookies();
            getHeaders().clear();
            setContentType(null);
            setStatusCode(HttpStatus.SC_OK);
        }
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
    public void removeHeader(String name) {

        if(isHeadersSent()) {
            this.logger.warn("Response headers are already sent, can't change them...");
            return;
        }

        getHeaders().remove(name);
    }

    @Override
    public void setHeader(String name, String value) {

        if(isHeadersSent()) {
            this.logger.warn("Response headers are already sent, can't change them...");
            return;
        }

        if(value == null) {
            removeHeader(name);
            return;
        }
        setHeader(name, Arrays.asList(value));
    }

    @Override
    public void setHeader(String name, List<String> values) {

        if(isHeadersSent()) {
            this.logger.warn("Response headers are already sent, can't change them...");
            return;
        }

        if(values == null) {
            removeHeader(name);
            return;
        }
        getHeaders().put(name, values);
    }

    @Override
    public void addHeaderValue(String name, String value) {

        if(isHeadersSent()) {
            this.logger.warn("Response headers are already sent, can't change them...");
            return;
        }

        if(value == null) {
            return;
        }
        addHeaderValues(name, Arrays.asList(value));
    }

    @Override
    public void addHeaderValues(String name, List<String> values) {

        if(isHeadersSent()) {
            this.logger.warn("Response headers are already sent, can't change them...");
            return;
        }

        if(values == null) {
            return;
        }
        List<String> currentValues = getHeaders().get(name);
        if(currentValues == null) {
            currentValues = new ArrayList<String>();
            getHeaders().put(name, currentValues);
        }
        currentValues.addAll(values);
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        if(this.headers == null) {
            Map<String, List<String>> headersFromServer = getServer().getResponseHeaders(getExchange());

            // We use a TreeMap with String.CASE_INSENSITIVE_ORDER so the
            // "get" is case insensitive!
            TreeMap<String, List<String>> treeMap = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);
            if(headersFromServer == null) {
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

    protected void setIsShouldGzip(Boolean isShouldGzip) {

        GzipOption gzipOption = getGzipOption();
        if(gzipOption != GzipOption.DEFAULT) {
            this.logger.warn("Can't turn on/off the gzip feature since the GzipOption is " + gzipOption);
            return;
        }

        try {
            if(isHeadersSent()) {
                if(this.isShouldGzip != isShouldGzip) {
                    this.logger.warn("Can't turn on/off the gzip feature since headers are already sent.");
                }
                return;
            }

            //==========================================
            // If we turn off the gziping, we have to
            // change the buffer.
            //==========================================
            if(this.isShouldGzip != null && this.isShouldGzip && (isShouldGzip == null || !isShouldGzip)) {

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

            this.isShouldGzip = isShouldGzip;
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
                setIsShouldGzip(!getSpincastUtils().isContentTypeToSkipGziping(responseContentType));
            } else {
                setIsShouldGzip(false);
            }

            if(this.isShouldGzip && !hasGzipAcceptHeader) {
                this.logger.debug("No gzip 'Accept-Encoding' header, we won't gzip the response.");
                setIsShouldGzip(false);
            }
        }
        return this.isShouldGzip;
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

}
