package org.spincast.tests.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exceptions.CantConvertException;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.json.JsonObjectArrayBase;
import org.spincast.core.json.JsonObjectDefault;
import org.spincast.core.json.JsonObjectFactory;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.defaults.testing.NoAppTestingBase;
import org.spincast.shaded.org.apache.commons.codec.binary.Base64;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.testing.core.utils.SpincastTestingUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class JsonObjectsTest extends NoAppTestingBase {

    @Inject
    protected JsonManager jsonManager;

    @Inject
    protected JsonObjectFactory jsonObjectFactory;

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected JsonObjectFactory getJsonObjectFactory() {
        return this.jsonObjectFactory;
    }

    public static enum TestEnum {
        ONE("first value"),
        SECOND("second value");

        private final String label;

        private TestEnum(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return this.label;
        }
    }

    @Test
    public void toJsonString() throws Exception {

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("someBoolean", true);
        jsonObj.set("someInt", 123);

        JsonObject jsonObj2 = getJsonManager().create();
        jsonObj2.set("anotherBoolean", true);
        jsonObj2.set("anotherInt", 44444);
        jsonObj2.set("innerObj", jsonObj);

        String jsonStr = getJsonManager().toJsonString(jsonObj2);
        assertNotNull(jsonStr);

        // The elements positions may not always be the same.
        assertEquals("{\"innerObj\":{\"someBoolean\":true,\"someInt\":123},\"anotherBoolean\":true,\"anotherInt\":44444}".length(),
                     jsonStr.length());

        @SuppressWarnings("unchecked")
        Map<String, Object> map = getJsonManager().fromString(jsonStr, Map.class);
        assertNotNull(map);
        assertEquals(44444, map.get("anotherInt"));

        // toJsonString
        String jsonString = jsonObj2.toJsonString();
        assertEquals("{\"innerObj\":{\"someBoolean\":true,\"someInt\":123},\"anotherBoolean\":true,\"anotherInt\":44444}".length(),
                     jsonString.length());

        // toString()
        jsonString = jsonObj2.toString();
        assertEquals("{\"innerObj\":{\"someBoolean\":true,\"someInt\":123},\"anotherBoolean\":true,\"anotherInt\":44444}".length(),
                     jsonString.length());

    }

    @Test
    public void toJsonStringPretty() throws Exception {

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("someInt", 123);

        String jsonStr = getJsonManager().toJsonString(jsonObj, true);
        assertNotNull(jsonStr);

        StringBuilder expected = new StringBuilder();

        expected.append("{\n");
        expected.append("    \"someInt\" : 123\n");
        expected.append("}");

        assertEquals(expected.toString(), jsonStr);

    }

    @Test
    public void toJsonStringBytes() throws Exception {

        JsonObject jsonObj = getJsonManager().create();
        byte[] bytes = SpincastTestingUtils.TEST_STRING.getBytes("UTF-8");
        jsonObj.set("someBytes", bytes);

        String jsonStr = getJsonManager().toJsonString(jsonObj);
        assertNotNull(jsonStr);

        assertEquals("{\"someBytes\":\"4oCbJ8OvxZPwo4608KCAi+GaocWgxaHDiMOGw6bDkMOw8J2FmPCdhaXwnYWv4oCZ\"}", jsonStr);
    }

    @Test
    public void toJsonStringDate() throws Exception {

        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.clear();
        cal.set(Calendar.YEAR, 2001);
        cal.set(Calendar.MONTH, 3);
        cal.set(Calendar.DATE, 6);
        cal.set(Calendar.HOUR_OF_DAY, 12);
        Date someDate = cal.getTime();

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("someDate", someDate);

        String jsonStr = getJsonManager().toJsonString(jsonObj);
        assertNotNull(jsonStr);
        assertEquals("{\"someDate\":\"2001-04-06T12:00:00.000Z\"}", jsonStr);
    }

    @Test
    public void toJsonStringDateWithMilliseconds() throws Exception {

        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.clear();
        cal.set(Calendar.YEAR, 2001);
        cal.set(Calendar.MONTH, 3);
        cal.set(Calendar.DATE, 6);
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MILLISECOND, 123);
        Date someDate = cal.getTime();

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("someDate", someDate);

        String jsonStr = getJsonManager().toJsonString(jsonObj);
        assertNotNull(jsonStr);
        assertEquals("{\"someDate\":\"2001-04-06T12:00:00.123Z\"}", jsonStr);
    }

    @Test
    public void jsonObjectLoop() throws Exception {

        JsonObject jsonObj2 = getJsonManager().create();
        jsonObj2.set("anotherBoolean", true);
        jsonObj2.set("anotherInt", 44444);

        boolean boolFound = false;
        boolean intFound = false;
        for (Entry<String, Object> entry : jsonObj2) {
            assertNotNull(entry);

            String key = entry.getKey();
            if ("anotherBoolean".equals(key)) {
                boolFound = true;
                assertEquals(true, entry.getValue());
            } else if ("anotherInt".equals(key)) {
                intFound = true;
                assertEquals(44444, entry.getValue());
            } else {
                fail();
            }
        }
        assertTrue(boolFound);
        assertTrue(intFound);
    }

    @Test
    public void fromJsonString() throws Exception {

        String str = "{\"innerObj\":{\"someBoolean\":true,\"someInt\":123}," +
                     "\"anotherBoolean\":true,\"anotherInt\":44444}";

        JsonObject jsonObj = getJsonManager().fromString(str);
        assertNotNull(jsonObj);
        assertEquals(true, jsonObj.getBoolean("anotherBoolean"));
        assertEquals(new Integer(44444), jsonObj.getInteger("anotherInt"));

        JsonObject jsonObj2 = jsonObj.getJsonObject("innerObj");
        assertNotNull(jsonObj2);
        assertEquals(true, jsonObj2.getBoolean("someBoolean"));
        assertEquals(new Integer(123), jsonObj2.getInteger("someInt"));

        String jsonString = jsonObj.toJsonString();
        assertEquals(str.length(), jsonString.length());
    }

    @Test
    public void getObject() throws Exception {

        JsonObject jsonObjInner = getJsonManager().create();

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("key1", "val1");
        jsonObj.set("key2", true);
        jsonObj.set("key3", "true");
        jsonObj.set("key4", jsonObjInner);
        jsonObj.set("key5", 123);
        jsonObj.set("key6", "123");
        jsonObj.set("key7", "12345678901234567890123456789012345678901234567890");

        Object object = jsonObj.getString("key1");
        assertEquals("val1", object);

        object = jsonObj.getBoolean("key2");
        assertEquals(true, object);

        object = jsonObj.getString("key3");
        assertEquals("true", object);

        object = jsonObj.getJsonObject("key4");
        assertEquals(jsonObjInner, object);

        object = jsonObj.getInteger("key5");
        assertEquals(123, object);

        object = jsonObj.getString("key6");
        assertEquals("123", object);

        object = jsonObj.getString("key7");
        assertEquals("12345678901234567890123456789012345678901234567890", object);

        object = jsonObj.getBigDecimal("nope");
        assertNull(object);

        object = jsonObj.getInteger("nope", 123);
        assertEquals(123, object);

        object = jsonObj.getInteger("nope", null);
        assertEquals(null, object);
    }

    @Test
    public void getBigDecimal() throws Exception {

        JsonObject jsonObjInner = getJsonManager().create();

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("key1", "val1");
        jsonObj.set("key2", true);
        jsonObj.set("key3", "true");
        jsonObj.set("key4", jsonObjInner);
        jsonObj.set("key5", 123);
        jsonObj.set("key6", "123");
        jsonObj.set("key7", "12345678901234567890123456789012345678901234567890");

        try {
            jsonObj.getBigDecimal("key1");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getBigDecimal("key2");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getBigDecimal("key3");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getBigDecimal("key4");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getBigDecimal("key4", BigDecimal.ONE);
            fail();
        } catch (Exception ex) {
        }

        BigDecimal result = jsonObj.getBigDecimal("key5");
        assertNotNull(result);
        assertEquals(new BigDecimal(123), result);

        result = jsonObj.getBigDecimal("key6");
        assertNotNull(result);
        assertEquals(new BigDecimal(123), result);

        result = jsonObj.getBigDecimal("key7");
        assertNotNull(result);
        assertEquals(new BigDecimal("12345678901234567890123456789012345678901234567890"), result);

        result = jsonObj.getBigDecimal("nope");
        assertNull(result);

        result = jsonObj.getBigDecimal("nope", new BigDecimal("123"));
        assertEquals(new BigDecimal("123"), result);

        result = jsonObj.getBigDecimal("nope", null);
        assertEquals(null, result);

    }

    @Test
    public void getBoolean() throws Exception {

        JsonObject jsonObjInner = getJsonManager().create();

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("key1", "val1");
        jsonObj.set("key2", true);
        jsonObj.set("key3", "true");
        jsonObj.set("key4", jsonObjInner);
        jsonObj.set("key5", 123);
        jsonObj.set("key6", "123");
        jsonObj.set("key7", "12345678901234567890123456789012345678901234567890");

        jsonObj.set("keyA", "false");
        jsonObj.set("keyB", false);

        try {
            jsonObj.getBoolean("key1");
            fail();
        } catch (Exception ex) {
        }

        Boolean result = jsonObj.getBoolean("key2");
        assertNotNull(result);
        assertEquals(true, result);

        result = jsonObj.getBoolean("key3");
        assertNotNull(result);
        assertEquals(true, result);

        result = jsonObj.getBoolean("keyA");
        assertNotNull(result);
        assertEquals(false, result);

        result = jsonObj.getBoolean("keyB");
        assertNotNull(result);
        assertEquals(false, result);

        try {
            jsonObj.getBoolean("key4");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getBoolean("key5");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getBoolean("key6");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getBoolean("key7");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getBoolean("key7", true);
            fail();
        } catch (Exception ex) {
        }

        result = jsonObj.getBoolean("nope", false);
        assertEquals(false, result);

        result = jsonObj.getBoolean("nope", null);
        assertEquals(null, result);

    }

    @Test
    public void getDouble() throws Exception {

        JsonObject jsonObjInner = getJsonManager().create();

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("key1", "val1");
        jsonObj.set("key2", true);
        jsonObj.set("key3", "true");
        jsonObj.set("key4", jsonObjInner);
        jsonObj.set("key5", 123);
        jsonObj.set("key6", "123");
        jsonObj.set("key7", "12345678901234567890123456789012345678901234567890");

        try {
            jsonObj.getDouble("key1");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getDouble("key2");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getDouble("key3");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getDouble("key4");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getDouble("key4", 123.4);
            fail();
        } catch (Exception ex) {
        }

        Double result = jsonObj.getDouble("key5");
        assertNotNull(result);
        assertEquals(new Double(123), result);

        result = jsonObj.getDouble("key6");
        assertNotNull(result);
        assertEquals(new Double(123), result);

        result = jsonObj.getDouble("key7");
        assertNotNull(result);

        result = jsonObj.getDouble("nope");
        assertNull(result);

        result = jsonObj.getDouble("nope", new Double("123"));
        assertEquals(new Double("123"), result);

        result = jsonObj.getDouble("nope", null);
        assertEquals(null, result);

    }

    @Test
    public void getInteger() throws Exception {

        JsonObject jsonObjInner = getJsonManager().create();

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("key1", "val1");
        jsonObj.set("key2", true);
        jsonObj.set("key3", "true");
        jsonObj.set("key4", jsonObjInner);
        jsonObj.set("key5", 123);
        jsonObj.set("key6", "123");
        jsonObj.set("key7", "12345678901234567890123456789012345678901234567890");

        try {
            jsonObj.getInteger("key1");
            fail();
        } catch (CantConvertException ex) {
        }

        try {
            jsonObj.getInteger("key2");
            fail();
        } catch (CantConvertException ex) {
        }

        try {
            jsonObj.getInteger("key3");
            fail();
        } catch (CantConvertException ex) {
        }

        try {
            jsonObj.getInteger("key4");
            fail();
        } catch (CantConvertException ex) {
        }

        try {
            jsonObj.getInteger("key4", 123);
            fail();
        } catch (CantConvertException ex) {
        }

        Integer result = jsonObj.getInteger("key5");
        assertNotNull(result);
        assertEquals(new Integer(123), result);

        result = jsonObj.getInteger("key6");
        assertNotNull(result);
        assertEquals(new Integer(123), result);

        try {
            jsonObj.getInteger("key7");
            fail();
        } catch (CantConvertException ex) {
        }

        result = jsonObj.getInteger("nope");
        assertNull(result);

        result = jsonObj.getInteger("nope", new Integer("123"));
        assertEquals(new Integer("123"), result);

        result = jsonObj.getInteger("nope", null);
        assertEquals(null, result);

    }

    @Test
    public void getLong() throws Exception {

        JsonObject jsonObjInner = getJsonManager().create();

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("key1", "val1");
        jsonObj.set("key2", true);
        jsonObj.set("key3", "true");
        jsonObj.set("key4", jsonObjInner);
        jsonObj.set("key5", 123);
        jsonObj.set("key6", "123");
        jsonObj.set("key7", "12345678901234567890123456789012345678901234567890");

        try {
            jsonObj.getLong("key1");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getLong("key2");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getLong("key3");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getLong("key4");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getLong("key4", 123L);
            fail();
        } catch (Exception ex) {
        }

        Long result = jsonObj.getLong("key5");
        assertNotNull(result);
        assertEquals(new Long(123), result);

        result = jsonObj.getLong("key6");
        assertNotNull(result);
        assertEquals(new Long(123), result);

        try {
            jsonObj.getLong("key7");
            fail();
        } catch (CantConvertException ex) {
        }

        result = jsonObj.getLong("nope");
        assertNull(result);

        result = jsonObj.getLong("nope", new Long("123"));
        assertEquals(new Long("123"), result);

        result = jsonObj.getLong("nope", null);
        assertEquals(null, result);
    }

    @Test
    public void getFloat() throws Exception {

        JsonObject jsonObjInner = getJsonManager().create();

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("key1", "val1");
        jsonObj.set("key2", true);
        jsonObj.set("key3", "true");
        jsonObj.set("key4", jsonObjInner);
        jsonObj.set("key5", 123);
        jsonObj.set("key6", "123");
        jsonObj.set("key7", "12345678901234567890123456789012345678901234567890");

        try {
            jsonObj.getFloat("key1");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getFloat("key2");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getFloat("key3");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getFloat("key4");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getFloat("key4", 123.4F);
            fail();
        } catch (Exception ex) {
        }

        Float result = jsonObj.getFloat("key5");
        assertNotNull(result);
        assertEquals(new Float(123), result);

        result = jsonObj.getFloat("key6");
        assertNotNull(result);
        assertEquals(new Float(123), result);

        result = jsonObj.getFloat("key7");
        assertNotNull(result);
        assertEquals((Float)Float.POSITIVE_INFINITY, result);

        result = jsonObj.getFloat("nope");
        assertNull(result);

        result = jsonObj.getFloat("nope", new Float("123"));
        assertEquals(new Float("123"), result);

        result = jsonObj.getFloat("nope", null);
        assertEquals(null, result);
    }

    @Test
    public void getDate() throws Exception {

        JsonObject jsonObjInner = getJsonManager().create();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2001);
        cal.set(Calendar.MONTH, 3);
        cal.set(Calendar.DATE, 6);
        cal.set(Calendar.HOUR_OF_DAY, 12);

        Date someDate = cal.getTime();
        String someDateISO8601 = SpincastStatics.getIso8601DateParserDefault().format(someDate);

        Instant someInstant = Instant.parse("2007-12-03T10:15:30.00Z");

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("key1", "val1");
        jsonObj.set("key2", true);
        jsonObj.set("key3", "true");
        jsonObj.set("key4", jsonObjInner);
        jsonObj.set("key5", 123);
        jsonObj.set("key6", "123");
        jsonObj.set("key7", "12345678901234567890123456789012345678901234567890");
        jsonObj.set("key8", someDateISO8601);
        jsonObj.set("key9", someDate);
        jsonObj.set("key10", someInstant);

        try {
            jsonObj.getDate("key1");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getDate("key2");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getDate("key3");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getDate("key4");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getDate("key5");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getDate("key6");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getDate("key7");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getDate("key7", new Date());
            fail();
        } catch (Exception ex) {
        }

        Date result = jsonObj.getDate("key8");
        assertNotNull(result);
        assertEquals(someDate.toString(), result.toString());

        result = jsonObj.getDate("nope");
        assertNull(result);

        result = jsonObj.getDate("nope", someDate);
        assertEquals(someDate, result);

        result = jsonObj.getDate("nope", null);
        assertEquals(null, result);

        result = jsonObj.getDate("key9");
        assertNotNull(result);
        assertEquals(someDate.toString(), result.toString());

        result = jsonObj.getDate("key10");
        assertNotNull(result);
        assertEquals(Date.from(someInstant).toString(), result.toString());
    }

    @Test
    public void getInstant() throws Exception {

        JsonObject jsonObjInner = getJsonManager().create();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2001);
        cal.set(Calendar.MONTH, 3);
        cal.set(Calendar.DATE, 6);
        cal.set(Calendar.HOUR_OF_DAY, 12);

        Date someDate = cal.getTime();

        Instant someInstant = Instant.parse("2007-12-03T10:15:30.00Z");

        String someDateISO8601 = someInstant.toString();

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("key1", "val1");
        jsonObj.set("key2", true);
        jsonObj.set("key3", "true");
        jsonObj.set("key4", jsonObjInner);
        jsonObj.set("key5", 123);
        jsonObj.set("key6", "123");
        jsonObj.set("key7", "12345678901234567890123456789012345678901234567890");
        jsonObj.set("key8", someDateISO8601);
        jsonObj.set("key9", someDate);
        jsonObj.set("key10", someInstant);

        try {
            jsonObj.getInstant("key1");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getInstant("key2");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getInstant("key3");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getInstant("key4");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getInstant("key5");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getInstant("key6");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getInstant("key7");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getInstant("key7", Instant.now());
            fail();
        } catch (Exception ex) {
        }

        Instant result = jsonObj.getInstant("key8");
        assertNotNull(result);
        assertEquals(someInstant.toString(), result.toString());

        result = jsonObj.getInstant("nope");
        assertNull(result);

        result = jsonObj.getInstant("nope", someInstant);
        assertEquals(someInstant, result);

        result = jsonObj.getInstant("nope", null);
        assertEquals(null, result);

        result = jsonObj.getInstant("key9");
        assertNotNull(result);
        Date d = Date.from(result);
        assertEquals(someDate.toString(), d.toString());

        result = jsonObj.getInstant("key10");
        assertNotNull(result);
        assertEquals(someInstant.toString(), result.toString());
    }

    @Test
    public void getString() throws Exception {

        JsonObject jsonObjInner = getJsonManager().create();

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("key1", "val1");
        jsonObj.set("key2", true);
        jsonObj.set("key3", "true");
        jsonObj.set("key4", jsonObjInner);
        jsonObj.set("key5", 123);
        jsonObj.set("key6", "123");
        jsonObj.set("key7", "12345678901234567890123456789012345678901234567890");

        String result = jsonObj.getString("key1");
        assertNotNull(result);
        assertEquals("val1", result);

        result = jsonObj.getString("key2");
        assertNotNull(result);
        assertEquals("true", result);

        result = jsonObj.getString("key3");
        assertNotNull(result);
        assertEquals("true", result);

        result = jsonObj.getString("key4");
        assertNotNull(result);

        result = jsonObj.getString("key5");
        assertNotNull(result);
        assertEquals("123", result);

        result = jsonObj.getString("key6");
        assertNotNull(result);
        assertEquals("123", result);

        result = jsonObj.getString("key7");
        assertNotNull(result);
        assertEquals("12345678901234567890123456789012345678901234567890", result);

        result = jsonObj.getString("nope", "123");
        assertEquals("123", result);

        result = jsonObj.getString("nope", null);
        assertEquals(null, result);

    }

    @Test
    public void getBytes() throws Exception {

        JsonObject jsonObjInner = getJsonManager().create();

        String base64String = Base64.encodeBase64String(SpincastTestingUtils.TEST_STRING.getBytes("UTF-8"));

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("key1", "val1");
        jsonObj.set("key2", true);
        jsonObj.set("key3", "true");
        jsonObj.set("key4", jsonObjInner);
        jsonObj.set("key5", 123);
        jsonObj.set("key6", "123");
        jsonObj.set("key7", "12345678901234567890123456789012345678901234567890");
        jsonObj.set("key8", base64String);

        byte[] result = jsonObj.getBytesFromBase64String("key1");
        assertNotNull(result);

        try {
            jsonObj.getBytesFromBase64String("key2");
            fail();
        } catch (Exception ex) {
        }

        result = jsonObj.getBytesFromBase64String("key3");
        assertNotNull(result);

        try {
            jsonObj.getBytesFromBase64String("key4");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getBytesFromBase64String("key5");
            fail();
        } catch (Exception ex) {
        }

        result = jsonObj.getBytesFromBase64String("key6");
        assertNotNull(result);

        result = jsonObj.getBytesFromBase64String("key7");
        assertNotNull(result);

        result = jsonObj.getBytesFromBase64String("key8");
        assertNotNull(result);
        byte[] bytes = SpincastTestingUtils.TEST_STRING.getBytes("UTF-8");
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], result[i]);
        }

        result = jsonObj.getBytesFromBase64String("nope", new byte[]{100});
        assertNotNull(result);
        assertTrue(result.length == 1);
        assertEquals(100, result[0]);

        result = jsonObj.getBytesFromBase64String("nope", null);
        assertEquals(null, result);

    }

    @Test
    public void getJsonObject() throws Exception {

        JsonObject jsonObjInner = getJsonManager().create();
        JsonArray jsonArrayInner = getJsonManager().createArray();

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("key1", "val1");
        jsonObj.set("key2", true);
        jsonObj.set("key3", "true");
        jsonObj.set("key4", jsonObjInner);
        jsonObj.set("key5", 123);
        jsonObj.set("key6", "123");
        jsonObj.set("key7", "12345678901234567890123456789012345678901234567890");
        jsonObj.set("key8", jsonArrayInner);

        try {
            jsonObj.getJsonObject("key1");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getJsonObject("key2");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getJsonObject("key3");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getJsonObject("key3", null);
            fail();
        } catch (Exception ex) {
        }

        JsonObject result = jsonObj.getJsonObject("key4");
        assertNotNull(result);
        assertEquals(jsonObjInner, result);

        try {
            jsonObj.getJsonObject("key5");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getJsonObject("key6");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getJsonObject("key7");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getJsonObject("key8");
            fail();
        } catch (Exception ex) {
        }

        JsonObject someObj = getJsonManager().create();
        result = jsonObj.getJsonObject("nope", someObj);
        assertEquals(someObj, result);

        result = jsonObj.getJsonObject("nope", null);
        assertEquals(null, result);
    }

    @Test
    public void getJsonArray() throws Exception {

        JsonObject jsonObjInner = getJsonManager().create();
        JsonArray jsonArrayInner = getJsonManager().createArray();

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("key1", "val1");
        jsonObj.set("key2", true);
        jsonObj.set("key3", "true");
        jsonObj.set("key4", jsonObjInner);
        jsonObj.set("key5", 123);
        jsonObj.set("key6", "123");
        jsonObj.set("key7", "12345678901234567890123456789012345678901234567890");
        jsonObj.set("key8", jsonArrayInner);

        try {
            jsonObj.getJsonArray("key1");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getJsonArray("key2");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getJsonArray("key3");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getJsonArray("key3", null);
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getJsonArray("key4");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getJsonArray("key5");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getJsonArray("key6");
            fail();
        } catch (Exception ex) {
        }

        try {
            jsonObj.getJsonArray("key7");
            fail();
        } catch (Exception ex) {
        }

        JsonArray result = jsonObj.getJsonArray("key8");
        assertNotNull(result);
        assertEquals(jsonArrayInner, result);

        JsonArray someArray = getJsonManager().createArray();
        result = jsonObj.getJsonArray("nope", someArray);
        assertEquals(someArray, result);

        result = jsonObj.getJsonArray("nope", null);
        assertEquals(null, result);
    }

    @Test
    public void jsonObjectArraySerialize() throws Exception {

        JsonArray jsonArray = getJsonManager().createArray();
        jsonArray.add(getJsonManager().create());
        jsonArray.add(123);
        jsonArray.add("abc");
        jsonArray.add(getJsonManager().create());

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("key1", "test");
        jsonObj.set("someArray", jsonArray);

        String jsonString = jsonObj.toJsonString();
        assertEquals("{\"key1\":\"test\",\"someArray\":[{},123,\"abc\",{}]}".length(), jsonString.length());

    }

    @Test
    public void jsonObjectArrayDeserialize() throws Exception {

        String str = "{\"someArray\":[{},123,\"abc\",[1, {}], \"123456\"]}";

        JsonObject jsonObj = getJsonManager().fromString(str);

        JsonArray jsonArray = jsonObj.getJsonArray("someArray");
        assertNotNull(jsonArray);

        JsonObject jsonObject = jsonArray.getJsonObject(0);
        assertNotNull(jsonObject);

        Integer integer = jsonArray.getInteger(1);
        assertNotNull(integer);

        Long longVal = jsonArray.getLong(1);
        assertNotNull(longVal);

        BigDecimal bd = jsonArray.getBigDecimal(1);
        assertNotNull(bd);

        String string = jsonArray.getString(2);
        assertNotNull(string);

        JsonArray array = jsonArray.getJsonArray(3);
        assertNotNull(array);

        Integer int1 = array.getInteger(0);
        assertNotNull(int1);

        JsonObject o1 = array.getJsonObject(1);
        assertNotNull(o1);

        integer = jsonArray.getInteger(4);
        assertNotNull(integer);

        longVal = jsonArray.getLong(4);
        assertNotNull(longVal);

        bd = jsonArray.getBigDecimal(4);
        assertNotNull(bd);
    }

    @Test
    public void jsonObjectArrayLoop() throws Exception {

        JsonObject jsonObj = getJsonManager().create();

        JsonArray jsonArray = getJsonManager().createArray();
        jsonArray.add(jsonObj);
        jsonArray.add(123);
        jsonArray.add("abc");

        int nbr = 0;
        for (Object element : jsonArray) {
            assertNotNull(element);
            if (nbr == 0) {
                assertEquals(jsonObj, element);
            } else if (nbr == 1) {
                assertEquals(123, element);
            } else if (nbr == 2) {
                assertEquals("abc", element);
            }
            nbr++;
        }
        assertEquals(3, nbr);
    }

    protected static class TestObject {

        @JsonIgnore
        @Inject
        protected SpincastConfig spincastConfig;

        protected String name;

        public TestObject() {
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public SpincastConfig getSpincastConfig() {
            return this.spincastConfig;
        }

    }

    @Test
    public void dependenciesInjectionOnDeserializationFromString() throws Exception {

        TestObject obj = new TestObject();
        obj.setName("test");
        assertNull(obj.getSpincastConfig());

        String jsonString = getJsonManager().toJsonString(obj);
        assertNotNull(jsonString);

        obj = getJsonManager().fromString(jsonString, TestObject.class);
        assertNotNull(obj);
        assertNotNull(obj.getSpincastConfig());
        assertEquals("test", obj.getName());
    }

    @Test
    public void dependenciesInjectionOnDeserializationFromInputStream() throws Exception {

        InputStream stream = getClass().getClassLoader().getResourceAsStream("obj.json");
        assertNotNull(stream);

        TestObject obj = getJsonManager().fromInputStream(stream, TestObject.class);
        assertNotNull(obj);
        assertNotNull(obj.getSpincastConfig());
        assertEquals("test", obj.getName());
    }

    @Test
    public void dependenciesInjectionOnCreate() throws Exception {

        JsonObject obj = getJsonManager().create();
        assertNotNull(obj);

        assertTrue(obj instanceof JsonObjectDefault);

        Field jsonManagerField = JsonObjectArrayBase.class.getDeclaredField("jsonManager");
        assertNotNull(jsonManagerField);

        jsonManagerField.setAccessible(true);

        Object jsonManager = jsonManagerField.get(obj);
        assertNotNull(jsonManager);
    }

    @Test
    public void dependenciesInjectionOnCreateFromString() throws Exception {

        JsonObject obj = getJsonManager().fromString("{\"name\":\"test\"}");
        assertNotNull(obj);

        assertTrue(obj instanceof JsonObjectDefault);

        Field jsonManagerField = JsonObjectArrayBase.class.getDeclaredField("jsonManager");
        assertNotNull(jsonManagerField);

        jsonManagerField.setAccessible(true);

        Object jsonManager = jsonManagerField.get(obj);
        assertNotNull(jsonManager);
    }

    @Test
    public void dependenciesInjectionOnCreateFromInputStream() throws Exception {

        InputStream stream = getClass().getClassLoader().getResourceAsStream("obj.json");
        assertNotNull(stream);

        JsonObject obj = getJsonManager().fromInputStream(stream);
        assertNotNull(obj);

        assertTrue(obj instanceof JsonObjectDefault);

        Field jsonManagerField = JsonObjectArrayBase.class.getDeclaredField("jsonManager");
        assertNotNull(jsonManagerField);

        jsonManagerField.setAccessible(true);

        Object jsonManager = jsonManagerField.get(obj);
        assertNotNull(jsonManager);
    }

    @Test
    public void customObjectConvertedToJsonObject() throws Exception {

        TestObject obj = new TestObject();
        obj.setName("test");

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("aaa", "bbb");
        jsonObj.set("custom", obj);

        String jsonString = jsonObj.toJsonString();
        assertNotNull(jsonString);

        jsonObj = getJsonManager().fromString(jsonString);
        assertNotNull(jsonObj);
        assertEquals("bbb", jsonObj.getString("aaa"));
        JsonObject obj2 = jsonObj.getJsonObject("custom");
        assertNotNull(obj2);
        assertEquals("test", obj2.getString("name"));
    }

    @Test
    public void arrayConvertedToJsonArray() throws Exception {

        String[] array = new String[]{"aaa", "bbb"};

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("array", array);

        JsonArray result = jsonObj.getJsonArray("array");
        assertNotNull(result);
        assertEquals("aaa", result.getString(0));
        assertEquals("bbb", result.getString(1));
    }

    @Test
    public void collectionConvertedToJsonArray() throws Exception {

        List<String> list = new ArrayList<String>();
        list.add("aaa");
        list.add("bbb");

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("array", list);

        JsonArray result = jsonObj.getJsonArray("array");
        assertNotNull(result);
        assertEquals("aaa", result.getString(0));
        assertEquals("bbb", result.getString(1));
    }

    @Test
    public void collectionConvertedToJsonArray2() throws Exception {

        Set<String> list = new HashSet<String>();
        list.add("aaa");
        list.add("bbb");

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("array", list);

        JsonArray result = jsonObj.getJsonArray("array");
        assertNotNull(result);
        assertEquals("aaa", result.getString(0));
        assertEquals("bbb", result.getString(1));
    }

    @Test
    public void mapConvertedToJsonObject() throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("key1", "val1");
        map.put("key2", "val2");

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("inner", map);

        JsonObject result = jsonObj.getJsonObject("inner");
        assertNotNull(result);
        assertEquals("val1", result.getString("key1"));
        assertEquals("val2", result.getString("key2"));
    }

    @Test
    public void mapConvertedToJsonObjectUsingCreateNoKeyParsing() throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("key1.key[2]", "val1");

        JsonObject jsonObj = getJsonManager().fromMap(map);

        assertFalse(jsonObj.isElementExists("key1.key[2]"));
        assertTrue(jsonObj.isElementExistsNoKeyParsing("key1.key[2]"));
    }

    @Test
    public void mapConvertedToJsonObjectUsingCreateNoKeyParsingExplicit() throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("key1.key[2]", "val1");

        JsonObject jsonObj = getJsonManager().fromMap(map, false);

        assertFalse(jsonObj.isElementExists("key1.key[2]"));
        assertTrue(jsonObj.isElementExistsNoKeyParsing("key1.key[2]"));
    }

    @Test
    public void mapConvertedToJsonObjectUsingCreate() throws Exception {

        JsonObject inner = getJsonManager().create();
        inner.set("innerKey1", "toto");

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("key1", "val1");
        map.put("inner", inner);

        JsonObject jsonObj = getJsonManager().fromMap(map, true);

        assertEquals("val1", jsonObj.getString("key1"));
        JsonObject innerResult = jsonObj.getJsonObject("inner");
        assertNotNull(innerResult);

        assertEquals("toto", innerResult.getString("innerKey1"));

        // A clone has been made
        inner.set("otherKey", "titi");
        assertEquals(null, innerResult.getString("otherKey"));
        innerResult.set("innerKey2", "tutu");
        assertEquals(null, inner.getString("innerKey2"));
    }

    @Test
    public void jsonObjectFromFile() throws Exception {

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("test", "Stromgol");

        File file = new File(createTestingFilePath());

        FileUtils.writeStringToFile(file, jsonObj.toJsonString(), "UTF-8");

        JsonObject jsonObj2 = getJsonManager().fromFile(file);
        assertNotNull(jsonObj2);
        assertEquals("Stromgol", jsonObj2.getString("test"));
    }

    @Test
    public void jsonObjectFromFilePath() throws Exception {

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("test", "Stromgol");

        File file = new File(createTestingFilePath());

        FileUtils.writeStringToFile(file, jsonObj.toJsonString(), "UTF-8");

        JsonObject jsonObj2 = getJsonManager().fromFile(file.getAbsolutePath());
        assertNotNull(jsonObj2);
        assertEquals("Stromgol", jsonObj2.getString("test"));
    }

    @Test
    public void jsonObjectFromClasspathFile() throws Exception {

        JsonObject jsonObj = getJsonManager().fromClasspathFile("/obj.json");
        assertNotNull(jsonObj);
        assertEquals("test", jsonObj.getString("name"));
    }

    @Test
    public void jsonObjectFromClasspathFileNoSlash() throws Exception {

        JsonObject jsonObj = getJsonManager().fromClasspathFile("obj.json");
        assertNotNull(jsonObj);
        assertEquals("test", jsonObj.getString("name"));
    }

    @Test
    public void jsonArrayArrayDeserialize() throws Exception {

        String str = "[{},123,\"abc\",[1, {}], \"123456\"]";

        JsonArray jsonArray = getJsonManager().fromStringArray(str);
        assertNotNull(jsonArray);

        Integer integer = jsonArray.getInteger(1);
        assertNotNull(integer);

        Long longVal = jsonArray.getLong(1);
        assertNotNull(longVal);

        BigDecimal bd = jsonArray.getBigDecimal(1);
        assertNotNull(bd);

        String string = jsonArray.getString(2);
        assertNotNull(string);

        JsonArray array = jsonArray.getJsonArray(3);
        assertNotNull(array);

        Integer int1 = array.getInteger(0);
        assertNotNull(int1);

        JsonObject o1 = array.getJsonObject(1);
        assertNotNull(o1);

        integer = jsonArray.getInteger(4);
        assertNotNull(integer);

        longVal = jsonArray.getLong(4);
        assertNotNull(longVal);

        bd = jsonArray.getBigDecimal(4);
        assertNotNull(bd);
    }

    @Test
    public void jsonArrayArrayDeserializeFromStream() throws Exception {

        InputStream stream = getClass().getClassLoader().getResourceAsStream("array.json");
        assertNotNull(stream);

        JsonArray jsonArray = getJsonManager().fromInputStreamArray(stream);
        assertNotNull(jsonArray);

        Integer integer = jsonArray.getInteger(1);
        assertNotNull(integer);

        Long longVal = jsonArray.getLong(1);
        assertNotNull(longVal);

        BigDecimal bd = jsonArray.getBigDecimal(1);
        assertNotNull(bd);

        String string = jsonArray.getString(2);
        assertNotNull(string);

        JsonArray array = jsonArray.getJsonArray(3);
        assertNotNull(array);

        Integer int1 = array.getInteger(0);
        assertNotNull(int1);

        JsonObject o1 = array.getJsonObject(1);
        assertNotNull(o1);

        integer = jsonArray.getInteger(4);
        assertNotNull(integer);

        longVal = jsonArray.getLong(4);
        assertNotNull(longVal);

        bd = jsonArray.getBigDecimal(4);
        assertNotNull(bd);
    }

    protected static class NoPropToSerialize {

        @JsonIgnore
        public String test;

    }

    @Test
    public void emptyObject() throws Exception {

        JsonObject jsonObj = getJsonManager().fromString("{}");
        assertNotNull(jsonObj);

        String jsonString = getJsonManager().toJsonString(new NoPropToSerialize());
        assertNotNull(jsonString);

        JsonArray jsonArray = getJsonManager().fromStringArray("[]");
        assertNotNull(jsonArray);
        jsonString = getJsonManager().toJsonString(new NoPropToSerialize[0]);
        assertNotNull(jsonString);
    }

    @Test
    public void getArray() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("toto");
        array.add("titi");

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("arr", array);

        JsonArray jsonArray = jsonObj.getJsonArray("arr");
        assertNotNull(jsonArray);
        assertEquals(2, jsonArray.size());
        assertEquals("toto", jsonArray.getString(0));
        assertEquals("titi", jsonArray.getString(1));
    }

    @Test
    public void getArrayFirstString() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("toto");
        array.add("titi");

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("arr", array);

        String arrayFirst = jsonObj.getArrayFirstString("arr");
        assertNotNull(arrayFirst);
        assertEquals("toto", arrayFirst);

        String arrayFirstString = jsonObj.getArrayFirstString("arr");
        assertNotNull(arrayFirstString);
        assertEquals("toto", arrayFirstString);
    }

    @Test
    public void getArrayFirstStringFromArray() throws Exception {

        JsonArray array2 = getJsonManager().createArray();
        array2.add("toto");
        array2.add("titi");

        JsonArray array = getJsonManager().createArray();
        array.add("nope");
        array.add(array2);

        String arrayFirst = array.getArrayFirstString(1);
        assertEquals("toto", arrayFirst);
    }

    @Test
    public void getArrayFirstInteger() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(123);
        array.add("titi");

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("arr", array);

        Integer arrayFirst = jsonObj.getArrayFirstInteger("arr");
        assertNotNull(arrayFirst);
        assertEquals(new Integer(123), arrayFirst);
    }

    @Test
    public void getArrayFirstIntegerFromArray() throws Exception {

        JsonArray array2 = getJsonManager().createArray();
        array2.add(123);
        array2.add("titi");

        JsonArray array = getJsonManager().createArray();
        array.add("nope");
        array.add(array2);

        Integer arrayFirst = array.getArrayFirstInteger(1);
        assertEquals(new Integer(123), arrayFirst);
    }

    @Test
    public void getArrayFirstLong() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(123L);
        array.add("titi");

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("arr", array);

        Long arrayFirst = jsonObj.getArrayFirstLong("arr");
        assertNotNull(arrayFirst);
        assertEquals(new Long(123), arrayFirst);
    }

    @Test
    public void getArrayFirstLongFromArray() throws Exception {

        JsonArray array2 = getJsonManager().createArray();
        array2.add(123L);
        array2.add("titi");

        JsonArray array = getJsonManager().createArray();
        array.add("nope");
        array.add(array2);

        Long arrayFirst = array.getArrayFirstLong(1);
        assertEquals(new Long(123), arrayFirst);
    }

    @Test
    public void getArrayFirstFloat() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(123F);
        array.add("titi");

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("arr", array);

        Float arrayFirst = jsonObj.getArrayFirstFloat("arr");
        assertNotNull(arrayFirst);
        assertEquals(new Float(123), arrayFirst);
    }

    @Test
    public void getArrayFirstFloatFromArray() throws Exception {

        JsonArray array2 = getJsonManager().createArray();
        array2.add(123F);
        array2.add("titi");

        JsonArray array = getJsonManager().createArray();
        array.add("nope");
        array.add(array2);

        Float arrayFirst = array.getArrayFirstFloat(1);
        assertEquals(new Float(123), arrayFirst);
    }

    @Test
    public void getArrayFirstDouble() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(123);
        array.add("titi");

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("arr", array);

        Double arrayFirst = jsonObj.getArrayFirstDouble("arr");
        assertNotNull(arrayFirst);
        assertEquals(new Double(123), arrayFirst);
    }

    @Test
    public void getArrayFirstDoubleFromArray() throws Exception {

        JsonArray array2 = getJsonManager().createArray();
        array2.add(123);
        array2.add("titi");

        JsonArray array = getJsonManager().createArray();
        array.add("nope");
        array.add(array2);

        Double arrayFirst = array.getArrayFirstDouble(1);
        assertEquals(new Double(123), arrayFirst);
    }

    @Test
    public void getArrayFirstBoolean() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(true);
        array.add("titi");

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("arr", array);

        Boolean arrayFirst = jsonObj.getArrayFirstBoolean("arr");
        assertNotNull(arrayFirst);
        assertEquals(true, arrayFirst);
    }

    @Test
    public void getArrayFirstBooleanFromArray() throws Exception {

        JsonArray array2 = getJsonManager().createArray();
        array2.add(true);
        array2.add("titi");

        JsonArray array = getJsonManager().createArray();
        array.add("nope");
        array.add(array2);

        Boolean arrayFirst = array.getArrayFirstBoolean(1);
        assertEquals(true, arrayFirst);
    }

    @Test
    public void getArrayFirstBigDecimal() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(new BigDecimal(123));
        array.add("titi");

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("arr", array);

        BigDecimal arrayFirst = jsonObj.getArrayFirstBigDecimal("arr");
        assertNotNull(arrayFirst);
        assertEquals(new BigDecimal(123), arrayFirst);
    }

    @Test
    public void getArrayFirstBigDecimalFromArray() throws Exception {

        JsonArray array2 = getJsonManager().createArray();
        array2.add(new BigDecimal(123));
        array2.add("titi");

        JsonArray array = getJsonManager().createArray();
        array.add("nope");
        array.add(array2);

        BigDecimal arrayFirst = array.getArrayFirstBigDecimal(1);
        assertEquals(new BigDecimal(123), arrayFirst);
    }

    @Test
    public void getArrayFirstDate() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(new Date());
        array.add("titi");

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("arr", array);

        Date arrayFirst = jsonObj.getArrayFirstDate("arr");
        assertNotNull(arrayFirst);
    }

    @Test
    public void getArrayFirstDateFromArray() throws Exception {

        JsonArray array2 = getJsonManager().createArray();
        array2.add(new Date());
        array2.add("titi");

        JsonArray array = getJsonManager().createArray();
        array.add("nope");
        array.add(array2);

        Date arrayFirst = array.getArrayFirstDate(1);
        assertNotNull(arrayFirst);
    }

    @Test
    public void getArrayFirstInstant() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(Instant.now());
        array.add("titi");

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("arr", array);

        Instant arrayFirst = jsonObj.getArrayFirstInstant("arr");
        assertNotNull(arrayFirst);
        assertTrue(arrayFirst instanceof Instant);
    }

    @Test
    public void getArrayFirstInstantFromArray() throws Exception {

        JsonArray array2 = getJsonManager().createArray();
        array2.add(Instant.now());
        array2.add("titi");

        JsonArray array = getJsonManager().createArray();
        array.add("nope");
        array.add(array2);

        Instant arrayFirst = array.getArrayFirstInstant(1);
        assertNotNull(arrayFirst);
        assertTrue(arrayFirst instanceof Instant);
    }

    @Test
    public void getArrayFirstByteArray() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("test".getBytes("UTF-8"));
        array.add("titi");

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("arr", array);

        byte[] arrayFirst = jsonObj.getArrayFirstBytesFromBase64String("arr");
        assertNotNull(arrayFirst);
        assertTrue(Arrays.equals("test".getBytes("UTF-8"), arrayFirst));
    }

    @Test
    public void getArrayFirstByteArrayFromArray() throws Exception {

        JsonArray array2 = getJsonManager().createArray();
        array2.add("test".getBytes("UTF-8"));
        array2.add("titi");

        JsonArray array = getJsonManager().createArray();
        array.add("nope");
        array.add(array2);

        byte[] arrayFirst = array.getArrayFirstBytesFromBase64String(1);
        assertTrue(Arrays.equals("test".getBytes("UTF-8"), arrayFirst));
    }

    @Test
    public void getArrayFirstElementDefaultValue() throws Exception {

        JsonObject jsonObj = getJsonManager().create();

        String arrayFirst = jsonObj.getString("nope", "titi");
        assertNotNull(arrayFirst);
        assertEquals("titi", arrayFirst);
    }

    @Test
    public void getArrayFirstElementDefaultValueFromArray() throws Exception {

        JsonArray array = getJsonManager().createArray();

        String arrayFirst = array.getString(0, "titi");
        assertNotNull(arrayFirst);
        assertEquals("titi", arrayFirst);
    }

    @Test
    public void getArrayFirstElementNoDefaultValue() throws Exception {

        JsonObject jsonObj = getJsonManager().create();

        String arrayFirst = jsonObj.getString("nope");
        assertNull(arrayFirst);
    }

    @Test
    public void getArrayFirstElementNoDefaultValueFroMArray() throws Exception {

        JsonArray array = getJsonManager().createArray();

        String arrayFirst = array.getString(0);
        assertNull(arrayFirst);
    }

    @Test
    public void getArrayFirstElementAsJsonObject() throws Exception {

        JsonObject inner = getJsonManager().create();
        inner.set("name", "inner");

        JsonArray array = getJsonManager().createArray();
        array.add(inner);
        array.add("titi");

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("arr", array);

        JsonObject arrayFirst = jsonObj.getArrayFirstJsonObject("arr");
        assertNotNull(arrayFirst);
        assertEquals("inner", arrayFirst.getString("name"));
    }

    @Test
    public void getArrayFirstElementAsJsonObjectFromArray() throws Exception {

        JsonObject inner = getJsonManager().create();
        inner.set("name", "inner");

        JsonArray array2 = getJsonManager().createArray();
        array2.add(inner);
        array2.add("titi");

        JsonArray array = getJsonManager().createArray();
        array.add("nope");
        array.add(array2);

        JsonObject arrayFirst = array.getArrayFirstJsonObject(1);
        assertNotNull(arrayFirst);
        assertEquals("inner", arrayFirst.getString("name"));
    }

    @Test
    public void getArrayFirstElementAsJsonArray() throws Exception {

        JsonArray inner = getJsonManager().createArray();
        inner.add("inner");

        JsonArray array = getJsonManager().createArray();
        array.add(inner);
        array.add("titi");

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("arr", array);

        JsonArray arrayFirst = jsonObj.getArrayFirstJsonArray("arr");
        assertNotNull(arrayFirst);
        assertEquals("inner", arrayFirst.getString(0));
    }

    @Test
    public void getArrayFirstElementAsJsonArrayFromArray() throws Exception {

        JsonArray inner = getJsonManager().createArray();
        inner.add("inner");

        JsonArray array2 = getJsonManager().createArray();
        array2.add(inner);
        array2.add("titi");

        JsonArray array = getJsonManager().createArray();
        array.add("nope");
        array.add(array2);

        JsonArray arrayFirst = array.getJsonArray(1);
        assertNotNull(arrayFirst);
        assertEquals("inner", arrayFirst.getArrayFirstString(0));
    }

    @Test
    public void getArrayFirstElementAsJsonArrayDefaultValue() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("toto");
        array.add("titi");

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("yo", "yeah");

        JsonArray arrayFirstJsonArray = jsonObj.getJsonArray("nope", array);
        assertNotNull(arrayFirstJsonArray);
        assertEquals("toto", array.getString(0));
        assertEquals("titi", array.getString(1));
    }

    @Test
    public void getArrayFirstElementAsJsonArrayNoDefaultValue() throws Exception {

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("yo", "yeah");

        JsonArray result = jsonObj.getJsonArray("nope");
        assertNull(result);
    }

    @Test
    public void getRealArrayFirstElement() throws Exception {

        String[] inner = new String[]{"titi"};

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("arr", inner);

        String first = jsonObj.getArrayFirstString("arr");
        assertNotNull(first);
        assertEquals("titi", first);
    }

    @Test
    public void getRealArrayFirstElementString() throws Exception {

        String[] inner = new String[]{"titi"};

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("arr", inner);

        String first = jsonObj.getArrayFirstString("arr");
        assertNotNull(first);
        assertEquals("titi", first);
    }

    @Test
    public void getRealArrayFirstElementJsonObject() throws Exception {

        JsonObject inner = getJsonManager().create();
        inner.set("yo", "yeah");

        JsonObject[] arr = new JsonObject[]{inner};

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("arr", arr);

        JsonObject first = jsonObj.getArrayFirstJsonObject("arr");
        assertNotNull(first);
        assertEquals("yeah", first.getString("yo"));
    }

    @Test
    public void getListFirstString() throws Exception {

        List<String> inner = Lists.newArrayList("titi");

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("list", inner);

        String first = jsonObj.getArrayFirstString("list");
        assertNotNull(first);
        assertEquals("titi", first);
    }

    @Test
    public void mapIsConvertedToJsonObject() throws Exception {

        Map<String, String> map = new HashMap<String, String>();
        map.put("name", "Stromgol");

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("map", map);

        JsonObject map2 = jsonObj.getJsonObject("map");
        assertNotNull(map2);

        assertTrue(map2.isElementExistsNoKeyParsing("name"));
        assertEquals("Stromgol", map2.getString("name"));
    }

    @Test
    public void getFirstDefaultValueEmptyArray() throws Exception {

        JsonArray emptyArray = getJsonManager().createArray();

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("array", emptyArray);

        String result = jsonObj.getArrayFirstString("array", "default");
        assertNotNull(result);
        assertEquals("default", result);
    }

    @Test
    public void getFirstDefaultValueInvalidConvertion() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("someString");

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("array", array);

        try {
            jsonObj.getInteger("array", 123);
            fail();
        } catch (CantConvertException ex) {
            return;
        }
        fail();
    }

    @Test
    public void getArrayElementInvalidIndexDefaultValue() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("aaa");

        String result = array.getString(3, "default");
        assertNotNull(result);
        assertEquals("default", result);
    }

    @Test
    public void getArrayElementInvalidIndexNoDefaultValue() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("aaa");

        String result = array.getString(3);
        assertNull(result);
    }

    @Test
    public void getArrayElementInvalidConversion() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("aaa");

        try {
            array.getInteger(0);
            fail();
        } catch (CantConvertException ex) {
            return;
        }
        fail();
    }

    @Test
    public void getArrayElementInvalidConversionDefaultValue() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("aaa");

        try {
            array.getInteger(0, 123);
            fail();
        } catch (CantConvertException ex) {
            return;
        }
        fail();
    }

    @Test
    public void getOrEmptyAbsentElements() throws Exception {

        JsonObject jsonObj = getJsonManager().create();

        String result = jsonObj.getJsonArrayOrEmpty("nope")
                               .getJsonObjectOrEmpty(5)
                               .getJsonArrayOrEmpty("nope")
                               .getJsonObjectOrEmpty(5)
                               .getString("nope");
        assertNull(result);
    }

    @Test
    public void getOrEmptyWithNullProperty() throws Exception {

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("key1", null);

        String result = jsonObj.getJsonObjectOrEmpty("key1")
                               .getJsonArrayOrEmpty("nope")
                               .getJsonArrayOrEmpty(0)
                               .getJsonObjectOrEmpty(5)
                               .getString("nope");
        assertNull(result);
    }

    @Test
    public void getOrEmptyWithNullElement() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(null);

        String result = array.getJsonArrayOrEmpty(0)
                             .getJsonArrayOrEmpty(0)
                             .getJsonObjectOrEmpty(5)
                             .getString("nope");
        assertNull(result);
    }

    @Test
    public void putArrayConvertNoClone() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("aaa");

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("array", array);

        JsonArray innerArray = jsonObj.getJsonArray("array");
        assertEquals(1, innerArray.size());
        assertEquals("aaa", innerArray.getString(0));

        array.add("bbb");
        assertEquals(2, innerArray.size());
        assertEquals("bbb", innerArray.getString(1));

        innerArray.add("ccc");
        assertEquals(3, innerArray.size());
        assertEquals("ccc", innerArray.getString(2));
        assertEquals(3, array.size());
        assertEquals("ccc", array.getString(2));
    }

    @Test
    public void putArrayCConvertClone() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("aaa");

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("array", array, true);

        JsonArray innerArray = jsonObj.getJsonArray("array");
        assertEquals(1, innerArray.size());
        assertEquals("aaa", innerArray.getString(0));

        array.add("bbb");
        assertEquals(2, array.size());
        assertEquals(1, innerArray.size());

        innerArray.add("ccc");
        assertEquals(2, array.size());
        assertEquals(2, innerArray.size());
    }

    @Test
    public void putJsonObjectConvertNoClone() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.set("key1", "val1");

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("inner", obj);

        JsonObject innerObj = jsonObj.getJsonObject("inner");
        assertEquals("val1", innerObj.getString("key1"));
        assertEquals("val1", obj.getString("key1"));
        assertNull(innerObj.getString("key2"));
        assertNull(obj.getString("key2"));
        assertNull(innerObj.getString("key3"));
        assertNull(obj.getString("key3"));

        innerObj.set("key2", "val2");
        assertEquals("val1", innerObj.getString("key1"));
        assertEquals("val1", obj.getString("key1"));
        assertEquals("val2", innerObj.getString("key2"));
        assertEquals("val2", obj.getString("key2"));
        assertNull(innerObj.getString("key3"));
        assertNull(obj.getString("key3"));

        obj.set("key3", "val3");
        assertEquals("val1", innerObj.getString("key1"));
        assertEquals("val1", obj.getString("key1"));
        assertEquals("val2", innerObj.getString("key2"));
        assertEquals("val2", obj.getString("key2"));
        assertEquals("val3", innerObj.getString("key3"));
        assertEquals("val3", obj.getString("key3"));
    }

    @Test
    public void putJsonObjectConvertClone() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.set("key1", "val1");

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("inner", obj, true);

        JsonObject innerObj = jsonObj.getJsonObject("inner");
        assertEquals("val1", innerObj.getString("key1"));
        assertEquals("val1", obj.getString("key1"));
        assertNull(innerObj.getString("key2"));
        assertNull(obj.getString("key2"));
        assertNull(innerObj.getString("key3"));
        assertNull(obj.getString("key3"));

        innerObj.set("key2", "val2");
        assertEquals("val1", innerObj.getString("key1"));
        assertEquals("val2", innerObj.getString("key2"));
        assertNull(obj.getString("key2"));
        assertNull(innerObj.getString("key3"));
        assertNull(obj.getString("key3"));

        obj.set("key3", "val3");
        assertEquals("val1", innerObj.getString("key1"));
        assertEquals("val1", obj.getString("key1"));
        assertEquals("val2", innerObj.getString("key2"));
        assertNull(obj.getString("key2"));
        assertNull(innerObj.getString("key3"));
        assertEquals("val3", obj.getString("key3"));
    }

    @Test
    public void arrayClone() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("aaa");

        JsonArray clone = array.clone(true);
        assertEquals(1, clone.size());
        assertEquals("aaa", clone.getString(0));

        array.add("bbb");
        assertEquals(2, array.size());
        assertEquals(1, clone.size());

        clone.add("ccc");
        assertEquals(2, array.size());
        assertEquals(2, clone.size());
    }

    @Test
    public void arrayCloneImmutable() throws Exception {

        JsonArray inner = getJsonManager().createArray();

        JsonArray array = getJsonManager().createArray();
        array.add("aaa");
        array.add(inner);

        JsonArray cloneImmutable = array.clone(false);
        assertFalse(cloneImmutable.isMutable());

        assertEquals(2, cloneImmutable.size());
        assertEquals("aaa", cloneImmutable.getString(0));

        JsonArray cloneInnerArray = cloneImmutable.getJsonArray(1);
        assertNotNull(cloneInnerArray);
        assertEquals(0, cloneInnerArray.size());

        array.add("bbb");
        assertEquals(3, array.size());
        assertEquals(2, cloneImmutable.size());

        inner.add("ccc");
        assertEquals(1, inner.size());
        assertEquals(0, cloneInnerArray.size());

        try {
            cloneImmutable.add("ddd");
            fail();
        } catch (Exception ex) {
        }

        try {
            cloneInnerArray.add("eee");
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void jsonObjectClone() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.set("key1", "val1");

        JsonObject clone = obj.clone(true);
        assertEquals("val1", clone.getString("key1"));
        assertEquals("val1", obj.getString("key1"));
        assertNull(clone.getString("key2"));
        assertNull(obj.getString("key2"));
        assertNull(clone.getString("key3"));
        assertNull(obj.getString("key3"));

        clone.set("key2", "val2");
        assertEquals("val1", clone.getString("key1"));
        assertEquals("val2", clone.getString("key2"));
        assertNull(obj.getString("key2"));
        assertNull(clone.getString("key3"));
        assertNull(obj.getString("key3"));

        obj.set("key3", "val3");
        assertEquals("val1", clone.getString("key1"));
        assertEquals("val1", obj.getString("key1"));
        assertEquals("val2", clone.getString("key2"));
        assertNull(obj.getString("key2"));
        assertNull(clone.getString("key3"));
        assertEquals("val3", obj.getString("key3"));
    }

    @Test
    public void jsonObjectCloneImmutable() throws Exception {

        JsonObject inner = getJsonManager().create();

        JsonObject obj = getJsonManager().create();
        obj.set("key1", "val1");
        obj.set("inner", inner);

        JsonObject clone = obj.clone(false);
        assertEquals("val1", clone.getString("key1"));
        assertEquals("val1", obj.getString("key1"));
        assertNull(clone.getString("key2"));
        assertNull(obj.getString("key2"));
        JsonObject innerClone = clone.getJsonObject("inner");

        assertNull(inner.getString("innerKey"));
        assertNull(innerClone.getString("innerKey"));

        obj.set("key2", "val2");
        assertEquals("val1", clone.getString("key1"));
        assertEquals("val1", obj.getString("key1"));
        assertNull(clone.getString("key2"));
        assertEquals("val2", obj.getString("key2"));

        inner.set("innerKey", "innerValue");
        assertEquals("innerValue", inner.getString("innerKey"));
        assertNull(innerClone.getString("innerKey"));

        try {
            clone.set("key3", "val3");
            fail();
        } catch (Exception ex) {
        }

        try {
            innerClone.set("key4", "val4");
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void arrayRemoveElement() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("aaa");
        array.add("bbb");
        array.add("ccc");
        array.add("ddd");

        assertEquals("aaa", array.getString(0));
        assertEquals("bbb", array.getString(1));
        assertEquals("ccc", array.getString(2));
        assertEquals("ddd", array.getString(3));

        array.remove(1);
        assertEquals("aaa", array.getString(0));
        assertEquals("ccc", array.getString(1));
        assertEquals("ddd", array.getString(2));

        array.remove(0);
        assertEquals("ccc", array.getString(0));
        assertEquals("ddd", array.getString(1));

        array.remove(1);
        assertEquals("ccc", array.getString(0));

        array.remove(0);
        assertEquals(0, array.size());
    }

    @Test
    public void addImmutableArrayInMutableJsonObject() throws Exception {

        JsonArray array = getJsonManager().createArray();

        JsonArray immutableArray = array.clone(false);

        try {
            immutableArray.add("nope");
            fail();
        } catch (Exception ex) {
        }

        JsonObject obj = getJsonManager().create();
        obj.set("array", immutableArray);

        try {
            immutableArray.add("nope");
            fail();
        } catch (Exception ex) {
        }

        JsonArray innerArray = obj.getJsonArray("array");
        innerArray.add("yes!");

        String result = obj.getArrayFirstString("array");
        assertEquals("yes!", result);
    }

    @Test
    public void addImmutableArrayInMutableArray() throws Exception {

        JsonArray array = getJsonManager().createArray();

        JsonArray immutableArray = array.clone(false);

        try {
            immutableArray.add("nope");
            fail();
        } catch (Exception ex) {
        }

        JsonArray parent = getJsonManager().createArray();
        parent.add(immutableArray);

        try {
            immutableArray.add("nope");
            fail();
        } catch (Exception ex) {
        }

        JsonArray innerArray = parent.getJsonArray(0);
        innerArray.add("yes!");

        String result = innerArray.getString(0);
        assertEquals("yes!", result);
    }

    @Test
    public void addImmutableObjectInMutableJsonObject() throws Exception {

        JsonObject obj2 = getJsonManager().create();

        JsonObject immutableObj = obj2.clone(false);

        try {
            immutableObj.set("nope", "I don't think so");
            fail();
        } catch (Exception ex) {
        }

        JsonObject obj = getJsonManager().create();
        obj.set("inner", immutableObj);

        try {
            immutableObj.set("nope", "I don't think so");
            fail();
        } catch (Exception ex) {
        }

        JsonObject innerObj = obj.getJsonObject("inner");
        innerObj.set("key1", "yes!");

    }

    @Test
    public void addImmutableObjectInMutableJsonArray() throws Exception {

        JsonObject obj2 = getJsonManager().create();

        JsonObject immutableObj = obj2.clone(false);

        try {
            immutableObj.set("nope", "I don't think so");
            fail();
        } catch (Exception ex) {
        }

        JsonArray array = getJsonManager().createArray();
        array.add(immutableObj);

        try {
            immutableObj.set("nope", "I don't think so");
            fail();
        } catch (Exception ex) {
        }

        JsonObject innerObj = array.getJsonObject(0);
        innerObj.set("key1", "yes!");

    }

    @Test
    public void arraySetValueToSpecificIndex() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.set(3, "value1");

        assertEquals(null, array.getString(0, "nope"));
        assertEquals(null, array.getString(1, "nope"));
        assertEquals(null, array.getString(2, "nope"));
        assertEquals("value1", array.getString(3, "nope"));
        assertEquals("nope", array.getString(4, "nope"));

        array.set(1, "value2");

        assertEquals(null, array.getString(0, "nope"));
        assertEquals("value2", array.getString(1, "nope"));
        assertEquals(null, array.getString(2, "nope"));
        assertEquals("value1", array.getString(3, "nope"));
        assertEquals("nope", array.getString(4, "nope"));
    }

    @Test
    public void arrayInsertValueToSpecificIndex() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(3, "value1");

        assertEquals(null, array.getString(0, "nope"));
        assertEquals(null, array.getString(1, "nope"));
        assertEquals(null, array.getString(2, "nope"));
        assertEquals("value1", array.getString(3, "nope"));
        assertEquals("nope", array.getString(4, "nope"));

        array.add(1, "value2");

        assertEquals(null, array.getString(0, "nope"));
        assertEquals("value2", array.getString(1, "nope"));
        assertEquals(null, array.getString(2, "nope"));
        assertEquals(null, array.getString(3, "nope"));
        assertEquals("value1", array.getString(4, "nope"));
        assertEquals("nope", array.getString(5, "nope"));
    }

    @Test
    public void arrayAddIndexUnderZero() throws Exception {

        JsonArray array = getJsonManager().createArray();

        try {
            array.add(-10, "value1");
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void arraySetIndexUnderZero() throws Exception {

        JsonArray array = getJsonManager().createArray();
        try {
            array.set(-10, "value1");
            fail();
        } catch (Exception ex) {
        }
    }

    protected static class ConvertTest {

        private boolean someBoolean;
        private int someInt;
        private String someString;

        public boolean isSomeBoolean() {
            return this.someBoolean;
        }

        public void setSomeBoolean(boolean someBoolean) {
            this.someBoolean = someBoolean;
        }

        public int getSomeInt() {
            return this.someInt;
        }

        public void setSomeInt(int someInt) {
            this.someInt = someInt;
        }

        public String getSomeString() {
            return this.someString;
        }

        public void setSomeString(String someString) {
            this.someString = someString;
        }
    }

    protected static class ConvertTestParent {

        private ConvertTest someObj;
        private String someString;

        public ConvertTest getSomeObj() {
            return this.someObj;
        }

        public void setSomeObj(ConvertTest someObj) {
            this.someObj = someObj;
        }

        public String getSomeString() {
            return this.someString;
        }

        public void setSomeString(String someString) {
            this.someString = someString;
        }
    }

    @Test
    public void convert() throws Exception {

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("someBoolean", true);
        jsonObj.set("someInt", 123);
        jsonObj.set("someString", "test");

        ConvertTest result = jsonObj.convert(ConvertTest.class);
        assertNotNull(result);
        assertEquals(true, result.isSomeBoolean());
        assertEquals(123, result.getSomeInt());
        assertEquals("test", result.getSomeString());
    }

    @Test
    public void convertWithParent() throws Exception {

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("someBoolean", true);
        jsonObj.set("someInt", 123);
        jsonObj.set("someString", "test");

        JsonObject parent = getJsonManager().create();
        parent.set("someObj", jsonObj);
        parent.set("someString", "test2");

        ConvertTestParent result = parent.convert(ConvertTestParent.class);
        assertNotNull(result);
        assertEquals("test2", result.getSomeString());
        ConvertTest inner = result.getSomeObj();
        assertNotNull(inner);
        assertEquals(true, inner.isSomeBoolean());
        assertEquals(123, inner.getSomeInt());
        assertEquals("test", inner.getSomeString());
    }

    @Test
    public void isElementExistsNoKeyParsing() throws Exception {

        JsonObject inner = getJsonManager().create();
        inner.set("key1", "test");

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("inner", inner);

        assertTrue(jsonObj.isElementExistsNoKeyParsing("inner"));

        assertFalse(jsonObj.isElementExistsNoKeyParsing("inner2"));
        assertFalse(jsonObj.isElementExistsNoKeyParsing("inner.key1"));
    }

    @Test
    public void isElementExists() throws Exception {

        JsonObject inner = getJsonManager().create();
        inner.set("key1", "test");

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("inner", inner);

        assertFalse(jsonObj.isElementExists(null));
        assertFalse(jsonObj.isElementExists(""));
        assertFalse(jsonObj.isElementExists("inner2"));
        assertFalse(jsonObj.isElementExists("inner.key2"));
        assertFalse(jsonObj.isElementExists("inner.nope.nope[2].key2"));

        assertTrue(jsonObj.isElementExists("inner"));
        assertTrue(jsonObj.isElementExists("inner.key1"));
    }

    @Test
    public void arrayIsIndexExists() throws Exception {

        JsonObject inner = getJsonManager().create();
        inner.set("key1", "test");

        JsonArray array = getJsonManager().createArray();
        array.set(1, inner);

        assertTrue(array.isElementExists(0));
        assertTrue(array.isElementExists(1));

        assertFalse(array.isElementExists(2));
    }

    @Test
    public void arrayisElementExists() throws Exception {

        JsonObject inner = getJsonManager().create();
        inner.set("key1", "test");

        JsonArray array = getJsonManager().createArray();
        array.set(1, inner);

        assertTrue(array.isElementExists("[0]"));
        assertTrue(array.isElementExists("[1]"));
        assertTrue(array.isElementExists("[1].key1"));

        assertFalse(array.isElementExists("[2]"));
        assertFalse(array.isElementExists("[0].key1"));
        assertFalse(array.isElementExists("[1].key2"));
        assertFalse(array.isElementExists("[0].nope.key1"));
        assertFalse(array.isElementExists("inner.nope.nope[2].key2"));
    }

    @Test
    public void objRemove() throws Exception {

        JsonObject inner = getJsonManager().create();
        inner.set("key1", "test");
        inner.set("key2", "test2");

        JsonObject inner2 = getJsonManager().create();
        inner2.set("key3", "test3");
        inner2.set("key4", "test4");

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("inner", inner);
        jsonObj.set("inner2", inner2);

        assertTrue(jsonObj.isElementExists("inner"));
        assertTrue(jsonObj.isElementExists("inner.key1"));
        assertTrue(jsonObj.isElementExists("inner.key2"));

        assertTrue(jsonObj.isElementExists("inner2"));
        assertTrue(jsonObj.isElementExists("inner2.key3"));
        assertTrue(jsonObj.isElementExists("inner2.key4"));

        jsonObj.remove("inner.key1");

        assertFalse(jsonObj.isElementExists("inner.key1"));
        assertTrue(jsonObj.isElementExists("inner.key2"));

        assertTrue(jsonObj.isElementExists("inner2"));
        assertTrue(jsonObj.isElementExists("inner2.key3"));
        assertTrue(jsonObj.isElementExists("inner2.key4"));

        jsonObj.remove("inner2");

        assertFalse(jsonObj.isElementExists("inner.key1"));
        assertTrue(jsonObj.isElementExists("inner.key2"));

        assertFalse(jsonObj.isElementExists("inner2"));
        assertFalse(jsonObj.isElementExists("inner2.key3"));
        assertFalse(jsonObj.isElementExists("inner2.key4"));

        // Does nothing
        jsonObj.remove(null);
        jsonObj.remove("");
    }

    @Test
    public void objRemoveNoKeyParsing() throws Exception {

        JsonObject inner = getJsonManager().create();
        inner.set("key1", "test");
        inner.set("key2", "test2");

        JsonObject inner2 = getJsonManager().create();
        inner2.set("key3", "test3");
        inner2.set("key4", "test4");

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("inner", inner);
        jsonObj.set("inner2", inner2);

        assertTrue(jsonObj.isElementExists("inner"));
        assertTrue(jsonObj.isElementExists("inner.key1"));
        assertTrue(jsonObj.isElementExists("inner.key2"));

        assertTrue(jsonObj.isElementExists("inner2"));
        assertTrue(jsonObj.isElementExists("inner2.key3"));
        assertTrue(jsonObj.isElementExists("inner2.key4"));

        jsonObj.removeNoKeyParsing("inner.key1");

        assertTrue(jsonObj.isElementExists("inner"));
        assertTrue(jsonObj.isElementExists("inner.key1"));
        assertTrue(jsonObj.isElementExists("inner.key2"));

        assertTrue(jsonObj.isElementExists("inner2"));
        assertTrue(jsonObj.isElementExists("inner2.key3"));
        assertTrue(jsonObj.isElementExists("inner2.key4"));

        jsonObj.removeNoKeyParsing("inner2");

        assertTrue(jsonObj.isElementExists("inner"));
        assertTrue(jsonObj.isElementExists("inner.key1"));
        assertTrue(jsonObj.isElementExists("inner.key2"));

        assertFalse(jsonObj.isElementExists("inner2"));
        assertFalse(jsonObj.isElementExists("inner2.key3"));
        assertFalse(jsonObj.isElementExists("inner2.key4"));

        // Does nothing
        jsonObj.removeNoKeyParsing(null);
        jsonObj.removeNoKeyParsing("");
    }

    @Test
    public void arrayRemove() throws Exception {

        JsonObject inner = getJsonManager().create();
        inner.set("key1", "test");
        inner.set("key2", "test2");

        JsonObject inner2 = getJsonManager().create();
        inner2.set("key3", "test3");
        inner2.set("key4", "test4");

        JsonArray array = getJsonManager().createArray();
        array.set(1, inner);
        array.set(2, inner2);

        assertTrue(array.isElementExists("[0]"));
        assertTrue(array.isElementExists("[1]"));
        assertTrue(array.isElementExists("[1].key1"));
        assertTrue(array.isElementExists("[1].key2"));
        assertTrue(array.isElementExists("[2]"));
        assertTrue(array.isElementExists("[2].key3"));
        assertTrue(array.isElementExists("[2].key4"));

        array.remove("[1].key1");

        assertTrue(array.isElementExists("[0]"));
        assertTrue(array.isElementExists("[1]"));
        assertFalse(array.isElementExists("[1].key1"));
        assertTrue(array.isElementExists("[1].key2"));
        assertTrue(array.isElementExists("[2]"));
        assertTrue(array.isElementExists("[2].key3"));
        assertTrue(array.isElementExists("[2].key4"));

        array.remove("[2]");

        assertTrue(array.isElementExists("[0]"));
        assertTrue(array.isElementExists("[1]"));
        assertFalse(array.isElementExists("[1].key1"));
        assertTrue(array.isElementExists("[1].key2"));
        assertFalse(array.isElementExists("[2]"));
        assertFalse(array.isElementExists("[2].key3"));
        assertFalse(array.isElementExists("[2].key4"));
    }

    @Test
    public void arrayRemoveIndex() throws Exception {

        JsonObject inner = getJsonManager().create();
        inner.set("key1", "test");
        inner.set("key2", "test2");

        JsonObject inner2 = getJsonManager().create();
        inner2.set("key3", "test3");
        inner2.set("key4", "test4");

        JsonArray array = getJsonManager().createArray();
        array.set(1, inner);
        array.set(2, inner2);

        assertTrue(array.isElementExists("[0]"));
        assertTrue(array.isElementExists("[1]"));
        assertTrue(array.isElementExists("[1].key1"));
        assertTrue(array.isElementExists("[1].key2"));
        assertTrue(array.isElementExists("[2]"));
        assertTrue(array.isElementExists("[2].key3"));
        assertTrue(array.isElementExists("[2].key4"));

        array.remove(2);

        assertTrue(array.isElementExists("[0]"));
        assertTrue(array.isElementExists("[1]"));
        assertTrue(array.isElementExists("[1].key1"));
        assertTrue(array.isElementExists("[1].key2"));
        assertFalse(array.isElementExists("[2]"));
        assertFalse(array.isElementExists("[2].key3"));
        assertFalse(array.isElementExists("[2].key4"));

        array.remove(0);

        // Last element to moved to the left.
        assertTrue(array.isElementExists("[0]"));
        assertTrue(array.isElementExists("[0].key1"));
        assertTrue(array.isElementExists("[0].key2"));
        assertFalse(array.isElementExists("[1]"));
        assertFalse(array.isElementExists("[2]"));
    }

    @Test
    public void bigDecimalUsesToPlainString() throws Exception {

        BigDecimal bigDecimal = new BigDecimal("1234E+4");

        JsonObject obj = getJsonManager().create();
        obj.set("key1", bigDecimal);

        assertEquals("{\"key1\":\"12340000\"}", obj.toJsonString());

        JsonArray array = getJsonManager().createArray();
        array.add(bigDecimal);

        assertEquals("[\"12340000\"]", array.toJsonString());
    }

    @Test
    public void arrayAddAllList() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("aaa");

        List<String> list = new ArrayList<String>();
        list.add("bbb");
        list.add("ccc");

        array.addAll(list);

        assertEquals(3, array.size());
        assertEquals("aaa", array.getString(0));
        assertEquals("bbb", array.getString(1));
        assertEquals("ccc", array.getString(2));
    }

    @Test
    public void arrayAddAllSet() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("aaa");

        Set<String> set = new LinkedHashSet<String>();
        set.add("bbb");
        set.add("ccc");

        array.addAll(set);

        assertEquals(3, array.size());
        assertEquals("aaa", array.getString(0));
        assertEquals("bbb", array.getString(1));
        assertEquals("ccc", array.getString(2));
    }

    @Test
    public void arrayAddAllArray() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("aaa");

        String[] toAdd = new String[]{"bbb", "ccc"};

        array.addAll(toAdd);

        assertEquals(3, array.size());
        assertEquals("aaa", array.getString(0));
        assertEquals("bbb", array.getString(1));
        assertEquals("ccc", array.getString(2));
    }

    @Test
    public void arrayAddAllJsonArray() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("aaa");

        JsonArray toAdd = getJsonManager().createArray();
        array.add("bbb");
        array.add("ccc");

        array.addAll(toAdd);

        assertEquals(3, array.size());
        assertEquals("aaa", array.getString(0));
        assertEquals("bbb", array.getString(1));
        assertEquals("ccc", array.getString(2));
    }

    @Test
    public void arrayPutUsingJsonPath() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.set("key1", "aaa");

        JsonArray arrayInner = getJsonManager().createArray();
        arrayInner.add("aaa");
        arrayInner.add("bbb");
        arrayInner.add(obj);

        JsonArray array = getJsonManager().createArray();
        array.add("aaa");
        array.add(arrayInner);

        array.set("[1][2].name", "Stromgol");
        array.set("[0]", "test");
        array.set("[3]", "titi");

        assertNotNull(obj.getString("name"));
        assertEquals("Stromgol", obj.getString("name"));

        String res = array.getString(0);
        assertNotNull(res);
        assertEquals("test", res);

        res = array.getString(2, "nope");
        assertNull(res);

        res = array.getString(3);
        assertNotNull(res);
        assertEquals("titi", res);

        res = array.getString("[1][2].name");
        assertNotNull(res);
        assertEquals("Stromgol", res);
    }

    @Test
    public void arrayPutUsingJsonPathInvalidNoStartingIndex() throws Exception {

        JsonArray array = getJsonManager().createArray();

        try {
            array.set("name", "Stromgol");
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void withInitialMapMutable() throws Exception {

        Map<String, Object> initialMap = new HashMap<String, Object>();
        initialMap.put("key1", "val1");
        initialMap.put("key2", "val2");

        JsonObject obj = getJsonObjectFactory().create(initialMap, true);
        assertNotNull(obj);
        assertEquals(2, obj.size());
        assertEquals("val1", obj.getString("key1"));
        assertEquals("val2", obj.getString("key2"));

        obj.set("key3", "val3");
        assertEquals(3, obj.size());
        assertEquals("val3", obj.getString("key3"));
    }

    @Test
    public void withInitialMapImmutable() throws Exception {

        Map<String, Object> initialMap = new HashMap<String, Object>();
        initialMap.put("key1", "val1");
        initialMap.put("key2", "val2");

        JsonObject obj = getJsonObjectFactory().create(initialMap, false);
        assertNotNull(obj);
        assertEquals(2, obj.size());
        assertEquals("val1", obj.getString("key1"));
        assertEquals("val2", obj.getString("key2"));

        try {
            obj.set("key3", "val3");
            fail();
        } catch (Exception ex) {
        }
        assertEquals(2, obj.size());
    }

    @Test
    public void withInitialObjMutable() throws Exception {

        JsonObject initialObj = getJsonManager().create();
        initialObj.set("key1", "val1");
        initialObj.set("key2", "val2");

        JsonObject obj = getJsonObjectFactory().create(initialObj, true);
        assertNotNull(obj);
        assertEquals(2, obj.size());
        assertEquals("val1", obj.getString("key1"));
        assertEquals("val2", obj.getString("key2"));

        obj.set("key3", "val3");
        assertEquals(3, obj.size());
        assertEquals("val3", obj.getString("key3"));
    }

    @Test
    public void withInitialObjImmutable() throws Exception {

        JsonObject initialObj = getJsonManager().create();
        initialObj.set("key1", "val1");
        initialObj.set("key2", "val2");

        JsonObject obj = getJsonObjectFactory().create(initialObj, false);
        assertNotNull(obj);
        assertEquals(2, obj.size());
        assertEquals("val1", obj.getString("key1"));
        assertEquals("val2", obj.getString("key2"));

        try {
            obj.set("key3", "val3");
            fail();
        } catch (Exception ex) {
        }
        assertEquals(2, obj.size());
    }

    @Test
    public void addIfDoesntExistJsonObject() throws Exception {

        JsonObject initialObj = getJsonManager().create();
        initialObj.set("key1", "val1");

        assertNull(initialObj.getJsonObject("toto"));
        JsonObject toto = initialObj.getJsonObjectOrEmpty("toto");
        assertNotNull(toto);
        assertNull(initialObj.getJsonObject("toto"));

        toto = initialObj.getJsonObjectOrEmpty("toto", true);
        assertNotNull(toto);
        assertNotNull(initialObj.getJsonObject("toto"));
    }

    @Test
    public void addIfDoesntExistJsonArray() throws Exception {

        JsonObject initialObj = getJsonManager().create();
        initialObj.set("key1", "val1");

        assertNull(initialObj.getJsonArray("toto"));
        JsonArray toto = initialObj.getJsonArrayOrEmpty("toto");
        assertNotNull(toto);
        assertNull(initialObj.getJsonArray("toto"));

        toto = initialObj.getJsonArrayOrEmpty("toto", true);
        assertNotNull(toto);
        assertNotNull(initialObj.getJsonArray("toto"));
    }

    @Test
    public void enumToFriendlyJsonObject() throws Exception {

        JsonObject jsonObj = getJsonManager().enumToFriendlyJsonObject(TestEnum.ONE);
        assertEquals(jsonObj.getString("name"), "ONE");
        assertEquals(jsonObj.getString("label"), "first value");

        jsonObj = getJsonManager().enumToFriendlyJsonObject(TestEnum.SECOND);
        assertEquals(jsonObj.getString("name"), "SECOND");
        assertEquals(jsonObj.getString("label"), "second value");
    }

    @Test
    public void enumsToFriendlyJsonArray() throws Exception {

        JsonArray jsonArr = getJsonManager().enumsToFriendlyJsonArray(TestEnum.values());

        assertEquals(jsonArr.getString("[0].name"), "ONE");
        assertEquals(jsonArr.getString("[0].label"), "first value");

        assertEquals(jsonArr.getString("[1].name"), "SECOND");
        assertEquals(jsonArr.getString("[1].label"), "second value");
    }

}
