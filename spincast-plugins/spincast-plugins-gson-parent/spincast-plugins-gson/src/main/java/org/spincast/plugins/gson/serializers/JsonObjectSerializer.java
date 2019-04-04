package org.spincast.plugins.gson.serializers;

import java.lang.reflect.Type;
import java.util.Map.Entry;

import org.spincast.core.json.JsonObject;
import org.spincast.plugins.gson.SpincastGsonManager;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class JsonObjectSerializer implements JsonSerializer<JsonObject> {

    private final Provider<SpincastGsonManager> spincastGsonManagerProvider;

    @Inject
    public JsonObjectSerializer(Provider<SpincastGsonManager> spincastGsonManagerProvider) {
        this.spincastGsonManagerProvider = spincastGsonManagerProvider;
    }

    protected SpincastGsonManager getSpincastGsonManager() {
        return this.spincastGsonManagerProvider.get();
    }

    @Override
    public JsonElement serialize(JsonObject jsonObject,
                                 Type typeOfSrc,
                                 JsonSerializationContext context) {

        if (jsonObject == null) {
            return null;
        }

        com.google.gson.JsonObject gsonObject = new com.google.gson.JsonObject();

        for (Entry<String, Object> entry : jsonObject) {
            String key = entry.getKey();
            Object el = entry.getValue();

            JsonElement jsonElement = getSpincastGsonManager().convertJsonObjectElementToGsonJsonElement(el);
            gsonObject.add(key, jsonElement);
        }

        return gsonObject;
    }

}
