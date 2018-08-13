package org.spincast.core.json;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.json.JsonObjectDefault.IFirstElementGetter;
import org.spincast.core.utils.ObjectConverter;
import org.spincast.core.utils.SpincastUtils;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * <code>JsonArray</code> implementation.
 */
public class JsonArrayDefault extends JsonObjectArrayBase implements JsonArray {

    protected final Logger logger = LoggerFactory.getLogger(JsonArrayDefault.class);

    private final List<Object> elements;

    /**
     * Constructor
     */
    @AssistedInject
    public JsonArrayDefault(JsonManager jsonManager,
                            SpincastUtils spincastUtils,
                            ObjectConverter objectConverter) {
        this(null,
             true,
             jsonManager,
             spincastUtils,
             objectConverter);
    }

    /**
     * Constructor
     */
    @AssistedInject
    public JsonArrayDefault(@Assisted @Nullable List<Object> initialElements,
                            JsonManager jsonManager,
                            SpincastUtils spincastUtils,
                            ObjectConverter objectConverter) {
        this(initialElements,
             true,
             jsonManager,
             spincastUtils,
             objectConverter);
    }

    /**
     * Constructor
     */
    @AssistedInject
    public JsonArrayDefault(@Assisted @Nullable List<Object> initialElements,
                            @Assisted boolean mutable,
                            JsonManager jsonManager,
                            SpincastUtils spincastUtils,
                            ObjectConverter objectConverter) {
        super(mutable, jsonManager, spincastUtils, objectConverter);

        List<Object> elements;
        if (initialElements != null) {

            //==========================================
            // If the JsonArray is immutable, all JsonObject
            // and JsonArray from the initial list must be
            // immutable too.
            //==========================================
            if (!mutable) {
                for (Object element : initialElements) {
                    if (element instanceof JsonObjectOrArray && ((JsonObjectOrArray)element).isMutable()) {
                        throw new RuntimeException("To create an immutable JsonArray from initial elements, " +
                                                   "all the JsonObject and JsonArray elements must already be " +
                                                   "immutable too. Here, at least one element is not immutable : " + element);
                    }
                }
            }
            elements = initialElements;
        } else {
            elements = new ArrayList<Object>();
        }

        //==========================================
        // If the JsonArray is immutable, we make the underlying
        // list immutable.
        //==========================================
        if (!mutable) {
            elements = Collections.unmodifiableList(elements);
        }

        this.elements = elements;
    }

    protected List<Object> getElements() {
        return this.elements;
    }

    protected Object getElement(int index) {

        if (index < 0) {
            index = 0;
        }

        if (index > getElements().size() - 1) {
            return null;
        }

        Object valObj = getElements().get(index);
        return valObj;
    }

    @Override
    public JsonArray add(Object value) {
        return setOrAdd(null, value, false, true);
    }

    @Override
    public JsonArray add(int index, Object value) {
        return setOrAdd(index, value, false, true);
    }

    @Override
    public JsonArray add(Object value, boolean clone) {
        return setOrAdd(null, value, clone, true);
    }

    @Override
    public JsonArray add(int index, Object value, boolean clone) {
        return setOrAdd(index, value, clone, true);
    }

    @Override
    public JsonArray addAll(Collection<?> values) {

        if (values == null) {
            return this;
        }

        for (Object value : values) {
            setOrAdd(null, value, false, true);
        }
        return this;
    }

    @Override
    public JsonArray addAll(Collection<?> values, boolean clone) {

        if (values == null) {
            return this;
        }

        for (Object value : values) {
            setOrAdd(null, value, clone, true);
        }
        return this;
    }

    @Override
    public JsonArray addAll(Object[] values) {

        if (values == null) {
            return this;
        }

        for (Object value : values) {
            setOrAdd(null, value, false, true);
        }
        return this;
    }

    @Override
    public JsonArray addAll(Object[] values, boolean clone) {

        if (values == null) {
            return this;
        }

        for (Object value : values) {
            setOrAdd(null, value, clone, true);
        }
        return this;
    }

    @Override
    public JsonArray addAll(JsonArray values) {

        if (values == null) {
            return this;
        }

        for (Object value : values) {
            setOrAdd(null, value, false, true);
        }
        return this;
    }

    @Override
    public JsonArray addAll(JsonArray values, boolean clone) {

        if (values == null) {
            return this;
        }

        for (Object value : values) {
            setOrAdd(null, value, clone, true);
        }
        return this;
    }

    @Override
    public JsonArray set(int index, Object value) {
        return setOrAdd(index, value, false, false);
    }

    @Override
    public JsonArray set(int index, Object value, boolean clone) {
        return setOrAdd(index, value, clone, false);
    }

    public JsonArray setOrAdd(Integer index, Object value, boolean clone, boolean insert) {

        if (!isMutable()) {
            throw new RuntimeException("This object is immutable");
        }

        if (value != null) {

            //==========================================
            // Can the object convert itself to a 
            // JsonObject or JsonArray?
            //==========================================
            boolean newObject = false;
            if (value instanceof ToJsonObjectConvertible) {
                newObject = true;
                value = ((ToJsonObjectConvertible)value).convertToJsonObject();
            } else if (value instanceof ToJsonArrayConvertible) {
                newObject = true;
                value = ((ToJsonArrayConvertible)value).convertToJsonArray();
            }

            //==========================================
            // Do we have to make a clone?
            // We always do if the value is a JsonObject or a JsonArray and
            // its mutability is not the same as the current object.
            //==========================================
            if (value instanceof JsonObjectOrArray) {
                if (!newObject && (clone || ((JsonObjectOrArray)value).isMutable() != isMutable())) {
                    value = ((JsonObjectOrArray)value).clone(isMutable());
                }
            } else {
                value = getJsonManager().convertToNativeType(value);
            }
        }

        setOrAddAsIs(index, value, insert);

        return this;
    }

    protected JsonArray addAsIs(Object value) {

        if (!isMutable()) {
            throw new RuntimeException("This object is immutable");
        }

        getElements().add(value);
        return this;
    }

    protected JsonArray addAsIs(Integer index, Object value) {
        return setOrAddAsIs(index, value, false);
    }

    protected JsonArray setOrAddAsIs(Integer index, Object value, boolean insert) {

        if (!isMutable()) {
            throw new RuntimeException("This object is immutable");
        }

        if (index == null) {
            return addAsIs(value);
        }

        if (index < 0) {
            throw new RuntimeException("Invalid index, must be >= 0 : " + index);
        }

        //==========================================
        // If we try to add an element at an index that
        // is greater than the next position in the array,
        // we insert NULL elements at intermediate indexes.
        //==========================================
        List<Object> elements = getElements();
        if (index >= elements.size()) {
            int limit = index;
            if (!insert) {
                limit = index + 1;
            }
            for (int i = elements.size(); i < limit; i++) {
                elements.add(i, null);
            }
        }

        if (insert) {
            elements.add(index, value);
        } else {
            elements.set(index, value);
        }

        return this;
    }

    @Override
    public JsonArray remove(String jsonPath) {

        if (!isMutable()) {
            throw new RuntimeException("This object is immutable");
        }

        getJsonManager().removeElementAtJsonPath(this, jsonPath);
        return this;
    }

    @Override
    public JsonArray remove(int index) {

        if (!isMutable()) {
            throw new RuntimeException("This object is immutable");
        }

        if (index < 0 || index > (getElements().size() - 1)) {
            return this;
        }

        getElements().remove(index);

        return this;
    }

    @Override
    public JsonArray clear() {

        if (!isMutable()) {
            throw new RuntimeException("This object is immutable");
        }

        getElements().clear();
        return this;
    }

    @Override
    public int size() {
        return getElements().size();
    }

    @Override
    public boolean isElementExists(int index) {
        return index >= 0 && index < size();
    }

    @Override
    public Iterator<Object> iterator() {
        return getElements().iterator();
    }

    @Override
    public List<String> convertToStringList() {

        List<Object> elements = getElements();

        List<String> stringList = new ArrayList<String>(elements.size());
        for (Object element : elements) {
            if (element == null) {
                stringList.add(null);
            } else {
                stringList.add(element.toString());
            }
        }

        return stringList;
    }

    @Override
    public JsonObject getJsonObject(int index) {
        return getJsonObjectNoKeyParsing(String.valueOf(index));
    }

    @Override
    public JsonObject getJsonObject(int index, JsonObject defaultValue) {
        return getJsonObjectNoKeyParsing(String.valueOf(index), defaultValue);
    }

    @Override
    public JsonObject getJsonObjectOrEmpty(int index) {
        return getJsonObjectOrEmptyNoKeyParsing(String.valueOf(index));
    }

    @Override
    public JsonArray getJsonArray(int index) {
        return getJsonArrayNoKeyParsing(String.valueOf(index));
    }

    @Override
    public JsonArray getJsonArray(int index, JsonArray defaultValue) {
        return getJsonArrayNoKeyParsing(String.valueOf(index), defaultValue);
    }

    @Override
    public JsonArray getJsonArrayOrEmpty(int index) {
        return getJsonArrayOrEmptyNoKeyParsing(String.valueOf(index));
    }

    @Override
    public String getString(int index) {
        return getStringNoKeyParsing(String.valueOf(index));
    }

    @Override
    public String getString(int index, String defaultValue) {
        return getStringNoKeyParsing(String.valueOf(index), defaultValue);
    }

    @Override
    public Integer getInteger(int index) {
        return getIntegerNoKeyParsing(String.valueOf(index));
    }

    @Override
    public Integer getInteger(int index, Integer defaultValue) {
        return getIntegerNoKeyParsing(String.valueOf(index), defaultValue);
    }

    @Override
    public Long getLong(int index) {
        return getLongNoKeyParsing(String.valueOf(index));
    }

    @Override
    public Long getLong(int index, Long defaultValue) {
        return getLongNoKeyParsing(String.valueOf(index), defaultValue);
    }

    @Override
    public Double getDouble(int index) {
        return getDoubleNoKeyParsing(String.valueOf(index));
    }

    @Override
    public Double getDouble(int index, Double defaultValue) {
        return getDoubleNoKeyParsing(String.valueOf(index), defaultValue);
    }

    @Override
    public Float getFloat(int index) {
        return getFloatNoKeyParsing(String.valueOf(index));
    }

    @Override
    public Float getFloat(int index, Float defaultValue) {
        return getFloatNoKeyParsing(String.valueOf(index), defaultValue);
    }

    @Override
    public Boolean getBoolean(int index) {
        return getBooleanNoKeyParsing(String.valueOf(index));
    }

    @Override
    public Boolean getBoolean(int index, Boolean defaultValue) {
        return getBooleanNoKeyParsing(String.valueOf(index), defaultValue);
    }

    @Override
    public BigDecimal getBigDecimal(int index) {
        return getBigDecimalNoKeyParsing(String.valueOf(index));
    }

    @Override
    public BigDecimal getBigDecimal(int index, BigDecimal defaultValue) {
        return getBigDecimalNoKeyParsing(String.valueOf(index), defaultValue);
    }

    @Override
    public byte[] getBytesFromBase64String(int index) {
        return getBytesFromBase64StringNoKeyParsing(String.valueOf(index));
    }

    @Override
    public byte[] getBytesFromBase64String(int index, byte[] defaultValue) {
        return getBytesFromBase64StringNoKeyParsing(String.valueOf(index), defaultValue);
    }

    @Override
    public Date getDate(int index) {
        return getDateNoKeyParsing(String.valueOf(index));
    }

    @Override
    public Date getDate(int index, Date defaultValue) {
        return getDateNoKeyParsing(String.valueOf(index), defaultValue);
    }

    @Override
    public Instant getInstant(int index) {
        return getInstantNoKeyParsing(String.valueOf(index));
    }

    @Override
    public Instant getInstant(int index, Instant defaultValue) {
        return getInstantNoKeyParsing(String.valueOf(index), defaultValue);
    }

    @Override
    public Object getObject(int index) {
        return getObjectNoKeyParsing(String.valueOf(index));
    }

    @Override
    public Object getObject(int index, Object defaultValue) {
        return getObjectNoKeyParsing(String.valueOf(index), defaultValue);
    }

    @Override
    protected Object getElementNoKeyParsing(String jsonPath, boolean hasDefaultValue, Object defaultValue) {
        int pos = Integer.parseInt(jsonPath);
        if (pos < 0 || pos > (getElements().size() - 1)) {

            if (hasDefaultValue) {
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

        JsonArray array = getJsonArray(index, null);

        if (array == null) {
            if (hasDefaultValue) {
                return defaultValue;
            }
            return null;
        }

        return firstElementGetter.get(array, hasDefaultValue, defaultValue);
    }

    @Override
    public JsonObject getArrayFirstJsonObject(int index) {
        return getArrayFirstJsonObjectNoKeyParsing(String.valueOf(index));
    }

    @Override
    public JsonObject getArrayFirstJsonObject(int index, JsonObject defaultValue) {
        return getArrayFirstJsonObjectNoKeyParsing(String.valueOf(index), defaultValue);
    }

    @Override
    public JsonArray getArrayFirstJsonArray(int index) {
        return getArrayFirstJsonArrayNoKeyParsing(String.valueOf(index));
    }

    @Override
    public JsonArray getArrayFirstJsonArray(int index, JsonArray defaultValue) {
        return getArrayFirstJsonArrayNoKeyParsing(String.valueOf(index), defaultValue);
    }

    @Override
    public String getArrayFirstString(int index) {
        return getArrayFirstStringNoKeyParsing(String.valueOf(index));
    }

    @Override
    public String getArrayFirstString(int index, String defaultValue) {
        return getArrayFirstStringNoKeyParsing(String.valueOf(index), defaultValue);
    }

    @Override
    public Integer getArrayFirstInteger(int index) {
        return getArrayFirstIntegerNoKeyParsing(String.valueOf(index));
    }

    @Override
    public Integer getArrayFirstInteger(int index, Integer defaultValue) {
        return getArrayFirstIntegerNoKeyParsing(String.valueOf(index), defaultValue);
    }

    @Override
    public Long getArrayFirstLong(int index) {
        return getArrayFirstLongNoKeyParsing(String.valueOf(index));
    }

    @Override
    public Long getArrayFirstLong(int index, Long defaultValue) {
        return getArrayFirstLongNoKeyParsing(String.valueOf(index));
    }

    @Override
    public Double getArrayFirstDouble(int index) {
        return getArrayFirstDoubleNoKeyParsing(String.valueOf(index));
    }

    @Override
    public Double getArrayFirstDouble(int index, Double defaultValue) {
        return getArrayFirstDoubleNoKeyParsing(String.valueOf(index), defaultValue);
    }

    @Override
    public Float getArrayFirstFloat(int index) {
        return getArrayFirstFloatNoKeyParsing(String.valueOf(index));
    }

    @Override
    public Float getArrayFirstFloat(int index, Float defaultValue) {
        return getArrayFirstFloatNoKeyParsing(String.valueOf(index), defaultValue);
    }

    @Override
    public Boolean getArrayFirstBoolean(int index) {
        return getArrayFirstBooleanNoKeyParsing(String.valueOf(index));
    }

    @Override
    public Boolean getArrayFirstBoolean(int index, Boolean defaultValue) {
        return getArrayFirstBooleanNoKeyParsing(String.valueOf(index), defaultValue);
    }

    @Override
    public BigDecimal getArrayFirstBigDecimal(int index) {
        return getArrayFirstBigDecimalNoKeyParsing(String.valueOf(index));
    }

    @Override
    public BigDecimal getArrayFirstBigDecimal(int index, BigDecimal defaultValue) {
        return getArrayFirstBigDecimalNoKeyParsing(String.valueOf(index), defaultValue);
    }

    @Override
    public byte[] getArrayFirstBytesFromBase64String(int index) {
        return getArrayFirstBytesFromBase64StringNoKeyParsing(String.valueOf(index));
    }

    @Override
    public byte[] getArrayFirstBytesFromBase64String(int index, byte[] defaultValue) {
        return getArrayFirstBytesFromBase64StringNoKeyParsing(String.valueOf(index), defaultValue);
    }

    @Override
    public Date getArrayFirstDate(int index) {
        return getArrayFirstDateNoKeyParsing(String.valueOf(index));
    }

    @Override
    public Date getArrayFirstDate(int index, Date defaultValue) {
        return getArrayFirstDateNoKeyParsing(String.valueOf(index), defaultValue);
    }

    @Override
    public Instant getArrayFirstInstant(int index) {
        return getArrayFirstInstantNoKeyParsing(String.valueOf(index));
    }

    @Override
    public Instant getArrayFirstInstant(int index, Instant defaultValue) {
        return getArrayFirstInstantNoKeyParsing(String.valueOf(index), defaultValue);
    }

    @Override
    public List<Object> convertToPlainList() {

        List<Object> list = new ArrayList<Object>();
        for (Object element : this) {

            if (element instanceof JsonObject) {
                element = ((JsonObject)element).convertToPlainMap();
            } else if (element instanceof JsonArray) {
                element = ((JsonArray)element).convertToPlainList();
            }
            list.add(element);
        }

        return list;
    }

    @Override
    public boolean isCanBeConvertedToString(int index) {
        return isCanBeConvertedToStringNoKeyParsing(String.valueOf(index));
    }

    @Override
    public boolean isCanBeConvertedToInteger(int index) {
        return isCanBeConvertedToIntegerNoKeyParsing(String.valueOf(index));
    }

    @Override
    public boolean isCanBeConvertedToLong(int index) {
        return isCanBeConvertedToLongNoKeyParsing(String.valueOf(index));
    }

    @Override
    public boolean isCanBeConvertedToFloat(int index) {
        return isCanBeConvertedToFloatNoKeyParsing(String.valueOf(index));
    }

    @Override
    public boolean isCanBeConvertedToDouble(int index) {
        return isCanBeConvertedToDoubleNoKeyParsing(String.valueOf(index));
    }

    @Override
    public boolean isCanBeConvertedToBoolean(int index) {
        return isCanBeConvertedToBooleanNoKeyParsing(String.valueOf(index));
    }

    @Override
    public boolean isCanBeConvertedToBigDecimal(int index) {
        return isCanBeConvertedToBigDecimalNoKeyParsing(String.valueOf(index));
    }

    @Override
    public boolean isCanBeConvertedToByteArray(int index) {
        return isCanBeConvertedToByteArrayNoKeyParsing(String.valueOf(index));
    }

    @Override
    public boolean isCanBeConvertedToDate(int index) {
        return isCanBeConvertedToDateNoKeyParsing(String.valueOf(index));
    }

    @Override
    public boolean isCanBeConvertedToJsonObject(int index) {
        return isCanBeConvertedToJsonObjectNoKeyParsing(String.valueOf(index));
    }

    @Override
    public boolean isCanBeConvertedToJsonArray(int index) {
        return isCanBeConvertedToJsonArrayNoKeyParsing(String.valueOf(index));
    }

    @Override
    public boolean isOfTypeString(int index) {
        return isOfTypeStringNoKeyParsing(String.valueOf(index));
    }

    @Override
    public boolean isOfTypeInteger(int index) {
        return isOfTypeIntegerNoKeyParsing(String.valueOf(index));
    }

    @Override
    public boolean isOfTypeLong(int index) {
        return isOfTypeLongNoKeyParsing(String.valueOf(index));
    }

    @Override
    public boolean isOfTypeFloat(int index) {
        return isOfTypeFloatNoKeyParsing(String.valueOf(index));
    }

    @Override
    public boolean isOfTypeDouble(int index) {
        return isOfTypeDoubleNoKeyParsing(String.valueOf(index));
    }

    @Override
    public boolean isOfTypeBoolean(int index) {
        return isOfTypeBooleanNoKeyParsing(String.valueOf(index));
    }

    @Override
    public boolean isOfTypeBigDecimal(int index) {
        return isOfTypeBigDecimalNoKeyParsing(String.valueOf(index));
    }

    @Override
    public boolean isOfTypeByteArray(int index, boolean acceptBase64StringToo) {
        return isOfTypeByteArrayNoKeyParsing(String.valueOf(index), acceptBase64StringToo);
    }

    @Override
    public boolean isOfTypeDate(int index) {
        return isOfTypeDateNoKeyParsing(String.valueOf(index));
    }

    @Override
    public boolean isOfTypeJsonObject(int index) {
        return isOfTypeJsonObjectNoKeyParsing(String.valueOf(index));
    }

    @Override
    public boolean isOfTypeJsonArray(int index) {
        return isOfTypeJsonArrayNoKeyParsing(String.valueOf(index));
    }

    @Override
    public boolean isNull(int index) {
        return isNullNoKeyParsing(String.valueOf(index));
    }

    @Override
    public boolean isEquivalentTo(JsonArray other) {

        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }

        if (other.size() != this.size()) {
            return false;
        }

        int nbr = this.size();
        for (int i = 0; i < nbr; i++) {

            Object thisElement = getElement(i);
            Object otherElement = other.getObject(i);

            Object otherElementConverted =
                    getObjectConverter().convertTo(otherElement, (thisElement != null ? thisElement.getClass() : null));

            if (thisElement != null && thisElement instanceof JsonObject) {
                if (otherElementConverted != null && !(otherElementConverted instanceof JsonObject)) {
                    return false;
                }
                if (!((JsonObject)thisElement).isEquivalentTo((JsonObject)otherElementConverted)) {
                    return false;
                }
            } else if (thisElement != null && thisElement instanceof JsonArray) {
                if (otherElementConverted != null && !(otherElementConverted instanceof JsonArray)) {
                    return false;
                }
                if (!((JsonArray)thisElement).isEquivalentTo((JsonArray)otherElementConverted)) {
                    return false;
                }
            } else if (thisElement != null && thisElement instanceof byte[]) {
                if (otherElementConverted != null && !(otherElementConverted instanceof byte[])) {
                    return false;
                }
                if (!Arrays.equals((byte[])thisElement, (byte[])otherElementConverted)) {
                    return false;
                }
            } else if (!Objects.equals(thisElement, otherElementConverted)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public JsonArray clone(boolean mutable) {
        return getJsonManager().cloneJsonArray(this, mutable);
    }

    @Override
    public void transformAll(ElementTransformer transformer, boolean recursive) {

        int size = size();
        for (int i = 0; i < size; i++) {
            transform(i, transformer);
            if (recursive) {
                Object obj = getObject(i);
                if (obj instanceof JsonArray) {
                    ((JsonArray)obj).transformAll(transformer, recursive);
                } else if (obj instanceof JsonObject) {
                    ((JsonObject)obj).transformAll(transformer, recursive);
                }
            }
        }
    }

    @Override
    public void transform(int index, ElementTransformer transformer) {
        super.transform("[" + index + "]", transformer);
    }

    @Override
    public void trim(int index) {
        super.trim("[" + index + "]");
    }

    @Override
    protected JsonArray putAsIs(String key, Object value) {

        if (!isMutable()) {
            throw new RuntimeException("This object is immutable");
        }

        Objects.requireNonNull(key, "The key can't be NULL");

        if (key.startsWith("[") && key.endsWith("]")) {
            key = key.substring(1, key.length() - 1);
        }
        int pos = Integer.parseInt(key);

        add(pos, value);

        return this;
    }

}
