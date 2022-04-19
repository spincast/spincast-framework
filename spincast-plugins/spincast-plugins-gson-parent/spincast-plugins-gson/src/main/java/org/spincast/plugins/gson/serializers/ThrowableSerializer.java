package org.spincast.plugins.gson.serializers;

import java.lang.reflect.Type;

import org.spincast.core.json.JsonObject;
import org.spincast.core.json.JsonObjectFactory;
import org.spincast.core.utils.SpincastStatics;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.inject.Inject;

public class ThrowableSerializer implements JsonSerializer<Throwable> {

    private final JsonObjectFactory jsonObjectFactory;
    private final JsonObjectSerializer jsonObjectSerializer;

    @Inject
    public ThrowableSerializer(JsonObjectFactory jsonObjectFactory,
                               JsonObjectSerializer jsonObjectSerializer) {
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
    public JsonElement serialize(Throwable ex,
                                 Type typeOfSrc,
                                 JsonSerializationContext context) {
        if (ex == null) {
            return null;
        }
        JsonObject jsonObject = toJsonObject(ex);
        return getJsonObjectSerializer().serialize(jsonObject, JsonObject.class, context);
    }

    protected JsonObject toJsonObject(Throwable ex) {
        JsonObject jsonObject = getJsonObjectFactory().create();
        jsonObject.set("name", ex.getClass().getName());
        jsonObject.set("message", ex.getMessage());
        jsonObject.set("stacktrace", SpincastStatics.getStackTrace(ex));

        return jsonObject;
    }

}
