package org.spincast.plugins.jacksonjson.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.json.JsonManager;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.testing.core.SpincastTestBase;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class NoMixinTest extends SpincastTestBase {

    @Inject
    JsonManager jsonManager;

    @Override
    protected Injector createInjector() {
        return Guice.createInjector(new SpincastDefaultTestingModule());
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    @Test
    public void noMixinSerialize() throws Exception {

        User user = new UserDefault();
        user.setName("Stromgol");
        user.setAge(123);
        user.setTitle("alien");

        String jsonString = getJsonManager().toJsonString(user);
        assertNotNull(jsonString);
        assertEquals("{\"name\":\"Stromgol\",\"age\":123,\"title\":\"Title is: alien\"}", jsonString);
    }

    @Test
    public void noMixinDeserialize() throws Exception {

        String jsonString = "{\"name\":\"Stromgol\",\"age\":123,\"title\":\"some title\"}";

        User user = getJsonManager().fromString(jsonString, UserDefault.class);
        assertNotNull(jsonString);
        assertEquals("Stromgol", user.getName());
        assertEquals(123, user.getAge());
        assertEquals("Title is: some title", user.getTitle());

    }

}
