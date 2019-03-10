package org.spincast.plugins.jacksonjson.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.json.JsonManager;
import org.spincast.testing.defaults.NoAppTestingBase;

import com.google.inject.Inject;

public class NoMixinTest extends NoAppTestingBase {

    @Inject
    JsonManager jsonManager;

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
