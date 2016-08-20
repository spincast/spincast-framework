package org.spincast.core.json;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.json.JsonObject.IFirstElementGetter;
import org.spincast.core.utils.ISpincastUtils;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * <code>IJsonArray</code> implementation.
 */
public class JsonArray extends JsonObjectArrayBase implements IJsonArray {

    protected final Logger logger = LoggerFactory.getLogger(JsonArray.class);

    private final List<Object> elements;
    private final List<Object> initialElements;

    /**
     * Constructor
     */
    @AssistedInject
    public JsonArray(IJsonManager jsonManager,
                     ISpincastUtils spincastUtils) {
        this(null, jsonManager, spincastUtils);
    }

    /**
     * Constructor
     */
    @AssistedInject
    public JsonArray(@Assisted @Nullable List<Object> initialElements,
                     IJsonManager jsonManager,
                     ISpincastUtils spincastUtils) {
        super(jsonManager, spincastUtils);
        if(initialElements == null) {
            initialElements = new ArrayList<Object>();
        }
        this.initialElements = initialElements;
        this.elements = new LinkedList<>();
    }

    /**
     * Init
     */
    @Inject
    protected void init() {
        addInitalElements();
    }

    protected void addInitalElements() {
        if(this.initialElements != null) {
            for(Object element : this.initialElements) {
                addConvert(element, true);
            }
        }
    }

    protected List<Object> getElements() {
        return this.elements;
    }

    protected Object getElement(int index) {

        if(index < 0) {
            index = 0;
        }

        if(index > getElements().size() - 1) {
            return null;
        }

        Object valObj = getElements().get(index);
        return valObj;
    }

    @Override
    public IJsonArray add(String value) {
        return addAsIs(value);
    }

    @Override
    public IJsonArray add(int index, String value) {
        return setOrAddAsIs(index, value, true);
    }

    @Override
    public IJsonArray set(int index, String value) {
        return addAsIs(index, value);
    }

    @Override
    public IJsonArray add(Integer value) {
        return addAsIs(value);
    }

    @Override
    public IJsonArray add(int index, Integer value) {
        return setOrAddAsIs(index, value, true);
    }

    @Override
    public IJsonArray set(int index, Integer value) {
        return addAsIs(index, value);
    }

    @Override
    public IJsonArray add(Long value) {
        return addAsIs(value);
    }

    @Override
    public IJsonArray add(int index, Long value) {
        return setOrAddAsIs(index, value, true);
    }

    @Override
    public IJsonArray set(int index, Long value) {
        return addAsIs(index, value);
    }

    @Override
    public IJsonArray add(Float value) {
        return addAsIs(value);
    }

    @Override
    public IJsonArray add(int index, Float value) {
        return setOrAddAsIs(index, value, true);
    }

    @Override
    public IJsonArray set(int index, Float value) {
        return addAsIs(index, value);
    }

    @Override
    public IJsonArray add(Double value) {
        return addAsIs(value);
    }

    @Override
    public IJsonArray add(int index, Double value) {
        return setOrAddAsIs(index, value, true);
    }

    @Override
    public IJsonArray set(int index, Double value) {
        return addAsIs(index, value);
    }

    @Override
    public IJsonArray add(Boolean value) {
        return addAsIs(value);
    }

    @Override
    public IJsonArray add(int index, Boolean value) {
        return setOrAddAsIs(index, value, true);
    }

    @Override
    public IJsonArray set(int index, Boolean value) {
        return addAsIs(index, value);
    }

    @Override
    public IJsonArray add(BigDecimal value) {
        return addAsIs(value);
    }

    @Override
    public IJsonArray add(int index, BigDecimal value) {
        return setOrAddAsIs(index, value, true);
    }

    @Override
    public IJsonArray set(int index, BigDecimal value) {
        return addAsIs(index, value);
    }

    @Override
    public IJsonArray add(byte[] value) {
        return addAsIs(value);
    }

    @Override
    public IJsonArray add(int index, byte[] value) {
        return setOrAddAsIs(index, value, true);
    }

    @Override
    public IJsonArray set(int index, byte[] value) {
        return addAsIs(index, value);
    }

    @Override
    public IJsonArray add(Date value) {
        return addAsIs(value);
    }

    @Override
    public IJsonArray add(int index, Date value) {
        return setOrAddAsIs(index, value, true);
    }

    @Override
    public IJsonArray set(int index, Date value) {
        return addAsIs(index, value);
    }

    @Override
    public IJsonArray add(IJsonObject jsonObj) {
        return setOrAdd(null, jsonObj, false, true);
    }

    @Override
    public IJsonArray add(int index, IJsonObject jsonObj) {
        return setOrAdd(index, jsonObj, false, true);
    }

    @Override
    public IJsonArray set(int index, IJsonObject jsonObj) {
        return setOrAdd(index, jsonObj, false, false);
    }

    @Override
    public IJsonArray add(IJsonObject jsonObj, boolean clone) {
        return setOrAdd(null, jsonObj, clone, true);
    }

    @Override
    public IJsonArray add(int index, IJsonObject jsonObj, boolean clone) {
        return setOrAdd(index, jsonObj, clone, true);
    }

    @Override
    public IJsonArray set(int index, IJsonObject jsonObj, boolean clone) {
        return setOrAdd(index, jsonObj, clone, false);
    }

    public IJsonArray setOrAdd(Integer index, IJsonObject jsonObj, boolean clone, boolean insert) {

        //==========================================
        // If the IJsonObject to put is Immutable, we
        // always clone it to make it mutable.
        //==========================================
        if(jsonObj != null && (clone || jsonObj instanceof Immutable)) {
            jsonObj = jsonObj.clone(true);
        }

        return setOrAddAsIs(index, jsonObj, insert);
    }

    @Override
    public IJsonArray add(IJsonArray value) {
        return setOrAdd(null, value, false, true);
    }

    @Override
    public IJsonArray add(int index, IJsonArray value) {
        return setOrAdd(index, value, false, true);
    }

    @Override
    public IJsonArray set(int index, IJsonArray value) {
        return setOrAdd(index, value, false, false);
    }

    @Override
    public IJsonArray add(IJsonArray value, boolean clone) {
        return setOrAdd(null, value, clone, true);
    }

    @Override
    public IJsonArray add(int index, IJsonArray value, boolean clone) {
        return setOrAdd(index, value, clone, true);
    }

    @Override
    public IJsonArray set(int index, IJsonArray array, boolean clone) {
        return setOrAdd(index, array, clone, false);
    }

    public IJsonArray setOrAdd(Integer index, IJsonArray array, boolean clone, boolean insert) {

        //==========================================
        // If the IJsonObject to put is Immutable, we
        // always clone it to make it mutable.
        //==========================================
        if(array != null && (clone || array instanceof Immutable)) {
            array = array.clone(true);
        }

        return setOrAddAsIs(index, array, insert);
    }

    @Override
    public IJsonArray addConvert(Object value) {
        return setOrAddConvert(null, value, false, true);
    }

    @Override
    public IJsonArray addConvert(int index, Object value) {
        return setOrAddConvert(index, value, false, true);
    }

    @Override
    public IJsonArray setConvert(int index, Object value) {
        return setOrAddConvert(index, value, false, false);
    }

    @Override
    public IJsonArray addConvert(Object value, boolean clone) {
        return setOrAddConvert(null, value, clone, true);
    }

    @Override
    public IJsonArray addConvert(int index, Object value, boolean clone) {
        return setOrAddConvert(index, value, clone, true);
    }

    @Override
    public IJsonArray setConvert(int index, Object value, boolean clone) {
        return setOrAddConvert(index, value, clone, false);
    }

    public IJsonArray setOrAddConvert(Integer index, Object value, boolean clone, boolean insert) {

        if(value != null) {

            if(value instanceof IJsonObject) {

                //==========================================
                // If the IJsonObject to put is Immutable, we
                // always clone it to make it mutable.
                //==========================================
                if(clone || value instanceof Immutable) {
                    value = getJsonManager().cloneJsonObject((IJsonObject)value);
                }

            } else if(value instanceof IJsonArray) {

                //==========================================
                // If the IJsonObject to put is Immutable, we
                // always clone it to make it mutable.
                //==========================================
                if(clone || value instanceof Immutable) {
                    value = getJsonManager().cloneJsonArray((IJsonArray)value);
                }
            } else {

                //==========================================
                // Make sure the value is of a known type. Otherwise,
                // we convert it.
                //==========================================
                value = getJsonManager().convertToNativeType(value);
            }
        }

        setOrAddAsIs(index, value, insert);

        return this;
    }

    protected IJsonArray addAsIs(Object value) {
        getElements().add(value);
        return this;
    }

    protected IJsonArray addAsIs(Integer index, Object value) {
        return setOrAddAsIs(index, value, false);
    }

    protected IJsonArray setOrAddAsIs(Integer index, Object value, boolean insert) {

        if(index == null) {
            return addAsIs(value);
        }

        if(index < 0) {
            throw new RuntimeException("Invalid index, must be >= 0 : " + index);
        }

        //==========================================
        // If we try to index a value at an index that
        // is greater than the next position in the array,
        // we insert NULL values at intermediate indexes.
        //==========================================
        List<Object> elements = getElements();
        if(index >= elements.size()) {
            int limit = index;
            if(!insert) {
                limit = index + 1;
            }
            for(int i = elements.size(); i < limit; i++) {
                elements.add(i, null);
            }
        }

        if(insert) {
            elements.add(index, value);
        } else {
            elements.set(index, value);
        }

        return this;
    }

    @Override
    public IJsonArray remove(int index) {

        if(index < 0 || index > (getElements().size() - 1)) {
            return this;
        }

        getElements().remove(index);

        return this;
    }

    @Override
    public IJsonArray clear() {
        getElements().clear();
        return this;
    }

    @Override
    public int size() {
        return getElements().size();
    }

    @Override
    public Iterator<Object> iterator() {
        return getElements().iterator();
    }

    @Override
    public IJsonArray clone() {
        return clone(true);
    }

    @Override
    public IJsonArray clone(boolean mutable) {
        return getJsonManager().cloneJsonArray(this, mutable);
    }

    @Override
    public List<String> convertToStringList() {

        List<Object> elements = getElements();

        List<String> stringList = new ArrayList<String>(elements.size());
        for(Object element : elements) {
            if(element == null) {
                stringList.add(null);
            } else {
                stringList.add(element.toString());
            }
        }

        return stringList;
    }

    @Override
    public IJsonObject getJsonObject(int index) {
        return getJsonObject(String.valueOf(index));
    }

    @Override
    public IJsonObject getJsonObject(int index, IJsonObject defaultValue) {
        return getJsonObject(String.valueOf(index), defaultValue);
    }

    @Override
    public IJsonObject getJsonObjectOrEmpty(int index) {
        return getJsonObjectOrEmpty(String.valueOf(index));
    }

    @Override
    public IJsonArray getJsonArray(int index) {
        return getJsonArray(String.valueOf(index));
    }

    @Override
    public IJsonArray getJsonArray(int index, IJsonArray defaultValue) {
        return getJsonArray(String.valueOf(index), defaultValue);
    }

    @Override
    public IJsonArray getJsonArrayOrEmpty(int index) {
        return getJsonArray(String.valueOf(index));
    }

    @Override
    public String getString(int index) {
        return getString(String.valueOf(index));
    }

    @Override
    public String getString(int index, String defaultValue) {
        return getString(String.valueOf(index), defaultValue);
    }

    @Override
    public Integer getInteger(int index) {
        return getInteger(String.valueOf(index));
    }

    @Override
    public Integer getInteger(int index, Integer defaultValue) {
        return getInteger(String.valueOf(index), defaultValue);
    }

    @Override
    public Long getLong(int index) {
        return getLong(String.valueOf(index));
    }

    @Override
    public Long getLong(int index, Long defaultValue) {
        return getLong(String.valueOf(index), defaultValue);
    }

    @Override
    public Double getDouble(int index) {
        return getDouble(String.valueOf(index));
    }

    @Override
    public Double getDouble(int index, Double defaultValue) {
        return getDouble(String.valueOf(index), defaultValue);
    }

    @Override
    public Float getFloat(int index) {
        return getFloat(String.valueOf(index));
    }

    @Override
    public Float getFloat(int index, Float defaultValue) {
        return getFloat(String.valueOf(index), defaultValue);
    }

    @Override
    public Boolean getBoolean(int index) {
        return getBoolean(String.valueOf(index));
    }

    @Override
    public Boolean getBoolean(int index, Boolean defaultValue) {
        return getBoolean(String.valueOf(index), defaultValue);
    }

    @Override
    public BigDecimal getBigDecimal(int index) {
        return getBigDecimal(String.valueOf(index));
    }

    @Override
    public BigDecimal getBigDecimal(int index, BigDecimal defaultValue) {
        return getBigDecimal(String.valueOf(index), defaultValue);
    }

    @Override
    public byte[] getBytesFromBase64String(int index) {
        return getBytesFromBase64String(String.valueOf(index));
    }

    @Override
    public byte[] getBytesFromBase64String(int index, byte[] defaultValue) {
        return getBytesFromBase64String(String.valueOf(index), defaultValue);
    }

    @Override
    public Date getDate(int index) {
        return getDate(String.valueOf(index));
    }

    @Override
    public Date getDate(int index, Date defaultValue) {
        return getDate(String.valueOf(index), defaultValue);
    }

    @Override
    protected Object getElement(String keyPosition, boolean hasDefaultValue, Object defaultValue) {

        int pos = Integer.parseInt(keyPosition);
        if(pos < 0 || pos > (getElements().size() - 1)) {

            if(hasDefaultValue) {
                return defaultValue;
            }
            return null;
        }

        return getElements().get(pos);
    }

    protected <T> T getArrayFirst(int index,
                                  boolean hasDefaultValue,
                                  T defaultValue,
                                  IFirstElementGetter<T> firstElementGetter) {

        IJsonArray array = getJsonArray(index, null);

        if(array == null) {
            if(hasDefaultValue) {
                return defaultValue;
            }
            return null;
        }

        return firstElementGetter.get(array, hasDefaultValue, defaultValue);
    }

    @Override
    public IJsonObject getArrayFirstJsonObject(int index) {
        return getArrayFirstJsonObject(index, false, null);
    }

    @Override
    public IJsonObject getArrayFirstJsonObject(int index, IJsonObject defaultValue) {
        return getArrayFirstJsonObject(index, true, defaultValue);
    }

    protected IJsonObject getArrayFirstJsonObject(int index, boolean hasDefaultValue, IJsonObject defaultValue) {

        return getArrayFirst(index, hasDefaultValue, defaultValue, new IFirstElementGetter<IJsonObject>() {

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
    public IJsonArray getArrayFirstJsonArray(int index) {
        return getArrayFirstJsonArray(index, false, null);
    }

    @Override
    public IJsonArray getArrayFirstJsonArray(int index, IJsonArray defaultValue) {
        return getArrayFirstJsonArray(index, true, defaultValue);
    }

    protected IJsonArray getArrayFirstJsonArray(int index, boolean hasDefaultValue, IJsonArray defaultValue) {

        return getArrayFirst(index, hasDefaultValue, defaultValue, new IFirstElementGetter<IJsonArray>() {

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
    public String getArrayFirstString(int index) {
        return getArrayFirstString(index, false, null);
    }

    @Override
    public String getArrayFirstString(int index, String defaultValue) {
        return getArrayFirstString(index, true, defaultValue);
    }

    protected String getArrayFirstString(int index, boolean hasDefaultValue, String defaultValue) {

        return getArrayFirst(index, hasDefaultValue, defaultValue, new IFirstElementGetter<String>() {

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
    public Integer getArrayFirstInteger(int index) {
        return getArrayFirstInteger(index, false, null);
    }

    @Override
    public Integer getArrayFirstInteger(int index, Integer defaultValue) {
        return getArrayFirstInteger(index, true, defaultValue);
    }

    protected Integer getArrayFirstInteger(int index, boolean hasDefaultValue, Integer defaultValue) {

        return getArrayFirst(index, hasDefaultValue, defaultValue, new IFirstElementGetter<Integer>() {

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
    public Long getArrayFirstLong(int index) {
        return getArrayFirstLong(index, false, null);
    }

    @Override
    public Long getArrayFirstLong(int index, Long defaultValue) {
        return getArrayFirstLong(index, true, defaultValue);
    }

    protected Long getArrayFirstLong(int index, boolean hasDefaultValue, Long defaultValue) {

        return getArrayFirst(index, hasDefaultValue, defaultValue, new IFirstElementGetter<Long>() {

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
    public Double getArrayFirstDouble(int index) {
        return getArrayFirstDouble(index, false, null);
    }

    @Override
    public Double getArrayFirstDouble(int index, Double defaultValue) {
        return getArrayFirstDouble(index, true, defaultValue);
    }

    protected Double getArrayFirstDouble(int index, boolean hasDefaultValue, Double defaultValue) {

        return getArrayFirst(index, hasDefaultValue, defaultValue, new IFirstElementGetter<Double>() {

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
    public Float getArrayFirstFloat(int index) {
        return getArrayFirstFloat(index, false, null);
    }

    @Override
    public Float getArrayFirstFloat(int index, Float defaultValue) {
        return getArrayFirstFloat(index, true, defaultValue);
    }

    protected Float getArrayFirstFloat(int index, boolean hasDefaultValue, Float defaultValue) {

        return getArrayFirst(index, hasDefaultValue, defaultValue, new IFirstElementGetter<Float>() {

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
    public Boolean getArrayFirstBoolean(int index) {
        return getArrayFirstBoolean(index, false, null);
    }

    @Override
    public Boolean getArrayFirstBoolean(int index, Boolean defaultValue) {
        return getArrayFirstBoolean(index, true, defaultValue);
    }

    protected Boolean getArrayFirstBoolean(int index, boolean hasDefaultValue, Boolean defaultValue) {

        return getArrayFirst(index, hasDefaultValue, defaultValue, new IFirstElementGetter<Boolean>() {

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
    public BigDecimal getArrayFirstBigDecimal(int index) {
        return getArrayFirstBigDecimal(index, false, null);
    }

    @Override
    public BigDecimal getArrayFirstBigDecimal(int index, BigDecimal defaultValue) {
        return getArrayFirstBigDecimal(index, true, defaultValue);
    }

    protected BigDecimal getArrayFirstBigDecimal(int index, boolean hasDefaultValue, BigDecimal defaultValue) {

        return getArrayFirst(index, hasDefaultValue, defaultValue, new IFirstElementGetter<BigDecimal>() {

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
    public byte[] getArrayFirstBytesFromBase64String(int index) {
        return getArrayFirstBytesFromBase64String(index, false, null);
    }

    @Override
    public byte[] getArrayFirstBytesFromBase64String(int index, byte[] defaultValue) {
        return getArrayFirstBytesFromBase64String(index, true, defaultValue);
    }

    protected byte[] getArrayFirstBytesFromBase64String(int index, boolean hasDefaultValue, byte[] defaultValue) {

        return getArrayFirst(index, hasDefaultValue, defaultValue, new IFirstElementGetter<byte[]>() {

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
    public Date getArrayFirstDate(int index) {
        return getArrayFirstDate(index, false, null);
    }

    @Override
    public Date getArrayFirstDate(int index, Date defaultValue) {
        return getArrayFirstDate(index, true, defaultValue);
    }

    protected Date getArrayFirstDate(int index, boolean hasDefaultValue, Date defaultValue) {

        return getArrayFirst(index, hasDefaultValue, defaultValue, new IFirstElementGetter<Date>() {

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
    public List<Object> convertToPlainList() {

        List<Object> list = new ArrayList<Object>();
        for(Object element : this) {

            if(element instanceof IJsonObject) {
                element = ((IJsonObject)element).convertToPlainMap();
            } else if(element instanceof IJsonArray) {
                element = ((IJsonArray)element).convertToPlainList();
            }
            list.add(element);
        }

        return list;
    }

}
