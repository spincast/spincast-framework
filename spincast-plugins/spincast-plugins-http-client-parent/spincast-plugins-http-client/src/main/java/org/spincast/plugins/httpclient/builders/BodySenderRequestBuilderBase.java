package org.spincast.plugins.httpclient.builders;

import java.util.List;
import java.util.Map;

import org.spincast.plugins.httpclient.HttpRequestBuilder;
import org.spincast.shaded.org.apache.http.HttpEntity;

/**
 * Builders that can send a body, or upload files.
 */
public interface BodySenderRequestBuilderBase<T extends BodySenderRequestBuilderBase<?>>
                                             extends HttpRequestBuilder<T> {

    /**
     * Sets an custom {@link HttpEntity} body to be sent.
     * <p>
     * Only one type of body can be set amongs:
     * <ul>
     *     <li>
     *         Form body
     *     </li>
     *     <li>
     *         String body
     *     </li>
     *     <li>
     *         File to upload body
     *     </li>
     *     <li>
     *         Custom {@link HttpEntity} body
     *     </li>
     * </ul>
     * <p>
     * When you set or add something to a new type of body, 
     * the existing body is overwritten, if there is one.
     */
    public T setBody(HttpEntity entity);

    /**
     * Sets fields on the form body.
     * <p>
     * A form body can contain more than one fields
     * and each field may have multiple values.
     * <p>
     * Overwrites any existing Form fields.
     * <p>
     * Only one type of body can be set amongs:
     * <ul>
     *     <li>
     *         Form body
     *     </li>
     *     <li>
     *         String body
     *     </li>
     *     <li>
     *         File to upload body
     *     </li>
     *     <li>
     *         Custom {@link HttpEntity} body
     *     </li>
     * </ul>
     * <p>
     * When you set or add something to a new type of body, 
     * the existing body is overwritten, if there is one.
     */
    public T setFormBodyFields(Map<String, List<String>> params);

    /**
     * Sets a field on the form body, with its values.
     * Each form field may have multiple values.
     * <p>
     * Overwrites an existing field of the same
     * name, but keeps the other ones.
     * <p>
     * Only one type of body can be set amongs:
     * <ul>
     *     <li>
     *         Form body
     *     </li>
     *     <li>
     *         String body
     *     </li>
     *     <li>
     *         File to upload body
     *     </li>
     *     <li>
     *         Custom {@link HttpEntity} body
     *     </li>
     * </ul>
     * <p>
     * When you set or add something to a new type of body, 
     * the existing body is overwritten, if there is one.
     */
    public T setFormBodyField(String fieldName, List<String> values);

    /**
     * Adds a value to a Form field.
     * <p>
     * Keeps the existing values of the field.
     * <p>
     * Only one type of body can be set amongs:
     * <ul>
     *     <li>
     *         Form body
     *     </li>
     *     <li>
     *         String body
     *     </li>
     *     <li>
     *         File to upload body
     *     </li>
     *     <li>
     *         Custom {@link HttpEntity} body
     *     </li>
     * </ul>
     * <p>
     * When you set or add something to a new type of body, 
     * the existing body is overwritten, if there is one.
     */
    public T addFormBodyFieldValue(String fieldName, String value);

    /**
     * Sets a String body.
     * <p>
     * Only one type of body can be set amongs:
     * <ul>
     *     <li>
     *         Form body
     *     </li>
     *     <li>
     *         String body
     *     </li>
     *     <li>
     *         File to upload body
     *     </li>
     *     <li>
     *         Custom {@link HttpEntity} body
     *     </li>
     * </ul>
     * <p>
     * When you set or add something to a new type of body, 
     * the existing body is overwritten, if there is one.
     */
    public T setStringBody(String entity, String contentType);

    /**
     * Sets a <code>Json</code> body to be sent.
     * <p>
     * The specified object will be converted to a <code>Json</code>'s
     * String representation and sent using the <code>application/json</code>
     * Content-Type.
     * <p>
     * Only one type of body can be set amongs:
     * <ul>
     *     <li>
     *         Form body
     *     </li>
     *     <li>
     *         String body
     *     </li>
     *     <li>
     *         File to upload body
     *     </li>
     *     <li>
     *         Custom {@link HttpEntity} body
     *     </li>
     * </ul>
     * <p>
     * When you set or add something to a new type of body, 
     * the existing body is overwritten, if there is one.
     */
    public T setJsonStringBody(Object object);

    /**
     * Sets a <code>XML</code> body to be sent.
     * <p>
     * The specified object will be converted to <code>XML</code>
     * and sent using the <code>application/xml</code>
     * Content-Type.
     * <p>
     * Only one type of body can be set amongs:
     * <ul>
     *     <li>
     *         Form body
     *     </li>
     *     <li>
     *         String body
     *     </li>
     *     <li>
     *         File to upload body
     *     </li>
     *     <li>
     *         Custom {@link HttpEntity} body
     *     </li>
     * </ul>
     * <p>
     * When you set or add something to a new type of body, 
     * the existing body is overwritten, if there is one.
     */
    public T setXmlStringBody(Object object);

    /**
     * Adds a file to upload body. 
     * <p>
     * More than one file can be uploaded at one time. 
     * The specified file is added to the existing ones.
     * <p>
     * Only one type of body can be set amongs:
     * <ul>
     *     <li>
     *         Form body
     *     </li>
     *     <li>
     *         String body
     *     </li>
     *     <li>
     *         File to upload body
     *     </li>
     *     <li>
     *         Custom {@link HttpEntity} body
     *     </li>
     * </ul>
     * <p>
     * When you set or add something to a new type of body, 
     * the existing body is overwritten, if there is one.
     * 
     * @param path the path to the file to upload,
     * on the file system.
     * 
     * @param name the name to use for the uploaded file.
     */
    public T addFileToUploadBody(String path, String name);

    /**
     * Adds a file to upload body. 
     * <p>
     * More than one file can be uploaded at one time. 
     * The specified file is added to the existing ones.
     * <p>
     * Only one type of body can be set amongs:
     * <p>
     * Only one type of body can be set amongs:
     * <ul>
     *     <li>
     *         Form body
     *     </li>
     *     <li>
     *         String body
     *     </li>
     *     <li>
     *         File to upload body
     *     </li>
     *     <li>
     *         Custom {@link HttpEntity} body
     *     </li>
     * </ul>
     * <p>
     * When you set or add something to a new type of body, 
     * the existing body is overwritten, if there is one.
     * 
     * @param path the path to the file to upload
     * 
     * @param isClasspathPath if <code>true</code>, the path to the file to upload is
     * on the classpath, otherwise, it's on the file system.
     * 
     * @param name the name to use for the uploaded file.
     * 
     */
    public T addFileToUploadBody(String path, boolean isClasspathPath, String name);

}
