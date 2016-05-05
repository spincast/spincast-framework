package org.spincast.plugins.jacksonjson.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;
import org.spincast.core.json.IJsonManager;
import org.spincast.defaults.tests.DefaultTestingBase;
import org.spincast.defaults.tests.DefaultTestingModule;
import org.spincast.plugins.jacksonjson.IJsonMixinInfo;
import org.spincast.plugins.jacksonjson.JsonMixinInfo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;

public class CustomDeserializerMixinTest extends DefaultTestingBase {

    @Inject
    IJsonManager jsonManager;

    protected IJsonManager getJsonManager() {
        return this.jsonManager;
    }

    /**
     * Custom Json deserializer
     */
    protected static class CustomDeserializer extends JsonDeserializer<String> {

        @Override
        public String deserialize(JsonParser p,
                                  DeserializationContext ctxt) throws IOException, JsonProcessingException {

            return "bang!";
        }
    }

    /**
     * Our IUser mixin
     */
    public static abstract class IUserMixin implements IUser {

        //==========================================
        // Uses our custom deserializer!
        //==========================================
        @Override
        @JsonDeserialize(using = CustomDeserializer.class)
        public abstract String getTitle();
    }

    @Override
    public Module getTestingModule() {
        return new DefaultTestingModule() {

            @Override
            protected void configure() {
                super.configure();
                bindJsonMixins();
            }

            //==========================================
            // Bind our mixin
            //==========================================
            protected void bindJsonMixins() {

                Multibinder<IJsonMixinInfo> jsonMixinsBinder = Multibinder.newSetBinder(binder(), IJsonMixinInfo.class);
                jsonMixinsBinder.addBinding().toInstance(new JsonMixinInfo(IUser.class, IUserMixin.class));
            }
        };
    }

    @Test
    public void customDeserializer() throws Exception {

        String jsonString = "{\"name\":\"Stromgol\",\"age\":123,\"title\":\"some title\"}";

        IUser user = getJsonManager().fromJsonString(jsonString, User.class);
        assertNotNull(jsonString);
        assertEquals("Stromgol", user.getName());
        assertEquals(123, user.getAge());
        assertEquals("Title is: bang!", user.getTitle());

    }

}
