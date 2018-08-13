package org.spincast.core.utils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.spincast.core.exceptions.CantCompareException;
import org.spincast.core.exceptions.CantConvertException;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.json.ToJsonArrayConvertible;
import org.spincast.core.json.ToJsonObjectConvertible;
import org.spincast.shaded.org.apache.commons.codec.binary.Base64;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;

public class ObjectConverterDefault implements ObjectConverter {

    private final JsonManager jsonManager;

    @Inject
    public ObjectConverterDefault(JsonManager jsonManager) {
        this.jsonManager = jsonManager;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    @Override
    public boolean isCanBeConvertedTo(Class<?> referenceType, Object elementToValidate) {

        if (referenceType == null || elementToValidate == null) {
            return true;
        }

        //==========================================
        // Those are the types we currently manage.
        //==========================================
        if (String.class.equals(referenceType)) {
            return isCanBeConvertedToString(elementToValidate);
        } else if (BigDecimal.class.equals(referenceType)) {
            return isCanBeConvertedToBigDecimal(elementToValidate);
        } else if (Boolean.class.equals(referenceType)) {
            return isCanBeConvertedToBoolean(elementToValidate);
        } else if (byte[].class.equals(referenceType) || Byte[].class.equals(referenceType)) {
            return isCanBeConvertedToByteArray(elementToValidate);
        } else if (Date.class.equals(referenceType)) {
            return isCanBeConvertedToDateFromJsonDateFormat(elementToValidate);
        } else if (Double.class.equals(referenceType)) {
            return isCanBeConvertedToDouble(elementToValidate);
        } else if (Float.class.equals(referenceType)) {
            return isCanBeConvertedToFloat(elementToValidate);
        } else if (Integer.class.equals(referenceType)) {
            return isCanBeConvertedToInteger(elementToValidate);
        } else if (JsonArray.class.equals(referenceType)) {
            return isCanBeConvertedToJsonArray(elementToValidate);
        } else if (JsonObject.class.equals(referenceType)) {
            return isCanBeConvertedToJsonObject(elementToValidate);
        } else if (Long.class.equals(referenceType)) {
            return isCanBeConvertedToLong(elementToValidate);
        }

        return false;
    }

    @Override
    public boolean isCanBeConvertedToString(Object object) {
        try {
            convertToString(object);
            return true;
        } catch (CantConvertException ex) {
            return false;
        }
    }

    @Override
    public boolean isCanBeConvertedToInteger(Object object) {
        try {
            convertToInteger(object);
            return true;
        } catch (CantConvertException ex) {
            return false;
        }
    }

    @Override
    public boolean isCanBeConvertedToLong(Object object) {
        try {
            convertToLong(object);
            return true;
        } catch (CantConvertException ex) {
            return false;
        }
    }

    @Override
    public boolean isCanBeConvertedToFloat(Object object) {
        try {
            convertToFloat(object);
            return true;
        } catch (CantConvertException ex) {
            return false;
        }
    }

    @Override
    public boolean isCanBeConvertedToDouble(Object object) {
        try {
            convertToDouble(object);
            return true;
        } catch (CantConvertException ex) {
            return false;
        }
    }

    @Override
    public boolean isCanBeConvertedToBoolean(Object object) {
        try {
            convertToBoolean(object);
            return true;
        } catch (CantConvertException ex) {
            return false;
        }
    }

    @Override
    public boolean isCanBeConvertedToBigDecimal(Object object) {
        try {
            convertToBigDecimal(object);
            return true;
        } catch (CantConvertException ex) {
            return false;
        }
    }

    @Override
    public boolean isCanBeConvertedToByteArray(Object object) {

        if (object == null) {
            return true;
        }

        if (object instanceof byte[]) {
            return true;
        }

        if (!(object instanceof String)) {
            return false;
        }

        return Base64.isBase64((String)object);
    }

    @Override
    public boolean isCanBeConvertedToDateFromJsonDateFormat(Object object) {
        try {
            convertToDateFromJsonDateFormat(object);
            return true;
        } catch (CantConvertException ex) {
            return false;
        }
    }

    @Override
    public boolean isCanBeConvertedToJsonObject(Object object) {
        try {
            convertToJsonObject(object);
            return true;
        } catch (CantConvertException ex) {
            return false;
        }
    }

    @Override
    public boolean isCanBeConvertedToJsonArray(Object object) {
        try {
            convertToJsonArray(object);
            return true;
        } catch (CantConvertException ex) {
            return false;
        }
    }

    @Override
    public boolean isBase64StringOrNull(Object object) {

        if (object == null) {
            return true;
        }

        if (!(object instanceof String)) {
            return false;
        }

        try {
            convertBase64StringToByteArray(object);
            return true;
        } catch (CantConvertException ex) {
            return false;
        }
    }

    @Override
    public JsonObject convertToJsonObject(Object object) {

        if (object == null) {
            return null;
        }

        if (object instanceof JsonObject) {
            return (JsonObject)object;
        }

        if (object instanceof ToJsonObjectConvertible) {
            return ((ToJsonObjectConvertible)object).convertToJsonObject();
        }

        try {
            JsonObject jsonObject = getJsonManager().fromString(object.toString());
            return jsonObject;
        } catch (Exception ex) {
            throw new CantConvertException(object.getClass().getSimpleName(), JsonObject.class.getSimpleName(), object);
        }
    }

    @Override
    public JsonArray convertToJsonArray(Object object) {

        if (object == null) {
            return null;
        }

        if (object instanceof JsonArray) {
            return (JsonArray)object;
        }

        if (object instanceof ToJsonArrayConvertible) {
            return ((ToJsonArrayConvertible)object).convertToJsonArray();
        }

        try {
            JsonArray jsonArray = getJsonManager().fromStringArray(object.toString());
            return jsonArray;
        } catch (Exception ex) {
            throw new CantConvertException(object.getClass().getSimpleName(), JsonArray.class.getSimpleName(), object);
        }
    }

    @Override
    public String convertToString(Object object) {

        if (object == null) {
            return null;
        }

        if (object instanceof String) {
            return (String)object;
        }

        return String.valueOf(object);
    }

    @Override
    public Integer convertToInteger(Object object) throws CantConvertException {

        if (object == null) {
            return null;
        }

        if (object instanceof Integer) {
            return (Integer)object;
        }

        try {

            //==========================================
            // We accept zero only decimals (ex: "123.0")
            //==========================================
            String asString = String.valueOf(object);
            int pos = asString.lastIndexOf(".");
            if (pos > -1) {
                String decimals = asString.substring(pos + 1);
                if (StringUtils.containsOnly(decimals, "0")) {
                    asString = asString.substring(0, pos);
                } else {
                    throw new NumberFormatException();
                }
            }

            return Integer.valueOf(asString);
        } catch (NumberFormatException ex) {
            throw new CantConvertException(object.getClass().getSimpleName(), Integer.class.getSimpleName(), object);
        }
    }

    @Override
    public Long convertToLong(Object object) throws CantConvertException {

        if (object == null) {
            return null;
        }

        if (object instanceof Long) {
            return (Long)object;
        }

        try {

            //==========================================
            // We accept zero only decimals (ex: "123.0")
            //==========================================
            String asString = String.valueOf(object);
            int pos = asString.lastIndexOf(".");
            if (pos > -1) {
                String decimals = asString.substring(pos + 1);
                if (StringUtils.containsOnly(decimals, "0")) {
                    asString = asString.substring(0, pos);
                } else {
                    throw new NumberFormatException();
                }
            }

            return Long.valueOf(asString);
        } catch (NumberFormatException ex) {
            throw new CantConvertException(object.getClass().getSimpleName(), Long.class.getSimpleName(), object);
        }
    }

    @Override
    public Float convertToFloat(Object object) throws CantConvertException {

        if (object == null) {
            return null;
        }

        if (object instanceof Float) {
            return (Float)object;
        }

        try {
            return Float.valueOf(String.valueOf(object));
        } catch (NumberFormatException ex) {
            throw new CantConvertException(object.getClass().getSimpleName(), Float.class.getSimpleName(), object);
        }
    }

    @Override
    public Double convertToDouble(Object object) throws CantConvertException {

        if (object == null) {
            return null;
        }

        if (object instanceof Double) {
            return (Double)object;
        }

        try {
            return Double.valueOf(String.valueOf(object));
        } catch (NumberFormatException ex) {
            throw new CantConvertException(object.getClass().getSimpleName(), Double.class.getSimpleName(), object);
        }
    }

    @Override
    public Boolean convertToBoolean(Object object) throws CantConvertException {

        if (object == null) {
            return null;
        }

        if (object instanceof Boolean) {
            return (Boolean)object;
        }

        String valStr = String.valueOf(object);
        if ("true".equalsIgnoreCase(valStr)) {
            return true;
        } else if ("false".equalsIgnoreCase(valStr)) {
            return false;
        }
        throw new CantConvertException(object.getClass().getSimpleName(), Boolean.class.getSimpleName(), object);
    }

    @Override
    public BigDecimal convertToBigDecimal(Object object) throws CantConvertException {

        if (object == null) {
            return null;
        }

        if (object instanceof BigDecimal) {
            return (BigDecimal)object;
        }

        try {
            return new BigDecimal(String.valueOf(object));
        } catch (NumberFormatException ex) {
            throw new CantConvertException(object.getClass().getSimpleName(), BigDecimal.class.getSimpleName(), object);
        }
    }

    @Override
    public byte[] convertBase64StringToByteArray(Object object) throws CantConvertException {

        if (object == null) {
            return null;
        }

        if (object instanceof byte[]) {
            return (byte[])object;
        }

        if (!(object instanceof String)) {
            throw new CantConvertException(object.getClass().getSimpleName(), byte[].class.getSimpleName(), object);
        }

        //==========================================
        // We validate the String because "Base64.decodeBase64()"
        // accepts invalid Strings by default... It
        // ignore invalid characters!
        //==========================================
        if (!isCanBeConvertedToByteArray(object)) {
            throw new CantConvertException(object.getClass().getSimpleName(), byte[].class.getSimpleName(), object);
        }

        try {
            return Base64.decodeBase64((String)object);
        } catch (Exception ex) {
            throw new CantConvertException(object.getClass().getSimpleName(), byte[].class.getSimpleName(), object);
        }
    }

    @Override
    public String convertByteArrayToBase64String(byte[] byteArray) {

        if (byteArray == null) {
            return null;
        }

        return Base64.encodeBase64String(byteArray);
    }

    @Override
    public Date convertToDateFromJsonDateFormat(Object object) throws CantConvertException {

        if (object == null) {
            return null;
        }

        if (object instanceof Date) {
            return (Date)object;
        }

        if (object instanceof Instant) {
            return Date.from((Instant)object);
        }

        try {
            return getJsonManager().parseDateFromJson(String.valueOf(object));
        } catch (Exception ex) {
            throw new CantConvertException(object.getClass().getSimpleName(), Date.class.getSimpleName(), object);
        }
    }

    @Override
    public Instant convertToInstantFromJsonDateFormat(Object object) throws CantConvertException {
        if (object == null) {
            return null;
        }

        if (object instanceof Instant) {
            return (Instant)object;
        }

        if (object instanceof Date) {
            return ((Date)object).toInstant();
        }

        try {
            String val = String.valueOf(object);
            return Instant.parse(val);
        } catch (Exception ex) {
            throw new CantConvertException(object.getClass().getSimpleName(), Instant.class.getSimpleName(), object);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T convertTo(Object object, Class<T> targetType) throws CantConvertException {

        if (object == null) {
            return null;
        }

        if (targetType == null) {
            throw new CantConvertException(object.getClass().getName(), "null", object);
        }

        //==========================================
        // Already of a correct type.
        //==========================================
        if (targetType.isAssignableFrom(object.getClass())) {
            return (T)object;
        }

        if (String.class.isAssignableFrom(targetType)) {
            return (T)convertToString(object);
        } else if (Integer.class.isAssignableFrom(targetType)) {
            return (T)convertToInteger(object);
        } else if (Long.class.isAssignableFrom(targetType)) {
            return (T)convertToLong(object);
        } else if (Float.class.isAssignableFrom(targetType)) {
            return (T)convertToFloat(object);
        } else if (Double.class.isAssignableFrom(targetType)) {
            return (T)convertToDouble(object);
        } else if (Boolean.class.isAssignableFrom(targetType)) {
            return (T)convertToBoolean(object);
        } else if (BigDecimal.class.isAssignableFrom(targetType)) {
            return (T)convertToBigDecimal(object);
        } else if (byte[].class.isAssignableFrom(targetType)) {
            return (T)convertBase64StringToByteArray(object);
        } else if (Date.class.isAssignableFrom(targetType)) {
            return (T)convertToDateFromJsonDateFormat(object);
        } else if (JsonObject.class.isAssignableFrom(targetType)) {
            return (T)convertToJsonObject(object);
        } else if (JsonArray.class.isAssignableFrom(targetType)) {
            return (T)convertToJsonArray(object);
        }

        throw new CantConvertException(object.getClass().getName(), targetType.getName(), object);
    }

    @Override
    public String convertToJsonDateFormat(Date date) {
        return getJsonManager().convertToJsonDate(date);
    }

    @Override
    public int compareTo(Object valueToCompare, Object compareTo) throws CantCompareException {

        if (valueToCompare == null) {
            if (compareTo == null) {
                return 0;
            } else {
                return -1;
            }
        } else if (compareTo == null) {
            return 1;
        }

        //==========================================
        // We first try to convert "valueToCompare" to
        // the same Comparable<?> type than "compareTo".
        //==========================================
        if (compareTo instanceof Comparable<?>) {

            try {
                Object valueToCompareConverted = convertTo(valueToCompare, compareTo.getClass());

                @SuppressWarnings({"unchecked", "rawtypes"})
                int result = ((Comparable)valueToCompareConverted).compareTo(compareTo);
                return result;
            } catch (CantConvertException ex) {
                // ok
            }
        }

        //==========================================
        // Are they numbers but it's "compareTo"
        // that has to be converted to the type of
        // "valueToCompare"?
        //==========================================
        if (valueToCompare instanceof Number) {
            Object compareToConverted = convertTo(compareTo, valueToCompare.getClass());

            @SuppressWarnings({"unchecked", "rawtypes"})
            int result = ((Comparable)valueToCompare).compareTo(compareToConverted);
            return result;
        }

        throw new CantCompareException(valueToCompare.getClass().getName(), compareTo.getClass().getName(), valueToCompare);
    }

    @Override
    public boolean isEquivalent(Object valueToCompare, Object compareTo) {

        if (valueToCompare == null) {
            if (compareTo == null) {
                return true;
            } else {
                return false;
            }
        } else if (compareTo == null) {
            return false;
        }

        if (valueToCompare instanceof JsonObject && compareTo instanceof JsonObject) {
            return ((JsonObject)compareTo).isEquivalentTo((JsonObject)valueToCompare);

        } else if (valueToCompare instanceof JsonArray && compareTo instanceof JsonArray) {
            return ((JsonArray)compareTo).isEquivalentTo((JsonArray)valueToCompare);

        } else if (compareTo instanceof byte[] || compareTo instanceof Byte[]) {

            if (!(valueToCompare instanceof byte[] || valueToCompare instanceof Byte[])) {

                try {
                    valueToCompare = convertTo(valueToCompare, compareTo.getClass());
                } catch (CantConvertException ex) {
                    return false;
                }
            }
            return Arrays.equals((byte[])valueToCompare, (byte[])compareTo);

        } else {

            boolean equals = compareTo.equals(valueToCompare);

            //==========================================
            // Maybe a converted version of the
            // "valueToCompare" is equals?
            // For example, the String "3" compared to the
            // Integer 3.
            //==========================================
            if (!equals) {

                try {
                    Object valueToCompareConverted = convertTo(valueToCompare, compareTo.getClass());
                    equals = valueToCompareConverted.equals(compareTo);
                } catch (CantConvertException ex) {
                    // ok
                }

                //==========================================
                // Are they numbers but it's "compareTo"
                // that has to be converted to the type of
                // "valueToCompare"?
                //==========================================
                if (!equals && valueToCompare instanceof Number) {

                    try {
                        Object compareToConverted = convertTo(compareTo, valueToCompare.getClass());
                        equals = valueToCompare.equals(compareToConverted);
                    } catch (CantConvertException ex) {
                        // ok
                    }
                }
            }

            return equals;
        }
    }

    @Override
    public boolean isAtLeastOneEquivalentElementInCommon(Collection<?> col1, Collection<?> col2) {

        if (col1 == null || col2 == null || col1.size() == 0 || col2.size() == 0) {
            return false;
        }

        for (Object obj1 : col1) {
            for (Object obj2 : col2) {
                if (isEquivalent(obj1, obj2)) {
                    return true;
                }
            }
        }
        return false;
    }

}
