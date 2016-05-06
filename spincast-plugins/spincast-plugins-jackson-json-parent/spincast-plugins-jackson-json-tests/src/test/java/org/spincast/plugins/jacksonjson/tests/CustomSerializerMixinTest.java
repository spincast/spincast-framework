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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;

public class CustomSerializerMixinTest extends DefaultTestingBase {

    @Inject
    IJsonManager jsonManager;

    protected IJsonManager getJsonManager() {
        return this.jsonManager;
    }

    /**
     * Custom Json serializer
     */
    protected static class CustomSerializer extends JsonSerializer<String> {

        @Override
        public void serialize(String value,
                              JsonGenerator gen,
                              SerializerProvider serializers) throws IOException, JsonProcessingException {
            gen.writeString("bang!");
        }
    }

    /**
     * Our IUser mixin
     */
    public static abstract class IUserMixin implements IUser {

        //==========================================
        // Uses our custom serializer!
        //==========================================
        @Override
        @JsonSerialize(using = CustomSerializer.class)
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
    public void customSerializer() throws Exception {

        IUser user = new User();
        user.setName("Stromgol");
        user.setAge(123);
        user.setTitle("alien");

        String jsonString = getJsonManager().toJsonString(user);
        assertNotNull(jsonString);
        assertEquals("{\"name\":\"Stromgol\",\"age\":123,\"title\":\"bang!\"}", jsonString);
    }

}