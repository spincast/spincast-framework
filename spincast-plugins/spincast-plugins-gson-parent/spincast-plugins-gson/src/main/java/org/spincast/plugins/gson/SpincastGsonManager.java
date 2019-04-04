package org.spincast.plugins.gson;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.json.JsonObjectFactory;
import org.spincast.core.json.JsonObjectOrArray;
import org.spincast.core.json.JsonPathUtils;
import org.spincast.core.request.Form;
import org.spincast.core.request.FormFactory;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.gson.serializers.EnumSerializer;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.shaded.org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class SpincastGsonManager implements JsonManager {

    private final JsonDeserializer<JsonObject> jsonObjectDeserializer;
    private final JsonDeserializer<JsonArray> jsonArrayDeserializer;
    private final JsonSerializer<JsonObject> jsonObjectSerializer;
    private final JsonSerializer<JsonArray> jsonArraySerializer;
    private final JsonSerializer<Date> dateSerializer;
    private final JsonSerializer<Instant> instantSerializer;
    private final JsonSerializer<BigDecimal> bigDecimalSerializer;
    private final JsonSerializer<Enum<?>> enumSerializer;
    private final JsonSerializer<Class<?>> classSerializer;

    private final JsonPathUtils jsonPathUtils;
    private final JsonObjectFactory jsonObjectFactory;
    private final SpincastConfig spincastConfig;
    private final SpincastUtils spincastUtils;
    private final FormFactory formFactory;
    private final Provider<Injector> guiceProvider;

    private Gson gson;
    private Gson gsonPrettyPrinting;

    @Inject
    public SpincastGsonManager(JsonDeserializer<JsonObject> jsonObjectDeserializer,
                               JsonDeserializer<JsonArray> jsonArrayDeserializer,
                               JsonSerializer<JsonObject> jsonObjectSerializer,
                               JsonSerializer<JsonArray> jsonArraySerializer,
                               JsonSerializer<Date> dateSerializer,
                               JsonSerializer<Instant> instantSerializer,
                               JsonSerializer<BigDecimal> bigDecimalSerializer,
                               JsonSerializer<Enum<?>> enumSerializer,
                               JsonSerializer<Class<?>> classSerializer,
                               JsonPathUtils jsonPathUtils,
                               JsonObjectFactory jsonObjectFactory,
                               SpincastConfig spincastConfig,
                               SpincastUtils spincastUtils,
                               FormFactory formFactory,
                               Provider<Injector> guiceProvider) {
        this.jsonObjectDeserializer = jsonObjectDeserializer;
        this.jsonArrayDeserializer = jsonArrayDeserializer;
        this.jsonObjectSerializer = jsonObjectSerializer;
        this.jsonArraySerializer = jsonArraySerializer;
        this.dateSerializer = dateSerializer;
        this.instantSerializer = instantSerializer;
        this.bigDecimalSerializer = bigDecimalSerializer;
        this.classSerializer = classSerializer;
        this.jsonPathUtils = jsonPathUtils;
        this.jsonObjectFactory = jsonObjectFactory;
        this.enumSerializer = enumSerializer;
        this.spincastConfig = spincastConfig;
        this.spincastUtils = spincastUtils;
        this.formFactory = formFactory;
        this.guiceProvider = guiceProvider;
    }

    protected JsonDeserializer<JsonObject> getJsonObjectDeserializer() {
        return this.jsonObjectDeserializer;
    }

    protected JsonDeserializer<JsonArray> getJsonArrayDeserializer() {
        return this.jsonArrayDeserializer;
    }

    protected JsonSerializer<JsonObject> getJsonObjectSerializer() {
        return this.jsonObjectSerializer;
    }

    protected JsonSerializer<JsonArray> getJsonArraySerializer() {
        return this.jsonArraySerializer;
    }

    protected JsonSerializer<Date> getDateSerializer() {
        return this.dateSerializer;
    }

    protected JsonSerializer<Instant> getInstantSerializer() {
        return this.instantSerializer;
    }

    protected JsonSerializer<BigDecimal> getBigDecimalSerializer() {
        return this.bigDecimalSerializer;
    }

    protected JsonSerializer<Enum<?>> getEnumSerializer() {
        return this.enumSerializer;
    }

    protected JsonSerializer<Class<?>> getClassSerializer() {
        return this.classSerializer;
    }

    protected JsonPathUtils getJsonPathUtils() {
        return this.jsonPathUtils;
    }

    protected JsonObjectFactory getJsonObjectFactory() {
        return this.jsonObjectFactory;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected FormFactory getFormFactory() {
        return this.formFactory;
    }

    protected Injector getGuice() {
        return this.guiceProvider.get();
    }

    public Gson getGson() {
        if (this.gson == null) {
            createGsons();
        }
        return this.gson;
    }

    public Gson getGsonPrettyPrinting() {
        if (this.gsonPrettyPrinting == null) {
            createGsons();
        }
        return this.gsonPrettyPrinting;
    }

    protected void createGsons() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        configureGsonBuilder(gsonBuilder);

        this.gson = gsonBuilder.create();

        //==========================================
        // Pretty printing version
        //==========================================
        this.gsonPrettyPrinting = gsonBuilder.setPrettyPrinting().create();
    }

    /**
     * Configure the Gson builder
     */
    protected void configureGsonBuilder(GsonBuilder gsonBuilder) {
        registerCustomDeserializers(gsonBuilder);
        registerCustomSerializers(gsonBuilder);
    }

    protected void registerCustomDeserializers(GsonBuilder gsonBuilder) {
        registerJsonObjectDeserializer(gsonBuilder);
        registerJsonArrayDeserializer(gsonBuilder);
    }

    protected void registerJsonObjectDeserializer(GsonBuilder gsonBuilder) {
        gsonBuilder.registerTypeHierarchyAdapter(JsonObject.class, getJsonObjectDeserializer());
    }

    protected void registerJsonArrayDeserializer(GsonBuilder gsonBuilder) {
        gsonBuilder.registerTypeHierarchyAdapter(JsonArray.class, getJsonArrayDeserializer());
    }

    protected void registerCustomSerializers(GsonBuilder gsonBuilder) {
        registerJsonObjectSerializer(gsonBuilder);
        registerJsonArraySerializer(gsonBuilder);
        registerDateSerializer(gsonBuilder);
        registerInstantSerializer(gsonBuilder);
        registerBigDecimalSerializer(gsonBuilder);
        registerEnumSerializer(gsonBuilder);
        registerClassSerializer(gsonBuilder);
    }

    protected void registerJsonObjectSerializer(GsonBuilder gsonBuilder) {
        gsonBuilder.registerTypeHierarchyAdapter(JsonObject.class, getJsonObjectSerializer());
    }

    protected void registerJsonArraySerializer(GsonBuilder gsonBuilder) {
        gsonBuilder.registerTypeHierarchyAdapter(JsonArray.class, getJsonArraySerializer());
    }

    protected void registerDateSerializer(GsonBuilder gsonBuilder) {
        gsonBuilder.registerTypeHierarchyAdapter(Date.class, getDateSerializer());
    }

    protected void registerInstantSerializer(GsonBuilder gsonBuilder) {
        gsonBuilder.registerTypeAdapter(Instant.class, getInstantSerializer());
    }

    protected void registerBigDecimalSerializer(GsonBuilder gsonBuilder) {
        gsonBuilder.registerTypeHierarchyAdapter(BigDecimal.class, getBigDecimalSerializer());
    }

    protected void registerEnumSerializer(GsonBuilder gsonBuilder) {
        gsonBuilder.registerTypeHierarchyAdapter(Enum.class, getEnumSerializer());
    }

    protected void registerClassSerializer(GsonBuilder gsonBuilder) {
        gsonBuilder.registerTypeHierarchyAdapter(Class.class, getClassSerializer());
    }

    @Override
    public JsonObject create() {
        return getJsonObjectFactory().create();
    }

    @Override
    public JsonArray createArray() {
        return getJsonObjectFactory().createArray();
    }

    @Override
    public JsonObject fromObject(Object object) {
        if (object == null) {
            return null;
        }

        String json = getGson().toJson(object);
        JsonObject jsonObject = fromString(json);
        return jsonObject;
    }

    @Override
    public JsonObject fromMap(Map<String, ?> params) {
        return fromMap(params, false);
    }

    @Override
    public JsonObject fromMap(Map<String, ?> params, boolean parseKeysAsJsonPaths) {
        if (params == null) {
            return null;
        }

        JsonObject root = create();
        if (parseKeysAsJsonPaths && params.size() > getMaxNumberOfKeysWhenConvertingMapToJsonObject()) {
            throw new RuntimeException("Too many keys to parse : " + params.size() + " as JsonPaths. " +
                                       "The maximum is currently set to " + getMaxNumberOfKeysWhenConvertingMapToJsonObject());
        }

        for (Entry<String, ?> entry : params.entrySet()) {

            String key = entry.getKey();
            Object value = entry.getValue();

            if (parseKeysAsJsonPaths) {
                putElementAtJsonPath(root, key, value, true);
            } else {
                root.setNoKeyParsing(key, value);
            }
        }

        return root;
    }

    @Override
    public JsonObject fromInputStream(InputStream inputStream) {
        try {
            if (inputStream == null) {
                return null;
            }

            String json = IOUtils.toString(inputStream, getFromInputStreamEncoding());

            JsonObject obj = fromString(json);
            injectDependencies(obj);

            return obj;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected String getFromInputStreamEncoding() {
        return "UTF-8";
    }

    @Override
    public JsonObject fromFile(File jsonFile) {
        try {
            if (jsonFile == null || !jsonFile.exists()) {
                return null;
            }

            String json = FileUtils.readFileToString(jsonFile, getFromFileEncoding());

            JsonObject jsonObject = fromString(json);
            return jsonObject;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }

    }

    protected String getFromFileEncoding() {
        return "UTF-8";
    }

    @Override
    public JsonObject fromFile(String jsonFilePath) {
        if (jsonFilePath == null) {
            return null;
        }
        File file = new File(jsonFilePath);

        return fromFile(file);
    }

    @Override
    public JsonObject fromClasspathFile(String path) {
        if (path == null) {
            return null;
        }

        String json = getSpincastUtils().readClasspathFile(path, getFromClasspathFileEncoding());
        return fromString(json);
    }

    protected String getFromClasspathFileEncoding() {
        return "UTF-8";
    }

    @Override
    public Map<String, Object> fromStringToMap(String jsonString) {
        if (jsonString == null) {
            return null;
        }

        JsonObject jsonObject = fromString(jsonString);
        return jsonObject.convertToPlainMap();
    }

    @Override
    public Map<String, Object> fromInputStreamToMap(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        JsonObject jsonObject = fromInputStream(inputStream);
        return jsonObject.convertToPlainMap();
    }

    @Override
    public JsonObject fromString(String jsonString) {
        if (jsonString == null) {
            return null;
        }

        JsonObject obj = getGson().fromJson(jsonString, JsonObject.class);
        injectDependencies(obj);
        return obj;
    }

    @Override
    public <T> T fromString(String jsonString, Class<T> clazz) {
        T obj = getGson().fromJson(jsonString, clazz);
        injectDependencies(obj);
        return obj;
    }

    @Override
    public <T> T fromInputStream(InputStream inputStream, Class<T> clazz) {
        try {
            if (inputStream == null) {
                return null;
            }

            String jsonString = IOUtils.toString(inputStream, getFromInputStreamEncoding());
            T obj = getGson().fromJson(jsonString, clazz);
            injectDependencies(obj);

            return obj;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public JsonArray fromCollectionToJsonArray(Collection<?> collection) {
        if (collection == null) {
            return null;
        }

        JsonArray jsonArray = createArray();
        for (Object el : collection) {
            jsonArray.add(el);
        }

        return jsonArray;
    }

    @Override
    public JsonArray fromStringArray(String jsonString) {
        if (jsonString == null) {
            return null;
        }

        JsonArray jsonArray = getGson().fromJson(jsonString, JsonArray.class);
        injectDependencies(jsonArray);
        return jsonArray;
    }

    @Override
    public JsonArray fromListArray(List<?> elements) {
        if (elements == null) {
            return null;
        }

        JsonArray jsonArray = createArray();
        for (Object el : elements) {
            jsonArray.add(el);
        }

        return jsonArray;
    }

    @Override
    public JsonArray fromInputStreamArray(InputStream inputStream) {
        try {
            if (inputStream == null) {
                return null;
            }

            String json = IOUtils.toString(inputStream, getFromInputStreamEncoding());

            JsonArray jsonArray = fromStringArray(json);
            return jsonArray;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public Form createForm(String formName) {
        return getFormFactory().createForm(formName, null);
    }

    @Override
    public String toJsonString(Object obj) {
        return toJsonString(obj, false);
    }

    @Override
    public String toJsonString(Object obj, boolean pretty) {
        try {
            Gson gson = pretty ? getGsonPrettyPrinting() : getGson();
            String asString = gson.toJson(obj);
            return asString;

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public Date parseDateFromJson(String str) {
        return SpincastStatics.parseISO8601date(str);
    }

    @Override
    public String convertToJsonDate(Date date) {
        return SpincastStatics.getIso8601DateParserDefault().format(date);
    }

    @Override
    public Object convertToNativeType(Object originalObject) {
        if (originalObject == null) {
            return null;
        }

        //==========================================
        // Already a native type
        //==========================================
        if ((originalObject instanceof String) ||
            (originalObject instanceof Integer) ||
            (originalObject instanceof Long) ||
            (originalObject instanceof Float) ||
            (originalObject instanceof Double) ||
            (originalObject instanceof Boolean) ||
            (originalObject instanceof BigDecimal) ||
            (originalObject instanceof byte[]) ||
            (originalObject instanceof Date) ||
            (originalObject instanceof Instant) ||
            (originalObject instanceof JsonObject) ||
            (originalObject instanceof JsonArray)) {

            return originalObject;
        }

        //==========================================
        // Is a Gson JsonElement
        //==========================================
        if (originalObject instanceof JsonElement) {
            return getObjectFromGsonJsonElement((JsonElement)originalObject);
        }

        //==========================================
        // Array or Collection
        //==========================================
        if (originalObject instanceof Collection<?>) {

            JsonArray array = createArray();
            for (Object element : (Collection<?>)originalObject) {
                array.add(element);
            }
            return array;

        } else if (originalObject instanceof Object[]) {

            JsonArray array = createArray();
            for (Object element : (Object[])originalObject) {
                array.add(element);
            }
            return array;

        } else if (originalObject instanceof Map) {

            JsonObject obj = create();

            Map<?, ?> map = (Map<?, ?>)originalObject;
            for (Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() == null) {
                    throw new RuntimeException("Cannot convert a Map to a JsonObject when a key is NULL.");
                }
                obj.setNoKeyParsing(String.valueOf(entry.getKey()),
                                    entry.getValue());
            }

            return obj;

        }
        //==========================================
        // Why this? Don't we want ".name" and ".label" to be generated?
        // We would have that using the "Converts to a JsonObject"
        // code below because of our custom Json serializer.
        //
        // But only using the *names* of the enum values
        // is very useful since it is then possible to use
        // the serialized data as is in some filters. For
        // example :
        //
        // {{something | checked(menuItemForm.availabilityTypes)}}
        //
        // Here, if "menuItemForm.availabilityTypes" was an object with
        // ".name" and ".label" properties, the filter wouldn't work.
        //
        // To convert an enum to a "friendly" JsonObject, one can
        // use the "enumToFriendlyJsonObject(...)" and
        // "enumsToFriendlyJsonArray(...)" methods.
        //==========================================
        else if (originalObject instanceof Enum<?>) {
            return ((Enum<?>)originalObject).name();
        }

        //==========================================
        // Converts to a JsonObject or returns as
        // a string
        //==========================================
        String jsonStr = toJsonString(originalObject);

        if (jsonStr.startsWith("\"") && jsonStr.endsWith("\"") && jsonStr.length() > 1) {
            jsonStr = jsonStr.substring(1, jsonStr.length() - 1);
            return jsonStr;
        }

        JsonObject jsonObject = fromString(jsonStr);
        return jsonObject;
    }

    public JsonElement convertJsonObjectElementToGsonJsonElement(Object originalObject) {
        if (originalObject == null) {
            return null;
        }

        if (originalObject instanceof String) {
            return new JsonPrimitive((String)originalObject);
        }

        if (originalObject instanceof Integer) {
            return new JsonPrimitive((Integer)originalObject);
        }

        if (originalObject instanceof Long) {
            return new JsonPrimitive((Long)originalObject);
        }

        if (originalObject instanceof Float) {
            return new JsonPrimitive((Float)originalObject);
        }

        if (originalObject instanceof Double) {
            return new JsonPrimitive((Double)originalObject);
        }

        if (originalObject instanceof Boolean) {
            return new JsonPrimitive((Boolean)originalObject);
        }

        if (originalObject instanceof BigDecimal) {
            return new JsonPrimitive(((BigDecimal)originalObject).toPlainString());
        }

        if (originalObject instanceof byte[]) {
            String str = Base64.getEncoder().encodeToString((byte[])originalObject);
            return new JsonPrimitive(str);
        }

        if (originalObject instanceof Date) {
            String dateStr = SpincastStatics.getIso8601DateParserDefault().format((Date)originalObject);
            return new JsonPrimitive(dateStr);
        }

        if (originalObject instanceof Instant) {
            return new JsonPrimitive(((Instant)originalObject).toString());
        }

        if (originalObject instanceof JsonObject) {
            return getJsonObjectSerializer().serialize((JsonObject)originalObject, JsonObject.class, null);
        }

        if (originalObject instanceof JsonArray) {
            return getJsonArraySerializer().serialize((JsonArray)originalObject, JsonArray.class, null);
        }

        return new JsonPrimitive(String.valueOf(originalObject));
    }

    protected Object getObjectFromGsonJsonElement(JsonElement jsonElement) {

        if (jsonElement == null) {
            return null;
        }

        if (jsonElement.isJsonObject()) {
            JsonObject jsonObj = getJsonObjectDeserializer().deserialize(jsonElement, JsonObject.class, null);
            return jsonObj;
        }

        if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = getJsonArrayDeserializer().deserialize(jsonElement, JsonObject.class, null);
            return jsonArray;
        }

        if (!(jsonElement instanceof JsonPrimitive)) {
            return String.valueOf(jsonElement);
        }

        JsonPrimitive jsonPrimitive = (JsonPrimitive)jsonElement;

        if (jsonPrimitive.isBoolean()) {
            return jsonPrimitive.getAsBoolean();
        }

        if (jsonPrimitive.isJsonNull()) {
            return null;
        }

        if (jsonPrimitive.isNumber()) {
            String asString = jsonPrimitive.getAsString();
            if (asString.contains(".")) {
                Double doubleVal = Double.parseDouble(asString);
                return doubleVal;
            } else {
                long longvalue = jsonPrimitive.getAsLong();
                return longvalue;
            }
        }

        if (jsonPrimitive.isString()) {
            return jsonPrimitive.getAsString();
        }

        return String.valueOf(jsonElement);
    }

    @Override
    public Object clone(Object originalObject) {
        return clone(originalObject, true);
    }

    @Override
    public Object clone(Object originalObject, boolean mutable) {

        if (originalObject == null) {
            return null;
        }

        if ((originalObject instanceof String) ||
            (originalObject instanceof Integer) ||
            (originalObject instanceof Long) ||
            (originalObject instanceof Float) ||
            (originalObject instanceof Double) ||
            (originalObject instanceof Boolean) ||
            (originalObject instanceof BigDecimal) ||
            (originalObject instanceof byte[]) ||
            (originalObject instanceof Date)) {
            return originalObject;

        } else if (originalObject instanceof JsonObject) {

            JsonObject jsonObj = (JsonObject)originalObject;

            //==========================================
            // Already immutable, no need to clone!
            //==========================================
            if (!mutable && !jsonObj.isMutable()) {
                return jsonObj;
            }

            Map<String, Object> map = new HashMap<String, Object>();
            for (Entry<String, Object> entry : jsonObj) {
                Object elementClone = clone(entry.getValue(), mutable);
                map.put(entry.getKey(), elementClone);
            }
            return getJsonObjectFactory().create(map, mutable);

        } else if (originalObject instanceof JsonArray) {

            JsonArray array = (JsonArray)originalObject;

            //==========================================
            // Already immutable, no need to clone!
            //==========================================
            if (!mutable && !array.isMutable()) {
                return array;
            }

            List<Object> elements = new ArrayList<Object>();
            for (Object element : array) {
                Object elementClone = clone(element, mutable);
                elements.add(elementClone);
            }

            return getJsonObjectFactory().createArray(elements, mutable);

        } else {
            return convertToNativeType(originalObject);
        }
    }

    @Override
    public JsonObject cloneJsonObject(JsonObject jsonObject, boolean mutable) {
        return (JsonObject)clone(jsonObject, mutable);
    }

    @Override
    public JsonArray cloneJsonArray(JsonArray jsonArray, boolean mutable) {
        return (JsonArray)clone(jsonArray, mutable);
    }

    @Override
    public void removeElementAtJsonPath(JsonObject obj, String jsonPath) {
        getJsonPathUtils().removeElementAtJsonPath(obj, jsonPath);
    }

    @Override
    public void removeElementAtJsonPath(JsonArray array, String jsonPath) {
        getJsonPathUtils().removeElementAtJsonPath(array, jsonPath);
    }

    @Override
    public boolean isElementExists(JsonObject obj, String jsonPath) {
        return getJsonPathUtils().isElementExists(obj, jsonPath);
    }

    @Override
    public boolean isElementExists(JsonArray array, String jsonPath) {
        return getJsonPathUtils().isElementExists(array, jsonPath);
    }

    @Override
    public JsonObject enumToFriendlyJsonObject(Enum<?> enumValue) {
        if (enumValue == null) {
            return null;
        }
        JsonObject obj = create();
        obj.set(EnumSerializer.ENUM_SERIALIZER_FIELD_NAME_NAME, enumValue.name());
        obj.set(EnumSerializer.ENUM_SERIALIZER_FIELD_NAME_LABEL, enumValue.toString());

        return obj;
    }

    @Override
    public JsonArray enumsToFriendlyJsonArray(Enum<?>[] enumValues) {

        JsonArray arr = createArray();
        if (enumValues == null || enumValues.length == 0) {
            return arr;
        }

        for (Enum<?> enumValue : enumValues) {
            arr.add(enumToFriendlyJsonObject(enumValue));
        }

        return arr;
    }

    @Override
    public Object getElementAtJsonPath(JsonObject obj, String jsonPath) {
        return getJsonPathUtils().getElementAtJsonPath(obj, jsonPath);
    }

    @Override
    public Object getElementAtJsonPath(JsonObject obj, String jsonPath, Object defaultValue) {
        return getJsonPathUtils().getElementAtJsonPath(obj, jsonPath, defaultValue);
    }

    @Override
    public Object getElementAtJsonPath(JsonArray array, String jsonPath) {
        return getJsonPathUtils().getElementAtJsonPath(array, jsonPath);
    }

    @Override
    public Object getElementAtJsonPath(JsonArray array, String jsonPath, Object defaultValue) {
        return getJsonPathUtils().getElementAtJsonPath(array, jsonPath, defaultValue);
    }

    @Override
    public void putElementAtJsonPath(JsonObjectOrArray objOrArray, String jsonPath, Object value) {
        putElementAtJsonPath(objOrArray, jsonPath, value, false);
    }

    @Override
    public void putElementAtJsonPath(JsonObjectOrArray objOrArray, String jsonPath, Object value, boolean clone) {

        if (clone) {
            value = clone(value);
        }

        getJsonPathUtils().putElementAtJsonPath(objOrArray, jsonPath, value);
    }

    protected int getMaxNumberOfKeysWhenConvertingMapToJsonObject() {
        return getSpincastConfig().getMaxNumberOfKeysWhenConvertingMapToJsonObject();
    }

    /**
     * Once the deserialization of an Object is done,
     * we inject dependencies using Guice.
     */
    protected void injectDependencies(Object obj) {
        if (obj != null) {
            getGuice().injectMembers(obj);
        }
    }

}
