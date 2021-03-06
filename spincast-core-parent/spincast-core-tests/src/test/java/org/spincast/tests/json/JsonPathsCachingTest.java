package org.spincast.tests.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.junit.Test;
import org.spincast.core.guice.SpincastCorePluginModule;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonArrayDefault;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.json.JsonObjectArrayBase.JsonPathCachingItem;
import org.spincast.core.json.JsonObjectDefault;
import org.spincast.core.utils.ObjectConverter;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.testing.defaults.NoAppTestingBase;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class JsonPathsCachingTest extends NoAppTestingBase {

    @Override
    protected Injector createInjector() {

        return Spincast.configure()
                       .disableCorePlugin()
                       .module(new SpincastCorePluginModule() {

                           @Override
                           protected Class<? extends JsonObject> getJsonObjectImplClass() {
                               return JsonObjectTest.class;
                           }

                           @Override
                           protected Class<? extends JsonArray> getJsonArrayImplClass() {
                               return JsonArrayTest.class;
                           }
                       })
                       .init(new String[]{});
    }

    @Inject
    protected JsonManager jsonManager;

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected final static List<String> requestedJsonPaths = new ArrayList<String>();

    protected final static Map<String, JsonPathCachingItem> jsonPathCachingMap = new HashMap<String, JsonPathCachingItem>() {

        private static final long serialVersionUID = 1L;

        @Override
        public JsonPathCachingItem get(Object key) {
            getRequestedJsonPaths().add((String)key);
            return super.get(key);
        };
    };

    protected static Map<String, JsonPathCachingItem> getJsonPathCachingMapGlobal() {
        return jsonPathCachingMap;
    }

    protected static List<String> getRequestedJsonPaths() {
        return requestedJsonPaths;
    }

    @Override
    public void beforeTest() {
        super.beforeTest();
        getJsonPathCachingMapGlobal().clear();
        getRequestedJsonPaths().clear();
    }

    public static class JsonObjectTest extends JsonObjectDefault {

        @AssistedInject
        public JsonObjectTest(JsonManager jsonManager,
                              SpincastUtils spincastUtils,
                              ObjectConverter objectConverter) {
            super(jsonManager,
                  spincastUtils,
                  objectConverter);
        }

        /**
         * Constructor
         */
        @AssistedInject
        public JsonObjectTest(@Assisted @Nullable Map<String, Object> initialMap,
                              JsonManager jsonManager,
                              SpincastUtils spincastUtils,
                              ObjectConverter objectConverter) {
            super(initialMap,
                  jsonManager,
                  spincastUtils,
                  objectConverter);
        }

        /**
         * Constructor
         */
        @AssistedInject
        public JsonObjectTest(@Assisted @Nullable Map<String, Object> initialMap,
                              @Assisted boolean mutable,
                              JsonManager jsonManager,
                              SpincastUtils spincastUtils,
                              ObjectConverter objectConverter) {
            super(initialMap, mutable, jsonManager, spincastUtils, objectConverter);
        }

        /**
         * Constructor
         */
        @AssistedInject
        public JsonObjectTest(@Assisted @Nullable JsonObject configToMerge,
                              @Assisted boolean mutable,
                              JsonManager jsonManager,
                              SpincastUtils spincastUtils,
                              ObjectConverter objectConverter) {
            super(configToMerge, mutable, jsonManager, spincastUtils, objectConverter);
        }

        @Override
        protected Map<String, JsonPathCachingItem> getJsonPathCachingMap() {
            return getJsonPathCachingMapGlobal();
        }
    }

    public static class JsonArrayTest extends JsonArrayDefault {

        /**
         * Constructor
         */
        @AssistedInject
        public JsonArrayTest(JsonManager jsonManager,
                             SpincastUtils spincastUtils,
                             ObjectConverter objectConverter) {
            super(null,
                  true,
                  jsonManager,
                  spincastUtils,
                  objectConverter);
        }

        /**
         * Constructor
         */
        @AssistedInject
        public JsonArrayTest(@Assisted @Nullable List<Object> initialElements,
                             JsonManager jsonManager,
                             SpincastUtils spincastUtils,
                             ObjectConverter objectConverter) {
            super(initialElements,
                  true,
                  jsonManager,
                  spincastUtils,
                  objectConverter);
        }

        /**
         * Constructor
         */
        @AssistedInject
        public JsonArrayTest(@Assisted @Nullable List<Object> initialElements,
                             @Assisted boolean mutable,
                             JsonManager jsonManager,
                             SpincastUtils spincastUtils,
                             ObjectConverter objectConverter) {
            super(initialElements,
                  mutable,
                  jsonManager,
                  spincastUtils,
                  objectConverter);
        }

        @Override
        protected Map<String, JsonPathCachingItem> getJsonPathCachingMap() {
            return getJsonPathCachingMapGlobal();
        }
    }

    @Test
    public void notImmutableNoCaching() throws Exception {

        JsonObject inner = getJsonManager().create();
        inner.set("key2", "value2");

        JsonObject obj = getJsonManager().create();
        obj.set("key1", inner);

        assertEquals(0, getRequestedJsonPaths().size());
        assertEquals(0, getJsonPathCachingMapGlobal().size());

        String result = obj.getString("key1.key2");
        assertEquals("value2", result);

        inner.set("key2", "value3");

        result = obj.getString("key1.key2");
        assertEquals("value3", result);

        assertEquals(0, getRequestedJsonPaths().size());
        assertEquals(0, getJsonPathCachingMapGlobal().size());
    }

    @Test
    public void caching() throws Exception {

        JsonObject inner = getJsonManager().create();
        inner.set("key2", "value2");

        JsonObject obj = getJsonManager().create();
        obj.set("key1", inner);

        assertEquals(0, getRequestedJsonPaths().size());
        assertEquals(0, getJsonPathCachingMapGlobal().size());

        JsonObject objImmutable = obj.clone(false);
        assertFalse(objImmutable.isMutable());

        String result = objImmutable.getString("key1.key2");
        assertEquals("value2", result);

        assertEquals(1, getRequestedJsonPaths().size());
        assertEquals(1, getJsonPathCachingMapGlobal().size());

        result = objImmutable.getString("key1.key2");
        assertEquals("value2", result);

        assertEquals(2, getRequestedJsonPaths().size());
        assertEquals(1, getJsonPathCachingMapGlobal().size());

        boolean elementExists = objImmutable.contains("key1.key2");
        assertTrue(elementExists);
        assertEquals(3, getRequestedJsonPaths().size());
        assertEquals(1, getJsonPathCachingMapGlobal().size());
    }

    @Test
    public void cachingKeyDoesntExist() throws Exception {

        JsonObject inner = getJsonManager().create();
        inner.set("key2", "value2");

        JsonObject obj = getJsonManager().create();
        obj.set("key1", inner);

        assertEquals(0, getRequestedJsonPaths().size());
        assertEquals(0, getJsonPathCachingMapGlobal().size());

        JsonObject objImmutable = obj.clone(false);
        assertFalse(objImmutable.isMutable());

        String result = objImmutable.getString("key1.nope", "defaultValue");
        assertEquals("defaultValue", result);

        assertEquals(1, getRequestedJsonPaths().size());
        assertEquals(1, getJsonPathCachingMapGlobal().size());

        result = objImmutable.getString("key1.nope", "defaultValue2");
        assertEquals("defaultValue2", result);

        assertEquals(2, getRequestedJsonPaths().size());
        assertEquals(1, getJsonPathCachingMapGlobal().size());

        boolean elementExists = objImmutable.contains("key1.nope");
        assertFalse(elementExists);
        assertEquals(3, getRequestedJsonPaths().size());
        assertEquals(1, getJsonPathCachingMapGlobal().size());
    }

    @Test
    public void cachingWithJsonArray() throws Exception {

        JsonObject innerObj = getJsonManager().create();
        innerObj.set("key2", "value2");

        JsonArray inner = getJsonManager().createArray();
        inner.add("123");
        inner.add(innerObj);

        JsonObject obj = getJsonManager().create();
        obj.set("key1", inner);

        JsonArray array = getJsonManager().createArray();
        array.add(obj);

        assertEquals(0, getRequestedJsonPaths().size());
        assertEquals(0, getJsonPathCachingMapGlobal().size());

        JsonArray arrayImmutable = array.clone(false);
        assertFalse(arrayImmutable.isMutable());

        String result = arrayImmutable.getString("[0].key1[1].key2");
        assertEquals("value2", result);

        assertEquals(1, getRequestedJsonPaths().size());
        assertEquals(1, getJsonPathCachingMapGlobal().size());

        result = arrayImmutable.getString("[0].key1[1].key2");
        assertEquals("value2", result);

        assertEquals(2, getRequestedJsonPaths().size());
        assertEquals(1, getJsonPathCachingMapGlobal().size());

        boolean elementExists = arrayImmutable.contains("[0].key1[1].key2");
        assertTrue(elementExists);
        assertEquals(3, getRequestedJsonPaths().size());
        assertEquals(1, getJsonPathCachingMapGlobal().size());
    }

    @Test
    public void cachingWithJsonArrayKeyDoesntExist() throws Exception {

        JsonObject innerObj = getJsonManager().create();
        innerObj.set("key2", "value2");

        JsonArray inner = getJsonManager().createArray();
        inner.add("123");
        inner.add(innerObj);

        JsonObject obj = getJsonManager().create();
        obj.set("key1", inner);

        JsonArray array = getJsonManager().createArray();
        array.add(obj);

        assertEquals(0, getRequestedJsonPaths().size());
        assertEquals(0, getJsonPathCachingMapGlobal().size());

        JsonArray arrayImmutable = array.clone(false);
        assertFalse(arrayImmutable.isMutable());

        String result = arrayImmutable.getString("[0].key1[1].nope", "defaultValue");
        assertEquals("defaultValue", result);

        assertEquals(1, getRequestedJsonPaths().size());
        assertEquals(1, getJsonPathCachingMapGlobal().size());

        result = arrayImmutable.getString("[0].key1[1].nope", "defaultValue2");
        assertEquals("defaultValue2", result);

        assertEquals(2, getRequestedJsonPaths().size());
        assertEquals(1, getJsonPathCachingMapGlobal().size());

        boolean elementExists = arrayImmutable.contains("[0].key1[1].nope");
        assertFalse(elementExists);
        assertEquals(3, getRequestedJsonPaths().size());
        assertEquals(1, getJsonPathCachingMapGlobal().size());
    }

}
