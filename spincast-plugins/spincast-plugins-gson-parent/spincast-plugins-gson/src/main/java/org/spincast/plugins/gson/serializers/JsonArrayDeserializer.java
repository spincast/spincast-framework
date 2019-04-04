package org.spincast.plugins.gson.serializers;

import java.lang.reflect.Type;

import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonObjectFactory;
import org.spincast.plugins.gson.SpincastGsonManager;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class JsonArrayDeserializer implements JsonDeserializer<JsonArray> {

    private final Provider<SpincastGsonManager> spincastGsonManagerProvider;
    private final JsonObjectFactory jsonObjectFactory;

    @Inject
    public JsonArrayDeserializer(JsonObjectFactory jsonObjectFactory,
                                 Provider<SpincastGsonManager> spincastGsonManagerProvider) {
        this.jsonObjectFactory = jsonObjectFactory;
        this.spincastGsonManagerProvider = spincastGsonManagerProvider;
    }

    protected JsonObjectFactory getJsonObjectFactory() {
        return this.jsonObjectFactory;
    }

    protected SpincastGsonManager getSpincastGsonManager() {
        return this.spincastGsonManagerProvider.get();
    }

    @Override
    public JsonArray deserialize(JsonElement jsonElement,
                                 Type typeOfT,
                                 JsonDeserializationContext context) throws JsonParseException {

        com.google.gson.JsonArray gsonArray = jsonElement.getAsJsonArray();
        if (gsonArray == null) {
            return null;
        }

        JsonArray jsonArray = getJsonObjectFactory().createArray();

        for (JsonElement element : gsonArray) {
            Object converted = getSpincastGsonManager().convertToNativeType(element);
            jsonArray.add(converted);
        }

        return jsonArray;

    }

}
