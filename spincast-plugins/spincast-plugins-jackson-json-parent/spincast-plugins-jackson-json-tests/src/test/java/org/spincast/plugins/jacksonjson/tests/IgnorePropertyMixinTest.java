package org.spincast.plugins.jacksonjson.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.json.IJsonManager;
import org.spincast.defaults.tests.DefaultTestingBase;
import org.spincast.defaults.tests.DefaultTestingModule;
import org.spincast.plugins.jacksonjson.IJsonMixinInfo;
import org.spincast.plugins.jacksonjson.JsonMixinInfo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;

public class IgnorePropertyMixinTest extends DefaultTestingBase {

    @Inject
    IJsonManager jsonManager;

    protected IJsonManager getJsonManager() {
        return this.jsonManager;
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
    public void mixinIgnoreProperty() throws Exception {

        IUser user = new User();
        user.setName("Stromgol");
        user.setAge(123);
        user.setTitle("alien");

        String jsonString = getJsonManager().toJsonString(user);
        assertNotNull(jsonString);
        assertEquals("{\"age\":123}", jsonString);

    }

}
