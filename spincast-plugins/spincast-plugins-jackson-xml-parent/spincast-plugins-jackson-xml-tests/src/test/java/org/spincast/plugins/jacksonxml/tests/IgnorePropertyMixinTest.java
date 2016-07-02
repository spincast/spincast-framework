package org.spincast.plugins.jacksonxml.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.xml.IXmlManager;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.plugins.jacksonxml.IXmlMixinInfo;
import org.spincast.plugins.jacksonxml.XmlMixinInfo;
import org.spincast.testing.core.SpincastTestBase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;

public class IgnorePropertyMixinTest extends SpincastTestBase {

    @Inject
    IXmlManager xmlManager;

    protected IXmlManager getXmlManager() {
        return this.xmlManager;
    }

    /**
     * Our IUser mixin
     */
    public static abstract class IUserMixin implements IUser {

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
    public void mixinIgnoreProperty() throws Exception {

        IUser user = new User();
        user.setName("Stromgol");
        user.setAge(123);
        user.setTitle("alien");

        String xml = getXmlManager().toXml(user);
        assertNotNull(xml);
        assertEquals("<User><age>123</age></User>", xml);

    }

}
