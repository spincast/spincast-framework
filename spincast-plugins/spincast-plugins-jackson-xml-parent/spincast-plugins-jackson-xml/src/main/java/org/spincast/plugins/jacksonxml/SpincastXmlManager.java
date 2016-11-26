package org.spincast.plugins.jacksonxml;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.xml.XmlManager;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.XmlPrettyPrinter;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

/**
 * Spincast Jackson XML manager
 */
public class SpincastXmlManager implements XmlManager {

    private final Provider<Injector> guiceProvider;
    private final JsonManager jsonManager;
    private final Set<XmlMixinInfo> xmlMixinInfos;
    private final XmlPrettyPrinter xmlPrettyPrinter;

    private XmlMapper xmlMapper;
    private XmlMapper xmlMapperPretty;
    private JsonSerializer<JsonObject> jsonObjectSerializer;
    private JsonDeserializer<JsonObject> jsonObjectDeserializer;
    private JsonDeserializer<JsonArray> jsonArrayDeserializer;
    private JsonSerializer<JsonArray> jsonArraySerializer;

    @Inject
    public SpincastXmlManager(Provider<Injector> guiceProvider,
                              JsonManager jsonManager,
                              @Nullable Set<XmlMixinInfo> xmlMixinInfos,
                              XmlPrettyPrinter xmlPrettyPrinter) {
        this.guiceProvider = guiceProvider;
        this.jsonManager = jsonManager;
        this.xmlMixinInfos = xmlMixinInfos;
        this.xmlPrettyPrinter = xmlPrettyPrinter;
    }

    protected Injector getGuice() {
        return this.guiceProvider.get();
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected Set<XmlMixinInfo> getXmlMixinInfos() {
        return this.xmlMixinInfos;
    }

    protected XmlPrettyPrinter getXmlPrettyPrinter() {
        return this.xmlPrettyPrinter;
    }

    protected XmlMapper getXmlMapper() {
        if(this.xmlMapper == null) {

            XmlMapper xmlMapper = createXmlMapper();
            registerCustomModules(xmlMapper);
            this.xmlMapper = xmlMapper;
        }
        return this.xmlMapper;
    }

    /**
     * Creates the XmlMapper
     */
    protected XmlMapper createXmlMapper() {

        XmlMapper xmlMapper = new XmlMapper();
        configureXmlMapper(xmlMapper);
        return xmlMapper;
    }

    /**
     * Configuration of the XmlMapper.
     */
    protected void configureXmlMapper(XmlMapper xmlMapper) {

        //==========================================
        // To allow serialization of "empty" POJOs (no properties to serialize)
        // (without this setting, an exception is thrown in those cases)
        //==========================================
        configureEmptyBeans(xmlMapper);

        //==========================================
        // Add the mixins, if any.
        //==========================================
        configureMixins(xmlMapper);
    }

    protected void configureEmptyBeans(XmlMapper xmlMapper) {
        xmlMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    protected void configureMixins(XmlMapper xmlMapper) {

        addSpincastMixins(xmlMapper);

        for(XmlMixinInfo xmlMixinInfo : getXmlMixinInfos()) {
            xmlMapper.addMixIn(xmlMixinInfo.getTargetClass(), xmlMixinInfo.getMixinClass());
        }
    }

    protected void addSpincastMixins(XmlMapper xmlMapper) {
        addJsonObjectMixin(xmlMapper);
        addJsonArrayMixin(xmlMapper);
    }

    @JsonRootName("JsonObject")
    protected static interface JsonObjectMixIn extends JsonObject {
        // nothing required
    }

    @JsonRootName("JsonArray")
    protected static interface JsonArrayMixIn extends JsonArray {
        // nothing required
    }

    /**
     * Specifies the root element name when serializing
     * a JsonObject
     */
    protected void addJsonObjectMixin(XmlMapper xmlMapper) {
        xmlMapper.addMixIn(JsonObject.class, JsonObjectMixIn.class);
    }

    /**
     * Specifies the root element name when serializing
     * a JsonArray
     */
    protected void addJsonArrayMixin(XmlMapper xmlMapper) {
        xmlMapper.addMixIn(JsonArray.class, JsonArrayMixIn.class);
    }

    protected XmlMapper getXmlMapperPretty() {
        if(this.xmlMapperPretty == null) {
            XmlMapper xmlMapper = getXmlMapper().copy();

            xmlMapper.setDefaultPrettyPrinter(getXmlPrettyPrinter());
            xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);

            registerCustomModules(xmlMapper);
            configureMixins(xmlMapper);

            this.xmlMapperPretty = xmlMapper;
        }
        return this.xmlMapperPretty;
    }

    /**
     * The name of the attribute set on a XML element to indicate it
     * comes from a JsonArray. This allows us to deserialize the XML
     * back to the correct structure.
     */
    protected String getArrayAttributeName() {
        return "isArray";
    }

    protected JsonSerializer<JsonObject> getJsonObjectSerializer() {

        if(this.jsonObjectSerializer == null) {
            this.jsonObjectSerializer = new JsonSerializer<JsonObject>() {

                @Override
                public void serialize(JsonObject jsonObject,
                                      JsonGenerator gen,
                                      SerializerProvider serializers)
                                                                      throws IOException, JsonProcessingException {

                    if(jsonObject == null) {
                        return;
                    }

                    ToXmlGenerator xmlGen = (ToXmlGenerator)gen;

                    xmlGen.writeStartObject();
                    for(Entry<String, Object> element : jsonObject) {
                        xmlGen.writeFieldName(element.getKey());
                        xmlGen.writeObject(element.getValue());
                    }
                    xmlGen.writeEndObject();
                }
            };
        }
        return this.jsonObjectSerializer;
    }

    protected JsonSerializer<JsonArray> getJsonArraySerializer() {

        if(this.jsonArraySerializer == null) {
            this.jsonArraySerializer = new JsonSerializer<JsonArray>() {

                @Override
                public void serialize(JsonArray jsonArray,
                                      JsonGenerator gen,
                                      SerializerProvider serializers)
                                                                      throws IOException, JsonProcessingException {

                    if(jsonArray == null) {
                        return;
                    }

                    ToXmlGenerator xmlGen = (ToXmlGenerator)gen;

                    xmlGen.writeStartObject();

                    //==========================================
                    // Write a isArray="true" attribute
                    //==========================================
                    xmlGen.setNextIsAttribute(true);
                    xmlGen.writeFieldName(getArrayAttributeName());
                    xmlGen.writeString("true");
                    xmlGen.setNextIsAttribute(false);

                    for(Object element : jsonArray) {

                        xmlGen.writeFieldName("element");

                        if(element == null) {
                            xmlGen.writeNull();
                        } else if(element instanceof JsonObject) {
                            xmlGen.writeStartObject();
                            xmlGen.writeFieldName("obj");
                            xmlGen.writeObject(element);
                            xmlGen.writeEndObject();
                        } else if(element instanceof JsonArray) {
                            xmlGen.writeStartObject();
                            xmlGen.writeFieldName("array");
                            xmlGen.writeObject(element);
                            xmlGen.writeEndObject();
                        } else {
                            xmlGen.writeObject(element);
                        }
                    }

                    xmlGen.writeEndObject();
                }
            };
        }
        return this.jsonArraySerializer;
    }

    protected JsonDeserializer<JsonObject> getJsonObjectDeserializer() {

        if(this.jsonObjectDeserializer == null) {
            this.jsonObjectDeserializer = new JsonDeserializer<JsonObject>() {

                @Override
                public JsonObject deserialize(JsonParser jsonParser,
                                              DeserializationContext context)
                                                                              throws IOException,
                                                                              JsonProcessingException {

                    FromXmlParser xmlParser = (FromXmlParser)jsonParser;

                    @SuppressWarnings("unused")
                    JsonToken token = xmlParser.nextToken();

                    JsonObject jsonObject = deserializeJsonObject(xmlParser, context, null);
                    return jsonObject;
                }
            };
        }

        return this.jsonObjectDeserializer;
    }

    protected JsonDeserializer<JsonArray> getJsonArrayDeserializer() {

        if(this.jsonArrayDeserializer == null) {
            this.jsonArrayDeserializer = new JsonDeserializer<JsonArray>() {

                @Override
                public JsonArray deserialize(JsonParser jsonParser,
                                             DeserializationContext context)
                                                                             throws IOException,
                                                                             JsonProcessingException {

                    FromXmlParser xmlParser = (FromXmlParser)jsonParser;

                    //==========================================
                    // We allow an array at the root position without
                    // a "isArray="true"".
                    //==========================================
                    boolean firstElementSkipped = false;
                    JsonToken token = xmlParser.nextToken();
                    if(token == JsonToken.FIELD_NAME) {

                        String fieldName = xmlParser.getValueAsString();
                        token = xmlParser.nextToken();
                        firstElementSkipped = true;

                        //==========================================
                        // We skip the "isArray" attribute, if present
                        //==========================================
                        if(fieldName != null && fieldName.equals(getArrayAttributeName())) {
                            if(token == JsonToken.VALUE_STRING &&
                               "true".equalsIgnoreCase(xmlParser.getValueAsString())) {
                                token = xmlParser.nextToken();
                                firstElementSkipped = false;
                            }
                        }
                    }

                    // true : first "<element>" already skipped.
                    return deserializeJsonArray(xmlParser, context, firstElementSkipped);

                }
            };
        }

        return this.jsonArrayDeserializer;
    }

    protected Object deserializeObjectOrArray(FromXmlParser xmlParser, DeserializationContext context) {

        try {

            JsonToken token = xmlParser.getCurrentToken();

            if(token == JsonToken.VALUE_NULL) {
                token = xmlParser.nextToken();
                return getJsonManager().create();
            }

            if(token != JsonToken.START_OBJECT) {
                throw new RuntimeException("Expecting a START_OBJECT token here, got : " + token);
            }

            token = xmlParser.nextToken();
            if(token != JsonToken.FIELD_NAME) {
                throw new RuntimeException("Expecting a FIELD_NAME token here, got : " + token);
            }
            String fieldName = xmlParser.getValueAsString();

            token = xmlParser.nextToken();

            //==========================================
            // Empty object
            //==========================================
            if(token == JsonToken.END_OBJECT || token == JsonToken.VALUE_NULL) {
                return getJsonManager().create();
            }

            //==========================================
            // We have to check if the attribute telling us
            // it's an array is present.
            //==========================================
            if(fieldName != null && fieldName.equals(getArrayAttributeName())) {
                if(token == JsonToken.VALUE_STRING &&
                   "true".equalsIgnoreCase(xmlParser.getValueAsString())) {
                    token = xmlParser.nextToken();
                    return deserializeJsonArray(xmlParser, context);
                }
            }

            //==========================================
            // Since we already started parsing the object to look 
            // for a potential attribute, we have to parse this
            // first property to the next method.
            //==========================================
            Object firstValue = null;
            if(token == JsonToken.START_OBJECT) {
                firstValue = deserializeObjectOrArray(xmlParser, context);
            } else {
                firstValue = xmlParser.readValueAs(Object.class);
            }

            token = xmlParser.nextToken();
            return deserializeJsonObject(xmlParser,
                                         context,
                                         new SimpleEntry<String, Object>(fieldName, firstValue));

        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected JsonArray deserializeJsonArray(FromXmlParser xmlParser, DeserializationContext context) {
        return deserializeJsonArray(xmlParser, context, false);
    }

    protected JsonArray deserializeJsonArray(FromXmlParser xmlParser,
                                             DeserializationContext context,
                                             boolean firstElementSkipped) {

        try {

            JsonArray jsonArray = getJsonManager().createArray();

            JsonToken token = xmlParser.getCurrentToken();
            while(token == JsonToken.FIELD_NAME || firstElementSkipped) {

                //==========================================
                // Skip the "<element>" part, except for a
                // root array for which the element has already
                // being skipped.
                //==========================================
                if(!firstElementSkipped) {
                    @SuppressWarnings("unused")
                    String tokenName = xmlParser.getValueAsString();
                    token = xmlParser.nextToken();
                } else {
                    firstElementSkipped = false;
                }

                //==========================================
                // The element is an Object or another array.
                //==========================================
                if(token == JsonToken.START_OBJECT) {

                    //==========================================
                    // The fieldName of the array element.
                    // This will be used in case the element is something
                    // like : <someKey>titi</someKey>. In that case, the
                    // element will be a JSonObject of one property "someKey".
                    // If the element is a complex object, for example
                    // <someKey><color>red</color><size>big</size></someKey>,
                    // then this "someKey" fieldName won't be used.
                    //==========================================
                    token = xmlParser.nextToken();
                    if(token != JsonToken.FIELD_NAME) {
                        throw new RuntimeException("Expecting a FIELD_NAME token here, got : " + token);
                    }
                    String fieldName = xmlParser.getValueAsString();

                    token = xmlParser.nextToken();
                    if(token == JsonToken.VALUE_NULL) {
                        //==========================================
                        // Empty object
                        //==========================================
                        jsonArray.add(getJsonManager().create());
                    } else if(token == JsonToken.START_OBJECT) {
                        //==========================================
                        // The array element is a complexe object such as
                        // <someKey><color>red</color><size>big</size></someKey>
                        // or is an array in the array.
                        //==========================================
                        jsonArray.add(deserializeObjectOrArray(xmlParser, context));
                    } else {

                        //==========================================
                        // The array element is a something like <someKey>titi</someKey>.
                        // We considere this as a JsonObject with one property.
                        //==========================================
                        JsonObject jsonObject = getJsonManager().create();
                        Object value = xmlParser.readValueAs(Object.class);
                        jsonObject.put(fieldName, value);

                        jsonArray.add(jsonObject);
                    }

                    token = xmlParser.nextToken();

                    //==========================================
                    // An array element can only contain a simple value
                    // or a single object/array. It can't contain multiple
                    // children.
                    //==========================================
                    if(token != JsonToken.END_OBJECT) {
                        throw new RuntimeException("An array element can't contain more than one child! The current array already contains " +
                                                   "one : " + jsonArray.toJsonString());
                    }

                } else {
                    //==========================================
                    // The array element is a simple value
                    //==========================================
                    Object value = xmlParser.readValueAs(Object.class);
                    jsonArray.add(value);
                }

                token = xmlParser.nextToken();
            }

            return jsonArray;
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected JsonObject deserializeJsonObject(FromXmlParser xmlParser,
                                               DeserializationContext context,
                                               Entry<String, Object> firstProperty) {

        try {

            JsonObject jsonObject = getJsonManager().create();

            if(firstProperty != null) {
                jsonObject.put(firstProperty.getKey(), firstProperty.getValue());
            }

            JsonToken token = xmlParser.getCurrentToken();
            while(token == JsonToken.FIELD_NAME) {

                String fieldName = xmlParser.getValueAsString();
                if(StringUtils.isBlank(fieldName)) {
                    throw new RuntimeException("A Json object can't have a property with an empty name. " +
                                               "For example, this is invalid : <root>someValue</root>, a " +
                                               "name must be specified : <root><someKey>someValue</someKey></root>");
                }

                token = xmlParser.nextToken();

                if(token == JsonToken.VALUE_NULL) {
                    jsonObject.put(fieldName, getJsonManager().create());
                } else if(token == JsonToken.START_OBJECT) {
                    jsonObject.put(fieldName, deserializeObjectOrArray(xmlParser, context));
                } else {
                    Object value = xmlParser.readValueAs(Object.class);
                    jsonObject.put(fieldName, value);
                }
                token = xmlParser.nextToken();
            }
            return jsonObject;
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected void registerCustomModules(XmlMapper objectMapper) {
        registerJsonObjectModule(objectMapper);
    }

    /**
     * Register our custom (de)serializers for JsonObject
     */
    protected void registerJsonObjectModule(XmlMapper objectMapper) {

        SimpleModule module = new SimpleModule();
        module.addSerializer(JsonObject.class, getJsonObjectSerializer());
        module.addDeserializer(JsonObject.class, getJsonObjectDeserializer());
        module.addSerializer(JsonArray.class, getJsonArraySerializer());
        module.addDeserializer(JsonArray.class, getJsonArrayDeserializer());
        objectMapper.registerModule(module);
    }

    @Override
    public String toXml(Object obj) {
        return toXml(obj, false);
    }

    @Override
    public String toXml(Object obj, boolean pretty) {
        try {
            if(pretty) {
                return getXmlMapperPretty().writeValueAsString(obj);
            } else {
                return getXmlMapper().writeValueAsString(obj);
            }
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public JsonObject fromXml(String xml) {
        return fromXmlToType(xml, JsonObject.class);
    }

    @Override
    public JsonArray fromXmlToJsonArray(String xml) {
        return fromXmlToType(xml, JsonArray.class);
    }

    @Override
    public <T> T fromXml(String xml, Class<T> clazz) {
        return fromXmlToType(xml, clazz);
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
    public <T> T fromXmlToType(String xml, Type type) {

        if(xml == null) {
            return null;
        }
        if(type == null) {
            return null;
        }

        try {
            JavaType javaType = TypeFactory.defaultInstance().constructType(type);
            T obj = getXmlMapper().readValue(xml, javaType);
            injectDependencies(obj);
            return obj;
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public <T> T fromXmlInputStream(InputStream inputStream, Class<T> clazz) {

        Objects.requireNonNull(clazz, "clazz can't be NULL");

        if(inputStream == null) {
            return null;
        }

        try {
            JavaType javaType = TypeFactory.defaultInstance().constructType(clazz);
            T obj = getXmlMapper().readValue(inputStream, javaType);
            injectDependencies(obj);
            return obj;
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

}
