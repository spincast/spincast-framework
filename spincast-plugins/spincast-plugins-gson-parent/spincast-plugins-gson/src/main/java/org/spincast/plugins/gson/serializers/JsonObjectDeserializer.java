package org.spincast.plugins.gson.serializers;

import java.lang.reflect.Type;
import java.util.Map.Entry;

import org.spincast.core.json.JsonObject;
import org.spincast.core.json.JsonObjectFactory;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.inject.Inject;

public class JsonObjectDeserializer implements JsonDeserializer<JsonObject> {

    private final JsonObjectFactory jsonObjectFactory;

    @Inject
    public JsonObjectDeserializer(JsonObjectFactory jsonObjectFactory) {
        this.jsonObjectFactory = jsonObjectFactory;
    }

    protected JsonObjectFactory getJsonObjectFactory() {
        return this.jsonObjectFactory;
    }

    @Override
    public JsonObject deserialize(JsonElement jsonElement,
                                  Type typeOfT,
                                  JsonDeserializationContext context) throws JsonParseException {


        com.google.gson.JsonObject gsonObj = jsonElement.getAsJsonObject();
        if (gsonObj == null) {
            return null;
        }

        JsonObject jsonObject = getJsonObjectFactory().create();

        for (Entry<String, JsonElement> entry : gsonObj.entrySet()) {
            String key = entry.getKey();
            JsonElement gsonSubObj = entry.getValue();

            jsonObject.set(key, gsonSubObj);
        }

        return jsonObject;

    }

}
