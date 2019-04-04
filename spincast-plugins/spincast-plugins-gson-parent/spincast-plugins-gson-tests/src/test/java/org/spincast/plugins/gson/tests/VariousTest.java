package org.spincast.plugins.gson.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.Test;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonObject;
import org.spincast.plugins.gson.SpincastGsonManager;
import org.spincast.plugins.gson.tests.utils.GsonTestBase;
import org.spincast.plugins.gson.tests.utils.Widget;
import org.spincast.plugins.gson.tests.utils.WidgetEnum;

public class VariousTest extends GsonTestBase {

    @Test
    public void correctImplementation() {
        assertNotNull(getJsonManager());
        assertTrue(getJsonManager() instanceof SpincastGsonManager);
    }

    @Test
    public void deserializeJsonObjectNull() {
        JsonObject jsonObj = getJsonManager().fromString(null);
        assertNull(jsonObj);
    }

    @Test
    public void deserializeJsonObjectEmpty() {
        JsonObject jsonObj = getJsonManager().fromString("{}");
        assertNotNull(jsonObj);
    }

    @Test
    public void deserializeJsonObjectSimple() {
        JsonObject jsonObj = getJsonManager().fromString("{" +
                                                         "\"key1\": \"val1\"," +
                                                         "\"key2\": 123" +
                                                         "}");
        assertNotNull(jsonObj);

        assertEquals("val1", jsonObj.getString("key1"));
        assertEquals(new Integer(123), jsonObj.getInteger("key2"));
    }

    @Test
    public void deserializeJsonObjectComplex() {

        Instant now = Instant.now();

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("key1", "val1");
        jsonObj.set("key2", 123);
        jsonObj.set("key3", now);

        JsonObject innerObj = getJsonManager().create();
        jsonObj.set("innerObj", innerObj);
        innerObj.set("key5", "val5");

        JsonArray innerArray = getJsonManager().createArray();
        innerObj.set("key6", innerArray);
        innerArray.add("el1");
        innerArray.add(456);

        String asJsonString = jsonObj.toJsonString();
        assertNotNull(asJsonString);

        JsonObject fromString = getJsonManager().fromString(asJsonString);
        assertNotNull(fromString);

        assertEquals("val1", fromString.getString("key1"));
        assertEquals(new Integer(123), fromString.getInteger("key2"));
        assertEquals(now, fromString.getInstant("key3"));
        assertEquals("val5", fromString.getString("innerObj.key5"));

        assertEquals("el1", fromString.getString("innerObj.key6[0]"));
        assertEquals(new Integer(456), fromString.getInteger("innerObj.key6[1]"));
    }

    @Test
    public void fromObject() {

        Instant now = Instant.now();

        Widget widget = new Widget();
        widget.setStr("val1");
        widget.setInstant(now);

        Widget inner = new Widget();
        widget.setWidget(inner);
        inner.setStr("val2");
        inner.setInstant(null);

        JsonObject jsonObject = getJsonManager().fromObject(widget);
        assertNotNull(jsonObject);
        assertEquals("val1", jsonObject.getString("str"));
        assertEquals(now.toString(), jsonObject.getString("instant"));
        assertEquals("val2", jsonObject.getString("widget.str"));
    }

    @Test
    public void bigDecimal1() {

        BigDecimal bd = new BigDecimal("12345678.123");

        String jsonString = getJsonManager().toJsonString(bd);
        assertNotNull(jsonString);
        assertEquals("\"12345678.123\"", jsonString);
    }

    @Test
    public void bigDecimal2() {

        BigDecimal bd = new BigDecimal("12345678.123");

        JsonObject jsonObject = getJsonManager().create();
        jsonObject.set("key1", bd);

        String jsonString = jsonObject.toJsonString();
        assertNotNull(jsonString);
        assertEquals("{\"key1\":\"12345678.123\"}", jsonString);
    }

    @Test
    public void enum1() {
        String jsonString = getJsonManager().toJsonString(WidgetEnum.SECOND);
        assertNotNull(jsonString);
        assertEquals("{\"name\":\"SECOND\",\"label\":\"second\"}", jsonString);
    }

}


