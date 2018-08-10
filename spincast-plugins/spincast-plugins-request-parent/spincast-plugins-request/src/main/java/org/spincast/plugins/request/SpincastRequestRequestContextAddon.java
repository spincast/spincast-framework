package org.spincast.plugins.request;

import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.config.SpincastConstants;
import org.spincast.core.dictionary.Dictionary;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.exchange.RequestRequestContextAddon;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.request.Form;
import org.spincast.core.request.FormFactory;
import org.spincast.core.routing.ETag;
import org.spincast.core.routing.ETagFactory;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.server.Server;
import org.spincast.core.server.UploadedFile;
import org.spincast.core.session.FlashMessage;
import org.spincast.core.session.FlashMessagesHolder;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.core.xml.XmlManager;
import org.spincast.shaded.org.apache.commons.io.IOUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.HttpHeaders;
import org.spincast.shaded.org.apache.http.NameValuePair;
import org.spincast.shaded.org.apache.http.client.utils.DateUtils;
import org.spincast.shaded.org.apache.http.client.utils.URLEncodedUtils;

import com.google.inject.Inject;

public class SpincastRequestRequestContextAddon<R extends RequestContext<?>>
                                               implements RequestRequestContextAddon<R> {

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

    private List<ETag> ifMatchETags = null;
    private Object ifMatchETagsLock = new Object();
    private List<ETag> ifNoneMatchETags = null;
    private Object ifNoneMatchETagsLock = new Object();

    private Date ifModifiedSinceDate = null;
    private Object ifModifiedSinceDateLock = new Object();

    private Date ifUnmodifiedSinceDate = null;
    private Object ifUnmodifiedSinceDateLock = new Object();

    private Map<String, List<String>> queryStringParams;
    private Map<String, List<String>> formDatasAsImmutableMap;
    private JsonObject formDatasAsImmutableJsonObject;
    private Map<String, List<UploadedFile>> uploadedFiles;
    private Map<String, List<String>> headers;
    private Map<String, Form> forms;

    private final R requestContext;
    private final Server server;
    private final JsonManager jsonManager;
    private final XmlManager xmlManager;
    private final SpincastUtils spincastUtils;
    private final SpincastConfig spincastConfig;
    private final ETagFactory etagFactory;
    private final FlashMessagesHolder flashMessagesHolder;
    private final Dictionary dictionary;

    private final FormFactory formFactory;

    private boolean flashMessageRetrieved = false;
    private FlashMessage flashMessage;

    private Pattern formDataArrayPattern;

    private Map<String, String> cookies;

    @Inject
    public SpincastRequestRequestContextAddon(R requestContext,
                                              Server server,
                                              JsonManager jsonManager,
                                              XmlManager xmlManager,
                                              SpincastUtils spincastUtils,
                                              SpincastConfig spincastConfig,
                                              ETagFactory etagFactory,
                                              FlashMessagesHolder flashMessagesHolder,
                                              FormFactory formFactory,
                                              Dictionary dictionary) {
        this.requestContext = requestContext;
        this.server = server;
        this.jsonManager = jsonManager;
        this.xmlManager = xmlManager;
        this.spincastUtils = spincastUtils;
        this.spincastConfig = spincastConfig;
        this.etagFactory = etagFactory;
        this.flashMessagesHolder = flashMessagesHolder;
        this.formFactory = formFactory;
        this.dictionary = dictionary;
    }

    protected R getRequestContext() {
        return this.requestContext;
    }

    protected Server getServer() {
        return this.server;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected XmlManager getXmlManager() {
        return this.xmlManager;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected ETagFactory getEtagFactory() {
        return this.etagFactory;
    }

    protected FlashMessagesHolder getFlashMessagesHolder() {
        return this.flashMessagesHolder;
    }

    protected FormFactory getFormFactory() {
        return this.formFactory;
    }

    protected Dictionary getDictionary() {
        return this.dictionary;
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

    protected Pattern getFormDataArrayPattern() {

        if (this.formDataArrayPattern == null) {
            this.formDataArrayPattern = Pattern.compile(".*(\\[(0|[1-9]+[0-9]?)\\])$");
        }
        return this.formDataArrayPattern;
    }

    @Override
    public String getCookie(String name) {
        return getCookies().get(name);
    }

    @Override
    public Map<String, String> getCookies() {
        if (this.cookies == null) {
            this.cookies = getServer().getCookies(getRequestContext().exchange());
        }
        return this.cookies;
    }

    @Override
    public boolean isCookiesEnabledValidated() {
        return getCookies().size() > 0;
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        if (this.headers == null) {

            //==========================================
            // We make sure everything is immutable
            //==========================================
            Map<String, List<String>> headersServer = getServer().getRequestHeaders(getExchange());

            // We use a TreeMap with String.CASE_INSENSITIVE_ORDER so the
            // "get" is case insensitive!
            Map<String, List<String>> headersFinal = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);

            if (headersServer == null) {
                headersServer = Collections.emptyMap();
            } else {
                for (Entry<String, List<String>> entry : headersServer.entrySet()) {

                    if (entry.getValue() == null) {
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

    protected String getFullUrlOriginalNoCacheBustersNonDecoded() {
        if (this.fullUrlOriginalNoCacheBustersNonDecoded == null) {
            this.fullUrlOriginalNoCacheBustersNonDecoded = getServer().getFullUrlOriginal(getExchange(), false);
        }
        return this.fullUrlOriginalNoCacheBustersNonDecoded;
    }

    protected String getFullUrlOriginalWithCacheBustersNonDecoded() {
        if (this.fullUrlOriginalWithCacheBustersNonDecoded == null) {
            this.fullUrlOriginalWithCacheBustersNonDecoded = getServer().getFullUrlOriginal(getExchange(), true);
        }
        return this.fullUrlOriginalWithCacheBustersNonDecoded;
    }

    protected String getFullUrlProxiedNoCacheBustersNonDecoded() {
        if (this.fullUrlProxiedNoCacheBustersNonDecoded == null) {
            this.fullUrlProxiedNoCacheBustersNonDecoded = getServer().getFullUrlProxied(getExchange(), false);
        }
        return this.fullUrlProxiedNoCacheBustersNonDecoded;
    }

    protected String getFullUrlProxiedWithCacheBustersNonDecoded() {
        if (this.fullUrlProxiedWithCacheBustersNonDecoded == null) {
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

        if (keepCacheBusters) {
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
        if (keepCacheBusters) {
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
        if (keepCacheBusters) {
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
            if (forwardUrl != null) {
                urlToUse = forwardUrl;
            } else {
                urlToUse = getFullUrlOriginalWithCacheBustersNonDecoded();
            }

            //==========================================
            // The regular "full URL" is already up-to-date.
            //==========================================
            if (this.fullUrlWithCacheBustersNonDecoded != null && this.fullUrlWithCacheBustersNonDecoded.equals(urlToUse)) {
                return;
            }

            this.fullUrlWithCacheBustersNonDecoded = urlToUse;
            this.fullUrlWithCacheBustersDecoded = URLDecoder.decode(urlToUse, "UTF-8");
            this.fullUrlNoCacheBustersDecoded = getSpincastUtils().removeCacheBusterCodes(this.fullUrlWithCacheBustersDecoded);

            parseUrl();

        } catch (Exception ex) {
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

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected void parseQueryString() {

        try {
            URL url = new URL(this.fullUrlWithCacheBustersNonDecoded);
            String qs = url.getQuery();
            if (qs == null) {
                qs = "";
            }
            this.queryStringNonDecoded = qs;
            this.queryStringDecoded = URLDecoder.decode(qs, "UTF-8");

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    public void parseQueryStringParams() {

        try {
            Map<String, List<String>> paramsFinal = new HashMap<String, List<String>>();

            String qs = this.queryStringNonDecoded;
            if (qs == null) {
                parseQueryString();
                qs = this.queryStringNonDecoded;
            }

            List<NameValuePair> params = URLEncodedUtils.parse(qs, Charset.forName("UTF-8"));
            if (params != null) {
                for (NameValuePair nameValuePair : params) {
                    String name = nameValuePair.getName();
                    List<String> values = paramsFinal.get(name);
                    if (values == null) {
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
            for (Entry<String, List<String>> entry : paramsFinal.entrySet()) {
                mapWithInmutableLists.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
            }

            this.queryStringParams = Collections.unmodifiableMap(mapWithInmutableLists);

        } catch (Exception ex) {
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

        if (keepCacheBusters) {
            return this.requestPathWithCacheBusters;
        } else {
            return this.requestPathNoCacheBusters;
        }
    }

    @Override
    public String getQueryString(boolean withQuestionMark) {

        validateFullUrlInfoCache();

        if (StringUtils.isBlank(this.queryStringDecoded)) {
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
        if (values == null) {
            values = Collections.emptyList();
        }
        return values;
    }

    @Override
    public String getQueryStringParamFirst(String name) {

        List<String> values = getQueryStringParam(name);
        if (values != null && values.size() > 0) {
            return values.get(0);
        }
        return null;
    }

    @Override
    public Map<String, String> getPathParams() {

        //==========================================
        // We make sure everything is immutable
        //==========================================
        Map<String, String> pathParams = getRequestContext().routing().getCurrentRouteHandlerMatch().getPathParams();
        if (pathParams == null) {
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
            if (inputStream == null) {
                return "";
            }
            return IOUtils.toString(inputStream, encoding);

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public byte[] getBodyAsByteArray() {

        try {
            InputStream inputStream = getBodyAsInputStream();
            if (inputStream == null) {
                return new byte[0];
            }
            return IOUtils.toByteArray(inputStream);

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public JsonObject getJsonBody() {
        JsonObject obj = getJsonBody(JsonObject.class);
        return obj.clone(false);
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
            if (inputStream == null) {
                return null;
            }

            return getJsonManager().fromInputStream(inputStream, clazz);

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public JsonObject getXmlBodyAsJsonObject() {
        JsonObject obj = getXmlBody(JsonObject.class);
        return obj.clone(false);
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
            if (inputStream == null) {
                return null;
            }

            return getXmlManager().fromXmlInputStream(inputStream, clazz);

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public Map<String, List<String>> getFormDataRaw() {
        if (this.formDatasAsImmutableMap == null) {

            Map<String, List<String>> formDatasServer = getServer().getFormData(getExchange());
            Map<String, List<String>> formDatasFinal = new HashMap<String, List<String>>();

            if (formDatasServer == null) {
                formDatasServer = Collections.emptyMap();
            } else {
                for (Entry<String, List<String>> entry : formDatasServer.entrySet()) {

                    if (entry.getValue() == null) {
                        formDatasFinal.put(entry.getKey(), Collections.<String>emptyList());
                    } else {
                        formDatasFinal.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
                    }
                }
                formDatasFinal = Collections.unmodifiableMap(formDatasFinal);
            }
            this.formDatasAsImmutableMap = formDatasFinal;
        }
        return this.formDatasAsImmutableMap;
    }

    @Override
    public JsonObject getFormData() {

        if (this.formDatasAsImmutableJsonObject == null) {

            Map<String, List<String>> formDatasRaw = getFormDataRaw();

            Map<String, Map<Integer, String>> formDataArrays = new LinkedHashMap<String, Map<Integer, String>>();
            JsonObject obj = getJsonManager().create();
            for (Entry<String, List<String>> entry : formDatasRaw.entrySet()) {

                String key = entry.getKey();
                if (key == null) {
                    continue;
                }

                List<String> values = entry.getValue();

                //==========================================
                // If the key ends with "[X]" we considere
                // that it is part of an *ordered* array, which
                // means the position of the elements are defined.
                //==========================================
                Matcher matcher = getFormDataArrayPattern().matcher(key);
                if (matcher.matches()) {

                    if (values.size() > 1) {
                        this.logger.error("More than one Form Data received with the array " +
                                          "index name \"" + key + "\", " +
                                          "we'll only keep the last element.");

                        values = values.subList(values.size() - 1, values.size());
                    }
                    String value = values.get(0);

                    String arrayPart = matcher.group(1);
                    String indexStr = matcher.group(2);

                    Integer index = Integer.parseInt(indexStr);

                    key = key.substring(0, key.length() - arrayPart.length());

                    Map<Integer, String> valuesMap = formDataArrays.get(key);
                    if (valuesMap == null) {

                        //==========================================
                        // TreeMap : we sort the values using there
                        // required position in the final array.
                        //==========================================
                        valuesMap = new TreeMap<>();
                        formDataArrays.put(key, valuesMap);
                    }
                    valuesMap.put(index, value);
                } else {

                    //==========================================
                    // If there are multiple elements with the same key,
                    // we create an array.
                    // Also if the key ends with "[]" we also considere
                    // the element as being is part of an array.
                    //
                    // The elemens in this array are ordred as they
                    // are received.
                    //==========================================
                    if (values.size() > 1 || key.endsWith("[]")) {

                        if (key.endsWith("[]")) {
                            key = key.substring(0, key.length() - "[]".length());
                        }

                        JsonArray array = getJsonManager().createArray();
                        array.addAll(values);
                        obj.put(key, array);
                    } else {
                        if (values.size() > 0) {
                            obj.put(key, values.get(0));
                        } else {
                            obj.put(key, null);
                        }
                    }
                }
            }

            //==========================================
            // We add the ordered arrays
            //==========================================
            for (Entry<String, Map<Integer, String>> entry : formDataArrays.entrySet()) {

                String key = entry.getKey();
                Map<Integer, String> valuesMap = entry.getValue();

                //==========================================
                // The base array may already exist...
                //==========================================
                JsonArray array;
                Object arrayObj = obj.getObject(key);
                if (arrayObj != null && (arrayObj instanceof JsonArray)) {
                    array = (JsonArray)arrayObj;
                } else {
                    array = getJsonManager().createArray();
                }

                LinkedList<Integer> indexes = new LinkedList<Integer>(valuesMap.keySet());
                Integer lastIndex = indexes.getLast();
                for (int i = 0; i <= lastIndex; i++) {
                    if (valuesMap.containsKey(i)) {
                        array.add(valuesMap.get(i));
                    } else if (!array.isElementExists(i)) {
                        array.add(null);
                    }
                }

                obj.put(key, array);
            }

            //==========================================
            // We make it immutable! This allows the caching
            // of already evaluated JsonPaths...
            //==========================================
            this.formDatasAsImmutableJsonObject = obj.clone(false);
        }

        return this.formDatasAsImmutableJsonObject;
    }

    @Override
    public Form getForm(String name) {

        if (StringUtils.isBlank(name)) {

            //==========================================
            // This message doesn't have to be localized, but
            // we do it anyway simply as an example on how
            // plugins can add messages to the
            // Dictionary...
            //==========================================
            String msg = getDictionary().get(SpincastRequestPluginDictionaryEntries.MESSAGE_KEY_FORM_GET_EMPTYNAME);
            throw new RuntimeException(msg);
        }

        if (this.forms == null) {
            this.forms = new HashMap<String, Form>();
        }

        if (!this.forms.containsKey(name)) {
            JsonObject formData = getFormData().getJsonObjectOrEmpty(name);
            Form form = getFormFactory().createForm(name, formData);
            this.forms.put(name, form);
        }

        Form form = this.forms.get(name);
        return form;
    }

    @Override
    public Map<String, List<UploadedFile>> getUploadedFiles() {
        if (this.uploadedFiles == null) {

            //==========================================
            // We make sure everything is immutable
            //==========================================
            Map<String, List<UploadedFile>> uploadedFilesServer = getServer().getUploadedFiles(getExchange());
            Map<String, List<UploadedFile>> uploadedFilesFinal = new HashMap<String, List<UploadedFile>>();

            if (uploadedFilesServer == null) {
                uploadedFilesServer = Collections.emptyMap();
            } else {
                for (Entry<String, List<UploadedFile>> entry : uploadedFilesServer.entrySet()) {

                    if (entry.getValue() == null) {
                        uploadedFilesFinal.put(entry.getKey(), Collections.<UploadedFile>emptyList());
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
    public List<UploadedFile> getUploadedFiles(String name) {

        List<UploadedFile> files = getUploadedFiles().get(name);
        if (files == null) {
            files = Collections.emptyList();
        }
        return files;
    }

    @Override
    public UploadedFile getUploadedFileFirst(String name) {

        List<UploadedFile> files = getUploadedFiles(name);
        if (files.size() > 0) {
            return files.get(0);
        }
        return null;
    }

    @Override
    public Locale getLocaleBestMatch() {

        String header = getHeaderFirst(HttpHeaders.ACCEPT_LANGUAGE);
        Locale locale = getSpincastUtils().getLocaleBestMatchFromAcceptLanguageHeader(header);
        if (locale == null) {
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
    public List<ETag> getEtagsFromIfMatchHeader() {

        if (this.ifMatchETags == null) {
            synchronized (this.ifMatchETagsLock) {
                if (this.ifMatchETags == null) {
                    this.ifMatchETags = parseETagHeader(HttpHeaders.IF_MATCH);
                }
            }
        }

        return this.ifMatchETags;
    }

    @Override
    public List<ETag> getEtagsFromIfNoneMatchHeader() {

        if (this.ifNoneMatchETags == null) {
            synchronized (this.ifNoneMatchETagsLock) {
                if (this.ifNoneMatchETags == null) {
                    this.ifNoneMatchETags = parseETagHeader(HttpHeaders.IF_NONE_MATCH);
                }
            }
        }

        return this.ifNoneMatchETags;
    }

    protected List<ETag> parseETagHeader(String headerName) {

        List<ETag> etags = new ArrayList<ETag>();

        List<String> eTagHeaders = getHeader(headerName);
        if (eTagHeaders != null) {

            for (String eTagHeader : eTagHeaders) {

                // Thanks to http://stackoverflow.com/a/1757107/843699
                String[] tokens = eTagHeader.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                for (String eTagStr : tokens) {
                    try {
                        ETag eTag = getEtagFactory().deserializeHeaderValue(eTagStr);
                        etags.add(eTag);
                    } catch (Exception ex) {
                        this.logger.info("Invalid " + headerName + " ETag header value received: " + eTagStr);
                    }
                }
            }
        }
        return etags;
    }

    @Override
    public Date getDateFromIfModifiedSinceHeader() {

        if (this.ifModifiedSinceDate == null) {
            synchronized (this.ifModifiedSinceDateLock) {
                if (this.ifModifiedSinceDate == null) {
                    this.ifModifiedSinceDate = parseDateHeader(HttpHeaders.IF_MODIFIED_SINCE);
                }
            }
        }
        return this.ifModifiedSinceDate;
    }

    @Override
    public Date getDateFromIfUnmodifiedSinceHeader() {

        if (this.ifUnmodifiedSinceDate == null) {
            synchronized (this.ifUnmodifiedSinceDateLock) {
                if (this.ifUnmodifiedSinceDate == null) {
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
        if (value == null) {
            return null;
        }

        try {
            Date date = DateUtils.parseDate(value);
            return date;
        } catch (Exception ex) {
            this.logger.info("Invalid '" + headerName + "' date received: " + value);
        }
        return null;
    }

    @Override
    public boolean isFlashMessageExists() {
        return getFlashMessage(false) != null;
    }

    @Override
    public FlashMessage getFlashMessage() {
        if (!this.flashMessageRetrieved) {
            this.flashMessageRetrieved = true;
            this.flashMessage = getFlashMessage(true);
        }

        return this.flashMessage;
    }

    protected FlashMessage getFlashMessage(boolean removeIt) {

        String flashMessageId = null;

        //==========================================
        // TODO Maybe we should use a user session.
        //==========================================
        flashMessageId = getCookie(getSpincastConfig().getCookieNameFlashMessage());
        if (flashMessageId != null) {
            if (removeIt) {
                getRequestContext().response().deleteCookie(getSpincastConfig().getCookieNameFlashMessage());
            }
        //==========================================@formatter:off 
        // In the queryString?
        //==========================================@formatter:on
        } else {
            flashMessageId = getQueryStringParamFirst(getSpincastConfig().getQueryParamFlashMessageId());
        }

        FlashMessage flashMessage = null;
        if (flashMessageId != null) {
            flashMessage = getFlashMessagesHolder().getFlashMessage(flashMessageId, removeIt);
        }
        return flashMessage;
    }

    @Override
    public String getIp() {
        return getServer().getIp(getExchange());
    }


}
