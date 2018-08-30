package org.spincast.tests.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.junit.Test;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.defaults.testing.NoAppTestingBase;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class ConvertToNativeTypeTest extends NoAppTestingBase {

    @Inject
    protected JsonManager jsonManager;

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    @Test
    public void nullTest() throws Exception {

        Object result = getJsonManager().convertToNativeType(null);
        assertEquals(null, result);
    }

    @Test
    public void nativeTypesString() throws Exception {
        Object result = getJsonManager().convertToNativeType("string");
        assertEquals("string", result);
    }

    @Test
    public void nativeTypesInteger() throws Exception {
        Object result = getJsonManager().convertToNativeType(123);
        assertEquals(new Integer(123), result);
    }

    @Test
    public void nativeTypesLong() throws Exception {
        Object result = getJsonManager().convertToNativeType(123L);
        assertEquals(new Long(123), result);
    }

    @Test
    public void nativeTypesFloat() throws Exception {
        Object result = getJsonManager().convertToNativeType(123.4F);
        assertEquals(new Float(123.4), result);
    }

    @Test
    public void nativeTypesDouble() throws Exception {
        Object result = getJsonManager().convertToNativeType(123.4D);
        assertEquals(new Double(123.4), result);
    }

    @Test
    public void nativeTypesBoolean() throws Exception {
        Object result = getJsonManager().convertToNativeType(true);
        assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void nativeTypesBigDecimal() throws Exception {
        Object result = getJsonManager().convertToNativeType(new BigDecimal(12345));
        assertEquals(new BigDecimal(12345), result);
    }

    @Test
    public void nativeTypesBytesArray() throws Exception {

        byte[] arr = new byte[]{11, 22, 33};

        Object result = getJsonManager().convertToNativeType(arr);
        assertEquals(arr, result);
    }

    @Test
    public void nativeTypesDate() throws Exception {

        Date now = new Date();

        Object result = getJsonManager().convertToNativeType(now);
        assertEquals(now, result);
    }

    @Test
    public void nativeTypesInstant() throws Exception {

        Instant now = Instant.now();

        Object result = getJsonManager().convertToNativeType(now);
        assertEquals(now, result);
    }

    @Test
    public void nativeTypesJsonObject() throws Exception {

        JsonObject jsonObj = getJsonManager().create();
        jsonObj.set("titi", 123);

        Object result = getJsonManager().convertToNativeType(jsonObj);
        assertEquals(jsonObj, result);
    }

    @Test
    public void nativeTypesJsonArray() throws Exception {

        JsonArray jsonArr = getJsonManager().createArray();
        jsonArr.add(123);

        Object result = getJsonManager().convertToNativeType(jsonArr);
        assertEquals(jsonArr, result);
    }

    @Test
    public void collectionsList() throws Exception {

        List<String> list = Lists.newArrayList("aaa", "bbb", "bbb");

        Object result = getJsonManager().convertToNativeType(list);
        assertTrue(result instanceof JsonArray);
        JsonArray resultArr = (JsonArray)result;
        assertEquals(3, resultArr.size());
        assertEquals("aaa", resultArr.getObject(0));
        assertEquals("bbb", resultArr.getObject(1));
        assertEquals("bbb", resultArr.getObject(2));
    }

    @Test
    public void collectionsSet() throws Exception {

        Set<String> set = Sets.newHashSet("aaa", "bbb", "bbb");

        Object result = getJsonManager().convertToNativeType(set);
        assertTrue(result instanceof JsonArray);
        JsonArray resultArr = (JsonArray)result;
        assertEquals(2, resultArr.size());
        assertEquals("aaa", resultArr.getObject(0));
        assertEquals("bbb", resultArr.getObject(1));
    }

    @Test
    public void arrays() throws Exception {

        String[] arr = new String[]{"aaa", "bbb"};

        Object result = getJsonManager().convertToNativeType(arr);
        assertTrue(result instanceof JsonArray);
        JsonArray resultArr = (JsonArray)result;
        assertEquals(2, resultArr.size());
        assertEquals("aaa", resultArr.getObject(0));
        assertEquals("bbb", resultArr.getObject(1));
    }

    enum EnumTest {
        AAA,
        BBB
    }

    @Test
    public void enums() throws Exception {
        Object result = getJsonManager().convertToNativeType(EnumTest.AAA);
        assertTrue(result instanceof String);
        assertEquals("AAA", result);
    }

    @Test
    public void otherTypesClass() throws Exception {
        Object result = getJsonManager().convertToNativeType(EnumTest.class);
        assertTrue(result instanceof String);
        assertEquals(EnumTest.class.getName(), result);
    }

    @Test
    public void otherTypesTimeZone() throws Exception {
        Object result = getJsonManager().convertToNativeType(TimeZone.getTimeZone("America/Los_Angeles"));
        assertTrue(result instanceof String);
        assertEquals("America/Los_Angeles", result);
    }

    @Test
    public void otherTypesException() throws Exception {

        Exception ex = new Exception("test123");

        Object result = getJsonManager().convertToNativeType(ex);
        assertTrue(result instanceof JsonObject);
        assertEquals("test123", ((JsonObject)result).getString("message"));
    }

    class MyClass {

        public String name;
        public int[] nbrs;
    }

    @Test
    public void otherTypesSimpleObject() throws Exception {

        MyClass obj = new MyClass();
        obj.name = "Stromgol";
        obj.nbrs = new int[]{1, 2, 3};

        Object result = getJsonManager().convertToNativeType(obj);
        assertTrue(result instanceof JsonObject);

        JsonObject resultObj = (JsonObject)result;
        assertEquals("Stromgol", resultObj.getString("name"));
        assertTrue(resultObj.isCanBeConvertedToJsonArray("nbrs"));
        assertEquals(new Integer(1), resultObj.getInteger("nbrs[0]"));
        assertEquals(new Integer(2), resultObj.getInteger("nbrs[1]"));
        assertEquals(new Integer(3), resultObj.getInteger("nbrs[2]"));
    }

}

