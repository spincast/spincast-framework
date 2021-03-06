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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;

public class CustomDeserializerMixinTest extends NoAppTestingBase {

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
     * Our User mixin
     */
    public static abstract class UserMixin implements User {

        //==========================================
        // Uses our custom deserializer!
        //==========================================
        @Override
        @JsonDeserialize(using = CustomDeserializer.class)
        public abstract String getTitle();
    }

    @Test
    public void customDeserializer() throws Exception {

        String xml = "<User><name>Stromgol</name><age>123</age><title>alien</title></User>";

        User user = getXmlManager().fromXml(xml, UserDefault.class);
        assertNotNull(user);
        assertEquals("Stromgol", user.getName());
        assertEquals(123, user.getAge());
        assertEquals("Title is: bang!", user.getTitle());
    }
}
