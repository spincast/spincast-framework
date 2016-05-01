package org.spincast.plugins.httpclient.builders;

import java.util.List;
import java.util.Map;

import org.spincast.plugins.httpclient.IRequestBuilderBase;
import org.spincast.shaded.org.apache.http.HttpEntity;

/**
 * Builders that can send an entity, or upload files.
 */
public interface IEntitySenderRequestBuilderBase<T extends IRequestBuilderBase<?>> extends IRequestBuilderBase<T> {

    /**
     * Sets the Form datas entity.
     * A form data can contain more than one value.
     * 
     * Overwrites any existing Form datas.
     * 
     * Only one type of entity can be set amongs:
     * - Form datas entity
     * - String entity
     * - File upload
     * - Custom <code>HttpEntity</code>
     * 
     * When you set or add an entity of a new type, the
     * existing entity is overwritten.
     * 
     */
    public T setEntityFormDatas(Map<String, List<String>> params);

    /**
     * Sets a Form data entity.
     * A form data can contain more than one value.
     * 
     * Overwrites the existing Form data of the same
     * name, but keeps the other ones.
     * 
     * Only one type of entity can be set amongs:
     * - Form datas entity
     * - String entity
     * - File upload
     * - Custom <code>HttpEntity</code>
     * 
     * When you set or add an entity of a new type, the
     * existing entity is overwritten.
     * 
     */
    public T setEntityFormData(String name, List<String> values);

    /**
     * Adds a value to a Form data.
     * 
     * Keeps the existing values.
     * 
     * Only one type of entity can be set amongs:
     * - Form datas entity
     * - String entity
     * - File upload
     * - Custom <code>HttpEntity</code>
     * 
     * When you set or add an entity of a new type, the
     * existing entity is overwritten.
     * 
     */
    public T addEntityFormDataValue(String name, String value);

    /**
     * Sets a String entity.
     * 
     * Only one type of entity can be set amongs:
     * - Form datas entity
     * - String entity
     * - File upload
     * - Custom <code>HttpEntity</code>
     * 
     * When you set or add an entity of a new type, the
     * existing entity is overwritten.
     */
    public T setEntityString(String entity, String contentType);

    /**
     * Sets an custom <code>HttpEntity</code> entity to be sent.
     * 
     * Only one type of entity can be set amongs:
     * - Form datas entity
     * - String entity
     * - File upload
     * - custom <code>HttpEntity</code>
     * 
     * When you set or add an entity of a new type, the
     * existing entity is overwritten.
     */
    public T setEntity(HttpEntity entity);

    /**
     * Sets a <code>Json</code> entity to be sent.
     * The object will be converted to a <code>Json</code>'s
     * String representation and sent using the <code>application/json</code>
     * Content-Type.
     * 
     * Only one type of entity can be set amongs:
     * - Form datas entity
     * - String entity
     * - File upload
     * - Custom <code>HttpEntity</code>
     * 
     * When you set or add an entity of a new type, the
     * existing entity is overwritten.
     */
    public T setEntityJson(Object object);

    /**
     * Sets a <code>XML</code> entity to be sent.
     * The object will be converted to a <code>XML</code>
     * and sent using the <code>application/xml</code>
     * Content-Type.
     * 
     * Only one type of entity can be set amongs:
     * - Form datas entity
     * - String entity
     * - File upload
     * - Custom <code>HttpEntity</code>
     * 
     * When you set or add an entity of a new type, the
     * existing entity is overwritten.
     */
    public T setEntityXml(Object object);

    /**
     * Adds a file to upload. More than one file
     * can be uploaded at one time. The specified file
     * is added to the existing ones.
     * 
     * Only one type of entity can be set amongs:
     * - Form datas entity
     * - String entity
     * - File upload
     * - Custom <code>HttpEntity</code>
     * 
     * @param path the path to the file to upload,
     * on the file system
     * 
     * @param name the name to use for the uploaded file.
     */
    public T addEntityFileUpload(String path, String name);

    /**
     * Adds a file to upload. More than one file
     * can be uploaded at one time. The specified file
     * is added to the existing ones.
     * 
     * 
     * Only one type of entity can be set amongs:
     * - Form datas entity
     * - String entity
     * - File upload
     * - Custom <code>HttpEntity</code>
     * 
     * @param path the path to the file to upload
     * 
     * @param isClasspathPath if <code>true</code>, the path to the file to upload is
     * on the classpath, otherwise, it's on the file system.
     * 
     * @param name the name to use for the uploaded file.
     * 
     */
    public T addEntityFileUpload(String path, boolean isClasspathPath, String name);

}
