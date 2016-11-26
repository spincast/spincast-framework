package org.spincast.plugins.jacksonxml.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.xml.XmlManager;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.plugins.jacksonxml.XmlMixinInfo;
import org.spincast.plugins.jacksonxml.XmlMixinInfoDefault;
import org.spincast.testing.core.SpincastTestBase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;

public class UseFieldInsteadOfGetterMixinTest extends SpincastTestBase {

    @Inject
    XmlManager xmlManager;

    protected XmlManager getXmlManager() {
        return this.xmlManager;
    }

    /**
     * Our User mixin
     */
    public static abstract class UserMixin implements User {

        //==========================================
        // Use the field instead of the getter!
        //==========================================
        @JsonProperty
        private String title;

        //==========================================
        // Ignore the getter!
        //==========================================
        @Override
        @JsonIgnore
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
            //
            // To use a *field*, we have to target the *implementation*
            // class in the Mixin.
            //==========================================
            protected void bindXmlMixins() {

                Multibinder<XmlMixinInfo> xmlMixinsBinder = Multibinder.newSetBinder(binder(), XmlMixinInfo.class);
                xmlMixinsBinder.addBinding().toInstance(new XmlMixinInfoDefault(UserDefault.class, UserMixin.class));
            }
        };
    }

    @Test
    public void useField() throws Exception {

        User user = new UserDefault();
        user.setName("Stromgol");
        user.setAge(123);
        user.setTitle("alien");

        String xml = getXmlManager().toXml(user);
        assertNotNull(xml);
        assertEquals("<UserDefault><name>Stromgol</name><age>123</age><title>alien</title></UserDefault>", xml);
    }

}
