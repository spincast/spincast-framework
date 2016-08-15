package org.spincast.core.json;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.utils.ISpincastUtils;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * <code>IJsonObject</code> implementation.
 */
public class JsonObject extends JsonObjectArrayBase implements IJsonObject {

    protected final Logger logger = LoggerFactory.getLogger(JsonObject.class);

    protected static interface IFirstElementGetter<T> {

        public T get(IJsonArray array, boolean hasDefaultValue, T defaultValue);
    }

    private final Map<String, ?> initialMap;
    private final Map<String, Object> map;

    @AssistedInject
    public JsonObject(IJsonManager jsonManager,
                      ISpincastUtils spincastUtils) {
        this(null, jsonManager, spincastUtils);
    }

    @AssistedInject
    public JsonObject(@Assisted @Nullable Map<String, ?> initialMap,
                      IJsonManager jsonManager,
                      ISpincastUtils spincastUtils) {
        super(jsonManager, spincastUtils);
        if(initialMap == null) {
            initialMap = new HashMap<String, Object>();
        }
        this.initialMap = initialMap;
        this.map = new HashMap<String, Object>();
    }

    /**
     * Init
     */
    @Inject
    protected void init() {
        addInitialMap();
    }

    protected void addInitialMap() {
        if(this.initialMap != null) {
            for(Entry<String, ?> entry : this.initialMap.entrySet()) {
                putConvert(entry.getKey(), entry.getValue());
            }
        }
    }

    protected Map<String, Object> getMap() {
        return this.map;
    }

    @Override
    public IJsonObject put(String key, String value) {
        return putAsIs(key, value);
    }

    @Override
    public IJsonObject put(String key, Integer value) {
        return putAsIs(key, value);
    }

    @Override
    public IJsonObject put(String key, Long value) {
        return putAsIs(key, value);
    }

    @Override
    public IJsonObject put(String key, Float value) {
        return putAsIs(key, value);
    }

    @Override
    public IJsonObject put(String key, Double value) {
        return putAsIs(key, value);
    }

    @Override
    public IJsonObject put(String key, Boolean value) {
        return putAsIs(key, value);
    }

    @Override
    public IJsonObject put(String key, BigDecimal value) {
        return putAsIs(key, value);
    }

    @Override
    public IJsonObject put(String key, byte[] value) {
        return putAsIs(key, value);
    }

    @Override
    public IJsonObject put(String key, Date value) {
        return putAsIs(key, value);
    }

    @Override
    public IJsonObject put(String key, IJsonObject value) {
        return put(key, value, false);
    }

    @Override
    public IJsonObject put(String key, IJsonObject value, boolean clone) {

        //==========================================
        // If the IJsonObject to put is Immutable, we
        // always clone it to make it mutable.
        //==========================================
        if(value != null && (clone || (value instanceof Immutable))) {
            value = value.clone(true);
        }
        return putAsIs(key, value);
    }

    @Override
    public IJsonObject put(String key, IJsonArray value) {
        return put(key, value, false);
    }

    @Override
    public IJsonObject put(String key, IJsonArray value, boolean clone) {

        //==========================================
        // If the IJsonArray to put is Immutable, we
        // always clone it to make it mutable.
        //==========================================
        if(value != null && (clone || (value instanceof Immutable))) {
            value = value.clone(true);
        }
        return putAsIs(key, value);
    }

    protected IJsonObject putAsIs(String key, Object value) {
        Objects.requireNonNull(key, "The key can't be NULL");
        getMap().put(key, value);
        return this;
    }

    @Override
    public IJsonObject putConvert(String key, Object value) {
        return putConvert(key, value, false);
    }

    @Override
    public IJsonObject putConvert(String key, Object value, boolean clone) {

        Objects.requireNonNull(key, "The key can't be NULL");

        //==========================================
        // If the element to put is Immutable, we
        // always clone it to make it mutable.
        //==========================================
        if(clone || value instanceof Immutable) {
            value = getJsonManager().clone(value, true);
        } else {
            value = getJsonManager().convertToNativeType(value);
        }

        putAsIs(key, value);

        return this;
    }

    @Override
    public IJsonObject merge(Map<String, Object> map) {
        return merge(map, false);
    }

    @Override
    public IJsonObject merge(Map<String, Object> map, boolean clone) {

        if(map == null) {
            return this;
        }

        for(Entry<String, Object> entry : map.entrySet()) {
            putConvert(entry.getKey(), entry.getValue(), clone);
        }
        return this;
    }

    @Override
    public IJsonObject merge(IJsonObject jsonObj) {
        return merge(jsonObj, false);
    }

    @Override
    public IJsonObject merge(IJsonObject jsonObj, boolean clone) {

        if(jsonObj == null) {
            return this;
        }

        for(Entry<String, Object> entry : jsonObj) {
            putConvert(entry.getKey(), entry.getValue(), clone);
        }
        return this;
    }

    @Override
    public IJsonObject remove(String key) {
        getMap().remove(key);
        return this;
    }

    @Override
    public IJsonObject removeAll() {
        getMap().clear();
        return this;
    }

    @Override
    public boolean isKeyExists(String key) {
        return getMap().containsKey(key);
    }

    protected <T> T getArrayFirst(String key,
                                  boolean hasDefaultValue,
                                  T defaultValue,
                                  IFirstElementGetter<T> firstElementGetter) {

        IJsonArray array = getJsonArray(key, null);

        if(array == null) {
            if(hasDefaultValue) {
                return defaultValue;
            }
            return null;
        }

        return firstElementGetter.get(array, hasDefaultValue, defaultValue);
    }

    @Override
    public IJsonObject getArrayFirstJsonObject(String key) {
        return getArrayFirstJsonObject(key, false, null);
    }

    @Override
    public IJsonObject getArrayFirstJsonObject(String key, IJsonObject defaultValue) {
        return getArrayFirstJsonObject(key, true, defaultValue);
    }

    protected IJsonObject getArrayFirstJsonObject(String key, boolean hasDefaultValue, IJsonObject defaultValue) {

        return getArrayFirst(key, hasDefaultValue, defaultValue, new IFirstElementGetter<IJsonObject>() {

            @Override
            public IJsonObject get(IJsonArray array,
                                   boolean hasDefaultValue,
                                   IJsonObject defaultValue) {
                if(hasDefaultValue) {
                    return array.getJsonObject(0, defaultValue);
                } else {
                    return array.getJsonObject(0);
                }
            }
        });
    }

    @Override
    public IJsonArray getArrayFirstJsonArray(String key) {
        return getArrayFirstJsonArray(key, false, null);
    }

    @Override
    public IJsonArray getArrayFirstJsonArray(String key, IJsonArray defaultValue) {
        return getArrayFirstJsonArray(key, true, defaultValue);
    }

    protected IJsonArray getArrayFirstJsonArray(String key, boolean hasDefaultValue, IJsonArray defaultValue) {

        return getArrayFirst(key, hasDefaultValue, defaultValue, new IFirstElementGetter<IJsonArray>() {

            @Override
            public IJsonArray get(IJsonArray array,
                                  boolean hasDefaultValue,
                                  IJsonArray defaultValue) {
                if(hasDefaultValue) {
                    return array.getJsonArray(0, defaultValue);
                } else {
                    return array.getJsonArray(0);
                }
            }
        });
    }

    @Override
    public String getArrayFirstString(String key) {
        return getArrayFirstString(key, false, null);
    }

    @Override
    public String getArrayFirstString(String key, String defaultValue) {
        return getArrayFirstString(key, true, defaultValue);
    }

    protected String getArrayFirstString(String key, boolean hasDefaultValue, String defaultValue) {

        return getArrayFirst(key, hasDefaultValue, defaultValue, new IFirstElementGetter<String>() {

            @Override
            public String get(IJsonArray array,
                              boolean hasDefaultValue,
                              String defaultValue) {
                if(hasDefaultValue) {
                    return array.getString(0, defaultValue);
                } else {
                    return array.getString(0);
                }
            }
        });
    }

    @Override
    public Integer getArrayFirstInteger(String key) {
        return getArrayFirstInteger(key, false, null);
    }

    @Override
    public Integer getArrayFirstInteger(String key, Integer defaultValue) {
        return getArrayFirstInteger(key, true, defaultValue);
    }

    protected Integer getArrayFirstInteger(String key, boolean hasDefaultValue, Integer defaultValue) {

        return getArrayFirst(key, hasDefaultValue, defaultValue, new IFirstElementGetter<Integer>() {

            @Override
            public Integer get(IJsonArray array,
                               boolean hasDefaultValue,
                               Integer defaultValue) {
                if(hasDefaultValue) {
                    return array.getInteger(0, defaultValue);
                } else {
                    return array.getInteger(0);
                }
            }
        });
    }

    @Override
    public Long getArrayFirstLong(String key) {
        return getArrayFirstLong(key, false, null);
    }

    @Override
    public Long getArrayFirstLong(String key, Long defaultValue) {
        return getArrayFirstLong(key, true, defaultValue);
    }

    protected Long getArrayFirstLong(String key, boolean hasDefaultValue, Long defaultValue) {

        return getArrayFirst(key, hasDefaultValue, defaultValue, new IFirstElementGetter<Long>() {

            @Override
            public Long get(IJsonArray array,
                            boolean hasDefaultValue,
                            Long defaultValue) {
                if(hasDefaultValue) {
                    return array.getLong(0, defaultValue);
                } else {
                    return array.getLong(0);
                }
            }
        });
    }

    @Override
    public Double getArrayFirstDouble(String key) {
        return getArrayFirstDouble(key, false, null);
    }

    @Override
    public Double getArrayFirstDouble(String key, Double defaultValue) {
        return getArrayFirstDouble(key, true, defaultValue);
    }

    protected Double getArrayFirstDouble(String key, boolean hasDefaultValue, Double defaultValue) {

        return getArrayFirst(key, hasDefaultValue, defaultValue, new IFirstElementGetter<Double>() {

            @Override
            public Double get(IJsonArray array,
                              boolean hasDefaultValue,
                              Double defaultValue) {
                if(hasDefaultValue) {
                    return array.getDouble(0, defaultValue);
                } else {
                    return array.getDouble(0);
                }
            }
        });
    }

    @Override
    public Float getArrayFirstFloat(String key) {
        return getArrayFirstFloat(key, false, null);
    }

    @Override
    public Float getArrayFirstFloat(String key, Float defaultValue) {
        return getArrayFirstFloat(key, true, defaultValue);
    }

    protected Float getArrayFirstFloat(String key, boolean hasDefaultValue, Float defaultValue) {

        return getArrayFirst(key, hasDefaultValue, defaultValue, new IFirstElementGetter<Float>() {

            @Override
            public Float get(IJsonArray array,
                             boolean hasDefaultValue,
                             Float defaultValue) {
                if(hasDefaultValue) {
                    return array.getFloat(0, defaultValue);
                } else {
                    return array.getFloat(0);
                }
            }
        });
    }

    @Override
    public Boolean getArrayFirstBoolean(String key) {
        return getArrayFirstBoolean(key, false, null);
    }

    @Override
    public Boolean getArrayFirstBoolean(String key, Boolean defaultValue) {
        return getArrayFirstBoolean(key, true, defaultValue);
    }

    protected Boolean getArrayFirstBoolean(String key, boolean hasDefaultValue, Boolean defaultValue) {

        return getArrayFirst(key, hasDefaultValue, defaultValue, new IFirstElementGetter<Boolean>() {

            @Override
            public Boolean get(IJsonArray array,
                               boolean hasDefaultValue,
                               Boolean defaultValue) {
                if(hasDefaultValue) {
                    return array.getBoolean(0, defaultValue);
                } else {
                    return array.getBoolean(0);
                }
            }
        });
    }

    @Override
    public BigDecimal getArrayFirstBigDecimal(String key) {
        return getArrayFirstBigDecimal(key, false, null);
    }

    @Override
    public BigDecimal getArrayFirstBigDecimal(String key, BigDecimal defaultValue) {
        return getArrayFirstBigDecimal(key, true, defaultValue);
    }

    protected BigDecimal getArrayFirstBigDecimal(String key, boolean hasDefaultValue, BigDecimal defaultValue) {

        return getArrayFirst(key, hasDefaultValue, defaultValue, new IFirstElementGetter<BigDecimal>() {

            @Override
            public BigDecimal get(IJsonArray array,
                                  boolean hasDefaultValue,
                                  BigDecimal defaultValue) {
                if(hasDefaultValue) {
                    return array.getBigDecimal(0, defaultValue);
                } else {
                    return array.getBigDecimal(0);
                }
            }
        });
    }

    @Override
    public byte[] getArrayFirstBytesFromBase64String(String key) {
        return getArrayFirstBytesFromBase64String(key, false, null);
    }

    @Override
    public byte[] getArrayFirstBytesFromBase64String(String key, byte[] defaultValue) {
        return getArrayFirstBytesFromBase64String(key, true, defaultValue);
    }

    protected byte[] getArrayFirstBytesFromBase64String(String key, boolean hasDefaultValue, byte[] defaultValue) {

        return getArrayFirst(key, hasDefaultValue, defaultValue, new IFirstElementGetter<byte[]>() {

            @Override
            public byte[] get(IJsonArray array,
                              boolean hasDefaultValue,
                              byte[] defaultValue) {
                if(hasDefaultValue) {
                    return array.getBytesFromBase64String(0, defaultValue);
                } else {
                    return array.getBytesFromBase64String(0);
                }
            }
        });
    }

    @Override
    public Date getArrayFirstDate(String key) {
        return getArrayFirstDate(key, false, null);
    }

    @Override
    public Date getArrayFirstDate(String key, Date defaultValue) {
        return getArrayFirstDate(key, true, defaultValue);
    }

    protected Date getArrayFirstDate(String key, boolean hasDefaultValue, Date defaultValue) {

        return getArrayFirst(key, hasDefaultValue, defaultValue, new IFirstElementGetter<Date>() {

            @Override
            public Date get(IJsonArray array,
                            boolean hasDefaultValue,
                            Date defaultValue) {
                if(hasDefaultValue) {
                    return array.getDate(0, defaultValue);
                } else {
                    return array.getDate(0);
                }
            }
        });
    }

    @Override
    public Iterator<Entry<String, Object>> iterator() {
        return getMap().entrySet().iterator();
    }

    @Override
    public IJsonObject clone() {
        return clone(true);
    }

    @Override
    public IJsonObject clone(boolean mutable) {
        return getJsonManager().cloneJsonObject(this, mutable);
    }

    @Override
    protected Object getElement(String keyPosition, boolean hasDefaultValue, Object defaultValue) {

        if(isKeyExists(keyPosition)) {
            return getMap().get(keyPosition);
        }
        if(hasDefaultValue) {
            return defaultValue;
        }
        return null;
    }

    @Override
    public Map<String, Object> convertToPlainMap() {

        Map<String, Object> map = new HashMap<String, Object>();
        for(Entry<String, Object> entry : this) {

            Object value = entry.getValue();
            if(value instanceof IJsonObject) {
                value = ((IJsonObject)value).convertToPlainMap();
            } else if(value instanceof IJsonArray) {
                value = ((IJsonArray)value).convertToPlainList();
            }
            map.put(entry.getKey(), value);
        }

        return map;
    }

}
