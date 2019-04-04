package org.spincast.plugins.gson.serializers;

import java.lang.reflect.Type;
import java.util.Date;

import org.spincast.core.utils.SpincastStatics;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DateSerializer implements JsonSerializer<Date> {

    @Override
    public JsonElement serialize(Date date,
                                 Type typeOfSrc,
                                 JsonSerializationContext context) {
        if (date == null) {
            return null;
        }

        String dateStr = SpincastStatics.getIso8601DateParserDefault().format(date);
        return new JsonPrimitive(dateStr);
    }

}
