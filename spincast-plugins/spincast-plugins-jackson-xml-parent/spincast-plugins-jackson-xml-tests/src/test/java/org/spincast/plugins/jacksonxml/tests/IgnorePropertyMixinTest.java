package org.spincast.plugins.jacksonxml.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.xml.XmlManager;
import org.spincast.defaults.testing.UnitTestDefaultContextsBase;
import org.spincast.plugins.jacksonxml.XmlMixinInfo;
import org.spincast.plugins.jacksonxml.XmlMixinInfoDefault;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;

public class IgnorePropertyMixinTest extends UnitTestDefaultContextsBase {

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
     * Our User mixin
     */
    public static abstract class UserMixin implements User {

        //==========================================
        // Ignore this property!
        //==========================================
        @Override
        @JsonIgnore
        public abstract String getName();

        //==========================================
        // Ignore this property!
        //==========================================
        @Override
        @JsonIgnore
        public abstract String getTitle();
    }

    @Test
    public void mixinIgnoreProperty() throws Exception {

        User user = new UserDefault();
        user.setName("Stromgol");
        user.setAge(123);
        user.setTitle("alien");

        String xml = getXmlManager().toXml(user);
        assertNotNull(xml);
        assertEquals("<UserDefault><age>123</age></UserDefault>", xml);

    }

}
