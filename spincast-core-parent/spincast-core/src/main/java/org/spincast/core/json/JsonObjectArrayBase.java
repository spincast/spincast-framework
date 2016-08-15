package org.spincast.core.json;

import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.json.exceptions.CantConvertException;
import org.spincast.core.utils.ISpincastUtils;
import org.spincast.shaded.org.apache.commons.codec.binary.Base64;

/**
 * Base class for both JsonObject and JsonArray.
 */
public abstract class JsonObjectArrayBase {

    protected final Logger logger = LoggerFactory.getLogger(JsonObjectArrayBase.class);

    private final IJsonManager jsonManager;
    private final ISpincastUtils spincastUtils;

    /**
     * Constructor
     */
    public JsonObjectArrayBase(IJsonManager jsonManager,
                               ISpincastUtils spincastUtils) {
        this.jsonManager = jsonManager;
        this.spincastUtils = spincastUtils;
    }

    protected IJsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected ISpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    public IJsonObject getJsonObject(String key) throws CantConvertException {
        return getJSONObjectValue(key, false, null);
    }

    public IJsonObject getJsonObjectOrEmpty(String key) {
        return getJsonObject(key, getJsonManager().create());
    }

    public IJsonObject getJsonObject(String key, IJsonObject defaultValue) {
        return getJSONObjectValue(key, true, defaultValue);
    }

    protected IJsonObject getJSONObjectValue(String key, boolean hasDefaultValue, IJsonObject defaultValue) {

        Object val = getElement(key, hasDefaultValue, defaultValue);
        if(val == null) {
            return null;
        }

        if(val instanceof IJsonObject) {
            return (IJsonObject)val;
        }
        throw new CantConvertException(val.getClass().getSimpleName(), IJsonObject.class.getSimpleName(), val);
    }

    public IJsonArray getJsonArray(String key) throws CantConvertException {
        return getJsonArrayValue(key, false, null);
    }

    public IJsonArray getJsonArrayOrEmpty(String key) {
        return getJsonArray(key, getJsonManager().createArray());
    }

    public IJsonArray getJsonArray(String key, IJsonArray defaultValue) {
        return getJsonArrayValue(key, true, defaultValue);
    }

    protected IJsonArray getJsonArrayValue(String key, boolean hasDefaultValue, IJsonArray defaultValue) {

        Object val = getElement(key, hasDefaultValue, defaultValue);
        if(val == null) {
            return null;
        }

        if(val instanceof IJsonArray) {
            return (IJsonArray)val;
        }
        throw new CantConvertException(val.getClass().getSimpleName(), IJsonArray.class.getSimpleName(), val);

    }

    public String getString(String key) throws CantConvertException {
        return getStringValue(key, false, null);
    }

    public String getString(String key, String defaultValue) {
        return getStringValue(key, true, defaultValue);
    }

    protected String getStringValue(String key, boolean hasDefaultValue, String defaultValue) {

        Object val = getElement(key, hasDefaultValue, defaultValue);
        if(val == null) {
            return null;
        }
        if(val instanceof String) {
            return (String)val;
        }

        return String.valueOf(val);

    }

    public Integer getInteger(String key) throws CantConvertException {
        return getIntegerValue(key, false, null);
    }

    public Integer getInteger(String key, Integer defaultValue) {
        return getIntegerValue(key, true, defaultValue);
    }

    protected Integer getIntegerValue(String key, boolean hasDefaultValue, Integer defaultValue) {

        Object val = getElement(key, hasDefaultValue, defaultValue);
        if(val == null) {
            return null;
        }

        if(val instanceof Integer) {
            return (Integer)val;
        }

        try {
            return Integer.valueOf(String.valueOf(val));
        } catch(NumberFormatException ex) {
            throw new CantConvertException(val.getClass().getSimpleName(), Integer.class.getSimpleName(), val);
        }
    }

    public Long getLong(String key) throws CantConvertException {
        return getLongValue(key, false, null);
    }

    public Long getLong(String key, Long defaultValue) {
        return getLongValue(key, true, defaultValue);
    }

    protected Long getLongValue(String key, boolean hasDefaultValue, Long defaultValue) {

        Object val = getElement(key, hasDefaultValue, defaultValue);
        if(val == null) {
            return null;
        }

        if(val instanceof Long) {
            return (Long)val;
        }

        try {
            return Long.valueOf(String.valueOf(val));
        } catch(NumberFormatException ex) {
            throw new CantConvertException(val.getClass().getSimpleName(), Long.class.getSimpleName(), val);
        }
    }

    public Float getFloat(String key) throws CantConvertException {
        return getFloatValue(key, false, null);
    }

    public Float getFloat(String key, Float defaultValue) {
        return getFloatValue(key, true, defaultValue);
    }

    protected Float getFloatValue(String key, boolean hasDefaultValue, Float defaultValue) {

        Object val = getElement(key, hasDefaultValue, defaultValue);
        if(val == null) {
            return null;
        }

        if(val instanceof Float) {
            return (Float)val;
        }

        try {
            return Float.valueOf(String.valueOf(val));
        } catch(NumberFormatException ex) {
            throw new CantConvertException(val.getClass().getSimpleName(), Float.class.getSimpleName(), val);
        }

    }

    public Double getDouble(String key) throws CantConvertException {
        return getDoubleValue(key, false, null);
    }

    public Double getDouble(String key, Double defaultValue) {
        return getDoubleValue(key, true, defaultValue);
    }

    protected Double getDoubleValue(String key, boolean hasDefaultValue, Double defaultValue) {

        Object val = getElement(key, hasDefaultValue, defaultValue);
        if(val == null) {
            return null;
        }

        if(val instanceof Double) {
            return (Double)val;
        }

        try {
            return Double.valueOf(String.valueOf(val));
        } catch(NumberFormatException ex) {
            throw new CantConvertException(val.getClass().getSimpleName(), Double.class.getSimpleName(), val);
        }
    }

    public Boolean getBoolean(String key) throws CantConvertException {
        return getBooleanValue(key, false, null);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        return getBooleanValue(key, true, defaultValue);
    }

    protected Boolean getBooleanValue(String key, boolean hasDefaultValue, Boolean defaultValue) {

        Object val = getElement(key, hasDefaultValue, defaultValue);
        if(val == null) {
            return null;
        }

        if(val instanceof Boolean) {
            return (Boolean)val;
        }

        String valStr = String.valueOf(val);
        if("true".equalsIgnoreCase(valStr)) {
            return true;
        } else if("false".equalsIgnoreCase(valStr)) {
            return false;
        }
        throw new CantConvertException(val.getClass().getSimpleName(), Boolean.class.getSimpleName(), val);
    }

    public BigDecimal getBigDecimal(String key) throws CantConvertException {
        return getBigDecimalValue(key, false, null);
    }

    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        return getBigDecimalValue(key, true, defaultValue);
    }

    protected BigDecimal getBigDecimalValue(String key, boolean hasDefaultValue, BigDecimal defaultValue) {

        Object val = getElement(key, hasDefaultValue, defaultValue);
        if(val == null) {
            return null;
        }

        if(val instanceof BigDecimal) {
            return (BigDecimal)val;
        }

        try {
            return new BigDecimal(String.valueOf(val));
        } catch(NumberFormatException ex) {
            throw new CantConvertException(val.getClass().getSimpleName(), BigDecimal.class.getSimpleName(), val);
        }
    }

    public byte[] getBytesFromBase64String(String key) throws CantConvertException {
        return getBytesFromBase64StringValue(key, false, null);
    }

    public byte[] getBytesFromBase64String(String key, byte[] defaultValue) {
        return getBytesFromBase64StringValue(key, true, defaultValue);
    }

    protected byte[] getBytesFromBase64StringValue(final String key, boolean hasDefaultValue, byte[] defaultValue) {

        Object val = getElement(key, hasDefaultValue, defaultValue);
        if(val == null) {
            return null;
        }

        if(val instanceof byte[]) {
            return (byte[])val;
        }

        try {
            return Base64.decodeBase64(String.valueOf(val));
        } catch(Exception ex) {
            throw new CantConvertException(val.getClass().getSimpleName(), byte[].class.getSimpleName(), val);
        }
    }

    public Date getDate(String key) throws CantConvertException {
        return getDateValue(key, false, null);
    }

    public Date getDate(String key, Date defaultValue) {
        return getDateValue(key, true, defaultValue);
    }

    protected Date getDateValue(final String key, boolean hasDefaultValue, Date defaultValue) {

        Object val = getElement(key, hasDefaultValue, defaultValue);
        if(val == null) {
            return null;
        }

        if(val instanceof Date) {
            return (Date)val;
        }

        try {
            String str = getString(key);
            return parseDate(str);
        } catch(Exception ex) {
            throw new CantConvertException(val.getClass().getSimpleName(), Date.class.getSimpleName(), val);
        }
    }

    protected Date parseDate(String str) {
        return getJsonManager().parseDateFromJson(str);
    }

    public String toJsonString() {
        return toJsonString(false);
    }

    public String toJsonString(boolean pretty) {
        return getJsonManager().toJsonString(this, pretty);
    }

    @Override
    public String toString() {
        return toJsonString();
    }

    /**
     * Gets the element at this key/position.
     */
    protected abstract Object getElement(String keyPosition, boolean hasDefaultValue, Object defaultValue);
}
