package org.spincast.plugins.jacksonxml;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

import org.spincast.core.json.IJsonArray;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.xml.IXmlManager;

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
public class SpincastXmlManager implements IXmlManager {

    private final Provider<Injector> guiceProvider;
    private final IJsonManager jsonManager;
    private final Set<IXmlMixinInfo> xmlMixinInfos;
    private final XmlPrettyPrinter xmlPrettyPrinter;

    private XmlMapper xmlMapper;
    private XmlMapper xmlMapperPretty;
    private JsonSerializer<IJsonObject> jsonObjectSerializer;
    private JsonDeserializer<IJsonObject> jsonObjectDeserializer;
    private JsonDeserializer<IJsonArray> jsonArrayDeserializer;
    private JsonSerializer<IJsonArray> jsonArraySerializer;

    @Inject
    public SpincastXmlManager(Provider<Injector> guiceProvider,
                              IJsonManager jsonManager,
                              @Nullable Set<IXmlMixinInfo> xmlMixinInfos,
                              XmlPrettyPrinter xmlPrettyPrinter) {
        this.guiceProvider = guiceProvider;
        this.jsonManager = jsonManager;
        this.xmlMixinInfos = xmlMixinInfos;
        this.xmlPrettyPrinter = xmlPrettyPrinter;
    }

    protected Injector getGuice() {
        return this.guiceProvider.get();
    }

    protected IJsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected Set<IXmlMixinInfo> getXmlMixinInfos() {
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
        for(IXmlMixinInfo xmlMixinInfo : getXmlMixinInfos()) {
            xmlMapper.addMixIn(xmlMixinInfo.getTargetClass(), xmlMixinInfo.getMixinClass());
        }
    }

    protected XmlMapper getXmlMapperPretty() {
        if(this.xmlMapperPretty == null) {
            XmlMapper xmlMapper = getXmlMapper().copy();

            xmlMapper.setDefaultPrettyPrinter(getXmlPrettyPrinter());
            xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);

            registerCustomModules(xmlMapper);

            this.xmlMapperPretty = xmlMapper;
        }
        return this.xmlMapperPretty;
    }

    /**
     * The name of the attribute set on a XML element to indicate it
     * comes from a IJsonArray. This allows us to deserialize the XML
     * back to the correct structure.
     */
    protected String getArrayAttributeName() {
        return "isArray";
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
                        xmlGen.writeObject(element);
                    }
                    xmlGen.writeEndObject();
                }
            };
        }
        return this.jsonArraySerializer;
    }

    protected JsonDeserializer<IJsonObject> getJsonObjectDeserializer() {

        if(this.jsonObjectDeserializer == null) {
            this.jsonObjectDeserializer = new JsonDeserializer<IJsonObject>() {

                @Override
                public IJsonObject deserialize(JsonParser jsonParser,
                                               DeserializationContext context)
                                                                               throws IOException,
                                                                               JsonProcessingException {

                    FromXmlParser xmlParser = (FromXmlParser)jsonParser;

                    @SuppressWarnings("unused")
                    JsonToken token = xmlParser.nextToken();

                    IJsonObject jsonObject = deserializeJsonObject(xmlParser, context, null);
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

    protected IJsonArray deserializeJsonArray(FromXmlParser xmlParser, DeserializationContext context) {
        return deserializeJsonArray(xmlParser, context, false);
    }

    protected IJsonArray deserializeJsonArray(FromXmlParser xmlParser,
                                              DeserializationContext context,
                                              boolean firstElementSkipped) {

        try {

            IJsonArray jsonArray = getJsonManager().createArray();

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

                if(token == JsonToken.START_OBJECT) {
                    token = xmlParser.nextToken(); // Skip the field name in an array!

                    if(token != JsonToken.FIELD_NAME) {
                        throw new RuntimeException("Expecting a FIELD_NAME token here, got : " + token);
                    }
                    String fieldName = xmlParser.getValueAsString();

                    token = xmlParser.nextToken();

                    if(token == JsonToken.VALUE_NULL) {
                        jsonArray.add(getJsonManager().create());
                        token = xmlParser.nextToken();
                    } else if(token == JsonToken.START_OBJECT) {
                        jsonArray.add(deserializeObjectOrArray(xmlParser, context));
                    } else {

                        //==========================================
                        // Direct value like <someObj>titi</someObj>
                        //==========================================
                        IJsonObject jsonObject = getJsonManager().create();
                        Object value = xmlParser.readValueAs(Object.class);
                        jsonObject.put(fieldName, value);

                        jsonArray.add(jsonObject);
                        token = xmlParser.nextToken();
                    }
                } else {
                    Object value = xmlParser.readValueAs(Object.class);
                    jsonArray.add(value);
                }

                token = xmlParser.nextToken();
            }
            token = xmlParser.nextToken();

            return jsonArray;
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected IJsonObject deserializeJsonObject(FromXmlParser xmlParser,
                                                DeserializationContext context,
                                                Entry<String, Object> firstProperty) {

        try {

            IJsonObject jsonObject = getJsonManager().create();

            if(firstProperty != null) {
                jsonObject.put(firstProperty.getKey(), firstProperty.getValue());
            }

            JsonToken token = xmlParser.getCurrentToken();
            while(token == JsonToken.FIELD_NAME) {

                String fieldName = xmlParser.getValueAsString();
                token = xmlParser.nextToken();

                if(token == JsonToken.VALUE_NULL) {
                    jsonObject.put(fieldName, getJsonManager().create());
                } else if(token == JsonToken.START_OBJECT) {
                    jsonObject.put(fieldName, deserializeObjectOrArray(xmlParser, context));
                } else {
                    Object value = xmlParser.readValueAs(Object.class);
                    jsonObject.put(fieldName, value);
                    token = xmlParser.nextToken();
                }
                token = xmlParser.getCurrentToken();
            }
            token = xmlParser.nextToken();
            return jsonObject;
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected void registerCustomModules(XmlMapper objectMapper) {
        registerIJsonObjectModule(objectMapper);
    }

    /**
     * Register our custom (de)serializers for IJsonObject
     */
    protected void registerIJsonObjectModule(XmlMapper objectMapper) {

        SimpleModule module = new SimpleModule();
        module.addSerializer(IJsonObject.class, getJsonObjectSerializer());
        module.addDeserializer(IJsonObject.class, getJsonObjectDeserializer());
        module.addSerializer(IJsonArray.class, getJsonArraySerializer());
        module.addDeserializer(IJsonArray.class, getJsonArrayDeserializer());
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
    public IJsonObject fromXml(String xml) {
        return fromXmlToType(xml, IJsonObject.class);
    }

    @Override
    public IJsonArray fromXmlToJsonArray(String xml) {
        return fromXmlToType(xml, IJsonArray.class);
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
