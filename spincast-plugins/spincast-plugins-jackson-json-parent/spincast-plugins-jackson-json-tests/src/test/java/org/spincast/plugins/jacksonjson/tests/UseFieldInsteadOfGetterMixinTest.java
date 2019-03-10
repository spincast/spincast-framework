package org.spincast.plugins.jacksonjson.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.json.JsonManager;
import org.spincast.plugins.jacksonjson.JsonMixinInfo;
import org.spincast.plugins.jacksonjson.JsonMixinInfoDefault;
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
                Multibinder<JsonMixinInfo> jsonMixinsBinder = Multibinder.newSetBinder(binder(), JsonMixinInfo.class);
                jsonMixinsBinder.addBinding().toInstance(new JsonMixinInfoDefault(UserDefault.class, UserMixin.class));
            }
        };
    }

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

        String jsonString = getJsonManager().toJsonString(user);
        assertNotNull(jsonString);
        assertEquals("{\"name\":\"Stromgol\",\"age\":123,\"title\":\"alien\"}", jsonString);

    }

}
