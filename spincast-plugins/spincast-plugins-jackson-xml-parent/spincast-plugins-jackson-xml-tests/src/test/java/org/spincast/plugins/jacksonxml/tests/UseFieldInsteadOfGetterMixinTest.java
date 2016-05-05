package org.spincast.plugins.jacksonxml.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.xml.IXmlManager;
import org.spincast.defaults.tests.DefaultTestingBase;
import org.spincast.defaults.tests.DefaultTestingModule;
import org.spincast.plugins.jacksonxml.IXmlMixinInfo;
import org.spincast.plugins.jacksonxml.XmlMixinInfo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;

public class UseFieldInsteadOfGetterMixinTest extends DefaultTestingBase {

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
    public Module getTestingModule() {
        return new DefaultTestingModule() {

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

                Multibinder<IXmlMixinInfo> xmlMixinsBinder = Multibinder.newSetBinder(binder(), IXmlMixinInfo.class);
                xmlMixinsBinder.addBinding().toInstance(new XmlMixinInfo(User.class, IUserMixin.class));
            }
        };
    }

    @Test
    public void useField() throws Exception {

        IUser user = new User();
        user.setName("Stromgol");
        user.setAge(123);
        user.setTitle("alien");

        String xml = getXmlManager().toXml(user);
        assertNotNull(xml);
        assertEquals("<User><name>Stromgol</name><age>123</age><title>alien</title></User>", xml);
    }

}
