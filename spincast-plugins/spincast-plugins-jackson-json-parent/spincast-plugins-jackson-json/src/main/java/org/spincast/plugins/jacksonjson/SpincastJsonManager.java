package org.spincast.plugins.jacksonjson;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.spincast.core.json.IJsonArray;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.json.IJsonObjectAssistedFactory;
import org.spincast.core.utils.SpincastStatics;
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
 * Spincast default Json manager
 */
public class SpincastJsonManager implements IJsonManager {

    private final IJsonObjectAssistedFactory jsonObjectFactory;
    private final Provider<Injector> guiceProvider;

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
                               IJsonObjectAssistedFactory jsonObjectFactory) {
        this.guiceProvider = guiceProvider;
        this.jsonObjectFactory = jsonObjectFactory;
    }

    protected Injector getGuice() {
        return this.guiceProvider.get();
    }

    protected IJsonObjectAssistedFactory getJsonObjectFactory() {
        return this.jsonObjectFactory;
    }

    protected DefaultPrettyPrinter getJacksonPrettyPrinter() {
        if(this.jacksonPrettyPrinter == null) {
            this.jacksonPrettyPrinter = new DefaultPrettyPrinter();

            Indenter indenter = new DefaultIndenter(getJacksonPrettyPrinterIndentation(), DefaultIndenter.SYS_LF);

            this.jacksonPrettyPrinter.indentObjectsWith(indenter);
            this.jacksonPrettyPrinter.indentArraysWith(indenter);
        }
        return this.jacksonPrettyPrinter;
    }

    protected String getJacksonPrettyPrinterIndentation() {
        return "    ";
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
     * Create the ObjectMapper
     */
    protected ObjectMapper createObjectManager() {
        ObjectMapper objectMapper = new ObjectMapper();
        configureObjectMapper(objectMapper);
        return objectMapper;
    }

    /**
     * Configuration of the Jackson's ObjectMapper.
     */
    protected void configureObjectMapper(ObjectMapper objectMapper) {

        //==========================================
        // To allow serialization of "empty" POJOs (no properties to serialize)
        // (without this setting, an exception is thrown in those cases)
        //==========================================
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
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

                    //==========================================
                    // Serialize using the underlying map...
                    // Jackson automatically convert byte[] to a
                    // base64 encoded String.
                    //==========================================
                    gen.writeObject(jsonObject.getUnderlyingMap());
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

                    //==========================================
                    // Serialize using the underlying list...
                    //==========================================
                    gen.writeObject(jsonArray.getUnderlyingList());
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
                            jsonObject.put(name, jsonParser.getEmbeddedObject());

                        } else if(jsonToken == JsonToken.VALUE_NULL) {
                            jsonObject.put(name, null);

                        } else if(jsonToken == JsonToken.VALUE_STRING) {
                            jsonObject.put(name, jsonParser.getText());

                        } else if(jsonToken == JsonToken.VALUE_TRUE) {
                            jsonObject.put(name, true);

                        } else if(jsonToken == JsonToken.VALUE_FALSE) {
                            jsonObject.put(name, false);

                        } else if(jsonToken == JsonToken.VALUE_NUMBER_INT ||
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
                            jsonArray.add(jsonParser.getEmbeddedObject());

                        } else if(jsonToken == JsonToken.VALUE_NULL) {
                            jsonArray.add(null);

                        } else if(jsonToken == JsonToken.VALUE_STRING) {
                            jsonArray.add(jsonParser.getText());

                        } else if(jsonToken == JsonToken.VALUE_TRUE) {
                            jsonArray.add(true);

                        } else if(jsonToken == JsonToken.VALUE_FALSE) {
                            jsonArray.add(false);

                        } else if(jsonToken == JsonToken.VALUE_NUMBER_INT ||
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
    public IJsonObject create(InputStream inputStream) {
        try {
            IJsonObject obj = getObjectMapper().readValue(inputStream, IJsonObject.class);
            return obj;
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public IJsonArray createArray() {
        return getJsonObjectFactory().createArray();
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

}
