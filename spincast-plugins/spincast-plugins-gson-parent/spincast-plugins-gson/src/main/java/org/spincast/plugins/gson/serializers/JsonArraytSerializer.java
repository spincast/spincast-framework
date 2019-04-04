package org.spincast.plugins.gson.serializers;

import java.lang.reflect.Type;

import org.spincast.core.json.JsonArray;
import org.spincast.plugins.gson.SpincastGsonManager;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class JsonArraytSerializer implements JsonSerializer<JsonArray> {

    private final Provider<SpincastGsonManager> spincastGsonManagerProvider;

    @Inject
    public JsonArraytSerializer(Provider<SpincastGsonManager> spincastGsonManagerProvider) {
        this.spincastGsonManagerProvider = spincastGsonManagerProvider;
    }

    protected SpincastGsonManager getSpincastGsonManager() {
        return this.spincastGsonManagerProvider.get();
    }

    @Override
    public JsonElement serialize(JsonArray jsonArray,
                                 Type typeOfSrc,
                                 JsonSerializationContext context) {

        if (jsonArray == null) {
            return null;
        }

        com.google.gson.JsonArray gsonArray = new com.google.gson.JsonArray();

        for (Object el : jsonArray) {
            JsonElement jsonElement = getSpincastGsonManager().convertJsonObjectElementToGsonJsonElement(el);
            gsonArray.add(jsonElement);
        }

        return gsonArray;
    }

}
