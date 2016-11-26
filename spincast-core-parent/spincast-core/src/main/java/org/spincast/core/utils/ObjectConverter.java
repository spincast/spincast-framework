package org.spincast.core.utils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import org.spincast.core.exceptions.CantCompareException;
import org.spincast.core.exceptions.CantConvertException;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonObject;

/**
 * Methods to convert an object from a type to another, when
 * possible. Some methods also allow to compare two
 * elements.
 */
public interface ObjectConverter {

    /**
     * Can the element be converted to the specified 
     * <code>referenceType</code>?
     */
    public boolean isCanBeConvertedTo(Class<?> referenceType, Object element);

    /**
     * Can the element be converted to a 
     * <code>String</code>?
     */
    public boolean isCanBeConvertedToString(Object element);

    /**
     * Can the element be converted to a 
     * <code>Integer</code>?
     */
    public boolean isCanBeConvertedToInteger(Object element);

    /**
     * Can the element be converted to a 
     * <code>Long</code>?
     */
    public boolean isCanBeConvertedToLong(Object element);

    /**
     * Can the element be converted to a 
     * <code>Float</code>?
     */
    public boolean isCanBeConvertedToFloat(Object element);

    /**
     * Can the element be converted to a 
     * <code>Double</code>?
     */
    public boolean isCanBeConvertedToDouble(Object element);

    /**
     * Can the element be converted to a 
     * <code>Boolean</code>?
     */
    public boolean isCanBeConvertedToBoolean(Object element);

    /**
     * Can the element be converted to a 
     * <code>BigDecimal</code>?
     */
    public boolean isCanBeConvertedToBigDecimal(Object element);

    /**
     * Can the element be converted to a 
     * <code>byte[]</code>?
     */
    public boolean isCanBeConvertedToByteArray(Object element);

    /**
     * Can the element be converted to a 
     * <code>Date</code>? The source element must follow
     * the <em>Json</em> date format.
     */
    public boolean isCanBeConvertedToDateFromJsonDateFormat(Object element);

    /**
     * Can the element be converted to a 
     * <code>JsonObject</code>?
     */
    public boolean isCanBeConvertedToJsonObject(Object element);

    /**
     * Can the element be converted to a 
     * <code>JsonArray</code>?
     */
    public boolean isCanBeConvertedToJsonArray(Object element);

    /**
     * Returns <code>true</code> if the element is a valid base 64 String, 
     * or is <code>null</code>.
     */
    public boolean isBase64StringOrNull(Object element);

    /**
     * Converts the element to a <code>JsonObject</code>.
     * 
     * @throws CantConvertException if the element can't be converted to
     * the required type.
     */
    public JsonObject convertToJsonObject(Object element) throws CantConvertException;

    /**
     * Converts the element to a <code>JsonArray</code>.
     * 
     * @throws CantConvertException if the element can't be converted to
     * the required type.
     */
    public JsonArray convertToJsonArray(Object element) throws CantConvertException;

    /**
     * Converts the element to a <code>JsonObject</code>.
     */
    public String convertToString(Object element);

    /**
     * Converts the element to a <code>Integer</code>.
     * <p>
     * The <code>toString()</code> method will be called on the object
     * before trying to convert it to an Integer. If the resulting
     * String contains <em>zeros only</em> decimals, those will be 
     * removed and the conversion will work.
     * <p>
     * For example : "123.0" will work.
     * 
     * @throws CantConvertException if the element can't be converted to
     * the required type.
     */
    public Integer convertToInteger(Object element) throws CantConvertException;

    /**
     * Converts the element to a <code>Long</code>.
     * <p>
     * The <code>toString()</code> method will be called on the object
     * before trying to convert it to a Long. If the resulting
     * String contains <em>zeros only</em> decimals, those will be 
     * removed and the conversion will work.
     * <p>
     * For example : "123.0" will work.
     * 
     * @throws CantConvertException if the element can't be converted to
     * the required type.
     */
    public Long convertToLong(Object element) throws CantConvertException;

    /**
     * Converts the element to a <code>Float</code>.
     * 
     * @throws CantConvertException if the element can't be converted to
     * the required type.
     */
    public Float convertToFloat(Object element) throws CantConvertException;

    /**
     * Converts the element to a <code>Double</code>.
     * 
     * @throws CantConvertException if the element can't be converted to
     * the required type.
     */
    public Double convertToDouble(Object element) throws CantConvertException;

    /**
     * Converts the element to a <code>JsonArray</code>.
     * <p>
     * Il can be converted to a <code>Boolean</code> if its already a Boolean,
     * if it's <code>null</code> or if it's the String "true" or the String
     * "false".
     * 
     * @throws CantConvertException if the element can't be converted to
     * the required type.
     */
    public Boolean convertToBoolean(Object element) throws CantConvertException;

    /**
     * Converts the element to a <code>BigDecimal</code>.
     * 
     * @throws CantConvertException if the element can't be converted to
     * the required type.
     */
    public BigDecimal convertToBigDecimal(Object element) throws CantConvertException;

    /**
     * Converts the element to a <code>BigDecimal</code>.
     * <p>
     * Can be converted to a <code>byte[]</code> if its already a <code>byte[]</code>,
     * if it's <code>null</code> or if it's a valid base 64 <code>String</code>.
     * 
     * @throws CantConvertException if the element can't be converted to
     * the required type.
     */
    public byte[] convertBase64StringToByteArray(Object element) throws CantConvertException;

    /**
     * Converts the element to a <code>Base64 String</code>.
     */
    public String convertByteArrayToBase64String(byte[] byteArray);

    /**
     * Converts the element to a <code>Date</code>.
     * <p>
     * The source element must follow the <em>Json</em> date format.
     * 
     * @throws CantConvertException if the element can't be converted to
     * the required type.
     */
    public Date convertToDateFromJsonDateFormat(Object element) throws CantConvertException;

    /**
     * Tries to convert the element to the specified <code>targetType</code>.
     * <p>
     * By default, only the types native to JsonObject are supported as
     * the target types.
     * 
     * @throws CantConvertException if the element can't be converted to
     * the required type.
     */
    public <T> T convertTo(Object element, Class<T> targetType) throws CantConvertException;

    /**
     * Converts the date to a String representaiton compatible with
     * the <em>Json</em> specification.
     */
    public String convertToJsonDateFormat(Date date);

    /**
     * Compare two elements.
     * <p>
     * If required, try to convert the <code>elementToCompare</code> so it
     * can be compared with <code>compareTo</code>. It is also possible
     * that we have to convert <code>compareTo</code>, for the comparison
     * to be possible. For example, if <code>compareTo</code> is Long(10) and
     * <code>elementToCompare</code> is BigDecimal(12.34), we have to convert
     * both elements to BigDecimal so they can be compared.
     * <p>
     * For a comparison to work, the two elements must be (after conversion
     * or not) of the same type and this type must implement 
     * {@link Comparable}.
     * 
     * @throws CantCompareException if the elements can't be compared together.
     */
    public int compareTo(Object elementToCompare, Object compareTo) throws CantCompareException;

    /**
     * Are the two elements equivalent?
     * <p>
     * If required, try to convert the <code>elementToCompare</code> so it
     * can be compared with <code>compareTo</code>. It is also possible
     * that we have to convert <code>compareTo</code>, for the comparison
     * to be possible. For example, if <code>compareTo</code> is Long(10) and
     * <code>elementToCompare</code> is BigDecimal(12.34), we have to convert
     * both elements to BigDecimal so they can be compared.
     * <p>
     * For the two elements to be equivalent, they must be (after conversion
     * or not) of the same type and then the {@link Object#equals(Object)}
     * method must return <code>true</code>.
     */
    public boolean isEquivalent(Object elementToCompare, Object compareTo);

    /**
     * Do the two Collections have at least one <em>equivalent</em> element
     * in common?
     * 
     * @see {@link #isEquivalent(Object, Object)}
     */
    public boolean isAtLeastOneEquivalentElementInCommon(Collection<?> col1, Collection<?> col2);

}
