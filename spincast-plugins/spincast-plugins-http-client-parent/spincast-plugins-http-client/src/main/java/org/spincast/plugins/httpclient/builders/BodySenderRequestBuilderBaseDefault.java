package org.spincast.plugins.httpclient.builders;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.cookies.CookieFactory;
import org.spincast.core.json.JsonManager;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.xml.XmlManager;
import org.spincast.plugins.httpclient.FileToUpload;
import org.spincast.plugins.httpclient.HttpResponseFactory;
import org.spincast.plugins.httpclient.SpincastHttpClientConfig;
import org.spincast.plugins.httpclient.utils.SpincastHttpClientUtils;
import org.spincast.shaded.org.apache.http.HttpEntity;
import org.spincast.shaded.org.apache.http.NameValuePair;
import org.spincast.shaded.org.apache.http.client.entity.UrlEncodedFormEntity;
import org.spincast.shaded.org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.spincast.shaded.org.apache.http.client.methods.HttpRequestBase;
import org.spincast.shaded.org.apache.http.entity.StringEntity;
import org.spincast.shaded.org.apache.http.entity.mime.MultipartEntityBuilder;
import org.spincast.shaded.org.apache.http.message.BasicNameValuePair;

/**
 * Implementation for the builders that can send a body or upload files..
 */
public abstract class BodySenderRequestBuilderBaseDefault<T extends BodySenderRequestBuilderBase<?>>
                                                         extends HttpRequestBuilderBase<T>
                                                         implements BodySenderRequestBuilderBase<T> {

    protected final Logger logger = LoggerFactory.getLogger(BodySenderRequestBuilderBaseDefault.class);

    protected enum BodyType {
        STANDARD,
        FORM,
        FILES_TO_UPLOAD
    }

    private final JsonManager jsonManager;
    private final XmlManager xmlManager;
    private Map<String, List<String>> formBodyFields;
    private List<FileToUpload> filesToUpload;
    private HttpEntity standardBody;
    private BodyType bodyType = null;

    /** 
     * Constructor
     */
    public BodySenderRequestBuilderBaseDefault(String url,
                                               CookieFactory cookieFactory,
                                               HttpResponseFactory spincastHttpResponseFactory,
                                               JsonManager jsonManager,
                                               XmlManager xmlManager,
                                               SpincastHttpClientUtils spincastHttpClientUtils,
                                               SpincastHttpClientConfig spincastHttpClientConfig) {
        super(url, cookieFactory, spincastHttpResponseFactory, spincastHttpClientUtils, spincastHttpClientConfig);
        this.jsonManager = jsonManager;
        this.xmlManager = xmlManager;
    }

    protected String getStringBodyEncoding() {
        return "UTF-8";
    }

    protected String getFormBodyEncoding() {
        return "UTF-8";
    }

    protected BodyType getBodyType() {
        return this.bodyType;
    }

    protected HttpEntity getStandardBody() {
        return this.standardBody;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected XmlManager getXmlManager() {
        return this.xmlManager;
    }

    protected Map<String, List<String>> getFormBodyFields() {

        if (this.formBodyFields == null) {
            this.formBodyFields = new HashMap<String, List<String>>();
        }
        return this.formBodyFields;
    }

    protected List<FileToUpload> getFileToUploads() {

        if (this.filesToUpload == null) {
            this.filesToUpload = new ArrayList<FileToUpload>();
        }
        return this.filesToUpload;
    }

    @Override
    public T addFormBodyFieldValue(String name, String value) {

        Objects.requireNonNull(name, "The name can't be NULL");

        if (getBodyType() != null && getBodyType() != BodyType.FORM) {
            this.logger.warn("There was already a body of a different type set ('" + getBodyType() + "'). It will be replaced.");
        }
        this.filesToUpload = null;
        this.standardBody = null;
        this.bodyType = BodyType.FORM;

        Map<String, List<String>> params = getFormBodyFields();
        List<String> values = params.get(name);
        if (values == null) {
            values = new ArrayList<String>();
            params.put(name, values);
        }

        values.add(value);

        @SuppressWarnings("unchecked")
        T t = (T)this;
        return t;
    }

    @Override
    public T setFormBodyField(String fieldName, List<String> values) {

        Objects.requireNonNull(fieldName, "The name can't be NULL");

        if (getBodyType() != null && getBodyType() != BodyType.FORM) {
            this.logger.warn("There was already a body of a different type set ('" + getBodyType() + "'). It will be replaced.");
        }
        this.filesToUpload = null;
        this.standardBody = null;
        this.bodyType = BodyType.FORM;

        getFormBodyFields().put(fieldName, values);

        @SuppressWarnings("unchecked")
        T t = (T)this;
        return t;
    }

    @Override
    public T setFormBodyFields(Map<String, List<String>> formFields) {

        if (getBodyType() != null && getBodyType() != BodyType.FORM) {
            this.logger.warn("There was already a body of a different type set ('" + getBodyType() + "'). It will be replaced.");
        }
        this.filesToUpload = null;
        this.standardBody = null;

        if (formFields != null && formFields.size() > 0) {
            this.bodyType = BodyType.FORM;
        }

        this.formBodyFields = formFields;

        @SuppressWarnings("unchecked")
        T t = (T)this;
        return t;
    }

    @Override
    public T setStringBody(String stringBody, String contentType) {

        Objects.requireNonNull(contentType, "The contentType can't be NULL");

        if (getStandardBody() != null || (getBodyType() != null && getBodyType() != BodyType.STANDARD)) {
            this.logger.warn("There was already a body set ('" + getBodyType() + "'). It will be replaced.");
        }

        StringEntity stringEntity = new StringEntity(stringBody, getStringBodyEncoding());
        stringEntity.setContentType(contentType);

        this.formBodyFields = null;
        this.filesToUpload = null;
        this.standardBody = stringEntity;
        this.bodyType = BodyType.STANDARD;

        @SuppressWarnings("unchecked")
        T t = (T)this;
        return t;
    }

    @Override
    public T setJsonStringBody(Object object) {

        Objects.requireNonNull(object, "The object can't be NULL");

        String jsonString = getJsonManager().toJsonString(object);
        return setStringBody(jsonString, ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset());
    }

    @Override
    public T setXmlStringBody(Object object) {

        Objects.requireNonNull(object, "The object can't be NULL");

        String xml = getXmlManager().toXml(object);
        return setStringBody(xml, ContentTypeDefaults.XML.getMainVariationWithUtf8Charset());
    }

    @Override
    public T setBody(HttpEntity body) {

        if (getStandardBody() != null || (getBodyType() != null && getBodyType() != BodyType.STANDARD)) {
            this.logger.warn("There was already a body set ('" + getBodyType() + "'). It will be replaced.");
        }

        this.formBodyFields = null;
        this.filesToUpload = null;
        this.standardBody = body;
        this.bodyType = BodyType.STANDARD;

        @SuppressWarnings("unchecked")
        T t = (T)this;
        return t;
    }

    @Override
    public T addFileToUploadBody(String path, String name) {
        return addFileToUploadBody(path, false, name);
    }

    @Override
    public T addFileToUploadBody(String path, boolean isClasspathPath, String name) {

        Objects.requireNonNull(path, "The path can't be NULL");

        if (getBodyType() != null && getBodyType() != BodyType.FILES_TO_UPLOAD) {
            this.logger.warn("There was already a body set ('" + getBodyType() + "'). It will be replaced.");
        }

        this.formBodyFields = null;
        this.standardBody = null;
        this.bodyType = BodyType.FILES_TO_UPLOAD;

        try {

            if (isClasspathPath) {
                URL resource = getClass().getClassLoader().getResource(path);
                if (resource == null) {
                    throw new RuntimeException("File to upload not found on the classpath: " + path);
                }
            } else {
                if (!new File(path).isFile()) {
                    throw new RuntimeException("File to upload not found on the file system: " + path);
                }
            }

            FileToUpload fileToUpload = new FileToUpload(path, isClasspathPath, name);
            getFileToUploads().add(fileToUpload);

            @SuppressWarnings("unchecked")
            T t = (T)this;
            return t;

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected List<NameValuePair> convertToNameValuePair(Map<String, List<String>> params) {

        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        if (params != null) {
            for (Entry<String, List<String>> entry : params.entrySet()) {

                String name = entry.getKey();
                if (entry.getValue() != null) {
                    for (String value : entry.getValue()) {
                        NameValuePair nameValuePair = new BasicNameValuePair(name, value);
                        nameValuePairList.add(nameValuePair);
                    }
                }
            }
        }

        return nameValuePairList;
    }

    @Override
    protected HttpRequestBase createMethodSpecificHttpRequest(String url) {

        HttpEntityEnclosingRequestBase request = getHttpEntityEnclosingRequestBase(url);

        try {

            if (getBodyType() != null) {

                if (getBodyType() == BodyType.STANDARD) {
                    if (getStandardBody() != null) {
                        request.setEntity(getStandardBody());
                    }
                } else if (getBodyType() == BodyType.FORM) {
                    if (getFormBodyFields() != null && getFormBodyFields().size() > 0) {
                        request.setEntity(new UrlEncodedFormEntity(convertToNameValuePair(getFormBodyFields()),
                                                                   getFormBodyEncoding()));
                    }
                } else if (getBodyType() == BodyType.FILES_TO_UPLOAD) {
                    if (getFileToUploads() != null && getFileToUploads().size() > 0) {

                        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                        for (FileToUpload fileToUpload : getFileToUploads()) {

                            File file;
                            if (fileToUpload.isClasspathPath()) {

                                URL resource = getClass().getClassLoader().getResource(fileToUpload.getPath());
                                file = new File(resource.toURI());
                            } else {
                                file = new File(fileToUpload.getPath());
                            }
                            builder.addBinaryBody(fileToUpload.getName(), file);
                        }
                        request.setEntity(builder.build());
                    }
                } else {
                    throw new RuntimeException("Not managed: " + getBodyType());
                }
            }
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
        return request;
    }

    protected abstract HttpEntityEnclosingRequestBase getHttpEntityEnclosingRequestBase(String url);

}
