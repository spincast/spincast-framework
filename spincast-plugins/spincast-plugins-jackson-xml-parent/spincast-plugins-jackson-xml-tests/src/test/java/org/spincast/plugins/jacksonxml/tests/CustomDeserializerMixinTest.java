package org.spincast.plugins.jacksonxml.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;
import org.spincast.core.xml.IXmlManager;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.plugins.jacksonxml.IXmlMixinInfo;
import org.spincast.plugins.jacksonxml.XmlMixinInfo;
import org.spincast.testing.core.SpincastTestBase;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;

public class CustomDeserializerMixinTest extends SpincastTestBase {

    @Inject
    IXmlManager xmlManager;

    protected IXmlManager getXmlManager() {
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
    protected Injector createInjector() {
        return Guice.createInjector(getTestingModule());
    }

    public Module getTestingModule() {
        return new SpincastDefaultTestingModule() {

            @Override
            protected void configure() {
                super.configure();
                bindXmlMixins();
            }

            //==========================================
            // Bind our mixin
            //==========================================
            protected void bindXmlMixins() {

                Multibinder<IXmlMixinInfo> xmlMixinsBinder = Multibinder.newSetBinder(binder(), IXmlMixinInfo.class);
                xmlMixinsBinder.addBinding().toInstance(new XmlMixinInfo(IUser.class, IUserMixin.class));
            }
        };
    }

    @Test
    public void customDeserializer() throws Exception {

        String xml = "<User><name>Stromgol</name><age>123</age><title>alien</title></User>";

        IUser user = getXmlManager().fromXml(xml, User.class);
        assertNotNull(user);
        assertEquals("Stromgol", user.getName());
        assertEquals(123, user.getAge());
        assertEquals("Title is: bang!", user.getTitle());

    }

}
