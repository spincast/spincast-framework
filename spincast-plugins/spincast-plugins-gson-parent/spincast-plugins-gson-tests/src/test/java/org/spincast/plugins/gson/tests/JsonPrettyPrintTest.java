package org.spincast.plugins.gson.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.json.JsonObject;
import org.spincast.plugins.gson.tests.utils.GsonTestBase;

public class JsonPrettyPrintTest extends GsonTestBase {

    @Test
    public void toJsonStringPretty() throws Exception {

        JsonObject jsonObj = this.jsonManager.create();
        jsonObj.set("someInt", 123);

        String jsonStr = this.jsonManager.toJsonString(jsonObj, true);
        assertNotNull(jsonStr);

        StringBuilder expected = new StringBuilder();

        expected.append("{\n");
        expected.append("  \"someInt\": 123\n");
        expected.append("}");

        assertEquals(expected.toString(), jsonStr);
    }

}
