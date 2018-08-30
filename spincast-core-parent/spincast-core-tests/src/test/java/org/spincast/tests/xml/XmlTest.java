package org.spincast.tests.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.lang.reflect.Field;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.json.JsonObjectArrayBase;
import org.spincast.core.json.JsonObjectDefault;
import org.spincast.core.xml.XmlManager;
import org.spincast.defaults.testing.NoAppTestingBase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.inject.Inject;

public class XmlTest extends NoAppTestingBase {

    @Inject
    protected JsonManager jsonManager;

    @Inject
    protected XmlManager xmlManager;

    @Test
    public void toXml() throws Exception {

        JsonObject jsonObj = this.jsonManager.create();
        jsonObj.set("someBoolean", true);
        jsonObj.set("someInt", 123);

        JsonArray jsonArray = this.jsonManager.createArray();
        jsonArray.add("toto");
        jsonArray.add(123);

        JsonObject jsonObj2 = this.jsonManager.create();
        jsonObj2.set("anotherBoolean", true);
        jsonObj2.set("anotherInt", 44444);
        jsonObj2.set("innerObj", jsonObj);
        jsonObj2.set("someArray", jsonArray);

        String xml = this.xmlManager.toXml(jsonObj2);
        assertNotNull(xml);

        // @formatter:off
        assertEquals(("<JsonObject>" + 
                          "<innerObj>" + 
                              "<someBoolean>true</someBoolean>" + 
                              "<someInt>123</someInt>" +
                          "</innerObj>" + 
                          "<anotherBoolean>true</anotherBoolean>" + 
                          "<someArray isArray=\"true\">" +
                              "<element>toto</element>" + 
                              "<element>123</element>" + 
                          "</someArray>" +
                          "<anotherInt>44444</anotherInt>" + 
                      "</JsonObject>").length(),
                     xml.length());
         // @formatter:on
    }

    @Test
    public void toXmlArrayElementIsJsonObject() throws Exception {

        JsonObject jsonObject = this.jsonManager.create();
        JsonArray array = this.jsonManager.createArray();
        jsonObject.set("someArray", array);
        JsonObject inner = this.jsonManager.create();
        inner.set("fieldName", "email");
        inner.set("message", "Not a valid email address.");
        inner.set("type", "VALIDATION_TYPE_EMAIL");
        array.add(inner);

        String xml = this.xmlManager.toXml(jsonObject, true);

        // @formatter:off
        String expected = "<JsonObject>\n" + 
                          "    <someArray isArray=\"true\">\n" + 
                          "        <element>\n" + 
                          "            <obj>\n" +  
                          "                <fieldName>email</fieldName>\n" + 
                          "                <message>Not a valid email address.</message>\n" + 
                          "                <type>VALIDATION_TYPE_EMAIL</type>\n" + 
                          "            </obj>\n" +  
                          "        </element>\n" + 
                          "    </someArray>\n" + 
                          "</JsonObject>\n";

        String expected2 = "<JsonObject>\n" + 
                "    <someArray isArray=\"true\">\n" + 
                "        <element>\n" + 
                "            <obj>\n" +  
                "                <fieldName>email</fieldName>\n" + 
                "                <type>VALIDATION_TYPE_EMAIL</type>\n" + 
                "                <message>Not a valid email address.</message>\n" + 
                "            </obj>\n" +  
                "        </element>\n" + 
                "    </someArray>\n" + 
                "</JsonObject>\n";
        
        String expected3 = "<JsonObject>\n" + 
                "    <someArray isArray=\"true\">\n" + 
                "        <element>\n" + 
                "            <obj>\n" +  
                "                <type>VALIDATION_TYPE_EMAIL</type>\n" + 
                "                <fieldName>email</fieldName>\n" + 
                "                <message>Not a valid email address.</message>\n" + 
                "            </obj>\n" +  
                "        </element>\n" + 
                "    </someArray>\n" + 
                "</JsonObject>\n";
        
        String expected4 = "<JsonObject>\n" + 
                "    <someArray isArray=\"true\">\n" + 
                "        <element>\n" + 
                "            <obj>\n" +  
                "                <type>VALIDATION_TYPE_EMAIL</type>\n" + 
                "                <message>Not a valid email address.</message>\n" + 
                "                <fieldName>email</fieldName>\n" + 
                "            </obj>\n" +  
                "        </element>\n" + 
                "    </someArray>\n" + 
                "</JsonObject>\n";
        
        String expected5 = "<JsonObject>\n" + 
                "    <someArray isArray=\"true\">\n" + 
                "        <element>\n" + 
                "            <obj>\n" +  
                "                <message>Not a valid email address.</message>\n" + 
                "                <type>VALIDATION_TYPE_EMAIL</type>\n" + 
                "                <fieldName>email</fieldName>\n" + 
                "            </obj>\n" +  
                "        </element>\n" + 
                "    </someArray>\n" + 
                "</JsonObject>\n";
        
        String expected6 = "<JsonObject>\n" + 
                "    <someArray isArray=\"true\">\n" + 
                "        <element>\n" + 
                "            <obj>\n" +  
                "                <message>Not a valid email address.</message>\n" + 
                "                <fieldName>email</fieldName>\n" + 
                "                <type>VALIDATION_TYPE_EMAIL</type>\n" + 
                "            </obj>\n" +  
                "        </element>\n" + 
                "    </someArray>\n" + 
                "</JsonObject>\n";

        // @formatter:on

        assertTrue(xml.equals(expected) ||
                   xml.equals(expected2) ||
                   xml.equals(expected3) ||
                   xml.equals(expected4) ||
                   xml.equals(expected5) ||
                   xml.equals(expected6));
    }

    @Test
    public void toXmlArrayElementIsAnotherArray() throws Exception {

        JsonObject jsonObject = this.jsonManager.create();
        JsonArray array = this.jsonManager.createArray();
        jsonObject.set("someArray", array);

        JsonArray inner = this.jsonManager.createArray();
        inner.add("aaa");
        inner.add("bbb");
        inner.add("ccc");
        array.add(inner);

        String xml = this.xmlManager.toXml(jsonObject, true);

        JsonObject resultObj = this.xmlManager.fromXml(xml);
        assertNotNull(resultObj);
        JsonArray arr1 = resultObj.getJsonArray("someArray");
        assertNotNull(arr1);
        JsonArray arr2 = arr1.getJsonArray(0);
        assertNotNull(arr2);
        assertEquals("aaa", arr2.getString(0));
        assertEquals("bbb", arr2.getString(1));
        assertEquals("ccc", arr2.getString(2));
    }

    @Test
    public void toXmlArrayElementsAreJsonObjectAnotherArray() throws Exception {

        JsonObject jsonObject = this.jsonManager.create();

        JsonObject innerObj = this.jsonManager.create();
        innerObj.set("name", "Stromgol");
        jsonObject.set("inner1", innerObj);

        JsonArray array = this.jsonManager.createArray();
        jsonObject.set("someArray", array);

        JsonArray inner = this.jsonManager.createArray();
        inner.add("aaa");
        inner.add("bbb");
        inner.add("ccc");
        JsonObject innerObj2 = this.jsonManager.create();
        innerObj2.set("name", "Stromgol2");
        inner.add(innerObj2);
        array.add(inner);

        String xml = this.xmlManager.toXml(jsonObject, true);

        JsonObject resultObj = this.xmlManager.fromXml(xml);
        assertNotNull(resultObj);

        JsonObject in1 = resultObj.getJsonObject("inner1");
        assertNotNull(in1);
        assertEquals("Stromgol", in1.getString("name"));

        JsonArray arr1 = resultObj.getJsonArray("someArray");
        assertNotNull(arr1);
        JsonArray arr2 = arr1.getJsonArray(0);
        assertNotNull(arr2);
        assertEquals("aaa", arr2.getString(0));
        assertEquals("bbb", arr2.getString(1));
        assertEquals("ccc", arr2.getString(2));
        JsonObject in2 = arr2.getJsonObject(3);
        assertNotNull(in2);
        assertEquals("Stromgol2", in2.getString("name"));
    }

    @Test
    public void fromXml() throws Exception {

        // @formatter:off
        String xml = "<JsonObject>\n" + 
                     "    <someArray isArray=\"true\">\n" + 
                     "        <element>\n" + 
                     "            <obj>\n" + 
                     "                <fieldName>email</fieldName>\n" + 
                     "                <message>Not a valid email address.</message>\n" + 
                     "                <type>VALIDATION_TYPE_EMAIL</type>\n" + 
                     "            </obj>\n" + 
                     "        </element>\n" + 
                     "    </someArray>\n" + 
                     "</JsonObject>\n";
        // @formatter:on

        JsonObject jsonObject = this.xmlManager.fromXml(xml);
        assertNotNull(jsonObject);

        JsonArray array = jsonObject.getJsonArray("someArray");
        assertNotNull(array);
        assertEquals(1, array.size());

        JsonObject inner = array.getJsonObject(0);
        assertNotNull(inner);
        assertEquals("email", inner.getString("fieldName"));
        assertEquals("Not a valid email address.", inner.getString("message"));
        assertEquals("VALIDATION_TYPE_EMAIL", inner.getString("type"));
    }

    @Test
    public void fromXmlOneChild() throws Exception {

        // @formatter:off
        String xml = "<JsonObject>\n" + 
                     "    <someArray isArray=\"true\">\n" + 
                     "        <element>\n" + 
                     "            <fieldName>email</fieldName>\n" + 
                     "        </element>\n" + 
                     "    </someArray>\n" + 
                     "</JsonObject>\n";
        // @formatter:on

        JsonObject jsonObject = this.xmlManager.fromXml(xml);
        assertNotNull(jsonObject);

        JsonArray array = jsonObject.getJsonArray("someArray");
        assertNotNull(array);
        assertEquals(1, array.size());

        JsonObject inner = array.getJsonObject(0);
        assertNotNull(inner);
        assertEquals("email", inner.getString("fieldName"));
    }

    @Test
    public void fromXmlInvalidArrayElementMoreThanOneChild() throws Exception {

        // @formatter:off
        String xml = "<JsonObject>\n" + 
                     "    <someArray isArray=\"true\">\n" + 
                     "        <element>\n" + 
                     "            <fieldName>email</fieldName>\n" + 
                     "            <message>Not a valid email address.</message>\n" + 
                     "        </element>\n" + 
                     "    </someArray>\n" + 
                     "</JsonObject>\n";
        // @formatter:on

        try {
            this.xmlManager.fromXml(xml);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void fromXmlInvalidArrayElementMoreThanOneChild2() throws Exception {

        // @formatter:off
        String xml = "<JsonObject>\n" + 
                     "    <someArray isArray=\"true\">\n" + 
                     "        <element>\n" + 
                     "            <fieldName>email</fieldName>\n" + 
                     "            <obj>\n" + 
                     "                <color>red</color>\n" +
                     "                <size>big</size>\n" +
                     "            </obj>\n" + 
                     "        </element>\n" + 
                     "    </someArray>\n" + 
                     "</JsonObject>\n";
        // @formatter:on

        try {
            this.xmlManager.fromXml(xml);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void fromXmlInvalidArrayElementMoreThanOneChild3() throws Exception {

        // @formatter:off
        String xml = "<JsonObject>\n" + 
                     "    <someArray isArray=\"true\">\n" + 
                     "        <element>\n" + 
                     "            <obj>\n" + 
                     "                <color>red</color>\n" +
                     "                <size>big</size>\n" +
                     "            </obj>\n" + 
                     "            <fieldName>email</fieldName>\n" + 
                     "        </element>\n" + 
                     "    </someArray>\n" + 
                     "</JsonObject>\n";
        // @formatter:on

        try {
            this.xmlManager.fromXml(xml);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void fromXmlInvalidArrayElementMoreThanOneChild4() throws Exception {

        // @formatter:off
        String xml = "<JsonObject>\n" + 
                     "    <someArray isArray=\"true\">\n" + 
                     "        <element>\n" + 
                     "            <obj>\n" + 
                     "                <color>red</color>\n" +
                     "                <size>big</size>\n" +
                     "            </obj>\n" + 
                     "            <obj2>\n" + 
                     "                <color>red</color>\n" +
                     "                <size>big</size>\n" +
                     "            </obj2>\n" + 
                     "        </element>\n" + 
                     "    </someArray>\n" + 
                     "</JsonObject>\n";
        // @formatter:on

        try {
            this.xmlManager.fromXml(xml);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void fromXmlInvalidArrayElementMoreThanOneChild5() throws Exception {

        // @formatter:off
        String xml = "<JsonObject>\n" + 
                     "    <someArray isArray=\"true\">\n" + 
                     "        <element>\n" + 
                     "            <obj>\n" + 
                     "                <color>red</color>\n" +
                     "                <size>big</size>\n" +
                     "            </obj>\n" + 
                     "            <empty />\n" + 
                     "        </element>\n" + 
                     "    </someArray>\n" + 
                     "</JsonObject>\n";
        // @formatter:on

        try {
            this.xmlManager.fromXml(xml);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void fromXmlInvalidArrayElementMoreThanOneChild6() throws Exception {

        // @formatter:off
        String xml = "<JsonObject>\n" + 
                     "    <someArray isArray=\"true\">\n" + 
                     "        <element>\n" + 
                     "            <empty />\n" + 
                     "            <fieldName>email</fieldName>\n" + 
                     "        </element>\n" + 
                     "    </someArray>\n" + 
                     "</JsonObject>\n";
        // @formatter:on

        try {
            this.xmlManager.fromXml(xml);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void fromXmlInvalidArrayElementMoreThanOneChild7() throws Exception {

        // @formatter:off
        String xml = "<JsonObject>\n" + 
                     "    <someArray isArray=\"true\">\n" + 
                     "        <element>\n" + 
                     "            directValue\n" + 
                     "            <fieldName>email</fieldName>\n" + 
                     "        </element>\n" + 
                     "    </someArray>\n" + 
                     "</JsonObject>\n";
        // @formatter:on

        try {
            this.xmlManager.fromXml(xml);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void fromXmlArrayElementDirectValueAtTheEnd() throws Exception {

        // @formatter:off
        String xml = "<JsonObject>\n" + 
                     "    <someArray isArray=\"true\">\n" + 
                     "        <element>\n" + 
                     "            <fieldName>email</fieldName>\n" + 
                     "            directValue\n" + 
                     "        </element>\n" + 
                     "    </someArray>\n" + 
                     "</JsonObject>\n";
        // @formatter:on

        //==========================================
        // It seems Jackson ignore the "directValue"
        // value here.
        //==========================================
        JsonObject jsonObject = this.xmlManager.fromXml(xml);
        assertNotNull(jsonObject);

        JsonArray array = jsonObject.getJsonArray("someArray");
        assertNotNull(array);
        assertEquals(1, array.size());

        JsonObject inner = array.getJsonObject(0);
        assertNotNull(inner);
        assertEquals("email", inner.getString("fieldName"));
    }

    @Test
    public void fromXmlInvalidArrayDirectValue() throws Exception {

        // @formatter:off
        String xml = "<JsonObject>\n" + 
                     "    <someArray isArray=\"true\">\n" + 
                     "        <element>\n" + 
                     "            <fieldName>email</fieldName>\n" + 
                     "        </element>\n" + 
                     "        directValue\n" + 
                     "    </someArray>\n" + 
                     "</JsonObject>\n";
        // @formatter:on

        //==========================================
        // It seems Jackson ignore the "directValue"
        // value here.
        //==========================================
        JsonObject jsonObject = this.xmlManager.fromXml(xml);
        assertNotNull(jsonObject);

        JsonArray array = jsonObject.getJsonArray("someArray");
        assertNotNull(array);
        assertEquals(1, array.size());

        JsonObject inner = array.getJsonObject(0);
        assertNotNull(inner);
        assertEquals("email", inner.getString("fieldName"));
    }

    @Test
    public void toXmlPretty() throws Exception {

        JsonObject jsonObj = this.jsonManager.create();
        jsonObj.set("someInt", 123);

        String xml = this.xmlManager.toXml(jsonObj, true);
        assertNotNull(xml);

        StringBuilder expected = new StringBuilder();
        expected.append("<JsonObject>\n");
        expected.append("    <someInt>123</someInt>\n");
        expected.append("</JsonObject>\n");

        String expectedStr = expected.toString();
        assertEquals(expectedStr, xml);
    }

    protected static class User {

        public String name;
        public int age;
    }

    @Test
    public void toXmlPretty2() throws Exception {

        User user = new User();
        user.name = "Stromgol";
        user.age = 42;

        String xml = this.xmlManager.toXml(user, true);
        assertNotNull(xml);

        StringBuilder expected = new StringBuilder();

        expected.append("<User>\n");
        expected.append("    <name>Stromgol</name>\n");
        expected.append("    <age>42</age>\n");
        expected.append("</User>\n");

        assertEquals(expected.toString(), xml);
    }

    @Test
    public void fromXmlToJsonObject() throws Exception {

        // @formatter:off
        String xml = "<JsonObject>" + 
                         "<innerObj>" + 
                             "<someBoolean>true</someBoolean>" + 
                             "<someInt>123</someInt>" +
                         "</innerObj>" + 
                         "<anotherBoolean>true</anotherBoolean>" + 
                         "<someArray isArray=\"true\">" +
                             "<element>toto</element>" + 
                             "<element>123</element>" + 
                         "</someArray>" +
                         "<anotherInt>44444</anotherInt>" + 
                     "</JsonObject>";
        // @formatter:on

        JsonObject jsonObject = this.xmlManager.fromXml(xml);
        assertNotNull(jsonObject);

        JsonObject innerObj = jsonObject.getJsonObject("innerObj");
        assertNotNull(innerObj);
        assertEquals(true, innerObj.getBoolean("someBoolean"));
        assertEquals(new Integer(123), innerObj.getInteger("someInt"));

        JsonArray array = jsonObject.getJsonArray("someArray");
        assertNotNull(array);
        assertEquals(2, array.size());
        assertEquals("toto", array.getString(0));
        assertEquals(new Integer(123), array.getInteger(1));
    }

    @Test
    public void fromXmlToJsonObjectEmtpyObject() throws Exception {

        // @formatter:off
        String xml = "<JsonObject>" + 
                         "<innerObj></innerObj>" + 
                     "</JsonObject>";
        // @formatter:on

        JsonObject jsonObject = this.xmlManager.fromXml(xml);
        assertNotNull(jsonObject);

        JsonObject innerObj = jsonObject.getJsonObject("innerObj");
        assertNotNull(innerObj);

    }

    @Test
    public void fromXmlToJsonObjectEmtpyArray() throws Exception {

        // @formatter:off
        String xml = "<JsonObject>" + 
                         "<someArray isArray=\"true\"></someArray>" + 
                     "</JsonObject>";
        // @formatter:on

        JsonObject jsonObject = this.xmlManager.fromXml(xml);
        assertNotNull(jsonObject);

        JsonArray array = jsonObject.getJsonArray("someArray");
        assertNotNull(array);
    }

    @Test
    public void fromXmlToJsonObjectInObjects() throws Exception {

        // @formatter:off
        String xml = "<JsonObject>" + 
                         "<someObj>" + 
                             "<tutu>222</tutu>" + 
                             "<otherObj>" + 
                                 "<coco>333</coco>" +
                                 "<thirdObj>" + 
                                     "<kiki>444</kiki>" + 
                                 "</thirdObj>" + 
                             "</otherObj>" + 
                         "</someObj>" + 
                     "</JsonObject>";
        // @formatter:on

        JsonObject jsonObject = this.xmlManager.fromXml(xml);
        assertNotNull(jsonObject);

        JsonObject someObj = jsonObject.getJsonObject("someObj");
        assertNotNull(someObj);
        assertEquals(new Integer(222), someObj.getInteger("tutu"));
        JsonObject otherObj = someObj.getJsonObject("otherObj");
        assertNotNull(otherObj);
        assertEquals("333", otherObj.getString("coco"));

        JsonObject thirdObj = otherObj.getJsonObject("thirdObj");
        assertNotNull(thirdObj);
        assertEquals("444", thirdObj.getString("kiki"));
    }

    @Test
    public void fromXmlToJsonObjectNotRealArray() throws Exception {

        // @formatter:off
        String xml = "<JsonObject>" + 
                         "<someArray isArray=\"nope\">" + 
                             "<element>123</element>" + 
                         "</someArray>" +
                     "</JsonObject>";
         // @formatter:on

        JsonObject jsonObject = this.xmlManager.fromXml(xml);
        assertNotNull(jsonObject);

        JsonObject falseArray = jsonObject.getJsonObject("someArray");
        assertNotNull(falseArray);
        assertEquals("nope", falseArray.getString("isArray"));
        assertEquals("123", falseArray.getString("element"));
    }

    @Test
    public void fromXmlToJsonObjectWithArray() throws Exception {

        // @formatter:off
        String xml = "<JsonObject>" + 
                         "<someArray isArray=\"true\">" + 
                             "<element>111</element>" + 
                         "</someArray>" +
                     "</JsonObject>";
        // @formatter:on

        JsonObject jsonObject = this.xmlManager.fromXml(xml);
        assertNotNull(jsonObject);

        JsonArray array = jsonObject.getJsonArray("someArray");
        assertNotNull(array);
        assertEquals(1, array.size());
        assertEquals("111", array.getString(0));
    }

    @Test
    public void fromXmlToJsonObjectWith2Arrays() throws Exception {

        // @formatter:off
        String xml = "<JsonObject>" + 
                         "<someArray isArray=\"true\">" + 
                             "<element>111</element>" + 
                         "</someArray>" +
                         "<otherArray isArray=\"true\">" + 
                             "<element>222</element>" + 
                         "</otherArray>" + 
                     "</JsonObject>";
        // @formatter:on

        JsonObject jsonObject = this.xmlManager.fromXml(xml);
        assertNotNull(jsonObject);

        JsonArray array = jsonObject.getJsonArray("someArray");
        assertNotNull(array);
        assertEquals(1, array.size());
        assertEquals("111", array.getString(0));

        array = jsonObject.getJsonArray("otherArray");
        assertNotNull(array);
        assertEquals(1, array.size());
        assertEquals("222", array.getString(0));
    }

    @Test
    public void fromXmlToJsonObjectWithArrayInArray() throws Exception {

        // @formatter:off
        String xml = "<JsonObject>" + 
                         "<someArray isArray=\"true\">" + 
                             "<element>" + 
                                 "<otherArray isArray=\"true\">" +
                                     "<element>222</element>" + 
                                 "</otherArray>" + 
                             "</element>" + 
                         "</someArray>" + 
                     "</JsonObject>";
         // @formatter:on

        JsonObject jsonObject = this.xmlManager.fromXml(xml);
        assertNotNull(jsonObject);

        JsonArray array = jsonObject.getJsonArray("someArray");
        assertNotNull(array);
        assertEquals(1, array.size());

        array = array.getJsonArray(0);
        assertNotNull(array);
        assertEquals(1, array.size());
        assertEquals("222", array.getString(0));
    }

    @Test
    public void toXmlMultiFieldElementInArray() throws Exception {

        JsonObject jsonObject = this.jsonManager.create();

        JsonArray array = this.jsonManager.createArray();
        jsonObject.set("someArray", array);

        JsonObject inner = this.jsonManager.create();
        inner.set("fieldName", "email");
        inner.set("message", "Not a valid email address.");
        inner.set("type", "VALIDATION_TYPE_EMAIL");
        array.add(inner);

        String xml = this.xmlManager.toXml(jsonObject, true);

        JsonObject result = this.xmlManager.fromXml(xml);
        assertNotNull(result);

        JsonArray resultArray = result.getJsonArray("someArray");
        assertNotNull(resultArray);
        assertEquals(1, resultArray.size());

        JsonObject resultInner = resultArray.getJsonObject(0);
        assertNotNull(resultInner);
        assertEquals("email", resultInner.getString("fieldName"));
        assertEquals("Not a valid email address.", resultInner.getString("message"));
        assertEquals("VALIDATION_TYPE_EMAIL", resultInner.getString("type"));
    }

    @Test
    public void fromXmlToJsonArray() throws Exception {

        // @formatter:off
        String xml = "<someArray isArray=\"true\">" + 
                         "<element>" + 
                             "<otherArray isArray=\"true\">" +
                                 "<element>222</element>" + 
                             "</otherArray>" + 
                         "</element>" + 
                         "<element>123</element>" +
                     "</someArray>";
        // @formatter:on

        JsonArray jsonArray = this.xmlManager.fromXmlToJsonArray(xml);
        assertNotNull(jsonArray);
        assertEquals(2, jsonArray.size());

        JsonArray otherArray = jsonArray.getJsonArray(0);
        assertNotNull(otherArray);
        assertEquals(1, otherArray.size());
        assertEquals("222", otherArray.getString(0));

        assertEquals(new Integer(123), jsonArray.getInteger(1));
    }

    @Test
    public void fromXmlJsonObjectOnePropOnly() throws Exception {

        String xml = "<root><name>coco</name></root>";

        JsonObject obj = this.xmlManager.fromXml(xml);
        assertNotNull(obj);
        assertEquals("coco", obj.getString("name"));
    }

    @Test
    public void fromXmlJsonObjectOnePropAndDirectValue() throws Exception {

        String xml = "<root>directValue<name>coco</name></root>";

        try {
            this.xmlManager.fromXml(xml);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void fromXmlJsonObjectOnePropAndDirectValue2() throws Exception {

        String xml = "<root><name>coco</name>directValue</root>";

        //==========================================
        // It seems Jackson ignore the "directValue"
        // value here.
        //==========================================
        JsonObject obj = this.xmlManager.fromXml(xml);
        assertNotNull(obj);
        assertEquals("coco", obj.getString("name"));
    }

    @Test
    public void fromXmlDirectValue() throws Exception {

        String xml = "<root>coco</root>";

        try {
            this.xmlManager.fromXml(xml);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void multipleRoots() throws Exception {

        String xml = "<root1>coco</root1><root2>coco</root2>";

        try {
            this.xmlManager.fromXml(xml);
            fail();
        } catch (Exception ex) {
            System.out.println();
        }
    }

    @Test
    public void fromXmlDirectValueInArray() throws Exception {

        //@formatter:off
        String xml = "<someArray isArray=\"true\">" 
                        + "<element>"
                            + "<otherObj2>coco</otherObj2>"
                        + "</element>" 
                    + "</someArray>";
        //@formatter:on

        JsonArray jsonArray = this.xmlManager.fromXmlToJsonArray(xml);
        assertNotNull(jsonArray);
        assertEquals(1, jsonArray.size());

        JsonObject obj = jsonArray.getJsonObject(0);
        assertNotNull(obj);
        assertEquals("coco", obj.getString("otherObj2"));
    }

    @Test
    public void fromXmlComplex() throws Exception {

        //@formatter:off
        String xml = "<someArray isArray=\"true\">"
                        + "<element>"
                            + "<otherArray isArray=\"true\">"
                                + "<element>222</element>"
                                + "<element>"
                                    + "<someObj>"
                                        + "<titi>tutu</titi>"
                                        + "<toto>333</toto>"
                                        + "<thirdArray isArray=\"true\">"
                                            + "<element>444</element>"
                                        + "</thirdArray>"
                                    + "</someObj>"
                                + "</element>"
                                + "<element>"
                                    + "<otherObj></otherObj>"
                                + "</element>"  
                            + "</otherArray>"  
                        + "</element>"
                        + "<element>123</element>"   
                        + "<element>"
                            + "<directValue>coco</directValue>"
                        + "</element>" 
                    + "</someArray>";
        //@formatter:on

        JsonArray jsonArray = this.xmlManager.fromXmlToJsonArray(xml);
        assertNotNull(jsonArray);
        assertEquals(3, jsonArray.size());

        JsonArray otherArray = jsonArray.getJsonArray(0);
        assertNotNull(otherArray);
        assertEquals(3, otherArray.size());
        assertEquals("222", otherArray.getString(0));
        JsonObject someObj = otherArray.getJsonObject(1);
        assertNotNull(someObj);
        assertEquals("tutu", someObj.getString("titi"));
        assertEquals(new Integer(333), someObj.getInteger("toto"));
        JsonArray thirdArray = someObj.getJsonArray("thirdArray");
        assertNotNull(thirdArray);
        assertEquals(1, thirdArray.size());
        assertEquals("444", thirdArray.getString(0));

        JsonObject otherObj = otherArray.getJsonObject(2);
        assertNotNull(otherObj);

        assertEquals(new Integer(123), jsonArray.getInteger(1));

        JsonObject directObj = jsonArray.getJsonObject(2);
        assertNotNull(directObj);
        assertEquals("coco", directObj.getString("directValue"));
    }

    @Test
    public void fromXmlToJsonArrayNoSpecialAttributeAtRoot1() throws Exception {

        // @formatter:off
        String xml = "<someArray>" + 
                         "<element>111</element>" + 
                         "<element>222</element>" + 
                     "</someArray>";
        // @formatter:on

        JsonArray jsonArray = this.xmlManager.fromXmlToJsonArray(xml);
        assertNotNull(jsonArray);
        assertEquals("111", jsonArray.getString(0));
        assertEquals("222", jsonArray.getString(1));
    }

    @Test
    public void fromXmlToJsonArrayNoSpecialAttributeAtRoot2() throws Exception {

        // @formatter:off
        String xml = "<someArray>" + 
                         "<element>111</element>" + 
                         "<element>" + 
                             "<obj>" + 
                                 "<titi>toto</titi>" + 
                             "</obj>" +
                         "</element>" + 
                     "</someArray>";
        // @formatter:on

        JsonArray jsonArray = this.xmlManager.fromXmlToJsonArray(xml);
        assertNotNull(jsonArray);
        assertEquals("111", jsonArray.getString(0));

        JsonObject obj = jsonArray.getJsonObject(1);
        assertNotNull(obj);
        assertEquals("toto", obj.getString("titi"));
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
    public void dependenciesInjectionFromXml() throws Exception {

        TestObject obj = new TestObject();
        obj.setName("test");
        assertNull(obj.getSpincastConfig());

        String xml = this.xmlManager.toXml(obj);
        assertNotNull(xml);

        obj = this.xmlManager.fromXml(xml, TestObject.class);
        assertNotNull(obj);
        assertNotNull(obj.getSpincastConfig());
        assertEquals("test", obj.getName());
    }

    @Test
    public void dependenciesInjectionFromXmlToType() throws Exception {

        TestObject obj = new TestObject();
        obj.setName("test");
        assertNull(obj.getSpincastConfig());

        String xml = this.xmlManager.toXml(obj);
        assertNotNull(xml);

        obj = this.xmlManager.fromXmlToType(xml, TestObject.class);
        assertNotNull(obj);
        assertNotNull(obj.getSpincastConfig());
        assertEquals("test", obj.getName());
    }

    @Test
    public void dependenciesInjectionOnDeserializationFromInputStream() throws Exception {

        InputStream stream = getClass().getClassLoader().getResourceAsStream("obj.xml");
        assertNotNull(stream);

        TestObject obj = this.xmlManager.fromXmlInputStream(stream, TestObject.class);
        assertNotNull(obj);
        assertNotNull(obj.getSpincastConfig());
        assertEquals("test", obj.getName());
    }

    @Test
    public void dependenciesInjectionOnCreateFromXmlToJsonObject() throws Exception {

        JsonObject obj = this.xmlManager.fromXml("<TestObject><name>test</name></TestObject>");
        assertNotNull(obj);

        assertTrue(obj instanceof JsonObjectDefault);

        Field jsonManagerField = JsonObjectArrayBase.class.getDeclaredField("jsonManager");
        assertNotNull(jsonManagerField);

        jsonManagerField.setAccessible(true);

        Object jsonManager = jsonManagerField.get(obj);
        assertNotNull(jsonManager);
    }

    protected static class NoPropToSerialize {

        @JsonIgnore
        public String test;

    }

    @Test
    public void emptyObject() throws Exception {

        JsonObject jsonObj = this.jsonManager.create();
        assertNotNull(jsonObj);

        String xml = this.xmlManager.toXml(new NoPropToSerialize());
        assertNotNull(xml);

        JsonArray jsonArray = this.jsonManager.fromStringArray("[]");
        assertNotNull(jsonArray);
        xml = this.xmlManager.toXml(new NoPropToSerialize[0]);
        assertNotNull(xml);

    }

    @Test
    public void fromXmlWithNamespacesToJsonObject() throws Exception {

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<rss xmlns:dc=\"http://purl.org/dc/elements/1.1/\" version=\"2.0\">\n");
        xml.append("  <channel>\n");
        xml.append("    <title>Spincast Framework</title>\n");
        xml.append("    <link>http://localhost:57125</link>\n");
        xml.append("    <description>What's new about the Spincast framework?</description>\n");
        xml.append("    <image>\n");
        xml.append("      <title>Spincast Framework</title>\n");
        xml.append("      <url>http://localhost:57125/public/images/feed.png</url>\n");
        xml.append("    </image>\n");
        xml.append("    <item>\n");
        xml.append("      <title>my title</title>\n");
        xml.append("      <description>&lt;p&gt;my description&lt;/p&gt;</description>\n");
        xml.append("      <pubDate>Sun, 02 Jan 2000 05:00:00 GMT</pubDate>\n");
        xml.append("      <dc:date>2000-01-02T05:00:00Z</dc:date>\n");
        xml.append("    </item>\n");
        xml.append("  </channel>\n");
        xml.append("</rss>\n");

        JsonObject jsonObject = this.xmlManager.fromXml(xml.toString());
        assertNotNull(jsonObject);

        JsonObject channelObj = jsonObject.getJsonObject("channel");
        assertNotNull(channelObj);

        JsonObject itemObj = channelObj.getJsonObject("item");
        assertNotNull(channelObj);

        //==========================================
        // Currently, the namespaces are not kept when
        // deserializing XML. Is that what we need?
        //==========================================
        String dcDate = itemObj.getString("date");
        assertNotNull(dcDate);
        assertEquals("2000-01-02T05:00:00Z", dcDate);
    }

}
