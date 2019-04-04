package org.spincast.plugins.gson.serializers;

import java.lang.reflect.Type;
import java.math.BigDecimal;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class BigDecimalSerializer implements JsonSerializer<BigDecimal> {

    @Override
    public JsonElement serialize(BigDecimal bigDecimal,
                                 Type typeOfSrc,
                                 JsonSerializationContext context) {
        if (bigDecimal == null) {
            return null;
        }

        return new JsonPrimitive(bigDecimal.toPlainString());
    }
}
