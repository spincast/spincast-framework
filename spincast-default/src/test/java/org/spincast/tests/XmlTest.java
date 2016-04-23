package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.spincast.core.json.IJsonArray;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.xml.IXmlManager;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;

public class XmlTest extends DefaultIntegrationTestingBase {

    @Inject
    protected IJsonManager jsonManager;

    @Inject
    protected IXmlManager xmlManager;

    @Test
    public void toXml() throws Exception {

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("someBoolean", true);
        jsonObj.put("someInt", 123);

        IJsonArray jsonArray = this.jsonManager.createArray();
        jsonArray.add("toto");
        jsonArray.add(123);

        IJsonObject jsonObj2 = this.jsonManager.create();
        jsonObj2.put("anotherBoolean", true);
        jsonObj2.put("anotherInt", 44444);
        jsonObj2.put("innerObj", jsonObj);
        jsonObj2.put("someArray", jsonArray);

        String xml = this.xmlManager.toXml(jsonObj2);
        assertNotNull(xml);
        assertEquals(("<JsonObject>" + "<innerObj>" + "<someBoolean>true</someBoolean>" + "<someInt>123</someInt>" +
                      "</innerObj>" + "<anotherBoolean>true</anotherBoolean>" + "<someArray isArray=\"true\">" +
                      "<element>toto</element>" + "<element>123</element>" + "</someArray>" +
                      "<anotherInt>44444</anotherInt>" + "</JsonObject>").length(),
                     xml.length());
    }

    @Test
    public void toXmlPretty() throws Exception {

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("someInt", 123);

        String xml = this.xmlManager.toXml(jsonObj, true);
        assertNotNull(xml);

        String[] tokens = xml.split("\n");
        assertEquals(3, tokens.length);
        assertEquals("<JsonObject>", StringUtils.strip(tokens[0], "\r\n\t "));
        assertEquals("<someInt>123</someInt>", StringUtils.strip(tokens[1], "\r\n\t "));
        assertEquals("</JsonObject>", StringUtils.strip(tokens[2], "\r\n\t "));
    }

    @Test
    public void fromXmlToJsonObject() throws Exception {

        String xml = "<JsonObject>" + "<innerObj>" + "<someBoolean>true</someBoolean>" + "<someInt>123</someInt>" +
                     "</innerObj>" + "<anotherBoolean>true</anotherBoolean>" + "<someArray isArray=\"true\">" +
                     "<element>toto</element>" + "<element>123</element>" + "</someArray>" +
                     "<anotherInt>44444</anotherInt>" + "</JsonObject>";

        IJsonObject jsonObject = this.xmlManager.fromXml(xml);
        assertNotNull(jsonObject);

        IJsonObject innerObj = jsonObject.getJsonObject("innerObj");
        assertNotNull(innerObj);
        assertEquals(true, innerObj.getBoolean("someBoolean"));
        assertEquals(new Integer(123), innerObj.getInteger("someInt"));

        IJsonArray array = jsonObject.getJsonArray("someArray");
        assertNotNull(array);
        assertEquals(2, array.size());
        assertEquals("toto", array.getString(0));
        assertEquals(new Integer(123), array.getInteger(1));
    }

    @Test
    public void fromXmlToJsonObjectEmtpyObject() throws Exception {

        String xml = "<JsonObject>" + "<innerObj></innerObj>" + "</JsonObject>";

        IJsonObject jsonObject = this.xmlManager.fromXml(xml);
        assertNotNull(jsonObject);

        IJsonObject innerObj = jsonObject.getJsonObject("innerObj");
        assertNotNull(innerObj);

    }

    @Test
    public void fromXmlToJsonObjectEmtpyArray() throws Exception {

        String xml = "<JsonObject>" + "<someArray isArray=\"true\"></someArray>" + "</JsonObject>";

        IJsonObject jsonObject = this.xmlManager.fromXml(xml);
        assertNotNull(jsonObject);

        IJsonArray array = jsonObject.getJsonArray("someArray");
        assertNotNull(array);

    }

    @Test
    public void fromXmlToJsonObjectInObjects() throws Exception {

        String xml = "<JsonObject>" + "<someObj>" + "<tutu>222</tutu>" + "<otherObj>" + "<coco>333</coco>" +
                     "<thirdObj>" + "<kiki>444</kiki>" + "</thirdObj>" + "</otherObj>" + "</someObj>" + "</JsonObject>";

        IJsonObject jsonObject = this.xmlManager.fromXml(xml);
        assertNotNull(jsonObject);

        IJsonObject someObj = jsonObject.getJsonObject("someObj");
        assertNotNull(someObj);
        assertEquals(new Integer(222), someObj.getInteger("tutu"));
        IJsonObject otherObj = someObj.getJsonObject("otherObj");
        assertNotNull(otherObj);
        assertEquals("333", otherObj.getString("coco"));

        IJsonObject thirdObj = otherObj.getJsonObject("thirdObj");
        assertNotNull(thirdObj);
        assertEquals("444", thirdObj.getString("kiki"));
    }

    @Test
    public void fromXmlToJsonObjectNotRealArray() throws Exception {

        String xml = "<JsonObject>" + "<someArray isArray=\"nope\">" + "<element>123</element>" + "</someArray>" +
                     "</JsonObject>";

        IJsonObject jsonObject = this.xmlManager.fromXml(xml);
        assertNotNull(jsonObject);

        IJsonObject falseArray = jsonObject.getJsonObject("someArray");
        assertNotNull(falseArray);
        assertEquals("nope", falseArray.get("isArray"));
        assertEquals("123", falseArray.get("element"));
    }

    @Test
    public void fromXmlToJsonObjectWithArray() throws Exception {

        String xml = "<JsonObject>" + "<someArray isArray=\"true\">" + "<element>111</element>" + "</someArray>" +
                     "</JsonObject>";

        IJsonObject jsonObject = this.xmlManager.fromXml(xml);
        assertNotNull(jsonObject);

        IJsonArray array = jsonObject.getJsonArray("someArray");
        assertNotNull(array);
        assertEquals(1, array.size());
        assertEquals("111", array.get(0));
    }

    @Test
    public void fromXmlToJsonObjectWith2Arrays() throws Exception {

        String xml = "<JsonObject>" + "<someArray isArray=\"true\">" + "<element>111</element>" + "</someArray>" +
                     "<otherArray isArray=\"true\">" + "<element>222</element>" + "</otherArray>" + "</JsonObject>";

        IJsonObject jsonObject = this.xmlManager.fromXml(xml);
        assertNotNull(jsonObject);

        IJsonArray array = jsonObject.getJsonArray("someArray");
        assertNotNull(array);
        assertEquals(1, array.size());
        assertEquals("111", array.get(0));

        array = jsonObject.getJsonArray("otherArray");
        assertNotNull(array);
        assertEquals(1, array.size());
        assertEquals("222", array.get(0));
    }

    @Test
    public void fromXmlToJsonObjectWithArrayInArray() throws Exception {

        String xml = "<JsonObject>" + "<someArray isArray=\"true\">" + "<element>" + "<otherArray isArray=\"true\">" +
                     "<element>222</element>" + "</otherArray>" + "</element>" + "</someArray>" + "</JsonObject>";

        IJsonObject jsonObject = this.xmlManager.fromXml(xml);
        assertNotNull(jsonObject);

        IJsonArray array = jsonObject.getJsonArray("someArray");
        assertNotNull(array);
        assertEquals(1, array.size());

        array = array.getJsonArray(0);
        assertNotNull(array);
        assertEquals(1, array.size());
        assertEquals("222", array.get(0));
    }

    @Test
    public void fromXmlToJsonArray() throws Exception {

        String xml = "<someArray isArray=\"true\">" + "<element>" + "<otherArray isArray=\"true\">" +
                     "<element>222</element>" + "</otherArray>" + "</element>" + "<element>123</element>" +
                     "</someArray>";

        IJsonArray jsonArray = this.xmlManager.fromXmlToJsonArray(xml);
        assertNotNull(jsonArray);
        assertEquals(2, jsonArray.size());

        IJsonArray otherArray = jsonArray.getJsonArray(0);
        assertNotNull(otherArray);
        assertEquals(1, otherArray.size());
        assertEquals("222", otherArray.get(0));

        assertEquals(new Integer(123), jsonArray.getInteger(1));
    }

    @Test
    public void multipleRoots() throws Exception {

        String xml = "<root1>coco</root1><root2>coco</root2>";

        try {
            this.xmlManager.fromXml(xml);
            fail();
        } catch(Exception ex) {
            System.out.println();
        }
    }

    @Test
    public void fromXmlDirectValue() throws Exception {

        //@formatter:off
        String xml = "<directValue>coco</directValue>";
        //@formatter:on

        IJsonObject obj = this.xmlManager.fromXml(xml);
        assertNotNull(obj);

        // For now, we considere this returns a IJsonObject
        // with an empty key pointing to the value.
        assertEquals("coco", obj.getString(""));
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

        IJsonArray jsonArray = this.xmlManager.fromXmlToJsonArray(xml);
        assertNotNull(jsonArray);
        assertEquals(1, jsonArray.size());

        IJsonObject obj = jsonArray.getJsonObject(0);
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

        IJsonArray jsonArray = this.xmlManager.fromXmlToJsonArray(xml);
        assertNotNull(jsonArray);
        assertEquals(3, jsonArray.size());

        IJsonArray otherArray = jsonArray.getJsonArray(0);
        assertNotNull(otherArray);
        assertEquals(3, otherArray.size());
        assertEquals("222", otherArray.get(0));
        IJsonObject someObj = otherArray.getJsonObject(1);
        assertNotNull(someObj);
        assertEquals("tutu", someObj.getString("titi"));
        assertEquals(new Integer(333), someObj.getInteger("toto"));
        IJsonArray thirdArray = someObj.getJsonArray("thirdArray");
        assertNotNull(thirdArray);
        assertEquals(1, thirdArray.size());
        assertEquals("444", thirdArray.getString(0));

        IJsonObject otherObj = otherArray.getJsonObject(2);
        assertNotNull(otherObj);

        assertEquals(new Integer(123), jsonArray.getInteger(1));

        IJsonObject directObj = jsonArray.getJsonObject(2);
        assertNotNull(directObj);
        assertEquals("coco", directObj.getString("directValue"));

    }

    @Test
    public void fromXmlToJsonArrayNoSpecialAttributeAtRoot1() throws Exception {

        String xml = "<someArray>" + "<element>111</element>" + "<element>222</element>" + "</someArray>";

        IJsonArray jsonArray = this.xmlManager.fromXmlToJsonArray(xml);
        assertNotNull(jsonArray);
        assertEquals("111", jsonArray.get(0));
        assertEquals("222", jsonArray.get(1));
    }

    @Test
    public void fromXmlToJsonArrayNoSpecialAttributeAtRoot2() throws Exception {

        String xml = "<someArray>" + "<element>111</element>" + "<element>" + "<obj>" + "<titi>toto</titi>" + "</obj>" +
                     "</element>" + "</someArray>";

        IJsonArray jsonArray = this.xmlManager.fromXmlToJsonArray(xml);
        assertNotNull(jsonArray);
        assertEquals("111", jsonArray.get(0));

        IJsonObject obj = jsonArray.getJsonObject(1);
        assertNotNull(obj);
        assertEquals("toto", obj.getString("titi"));
    }

}
