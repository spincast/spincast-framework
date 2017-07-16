package org.spincast.tests.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.defaults.testing.NoAppTestingBase;
import org.spincast.plugins.config.SpincastConfigPluginConfig;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.testing.core.utils.SpincastConfigTestingDefault;

import com.google.inject.Inject;

public class JsonPathsMergeTest extends NoAppTestingBase {

    @Override
    protected Class<? extends SpincastConfig> getTestingConfigImplementationClass() {
        return SpincastTestConfigTest.class;
    }

    @Inject
    protected JsonManager jsonManager;

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected static class SpincastTestConfigTest extends SpincastConfigTestingDefault {

        /**
         * Constructor
         */
        @Inject
        protected SpincastTestConfigTest(SpincastConfigPluginConfig spincastConfigPluginConfig) {
            super(spincastConfigPluginConfig);
        }

        @Override
        public int getMaxNumberOfKeysWhenConvertingMapToJsonObject() {
            return 5;
        }

        @Override
        public int getKeyMaxLengthWhenConvertingMapToJsonObject() {
            return 100;
        }

        @Override
        public int getJsonPathArrayIndexMax() {
            return 100;
        }
    }

    @Test
    public void noParsingRequired() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("key1", "value1");

        JsonObject root = getJsonManager().fromMap(params, true);
        assertNotNull(root);

        assertEquals("value1", root.getString("key1"));
    }

    @Test
    public void startsWithKeyInQuotes() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("['some key']", "value1");

        JsonObject root = getJsonManager().fromMap(params, true);
        assertNotNull(root);

        assertEquals("value1", root.getString("some key"));
    }

    @Test
    public void startsWithKeyInDoubleQuotes() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("[\"some key\"]", "value1");

        JsonObject root = getJsonManager().fromMap(params, true);
        assertNotNull(root);

        assertEquals("value1", root.getString("some key"));
    }

    @Test
    public void rootIsNotAnArray() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("[0]", "value1");

        try {
            getJsonManager().fromMap(params, true);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void objectAndProp() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("obj1.key1", "value1");

        JsonObject root = getJsonManager().fromMap(params, true);
        assertNotNull(root);

        JsonObject obj1 = root.getJsonObject("obj1");
        assertNotNull(obj1);
        assertEquals("value1", obj1.getString("key1"));
    }

    @Test
    public void objectAndPropUsingBracketsAndQuotes() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("obj1['key1']", "value1");

        JsonObject root = getJsonManager().fromMap(params, true);
        assertNotNull(root);

        JsonObject obj1 = root.getJsonObject("obj1");
        assertNotNull(obj1);
        assertEquals("value1", obj1.getString("key1"));
    }

    @Test
    public void objectAndPropUsingBracketsAndDoubleQuotes() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("obj1[\"key1\"]", "value1");

        JsonObject root = getJsonManager().fromMap(params, true);
        assertNotNull(root);

        JsonObject obj1 = root.getJsonObject("obj1");
        assertNotNull(obj1);
        assertEquals("value1", obj1.getString("key1"));
    }

    @Test
    public void keySpacesOk() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("obj1[\" \"]", "value1");

        JsonObject root = getJsonManager().fromMap(params, true);
        assertNotNull(root);

        JsonObject obj1 = root.getJsonObject("obj1");
        assertNotNull(obj1);
        assertEquals("value1", obj1.getString(" "));
    }

    @Test
    public void keyFreeForAll() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("obj1[\" | 1#23 4.,][ *()\"]", "value1");

        JsonObject root = getJsonManager().fromMap(params, true);
        assertNotNull(root);

        JsonObject obj1 = root.getJsonObject("obj1");
        assertNotNull(obj1);
        assertEquals("value1", obj1.getStringNoKeyParsing(" | 1#23 4.,][ *()"));
    }

    @Test
    public void arrayFirstIndex() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("array1[0]", "value1");

        JsonObject root = getJsonManager().fromMap(params, true);
        assertNotNull(root);

        JsonArray array1 = root.getJsonArray("array1");
        assertNotNull(array1);
        assertEquals("value1", array1.getString(0));
    }

    @Test
    public void arrayThirdIndex() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("array1[2]", "value1");

        JsonObject root = getJsonManager().fromMap(params, true);
        assertNotNull(root);

        JsonArray array1 = root.getJsonArray("array1");
        assertNotNull(array1);
        assertNull(array1.getString(0, "nope"));
        assertNull(array1.getString(1, "nope"));
        assertEquals("value1", array1.getString(2));
    }

    @Test
    public void arrayTwoDigitsIndex() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("array1[12]", "value1");

        JsonObject root = getJsonManager().fromMap(params, true);
        assertNotNull(root);

        JsonArray array1 = root.getJsonArray("array1");
        assertNotNull(array1);
        assertEquals("value1", array1.getString(12));
    }

    @Test
    public void twoObjects() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("obj1.obj2.key1", "value1");

        JsonObject root = getJsonManager().fromMap(params, true);
        assertNotNull(root);

        JsonObject obj1 = root.getJsonObject("obj1");
        assertNotNull(obj1);

        JsonObject obj2 = obj1.getJsonObject("obj2");
        assertNotNull(obj2);

        assertEquals("value1", obj2.getString("key1"));
    }

    @Test
    public void objectArray() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("obj1.array1[0]", "value1");

        JsonObject root = getJsonManager().fromMap(params, true);
        assertNotNull(root);

        JsonObject obj1 = root.getJsonObject("obj1");
        assertNotNull(obj1);

        JsonArray array1 = obj1.getJsonArray("array1");
        assertNotNull(array1);
        assertEquals("value1", array1.getString(0));
    }

    @Test
    public void twoArrays() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("array1[2][1]", "value1");

        JsonObject root = getJsonManager().fromMap(params, true);
        assertNotNull(root);

        JsonArray array1 = root.getJsonArray("array1");
        assertNotNull(array1);

        JsonArray array2 = array1.getJsonArray(2);
        assertNotNull(array2);

        assertNull(array2.getString(0, "nope"));
        assertEquals("value1", array2.getString(1));
    }

    @Test
    public void arrayObject() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("array1[2].key1", "value1");

        JsonObject root = getJsonManager().fromMap(params, true);
        assertNotNull(root);

        JsonArray array1 = root.getJsonArray("array1");
        assertNotNull(array1);

        JsonObject obj1 = array1.getJsonObject(2);
        assertNotNull(obj1);

        assertEquals("value1", obj1.getString("key1"));
    }

    @Test
    public void lots() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("array1[2].array2[12][1].obj1.obj2['array3'][0]['obj3b']['obj3c'].obj4[\"obj5\"].key1", "value1");

        JsonObject root = getJsonManager().fromMap(params, true);
        assertNotNull(root);

        JsonArray array1 = root.getJsonArray("array1");
        assertNotNull(array1);

        JsonObject obj = array1.getJsonObject(2);
        assertNotNull(obj);

        JsonArray array2 = obj.getJsonArray("array2");
        assertNotNull(array2);

        JsonArray array = array2.getJsonArray(12);
        assertNotNull(array);

        obj = array.getJsonObject(1);
        assertNotNull(obj);

        JsonObject obj1 = obj.getJsonObject("obj1");
        assertNotNull(obj1);

        JsonObject obj2 = obj1.getJsonObject("obj2");
        assertNotNull(obj2);

        array = obj2.getJsonArray("array3");
        assertNotNull(array);

        obj = array.getJsonObject(0);
        assertNotNull(obj);

        JsonObject obj3b = obj.getJsonObject("obj3b");
        assertNotNull(obj3b);

        JsonObject obj3c = obj3b.getJsonObject("obj3c");
        assertNotNull(obj3c);

        JsonObject obj4 = obj3c.getJsonObject("obj4");
        assertNotNull(obj4);

        JsonObject obj5 = obj4.getJsonObject("obj5");
        assertNotNull(obj5);

        assertEquals("value1", obj5.getString("key1"));
    }

    @Test
    public void invalidCharAfterEndBracket() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("array1[0]nope.key1", "value1");

        try {
            getJsonManager().fromMap(params, true);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void invalidDotAtTheEnd() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("obj1.", "value1");

        try {
            getJsonManager().fromMap(params, true);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void invalidEmptyName() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("obj1..key1", "value1");

        try {
            getJsonManager().fromMap(params, true);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void invalidEmptyNameInBrackets1() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("obj1[''].key1", "value1");

        try {
            getJsonManager().fromMap(params, true);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void invalidEmptyNameInBrackets2() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("obj1[\"\"].key1", "value1");

        try {
            getJsonManager().fromMap(params, true);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void invalidIndex() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("array1[nope].key1", "value1");

        try {
            getJsonManager().fromMap(params, true);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void invalidIndex2() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("array1[1nope].key1", "value1");

        try {
            getJsonManager().fromMap(params, true);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void invalidIndex3() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("array1[1'].key1", "value1");

        try {
            getJsonManager().fromMap(params, true);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void invalidIndex4() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("array1[1'a'].key1", "value1");

        try {
            getJsonManager().fromMap(params, true);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void invalidIndex5() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("array1['a'1].key1", "value1");

        try {
            getJsonManager().fromMap(params, true);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void invalidIndex6() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("array1[[0].key1", "value1");

        try {
            getJsonManager().fromMap(params, true);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void invalidIndex7() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("array1[-2].key1", "value1");

        try {
            getJsonManager().fromMap(params, true);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void nameNotQuotedAllCharsValidExceptSpecialOnes() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put(" |1#/'\"\\,&$<>();:*\t\n . |1#/'\"\\,&$<>();:*\t\n ", " |1#/'\"\\,&$<>();:*\t\n ");

        JsonObject root = getJsonManager().fromMap(params, true);
        assertNotNull(root);

        JsonObject obj = root.getJsonObject(" |1#/'\"\\,&$<>();:*\t\n ");
        assertNotNull(obj);

        assertEquals(" |1#/'\"\\,&$<>();:*\t\n ", obj.getString(" |1#/'\"\\,&$<>();:*\t\n "));
    }

    @Test
    public void nameQuotedAllCharsValidSomeNeedEscaping() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("['[] |1#/\\\\,\"\\'&$<>();:*\t\n.']", "value");

        JsonObject root = getJsonManager().fromMap(params, true);
        assertNotNull(root);

        assertEquals("value", root.getStringNoKeyParsing("[] |1#/\\,\"'&$<>();:*\t\n."));
        assertEquals("value", root.getString("['[] |1#/\\\\,\"\\'&$<>();:*\t\n.']"));
    }

    @Test
    public void nameDoubleQuotedAllCharsValidSomeNeedEscaping() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("[\"[] |1#/\\\\,'&$\\\"<>();:*\t\n.\"]", "value");

        JsonObject root = getJsonManager().fromMap(params, true);
        assertNotNull(root);

        assertEquals("value", root.getStringNoKeyParsing("[] |1#/\\,'&$\"<>();:*\t\n."));
    }

    @Test
    public void invalidEscaping() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("['\\a']", "value1");

        try {
            getJsonManager().fromMap(params, true);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void escapingEndQuote() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("['a\\']", "value1");

        try {
            getJsonManager().fromMap(params, true);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void arrayThenKeyInQuotes() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("array1[0]['key1']", "value1");

        JsonObject root = getJsonManager().fromMap(params, true);
        assertNotNull(root);

        JsonArray array1 = root.getJsonArray("array1");
        assertNotNull(array1);

        JsonObject obj = array1.getJsonObject(0);
        assertNotNull(obj);

        assertEquals("value1", obj.getString("key1"));
    }

    @Test
    public void mergeSimple() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("key1", "value1");
        params.put("key2", "value2");
        params.put("key3", "value3");

        JsonObject root = getJsonManager().fromMap(params, true);
        assertNotNull(root);

        assertEquals("value1", root.getString("key1"));
        assertEquals("value2", root.getString("key2"));
        assertEquals("value3", root.getString("key3"));
    }

    @Test
    public void mergeObject() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("obj1.key2", "value2");
        params.put("key1", "value1");
        params.put("obj1.key3", "value3");

        JsonObject root = getJsonManager().fromMap(params, true);
        assertNotNull(root);

        assertEquals("value1", root.getString("key1"));

        JsonObject obj1 = root.getJsonObject("obj1");
        assertNotNull(obj1);

        assertEquals("value2", obj1.getString("key2"));
        assertEquals("value3", obj1.getString("key3"));
    }

    @Test
    public void mergeArray() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("array1[2]", "value2");
        params.put("array1[0]", "value1");

        JsonObject root = getJsonManager().fromMap(params, true);
        assertNotNull(root);

        JsonArray array1 = root.getJsonArray("array1");
        assertNotNull(array1);

        assertEquals("value1", array1.getString(0));
        assertEquals(null, array1.getString(1, "nope"));
        assertEquals("value2", array1.getString(2));
    }

    @Test
    public void mergeArrays() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("array1[2][1]", "value1");
        params.put("array1[2][2]", "value2");

        JsonObject root = getJsonManager().fromMap(params, true);
        assertNotNull(root);

        JsonArray array1 = root.getJsonArray("array1");
        assertNotNull(array1);

        JsonArray array2 = array1.getJsonArray(2);
        assertNotNull(array2);

        assertEquals(null, array2.getString(0, "nope"));
        assertEquals("value1", array2.getString(1));
        assertEquals("value2", array2.getString(2));
    }

    @Test
    public void deepMerge() throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("array1[2].array2[12][1].obj1.obj2['array3'][0]['obj3b']['obj3c'].obj4[\"obj5\"].key1", "value1");
        params.put("array1[2].array2[12][1].obj1.obj2['array3'][0]['obj3b']['obj3c'].obj4[\"obj6\"].key2", "value2");
        params.put("array1[2].array2[12][1].obj1.obj2['array3'][3]", "value3");

        JsonObject root = getJsonManager().fromMap(params, true);
        assertNotNull(root);

        JsonArray array1 = root.getJsonArray("array1");
        assertNotNull(array1);

        JsonObject obj = array1.getJsonObject(2);
        assertNotNull(obj);

        JsonArray array2 = obj.getJsonArray("array2");
        assertNotNull(array2);

        JsonArray array = array2.getJsonArray(12);
        assertNotNull(array);

        obj = array.getJsonObject(1);
        assertNotNull(obj);

        JsonObject obj1 = obj.getJsonObject("obj1");
        assertNotNull(obj1);

        JsonObject obj2 = obj1.getJsonObject("obj2");
        assertNotNull(obj2);

        array = obj2.getJsonArray("array3");
        assertNotNull(array);

        obj = array.getJsonObject(0);
        assertNotNull(obj);

        assertEquals("value3", array.getString(3));

        JsonObject obj3b = obj.getJsonObject("obj3b");
        assertNotNull(obj3b);

        JsonObject obj3c = obj3b.getJsonObject("obj3c");
        assertNotNull(obj3c);

        JsonObject obj4 = obj3c.getJsonObject("obj4");
        assertNotNull(obj4);

        JsonObject obj5 = obj4.getJsonObject("obj5");
        assertNotNull(obj5);

        JsonObject obj6 = obj4.getJsonObject("obj6");
        assertNotNull(obj6);

        assertEquals("value1", obj5.getString("key1"));
        assertEquals("value2", obj6.getString("key2"));
    }

    @Test
    public void mergeInvalidTypeNotAnObject() throws Exception {

        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("key1", "value1");
        params.put("key1.key2", "value2");

        try {
            getJsonManager().fromMap(params, true);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void mergeInvalidTypeNotAnObject2() throws Exception {

        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("array1[0]", "value1");
        params.put("array1.key2", "value2");

        try {
            getJsonManager().fromMap(params, true);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void mergeInvalidTypeNotAnArray() throws Exception {

        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("array1.key2", "value2");
        params.put("array1[0]", "value1");

        try {
            getJsonManager().fromMap(params, true);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void keyReplace() throws Exception {

        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("key1.key2", "value2");
        params.put("key1", "value1");

        JsonObject obj = getJsonManager().fromMap(params, true);

        assertEquals("value1", obj.getString("key1"));
    }

    @Test
    public void tooManyKeysToParse() throws Exception {

        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();

        params.put("key1", "value1");
        params.put("key2", "value2");
        params.put("key3", "value3");
        params.put("key4", "value4");
        params.put("key5", "value5");
        params.put("key6", "value6");

        try {
            getJsonManager().fromMap(params, true);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void keyTooLong() throws Exception {

        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();

        params.put("a." + StringUtils.repeat("x", 101), "value1");

        try {
            getJsonManager().fromMap(params, true);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void arrayIndexTooBig() throws Exception {

        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();

        params.put("array[101]", "value1");

        try {
            getJsonManager().fromMap(params, true);
            fail();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    @Test
    public void mergeAtJsonPath() throws Exception {

        JsonObject root = getJsonManager().create();
        root.put("key1", "value1");

        root.put("titi.toto[2].tata", "value2");

        assertEquals("value1", root.getString("key1"));

        JsonObject obj = root.getJsonObject("titi");
        assertNotNull(obj);

        JsonArray array = obj.getJsonArray("toto");
        assertNotNull(array);

        obj = array.getJsonObject(2);
        assertNotNull(obj);

        assertEquals("value2", obj.getString("tata"));
    }

    @Test
    public void mergeAtJsonPathNoClone() throws Exception {

        JsonObject inner = getJsonManager().create();
        inner.put("key3", "value3");

        JsonObject root = getJsonManager().create();
        root.put("key1", "value1");

        root.put("titi.toto[2].tata", inner);

        assertEquals("value1", root.getString("key1"));

        JsonObject obj = root.getJsonObject("titi");
        assertNotNull(obj);

        JsonArray array = obj.getJsonArray("toto");
        assertNotNull(array);

        obj = array.getJsonObject(2);
        assertNotNull(obj);

        obj = obj.getJsonObject("tata");
        assertNotNull(obj);

        assertEquals("value3", obj.getString("key3"));

        inner.put("key3", "value4");

        assertEquals("value4", obj.getString("key3"));
    }

    @Test
    public void mergeAtJsonPathClone() throws Exception {

        JsonObject inner = getJsonManager().create();
        inner.put("key3", "value3");

        JsonObject root = getJsonManager().create();
        root.put("key1", "value1");

        root.put("titi.toto[2].tata", inner, true);

        assertEquals("value1", root.getString("key1"));

        JsonObject obj = root.getJsonObject("titi");
        assertNotNull(obj);

        JsonArray array = obj.getJsonArray("toto");
        assertNotNull(array);

        obj = array.getJsonObject(2);
        assertNotNull(obj);

        obj = obj.getJsonObject("tata");
        assertNotNull(obj);

        assertEquals("value3", obj.getString("key3"));

        inner.put("key3", "value4");

        assertEquals("value3", obj.getString("key3"));
    }

}
