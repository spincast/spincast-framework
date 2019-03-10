package org.spincast.plugins.jacksonxml.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.xml.XmlManager;
import org.spincast.plugins.jacksonxml.XmlMixinInfo;
import org.spincast.plugins.jacksonxml.XmlMixinInfoDefault;
import org.spincast.testing.defaults.NoAppTestingBase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;

public class UseFieldInsteadOfGetterMixinTest extends NoAppTestingBase {

    @Override
    protected Module getExtraOverridingModule() {

        return new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {

                //==========================================
                // Bind our mixin
                //
                // To use a *field*, we have to target the *implementation*
                // class in the Mixin.
                //==========================================
                Multibinder<XmlMixinInfo> xmlMixinsBinder = Multibinder.newSetBinder(binder(), XmlMixinInfo.class);
                xmlMixinsBinder.addBinding().toInstance(new XmlMixinInfoDefault(UserDefault.class, UserMixin.class));
            }
        };
    }

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
