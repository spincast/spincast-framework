package org.spincast.core.json;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.shaded.org.apache.commons.codec.binary.Base64;

import com.google.common.base.Function;
import com.google.inject.assistedinject.AssistedInject;

/**
 * <code>IJsonObject</code> implementation.
 */
public class JsonObject implements IJsonObject {

    protected final Logger logger = LoggerFactory.getLogger(JsonObject.class);

    private final IJsonManager jsonManager;
    private final Map<String, Object> map;

    @AssistedInject
    public JsonObject(IJsonManager jsonManager) {
        this.jsonManager = jsonManager;
        this.map = new HashMap<String, Object>();
    }

    protected IJsonManager getJsonManager() {
        return this.jsonManager;
    }

    @Override
    public Map<String, Object> getUnderlyingMap() {
        return this.map;
    }

    @Override
    public void put(String key, Object value) {
        if(key == null) {
            throw new RuntimeException("key can't be NULL");
        }

        getUnderlyingMap().put(key, value);
    }

    @Override
    public void remove(String key) {
        getUnderlyingMap().remove(key);
    }

    @Override
    public void removeAll() {
        getUnderlyingMap().clear();
    }

    @Override
    public Object get(String key) throws KeyNotFoundException {
        return getValue(key, false, null);
    }

    @Override
    public Object get(String key, Object defaultValue) {
        return getValue(key, true, defaultValue);
    }

    protected Object getValue(String key, boolean hasDefaultValue, Object defaultValue) {

        return getValueDyn(key, hasDefaultValue, defaultValue, Object.class, new Function<Object, Object>() {

            @Override
            public Object apply(Object val) {
                return val;
            }
        });
    }

    @Override
    public IJsonObject getJsonObject(String key) throws KeyNotFoundException {
        return getJSONObjectValue(key, false, null);
    }

    @Override
    public IJsonObject getJsonObject(String key, IJsonObject defaultValue) {
        return getJSONObjectValue(key, true, defaultValue);
    }

    protected IJsonObject getJSONObjectValue(String key, boolean hasDefaultValue, IJsonObject defaultValue) {

        return getValueDyn(key, hasDefaultValue, defaultValue, IJsonObject.class, new Function<Object, IJsonObject>() {

            @Override
            public IJsonObject apply(Object val) {
                if(val instanceof IJsonObject) {
                    return (IJsonObject)val;
                }
                throw new RuntimeException("Can't convert'" + val + "' to a " + IJsonObject.class.getSimpleName() +
                                           ".");
            }
        });
    }

    @Override
    public IJsonArray getJsonArray(String key) throws KeyNotFoundException {
        return getJsonArrayValue(key, false, null);
    }

    @Override
    public IJsonArray getJsonArray(String key, IJsonArray defaultValue) {
        return getJsonArrayValue(key, true, defaultValue);
    }

    protected IJsonArray getJsonArrayValue(String key, boolean hasDefaultValue, IJsonArray defaultValue) {

        return getValueDyn(key, hasDefaultValue, defaultValue, IJsonArray.class, new Function<Object, IJsonArray>() {

            @Override
            public IJsonArray apply(Object val) {
                if(val instanceof IJsonArray) {
                    return (IJsonArray)val;
                }
                throw new RuntimeException("Can't convert'" + val + "' to a " + IJsonArray.class.getSimpleName() + ".");
            }
        });
    }

    @Override
    public String getString(String key) throws KeyNotFoundException {
        return getStringValue(key, false, null);
    }

    @Override
    public String getString(String key, String defaultValue) {
        return getStringValue(key, true, defaultValue);
    }

    protected String getStringValue(String key, boolean hasDefaultValue, String defaultValue) {
        return getValueDyn(key, hasDefaultValue, defaultValue, String.class, new Function<Object, String>() {

            @Override
            public String apply(Object val) {
                return String.valueOf(val);
            }
        });
    }

    @Override
    public Integer getInteger(String key) throws KeyNotFoundException {
        return getIntegerValue(key, false, null);
    }

    @Override
    public Integer getInteger(String key, Integer defaultValue) {
        return getIntegerValue(key, true, defaultValue);
    }

    protected Integer getIntegerValue(String key, boolean hasDefaultValue, Integer defaultValue) {
        return getValueDyn(key, hasDefaultValue, defaultValue, Integer.class, new Function<Object, Integer>() {

            @Override
            public Integer apply(Object val) {
                if(val instanceof Integer) {
                    return (Integer)val;
                }
                return Integer.valueOf(String.valueOf(val));
            }
        });
    }

    @Override
    public Long getLong(String key) throws KeyNotFoundException {
        return getLongValue(key, false, null);
    }

    @Override
    public Long getLong(String key, Long defaultValue) {
        return getLongValue(key, true, defaultValue);
    }

    protected Long getLongValue(String key, boolean hasDefaultValue, Long defaultValue) {
        return getValueDyn(key, hasDefaultValue, defaultValue, Long.class, new Function<Object, Long>() {

            @Override
            public Long apply(Object val) {
                if(val instanceof Long) {
                    return (Long)val;
                }
                return Long.valueOf(String.valueOf(val));
            }
        });
    }

    @Override
    public Float getFloat(String key) throws KeyNotFoundException {
        return getFloatValue(key, false, null);
    }

    @Override
    public Float getFloat(String key, Float defaultValue) {
        return getFloatValue(key, true, defaultValue);
    }

    protected Float getFloatValue(String key, boolean hasDefaultValue, Float defaultValue) {
        return getValueDyn(key, hasDefaultValue, defaultValue, Float.class, new Function<Object, Float>() {

            @Override
            public Float apply(Object val) {
                if(val instanceof Float) {
                    return (Float)val;
                }
                return Float.valueOf(String.valueOf(val));
            }
        });
    }

    @Override
    public Double getDouble(String key) throws KeyNotFoundException {
        return getDoubleValue(key, false, null);
    }

    @Override
    public Double getDouble(String key, Double defaultValue) {
        return getDoubleValue(key, true, defaultValue);
    }

    protected Double getDoubleValue(String key, boolean hasDefaultValue, Double defaultValue) {
        return getValueDyn(key, hasDefaultValue, defaultValue, Double.class, new Function<Object, Double>() {

            @Override
            public Double apply(Object val) {
                if(val instanceof Double) {
                    return (Double)val;
                }
                return Double.valueOf(String.valueOf(val));
            }
        });
    }

    @Override
    public Boolean getBoolean(String key) throws KeyNotFoundException {
        return getBooleanValue(key, false, null);
    }

    @Override
    public Boolean getBoolean(String key, Boolean defaultValue) {
        return getBooleanValue(key, true, defaultValue);
    }

    protected Boolean getBooleanValue(String key, boolean hasDefaultValue, Boolean defaultValue) {
        return getValueDyn(key, hasDefaultValue, defaultValue, Boolean.class, new Function<Object, Boolean>() {

            @Override
            public Boolean apply(Object val) {
                if(val instanceof Boolean) {
                    return (Boolean)val;
                }
                String valStr = String.valueOf(val);
                if("true".equalsIgnoreCase(valStr)) {
                    return true;
                } else if("false".equalsIgnoreCase(valStr)) {
                    return false;
                }
                throw new RuntimeException("Can't convert'" + valStr + "' to a boolean value.");
            }
        });
    }

    @Override
    public BigDecimal getBigDecimal(String key) throws KeyNotFoundException {
        return getBigDecimalValue(key, false, null);
    }

    @Override
    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        return getBigDecimalValue(key, true, defaultValue);
    }

    protected BigDecimal getBigDecimalValue(String key, boolean hasDefaultValue, BigDecimal defaultValue) {
        return getValueDyn(key, hasDefaultValue, defaultValue, BigDecimal.class, new Function<Object, BigDecimal>() {

            @Override
            public BigDecimal apply(Object val) {
                if(val instanceof BigDecimal) {
                    return (BigDecimal)val;
                }
                return new BigDecimal(String.valueOf(val));
            }
        });
    }

    @Override
    public byte[] getBytesFromBase64String(String key) throws KeyNotFoundException {
        return getBytesFromBase64StringValue(key, false, null);
    }

    @Override
    public byte[] getBytesFromBase64String(String key, byte[] defaultValue) {
        return getBytesFromBase64StringValue(key, true, defaultValue);
    }

    protected byte[] getBytesFromBase64StringValue(final String key, boolean hasDefaultValue, byte[] defaultValue) {
        return getValueDyn(key, hasDefaultValue, defaultValue, byte[].class, new Function<Object, byte[]>() {

            @Override
            public byte[] apply(Object val) {

                String str = getString(key);
                return Base64.decodeBase64(str);
            }
        });
    }

    @Override
    public Date getDate(String key) throws KeyNotFoundException {
        return getDateValue(key, false, null);
    }

    @Override
    public Date getDate(String key, Date defaultValue) {
        return getDateValue(key, true, defaultValue);
    }

    protected Date getDateValue(final String key, boolean hasDefaultValue, Date defaultValue) {
        return getValueDyn(key, hasDefaultValue, defaultValue, Date.class, new Function<Object, Date>() {

            @Override
            public Date apply(Object val) {

                try {
                    String str = getString(key);
                    return parseDate(str);
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });
    }

    protected Date parseDate(String str) {
        return getJsonManager().parseDateFromJson(str);
    }

    protected <T> T getValueDyn(String key,
                                boolean hasDefaultValue,
                                T defaultValue,
                                Class<T> clazz,
                                Function<Object, T> converter) {

        if(!getUnderlyingMap().containsKey(key)) {
            if(hasDefaultValue) {
                return defaultValue;
            } else {
                throw new KeyNotFoundException(key);
            }
        }

        Object val = getUnderlyingMap().get(key);
        if(val == null) {
            return null;
        }

        try {
            return converter.apply(val);
        } catch(Exception ex) {

            if(hasDefaultValue) {
                this.logger.warn("The key '" + key + "' was found but can't be converted to " + clazz.getSimpleName() +
                                 " : " + val.getClass().getSimpleName() + ". Using the default value instead...");
                return defaultValue;
            }

            throw new RuntimeException("The object found can't be converted to " + clazz.getSimpleName() + " and no " +
                                       "default value provided : " + val);
        }
    }

    @Override
    public Iterator<Entry<String, Object>> iterator() {
        return getUnderlyingMap().entrySet().iterator();
    }

    @Override
    public String toJsonString() {
        return toJsonString(false);
    }

    @Override
    public String toJsonString(boolean pretty) {
        return getJsonManager().toJsonString(this, pretty);
    }

    @Override
    public String toString() {
        return toJsonString();
    }

}
