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
                addConvert(element);
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
    public IJsonArray add(Integer value) {
        return addAsIs(value);
    }

    @Override
    public IJsonArray add(Long value) {
        return addAsIs(value);
    }

    @Override
    public IJsonArray add(Float value) {
        return addAsIs(value);
    }

    @Override
    public IJsonArray add(Double value) {
        return addAsIs(value);
    }

    @Override
    public IJsonArray add(Boolean value) {
        return addAsIs(value);
    }

    @Override
    public IJsonArray add(BigDecimal value) {
        return addAsIs(value);
    }

    @Override
    public IJsonArray add(byte[] value) {
        return addAsIs(value);
    }

    @Override
    public IJsonArray add(Date value) {
        return addAsIs(value);
    }

    @Override
    public IJsonArray addConvert(Object value) {
        return addConvert(value, false);
    }

    @Override
    public IJsonArray addConvert(Object value, boolean clone) {

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

        addAsIs(value);

        return this;
    }

    @Override
    public IJsonArray add(IJsonObject jsonObj) {
        return add(jsonObj, false);
    }

    @Override
    public IJsonArray add(IJsonObject jsonObj, boolean clone) {

        //==========================================
        // If the IJsonObject to put is Immutable, we
        // always clone it to make it mutable.
        //==========================================
        if(jsonObj != null && (clone || jsonObj instanceof Immutable)) {
            jsonObj = jsonObj.clone(true);
        }

        addAsIs(jsonObj);
        return this;
    }

    @Override
    public IJsonArray add(IJsonArray value) {
        return add(value, false);
    }

    @Override
    public IJsonArray add(IJsonArray array, boolean clone) {

        //==========================================
        // If the IJsonObject to put is Immutable, we
        // always clone it to make it mutable.
        //==========================================
        if(array != null && (clone || array instanceof Immutable)) {
            array = array.clone(true);
        }
        return addAsIs(array);
    }

    protected IJsonArray addAsIs(Object value) {
        getElements().add(value);
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
