package org.spincast.plugins.gson.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import org.junit.Test;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonObject;
import org.spincast.plugins.gson.tests.utils.GsonTestBase;
import org.spincast.shaded.org.apache.commons.lang3.time.DateUtils;

public class JsonPathsSelectTest extends GsonTestBase {

    @Test
    public void directString() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.set("key1", "value1");

        String result = obj.getString("key1");
        assertEquals("value1", result);

        result = obj.getString(".key1");
        assertEquals("value1", result);

        result = obj.getString("['key1']");
        assertEquals("value1", result);

        result = obj.getString("[\"key1\"]");
        assertEquals("value1", result);

        result = obj.getString("nope", "defaultVal");
        assertEquals("defaultVal", result);
    }

    @Test
    public void directInteger() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.set("key1", 123);

        Integer result = obj.getInteger("key1");
        assertEquals(Integer.valueOf(123), result);

        result = obj.getInteger("['key1']");
        assertEquals(Integer.valueOf(123), result);

        result = obj.getInteger("[\"key1\"]");
        assertEquals(Integer.valueOf(123), result);

        result = obj.getInteger("nope", 456);
        assertEquals(Integer.valueOf(456), result);
    }

    @Test
    public void directLong() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.set("key1", 123L);

        Long result = obj.getLong("key1");
        assertEquals(Long.valueOf(123), result);

        result = obj.getLong("['key1']");
        assertEquals(Long.valueOf(123), result);

        result = obj.getLong("[\"key1\"]");
        assertEquals(Long.valueOf(123), result);

        result = obj.getLong("nope", 456L);
        assertEquals(Long.valueOf(456), result);
    }

    @Test
    public void directFloat() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.set("key1", 12.34F);

        Float result = obj.getFloat("key1");
        assertEquals(Float.valueOf("12.34"), result);

        result = obj.getFloat("['key1']");
        assertEquals(Float.valueOf("12.34"), result);

        result = obj.getFloat("[\"key1\"]");
        assertEquals(Float.valueOf("12.34"), result);

        result = obj.getFloat("nope", 56.78F);
        assertEquals(Float.valueOf("56.78"), result);
    }

    @Test
    public void directDouble() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.set("key1", 12.34);

        Double result = obj.getDouble("key1");
        assertEquals(Double.valueOf(12.34), result);

        result = obj.getDouble("['key1']");
        assertEquals(Double.valueOf(12.34), result);

        result = obj.getDouble("[\"key1\"]");
        assertEquals(Double.valueOf(12.34), result);

        result = obj.getDouble("nope", 56.78);
        assertEquals(Double.valueOf(56.78), result);
    }

    @Test
    public void directBoolean() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.set("key1", true);

        Boolean result = obj.getBoolean("key1");
        assertEquals(true, result);

        result = obj.getBoolean("['key1']");
        assertEquals(true, result);

        result = obj.getBoolean("[\"key1\"]");
        assertEquals(true, result);

        result = obj.getBoolean("nope", true);
        assertEquals(true, result);
    }

    @Test
    public void directBigDecimal() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.set("key1", new BigDecimal("1234"));

        BigDecimal result = obj.getBigDecimal("key1");
        assertEquals(new BigDecimal("1234"), result);

        result = obj.getBigDecimal("['key1']");
        assertEquals(new BigDecimal("1234"), result);

        result = obj.getBigDecimal("[\"key1\"]");
        assertEquals(new BigDecimal("1234"), result);

        result = obj.getBigDecimal("nope", new BigDecimal("567"));
        assertEquals(new BigDecimal("567"), result);
    }

    @Test
    public void directBytes() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.set("key1", "test".getBytes("UTF-8"));

        byte[] result = obj.getBytesFromBase64String("key1");
        assertTrue(Arrays.equals("test".getBytes("UTF-8"), result));

        result = obj.getBytesFromBase64String("['key1']");
        assertTrue(Arrays.equals("test".getBytes("UTF-8"), result));

        result = obj.getBytesFromBase64String("[\"key1\"]");
        assertTrue(Arrays.equals("test".getBytes("UTF-8"), result));

        result = obj.getBytesFromBase64String("nope", "test2".getBytes("UTF-8"));
        assertTrue(Arrays.equals("test2".getBytes("UTF-8"), result));
    }

    @Test
    public void directDate() throws Exception {

        Date date = new Date();

        JsonObject obj = getJsonManager().create();
        obj.set("key1", date);

        Date result = obj.getDate("key1");
        assertEquals(date, result);

        result = obj.getDate("['key1']");
        assertEquals(date, result);

        result = obj.getDate("[\"key1\"]");
        assertEquals(date, result);

        Date newDate = DateUtils.addHours(date, 1);

        result = obj.getDate("nope", newDate);
        assertEquals(newDate, result);
    }

    @Test
    public void obj() throws Exception {

        JsonObject inner = getJsonManager().create();
        inner.set("key1", "value1");

        JsonObject obj = getJsonManager().create();
        obj.set("inner", inner);

        JsonObject result = obj.getJsonObject("inner");
        assertNotNull(result);
        assertEquals("value1", result.getString("key1"));

        result = obj.getJsonObject("nope");
        assertNull(result);

        result = obj.getJsonObjectOrEmpty("nope");
        assertNotNull(result);

        JsonObject defaultObj = getJsonManager().create();
        defaultObj.set("key2", "value2");

        result = obj.getJsonObject("nope", defaultObj);
        assertNotNull(result);
        assertEquals("value2", result.getString("key2"));
    }

    @Test
    public void array() throws Exception {

        JsonArray inner = getJsonManager().createArray();
        inner.add("value1");

        JsonObject obj = getJsonManager().create();
        obj.set("inner", inner);

        JsonArray result = obj.getJsonArray("inner");
        assertNotNull(result);
        assertEquals("value1", result.getString(0));

        result = obj.getJsonArray("nope");
        assertNull(result);

        result = obj.getJsonArrayOrEmpty("nope");
        assertNotNull(result);

        JsonArray defaultArray = getJsonManager().createArray();
        defaultArray.add("value2");

        result = obj.getJsonArray("nope", defaultArray);
        assertNotNull(result);
        assertEquals("value2", result.getString(0));
    }

    @Test
    public void objString() throws Exception {

        JsonObject inner = getJsonManager().create();
        inner.set("key1", "value1");

        JsonObject obj = getJsonManager().create();
        obj.set("inner", inner);

        String result = obj.getString("inner.key1");
        assertEquals("value1", result);

        result = obj.getString("nope", "defaultVal");
        assertEquals("defaultVal", result);

        result = obj.getString("inner.nope", "defaultVal");
        assertEquals("defaultVal", result);
    }

    @Test
    public void objInteger() throws Exception {

        JsonObject inner = getJsonManager().create();
        inner.set("key1", 123);

        JsonObject obj = getJsonManager().create();
        obj.set("inner", inner);

        Integer result = obj.getInteger("inner.key1");
        assertEquals(Integer.valueOf(123), result);

        result = obj.getInteger("nope", 456);
        assertEquals(Integer.valueOf(456), result);

        result = obj.getInteger("inner.nope", 456);
        assertEquals(Integer.valueOf(456), result);
    }

    @Test
    public void objObjString() throws Exception {

        JsonObject inner2 = getJsonManager().create();
        inner2.set("key1", "value1");

        JsonObject inner = getJsonManager().create();
        inner.set("inner2", inner2);

        JsonObject obj = getJsonManager().create();
        obj.set("inner", inner);

        String result = obj.getString("inner.inner2.key1");
        assertEquals("value1", result);

        result = obj.getString("inner.nope.key1");
        assertEquals(null, result);

        result = obj.getString("inner.nope.key1", "defaultVal");
        assertEquals("defaultVal", result);

        result = obj.getString("nope", "defaultVal");
        assertEquals("defaultVal", result);

        result = obj.getString("inner.nope", "defaultVal");
        assertEquals("defaultVal", result);
    }

    @Test
    public void arrayString() throws Exception {

        JsonArray inner = getJsonManager().createArray();
        inner.add("value1");
        inner.add("value2");

        JsonObject obj = getJsonManager().create();
        obj.set("inner", inner);

        String result = obj.getString("inner[0]");
        assertEquals("value1", result);

        result = obj.getString("inner[1]");
        assertEquals("value2", result);

        result = obj.getString("inner[2]");
        assertEquals(null, result);

        result = obj.getString("inner[2]", "defaultVal");
        assertEquals("defaultVal", result);
    }

    @Test
    public void arrayArrayString() throws Exception {

        JsonArray inner2 = getJsonManager().createArray();
        inner2.add("value1");
        inner2.add("value2");

        JsonArray inner = getJsonManager().createArray();
        inner.add("value3");
        inner.add(inner2);

        JsonObject obj = getJsonManager().create();
        obj.set("inner", inner);

        String result = obj.getString("inner[0]");
        assertEquals("value3", result);

        result = obj.getString("inner[1][0]");
        assertEquals("value1", result);

        result = obj.getString("inner[1][1]");
        assertEquals("value2", result);

        result = obj.getString("inner[1][2]");
        assertEquals(null, result);

        result = obj.getString("inner[1][2]", "defaultVal");
        assertEquals("defaultVal", result);
    }

    @Test
    public void objArrayString() throws Exception {

        JsonArray inner2 = getJsonManager().createArray();
        inner2.add("value1");
        inner2.add("value2");

        JsonObject inner = getJsonManager().create();
        inner.set("inner2", inner2);

        JsonObject obj = getJsonManager().create();
        obj.set("inner", inner);

        String result = obj.getString("inner.inner2[0]");
        assertEquals("value1", result);

        result = obj.getString("inner.inner2[1]");
        assertEquals("value2", result);

        result = obj.getString("inner.inner2[2]");
        assertEquals(null, result);

        result = obj.getString("inner.inner2[2]", "defaultVal");
        assertEquals("defaultVal", result);
    }

    @Test
    public void arrayObjString() throws Exception {

        JsonObject inner2 = getJsonManager().create();
        inner2.set("key1", "value1");

        JsonArray inner = getJsonManager().createArray();
        inner.add("value3");
        inner.add(inner2);

        JsonObject obj = getJsonManager().create();
        obj.set("inner", inner);

        String result = obj.getString("inner[0]");
        assertEquals("value3", result);

        result = obj.getString("inner[1].key1");
        assertEquals("value1", result);

        result = obj.getString("inner[1].nope");
        assertEquals(null, result);

        result = obj.getString("inner[1].nope", "defaultVal");
        assertEquals("defaultVal", result);
    }

    @Test
    public void lots() throws Exception {

        Date date = new Date();

        JsonArray inner7 = getJsonManager().createArray();
        inner7.add("value7");
        inner7.add(123);
        inner7.add(12.34);
        inner7.add(true);
        inner7.add(date);

        JsonObject inner6 = getJsonManager().create();
        inner6.set("key5", inner7);
        inner6.set("key6", "value6");

        JsonObject inner5 = getJsonManager().create();
        inner5.set("key3", "value3");
        inner5.set("key4", inner6);

        JsonArray inner4 = getJsonManager().createArray();
        inner4.add("value2");
        inner4.add(inner5);

        JsonArray inner3 = getJsonManager().createArray();
        inner3.add(inner4);

        JsonObject inner2 = getJsonManager().create();
        inner2.set("key1", "value1");
        inner2.set("key2", inner3);

        JsonArray inner = getJsonManager().createArray();
        inner.add("value0");
        inner.add(inner2);

        JsonObject obj = getJsonManager().create();
        obj.set("inner", inner);

        String result = obj.getString("inner[0]");
        assertEquals("value0", result);

        result = obj.getString("inner[1].key1");
        assertEquals("value1", result);

        result = obj.getString("inner[1].key2[0][0]");
        assertEquals("value2", result);

        result = obj.getString("inner[1].key2[0][1].key3");
        assertEquals("value3", result);

        result = obj.getString("inner[1].key2[0][1].key4.key6");
        assertEquals("value6", result);

        result = obj.getString("inner[1].key2[0][1].key4.key5[0]");
        assertEquals("value7", result);

        result = obj.getString("inner[1]['key2'][0][1]['key4']['key5'][0]");
        assertEquals("value7", result);

        result = obj.getString("inner[1][\"key2\"][0][1][\"key4\"][\"key5\"][0]");
        assertEquals("value7", result);

        Integer resultInt = obj.getInteger("inner[1].key2[0][1].key4.key5[1]");
        assertEquals(Integer.valueOf(123), resultInt);

        Double resultDouble = obj.getDouble("inner[1].key2[0][1].key4.key5[2]");
        assertEquals(Double.valueOf(12.34), resultDouble);

        Boolean resultBoolean = obj.getBoolean("inner[1].key2[0][1].key4.key5[3]");
        assertEquals(true, resultBoolean);

        Date resultDate = obj.getDate("inner[1].key2[0][1].key4.key5[4]");
        assertEquals(date, resultDate);

    }

    @Test
    public void fromArrayString() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("value1");

        String result = array.getString("[0]");
        assertEquals("value1", result);

        result = array.getString("[1]", "defaultVal");
        assertEquals("defaultVal", result);
    }

    @Test
    public void fromArrayArrayString() throws Exception {

        JsonArray inner = getJsonManager().createArray();
        inner.add("value2");

        JsonArray array = getJsonManager().createArray();
        array.add("value1");
        array.add(inner);

        String result = array.getString("[0]");
        assertEquals("value1", result);

        result = array.getString("[1][0]");
        assertEquals("value2", result);

        result = array.getString("[1][1]");
        assertEquals(null, result);

        result = array.getString("[1][1]", "defaultVal");
        assertEquals("defaultVal", result);
    }

    @Test
    public void fromArrayObjectString() throws Exception {

        JsonObject inner = getJsonManager().create();
        inner.set("key2", "value2");

        JsonArray array = getJsonManager().createArray();
        array.add("value1");
        array.add(inner);

        String result = array.getString("[0]");
        assertEquals("value1", result);

        result = array.getString("[1].key2");
        assertEquals("value2", result);

        result = array.getString("[1]['key2']");
        assertEquals("value2", result);

        result = array.getString("[1][\"key2\"]");
        assertEquals("value2", result);
    }

    @Test
    public void fromArrayLots() throws Exception {

        JsonArray inner7 = getJsonManager().createArray();
        inner7.add("value7");

        JsonObject inner6 = getJsonManager().create();
        inner6.set("key5", inner7);
        inner6.set("key6", "value6");

        JsonObject inner5 = getJsonManager().create();
        inner5.set("key3", "value3");
        inner5.set("key4", inner6);

        JsonArray inner4 = getJsonManager().createArray();
        inner4.add("value2");
        inner4.add(inner5);

        JsonArray inner3 = getJsonManager().createArray();
        inner3.add(inner4);

        JsonObject inner2 = getJsonManager().create();
        inner2.set("key1", "value1");
        inner2.set("key2", inner3);

        JsonArray inner = getJsonManager().createArray();
        inner.add("value0");
        inner.add(inner2);

        JsonObject obj = getJsonManager().create();
        obj.set("inner", inner);

        JsonArray array = getJsonManager().createArray();
        array.add("toto");
        array.add(obj);

        String result = array.getString("[0]");
        assertEquals("toto", result);

        result = array.getString("[1].inner[1].key2[0][1]['key4'].key5[0]");
        assertEquals("value7", result);
    }

    @Test
    public void invalidUnclosedBrackets() throws Exception {

        JsonArray inner = getJsonManager().createArray();
        inner.add("value1");

        JsonObject obj = getJsonManager().create();
        obj.set("inner", inner);

        try {
            obj.getString("inner[2", "defaultVal");
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void invalidEmptyKey() throws Exception {

        JsonObject inner = getJsonManager().create();
        inner.set("key1", "value1");

        JsonObject obj = getJsonManager().create();
        obj.set("inner", inner);

        try {
            obj.getString("inner..inner", "defaultVal");
            fail();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    @Test
    public void jsonPathIsUsedAsIsWithBracketsAndQuotesAndCorrectEscaping() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.setNoKeyParsing("this'.\"is[\\a.key[x", "value1", false);

        String result = obj.getString("[\"this'.\\\"is[\\\\a.key[x\"]", "defaultVal");
        assertEquals("value1", result);

        result = obj.getString("['this\\'.\"is[\\\\a.key[x']", "defaultVal");
        assertEquals("value1", result);

        result = obj.getString("this'.\"is[\\a.key[x", "defaultVal");
        assertEquals("defaultVal", result);
    }

    @Test
    public void jsonPathOnImmutableObjets() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.set("aaa.bbb[2].ccc", "Stromgol");

        obj = obj.clone(false);
        assertFalse(obj.isMutable());

        assertEquals("Stromgol", obj.getString("aaa.bbb[2].ccc"));

        JsonArray array = obj.getJsonArray("aaa.bbb");
        assertNotNull(array);
        assertFalse(array.isMutable());

        obj = obj.getJsonObject("aaa.bbb[2]");
        assertNotNull(obj);
        assertFalse(obj.isMutable());
        assertEquals("Stromgol", obj.getString(".ccc"));
        assertEquals("Stromgol", obj.getString("ccc"));
        assertEquals("Stromgol", obj.getString("['ccc']"));
        assertEquals("Stromgol", obj.getString("[\"ccc\"]"));
    }

}
