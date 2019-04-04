package org.spincast.plugins.gson;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.json.JsonPathUtils;
import org.spincast.core.json.JsonPathUtilsDefault;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.plugins.gson.serializers.BigDecimalSerializer;
import org.spincast.plugins.gson.serializers.ClassSerializer;
import org.spincast.plugins.gson.serializers.DateSerializer;
import org.spincast.plugins.gson.serializers.EnumSerializer;
import org.spincast.plugins.gson.serializers.InstantSerializer;
import org.spincast.plugins.gson.serializers.JsonArrayDeserializer;
import org.spincast.plugins.gson.serializers.JsonArraytSerializer;
import org.spincast.plugins.gson.serializers.JsonObjectDeserializer;
import org.spincast.plugins.gson.serializers.JsonObjectSerializer;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * Spincast Gson plugin module.
 */
public class SpincastGsonPluginModule extends SpincastGuiceModuleBase {

    public SpincastGsonPluginModule() {
        super();
    }

    public SpincastGsonPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                    Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
    }

    @Override
    protected void configure() {
        bind(JsonManager.class).to(getJsonManagerImpl()).in(Scopes.SINGLETON);
        bind(JsonPathUtils.class).to(getJsonPathUtilsImpl()).in(Scopes.SINGLETON);
        bind(new TypeLiteral<JsonDeserializer<JsonObject>>() {}).to(getJsonObjectDeserializerImpl()).in(Scopes.SINGLETON);
        bind(new TypeLiteral<JsonDeserializer<JsonArray>>() {}).to(getJsonArrayDeserializerImpl()).in(Scopes.SINGLETON);
        bind(new TypeLiteral<JsonSerializer<JsonObject>>() {}).to(getJsonObjectSerializerImpl()).in(Scopes.SINGLETON);
        bind(new TypeLiteral<JsonSerializer<JsonArray>>() {}).to(getJsonArraySerializerImpl()).in(Scopes.SINGLETON);
        bind(new TypeLiteral<JsonSerializer<Date>>() {}).to(getDateSerializerImpl()).in(Scopes.SINGLETON);
        bind(new TypeLiteral<JsonSerializer<Instant>>() {}).to(getInstantSerializerImpl()).in(Scopes.SINGLETON);
        bind(new TypeLiteral<JsonSerializer<BigDecimal>>() {}).to(getBigDecimalSerializerImpl()).in(Scopes.SINGLETON);
        bind(new TypeLiteral<JsonSerializer<Enum<?>>>() {}).to(getEnumSerializerImpl()).in(Scopes.SINGLETON);
        bind(new TypeLiteral<JsonSerializer<Class<?>>>() {}).to(getClassSerializerImpl()).in(Scopes.SINGLETON);

    }

    protected Class<? extends JsonManager> getJsonManagerImpl() {
        return SpincastGsonManager.class;
    }

    protected Class<? extends JsonPathUtils> getJsonPathUtilsImpl() {
        return JsonPathUtilsDefault.class;
    }

    protected Class<? extends JsonDeserializer<JsonObject>> getJsonObjectDeserializerImpl() {
        return JsonObjectDeserializer.class;
    }

    protected Class<? extends JsonDeserializer<JsonArray>> getJsonArrayDeserializerImpl() {
        return JsonArrayDeserializer.class;
    }

    protected Class<? extends JsonSerializer<JsonObject>> getJsonObjectSerializerImpl() {
        return JsonObjectSerializer.class;
    }

    protected Class<? extends JsonSerializer<JsonArray>> getJsonArraySerializerImpl() {
        return JsonArraytSerializer.class;
    }

    protected Class<? extends JsonSerializer<Date>> getDateSerializerImpl() {
        return DateSerializer.class;
    }

    protected Class<? extends JsonSerializer<Instant>> getInstantSerializerImpl() {
        return InstantSerializer.class;
    }

    protected Class<? extends JsonSerializer<BigDecimal>> getBigDecimalSerializerImpl() {
        return BigDecimalSerializer.class;
    }

    protected Class<? extends JsonSerializer<Enum<?>>> getEnumSerializerImpl() {
        return EnumSerializer.class;
    }

    protected Class<? extends JsonSerializer<Class<?>>> getClassSerializerImpl() {
        return ClassSerializer.class;
    }

}
