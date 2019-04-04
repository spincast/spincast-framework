package org.spincast.plugins.gson.serializers;

import java.lang.reflect.Type;
import java.time.Instant;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class InstantSerializer implements JsonSerializer<Instant> {

    @Override
    public JsonElement serialize(Instant instant,
                                 Type typeOfSrc,
                                 JsonSerializationContext context) {
        if (instant == null) {
            return null;
        }

        return new JsonPrimitive(instant.toString());
    }
}
