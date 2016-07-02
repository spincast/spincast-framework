package org.spincast.plugins.request;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.config.SpincastConstants;
import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.exchange.IRequestRequestContextAddon;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.server.IServer;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.ISpincastUtils;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.xml.IXmlManager;
import org.spincast.shaded.org.apache.commons.io.IOUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.HttpHeaders;
import org.spincast.shaded.org.apache.http.NameValuePair;
import org.spincast.shaded.org.apache.http.client.utils.URLEncodedUtils;

import com.google.inject.Inject;

public class SpincastRequestRequestContextAddon<R extends IRequestContext<?>>
                                               implements IRequestRequestContextAddon<R> {

    private String fullUrlRaw = null;
    private String fullUrl = null;
    private String requestPath = null;
    private String queryStringRaw = "";
    private String queryString = "";

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

    @Inject
    public SpincastRequestRequestContextAddon(R requestContext,
                                              IServer server,
                                              IJsonManager jsonManager,
                                              IXmlManager xmlManager,
                                              ISpincastUtils spincastUtils,
                                              ISpincastConfig spincastConfig) {
        this.requestContext = requestContext;
        this.server = server;
        this.jsonManager = jsonManager;
        this.xmlManager = xmlManager;
        this.spincastUtils = spincastUtils;
        this.spincastConfig = spincastConfig;
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

    protected void validateUrlInfoCache() {

        String url = getRequestContext().variables().getAsString(SpincastConstants.RequestScopedVariables.FORWARD_ROUTE_URL);
        if(url == null) {
            // The original not-forwarded URL route can't change.
            if(this.fullUrlRaw != null) {
                return;
            }
            url = getServer().getFullUrl(getExchange());
        }

        if(!url.equals(this.fullUrlRaw)) {
            this.fullUrlRaw = url;
            try {
                this.fullUrl = URLDecoder.decode(url, "UTF-8");
            } catch(Exception ex) {
                throw SpincastStatics.runtimize(ex);
            }
            parseUrl();
        }
    }

    protected void parseUrl() {
        parseRequestPath();
        parseQueryString();
        parseQueryStringParams();
    }

    protected void parseRequestPath() {
        try {
            URL url = new URL(this.fullUrlRaw);
            this.requestPath = url.getPath();
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected void parseQueryString() {

        try {
            URL url = new URL(this.fullUrlRaw);
            String qs = url.getQuery();
            if(qs == null) {
                qs = "";
            }
            this.queryStringRaw = qs;
            this.queryString = URLDecoder.decode(qs, "UTF-8");

        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    public void parseQueryStringParams() {

        try {
            Map<String, List<String>> paramsFinal = new HashMap<String, List<String>>();

            String qs = this.queryStringRaw;
            if(qs == null) {
                parseQueryString();
                qs = this.queryStringRaw;
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
    public String getFullUrl() {

        validateUrlInfoCache();
        return this.fullUrl;
    }

    @Override
    public String getOriginalFullUrl() {
        return getServer().getFullUrl(getExchange());
    }

    @Override
    public String getRequestPath() {

        validateUrlInfoCache();
        return this.requestPath;
    }

    @Override
    public String getQueryString() {

        validateUrlInfoCache();
        return this.queryString;
    }

    @Override
    public Map<String, List<String>> getQueryStringParams() {

        validateUrlInfoCache();
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

}
