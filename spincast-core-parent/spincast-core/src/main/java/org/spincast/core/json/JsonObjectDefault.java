package org.spincast.core.json;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.utils.ObjectConverter;
import org.spincast.core.utils.SpincastUtils;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * <code>JsonObject</code> implementation.
 */
public class JsonObjectDefault extends JsonObjectArrayBase implements JsonObject {

    protected static final Logger logger = LoggerFactory.getLogger(JsonObjectDefault.class);

    protected static interface IFirstElementGetter<T> {

        public T get(JsonArray array, boolean hasDefaultValue, T defaultValue);
    }

    private final Map<String, Object> map;

    /**
     * Constructor
     */
    @AssistedInject
    public JsonObjectDefault(JsonManager jsonManager,
                             SpincastUtils spincastUtils,
                             ObjectConverter objectConverter) {
        this((Map<String, Object>)null,
             true,
             jsonManager,
             spincastUtils,
             objectConverter);
    }

    /**
     * Constructor
     */
    @AssistedInject
    public JsonObjectDefault(@Assisted @Nullable Map<String, Object> initialMap,
                             JsonManager jsonManager,
                             SpincastUtils spincastUtils,
                             ObjectConverter objectConverter) {
        this(initialMap,
             true,
             jsonManager,
             spincastUtils,
             objectConverter);
    }

    /**
     * Constructor
     */
    @AssistedInject
    public JsonObjectDefault(@Assisted @Nullable Map<String, Object> initialMap,
                             @Assisted boolean mutable,
                             JsonManager jsonManager,
                             SpincastUtils spincastUtils,
                             ObjectConverter objectConverter) {
        super(mutable, jsonManager, spincastUtils, objectConverter);

        Map<String, Object> map;
        if (initialMap != null) {

            //==========================================
            // If the JsonObject is immutable, all JsonObject
            // and JsonArray from the initial map must be
            // immutable too.
            //==========================================
            if (!mutable) {
                for (Object element : initialMap.values()) {
                    if (element instanceof JsonObjectOrArray && ((JsonObjectOrArray)element).isMutable()) {
                        throw new RuntimeException("To create an immutable JsonObject from an initial Map, " +
                                                   "all the JsonObject and JsonArray elements of that Map must already be " +
                                                   "immutable too. Here, at least one element is not immutable : " + element);
                    }
                }
            }
            map = initialMap;
        } else {
            map = new HashMap<String, Object>();
        }

        //==========================================
        // If the JsonObject is immutable, we make the underlying
        // map immutable.
        //==========================================
        if (!mutable) {
            map = Collections.unmodifiableMap(map);
        }

        this.map = map;
    }

    /**
     * Constructor
     */
    @AssistedInject
    public JsonObjectDefault(@Assisted @Nullable JsonObject configToMerge,
                             @Assisted boolean mutable,
                             JsonManager jsonManager,
                             SpincastUtils spincastUtils,
                             ObjectConverter objectConverter) {
        super(mutable, jsonManager, spincastUtils, objectConverter);

        Map<String, Object> map = new HashMap<String, Object>();
        if (configToMerge != null) {

            configToMerge = configToMerge.clone(mutable);

            for (Entry<String, Object> entry : configToMerge) {
                map.put(entry.getKey(), entry.getValue());
            }
        }

        //==========================================
        // If the JsonObject is immutable, we make the underlying
        // map immutable.
        //==========================================
        if (!mutable) {
            map = Collections.unmodifiableMap(map);
        }

        this.map = map;
    }

    protected Map<String, Object> getMap() {
        return this.map;
    }

    @Override
    protected JsonObject putAsIs(String key, Object value) {

        if (!isMutable()) {
            throw new RuntimeException("This object is immutable");
        }

        Objects.requireNonNull(key, "The key can't be NULL");
        getMap().put(key, value);
        return this;
    }

    @Override
    public JsonObject setNoKeyParsing(String jsonPath, Object value) {
        put(jsonPath, value, false, false);
        return this;
    }

    @Override
    public JsonObject setNoKeyParsing(String jsonPath, Object value, boolean clone) {
        put(jsonPath, value, clone, false);
        return this;
    }

    @Override
    public JsonObject merge(Map<String, ?> map) {
        return merge(map, false);
    }

    @Override
    public JsonObject merge(Map<String, ?> map, boolean clone) {

        if (!isMutable()) {
            throw new RuntimeException("This object is immutable");
        }

        if (map == null) {
            return this;
        }

        for (Entry<String, ?> entry : map.entrySet()) {
            set(entry.getKey(), entry.getValue(), clone);
        }
        return this;
    }

    @Override
    public JsonObject merge(JsonObject jsonObj) {
        return merge(jsonObj, false);
    }

    @Override
    public JsonObject merge(ToJsonObjectConvertible obj) {

        JsonObject jsonObj = null;
        if (obj != null) {
            jsonObj = obj.convertToJsonObject();
        }

        return merge(jsonObj, false);
    }

    @Override
    public JsonObject merge(JsonObject jsonObj, boolean clone) {

        if (!isMutable()) {
            throw new RuntimeException("This object is immutable");
        }

        if (jsonObj == null) {
            return this;
        }

        for (Entry<String, Object> entry : jsonObj) {

            //==========================================
            // No JsonPath parsing required.
            //==========================================
            put(entry.getKey(), entry.getValue(), clone, false);
        }
        return this;
    }

    /**
     * Removes a property at <code>JsonPath</code> 
     * from the object.
     */
    @Override
    public JsonObject remove(String jsonPath) {

        if (!isMutable()) {
            throw new RuntimeException("This object is immutable");
        }

        getJsonManager().removeElementAtJsonPath(this, jsonPath);
        return this;
    }

    @Override
    public JsonObject removeNoKeyParsing(String key) {

        if (!isMutable()) {
            throw new RuntimeException("This object is immutable");
        }

        getMap().remove(key);
        return this;
    }

    @Override
    public JsonObject clear() {

        if (!isMutable()) {
            throw new RuntimeException("This object is immutable");
        }

        getMap().clear();
        return this;
    }

    @Override
    public boolean isElementExistsNoKeyParsing(String key) {
        return getMap().containsKey(key);
    }

    @Override
    public Iterator<Entry<String, Object>> iterator() {
        return getMap().entrySet().iterator();
    }

    @Override
    protected Object getElementNoKeyParsing(String jsonPath, boolean hasDefaultValue, Object defaultValue) {
        if (isElementExistsNoKeyParsing(jsonPath)) {
            return getMap().get(jsonPath);
        }
        if (hasDefaultValue) {
            return defaultValue;
        }
        return null;
    }

    @Override
    public Map<String, Object> convertToPlainMap() {

        Map<String, Object> map = new HashMap<String, Object>();
        for (Entry<String, Object> entry : this) {

            Object value = entry.getValue();
            if (value instanceof JsonObject) {
                value = ((JsonObject)value).convertToPlainMap();
            } else if (value instanceof JsonArray) {
                value = ((JsonArray)value).convertToPlainList();
            }
            map.put(entry.getKey(), value);
        }

        return map;
    }

    @Override
    public <T> T convert(Class<T> clazz) {
        Objects.requireNonNull(clazz, "The class can't be NULL");
        return getJsonManager().fromString(toJsonString(), clazz);
    }

    @Override
    public int size() {
        return getMap().size();
    }

    @Override
    public boolean isEquivalentTo(JsonObject other) {

        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }

        if (other.size() != this.size()) {
            return false;
        }

        for (Entry<String, Object> entry : getMap().entrySet()) {

            String key = entry.getKey();
            Object thisElement = entry.getValue();

            Object otherElement = other.getObject(key);
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
    public JsonObject clone(boolean mutable) {
        return getJsonManager().cloneJsonObject(this, mutable);
    }

    @Override
    public void transformAll(ElementTransformer transformer, boolean recursive) {

        for (String key : getMap().keySet()) {
            transform(key, transformer);
            if (recursive) {
                Object obj = getObject(key);
                if (obj instanceof JsonArray) {
                    ((JsonArray)obj).transformAll(transformer, recursive);
                } else if (obj instanceof JsonObject) {
                    ((JsonObject)obj).transformAll(transformer, recursive);
                }
            }
        }
    }

}
