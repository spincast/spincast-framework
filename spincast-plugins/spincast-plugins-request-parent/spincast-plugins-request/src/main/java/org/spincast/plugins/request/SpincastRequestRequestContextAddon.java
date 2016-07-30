package org.spincast.plugins.request;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.config.SpincastConstants;
import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.exchange.IRequestRequestContextAddon;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.IETag;
import org.spincast.core.routing.IETagFactory;
import org.spincast.core.server.IServer;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.ISpincastUtils;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.xml.IXmlManager;
import org.spincast.shaded.org.apache.commons.io.IOUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.HttpHeaders;
import org.spincast.shaded.org.apache.http.NameValuePair;
import org.spincast.shaded.org.apache.http.client.utils.DateUtils;
import org.spincast.shaded.org.apache.http.client.utils.URLEncodedUtils;

import com.google.inject.Inject;

public class SpincastRequestRequestContextAddon<R extends IRequestContext<?>>
                                               implements IRequestRequestContextAddon<R> {

    protected final Logger logger = LoggerFactory.getLogger(SpincastRequestRequestContextAddon.class);

    private String fullUrlOriginalWithCacheBustersNonDecoded = null;
    private String fullUrlOriginalNoCacheBustersNonDecoded = null;
    private String fullUrlProxiedWithCacheBustersNonDecoded = null;
    private String fullUrlProxiedNoCacheBustersNonDecoded = null;

    private String fullUrlWithCacheBustersNonDecoded = null;
    private String fullUrlWithCacheBustersDecoded = null;
    private String fullUrlNoCacheBustersDecoded = null;

    private String requestPathNoCacheBusters = null;
    private String requestPathWithCacheBusters = null;
    private String queryStringNonDecoded = "";
    private String queryStringDecoded = "";

    private List<IETag> ifMatchETags = null;
    private Object ifMatchETagsLock = new Object();
    private List<IETag> ifNoneMatchETags = null;
    private Object ifNoneMatchETagsLock = new Object();

    private Date ifModifiedSinceDate = null;
    private Object ifModifiedSinceDateLock = new Object();

    private Date ifUnmodifiedSinceDate = null;
    private Object ifUnmodifiedSinceDateLock = new Object();

    private Map<String, List<String>> queryStringParams;
    private Map<String, List<String>> formDatas;
    private Map<String, List<File>> uploadedFiles;
    private Map<String, List<String>> headers;

    private final R requestContext;
    private final IServer server;
    private final IJsonManager jsonManager;
    private final IXmlManager xmlManager;
    private final ISpincastUtils spincastUtils;
    private final ISpincastConfig spincastConfig;
    private final IETagFactory etagFactory;

    @Inject
    public SpincastRequestRequestContextAddon(R requestContext,
                                              IServer server,
                                              IJsonManager jsonManager,
                                              IXmlManager xmlManager,
                                              ISpincastUtils spincastUtils,
                                              ISpincastConfig spincastConfig,
                                              IETagFactory etagFactory) {
        this.requestContext = requestContext;
        this.server = server;
        this.jsonManager = jsonManager;
        this.xmlManager = xmlManager;
        this.spincastUtils = spincastUtils;
        this.spincastConfig = spincastConfig;
        this.etagFactory = etagFactory;
    }

    protected R getRequestContext() {
        return this.requestContext;
    }

    protected IServer getServer() {
        return this.server;
    }

    protected IJsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected IXmlManager getXmlManager() {
        return this.xmlManager;
    }

    protected ISpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected ISpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected IETagFactory getEtagFactory() {
        return this.etagFactory;
    }

    protected Object getExchange() {
        return getRequestContext().exchange();
    }

    @Override
    public HttpMethod getHttpMethod() {
        return getServer().getHttpMethod(getExchange());
    }

    @Override
    public ContentTypeDefaults getContentTypeBestMatch() {
        return getServer().getContentTypeBestMatch(getExchange());
    }

    @Override
    public boolean isJsonShouldBeReturn() {
        return ContentTypeDefaults.JSON == getContentTypeBestMatch();
    }

    @Override
    public boolean isHTMLShouldBeReturn() {
        return ContentTypeDefaults.HTML == getContentTypeBestMatch();
    }

    @Override
    public boolean isXMLShouldBeReturn() {
        return ContentTypeDefaults.XML == getContentTypeBestMatch();
    }

    @Override
    public boolean isPlainTextShouldBeReturn() {
        return ContentTypeDefaults.TEXT == getContentTypeBestMatch();
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        if(this.headers == null) {

            //==========================================
            // We make sure everything is immutable
            //==========================================
            Map<String, List<String>> headersServer = getServer().getRequestHeaders(getExchange());

            // We use a TreeMap with String.CASE_INSENSITIVE_ORDER so the
            // "get" is case insensitive!
            Map<String, List<String>> headersFinal = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);

            if(headersServer == null) {
                headersServer = Collections.emptyMap();
            } else {
                for(Entry<String, List<String>> entry : headersServer.entrySet()) {

                    if(entry.getValue() == null) {
                        headersFinal.put(entry.getKey(), Collections.<String>emptyList());
                    } else {
                        headersFinal.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
                    }
                }
                headersFinal = Collections.unmodifiableMap(headersFinal);
            }
            this.headers = headersFinal;
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

    protected String getFullUrlOriginalNoCacheBustersNonDecoded() {
        if(this.fullUrlOriginalNoCacheBustersNonDecoded == null) {
            this.fullUrlOriginalNoCacheBustersNonDecoded = getServer().getFullUrlOriginal(getExchange(), false);
        }
        return this.fullUrlOriginalNoCacheBustersNonDecoded;
    }

    protected String getFullUrlOriginalWithCacheBustersNonDecoded() {
        if(this.fullUrlOriginalWithCacheBustersNonDecoded == null) {
            this.fullUrlOriginalWithCacheBustersNonDecoded = getServer().getFullUrlOriginal(getExchange(), true);
        }
        return this.fullUrlOriginalWithCacheBustersNonDecoded;
    }

    protected String getFullUrlProxiedNoCacheBustersNonDecoded() {
        if(this.fullUrlProxiedNoCacheBustersNonDecoded == null) {
            this.fullUrlProxiedNoCacheBustersNonDecoded = getServer().getFullUrlProxied(getExchange(), false);
        }
        return this.fullUrlProxiedNoCacheBustersNonDecoded;
    }

    protected String getFullUrlProxiedWithCacheBustersNonDecoded() {
        if(this.fullUrlProxiedWithCacheBustersNonDecoded == null) {
            this.fullUrlProxiedWithCacheBustersNonDecoded = getServer().getFullUrlProxied(getExchange(), true);
        }
        return this.fullUrlProxiedWithCacheBustersNonDecoded;
    }

    @Override
    public String getFullUrlOriginal() {
        return getFullUrlOriginal(false);
    }

    @Override
    public String getFullUrlOriginal(boolean keepCacheBusters) {

        if(keepCacheBusters) {
            return getFullUrlOriginalWithCacheBustersNonDecoded();
        } else {
            return getFullUrlOriginalNoCacheBustersNonDecoded();
        }
    }

    @Override
    public String getFullUrlProxied() {
        return getFullUrlProxied(false);
    }

    @Override
    public String getFullUrlProxied(boolean keepCacheBusters) {
        if(keepCacheBusters) {
            return getFullUrlProxiedWithCacheBustersNonDecoded();
        } else {
            return getFullUrlProxiedNoCacheBustersNonDecoded();
        }
    }

    @Override
    public String getFullUrl() {
        return getFullUrl(false);
    }

    @Override
    public String getFullUrl(boolean keepCacheBusters) {

        validateFullUrlInfoCache();
        if(keepCacheBusters) {
            return this.fullUrlWithCacheBustersDecoded;
        } else {
            return this.fullUrlNoCacheBustersDecoded;
        }
    }

    protected void validateFullUrlInfoCache() {

        try {
            String urlToUse;

            //==========================================
            // If the URL has been forwarded, we use it
            // as the regular "full Url".
            //==========================================
            String forwardUrl =
                    getRequestContext().variables().getAsString(SpincastConstants.RequestScopedVariables.FORWARD_ROUTE_URL);
            if(forwardUrl != null) {
                urlToUse = forwardUrl;
            } else {
                urlToUse = getFullUrlOriginalWithCacheBustersNonDecoded();
            }

            //==========================================
            // The regular "full URL" is already up-to-date.
            //==========================================
            if(this.fullUrlWithCacheBustersNonDecoded != null && this.fullUrlWithCacheBustersNonDecoded.equals(urlToUse)) {
                return;
            }

            this.fullUrlWithCacheBustersNonDecoded = urlToUse;
            this.fullUrlWithCacheBustersDecoded = URLDecoder.decode(urlToUse, "UTF-8");
            this.fullUrlNoCacheBustersDecoded = getSpincastUtils().removeCacheBusterCodes(this.fullUrlWithCacheBustersDecoded);

            parseUrl();

        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected void parseUrl() {
        parseRequestPath();
        parseQueryString();
        parseQueryStringParams();
    }

    protected void parseRequestPath() {
        try {
            URL url = new URL(this.fullUrlWithCacheBustersDecoded);
            this.requestPathWithCacheBusters = url.getPath();

            url = new URL(this.fullUrlNoCacheBustersDecoded);
            this.requestPathNoCacheBusters = url.getPath();

        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected void parseQueryString() {

        try {
            URL url = new URL(this.fullUrlWithCacheBustersNonDecoded);
            String qs = url.getQuery();
            if(qs == null) {
                qs = "";
            }
            this.queryStringNonDecoded = qs;
            this.queryStringDecoded = URLDecoder.decode(qs, "UTF-8");

        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    public void parseQueryStringParams() {

        try {
            Map<String, List<String>> paramsFinal = new HashMap<String, List<String>>();

            String qs = this.queryStringNonDecoded;
            if(qs == null) {
                parseQueryString();
                qs = this.queryStringNonDecoded;
            }

            List<NameValuePair> params = URLEncodedUtils.parse(qs, Charset.forName("UTF-8"));
            if(params != null) {
                for(NameValuePair nameValuePair : params) {
                    String name = nameValuePair.getName();
                    List<String> values = paramsFinal.get(name);
                    if(values == null) {
                        values = new ArrayList<String>();
                        paramsFinal.put(name, values);
                    }
                    values.add(nameValuePair.getValue());
                }
            }

            //==========================================
            // Make sure everything is immutable.
            //==========================================
            Map<String, List<String>> mapWithInmutableLists = new HashMap<String, List<String>>();
            for(Entry<String, List<String>> entry : paramsFinal.entrySet()) {
                mapWithInmutableLists.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
            }

            this.queryStringParams = Collections.unmodifiableMap(mapWithInmutableLists);

        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public String getRequestPath() {
        return getRequestPath(false);
    }

    @Override
    public String getRequestPath(boolean keepCacheBusters) {

        validateFullUrlInfoCache();

        if(keepCacheBusters) {
            return this.requestPathWithCacheBusters;
        } else {
            return this.requestPathNoCacheBusters;
        }
    }

    @Override
    public String getQueryString(boolean withQuestionMark) {

        validateFullUrlInfoCache();

        if(StringUtils.isBlank(this.queryStringDecoded)) {
            return "";
        }
        return (withQuestionMark ? "?" : "") + this.queryStringDecoded;
    }

    @Override
    public Map<String, List<String>> getQueryStringParams() {

        validateFullUrlInfoCache();
        return this.queryStringParams;
    }

    @Override
    public List<String> getQueryStringParam(String name) {

        List<String> values = getQueryStringParams().get(name);
        if(values == null) {
            values = Collections.emptyList();
        }
        return values;
    }

    @Override
    public String getQueryStringParamFirst(String name) {

        List<String> values = getQueryStringParam(name);
        if(values != null && values.size() > 0) {
            return values.get(0);
        }
        return null;
    }

    @Override
    public Map<String, String> getPathParams() {

        //==========================================
        // We make sure everything is immutable
        //==========================================
        Map<String, String> pathParams = getRequestContext().routing().getCurrentRouteHandlerMatch().getParameters();
        if(pathParams == null) {
            pathParams = Collections.emptyMap();
        } else {
            pathParams = Collections.unmodifiableMap(pathParams);
        }

        return pathParams;
    }

    @Override
    public String getPathParam(String name) {
        return getPathParams().get(name);
    }

    @Override
    public InputStream getBodyAsInputStream() {
        return getServer().getRawInputStream(getExchange());
    }

    @Override
    public String getBodyAsString() {
        return getBodyAsString("UTF-8");
    }

    @Override
    public String getBodyAsString(String encoding) {

        try {

            InputStream inputStream = getBodyAsInputStream();
            if(inputStream == null) {
                return "";
            }
            return IOUtils.toString(inputStream, encoding);

        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public byte[] getBodyAsByteArray() {

        try {
            InputStream inputStream = getBodyAsInputStream();
            if(inputStream == null) {
                return new byte[0];
            }
            return IOUtils.toByteArray(inputStream);

        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public IJsonObject getJsonBodyAsJsonObject() {
        IJsonObject obj = getJsonBody(IJsonObject.class);
        return obj;
    }

    @Override
    public Map<String, Object> getJsonBodyAsMap() {
        @SuppressWarnings("unchecked")
        Map<String, Object> obj = getJsonBody(Map.class);
        return obj;
    }

    @Override
    public <T> T getJsonBody(Class<T> clazz) {
        try {
            InputStream inputStream = getBodyAsInputStream();
            if(inputStream == null) {
                return null;
            }

            return getJsonManager().fromJsonInputStream(inputStream, clazz);

        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public IJsonObject getXmlBodyAsJsonObject() {
        IJsonObject obj = getXmlBody(IJsonObject.class);
        return obj;
    }

    @Override
    public Map<String, Object> getXmlBodyAsMap() {
        @SuppressWarnings("unchecked")
        Map<String, Object> obj = getXmlBody(Map.class);
        return obj;
    }

    @Override
    public <T> T getXmlBody(Class<T> clazz) {
        try {
            InputStream inputStream = getBodyAsInputStream();
            if(inputStream == null) {
                return null;
            }

            return getXmlManager().fromXmlInputStream(inputStream, clazz);

        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public Map<String, List<String>> getFormDatas() {
        if(this.formDatas == null) {

            //==========================================
            // We make sure everything is immutable
            //==========================================
            Map<String, List<String>> formDatasServer = getServer().getFormDatas(getExchange());
            Map<String, List<String>> formDatasFinal = new HashMap<String, List<String>>();

            if(formDatasServer == null) {
                formDatasServer = Collections.emptyMap();
            } else {
                for(Entry<String, List<String>> entry : formDatasServer.entrySet()) {

                    if(entry.getValue() == null) {
                        formDatasFinal.put(entry.getKey(), Collections.<String>emptyList());
                    } else {
                        formDatasFinal.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
                    }
                }
                formDatasFinal = Collections.unmodifiableMap(formDatasFinal);
            }
            this.formDatas = formDatasFinal;
        }
        return this.formDatas;
    }

    @Override
    public List<String> getFormData(String name) {
        List<String> values = getFormDatas().get(name);
        if(values == null) {
            values = Collections.emptyList();
        }
        return values;
    }

    @Override
    public String getFormDataFirst(String name) {
        List<String> values = getFormData(name);
        if(values != null && values.size() > 0) {
            return values.get(0);
        }
        return null;
    }

    @Override
    public Map<String, List<File>> getUploadedFiles() {
        if(this.uploadedFiles == null) {

            //==========================================
            // We make sure everything is immutable
            //==========================================
            Map<String, List<File>> uploadedFilesServer = getServer().getUploadedFiles(getExchange());
            Map<String, List<File>> uploadedFilesFinal = new HashMap<String, List<File>>();

            if(uploadedFilesServer == null) {
                uploadedFilesServer = Collections.emptyMap();
            } else {
                for(Entry<String, List<File>> entry : uploadedFilesServer.entrySet()) {

                    if(entry.getValue() == null) {
                        uploadedFilesFinal.put(entry.getKey(), Collections.<File>emptyList());
                    } else {
                        uploadedFilesFinal.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
                    }
                }
                uploadedFilesFinal = Collections.unmodifiableMap(uploadedFilesFinal);
            }

            this.uploadedFiles = uploadedFilesFinal;
        }
        return this.uploadedFiles;
    }

    @Override
    public List<File> getUploadedFiles(String name) {

        List<File> files = getUploadedFiles().get(name);
        if(files == null) {
            files = Collections.emptyList();
        }
        return files;
    }

    @Override
    public File getUploadedFileFirst(String name) {

        List<File> files = getUploadedFiles(name);
        if(files.size() > 0) {
            return files.get(0);
        }
        return null;
    }

    @Override
    public Locale getLocaleBestMatch() {

        String header = getHeaderFirst(HttpHeaders.ACCEPT_LANGUAGE);
        Locale locale = getSpincastUtils().getLocaleBestMatchFromAcceptLanguageHeader(header);
        if(locale == null) {
            locale = getSpincastConfig().getDefaultLocale();
        }
        return locale;
    }

    @Override
    public String getContentType() {
        String contentType = getHeaderFirst(HttpHeaders.CONTENT_TYPE);
        return contentType;
    }

    @Override
    public boolean isHttps() {
        return getFullUrl().trim().toLowerCase().startsWith("https://");
    }

    @Override
    public List<IETag> getEtagsFromIfMatchHeader() {

        if(this.ifMatchETags == null) {
            synchronized(this.ifMatchETagsLock) {
                if(this.ifMatchETags == null) {
                    this.ifMatchETags = parseETagHeader(HttpHeaders.IF_MATCH);
                }
            }
        }

        return this.ifMatchETags;
    }

    @Override
    public List<IETag> getEtagsFromIfNoneMatchHeader() {

        if(this.ifNoneMatchETags == null) {
            synchronized(this.ifNoneMatchETagsLock) {
                if(this.ifNoneMatchETags == null) {
                    this.ifNoneMatchETags = parseETagHeader(HttpHeaders.IF_NONE_MATCH);
                }
            }
        }

        return this.ifNoneMatchETags;
    }

    protected List<IETag> parseETagHeader(String headerName) {

        List<IETag> etags = new ArrayList<IETag>();

        List<String> eTagHeaders = getHeader(headerName);
        if(eTagHeaders != null) {

            for(String eTagHeader : eTagHeaders) {

                // Thanks to http://stackoverflow.com/a/1757107/843699
                String[] tokens = eTagHeader.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                for(String eTagStr : tokens) {
                    try {
                        IETag eTag = getEtagFactory().deserializeHeaderValue(eTagStr);
                        etags.add(eTag);
                    } catch(Exception ex) {
                        this.logger.info("Invalid " + headerName + " ETag header value received: " + eTagStr);
                    }
                }
            }
        }
        return etags;
    }

    @Override
    public Date getDateFromIfModifiedSinceHeader() {

        if(this.ifModifiedSinceDate == null) {
            synchronized(this.ifModifiedSinceDateLock) {
                if(this.ifModifiedSinceDate == null) {
                    this.ifModifiedSinceDate = parseDateHeader(HttpHeaders.IF_MODIFIED_SINCE);
                }
            }
        }
        return this.ifModifiedSinceDate;
    }

    @Override
    public Date getDateFromIfUnmodifiedSinceHeader() {

        if(this.ifUnmodifiedSinceDate == null) {
            synchronized(this.ifUnmodifiedSinceDateLock) {
                if(this.ifUnmodifiedSinceDate == null) {
                    this.ifUnmodifiedSinceDate = parseDateHeader(HttpHeaders.IF_UNMODIFIED_SINCE);
                }
            }
        }
        return this.ifUnmodifiedSinceDate;
    }

    /**
     * Returns NULL if the date is not there or not parsable.
     */
    protected Date parseDateHeader(String headerName) {
        String value = getHeaderFirst(headerName);
        if(value == null) {
            return null;
        }

        try {
            Date date = DateUtils.parseDate(value);
            return date;
        } catch(Exception ex) {
            this.logger.info("Invalid '" + headerName + "' date received: " + value);
        }
        return null;
    }

}
