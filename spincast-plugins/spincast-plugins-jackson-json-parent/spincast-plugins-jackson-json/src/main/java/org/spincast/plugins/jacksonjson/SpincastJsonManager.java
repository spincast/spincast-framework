package org.spincast.plugins.jacksonjson;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;

import javax.annotation.Nullable;

import org.spincast.core.json.IJsonArray;
import org.spincast.core.json.IJsonArrayImmutable;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.json.IJsonObjectFactory;
import org.spincast.core.json.IJsonObjectImmutable;
import org.spincast.core.json.exceptions.CantConvertException;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.commons.lang3.time.FastDateFormat;

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
public class SpincastJsonManager implements IJsonManager {

    private final IJsonObjectFactory jsonObjectFactory;
    private final Provider<Injector> guiceProvider;
    private final Set<IJsonMixinInfo> jsonMixinInfos;
    private final ISpincastJsonManagerConfig spincastJsonManagerConfig;

    private ObjectMapper objectMapper;
    private JsonSerializer<IJsonObject> jsonObjectSerializer;
    private JsonDeserializer<IJsonObject> jsonObjectDeserializer;
    private JsonSerializer<IJsonArray> jsonArraySerializer;
    private JsonDeserializer<IJsonArray> jsonArrayDeserializer;
    private JsonSerializer<Date> dateSerializer;
    private DefaultPrettyPrinter jacksonPrettyPrinter;

    // All thread safe!
    private static FastDateFormat iso8601DateParserDefault;
    private static FastDateFormat iso8601DateParser1;
    private static FastDateFormat iso8601DateParser2;
    private static FastDateFormat iso8601DateParser3;

    static {
        iso8601DateParserDefault = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mmZ", TimeZone.getTimeZone("UTC"));
        iso8601DateParser1 = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ssX", TimeZone.getTimeZone("UTC"));
        iso8601DateParser2 = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSZ", TimeZone.getTimeZone("UTC"));
        iso8601DateParser3 = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", TimeZone.getTimeZone("UTC"));
    }

    @Inject
    public SpincastJsonManager(Provider<Injector> guiceProvider,
                               IJsonObjectFactory jsonObjectFactory,
                               @Nullable Set<IJsonMixinInfo> jsonMixinInfos,
                               ISpincastJsonManagerConfig spincastJsonManagerConfig) {
        this.guiceProvider = guiceProvider;
        this.jsonObjectFactory = jsonObjectFactory;

        if(jsonMixinInfos == null) {
            jsonMixinInfos = new HashSet<IJsonMixinInfo>();
        }
        this.jsonMixinInfos = jsonMixinInfos;
        this.spincastJsonManagerConfig = spincastJsonManagerConfig;
    }

    protected Injector getGuice() {
        return this.guiceProvider.get();
    }

    protected IJsonObjectFactory getJsonObjectFactory() {
        return this.jsonObjectFactory;
    }

    protected Set<IJsonMixinInfo> getJsonMixinInfos() {
        return this.jsonMixinInfos;
    }

    protected ISpincastJsonManagerConfig getSpincastJsonManagerConfig() {
        return this.spincastJsonManagerConfig;
    }

    protected DefaultPrettyPrinter getJacksonPrettyPrinter() {
        if(this.jacksonPrettyPrinter == null) {
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

    protected FastDateFormat getIso8601DateParserDefault() {
        return iso8601DateParserDefault;
    }

    protected FastDateFormat getIso8601DateParser1() {
        return iso8601DateParser1;
    }

    protected FastDateFormat getIso8601DateParser2() {
        return iso8601DateParser2;
    }

    protected FastDateFormat getIso8601DateParser3() {
        return iso8601DateParser3;
    }

    protected ObjectMapper getObjectMapper() {
        if(this.objectMapper == null) {

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

    protected void configureEmptyBeans(ObjectMapper objectMapper) {
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    protected void configureMixins(ObjectMapper objectMapper) {
        for(IJsonMixinInfo jsonMixinInfo : getJsonMixinInfos()) {
            objectMapper.addMixIn(jsonMixinInfo.getTargetClass(), jsonMixinInfo.getMixinClass());
        }
    }

    protected JsonSerializer<IJsonObject> getJsonObjectSerializer() {

        if(this.jsonObjectSerializer == null) {
            this.jsonObjectSerializer = new JsonSerializer<IJsonObject>() {

                @Override
                public void serialize(IJsonObject jsonObject,
                                      JsonGenerator gen,
                                      SerializerProvider serializers)
                                                                      throws IOException, JsonProcessingException {

                    if(jsonObject == null) {
                        return;
                    }

                    Map<String, Object> asMap = jsonObject.convertToPlainMap();
                    gen.writeObject(asMap);
                }
            };
        }
        return this.jsonObjectSerializer;
    }

    protected JsonSerializer<IJsonArray> getJsonArraySerializer() {

        if(this.jsonArraySerializer == null) {
            this.jsonArraySerializer = new JsonSerializer<IJsonArray>() {

                @Override
                public void serialize(IJsonArray jsonArray,
                                      JsonGenerator gen,
                                      SerializerProvider serializers)
                                                                      throws IOException, JsonProcessingException {

                    if(jsonArray == null) {
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

        if(this.dateSerializer == null) {
            this.dateSerializer = new JsonSerializer<Date>() {

                @Override
                public void serialize(Date jsonObject,
                                      JsonGenerator gen,
                                      SerializerProvider serializers)
                                                                      throws IOException, JsonProcessingException {

                    if(jsonObject == null) {
                        return;
                    }

                    String dateStr = getIso8601DateParserDefault().format(jsonObject);
                    gen.writeObject(dateStr);
                }
            };
        }
        return this.dateSerializer;
    }

    protected JsonDeserializer<IJsonObject> getJsonObjectDeserializer() {

        if(this.jsonObjectDeserializer == null) {
            this.jsonObjectDeserializer = new JsonDeserializer<IJsonObject>() {

                @Override
                public IJsonObject deserialize(JsonParser jsonParser,
                                               DeserializationContext context)
                                                                               throws IOException,
                                                                               JsonProcessingException {

                    IJsonObject jsonObject = getJsonObjectFactory().create();

                    JsonToken jsonToken = jsonParser.getCurrentToken();
                    if(jsonToken == JsonToken.START_OBJECT) {
                        jsonToken = jsonParser.nextToken();
                    }

                    while(jsonToken != null) {

                        if(jsonToken != JsonToken.FIELD_NAME) {
                            break;
                        }
                        String name = jsonParser.getCurrentName();
                        jsonToken = jsonParser.nextToken();

                        if(jsonToken == JsonToken.START_OBJECT) {
                            jsonObject.put(name, deserialize(jsonParser, context));

                        } else if(jsonToken == JsonToken.START_ARRAY) {
                            jsonObject.put(name, getJsonArrayDeserializer().deserialize(jsonParser, context));

                        } else if(jsonToken == JsonToken.VALUE_EMBEDDED_OBJECT) {
                            jsonObject.putConvert(name, jsonParser.getEmbeddedObject());

                        } else if(jsonToken == JsonToken.VALUE_NULL) {
                            jsonObject.putConvert(name, null);

                        } else if(jsonToken == JsonToken.VALUE_STRING) {
                            jsonObject.put(name, jsonParser.getText());

                        } else if(jsonToken == JsonToken.VALUE_TRUE) {
                            jsonObject.put(name, true);

                        } else if(jsonToken == JsonToken.VALUE_FALSE) {
                            jsonObject.put(name, false);

                        } else if(jsonToken == JsonToken.VALUE_NUMBER_INT ||
                                  jsonToken == JsonToken.VALUE_NUMBER_FLOAT) {
                            jsonObject.putConvert(name, jsonParser.getNumberValue());

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

    protected JsonDeserializer<IJsonArray> getJsonArrayDeserializer() {

        if(this.jsonArrayDeserializer == null) {
            this.jsonArrayDeserializer = new JsonDeserializer<IJsonArray>() {

                @Override
                public IJsonArray deserialize(JsonParser jsonParser,
                                              DeserializationContext context)
                                                                              throws IOException,
                                                                              JsonProcessingException {

                    IJsonArray jsonArray = getJsonObjectFactory().createArray();

                    JsonToken jsonToken = jsonParser.getCurrentToken();
                    if(jsonToken == JsonToken.START_ARRAY) {
                        jsonToken = jsonParser.nextToken();
                    }

                    while(jsonToken != null) {

                        if(jsonToken == JsonToken.END_ARRAY) {
                            break;
                        }

                        if(jsonToken == JsonToken.START_OBJECT) {
                            jsonArray.add(getJsonObjectDeserializer().deserialize(jsonParser, context));

                        } else if(jsonToken == JsonToken.START_ARRAY) {
                            jsonArray.add(deserialize(jsonParser, context));

                        } else if(jsonToken == JsonToken.VALUE_EMBEDDED_OBJECT) {
                            jsonArray.addConvert(jsonParser.getEmbeddedObject());

                        } else if(jsonToken == JsonToken.VALUE_NULL) {
                            jsonArray.addConvert(null);

                        } else if(jsonToken == JsonToken.VALUE_STRING) {
                            jsonArray.add(jsonParser.getText());

                        } else if(jsonToken == JsonToken.VALUE_TRUE) {
                            jsonArray.add(true);

                        } else if(jsonToken == JsonToken.VALUE_FALSE) {
                            jsonArray.add(false);

                        } else if(jsonToken == JsonToken.VALUE_NUMBER_INT ||
                                  jsonToken == JsonToken.VALUE_NUMBER_FLOAT) {
                            jsonArray.addConvert(jsonParser.getNumberValue());

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
        registerIJsonObjectModule(objectMapper);
        registerDateModule(objectMapper);
    }

    /**
     * Register our custom (de)serializers for IJsonObject
     */
    protected void registerIJsonObjectModule(ObjectMapper objectMapper) {

        SimpleModule module = new SimpleModule();
        module.addSerializer(IJsonObject.class, getJsonObjectSerializer());
        module.addDeserializer(IJsonObject.class, getJsonObjectDeserializer());
        module.addSerializer(IJsonArray.class, getJsonArraySerializer());
        module.addDeserializer(IJsonArray.class, getJsonArrayDeserializer());
        objectMapper.registerModule(module);
    }

    /**
     * Register our custom serializers for dates.
     */
    protected void registerDateModule(ObjectMapper objectMapper) {

        SimpleModule module = new SimpleModule();
        module.addSerializer(Date.class, getDateSerializer());
        objectMapper.registerModule(module);
    }

    @Override
    public String toJsonString(Object obj) {
        return toJsonString(obj, false);
    }

    @Override
    public String toJsonString(Object obj, boolean pretty) {
        try {
            if(pretty) {
                return getObjectMapper().writer(getJacksonPrettyPrinter()).writeValueAsString(obj);
            } else {
                return getObjectMapper().writeValueAsString(obj);
            }
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    /**
     * Once the deserialization of an Object is done,
     * we inject dependencies using Guice.
     */
    protected void injectDependencies(Object obj) {
        if(obj != null) {
            getGuice().injectMembers(obj);
        }
    }

    @Override
    public <T> T fromJsonString(String jsonString, Class<T> clazz) {
        try {
            T jsonObj = getObjectMapper().readValue(jsonString, clazz);
            injectDependencies(jsonObj);
            return jsonObj;
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public <T> T fromJsonInputStream(InputStream inputStream, Class<T> clazz) {
        try {
            T jsonObj = getObjectMapper().readValue(inputStream, clazz);
            injectDependencies(jsonObj);
            return jsonObj;
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public Map<String, Object> fromJsonStringToMap(String jsonString) {
        try {
            Map<String, Object> map = getObjectMapper().readValue(jsonString, new TypeReference<Map<String, Object>>() {});
            return map;
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public Map<String, Object> fromJsonInputStreamToMap(InputStream inputStream) {
        try {
            Map<String, Object> map = getObjectMapper().readValue(inputStream,
                                                                  new TypeReference<Map<String, Object>>() {});
            return map;
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public IJsonObject create() {
        return getJsonObjectFactory().create();
    }

    @Override
    public IJsonObject create(String jsonString) {
        try {
            IJsonObject obj = getObjectMapper().readValue(jsonString, IJsonObject.class);
            return obj;
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public IJsonObject create(Map<String, ?> params) {
        return create(params, false);
    }

    @Override
    public IJsonObject create(Map<String, ?> params, boolean parseKeyAsFieldPath) {

        if(!parseKeyAsFieldPath) {
            return getJsonObjectFactory().create(params);
        }

        //==========================================
        // Parse keys as FieldPaths
        //==========================================
        IJsonObject root = getJsonObjectFactory().create();
        if(params == null || params.size() == 0) {
            return root;
        }

        if(params.size() > getMaxNumberOfFieldPathKeys()) {
            throw new RuntimeException("Too many keys to parse : " + params.size() + " as FieldPaths. " +
                                       "The maximum is currently set to " + getMaxNumberOfFieldPathKeys());
        }

        for(Entry<String, ?> entry : params.entrySet()) {

            String fieldPath = entry.getKey();
            Object value = entry.getValue();

            parseFieldPathAndMerge(root, fieldPath, value);
        }

        return root;

    }

    protected void parseFieldPathAndMerge(IJsonObject root,
                                          String fieldPath,
                                          Object value) {

        Objects.requireNonNull(root, "The root object can't be NULL");
        if(fieldPath == null) {
            return;
        }

        if(fieldPath.length() > getFieldPathKeyMaxLength()) {
            throw new RuntimeException("A FieldPath is too long to be parsed. This FieldPath starts with : " +
                                       fieldPath.substring(0, Math.min(30, (fieldPath.length() - 1))));
        }

        //==========================================
        // Initial validations
        //==========================================
        if(fieldPath.startsWith(".") || fieldPath.endsWith(".")) {
            throw new RuntimeException("FieldPath parsing error on character '.'. The FieldPath can't start or end with the " +
                                       "'.' character. FieldPath : " + fieldPath);
        }
        if(fieldPath.endsWith("[")) {
            throw new RuntimeException("FieldPath parsing error on character '['. The FieldPath can't end with the " +
                                       "'[' character. FieldPath : " + fieldPath);
        }
        if(fieldPath.startsWith("]")) {
            throw new RuntimeException("FieldPath parsing error on character '['. The FieldPath can't start with the " +
                                       "']' character. FieldPath : " + fieldPath);
        }

        //==========================================
        // We parse the FieldPath, one character at the time.
        //==========================================
        IJsonObject currentObj = root;
        IJsonArray currentArray = null;
        int currentArrayIndex = 0;
        StringBuilder tokenBuilder = new StringBuilder();
        boolean isInBrackets = false;
        boolean isInQuotes = false;
        boolean isInDoubleQuotes = false;
        char ch = '\0';
        char previousChar = '\0';
        for(int i = 0; i < fieldPath.length(); i++) {

            previousChar = ch;
            ch = fieldPath.charAt(i);

            //==========================================
            // The '.' character
            //==========================================
            if(ch == '.') {

                //==========================================
                // In quotes, this character has no
                // special meaning.
                //==========================================
                if(isInQuotes || isInDoubleQuotes) {
                    tokenBuilder.append(ch);
                    continue;
                }

                if(isInBrackets) {
                    throw new RuntimeException("FieldPath parsing error on character '.'. A dot is not valid inside brackets but " +
                                               "outside quotes. FieldPath : " + fieldPath);
                }

                if(currentObj != null) {

                    //==========================================
                    // Empty token : invalid name for an object
                    //==========================================
                    String token = tokenBuilder.toString();
                    if("".equals(token)) {
                        throw new RuntimeException("FieldPath parsing error on character '.'. A token is empty. FieldPath : " +
                                                   fieldPath);
                    }
                    tokenBuilder = new StringBuilder();

                    //==========================================
                    // Creates a new object on the current object, 
                    // if this object doesn't already exist.
                    //==========================================
                    IJsonObject obj;
                    try {
                        obj = currentObj.getJsonObject(token);
                    } catch(CantConvertException ex) {
                        throw new RuntimeException("FieldPath parsing error on character '.'. " +
                                                   "The key '" + token + "' already exists, but the associated value is " +
                                                   "not of type IJsonObject as expected here. FieldPath : " +
                                                   fieldPath);
                    }

                    if(obj == null) {
                        obj = create();
                        currentObj.put(token, obj);
                    }

                    //==========================================
                    // This object is now our current object.
                    //==========================================
                    currentObj = obj;
                    currentArray = null;

                } else {

                    //==========================================
                    // Creates a new object on the current array, 
                    // if this object doesn't already exist.
                    //==========================================
                    IJsonObject obj;
                    try {
                        obj = currentArray.getJsonObject(currentArrayIndex);
                    } catch(CantConvertException ex) {
                        throw new RuntimeException("FieldPath parsing error on character '.'. " +
                                                   "The element of the array at index '" + currentArrayIndex +
                                                   "' already exists, but is " +
                                                   "not of type IJsonObject as expected here. FieldPath : " +
                                                   fieldPath);
                    }

                    if(obj == null) {
                        obj = create();
                        currentArray.set(currentArrayIndex, obj);
                    }

                    currentObj = obj;
                    currentArray = null;
                }

            //==========================================@formatter:off 
            // The '[' character
            //==========================================@formatter:on
            } else if(ch == '[') {

                //==========================================
                // In quotes, this character has no
                // special meaning.
                //==========================================
                if(isInQuotes || isInDoubleQuotes) {
                    tokenBuilder.append(ch);
                    continue;
                }

                if(isInBrackets) {
                    throw new RuntimeException("FieldPath parsing error on character '['.The '[' is not valid inside " +
                                               "already started brackets. FieldPath : " + fieldPath);
                }

                String token = tokenBuilder.toString();
                tokenBuilder = new StringBuilder();

                //==========================================
                // If what's inside the bracket is a String key, then
                // we create an object.
                // If it's an integer index, we create an array.
                //==========================================
                char nextChar = fieldPath.charAt(i + 1);
                if(nextChar == '"' || nextChar == '\'') {

                    IJsonObject obj = null;
                    if(currentObj != null) {

                        //==========================================
                        // If the FieldPath starts with a
                        // quoted key (for example : "['some key']" ), then 
                        // we do not create a new object since it already exists
                        // (its the root object).
                        //
                        // Also, we only create a new object if it doesn't
                        // already exist.
                        //==========================================
                        if(i != 0) {
                            try {
                                obj = currentObj.getJsonObject(token);
                            } catch(CantConvertException ex) {
                                throw new RuntimeException("FieldPath parsing error on character '['. " +
                                                           "The key '" + token +
                                                           "' already exists, but the associated value is " +
                                                           "not of type IJsonObject, as expected here. FieldPath : " +
                                                           fieldPath);
                            }

                            if(obj == null) {
                                obj = create();
                                currentObj.put(token, obj);
                            }
                            currentObj = obj;
                            currentArray = null;
                        }

                    } else {

                        //==========================================
                        // Creates a new object on the current array, 
                        // if this object doesn't already exist.
                        //==========================================
                        try {
                            obj = currentArray.getJsonObject(currentArrayIndex);
                        } catch(CantConvertException ex) {
                            throw new RuntimeException("FieldPath parsing error on character '['. " +
                                                       "The index '" + currentArrayIndex +
                                                       "' points to an already existing element, but " +
                                                       "not of type IJsonObject. FieldPath : " +
                                                       fieldPath);
                        }

                        if(obj == null) {
                            obj = create();
                            currentArray.set(currentArrayIndex, obj);
                        }

                        currentObj = obj;
                        currentArray = null;
                    }

                //==========================================@formatter:off 
                // We are dealing with an integer index,
                // we have to create an array if requried.
                //==========================================@formatter:on
                } else {

                    IJsonArray array;
                    if(currentObj != null) {

                        //==========================================
                        // The root object is not an array so the
                        // FieldPath Key can't start with something
                        // like "[0]"
                        //==========================================
                        if(i == 0) {
                            throw new RuntimeException("FieldPath parsing error on character '['. The root object is not " +
                                                       "an array. FieldPath : " +
                                                       fieldPath);
                        }

                        //==========================================
                        // The index can't be empty
                        //==========================================
                        if("".equals(token)) {
                            throw new RuntimeException("FieldPath parsing error on character '['. A token is empty . FieldPath : " +
                                                       fieldPath);
                        }

                        //==========================================
                        // Creates a new array on the current object, 
                        // if this array doesn't already exist.
                        //==========================================
                        try {
                            array = currentObj.getJsonArray(token);
                        } catch(CantConvertException ex) {
                            throw new RuntimeException("FieldPath parsing error on character '['. " +
                                                       "The key '" + token +
                                                       "' points to an already existing element, but " +
                                                       "not of type IJsonArray, as expected here. " +
                                                       "FieldPath : " + fieldPath);
                        }

                        if(array == null) {
                            array = createArray();
                        }

                        currentObj.put(token, array);

                    } else {

                        //==========================================
                        // Creates a new array on the current array, 
                        // if this array doesn't already exist.
                        //==========================================
                        try {
                            array = currentArray.getJsonArray(currentArrayIndex);
                        } catch(CantConvertException ex) {
                            throw new RuntimeException("FieldPath parsing error on character '['. " +
                                                       "The index '" + currentArrayIndex +
                                                       "' points to an already existing element, but " +
                                                       "not of type IJsonArray. FieldPath : " +
                                                       fieldPath);
                        }

                        if(array == null) {
                            array = createArray();
                            currentArray.set(currentArrayIndex, array);
                        }
                    }

                    currentObj = null;
                    currentArray = array;
                }

                isInBrackets = true;

            //==========================================@formatter:off 
            // The ']' character
            //==========================================@formatter:on
            } else if(ch == ']') {

                //==========================================
                // In quotes, this  character has no
                // special meaning.
                //==========================================
                if(isInQuotes || isInDoubleQuotes) {
                    tokenBuilder.append(ch);
                    continue;
                }

                if(!isInBrackets) {
                    throw new RuntimeException("FieldPath parsing error on character ']'. No start bracket found. FieldPath : " +
                                               fieldPath);
                }

                //==========================================
                // Next char must be ".", "[" or the end 
                // of the FieldPath.
                //==========================================
                if(i < (fieldPath.length() - 1)) {
                    char nextChar = fieldPath.charAt(i + 1);
                    if(nextChar != '.' && nextChar != '[') {
                        throw new RuntimeException("FieldPath parsing error on character ']'. The character following a ']', if any, must " +
                                                   "be '.' or '['. Here, the following character is '" + nextChar +
                                                   "'. FieldPath : " + fieldPath);
                    }
                }

                //==========================================
                // Empty token : invalid name for an index/key
                //==========================================
                String token = tokenBuilder.toString();
                if("".equals(token)) {
                    throw new RuntimeException("FieldPath parsing error on character ']'. A key or an index was expected. FieldPath : " +
                                               fieldPath);
                }

                //==========================================
                // What's inside the brackets is a key
                //==========================================
                if((token.startsWith("\"") && token.endsWith("\"")) || (token.startsWith("'") && token.endsWith("'"))) {
                    String key = token.substring(1, token.length() - 1);
                    if("".equals(key)) {
                        throw new RuntimeException("FieldPath parsing error on character ']'. A key can't be empty. FieldPath : " +
                                                   fieldPath);
                    }
                    //==========================================
                    // This becomes our token :
                    //==========================================
                    tokenBuilder = new StringBuilder(key);

                //==========================================@formatter:off 
                // What's inside the beckets is an index
                //==========================================@formatter:on
                } else {

                    Integer index = null;
                    try {
                        index = Integer.parseInt(token);
                    } catch(NumberFormatException ex) {
                        throw new RuntimeException("FieldPath parsing error on character ']'. The index '" + token +
                                                   "' is not a valid integer. " +
                                                   "You have to use quotes or double-quotes for an object key, or a valid integer for an array index. FieldPath : " +
                                                   fieldPath);
                    }

                    if(currentArray == null) {
                        throw new RuntimeException("FieldPath parsing error on character ']'. Expecting a non-null array here. FieldPath : " +
                                                   fieldPath);
                    }

                    //==========================================
                    // We keep the current array index.
                    //==========================================
                    currentArrayIndex = index;

                    //==========================================
                    // Reset the token builder
                    //==========================================
                    tokenBuilder = new StringBuilder();
                }

                isInBrackets = false;

            //==========================================@formatter:off 
            // The '"' character
            //==========================================@formatter:on
            } else if(ch == '"') {

                //==========================================
                // In single quotes, when not inside brackets, or
                // when preceding by a "\", the '"' character has no
                // special meaning.
                //==========================================
                if(isInBrackets && !isInQuotes && previousChar != '\\') {

                    //==========================================
                    // Otherwise, it starts or ends a key.
                    //==========================================
                    isInDoubleQuotes = !isInDoubleQuotes;
                }

                tokenBuilder.append(ch);

            //==========================================@formatter:off 
            // The "'" character
            //==========================================@formatter:on
            } else if(ch == '\'') {

                //==========================================
                // In double quotes, when not inside brackets, or
                // when preceding by a "\", the "'" character has no
                // special meaning.
                //==========================================
                if(isInBrackets && !isInDoubleQuotes && previousChar != '\\') {

                    //==========================================
                    // Otherwise, it starts or ends a key.
                    //==========================================
                    isInQuotes = !isInQuotes;
                }
                tokenBuilder.append(ch);

            //========================================== @formatter:off 
            // Any other character!
            //========================================== @formatter:on
            } else {

                if(isInQuotes || isInDoubleQuotes) {

                    //==========================================
                    // Only the special characters and "\" can be
                    // escaped by a "\".
                    //==========================================
                    if(previousChar == '\\') {

                        if(ch != '\\') {
                            throw new RuntimeException("FieldPath parsing error on the '" + ch + "' character. " +
                                                       "This character can't be escaped. If you want to use a '\'  inside a " +
                                                       "name, you need to escape it : \"\\\\\". FieldPath : " +
                                                       fieldPath);
                        } else {
                            tokenBuilder.append(ch);
                            ch = '\0';
                            continue;
                        }
                    }

                    //==========================================
                    // We do not add the '\' character, except if it's
                    // doubled (it's already added above!).
                    //==========================================
                    if(ch == '\\') {
                        continue;
                    }

                } else if(isInBrackets) {

                    if(!(ch >= '0' && ch <= '9')) {
                        throw new RuntimeException("FieldPath parsing error on the '" + ch + "' character. " +
                                                   "Invalid character in the index, expecting a digit, got " +
                                                   "'" + ch + "'. FieldPath : " +
                                                   fieldPath);
                    }

                } else {

                    //==========================================
                    // Validates the char to be used a an object 
                    // property name without quotes.
                    //==========================================
                    if(ch == '.' || ch == '[' || ch == ']') {
                        throw new RuntimeException("The characters '.', '[' and ']' are not valid inside a object " +
                                                   "name or a property name, if this " +
                                                   "name is not in quotes. In FieldPath : " + fieldPath);
                    }
                }

                tokenBuilder.append(ch);
            }
        }

        //==========================================
        // We reached the end of the FieldPath...
        //==========================================

        if(isInQuotes) {
            throw new RuntimeException("FieldPath parsing error on the last character. Some brackets were not " +
                                       "closed properly. FieldPath : " + fieldPath);
        }

        String token = tokenBuilder.toString();

        if(currentObj != null) {

            //==========================================
            // The last key to add already exists on
            // the object.
            //==========================================
            if(currentObj.isKeyExists(token)) {
                throw new RuntimeException("FieldPath parsing error on the last character. The key '" + token + "' " +
                                           "already exists. FieldPath : " +
                                           fieldPath);
            }

            currentObj.putConvert(token, value, true);

        } else if(currentArray != null) {

            //==========================================
            // The last index to with the value has to be
            // added is already used on the array.
            //==========================================
            String currentElementStr = currentArray.getString(currentArrayIndex);
            if(currentElementStr != null) {
                throw new RuntimeException("FieldPath parsing error on the last character. The array " +
                                           "already contains a value at index '" + currentArrayIndex + "'. FieldPath : " +
                                           fieldPath);
            }

            currentArray.setConvert(currentArrayIndex, value, true);

        } else {
            throw new RuntimeException("Not supposed. Invalid parsing of FieldPath : " + fieldPath);
        }
    }

    protected int getMaxNumberOfFieldPathKeys() {
        return getSpincastJsonManagerConfig().getMaxNumberOfFieldPathKeys();
    }

    protected int getFieldPathKeyMaxLength() {
        return getSpincastJsonManagerConfig().getFieldPathKeyMaxLength();
    }

    @Override
    public IJsonObject create(InputStream inputStream) {
        try {
            IJsonObject obj = getObjectMapper().readValue(inputStream, IJsonObject.class);
            return obj;
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public IJsonObjectImmutable createImmutable(IJsonObject jsonObject) {

        Map<String, Object> map = new HashMap<String, Object>();

        if(jsonObject != null) {
            for(Entry<String, Object> entry : jsonObject) {
                Object elementClone = clone(entry.getValue(), false);
                map.put(entry.getKey(), elementClone);
            }
        }
        map = Collections.unmodifiableMap(map);

        return getJsonObjectFactory().createImmutable(map);
    }

    @Override
    public IJsonArray createArray() {
        return getJsonObjectFactory().createArray();
    }

    @Override
    public IJsonArray createArray(String jsonString) {
        try {
            IJsonArray obj = getObjectMapper().readValue(jsonString, IJsonArray.class);
            return obj;
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public IJsonArray createArray(List<?> elements) {
        IJsonArray array = createArray();
        if(elements != null) {
            for(Object element : elements) {
                array.addConvert(element);
            }
        }

        return array;
    }

    @Override
    public IJsonArray createArray(InputStream inputStream) {
        try {
            IJsonArray obj = getObjectMapper().readValue(inputStream, IJsonArray.class);
            return obj;
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public IJsonArrayImmutable createArrayImmutable(IJsonArray jsonArray) {

        List<Object> elements = new ArrayList<Object>();

        if(jsonArray != null) {
            for(Object element : jsonArray) {
                Object elementClone = clone(element, false);
                elements.add(elementClone);
            }
        }
        elements = Collections.unmodifiableList(elements);

        return getJsonObjectFactory().createArrayImmutable(elements);
    }

    /**
     * Currently support ISO 8601 encoded dates.
     */
    @Override
    public Date parseDateFromJson(String str) {

        if(str == null) {
            return null;
        }

        Date date = null;
        try {
            date = getIso8601DateParserDefault().parse(str);
            return date;
        } catch(Exception ex) {
        }

        try {
            date = getIso8601DateParser1().parse(str);
            return date;
        } catch(Exception ex) {
        }

        try {
            date = getIso8601DateParser2().parse(str);
            return date;
        } catch(Exception ex) {
        }

        try {
            date = getIso8601DateParser3().parse(str);
            return date;
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public IJsonObject cloneJsonObject(IJsonObject jsonObject) {
        return cloneJsonObject(jsonObject, true);
    }

    @Override
    public IJsonObject cloneJsonObject(IJsonObject jsonObject, boolean mutable) {
        return (IJsonObject)clone(jsonObject, mutable);
    }

    @Override
    public IJsonArray cloneJsonArray(IJsonArray jsonArray) {
        return cloneJsonArray(jsonArray, true);
    }

    @Override
    public IJsonArray cloneJsonArray(IJsonArray jsonArray, boolean mutable) {
        return (IJsonArray)clone(jsonArray, mutable);
    }

    @Override
    public Object convertToNativeType(Object originalObject) {

        if(originalObject == null) {
            return null;
        }

        //==========================================
        // Already a native type
        //==========================================
        if((originalObject instanceof String) ||
           (originalObject instanceof Integer) ||
           (originalObject instanceof Long) ||
           (originalObject instanceof Float) ||
           (originalObject instanceof Double) ||
           (originalObject instanceof Boolean) ||
           (originalObject instanceof BigDecimal) ||
           (originalObject instanceof byte[]) ||
           (originalObject instanceof Date) ||
           (originalObject instanceof IJsonObject) ||
           (originalObject instanceof IJsonArray)) {

            return originalObject;
        }

        //==========================================
        // Array or Collection
        //==========================================
        if(originalObject instanceof Collection<?>) {

            IJsonArray array = createArray();
            for(Object element : (Collection<?>)originalObject) {
                array.addConvert(element);
            }
            return array;

        } else if(originalObject instanceof Object[]) {

            IJsonArray array = createArray();
            for(Object element : (Object[])originalObject) {
                array.addConvert(element);
            }
            return array;

        } else if(originalObject instanceof Map) {

            IJsonObject obj = create();

            Map<?, ?> map = (Map<?, ?>)originalObject;
            for(Entry<?, ?> entry : map.entrySet()) {
                if(entry.getKey() == null) {
                    throw new RuntimeException("Cannot convert a Map to a IJsonObject when a key is NULL.");
                }
                obj.putConvert(String.valueOf(entry.getKey()),
                               entry.getValue());
            }

            return obj;
        }

        //==========================================
        // Converts to a IJsonObject
        //==========================================
        String jsonStr = toJsonString(originalObject);
        IJsonObject jsonObject = create(jsonStr);
        return jsonObject;
    }

    @Override
    public Object clone(Object originalObject) {
        return clone(originalObject, true);
    }

    @Override
    public Object clone(Object originalObject, boolean mutable) {

        if(originalObject == null) {
            return null;
        }

        if((originalObject instanceof String) ||
           (originalObject instanceof Integer) ||
           (originalObject instanceof Long) ||
           (originalObject instanceof Float) ||
           (originalObject instanceof Double) ||
           (originalObject instanceof Boolean) ||
           (originalObject instanceof BigDecimal) ||
           (originalObject instanceof byte[]) ||
           (originalObject instanceof Date)) {
            return originalObject;

        } else if(originalObject instanceof IJsonObject) {

            if(!mutable) {

                //==========================================
                // Already immutable!
                //==========================================
                if(originalObject instanceof IJsonObjectImmutable) {
                    return originalObject;
                }

                return createImmutable((IJsonObject)originalObject);
            }

            IJsonObject clone = create();
            for(Entry<String, Object> entry : (IJsonObject)originalObject) {
                clone.putConvert(entry.getKey(), clone(entry.getValue(), true));
            }

            return clone;

        } else if(originalObject instanceof IJsonArray) {

            if(!mutable) {

                //==========================================
                // Already immutable!
                //==========================================
                if(originalObject instanceof IJsonArrayImmutable) {
                    return originalObject;
                }

                return createArrayImmutable((IJsonArray)originalObject);
            }

            IJsonArray clone = createArray();
            for(Object element : (IJsonArray)originalObject) {
                clone.addConvert(clone(element));
            }
            return clone;

        } else {
            return convertToNativeType(originalObject);
        }
    }

}
