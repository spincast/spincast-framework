package org.spincast.plugins.gson.serializers;

import java.lang.reflect.Type;

import org.spincast.core.json.JsonObject;
import org.spincast.core.json.JsonObjectFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.inject.Inject;

public class EnumSerializer implements JsonSerializer<Enum<?>> {

    public static final String ENUM_SERIALIZER_FIELD_NAME_NAME = "name";
    public static final String ENUM_SERIALIZER_FIELD_NAME_LABEL = "label";

    private final JsonObjectFactory jsonObjectFactory;
    private final JsonObjectSerializer jsonObjectSerializer;

    @Inject
    public EnumSerializer(JsonObjectFactory jsonObjectFactory, JsonObjectSerializer jsonObjectSerializer) {
        this.jsonObjectFactory = jsonObjectFactory;
        this.jsonObjectSerializer = jsonObjectSerializer;
    }

    protected JsonObjectFactory getJsonObjectFactory() {
        return this.jsonObjectFactory;
    }

    protected JsonObjectSerializer getJsonObjectSerializer() {
        return this.jsonObjectSerializer;
    }

    @Override
    public JsonElement serialize(Enum<?> enumValue,
                                 Type typeOfSrc,
                                 JsonSerializationContext context) {
        if (enumValue == null) {
            return null;
        }

        JsonObject jsonObject = getJsonObjectFactory().create();
        jsonObject.set(ENUM_SERIALIZER_FIELD_NAME_NAME, enumValue.name());
        jsonObject.set(ENUM_SERIALIZER_FIELD_NAME_LABEL, enumValue.toString());

        return getJsonObjectSerializer().serialize(jsonObject, JsonObject.class, context);
    }
}
