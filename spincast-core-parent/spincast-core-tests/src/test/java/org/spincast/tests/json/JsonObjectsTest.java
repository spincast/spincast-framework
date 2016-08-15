package org.spincast.tests.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import org.junit.Test;
import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.json.IJsonArray;
import org.spincast.core.json.IJsonArrayImmutable;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.json.Immutable;
import org.spincast.core.json.JsonObject;
import org.spincast.core.json.JsonObjectArrayBase;
import org.spincast.core.json.exceptions.CantConvertException;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.shaded.org.apache.commons.codec.binary.Base64;
import org.spincast.testing.core.SpincastTestBase;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class JsonObjectsTest extends SpincastTestBase {

    @Inject
    protected IJsonManager jsonManager;

    @Override
    protected Injector createInjector() {
        return Guice.createInjector(new SpincastDefaultTestingModule());
    }

    @Test
    public void toJsonString() throws Exception {

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("someBoolean", true);
        jsonObj.put("someInt", 123);

        IJsonObject jsonObj2 = this.jsonManager.create();
        jsonObj2.put("anotherBoolean", true);
        jsonObj2.put("anotherInt", 44444);
        jsonObj2.put("innerObj", jsonObj);

        String jsonStr = this.jsonManager.toJsonString(jsonObj2);
        assertNotNull(jsonStr);

        // The elements positions may not always be the same.
        assertEquals("{\"innerObj\":{\"someBoolean\":true,\"someInt\":123},\"anotherBoolean\":true,\"anotherInt\":44444}".length(),
                     jsonStr.length());

        @SuppressWarnings("unchecked")
        Map<String, Object> map = this.jsonManager.fromJsonString(jsonStr, Map.class);
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

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("someInt", 123);

        String jsonStr = this.jsonManager.toJsonString(jsonObj, true);
        assertNotNull(jsonStr);

        StringBuilder expected = new StringBuilder();

        expected.append("{\n");
        expected.append("    \"someInt\" : 123\n");
        expected.append("}");

        assertEquals(expected.toString(), jsonStr);

    }

    @Test
    public void toJsonStringBytes() throws Exception {

        IJsonObject jsonObj = this.jsonManager.create();
        byte[] bytes = SpincastTestUtils.TEST_STRING.getBytes("UTF-8");
        jsonObj.put("someBytes", bytes);

        String jsonStr = this.jsonManager.toJsonString(jsonObj);
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

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("someDate", someDate);

        String jsonStr = this.jsonManager.toJsonString(jsonObj);
        assertNotNull(jsonStr);
        assertEquals("{\"someDate\":\"2001-04-06T12:00+0000\"}", jsonStr);
    }

    @Test
    public void jsonObjectLoop() throws Exception {

        IJsonObject jsonObj2 = this.jsonManager.create();
        jsonObj2.put("anotherBoolean", true);
        jsonObj2.put("anotherInt", 44444);

        boolean boolFound = false;
        boolean intFound = false;
        for(Entry<String, Object> entry : jsonObj2) {
            assertNotNull(entry);

            String key = entry.getKey();
            if("anotherBoolean".equals(key)) {
                boolFound = true;
                assertEquals(true, entry.getValue());
            } else if("anotherInt".equals(key)) {
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

        IJsonObject jsonObj = this.jsonManager.create(str);
        assertNotNull(jsonObj);
        assertEquals(true, jsonObj.getBoolean("anotherBoolean"));
        assertEquals(new Integer(44444), jsonObj.getInteger("anotherInt"));

        IJsonObject jsonObj2 = jsonObj.getJsonObject("innerObj");
        assertNotNull(jsonObj2);
        assertEquals(true, jsonObj2.getBoolean("someBoolean"));
        assertEquals(new Integer(123), jsonObj2.getInteger("someInt"));

        String jsonString = jsonObj.toJsonString();
        assertEquals(str.length(), jsonString.length());
    }

    @Test
    public void getObject() throws Exception {

        IJsonObject jsonObjInner = this.jsonManager.create();

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("key1", "val1");
        jsonObj.put("key2", true);
        jsonObj.put("key3", "true");
        jsonObj.put("key4", jsonObjInner);
        jsonObj.put("key5", 123);
        jsonObj.put("key6", "123");
        jsonObj.put("key7", "12345678901234567890123456789012345678901234567890");

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

        IJsonObject jsonObjInner = this.jsonManager.create();

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("key1", "val1");
        jsonObj.put("key2", true);
        jsonObj.put("key3", "true");
        jsonObj.put("key4", jsonObjInner);
        jsonObj.put("key5", 123);
        jsonObj.put("key6", "123");
        jsonObj.put("key7", "12345678901234567890123456789012345678901234567890");

        try {
            jsonObj.getBigDecimal("key1");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getBigDecimal("key2");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getBigDecimal("key3");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getBigDecimal("key4");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getBigDecimal("key4", BigDecimal.ONE);
            fail();
        } catch(Exception ex) {
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

        IJsonObject jsonObjInner = this.jsonManager.create();

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("key1", "val1");
        jsonObj.put("key2", true);
        jsonObj.put("key3", "true");
        jsonObj.put("key4", jsonObjInner);
        jsonObj.put("key5", 123);
        jsonObj.put("key6", "123");
        jsonObj.put("key7", "12345678901234567890123456789012345678901234567890");

        jsonObj.put("keyA", "false");
        jsonObj.put("keyB", false);

        try {
            jsonObj.getBoolean("key1");
            fail();
        } catch(Exception ex) {
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
        } catch(Exception ex) {
        }

        try {
            jsonObj.getBoolean("key5");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getBoolean("key6");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getBoolean("key7");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getBoolean("key7", true);
            fail();
        } catch(Exception ex) {
        }

        result = jsonObj.getBoolean("nope", false);
        assertEquals(false, result);

        result = jsonObj.getBoolean("nope", null);
        assertEquals(null, result);

    }

    @Test
    public void getDouble() throws Exception {

        IJsonObject jsonObjInner = this.jsonManager.create();

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("key1", "val1");
        jsonObj.put("key2", true);
        jsonObj.put("key3", "true");
        jsonObj.put("key4", jsonObjInner);
        jsonObj.put("key5", 123);
        jsonObj.put("key6", "123");
        jsonObj.put("key7", "12345678901234567890123456789012345678901234567890");

        try {
            jsonObj.getDouble("key1");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getDouble("key2");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getDouble("key3");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getDouble("key4");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getDouble("key4", 123.4);
            fail();
        } catch(Exception ex) {
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

        IJsonObject jsonObjInner = this.jsonManager.create();

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("key1", "val1");
        jsonObj.put("key2", true);
        jsonObj.put("key3", "true");
        jsonObj.put("key4", jsonObjInner);
        jsonObj.put("key5", 123);
        jsonObj.put("key6", "123");
        jsonObj.put("key7", "12345678901234567890123456789012345678901234567890");

        try {
            jsonObj.getInteger("key1");
            fail();
        } catch(CantConvertException ex) {
        }

        try {
            jsonObj.getInteger("key2");
            fail();
        } catch(CantConvertException ex) {
        }

        try {
            jsonObj.getInteger("key3");
            fail();
        } catch(CantConvertException ex) {
        }

        try {
            jsonObj.getInteger("key4");
            fail();
        } catch(CantConvertException ex) {
        }

        try {
            jsonObj.getInteger("key4", 123);
            fail();
        } catch(CantConvertException ex) {
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
        } catch(CantConvertException ex) {
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

        IJsonObject jsonObjInner = this.jsonManager.create();

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("key1", "val1");
        jsonObj.put("key2", true);
        jsonObj.put("key3", "true");
        jsonObj.put("key4", jsonObjInner);
        jsonObj.put("key5", 123);
        jsonObj.put("key6", "123");
        jsonObj.put("key7", "12345678901234567890123456789012345678901234567890");

        try {
            jsonObj.getLong("key1");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getLong("key2");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getLong("key3");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getLong("key4");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getLong("key4", 123L);
            fail();
        } catch(Exception ex) {
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
        } catch(CantConvertException ex) {
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

        IJsonObject jsonObjInner = this.jsonManager.create();

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("key1", "val1");
        jsonObj.put("key2", true);
        jsonObj.put("key3", "true");
        jsonObj.put("key4", jsonObjInner);
        jsonObj.put("key5", 123);
        jsonObj.put("key6", "123");
        jsonObj.put("key7", "12345678901234567890123456789012345678901234567890");

        try {
            jsonObj.getFloat("key1");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getFloat("key2");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getFloat("key3");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getFloat("key4");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getFloat("key4", 123.4F);
            fail();
        } catch(Exception ex) {
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

        IJsonObject jsonObjInner = this.jsonManager.create();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2001);
        cal.set(Calendar.MONTH, 3);
        cal.set(Calendar.DATE, 6);
        cal.set(Calendar.HOUR_OF_DAY, 12);

        Date someDate = cal.getTime();

        ISO8601DateFormat df = new ISO8601DateFormat();
        String someDateISO8601 = df.format(someDate);

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("key1", "val1");
        jsonObj.put("key2", true);
        jsonObj.put("key3", "true");
        jsonObj.put("key4", jsonObjInner);
        jsonObj.put("key5", 123);
        jsonObj.put("key6", "123");
        jsonObj.put("key7", "12345678901234567890123456789012345678901234567890");
        jsonObj.put("key8", someDateISO8601);

        try {
            jsonObj.getDate("key1");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getDate("key2");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getDate("key3");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getDate("key4");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getDate("key5");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getDate("key6");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getDate("key7");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getDate("key7", new Date());
            fail();
        } catch(Exception ex) {
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

    }

    @Test
    public void getString() throws Exception {

        IJsonObject jsonObjInner = this.jsonManager.create();

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("key1", "val1");
        jsonObj.put("key2", true);
        jsonObj.put("key3", "true");
        jsonObj.put("key4", jsonObjInner);
        jsonObj.put("key5", 123);
        jsonObj.put("key6", "123");
        jsonObj.put("key7", "12345678901234567890123456789012345678901234567890");

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

        IJsonObject jsonObjInner = this.jsonManager.create();

        String base64String = Base64.encodeBase64String(SpincastTestUtils.TEST_STRING.getBytes("UTF-8"));

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("key1", "val1");
        jsonObj.put("key2", true);
        jsonObj.put("key3", "true");
        jsonObj.put("key4", jsonObjInner);
        jsonObj.put("key5", 123);
        jsonObj.put("key6", "123");
        jsonObj.put("key7", "12345678901234567890123456789012345678901234567890");
        jsonObj.put("key8", base64String);

        byte[] result = jsonObj.getBytesFromBase64String("key1");
        assertNotNull(result);

        result = jsonObj.getBytesFromBase64String("key2");
        assertNotNull(result);

        result = jsonObj.getBytesFromBase64String("key3");
        assertNotNull(result);

        result = jsonObj.getBytesFromBase64String("key4");
        assertNotNull(result);

        result = jsonObj.getBytesFromBase64String("key5");
        assertNotNull(result);

        result = jsonObj.getBytesFromBase64String("key6");
        assertNotNull(result);

        result = jsonObj.getBytesFromBase64String("key7");
        assertNotNull(result);

        result = jsonObj.getBytesFromBase64String("key8");
        assertNotNull(result);
        byte[] bytes = SpincastTestUtils.TEST_STRING.getBytes("UTF-8");
        for(int i = 0; i < bytes.length; i++) {
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

        IJsonObject jsonObjInner = this.jsonManager.create();
        IJsonArray jsonArrayInner = this.jsonManager.createArray();

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("key1", "val1");
        jsonObj.put("key2", true);
        jsonObj.put("key3", "true");
        jsonObj.put("key4", jsonObjInner);
        jsonObj.put("key5", 123);
        jsonObj.put("key6", "123");
        jsonObj.put("key7", "12345678901234567890123456789012345678901234567890");
        jsonObj.put("key8", jsonArrayInner);

        try {
            jsonObj.getJsonObject("key1");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getJsonObject("key2");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getJsonObject("key3");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getJsonObject("key3", null);
            fail();
        } catch(Exception ex) {
        }

        IJsonObject result = jsonObj.getJsonObject("key4");
        assertNotNull(result);
        assertEquals(jsonObjInner, result);

        try {
            jsonObj.getJsonObject("key5");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getJsonObject("key6");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getJsonObject("key7");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getJsonObject("key8");
            fail();
        } catch(Exception ex) {
        }

        IJsonObject someObj = this.jsonManager.create();
        result = jsonObj.getJsonObject("nope", someObj);
        assertEquals(someObj, result);

        result = jsonObj.getJsonObject("nope", null);
        assertEquals(null, result);
    }

    @Test
    public void getJsonArray() throws Exception {

        IJsonObject jsonObjInner = this.jsonManager.create();
        IJsonArray jsonArrayInner = this.jsonManager.createArray();

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("key1", "val1");
        jsonObj.put("key2", true);
        jsonObj.put("key3", "true");
        jsonObj.put("key4", jsonObjInner);
        jsonObj.put("key5", 123);
        jsonObj.put("key6", "123");
        jsonObj.put("key7", "12345678901234567890123456789012345678901234567890");
        jsonObj.put("key8", jsonArrayInner);

        try {
            jsonObj.getJsonArray("key1");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getJsonArray("key2");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getJsonArray("key3");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getJsonArray("key3", null);
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getJsonArray("key4");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getJsonArray("key5");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getJsonArray("key6");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getJsonArray("key7");
            fail();
        } catch(Exception ex) {
        }

        IJsonArray result = jsonObj.getJsonArray("key8");
        assertNotNull(result);
        assertEquals(jsonArrayInner, result);

        IJsonArray someArray = this.jsonManager.createArray();
        result = jsonObj.getJsonArray("nope", someArray);
        assertEquals(someArray, result);

        result = jsonObj.getJsonArray("nope", null);
        assertEquals(null, result);
    }

    @Test
    public void jsonObjectArraySerialize() throws Exception {

        IJsonArray jsonArray = this.jsonManager.createArray();
        jsonArray.add(this.jsonManager.create());
        jsonArray.add(123);
        jsonArray.add("abc");
        jsonArray.add(this.jsonManager.create());

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("key1", "test");
        jsonObj.put("someArray", jsonArray);

        String jsonString = jsonObj.toJsonString();
        assertEquals("{\"key1\":\"test\",\"someArray\":[{},123,\"abc\",{}]}".length(), jsonString.length());

    }

    @Test
    public void jsonObjectArrayDeserialize() throws Exception {

        String str = "{\"someArray\":[{},123,\"abc\",[1, {}], \"123456\"]}";

        IJsonObject jsonObj = this.jsonManager.create(str);

        IJsonArray jsonArray = jsonObj.getJsonArray("someArray");
        assertNotNull(jsonArray);

        IJsonObject jsonObject = jsonArray.getJsonObject(0);
        assertNotNull(jsonObject);

        Integer integer = jsonArray.getInteger(1);
        assertNotNull(integer);

        Long longVal = jsonArray.getLong(1);
        assertNotNull(longVal);

        BigDecimal bd = jsonArray.getBigDecimal(1);
        assertNotNull(bd);

        String string = jsonArray.getString(2);
        assertNotNull(string);

        IJsonArray array = jsonArray.getJsonArray(3);
        assertNotNull(array);

        Integer int1 = array.getInteger(0);
        assertNotNull(int1);

        IJsonObject o1 = array.getJsonObject(1);
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

        IJsonObject jsonObj = this.jsonManager.create();

        IJsonArray jsonArray = this.jsonManager.createArray();
        jsonArray.add(jsonObj);
        jsonArray.add(123);
        jsonArray.add("abc");

        int nbr = 0;
        for(Object element : jsonArray) {
            assertNotNull(element);
            if(nbr == 0) {
                assertEquals(jsonObj, element);
            } else if(nbr == 1) {
                assertEquals(123, element);
            } else if(nbr == 2) {
                assertEquals("abc", element);
            }
            nbr++;
        }
        assertEquals(3, nbr);
    }

    protected static class TestObject {

        @JsonIgnore
        @Inject
        protected ISpincastConfig spincastConfig;

        protected String name;

        public TestObject() {
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ISpincastConfig getSpincastConfig() {
            return this.spincastConfig;
        }

    }

    @Test
    public void dependenciesInjectionOnDeserializationFromString() throws Exception {

        TestObject obj = new TestObject();
        obj.setName("test");
        assertNull(obj.getSpincastConfig());

        String jsonString = this.jsonManager.toJsonString(obj);
        assertNotNull(jsonString);

        obj = this.jsonManager.fromJsonString(jsonString, TestObject.class);
        assertNotNull(obj);
        assertNotNull(obj.getSpincastConfig());
        assertEquals("test", obj.getName());
    }

    @Test
    public void dependenciesInjectionOnDeserializationFromInputStream() throws Exception {

        InputStream stream = getClass().getClassLoader().getResourceAsStream("obj.json");
        assertNotNull(stream);

        TestObject obj = this.jsonManager.fromJsonInputStream(stream, TestObject.class);
        assertNotNull(obj);
        assertNotNull(obj.getSpincastConfig());
        assertEquals("test", obj.getName());
    }

    @Test
    public void dependenciesInjectionOnCreate() throws Exception {

        IJsonObject obj = this.jsonManager.create();
        assertNotNull(obj);

        assertTrue(obj instanceof JsonObject);

        Field jsonManagerField = JsonObjectArrayBase.class.getDeclaredField("jsonManager");
        assertNotNull(jsonManagerField);

        jsonManagerField.setAccessible(true);

        Object jsonManager = jsonManagerField.get(obj);
        assertNotNull(jsonManager);
    }

    @Test
    public void dependenciesInjectionOnCreateFromString() throws Exception {

        IJsonObject obj = this.jsonManager.create("{\"name\":\"test\"}");
        assertNotNull(obj);

        assertTrue(obj instanceof JsonObject);

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

        IJsonObject obj = this.jsonManager.create(stream);
        assertNotNull(obj);

        assertTrue(obj instanceof JsonObject);

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

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("aaa", "bbb");
        jsonObj.putConvert("custom", obj);

        String jsonString = jsonObj.toJsonString();
        assertNotNull(jsonString);

        jsonObj = this.jsonManager.create(jsonString);
        assertNotNull(jsonObj);
        assertEquals("bbb", jsonObj.getString("aaa"));
        IJsonObject obj2 = jsonObj.getJsonObject("custom");
        assertNotNull(obj2);
        assertEquals("test", obj2.getString("name"));
    }

    @Test
    public void arrayConvertedToJsonArray() throws Exception {

        String[] array = new String[]{"aaa", "bbb"};

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.putConvert("array", array);

        IJsonArray result = jsonObj.getJsonArray("array");
        assertNotNull(result);
        assertEquals("aaa", result.getString(0));
        assertEquals("bbb", result.getString(1));
    }

    @Test
    public void collectionConvertedToJsonArray() throws Exception {

        List<String> list = new ArrayList<String>();
        list.add("aaa");
        list.add("bbb");

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.putConvert("array", list);

        IJsonArray result = jsonObj.getJsonArray("array");
        assertNotNull(result);
        assertEquals("aaa", result.getString(0));
        assertEquals("bbb", result.getString(1));
    }

    @Test
    public void collectionConvertedToJsonArray2() throws Exception {

        Set<String> list = new HashSet<String>();
        list.add("aaa");
        list.add("bbb");

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.putConvert("array", list);

        IJsonArray result = jsonObj.getJsonArray("array");
        assertNotNull(result);
        assertEquals("aaa", result.getString(0));
        assertEquals("bbb", result.getString(1));
    }

    @Test
    public void mapConvertedToJsonObject() throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("key1", "val1");
        map.put("key2", "val2");

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.putConvert("inner", map);

        IJsonObject result = jsonObj.getJsonObject("inner");
        assertNotNull(result);
        assertEquals("val1", result.getString("key1"));
        assertEquals("val2", result.getString("key2"));
    }

    @Test
    public void jsonArrayArrayDeserialize() throws Exception {

        String str = "[{},123,\"abc\",[1, {}], \"123456\"]";

        IJsonArray jsonArray = this.jsonManager.createArray(str);
        assertNotNull(jsonArray);

        Integer integer = jsonArray.getInteger(1);
        assertNotNull(integer);

        Long longVal = jsonArray.getLong(1);
        assertNotNull(longVal);

        BigDecimal bd = jsonArray.getBigDecimal(1);
        assertNotNull(bd);

        String string = jsonArray.getString(2);
        assertNotNull(string);

        IJsonArray array = jsonArray.getJsonArray(3);
        assertNotNull(array);

        Integer int1 = array.getInteger(0);
        assertNotNull(int1);

        IJsonObject o1 = array.getJsonObject(1);
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

        IJsonArray jsonArray = this.jsonManager.createArray(stream);
        assertNotNull(jsonArray);

        Integer integer = jsonArray.getInteger(1);
        assertNotNull(integer);

        Long longVal = jsonArray.getLong(1);
        assertNotNull(longVal);

        BigDecimal bd = jsonArray.getBigDecimal(1);
        assertNotNull(bd);

        String string = jsonArray.getString(2);
        assertNotNull(string);

        IJsonArray array = jsonArray.getJsonArray(3);
        assertNotNull(array);

        Integer int1 = array.getInteger(0);
        assertNotNull(int1);

        IJsonObject o1 = array.getJsonObject(1);
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

        IJsonObject jsonObj = this.jsonManager.create("{}");
        assertNotNull(jsonObj);

        String jsonString = this.jsonManager.toJsonString(new NoPropToSerialize());
        assertNotNull(jsonString);

        IJsonArray jsonArray = this.jsonManager.createArray("[]");
        assertNotNull(jsonArray);
        jsonString = this.jsonManager.toJsonString(new NoPropToSerialize[0]);
        assertNotNull(jsonString);
    }

    @Test
    public void getArray() throws Exception {

        IJsonArray array = this.jsonManager.createArray();
        array.add("toto");
        array.add("titi");

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("arr", array);

        IJsonArray jsonArray = jsonObj.getJsonArray("arr");
        assertNotNull(jsonArray);
        assertEquals(2, jsonArray.size());
        assertEquals("toto", jsonArray.getString(0));
        assertEquals("titi", jsonArray.getString(1));
    }

    @Test
    public void getArrayFirstString() throws Exception {

        IJsonArray array = this.jsonManager.createArray();
        array.add("toto");
        array.add("titi");

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("arr", array);

        String arrayFirst = jsonObj.getArrayFirstString("arr");
        assertNotNull(arrayFirst);
        assertEquals("toto", arrayFirst);

        String arrayFirstString = jsonObj.getArrayFirstString("arr");
        assertNotNull(arrayFirstString);
        assertEquals("toto", arrayFirstString);
    }

    @Test
    public void getArrayFirstInteger() throws Exception {

        IJsonArray array = this.jsonManager.createArray();
        array.add(123);
        array.add("titi");

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("arr", array);

        Integer arrayFirst = jsonObj.getArrayFirstInteger("arr");
        assertNotNull(arrayFirst);
        assertEquals(new Integer(123), arrayFirst);
    }

    @Test
    public void getArrayFirstLong() throws Exception {

        IJsonArray array = this.jsonManager.createArray();
        array.add(123L);
        array.add("titi");

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("arr", array);

        Long arrayFirst = jsonObj.getArrayFirstLong("arr");
        assertNotNull(arrayFirst);
        assertEquals(new Long(123), arrayFirst);
    }

    @Test
    public void getArrayFirstFloat() throws Exception {

        IJsonArray array = this.jsonManager.createArray();
        array.add(123F);
        array.add("titi");

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("arr", array);

        Float arrayFirst = jsonObj.getArrayFirstFloat("arr");
        assertNotNull(arrayFirst);
        assertEquals(new Float(123), arrayFirst);
    }

    @Test
    public void getArrayFirstDouble() throws Exception {

        IJsonArray array = this.jsonManager.createArray();
        array.add(123);
        array.add("titi");

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("arr", array);

        Double arrayFirst = jsonObj.getArrayFirstDouble("arr");
        assertNotNull(arrayFirst);
        assertEquals(new Double(123), arrayFirst);
    }

    @Test
    public void getArrayFirstBoolean() throws Exception {

        IJsonArray array = this.jsonManager.createArray();
        array.add(true);
        array.add("titi");

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("arr", array);

        Boolean arrayFirst = jsonObj.getArrayFirstBoolean("arr");
        assertNotNull(arrayFirst);
        assertEquals(true, arrayFirst);
    }

    @Test
    public void getArrayFirstBigDecimal() throws Exception {

        IJsonArray array = this.jsonManager.createArray();
        array.add(new BigDecimal(123));
        array.add("titi");

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("arr", array);

        BigDecimal arrayFirst = jsonObj.getArrayFirstBigDecimal("arr");
        assertNotNull(arrayFirst);
        assertEquals(new BigDecimal(123), arrayFirst);
    }

    @Test
    public void getArrayFirstDate() throws Exception {

        IJsonArray array = this.jsonManager.createArray();
        array.add(new Date());
        array.add("titi");

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("arr", array);

        Date arrayFirst = jsonObj.getArrayFirstDate("arr");
        assertNotNull(arrayFirst);
    }

    @Test
    public void getArrayFirstByteArray() throws Exception {

        IJsonArray array = this.jsonManager.createArray();
        array.add("test".getBytes("UTF-8"));
        array.add("titi");

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("arr", array);

        byte[] arrayFirst = jsonObj.getArrayFirstBytesFromBase64String("arr");
        assertNotNull(arrayFirst);
        assertTrue(Arrays.equals("test".getBytes("UTF-8"), arrayFirst));
    }

    @Test
    public void getArrayFirstElementDefaultValue() throws Exception {

        IJsonObject jsonObj = this.jsonManager.create();

        String arrayFirstString = jsonObj.getArrayFirstString("nope", "titi");
        assertNotNull(arrayFirstString);
        assertEquals("titi", arrayFirstString);
    }

    @Test
    public void getArrayFirstElementNoDefaultValue() throws Exception {

        IJsonObject jsonObj = this.jsonManager.create();

        String result = jsonObj.getArrayFirstString("nope");
        assertNull(result);
    }

    @Test
    public void getArrayFirstElementAsJsonObject() throws Exception {

        IJsonObject inner = this.jsonManager.create();
        inner.put("name", "inner");

        IJsonArray array = this.jsonManager.createArray();
        array.add(inner);
        array.add("titi");

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("arr", array);

        IJsonObject arrayFirstJsonObject = jsonObj.getArrayFirstJsonObject("arr");
        assertNotNull(arrayFirstJsonObject);
        assertEquals("inner", arrayFirstJsonObject.getString("name"));
    }

    @Test
    public void getArrayFirstElementAsJsonArray() throws Exception {

        IJsonArray inner = this.jsonManager.createArray();
        inner.add("inner");

        IJsonArray array = this.jsonManager.createArray();
        array.add(inner);
        array.add("titi");

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("arr", array);

        IJsonArray arrayFirstJsonArray = jsonObj.getArrayFirstJsonArray("arr");
        assertNotNull(arrayFirstJsonArray);
        assertEquals("inner", arrayFirstJsonArray.getString(0));
    }

    @Test
    public void getArrayFirstElementAsJsonArrayDefaultValue() throws Exception {

        IJsonArray array = this.jsonManager.createArray();
        array.add("toto");
        array.add("titi");

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("yo", "yeah");

        IJsonArray arrayFirstJsonArray = jsonObj.getArrayFirstJsonArray("nope", array);
        assertNotNull(arrayFirstJsonArray);
        assertEquals("toto", array.getString(0));
        assertEquals("titi", array.getString(1));
    }

    @Test
    public void getArrayFirstElementAsJsonArrayNoDefaultValue() throws Exception {

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("yo", "yeah");

        IJsonArray result = jsonObj.getArrayFirstJsonArray("nope");
        assertNull(result);
    }

    @Test
    public void getRealArrayFirstElement() throws Exception {

        String[] inner = new String[]{"titi"};

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.putConvert("arr", inner);

        String first = jsonObj.getArrayFirstString("arr");
        assertNotNull(first);
        assertEquals("titi", first);
    }

    @Test
    public void getRealArrayFirstElementString() throws Exception {

        String[] inner = new String[]{"titi"};

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.putConvert("arr", inner);

        String first = jsonObj.getArrayFirstString("arr");
        assertNotNull(first);
        assertEquals("titi", first);
    }

    @Test
    public void getRealArrayFirstElementJsonObject() throws Exception {

        IJsonObject inner = this.jsonManager.create();
        inner.put("yo", "yeah");

        IJsonObject[] arr = new IJsonObject[]{inner};

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.putConvert("arr", arr);

        IJsonObject first = jsonObj.getArrayFirstJsonObject("arr");
        assertNotNull(first);
        assertEquals("yeah", first.getString("yo"));
    }

    @Test
    public void getListFirstString() throws Exception {

        List<String> inner = Lists.newArrayList("titi");

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.putConvert("list", inner);

        String first = jsonObj.getArrayFirstString("list");
        assertNotNull(first);
        assertEquals("titi", first);
    }

    @Test
    public void getMapFirstElementAsJsonObjectInvalid() throws Exception {

        Map<String, String> map = new HashMap<String, String>();
        map.put("name", "Stromgol");

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.putConvert("map", map);

        try {
            jsonObj.getArrayFirstJsonObject("map");
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void mapIsConvertedToJsonObject() throws Exception {

        Map<String, String> map = new HashMap<String, String>();
        map.put("name", "Stromgol");

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.putConvert("map", map);

        IJsonObject map2 = jsonObj.getJsonObject("map");
        assertNotNull(map2);

        assertTrue(map2.isKeyExists("name"));
        assertEquals("Stromgol", map2.getString("name"));
    }

    @Test
    public void getFirstDefaultValueEmptyArray() throws Exception {

        IJsonArray emptyArray = this.jsonManager.createArray();

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("array", emptyArray);

        String result = jsonObj.getArrayFirstString("array", "default");
        assertNotNull(result);
        assertEquals("default", result);
    }

    @Test
    public void getFirstDefaultValueInvalidConvertion() throws Exception {

        IJsonArray array = this.jsonManager.createArray();
        array.add("someString");

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("array", array);

        try {
            jsonObj.getArrayFirstInteger("array", 123);
            fail();
        } catch(CantConvertException ex) {
            return;
        }
        fail();
    }

    @Test
    public void getArrayElementInvalidIndexDefaultValue() throws Exception {

        IJsonArray array = this.jsonManager.createArray();
        array.add("aaa");

        String result = array.getString(3, "default");
        assertNotNull(result);
        assertEquals("default", result);
    }

    @Test
    public void getArrayElementInvalidIndexNoDefaultValue() throws Exception {

        IJsonArray array = this.jsonManager.createArray();
        array.add("aaa");

        String result = array.getString(3);
        assertNull(result);
    }

    @Test
    public void getArrayElementInvalidConversion() throws Exception {

        IJsonArray array = this.jsonManager.createArray();
        array.add("aaa");

        try {
            array.getInteger(0);
            fail();
        } catch(CantConvertException ex) {
            return;
        }
        fail();
    }

    @Test
    public void getArrayElementInvalidConversionDefaultValue() throws Exception {

        IJsonArray array = this.jsonManager.createArray();
        array.add("aaa");

        try {
            array.getInteger(0, 123);
            fail();
        } catch(CantConvertException ex) {
            return;
        }
        fail();
    }

    @Test
    public void getOrEmpty() throws Exception {

        IJsonObject jsonObj = this.jsonManager.create();

        String result = jsonObj.getJsonArrayOrEmpty("nope")
                               .getJsonObjectOrEmpty(5)
                               .getJsonArrayOrEmpty("nope")
                               .getJsonObjectOrEmpty(5)
                               .getString("nope");
        assertNull(result);
    }

    @Test
    public void putArrayConvertNoClone() throws Exception {

        IJsonArray array = this.jsonManager.createArray();
        array.add("aaa");

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.putConvert("array", array);

        IJsonArray innerArray = jsonObj.getJsonArray("array");
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

        IJsonArray array = this.jsonManager.createArray();
        array.add("aaa");

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.putConvert("array", array, true);

        IJsonArray innerArray = jsonObj.getJsonArray("array");
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

        IJsonObject obj = this.jsonManager.create();
        obj.put("key1", "val1");

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.putConvert("inner", obj);

        IJsonObject innerObj = jsonObj.getJsonObject("inner");
        assertEquals("val1", innerObj.getString("key1"));
        assertEquals("val1", obj.getString("key1"));
        assertNull(innerObj.getString("key2"));
        assertNull(obj.getString("key2"));
        assertNull(innerObj.getString("key3"));
        assertNull(obj.getString("key3"));

        innerObj.put("key2", "val2");
        assertEquals("val1", innerObj.getString("key1"));
        assertEquals("val1", obj.getString("key1"));
        assertEquals("val2", innerObj.getString("key2"));
        assertEquals("val2", obj.getString("key2"));
        assertNull(innerObj.getString("key3"));
        assertNull(obj.getString("key3"));

        obj.put("key3", "val3");
        assertEquals("val1", innerObj.getString("key1"));
        assertEquals("val1", obj.getString("key1"));
        assertEquals("val2", innerObj.getString("key2"));
        assertEquals("val2", obj.getString("key2"));
        assertEquals("val3", innerObj.getString("key3"));
        assertEquals("val3", obj.getString("key3"));
    }

    @Test
    public void putJsonObjectConvertClone() throws Exception {

        IJsonObject obj = this.jsonManager.create();
        obj.put("key1", "val1");

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.putConvert("inner", obj, true);

        IJsonObject innerObj = jsonObj.getJsonObject("inner");
        assertEquals("val1", innerObj.getString("key1"));
        assertEquals("val1", obj.getString("key1"));
        assertNull(innerObj.getString("key2"));
        assertNull(obj.getString("key2"));
        assertNull(innerObj.getString("key3"));
        assertNull(obj.getString("key3"));

        innerObj.put("key2", "val2");
        assertEquals("val1", innerObj.getString("key1"));
        assertEquals("val2", innerObj.getString("key2"));
        assertNull(obj.getString("key2"));
        assertNull(innerObj.getString("key3"));
        assertNull(obj.getString("key3"));

        obj.put("key3", "val3");
        assertEquals("val1", innerObj.getString("key1"));
        assertEquals("val1", obj.getString("key1"));
        assertEquals("val2", innerObj.getString("key2"));
        assertNull(obj.getString("key2"));
        assertNull(innerObj.getString("key3"));
        assertEquals("val3", obj.getString("key3"));
    }

    @Test
    public void arrayClone() throws Exception {

        IJsonArray array = this.jsonManager.createArray();
        array.add("aaa");

        IJsonArray clone = array.clone();
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

        IJsonArray inner = this.jsonManager.createArray();

        IJsonArray array = this.jsonManager.createArray();
        array.add("aaa");
        array.add(inner);

        IJsonArray cloneImmutable = array.clone(false);
        assertTrue(cloneImmutable instanceof IJsonArrayImmutable);
        assertTrue(cloneImmutable instanceof Immutable);

        assertEquals(2, cloneImmutable.size());
        assertEquals("aaa", cloneImmutable.getString(0));

        IJsonArray cloneInnerArray = cloneImmutable.getJsonArray(1);
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
        } catch(Exception ex) {
        }

        try {
            cloneInnerArray.add("eee");
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void jsonObjectClone() throws Exception {

        IJsonObject obj = this.jsonManager.create();
        obj.put("key1", "val1");

        IJsonObject clone = obj.clone();
        assertEquals("val1", clone.getString("key1"));
        assertEquals("val1", obj.getString("key1"));
        assertNull(clone.getString("key2"));
        assertNull(obj.getString("key2"));
        assertNull(clone.getString("key3"));
        assertNull(obj.getString("key3"));

        clone.put("key2", "val2");
        assertEquals("val1", clone.getString("key1"));
        assertEquals("val2", clone.getString("key2"));
        assertNull(obj.getString("key2"));
        assertNull(clone.getString("key3"));
        assertNull(obj.getString("key3"));

        obj.put("key3", "val3");
        assertEquals("val1", clone.getString("key1"));
        assertEquals("val1", obj.getString("key1"));
        assertEquals("val2", clone.getString("key2"));
        assertNull(obj.getString("key2"));
        assertNull(clone.getString("key3"));
        assertEquals("val3", obj.getString("key3"));
    }

    @Test
    public void jsonObjectCloneImmutable() throws Exception {

        IJsonObject inner = this.jsonManager.create();

        IJsonObject obj = this.jsonManager.create();
        obj.put("key1", "val1");
        obj.put("inner", inner);

        IJsonObject clone = obj.clone(false);
        assertEquals("val1", clone.getString("key1"));
        assertEquals("val1", obj.getString("key1"));
        assertNull(clone.getString("key2"));
        assertNull(obj.getString("key2"));
        IJsonObject innerClone = clone.getJsonObject("inner");

        assertNull(inner.getString("innerKey"));
        assertNull(innerClone.getString("innerKey"));

        obj.put("key2", "val2");
        assertEquals("val1", clone.getString("key1"));
        assertEquals("val1", obj.getString("key1"));
        assertNull(clone.getString("key2"));
        assertEquals("val2", obj.getString("key2"));

        inner.put("innerKey", "innerValue");
        assertEquals("innerValue", inner.getString("innerKey"));
        assertNull(innerClone.getString("innerKey"));

        try {
            clone.put("key3", "val3");
            fail();
        } catch(Exception ex) {
        }

        try {
            innerClone.put("key4", "val4");
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void arrayRemoveElement() throws Exception {

        IJsonArray array = this.jsonManager.createArray();
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

        IJsonArray array = this.jsonManager.createArray();

        IJsonArray immutableArray = array.clone(false);

        try {
            immutableArray.add("nope");
            fail();
        } catch(Exception ex) {
        }

        IJsonObject obj = this.jsonManager.create();
        obj.put("array", immutableArray);

        try {
            immutableArray.add("nope");
            fail();
        } catch(Exception ex) {
        }

        IJsonArray innerArray = obj.getJsonArray("array");
        innerArray.add("yes!");

        String result = obj.getArrayFirstString("array");
        assertEquals("yes!", result);
    }

    @Test
    public void addImmutableArrayInMutableArray() throws Exception {

        IJsonArray array = this.jsonManager.createArray();

        IJsonArray immutableArray = array.clone(false);

        try {
            immutableArray.add("nope");
            fail();
        } catch(Exception ex) {
        }

        IJsonArray parent = this.jsonManager.createArray();
        parent.add(immutableArray);

        try {
            immutableArray.add("nope");
            fail();
        } catch(Exception ex) {
        }

        IJsonArray innerArray = parent.getJsonArray(0);
        innerArray.add("yes!");

        String result = innerArray.getString(0);
        assertEquals("yes!", result);
    }

    @Test
    public void addImmutableObjectInMutableJsonObject() throws Exception {

        IJsonObject obj2 = this.jsonManager.create();

        IJsonObject immutableObj = obj2.clone(false);

        try {
            immutableObj.put("nope", "I don't think so");
            fail();
        } catch(Exception ex) {
        }

        IJsonObject obj = this.jsonManager.create();
        obj.put("inner", immutableObj);

        try {
            immutableObj.put("nope", "I don't think so");
            fail();
        } catch(Exception ex) {
        }

        IJsonObject innerObj = obj.getJsonObject("inner");
        innerObj.put("key1", "yes!");

    }

    @Test
    public void addImmutableObjectInMutableJsonArray() throws Exception {

        IJsonObject obj2 = this.jsonManager.create();

        IJsonObject immutableObj = obj2.clone(false);

        try {
            immutableObj.put("nope", "I don't think so");
            fail();
        } catch(Exception ex) {
        }

        IJsonArray array = this.jsonManager.createArray();
        array.add(immutableObj);

        try {
            immutableObj.put("nope", "I don't think so");
            fail();
        } catch(Exception ex) {
        }

        IJsonObject innerObj = array.getJsonObject(0);
        innerObj.put("key1", "yes!");

    }

}
