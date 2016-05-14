package org.spincast.tests.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.junit.Test;
import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.json.IJsonArray;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.json.JsonObject;
import org.spincast.defaults.tests.DefaultTestingModule;
import org.spincast.shaded.org.apache.commons.codec.binary.Base64;
import org.spincast.testing.core.SpincastGuiceModuleBasedTestBase;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.google.inject.Inject;
import com.google.inject.Module;

public class JsonObjectsTest extends SpincastGuiceModuleBasedTestBase {

    @Inject
    protected IJsonManager jsonManager;

    @Override
    public Module getTestingModule() {
        return new DefaultTestingModule();
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

        String str = "{\"innerObj\":{\"someBoolean\":true,\"someInt\":123},\"anotherBoolean\":true,\"anotherInt\":44444}";

        IJsonObject jsonObj = this.jsonManager.fromJsonString(str, IJsonObject.class);
        assertNotNull(jsonObj);
        assertEquals(true, jsonObj.getBoolean("anotherBoolean"));
        assertEquals((Integer)44444, jsonObj.getInteger("anotherInt"));

        IJsonObject jsonObj2 = jsonObj.getJsonObject("innerObj");
        assertNotNull(jsonObj2);
        assertEquals(true, jsonObj2.getBoolean("someBoolean"));
        assertEquals((Integer)123, jsonObj2.getInteger("someInt"));

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

        Object object = jsonObj.get("key1");
        assertEquals("val1", object);

        object = jsonObj.get("key2");
        assertEquals(true, object);

        object = jsonObj.get("key3");
        assertEquals("true", object);

        object = jsonObj.get("key4");
        assertEquals(jsonObjInner, object);

        object = jsonObj.get("key5");
        assertEquals(123, object);

        object = jsonObj.get("key6");
        assertEquals("123", object);

        object = jsonObj.get("key7");
        assertEquals("12345678901234567890123456789012345678901234567890", object);

        try {
            jsonObj.getBigDecimal("nope");
            fail();
        } catch(Exception ex) {
        }

        object = jsonObj.get("nope", 123);
        assertEquals(123, object);

        object = jsonObj.get("nope", null);
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

        BigDecimal result = jsonObj.getBigDecimal("key5");
        assertNotNull(result);
        assertEquals(new BigDecimal(123), result);

        result = jsonObj.getBigDecimal("key6");
        assertNotNull(result);
        assertEquals(new BigDecimal(123), result);

        result = jsonObj.getBigDecimal("key7");
        assertNotNull(result);
        assertEquals(new BigDecimal("12345678901234567890123456789012345678901234567890"), result);

        try {
            jsonObj.getBigDecimal("nope");
            fail();
        } catch(Exception ex) {
        }

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

        Double result = jsonObj.getDouble("key5");
        assertNotNull(result);
        assertEquals(new Double(123), result);

        result = jsonObj.getDouble("key6");
        assertNotNull(result);
        assertEquals(new Double(123), result);

        result = jsonObj.getDouble("key7");
        assertNotNull(result);

        try {
            jsonObj.getDouble("nope");
            fail();
        } catch(Exception ex) {
        }

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
        } catch(Exception ex) {
        }

        try {
            jsonObj.getInteger("key2");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getInteger("key3");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getInteger("key4");
            fail();
        } catch(Exception ex) {
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
        } catch(Exception ex) {
        }

        try {
            jsonObj.getInteger("nope");
            fail();
        } catch(Exception ex) {
        }

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

        Long result = jsonObj.getLong("key5");
        assertNotNull(result);
        assertEquals(new Long(123), result);

        result = jsonObj.getLong("key6");
        assertNotNull(result);
        assertEquals(new Long(123), result);

        try {
            jsonObj.getLong("key7");
            fail();
        } catch(Exception ex) {
        }

        try {
            jsonObj.getLong("nope");
            fail();
        } catch(Exception ex) {
        }

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

        Float result = jsonObj.getFloat("key5");
        assertNotNull(result);
        assertEquals(new Float(123), result);

        result = jsonObj.getFloat("key6");
        assertNotNull(result);
        assertEquals(new Float(123), result);

        result = jsonObj.getFloat("key7");
        assertNotNull(result);
        assertEquals((Float)Float.POSITIVE_INFINITY, result);

        try {
            jsonObj.getFloat("nope");
            fail();
        } catch(Exception ex) {
        }

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

        Date result = jsonObj.getDate("key8");
        assertNotNull(result);
        assertEquals(someDate.toString(), result.toString());

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

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("key1", "val1");
        jsonObj.put("key2", true);
        jsonObj.put("key3", "true");
        jsonObj.put("key4", jsonObjInner);
        jsonObj.put("key5", 123);
        jsonObj.put("key6", "123");
        jsonObj.put("key7", "12345678901234567890123456789012345678901234567890");

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

        IJsonObject someObj = this.jsonManager.create();
        result = jsonObj.getJsonObject("nope", someObj);
        assertEquals(someObj, result);

        result = jsonObj.getJsonObject("nope", null);
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

        Field jsonManagerField = obj.getClass().getDeclaredField("jsonManager");
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

        Field jsonManagerField = obj.getClass().getDeclaredField("jsonManager");
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

        Field jsonManagerField = obj.getClass().getDeclaredField("jsonManager");
        assertNotNull(jsonManagerField);

        jsonManagerField.setAccessible(true);

        Object jsonManager = jsonManagerField.get(obj);
        assertNotNull(jsonManager);
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

}
