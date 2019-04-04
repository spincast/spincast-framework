package org.spincast.plugins.gson.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ClassSerializer implements JsonSerializer<Class<?>> {

    @Override
    public JsonElement serialize(Class<?> clazz,
                                 Type typeOfSrc,
                                 JsonSerializationContext context) {
        if (clazz == null) {
            return null;
        }

        return new JsonPrimitive(clazz.getName());
    }
}
