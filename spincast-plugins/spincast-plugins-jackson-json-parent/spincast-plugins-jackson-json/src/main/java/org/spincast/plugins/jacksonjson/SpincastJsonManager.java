package org.spincast.plugins.jacksonjson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter.Indenter;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

/**
 * Spincast Jackson Json manager
 */
public class SpincastJsonManager implements JsonManager {

    protected final Logger logger = LoggerFactory.getLogger(SpincastJsonManager.class);

    public static final String ENUM_SERIALIZER_FIELD_NAME_NAME = "name";
    public static final String ENUM_SERIALIZER_FIELD_NAME_LABEL = "label";

    private final JsonObjectFactory jsonObjectFactory;
    private final Provider<Injector> guiceProvider;
    private final Set<JsonMixinInfo> jsonMixinInfos;
    private final SpincastConfig spincastConfig;
    private final SpincastJsonManagerConfig spincastJsonManagerConfig;
    private final JsonPathUtils jsonPathUtils;
    private final SpincastUtils spincastUtils;
    private final FormFactory formFactory;

    private ObjectMapper objectMapper;
    private JsonSerializer<JsonObject> jsonObjectSerializer;
    private JsonDeserializer<JsonObject> jsonObjectDeserializer;
    private JsonSerializer<JsonArray> jsonArraySerializer;
    private JsonDeserializer<JsonArray> jsonArrayDeserializer;
    private JsonSerializer<Date> dateSerializer;
    private JsonSerializer<Instant> instantSerializer;
    private JsonSerializer<BigDecimal> bigDecimalSerializer;
    private DefaultPrettyPrinter jacksonPrettyPrinter;
    private JsonSerializer<Enum<?>> enumSerializer;

    @Inject
    public SpincastJsonManager(Provider<Injector> guiceProvider,
                               JsonObjectFactory jsonObjectFactory,
                               @Nullable Set<JsonMixinInfo> jsonMixinInfos,
                               SpincastJsonManagerConfig spincastJsonManagerConfig,
                               SpincastConfig spincastConfig,
                               JsonPathUtils jsonPathUtils,
                               SpincastUtils spincastUtils,
                               FormFactory formFactory) {

        this.guiceProvider = guiceProvider;
        this.jsonObjectFactory = jsonObjectFactory;

        if (jsonMixinInfos == null) {
            jsonMixinInfos = new HashSet<JsonMixinInfo>();
        }
        this.jsonMixinInfos = jsonMixinInfos;
        this.spincastJsonManagerConfig = spincastJsonManagerConfig;
        this.spincastConfig = spincastConfig;
        this.jsonPathUtils = jsonPathUtils;
        this.spincastUtils = spincastUtils;
        this.formFactory = formFactory;
    }

    protected Injector getGuice() {
        return this.guiceProvider.get();
    }

    protected JsonObjectFactory getJsonObjectFactory() {
        return this.jsonObjectFactory;
    }

    protected Set<JsonMixinInfo> getJsonMixinInfos() {
        return this.jsonMixinInfos;
    }

    protected SpincastJsonManagerConfig getSpincastJsonManagerConfig() {
        return this.spincastJsonManagerConfig;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected JsonPathUtils getJsonPathUtils() {
        return this.jsonPathUtils;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected FormFactory getFormFactory() {
        return this.formFactory;
    }

    protected DefaultPrettyPrinter getJacksonPrettyPrinter() {
        if (this.jacksonPrettyPrinter == null) {
            this.jacksonPrettyPrinter = new DefaultPrettyPrinter();

            Indenter indenter = new DefaultIndenter(getJacksonPrettyPrinterIndentation(), getJacksonPrettyPrinterNewline());

            this.jacksonPrettyPrinter.indentObjectsWith(indenter);
            this.jacksonPrettyPrinter.indentArraysWith(indenter);
        }
        return this.jacksonPrettyPrinter;
    }

    protected String getJacksonPrettyPrinterNewline() {
        return getSpincastJsonManagerConfig().getPrettyPrinterNewlineChars();
    }

    protected String getJacksonPrettyPrinterIndentation() {
        return StringUtils.repeat(" ", getSpincastJsonManagerConfig().getPrettyPrinterIndentationSpaceNumber());
    }


    protected ObjectMapper getObjectMapper() {
        if (this.objectMapper == null) {

            ObjectMapper objectMapper = createObjectManager();
            registerCustomModules(objectMapper);
            this.objectMapper = objectMapper;
        }
        return this.objectMapper;
    }

    /**
     * Creates the ObjectMapper
     */
    protected ObjectMapper createObjectManager() {
        ObjectMapper objectMapper = new ObjectMapper();
        configureObjectMapper(objectMapper);
        return objectMapper;
    }

    /**
     * Configuration of the ObjectMapper.
     */
    protected void configureObjectMapper(ObjectMapper objectMapper) {

        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, isAllowCommentsInJson());

        //==========================================
        // To allow serialization of "empty" POJOs (no properties to serialize)
        // (without this setting, an exception is thrown in those cases)
        //==========================================
        configureEmptyBeans(objectMapper);

        //==========================================
        // Add the mixins, if any.
        //==========================================
        configureMixins(objectMapper);
    }

    /**
     * Should comments be accepted in Json?
     * @see https://github.com/FasterXML/jackson-core/wiki/JsonParser-Features#support-for-non-standard-data-format-constructs
     */
    protected boolean isAllowCommentsInJson() {

        //==========================================
        // Accepted by default
        //==========================================
        return true;
    }

    protected void configureEmptyBeans(ObjectMapper objectMapper) {
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    protected void configureMixins(ObjectMapper objectMapper) {
        for (JsonMixinInfo jsonMixinInfo : getJsonMixinInfos()) {
            objectMapper.addMixIn(jsonMixinInfo.getTargetClass(), jsonMixinInfo.getMixinClass());
        }
    }

    protected JsonSerializer<JsonObject> getJsonObjectSerializer() {

        if (this.jsonObjectSerializer == null) {
            this.jsonObjectSerializer = new JsonSerializer<JsonObject>() {

                @Override
                public void serialize(JsonObject jsonObject,
                                      JsonGenerator gen,
                                      SerializerProvider serializers)
                                                                      throws IOException, JsonProcessingException {

                    if (jsonObject == null) {
                        return;
                    }

                    Map<String, Object> asMap = jsonObject.convertToPlainMap();
                    gen.writeObject(asMap);
                }
            };
        }
        return this.jsonObjectSerializer;
    }

    protected JsonSerializer<Enum<?>> getEnumSerializer() {

        if (this.enumSerializer == null) {
            this.enumSerializer = new JsonSerializer<Enum<?>>() {

                @Override
                public void serialize(Enum<?> enumValue,
                                      JsonGenerator gen,
                                      SerializerProvider serializers)
                                                                      throws IOException, JsonProcessingException {

                    if (enumValue == null) {
                        return;
                    }

                    Map<String, Object> enumObj = new HashMap<String, Object>();
                    enumObj.put(ENUM_SERIALIZER_FIELD_NAME_NAME, enumValue.name());
                    enumObj.put(ENUM_SERIALIZER_FIELD_NAME_LABEL, enumValue.toString());

                    gen.writeObject(enumObj);
                }
            };
        }
        return this.enumSerializer;
    }


    protected JsonSerializer<JsonArray> getJsonArraySerializer() {

        if (this.jsonArraySerializer == null) {
            this.jsonArraySerializer = new JsonSerializer<JsonArray>() {

                @Override
                public void serialize(JsonArray jsonArray,
                                      JsonGenerator gen,
                                      SerializerProvider serializers)
                                                                      throws IOException, JsonProcessingException {

                    if (jsonArray == null) {
                        return;
                    }

                    List<Object> asList = jsonArray.convertToPlainList();
                    gen.writeObject(asList);
                }
            };
        }
        return this.jsonArraySerializer;
    }

    protected JsonSerializer<Date> getDateSerializer() {

        if (this.dateSerializer == null) {
            this.dateSerializer = new JsonSerializer<Date>() {

                @Override
                public void serialize(Date date,
                                      JsonGenerator gen,
                                      SerializerProvider serializers)
                                                                      throws IOException, JsonProcessingException {

                    if (date == null) {
                        return;
                    }

                    String dateStr = SpincastStatics.getIso8601DateParserDefault().format(date);
                    gen.writeObject(dateStr);
                }
            };
        }
        return this.dateSerializer;
    }

    protected JsonSerializer<Instant> getInstantSerializer() {

        if (this.instantSerializer == null) {
            this.instantSerializer = new JsonSerializer<Instant>() {

                @Override
                public void serialize(Instant instant,
                                      JsonGenerator gen,
                                      SerializerProvider serializers)
                                                                      throws IOException, JsonProcessingException {

                    if (instant == null) {
                        return;
                    }
                    getDateSerializer().serialize(Date.from(instant), gen, serializers);
                }
            };
        }
        return this.instantSerializer;
    }


    protected JsonSerializer<BigDecimal> getBigDecimalSerializer() {

        if (this.bigDecimalSerializer == null) {
            this.bigDecimalSerializer = new JsonSerializer<BigDecimal>() {

                @Override
                public void serialize(BigDecimal bigDecimal,
                                      JsonGenerator gen,
                                      SerializerProvider serializers)
                                                                      throws IOException, JsonProcessingException {

                    if (bigDecimal == null) {
                        return;
                    }
                    gen.writeObject(bigDecimal.toPlainString());
                }
            };
        }
        return this.bigDecimalSerializer;
    }

    protected JsonDeserializer<JsonObject> getJsonObjectDeserializer() {

        if (this.jsonObjectDeserializer == null) {
            this.jsonObjectDeserializer = new JsonDeserializer<JsonObject>() {

                @Override
                public JsonObject deserialize(JsonParser jsonParser,
                                              DeserializationContext context)
                                                                              throws IOException,
                                                                              JsonProcessingException {

                    JsonObject jsonObject = getJsonObjectFactory().create();

                    JsonToken jsonToken = jsonParser.getCurrentToken();
                    if (jsonToken == JsonToken.START_OBJECT) {
                        jsonToken = jsonParser.nextToken();
                    } else {
                        throw new RuntimeException("Invalid json object");
                    }

                    while (jsonToken != null) {

                        if (jsonToken != JsonToken.FIELD_NAME) {
                            break;
                        }

                        String name = jsonParser.getCurrentName();
                        jsonToken = jsonParser.nextToken();

                        if (jsonToken == JsonToken.START_OBJECT) {
                            jsonObject.put(name, deserialize(jsonParser, context));

                        } else if (jsonToken == JsonToken.START_ARRAY) {
                            jsonObject.put(name, getJsonArrayDeserializer().deserialize(jsonParser, context));

                        } else if (jsonToken == JsonToken.VALUE_EMBEDDED_OBJECT) {
                            jsonObject.put(name, jsonParser.getEmbeddedObject());

                        } else if (jsonToken == JsonToken.VALUE_NULL) {
                            jsonObject.put(name, null);

                        } else if (jsonToken == JsonToken.VALUE_STRING) {
                            jsonObject.put(name, jsonParser.getText());

                        } else if (jsonToken == JsonToken.VALUE_TRUE) {
                            jsonObject.put(name, true);

                        } else if (jsonToken == JsonToken.VALUE_FALSE) {
                            jsonObject.put(name, false);

                        } else if (jsonToken == JsonToken.VALUE_NUMBER_INT ||
                                   jsonToken == JsonToken.VALUE_NUMBER_FLOAT) {
                            jsonObject.put(name, jsonParser.getNumberValue());

                        } else {
                            throw new RuntimeException("Unmanaged json token type : " + jsonToken);
                        }

                        jsonToken = jsonParser.nextToken();
                    }

                    return jsonObject;
                }
            };
        }

        return this.jsonObjectDeserializer;
    }

    protected JsonDeserializer<JsonArray> getJsonArrayDeserializer() {

        if (this.jsonArrayDeserializer == null) {
            this.jsonArrayDeserializer = new JsonDeserializer<JsonArray>() {

                @Override
                public JsonArray deserialize(JsonParser jsonParser,
                                             DeserializationContext context)
                                                                             throws IOException,
                                                                             JsonProcessingException {

                    JsonArray jsonArray = getJsonObjectFactory().createArray();

                    JsonToken jsonToken = jsonParser.getCurrentToken();
                    if (jsonToken == JsonToken.START_ARRAY) {
                        jsonToken = jsonParser.nextToken();
                    } else {
                        throw new RuntimeException("Invalid json array");
                    }

                    while (jsonToken != null) {

                        if (jsonToken == JsonToken.END_ARRAY) {
                            break;
                        }

                        if (jsonToken == JsonToken.START_OBJECT) {
                            jsonArray.add(getJsonObjectDeserializer().deserialize(jsonParser, context));

                        } else if (jsonToken == JsonToken.START_ARRAY) {
                            jsonArray.add(deserialize(jsonParser, context));

                        } else if (jsonToken == JsonToken.VALUE_EMBEDDED_OBJECT) {
                            jsonArray.add(jsonParser.getEmbeddedObject());

                        } else if (jsonToken == JsonToken.VALUE_NULL) {
                            jsonArray.add(null);

                        } else if (jsonToken == JsonToken.VALUE_STRING) {
                            jsonArray.add(jsonParser.getText());

                        } else if (jsonToken == JsonToken.VALUE_TRUE) {
                            jsonArray.add(true);

                        } else if (jsonToken == JsonToken.VALUE_FALSE) {
                            jsonArray.add(false);

                        } else if (jsonToken == JsonToken.VALUE_NUMBER_INT ||
                                   jsonToken == JsonToken.VALUE_NUMBER_FLOAT) {
                            jsonArray.add(jsonParser.getNumberValue());

                        } else {
                            throw new RuntimeException("Unmanaged json token type : " + jsonToken);
                        }

                        jsonToken = jsonParser.nextToken();
                    }
                    return jsonArray;
                }
            };
        }

        return this.jsonArrayDeserializer;
    }

    protected void registerCustomModules(ObjectMapper objectMapper) {
        registerJsonObjectModule(objectMapper);
        registerCustomTypeSerializerModule(objectMapper);
    }

    /**
     * Register our custom (de)serializers for JsonObject
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void registerJsonObjectModule(ObjectMapper objectMapper) {

        SimpleModule module = new SimpleModule();
        module.addSerializer(JsonObject.class, getJsonObjectSerializer());
        module.addDeserializer(JsonObject.class, getJsonObjectDeserializer());
        module.addSerializer(JsonArray.class, getJsonArraySerializer());
        module.addDeserializer(JsonArray.class, getJsonArrayDeserializer());

        if (getSpincastJsonManagerConfig().isSerializeEnumsToNameAndLabelObjects()) {
            module.addSerializer((Class)Enum.class, getEnumSerializer());
        }

        objectMapper.registerModule(module);
    }

    /**
     * Register our custom serializers for some types.
     */
    protected void registerCustomTypeSerializerModule(ObjectMapper objectMapper) {

        SimpleModule module = new SimpleModule();
        module.addSerializer(Date.class, getDateSerializer());
        module.addSerializer(Instant.class, getInstantSerializer());
        module.addSerializer(BigDecimal.class, getBigDecimalSerializer());
        objectMapper.registerModule(module);
    }

    @Override
    public String toJsonString(Object obj) {
        return toJsonString(obj, false);
    }

    @Override
    public String toJsonString(Object obj, boolean pretty) {
        try {
            if (pretty) {
                return getObjectMapper().writer(getJacksonPrettyPrinter()).writeValueAsString(obj);
            } else {
                return getObjectMapper().writeValueAsString(obj);
            }
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
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

    @Override
    public <T> T fromString(String jsonString, Class<T> clazz) {
        try {
            T jsonObj = getObjectMapper().readValue(jsonString, clazz);
            injectDependencies(jsonObj);
            return jsonObj;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public <T> T fromInputStream(InputStream inputStream, Class<T> clazz) {
        try {
            T jsonObj = getObjectMapper().readValue(inputStream, clazz);
            injectDependencies(jsonObj);
            return jsonObj;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public Map<String, Object> fromStringToMap(String jsonString) {
        try {
            Map<String, Object> map = getObjectMapper().readValue(jsonString, new TypeReference<Map<String, Object>>() {});
            return map;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public Map<String, Object> fromInputStreamToMap(InputStream inputStream) {
        try {
            Map<String, Object> map = getObjectMapper().readValue(inputStream,
                                                                  new TypeReference<Map<String, Object>>() {});
            return map;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public JsonObject create() {
        return getJsonObjectFactory().create();
    }

    @Override
    public JsonObject fromString(String jsonString) {
        try {
            JsonObject obj = getObjectMapper().readValue(jsonString, JsonObject.class);
            return obj;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public JsonObject fromMap(Map<String, ?> params) {
        return fromMap(params, false);
    }

    @Override
    public JsonObject fromMap(Map<String, ?> params, boolean parseKeysAsJsonPaths) {

        JsonObject root = getJsonObjectFactory().create();
        if (params == null || params.size() == 0) {
            return root;
        }

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
                root.putNoKeyParsing(key, value);
            }
        }

        return root;
    }

    protected int getMaxNumberOfKeysWhenConvertingMapToJsonObject() {
        return getSpincastConfig().getMaxNumberOfKeysWhenConvertingMapToJsonObject();
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

    @Override
    public JsonObject fromInputStream(InputStream inputStream) {
        try {
            JsonObject obj = getObjectMapper().readValue(inputStream, JsonObject.class);
            return obj;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public JsonObject fromFile(File jsonFile) {
        try {
            JsonObject obj = getObjectMapper().readValue(jsonFile, JsonObject.class);
            return obj;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public JsonObject fromFile(String jsonFilePath) {
        File file = new File(jsonFilePath);
        return fromFile(file);
    }

    @Override
    public JsonObject fromClasspathFile(String path) {
        String content = getSpincastUtils().readClasspathFile(path);
        return fromString(content);
    }

    @Override
    public JsonArray createArray() {
        return getJsonObjectFactory().createArray();
    }

    @Override
    public JsonArray fromStringArray(String jsonString) {
        try {
            JsonArray obj = getObjectMapper().readValue(jsonString, JsonArray.class);
            return obj;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public JsonArray fromListArray(List<?> elements) {
        JsonArray array = createArray();
        if (elements != null) {
            for (Object element : elements) {
                array.add(element);
            }
        }

        return array;
    }

    @Override
    public JsonArray fromInputStreamArray(InputStream inputStream) {
        try {
            JsonArray obj = getObjectMapper().readValue(inputStream, JsonArray.class);
            return obj;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public Form createForm(String formName) {
        return getFormFactory().createForm(formName, null);
    }

    @Override
    public String convertToJsonDate(Date date) {
        return SpincastStatics.getIso8601DateParserDefault().format(date);
    }

    /**
     * Currently support ISO 8601 encoded dates.
     */
    @Override
    public Date parseDateFromJson(String str) {
        return SpincastStatics.parseISO8601date(str);
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
                obj.putNoKeyParsing(String.valueOf(entry.getKey()),
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

    @Override
    public JsonObject cloneJsonObject(JsonObject jsonObject, boolean mutable) {
        return (JsonObject)clone(jsonObject, mutable);
    }

    @Override
    public JsonArray cloneJsonArray(JsonArray jsonArray, boolean mutable) {
        return (JsonArray)clone(jsonArray, mutable);
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
        obj.put("name", enumValue.name());
        obj.put("label", enumValue.toString());

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

}
