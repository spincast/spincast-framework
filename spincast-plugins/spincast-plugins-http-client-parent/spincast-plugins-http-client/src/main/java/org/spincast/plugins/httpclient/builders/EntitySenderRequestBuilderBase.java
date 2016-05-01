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
import org.spincast.core.cookies.ICookieFactory;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.xml.IXmlManager;
import org.spincast.plugins.httpclient.FileToUpload;
import org.spincast.plugins.httpclient.IHttpResponseFactory;
import org.spincast.plugins.httpclient.IRequestBuilderBase;
import org.spincast.shaded.org.apache.http.HttpEntity;
import org.spincast.shaded.org.apache.http.NameValuePair;
import org.spincast.shaded.org.apache.http.client.entity.UrlEncodedFormEntity;
import org.spincast.shaded.org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.spincast.shaded.org.apache.http.client.methods.HttpRequestBase;
import org.spincast.shaded.org.apache.http.entity.StringEntity;
import org.spincast.shaded.org.apache.http.entity.mime.MultipartEntityBuilder;
import org.spincast.shaded.org.apache.http.message.BasicNameValuePair;

/**
 * Implementation for the builders that can send an entity, or upload files..
 */
public abstract class EntitySenderRequestBuilderBase<T extends IRequestBuilderBase<?>>
                                                    extends SpincastRequestBuilderBase<T>
                                                    implements IEntitySenderRequestBuilderBase<T> {

    protected final Logger logger = LoggerFactory.getLogger(EntitySenderRequestBuilderBase.class);

    protected enum EntityType {
        HTTP_ENTITY,
        FORM_DATAS,
        FILE_UPLOADS
    }

    private final IJsonManager jsonManager;
    private final IXmlManager xmlManager;
    private Map<String, List<String>> entityFormDatas;
    private List<FileToUpload> entityFileUploads;
    private HttpEntity entity;
    private EntityType entityType = null;

    /**
     * Constructor
     */
    public EntitySenderRequestBuilderBase(String url,
                                          ICookieFactory cookieFactory,
                                          IHttpResponseFactory spincastHttpResponseFactory,
                                          IJsonManager jsonManager,
                                          IXmlManager xmlManager) {
        super(url, cookieFactory, spincastHttpResponseFactory);
        this.jsonManager = jsonManager;
        this.xmlManager = xmlManager;
    }

    protected String getEntityStringEncoding() {
        return "UTF-8";
    }

    protected String getEntityFormDatasEncoding() {
        return "UTF-8";
    }

    protected EntityType getEntityType() {
        return this.entityType;
    }

    protected HttpEntity getEntity() {
        return this.entity;
    }

    protected IJsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected IXmlManager getXmlManager() {
        return this.xmlManager;
    }

    protected Map<String, List<String>> getEntityFormDatas() {

        if(this.entityFormDatas == null) {
            this.entityFormDatas = new HashMap<String, List<String>>();
        }
        return this.entityFormDatas;
    }

    protected List<FileToUpload> getEntityFileUploads() {

        if(this.entityFileUploads == null) {
            this.entityFileUploads = new ArrayList<FileToUpload>();
        }
        return this.entityFileUploads;
    }

    @Override
    public T addEntityFormDataValue(String name, String value) {

        Objects.requireNonNull(name, "The name can't be NULL");

        if(getEntityType() != null && getEntityType() != EntityType.FORM_DATAS) {
            this.logger.warn("There was already an entity of a different type set. It will be replaced.");
        }
        this.entityFileUploads = null;
        this.entity = null;
        this.entityType = EntityType.FORM_DATAS;

        Map<String, List<String>> params = getEntityFormDatas();
        List<String> values = params.get(name);
        if(values == null) {
            values = new ArrayList<String>();
            params.put(name, values);
        }

        values.add(value);

        @SuppressWarnings("unchecked")
        T t = (T)this;
        return t;
    }

    @Override
    public T setEntityFormData(String name, List<String> values) {

        Objects.requireNonNull(name, "The name can't be NULL");

        if(getEntityType() != null && getEntityType() != EntityType.FORM_DATAS) {
            this.logger.warn("There was already an entity of a different type set. It will be replaced.");
        }
        this.entityFileUploads = null;
        this.entity = null;
        this.entityType = EntityType.FORM_DATAS;

        getEntityFormDatas().put(name, values);

        @SuppressWarnings("unchecked")
        T t = (T)this;
        return t;
    }

    @Override
    public T setEntityFormDatas(Map<String, List<String>> formDatas) {

        if(getEntityType() != null && getEntityType() != EntityType.FORM_DATAS) {
            this.logger.warn("There was already an entity of a different type set. It will be replaced.");
        }
        this.entityFileUploads = null;
        this.entity = null;

        if(formDatas != null && formDatas.size() > 0) {
            this.entityType = EntityType.FORM_DATAS;
        }

        this.entityFormDatas = formDatas;

        @SuppressWarnings("unchecked")
        T t = (T)this;
        return t;
    }

    @Override
    public T setEntityString(String entityString, String contentType) {

        Objects.requireNonNull(contentType, "The contentType can't be NULL");

        if(getEntity() != null || (getEntityType() != null && getEntityType() != EntityType.HTTP_ENTITY)) {
            this.logger.warn("There was already an entity set. It will be replaced.");
        }

        StringEntity stringEntity = new StringEntity(entityString, getEntityStringEncoding());
        stringEntity.setContentType(contentType);

        this.entityFormDatas = null;
        this.entityFileUploads = null;
        this.entity = stringEntity;
        this.entityType = EntityType.HTTP_ENTITY;

        @SuppressWarnings("unchecked")
        T t = (T)this;
        return t;
    }

    @Override
    public T setEntityJson(Object object) {

        Objects.requireNonNull(object, "The object can't be NULL");

        String jsonString = getJsonManager().toJsonString(object);
        return setEntityString(jsonString, ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset());
    }

    @Override
    public T setEntityXml(Object object) {

        Objects.requireNonNull(object, "The object can't be NULL");

        String xml = getXmlManager().toXml(object);
        return setEntityString(xml, ContentTypeDefaults.XML.getMainVariationWithUtf8Charset());
    }

    @Override
    public T setEntity(HttpEntity entity) {

        if(getEntity() != null || (getEntityType() != null && getEntityType() != EntityType.HTTP_ENTITY)) {
            this.logger.warn("There was already an entity set. It will be replaced.");
        }

        this.entityFormDatas = null;
        this.entityFileUploads = null;
        this.entity = entity;
        this.entityType = EntityType.HTTP_ENTITY;

        @SuppressWarnings("unchecked")
        T t = (T)this;
        return t;
    }

    @Override
    public T addEntityFileUpload(String path, String name) {
        return addEntityFileUpload(path, false, name);
    }

    @Override
    public T addEntityFileUpload(String path, boolean isClasspathPath, String name) {

        Objects.requireNonNull(path, "The path can't be NULL");
        Objects.requireNonNull(name, "The name can't be NULL");

        if(getEntityType() != null && getEntityType() != EntityType.FILE_UPLOADS) {
            this.logger.warn("There was already an entity set. It will be replaced.");
        }

        this.entityFormDatas = null;
        this.entity = null;
        this.entityType = EntityType.FILE_UPLOADS;

        try {

            if(isClasspathPath) {

                URL resource = getClass().getClassLoader().getResource(path);
                if(resource == null) {
                    throw new RuntimeException("File to upload not found on the classpath: " + path);
                }
            } else {
                if(!new File(path).isFile()) {
                    throw new RuntimeException("File to upload not found on the file system: " + path);
                }
            }

            FileToUpload fileToUpload = new FileToUpload(path, isClasspathPath, name);

            getEntityFileUploads().add(fileToUpload);

            @SuppressWarnings("unchecked")
            T t = (T)this;
            return t;

        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected List<NameValuePair> convertToNameValuePair(Map<String, List<String>> params) {

        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        if(params != null) {
            for(Entry<String, List<String>> entry : params.entrySet()) {

                String name = entry.getKey();
                if(entry.getValue() != null) {
                    for(String value : entry.getValue()) {
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

            if(getEntityType() != null) {

                if(getEntityType() == EntityType.HTTP_ENTITY) {
                    if(getEntity() != null) {
                        request.setEntity(getEntity());
                    }
                } else if(getEntityType() == EntityType.FORM_DATAS) {
                    if(getEntityFormDatas() != null && getEntityFormDatas().size() > 0) {
                        request.setEntity(new UrlEncodedFormEntity(convertToNameValuePair(getEntityFormDatas()),
                                                                   getEntityFormDatasEncoding()));
                    }
                } else if(getEntityType() == EntityType.FILE_UPLOADS) {
                    if(getEntityFileUploads() != null && getEntityFileUploads().size() > 0) {

                        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                        for(FileToUpload fileToUpload : getEntityFileUploads()) {

                            File file;
                            if(fileToUpload.isClasspathPath()) {

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
                    throw new RuntimeException("Not managed: " + getEntityType());
                }
            }
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
        return request;
    }

    protected abstract HttpEntityEnclosingRequestBase getHttpEntityEnclosingRequestBase(String url);

}
