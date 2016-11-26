package org.spincast.plugins.jacksonjson.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.json.JsonManager;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.plugins.jacksonjson.JsonMixinInfo;
import org.spincast.plugins.jacksonjson.JsonMixinInfoDefault;
import org.spincast.testing.core.SpincastTestBase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;

public class IgnorePropertyMixinTest extends SpincastTestBase {

    @Inject
    JsonManager jsonManager;

    protected JsonManager getJsonManager() {
        return this.jsonManager;
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

    @Override
    protected Injector createInjector() {
        return Guice.createInjector(getTestingModule());
    }

    public Module getTestingModule() {
        return new SpincastDefaultTestingModule() {

            @Override
            protected void configure() {
                super.configure();
                bindJsonMixins();
            }

            //==========================================
            // Bind our mixin
            //==========================================
            protected void bindJsonMixins() {

                Multibinder<JsonMixinInfo> jsonMixinsBinder = Multibinder.newSetBinder(binder(), JsonMixinInfo.class);
                jsonMixinsBinder.addBinding().toInstance(new JsonMixinInfoDefault(User.class, UserMixin.class));
            }
        };
    }

    @Test
    public void mixinIgnoreProperty() throws Exception {

        User user = new UserDefault();
        user.setName("Stromgol");
        user.setAge(123);
        user.setTitle("alien");

        String jsonString = getJsonManager().toJsonString(user);
        assertNotNull(jsonString);
        assertEquals("{\"age\":123}", jsonString);

    }

}
