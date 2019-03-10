package org.spincast.plugins.jacksonxml.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.xml.XmlManager;
import org.spincast.plugins.jacksonxml.XmlMixinInfo;
import org.spincast.plugins.jacksonxml.XmlMixinInfoDefault;
import org.spincast.testing.defaults.NoAppTestingBase;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;

public class CustomSerializerMixinTest extends NoAppTestingBase {

    @Override
    protected Module getExtraOverridingModule() {

        return new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {

                //==========================================
                // Binds our mixin
                //==========================================
                Multibinder<XmlMixinInfo> xmlMixinsBinder = Multibinder.newSetBinder(binder(), XmlMixinInfo.class);
                xmlMixinsBinder.addBinding().toInstance(new XmlMixinInfoDefault(User.class, UserMixin.class));
            }
        };
    }

    @Inject
    XmlManager xmlManager;

    protected XmlManager getXmlManager() {
        return this.xmlManager;
    }

    /**
     * Custom XML serializer
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
     * Our User mixin
     */
    public static abstract class UserMixin implements User {

        //==========================================
        // Uses our custom serializer!
        //==========================================
        @Override
        @JsonSerialize(using = CustomSerializer.class)
        public abstract String getTitle();
    }

    @Test
    public void customSerializer() throws Exception {

        User user = new UserDefault();
        user.setName("Stromgol");
        user.setAge(123);
        user.setTitle("alien");

        String xml = getXmlManager().toXml(user);
        assertNotNull(xml);
        assertEquals("<UserDefault><name>Stromgol</name><age>123</age><title>bang!</title></UserDefault>", xml);
    }

}
