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
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.json.JsonArrayDefault;
import org.spincast.core.json.JsonObjectDefault;
import org.spincast.core.json.JsonObjectArrayBase.JsonPathCachingItem;
import org.spincast.core.utils.ObjectConverter;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.core.validation.ValidationFactory;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.testing.core.SpincastTestBase;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class JsonPathsCachingTest extends SpincastTestBase {

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

        /**
         * Constructor
         */
        @AssistedInject
        public JsonObjectTest(JsonManager jsonManager,
                              SpincastUtils spincastUtils,
                              ObjectConverter objectConverter,
                              ValidationFactory validationFactory) {
            super(null,
                  true,
                  jsonManager,
                  spincastUtils,
                  objectConverter,
                  validationFactory);
        }

        /**
         * Constructor
         */
        @AssistedInject
        public JsonObjectTest(@Assisted @Nullable Map<String, Object> initialMap,
                              JsonManager jsonManager,
                              SpincastUtils spincastUtils,
                              ObjectConverter objectConverter,
                              ValidationFactory validationFactory) {
            super(initialMap,
                  true,
                  jsonManager,
                  spincastUtils,
                  objectConverter,
                  validationFactory);
        }

        /**
         * Constructor
         */
        @AssistedInject
        public JsonObjectTest(@Assisted @Nullable Map<String, Object> initialMap,
                              @Assisted boolean mutable,
                              JsonManager jsonManager,
                              SpincastUtils spincastUtils,
                              ObjectConverter objectConverter,
                              ValidationFactory validationFactory) {
            super(initialMap,
                  mutable,
                  jsonManager,
                  spincastUtils,
                  objectConverter,
                  validationFactory);
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
                             ObjectConverter objectConverter,
                             ValidationFactory validationFactory) {
            super(null,
                  true,
                  jsonManager,
                  spincastUtils,
                  objectConverter,
                  validationFactory);
        }

        /**
         * Constructor
         */
        @AssistedInject
        public JsonArrayTest(@Assisted @Nullable List<Object> initialElements,
                             JsonManager jsonManager,
                             SpincastUtils spincastUtils,
                             ObjectConverter objectConverter,
                             ValidationFactory validationFactory) {
            super(initialElements,
                  true,
                  jsonManager,
                  spincastUtils,
                  objectConverter,
                  validationFactory);
        }

        /**
         * Constructor
         */
        @AssistedInject
        public JsonArrayTest(@Assisted @Nullable List<Object> initialElements,
                             @Assisted boolean mutable,
                             JsonManager jsonManager,
                             SpincastUtils spincastUtils,
                             ObjectConverter objectConverter,
                             ValidationFactory validationFactory) {
            super(initialElements,
                  mutable,
                  jsonManager,
                  spincastUtils,
                  objectConverter,
                  validationFactory);
        }

        @Override
        protected Map<String, JsonPathCachingItem> getJsonPathCachingMap() {
            return getJsonPathCachingMapGlobal();
        }
    }

    @Override
    protected Injector createInjector() {

        // @formatter:off
        return Guice.createInjector(new SpincastDefaultTestingModule() {
            
            @Override
            protected Class<? extends JsonObject> getJsonObjectImplClass() {
                return JsonObjectTest.class;
            }

            @Override
            protected Class<? extends JsonArray> getJsonArrayImplClass() {
                return JsonArrayTest.class;
            }

        });
    }

    @Test
    public void notImmutableNoCaching() throws Exception {

        JsonObject inner = getJsonManager().create();
        inner.put("key2", "value2");

        JsonObject obj = getJsonManager().create();
        obj.put("key1", inner);
        
        assertEquals(0, getRequestedJsonPaths().size());
        assertEquals(0, getJsonPathCachingMapGlobal().size());

        String result = obj.getString("key1.key2");
        assertEquals("value2", result);

        inner.put("key2", "value3");

        result = obj.getString("key1.key2");
        assertEquals("value3", result);
        
        assertEquals(0, getRequestedJsonPaths().size());
        assertEquals(0, getJsonPathCachingMapGlobal().size());
    }

    @Test
    public void caching() throws Exception {

        JsonObject inner = getJsonManager().create();
        inner.put("key2", "value2");

        JsonObject obj = getJsonManager().create();
        obj.put("key1", inner);
        
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
        
        boolean elementExists = objImmutable.isElementExists("key1.key2");
        assertTrue(elementExists);
        assertEquals(3, getRequestedJsonPaths().size());
        assertEquals(1, getJsonPathCachingMapGlobal().size());
    }
    
    @Test
    public void cachingKeyDoesntExist() throws Exception {

        JsonObject inner = getJsonManager().create();
        inner.put("key2", "value2");

        JsonObject obj = getJsonManager().create();
        obj.put("key1", inner);
        
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
        
        boolean elementExists = objImmutable.isElementExists("key1.nope");
        assertFalse(elementExists);
        assertEquals(3, getRequestedJsonPaths().size());
        assertEquals(1, getJsonPathCachingMapGlobal().size());
    }
    
    @Test
    public void cachingWithJsonArray() throws Exception {

        JsonObject innerObj = getJsonManager().create();
        innerObj.put("key2", "value2");
        
        JsonArray inner = getJsonManager().createArray();
        inner.add("123");
        inner.add(innerObj);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", inner);
        
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
        
        boolean elementExists = arrayImmutable.isElementExists("[0].key1[1].key2");
        assertTrue(elementExists);
        assertEquals(3, getRequestedJsonPaths().size());
        assertEquals(1, getJsonPathCachingMapGlobal().size());
    }
    
    @Test
    public void cachingWithJsonArrayKeyDoesntExist() throws Exception {

        JsonObject innerObj = getJsonManager().create();
        innerObj.put("key2", "value2");
        
        JsonArray inner = getJsonManager().createArray();
        inner.add("123");
        inner.add(innerObj);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", inner);
        
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
        
        boolean elementExists = arrayImmutable.isElementExists("[0].key1[1].nope");
        assertFalse(elementExists);
        assertEquals(3, getRequestedJsonPaths().size());
        assertEquals(1, getJsonPathCachingMapGlobal().size());
    }


}
