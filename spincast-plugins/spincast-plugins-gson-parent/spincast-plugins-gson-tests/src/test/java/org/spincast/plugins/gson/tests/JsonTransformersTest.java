package org.spincast.plugins.gson.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.json.ElementTransformer;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonObject;
import org.spincast.plugins.gson.tests.utils.GsonTestBase;

public class JsonTransformersTest extends GsonTestBase {

    private ElementTransformer spaceToStarTransformer;

    protected ElementTransformer getSpaceToStarTransformer() {
        if (this.spaceToStarTransformer == null) {
            this.spaceToStarTransformer = new ElementTransformer() {

                @Override
                public Object transform(Object obj) {

                    if (obj == null) {
                        return null;
                    }

                    if (!(obj instanceof String)) {
                        return obj;
                    }

                    return ((String)obj).replace(" ", "*");
                }
            };
        }
        return this.spaceToStarTransformer;
    }

    private ElementTransformer aToDollarTransformer;

    protected ElementTransformer getAToDollarTransformer() {
        if (this.aToDollarTransformer == null) {
            this.aToDollarTransformer = new ElementTransformer() {

                @Override
                public Object transform(Object obj) {

                    if (obj == null) {
                        return null;
                    }

                    if (!(obj instanceof String)) {
                        return obj;
                    }

                    return ((String)obj).replace("a", "$");
                }
            };
        }
        return this.aToDollarTransformer;
    }

    @Test
    public void trimAllDefault() throws Exception {

        JsonObject innerObj = getJsonManager().create();
        innerObj.set("keyInner1", "x  ");
        innerObj.set("keyInner2", " ");

        JsonObject obj = getJsonManager().create();
        obj.set("key1", "a");
        obj.set("key2", "b  ");
        obj.set("key3", "   c   ");
        obj.set("key4", "      ");
        obj.set("key5", null);
        obj.set("key6", 123);
        obj.set("inner", innerObj);

        assertEquals("a", obj.getObject("key1"));
        assertEquals("b  ", obj.getObject("key2"));
        assertEquals("   c   ", obj.getObject("key3"));
        assertEquals("      ", obj.getObject("key4"));
        assertEquals(null, obj.getObject("key5"));

        JsonObject innerResult = obj.getJsonObject("inner");
        assertNotNull(innerResult);
        assertEquals("x  ", innerResult.getObject("keyInner1"));
        assertEquals(" ", innerResult.getObject("keyInner2"));

        // transformation
        obj.trimAll();

        assertEquals("a", obj.getObject("key1"));
        assertEquals("b", obj.getObject("key2"));
        assertEquals("c", obj.getObject("key3"));
        assertEquals("", obj.getObject("key4"));
        assertEquals(null, obj.getObject("key5"));

        innerResult = obj.getJsonObject("inner");
        assertNotNull(innerResult);
        assertEquals("x", innerResult.getObject("keyInner1"));
        assertEquals("", innerResult.getObject("keyInner2"));
    }

    @Test
    public void trimAllRecursive() throws Exception {

        JsonObject innerObj = getJsonManager().create();
        innerObj.set("keyInner1", "x  ");
        innerObj.set("keyInner2", " ");

        JsonObject obj = getJsonManager().create();
        obj.set("key1", "a");
        obj.set("key2", "b  ");
        obj.set("key3", "   c   ");
        obj.set("key4", "      ");
        obj.set("key5", null);
        obj.set("key6", 123);
        obj.set("inner", innerObj);

        assertEquals("a", obj.getObject("key1"));
        assertEquals("b  ", obj.getObject("key2"));
        assertEquals("   c   ", obj.getObject("key3"));
        assertEquals("      ", obj.getObject("key4"));
        assertEquals(null, obj.getObject("key5"));

        JsonObject innerResult = obj.getJsonObject("inner");
        assertNotNull(innerResult);
        assertEquals("x  ", innerResult.getObject("keyInner1"));
        assertEquals(" ", innerResult.getObject("keyInner2"));

        // transformation
        obj.trimAll(true);

        assertEquals("a", obj.getObject("key1"));
        assertEquals("b", obj.getObject("key2"));
        assertEquals("c", obj.getObject("key3"));
        assertEquals("", obj.getObject("key4"));
        assertEquals(null, obj.getObject("key5"));

        innerResult = obj.getJsonObject("inner");
        assertNotNull(innerResult);
        assertEquals("x", innerResult.getObject("keyInner1"));
        assertEquals("", innerResult.getObject("keyInner2"));
    }

    @Test
    public void trimAllNotRecursive() throws Exception {

        JsonObject innerObj = getJsonManager().create();
        innerObj.set("keyInner1", "x  ");
        innerObj.set("keyInner2", " ");

        JsonObject obj = getJsonManager().create();
        obj.set("key1", "a");
        obj.set("key2", "b  ");
        obj.set("key3", "   c   ");
        obj.set("key4", "      ");
        obj.set("key5", null);
        obj.set("key6", 123);
        obj.set("inner", innerObj);

        // transformation
        obj.trimAll(false);

        assertEquals("a", obj.getObject("key1"));
        assertEquals("b", obj.getObject("key2"));
        assertEquals("c", obj.getObject("key3"));
        assertEquals("", obj.getObject("key4"));
        assertEquals(null, obj.getObject("key5"));

        JsonObject innerResult = obj.getJsonObject("inner");
        assertNotNull(innerResult);
        assertEquals("x  ", innerResult.getObject("keyInner1"));
        assertEquals(" ", innerResult.getObject("keyInner2"));
    }

    @Test
    public void trimOneOnly() throws Exception {

        JsonObject innerObj = getJsonManager().create();
        innerObj.set("keyInner1", "x  ");
        innerObj.set("keyInner2", " ");

        JsonObject obj = getJsonManager().create();
        obj.set("key1", "a");
        obj.set("key2", "b  ");
        obj.set("key3", "   c   ");
        obj.set("key4", "      ");
        obj.set("key5", null);
        obj.set("key6", 123);
        obj.set("inner", innerObj);

        // transformation
        obj.trim("key3");

        assertEquals("a", obj.getObject("key1"));
        assertEquals("b  ", obj.getObject("key2"));
        assertEquals("c", obj.getObject("key3"));
        assertEquals("      ", obj.getObject("key4"));
        assertEquals(null, obj.getObject("key5"));

        JsonObject innerResult = obj.getJsonObject("inner");
        assertNotNull(innerResult);
        assertEquals("x  ", innerResult.getObject("keyInner1"));
        assertEquals(" ", innerResult.getObject("keyInner2"));

        // transformation
        obj.trim("inner.keyInner1");
        obj.trim("inner"); // no effect
        obj.trim("key5"); // no effect

        assertEquals("a", obj.getObject("key1"));
        assertEquals("b  ", obj.getObject("key2"));
        assertEquals("c", obj.getObject("key3"));
        assertEquals("      ", obj.getObject("key4"));
        assertEquals(null, obj.getObject("key5"));

        innerResult = obj.getJsonObject("inner");
        assertNotNull(innerResult);
        assertEquals("x", innerResult.getObject("keyInner1"));
        assertEquals(" ", innerResult.getObject("keyInner2"));
    }

    @Test
    public void trimAllDefaultArray() throws Exception {

        JsonArray inner = getJsonManager().createArray();
        inner.add("x  ");
        inner.add(" ");

        JsonArray array = getJsonManager().createArray();
        array.add("a");
        array.add("b  ");
        array.add("   c   ");
        array.add("      ");
        array.add(null);
        array.add(123);
        array.add(inner);

        assertEquals("a", array.getObject(0));
        assertEquals("b  ", array.getObject(1));
        assertEquals("   c   ", array.getObject(2));
        assertEquals("      ", array.getObject(3));
        assertEquals(null, array.getObject(4));
        assertEquals(123, array.getObject(5));

        JsonArray innerResult = array.getJsonArray(6);
        assertNotNull(innerResult);
        assertEquals("x  ", innerResult.getObject(0));
        assertEquals(" ", innerResult.getObject(1));

        // transformation
        array.trimAll();

        assertEquals("a", array.getObject(0));
        assertEquals("b", array.getObject(1));
        assertEquals("c", array.getObject(2));
        assertEquals("", array.getObject(3));
        assertEquals(null, array.getObject(4));
        assertEquals(123, array.getObject(5));

        innerResult = array.getJsonArray(6);
        assertNotNull(innerResult);
        assertEquals("x", innerResult.getObject(0));
        assertEquals("", innerResult.getObject(1));
    }

    @Test
    public void trimAllRecursiveArray() throws Exception {

        JsonArray inner = getJsonManager().createArray();
        inner.add("x  ");
        inner.add(" ");

        JsonArray array = getJsonManager().createArray();
        array.add("a");
        array.add("b  ");
        array.add("   c   ");
        array.add("      ");
        array.add(null);
        array.add(123);
        array.add(inner);

        // transformation
        array.trimAll(true);

        assertEquals("a", array.getObject(0));
        assertEquals("b", array.getObject(1));
        assertEquals("c", array.getObject(2));
        assertEquals("", array.getObject(3));
        assertEquals(null, array.getObject(4));
        assertEquals(123, array.getObject(5));

        JsonArray innerResult = array.getJsonArray(6);
        assertNotNull(innerResult);
        assertEquals("x", innerResult.getObject(0));
        assertEquals("", innerResult.getObject(1));
    }

    @Test
    public void trimAllNotRecursiveArray() throws Exception {

        JsonArray inner = getJsonManager().createArray();
        inner.add("x  ");
        inner.add(" ");

        JsonArray array = getJsonManager().createArray();
        array.add("a");
        array.add("b  ");
        array.add("   c   ");
        array.add("      ");
        array.add(null);
        array.add(123);
        array.add(inner);

        // transformation
        array.trimAll(false);

        assertEquals("a", array.getObject(0));
        assertEquals("b", array.getObject(1));
        assertEquals("c", array.getObject(2));
        assertEquals("", array.getObject(3));
        assertEquals(null, array.getObject(4));
        assertEquals(123, array.getObject(5));

        JsonArray innerResult = array.getJsonArray(6);
        assertNotNull(innerResult);
        assertEquals("x  ", innerResult.getObject(0));
        assertEquals(" ", innerResult.getObject(1));
    }

    @Test
    public void trimOneOnlyArray() throws Exception {

        JsonArray inner = getJsonManager().createArray();
        inner.add("x  ");
        inner.add(" ");

        JsonArray array = getJsonManager().createArray();
        array.add("a");
        array.add("b  ");
        array.add("   c   ");
        array.add("      ");
        array.add(null);
        array.add(123);
        array.add(inner);

        // transformation
        array.trim(2);
        array.trim(6); // no effect
        array.trim(5); // no effect

        assertEquals("a", array.getObject(0));
        assertEquals("b  ", array.getObject(1));
        assertEquals("c", array.getObject(2));
        assertEquals("      ", array.getObject(3));
        assertEquals(null, array.getObject(4));
        assertEquals(123, array.getObject(5));

        JsonArray innerResult = array.getJsonArray(6);
        assertNotNull(innerResult);
        assertEquals("x  ", innerResult.getObject(0));
        assertEquals(" ", innerResult.getObject(1));
    }

    @Test
    public void trimMix() throws Exception {

        JsonArray inner = getJsonManager().createArray();
        inner.add("x  ");
        inner.add("z  ");

        JsonObject objInner = getJsonManager().create();
        objInner.set("key1", "a");
        objInner.set("key2", "b  ");
        objInner.set("key3", "   c   ");
        objInner.set("key4", "      ");
        objInner.set("key5", null);
        objInner.set("key6", 123);
        objInner.set("inner", inner);

        JsonArray array = getJsonManager().createArray();
        array.add("a");
        array.add("b  ");
        array.add("   c   ");
        array.add("      ");
        array.add(null);
        array.add(123);
        array.add(objInner);

        JsonObject obj = getJsonManager().create();
        obj.set("key1", "a  ");
        obj.set("key2", array);
        obj.set("key3", "b  ");

        assertEquals("a  ", obj.getObject("key1"));
        assertEquals("b  ", obj.getObject("key3"));

        JsonArray arrayResult = obj.getJsonArray("key2");
        assertNotNull(arrayResult);
        assertEquals("a", arrayResult.getObject(0));
        assertEquals("b  ", arrayResult.getObject(1));
        assertEquals("   c   ", arrayResult.getObject(2));
        assertEquals("      ", arrayResult.getObject(3));
        assertEquals(null, arrayResult.getObject(4));
        assertEquals(new Integer(123), arrayResult.getObject(5));

        JsonObject objResult = arrayResult.getJsonObject(6);
        assertNotNull(objResult);
        assertEquals("a", objResult.getObject("key1"));
        assertEquals("b  ", objResult.getObject("key2"));
        assertEquals("   c   ", objResult.getObject("key3"));
        assertEquals("      ", objResult.getObject("key4"));
        assertEquals(null, objResult.getObject("key5"));
        assertEquals(new Integer(123), objResult.getObject("key6"));

        JsonArray array2Result = objResult.getJsonArray("inner");
        assertNotNull(array2Result);
        assertEquals("x  ", array2Result.getObject(0));
        assertEquals("z  ", array2Result.getObject(1));

        // transformation
        obj.trim("key2[6].inner[1]");
        obj.trim("key2[2]");
        obj.trim("key2[6].key3");

        assertEquals("a  ", obj.getObject("key1"));
        assertEquals("b  ", obj.getObject("key3"));

        arrayResult = obj.getJsonArray("key2");
        assertNotNull(arrayResult);
        assertEquals("a", arrayResult.getObject(0));
        assertEquals("b  ", arrayResult.getObject(1));
        assertEquals("c", arrayResult.getObject(2));
        assertEquals("      ", arrayResult.getObject(3));
        assertEquals(null, arrayResult.getObject(4));
        assertEquals(new Integer(123), arrayResult.getObject(5));

        objResult = arrayResult.getJsonObject(6);
        assertNotNull(objResult);
        assertEquals("a", objResult.getObject("key1"));
        assertEquals("b  ", objResult.getObject("key2"));
        assertEquals("c", objResult.getObject("key3"));
        assertEquals("      ", objResult.getObject("key4"));
        assertEquals(null, objResult.getObject("key5"));
        assertEquals(new Integer(123), objResult.getObject("key6"));

        array2Result = objResult.getJsonArray("inner");
        assertNotNull(array2Result);
        assertEquals("x  ", array2Result.getObject(0));
        assertEquals("z", array2Result.getObject(1));
    }

    @Test
    public void customTransformerAllDefault() throws Exception {

        JsonObject innerObj = getJsonManager().create();
        innerObj.set("keyInner1", "x  ");
        innerObj.set("keyInner2", " ");

        JsonObject obj = getJsonManager().create();
        obj.set("key1", "a");
        obj.set("key2", "b  ");
        obj.set("key3", "   c   ");
        obj.set("key4", "      ");
        obj.set("key5", null);
        obj.set("key6", 123);
        obj.set("inner", innerObj);

        assertEquals("a", obj.getObject("key1"));
        assertEquals("b  ", obj.getObject("key2"));
        assertEquals("   c   ", obj.getObject("key3"));
        assertEquals("      ", obj.getObject("key4"));
        assertEquals(null, obj.getObject("key5"));

        JsonObject innerResult = obj.getJsonObject("inner");
        assertNotNull(innerResult);
        assertEquals("x  ", innerResult.getObject("keyInner1"));
        assertEquals(" ", innerResult.getObject("keyInner2"));

        // transformation
        obj.transformAll(getSpaceToStarTransformer());

        assertEquals("a", obj.getObject("key1"));
        assertEquals("b**", obj.getObject("key2"));
        assertEquals("***c***", obj.getObject("key3"));
        assertEquals("******", obj.getObject("key4"));
        assertEquals(null, obj.getObject("key5"));

        innerResult = obj.getJsonObject("inner");
        assertNotNull(innerResult);
        assertEquals("x**", innerResult.getObject("keyInner1"));
        assertEquals("*", innerResult.getObject("keyInner2"));
    }

    @Test
    public void customTransformerAllRecursive() throws Exception {

        JsonObject innerObj = getJsonManager().create();
        innerObj.set("keyInner1", "x  ");
        innerObj.set("keyInner2", " ");

        JsonObject obj = getJsonManager().create();
        obj.set("key1", "a");
        obj.set("key2", "b  ");
        obj.set("key3", "   c   ");
        obj.set("key4", "      ");
        obj.set("key5", null);
        obj.set("key6", 123);
        obj.set("inner", innerObj);

        assertEquals("a", obj.getObject("key1"));
        assertEquals("b  ", obj.getObject("key2"));
        assertEquals("   c   ", obj.getObject("key3"));
        assertEquals("      ", obj.getObject("key4"));
        assertEquals(null, obj.getObject("key5"));

        JsonObject innerResult = obj.getJsonObject("inner");
        assertNotNull(innerResult);
        assertEquals("x  ", innerResult.getObject("keyInner1"));
        assertEquals(" ", innerResult.getObject("keyInner2"));

        // transformation
        obj.transformAll(getSpaceToStarTransformer());

        assertEquals("a", obj.getObject("key1"));
        assertEquals("b**", obj.getObject("key2"));
        assertEquals("***c***", obj.getObject("key3"));
        assertEquals("******", obj.getObject("key4"));
        assertEquals(null, obj.getObject("key5"));

        innerResult = obj.getJsonObject("inner");
        assertNotNull(innerResult);
        assertEquals("x**", innerResult.getObject("keyInner1"));
        assertEquals("*", innerResult.getObject("keyInner2"));
    }

    @Test
    public void customTransformerAllNotRecursive() throws Exception {

        JsonObject innerObj = getJsonManager().create();
        innerObj.set("keyInner1", "x  ");
        innerObj.set("keyInner2", " ");

        JsonObject obj = getJsonManager().create();
        obj.set("key1", "a");
        obj.set("key2", "b  ");
        obj.set("key3", "   c   ");
        obj.set("key4", "      ");
        obj.set("key5", null);
        obj.set("key6", 123);
        obj.set("inner", innerObj);

        // transformation
        obj.transformAll(getSpaceToStarTransformer(), false);

        assertEquals("a", obj.getObject("key1"));
        assertEquals("b**", obj.getObject("key2"));
        assertEquals("***c***", obj.getObject("key3"));
        assertEquals("******", obj.getObject("key4"));
        assertEquals(null, obj.getObject("key5"));
        assertEquals(123, obj.getObject("key6"));

        JsonObject innerResult = obj.getJsonObject("inner");
        assertNotNull(innerResult);
        assertEquals("x  ", innerResult.getObject("keyInner1"));
        assertEquals(" ", innerResult.getObject("keyInner2"));
    }

    @Test
    public void customTransformerOneOnly() throws Exception {

        JsonObject innerObj = getJsonManager().create();
        innerObj.set("keyInner1", "x  ");
        innerObj.set("keyInner2", " ");

        JsonObject obj = getJsonManager().create();
        obj.set("key1", "a");
        obj.set("key2", "b  ");
        obj.set("key3", "   c   ");
        obj.set("key4", "      ");
        obj.set("key5", null);
        obj.set("key6", 123);
        obj.set("inner", innerObj);

        // transformation
        obj.transform("key3", getSpaceToStarTransformer());

        assertEquals("a", obj.getObject("key1"));
        assertEquals("b  ", obj.getObject("key2"));
        assertEquals("***c***", obj.getObject("key3"));
        assertEquals("      ", obj.getObject("key4"));
        assertEquals(null, obj.getObject("key5"));

        JsonObject innerResult = obj.getJsonObject("inner");
        assertNotNull(innerResult);
        assertEquals("x  ", innerResult.getObject("keyInner1"));
        assertEquals(" ", innerResult.getObject("keyInner2"));

        // transformation
        obj.transform("inner.keyInner1", getSpaceToStarTransformer());
        obj.transform("inner", getSpaceToStarTransformer());
        obj.transform("key5", getSpaceToStarTransformer());

        assertEquals("a", obj.getObject("key1"));
        assertEquals("b  ", obj.getObject("key2"));
        assertEquals("***c***", obj.getObject("key3"));
        assertEquals("      ", obj.getObject("key4"));
        assertEquals(null, obj.getObject("key5"));

        innerResult = obj.getJsonObject("inner");
        assertNotNull(innerResult);
        assertEquals("x**", innerResult.getObject("keyInner1"));
        assertEquals(" ", innerResult.getObject("keyInner2"));
    }

    @Test
    public void customTransformerAllDefaultArray() throws Exception {

        JsonArray inner = getJsonManager().createArray();
        inner.add("x  ");
        inner.add(" ");

        JsonArray array = getJsonManager().createArray();
        array.add("a");
        array.add("b  ");
        array.add("   c   ");
        array.add("      ");
        array.add(null);
        array.add(123);
        array.add(inner);

        assertEquals("a", array.getObject(0));
        assertEquals("b  ", array.getObject(1));
        assertEquals("   c   ", array.getObject(2));
        assertEquals("      ", array.getObject(3));
        assertEquals(null, array.getObject(4));
        assertEquals(123, array.getObject(5));

        JsonArray innerResult = array.getJsonArray(6);
        assertNotNull(innerResult);
        assertEquals("x  ", innerResult.getObject(0));
        assertEquals(" ", innerResult.getObject(1));

        // transformation
        array.transformAll(getSpaceToStarTransformer());

        assertEquals("a", array.getObject(0));
        assertEquals("b**", array.getObject(1));
        assertEquals("***c***", array.getObject(2));
        assertEquals("******", array.getObject(3));
        assertEquals(null, array.getObject(4));
        assertEquals(123, array.getObject(5));

        innerResult = array.getJsonArray(6);
        assertNotNull(innerResult);
        assertEquals("x**", innerResult.getObject(0));
        assertEquals("*", innerResult.getObject(1));
    }

    @Test
    public void customTransformerAllRecursiveArray() throws Exception {

        JsonArray inner = getJsonManager().createArray();
        inner.add("x  ");
        inner.add(" ");

        JsonArray array = getJsonManager().createArray();
        array.add("a");
        array.add("b  ");
        array.add("   c   ");
        array.add("      ");
        array.add(null);
        array.add(123);
        array.add(inner);

        // transformation
        array.transformAll(getSpaceToStarTransformer(), true);

        assertEquals("a", array.getObject(0));
        assertEquals("b**", array.getObject(1));
        assertEquals("***c***", array.getObject(2));
        assertEquals("******", array.getObject(3));
        assertEquals(null, array.getObject(4));
        assertEquals(123, array.getObject(5));

        JsonArray innerResult = array.getJsonArray(6);
        assertNotNull(innerResult);
        assertEquals("x**", innerResult.getObject(0));
        assertEquals("*", innerResult.getObject(1));
    }

    @Test
    public void customTransformerAllNotRecursiveArray() throws Exception {

        JsonArray inner = getJsonManager().createArray();
        inner.add("x  ");
        inner.add(" ");

        JsonArray array = getJsonManager().createArray();
        array.add("a");
        array.add("b  ");
        array.add("   c   ");
        array.add("      ");
        array.add(null);
        array.add(123);
        array.add(inner);

        // transformation
        array.transformAll(getSpaceToStarTransformer(), false);

        assertEquals("a", array.getObject(0));
        assertEquals("b**", array.getObject(1));
        assertEquals("***c***", array.getObject(2));
        assertEquals("******", array.getObject(3));
        assertEquals(null, array.getObject(4));
        assertEquals(123, array.getObject(5));

        JsonArray innerResult = array.getJsonArray(6);
        assertNotNull(innerResult);
        assertEquals("x  ", innerResult.getObject(0));
        assertEquals(" ", innerResult.getObject(1));
    }

    @Test
    public void customTransformerOneOnlyArray() throws Exception {

        JsonArray inner = getJsonManager().createArray();
        inner.add("x  ");
        inner.add(" ");

        JsonArray array = getJsonManager().createArray();
        array.add("a");
        array.add("b  ");
        array.add("   c   ");
        array.add("      ");
        array.add(null);
        array.add(123);
        array.add(inner);

        // transformation
        array.transform(2, getSpaceToStarTransformer());
        array.transform(6, getSpaceToStarTransformer());
        array.transform(5, getSpaceToStarTransformer());

        assertEquals("a", array.getObject(0));
        assertEquals("b  ", array.getObject(1));
        assertEquals("***c***", array.getObject(2));
        assertEquals("      ", array.getObject(3));
        assertEquals(null, array.getObject(4));
        assertEquals(123, array.getObject(5));

        JsonArray innerResult = array.getJsonArray(6);
        assertNotNull(innerResult);
        assertEquals("x  ", innerResult.getObject(0));
        assertEquals(" ", innerResult.getObject(1));
    }

    @Test
    public void customTransformerMix() throws Exception {

        JsonArray inner = getJsonManager().createArray();
        inner.add("x  ");
        inner.add("z  ");

        JsonObject objInner = getJsonManager().create();
        objInner.set("key1", "a");
        objInner.set("key2", "b  ");
        objInner.set("key3", "   c   ");
        objInner.set("key4", "      ");
        objInner.set("key5", null);
        objInner.set("key6", 123);
        objInner.set("inner", inner);

        JsonArray array = getJsonManager().createArray();
        array.add("a");
        array.add("b  ");
        array.add("   c   ");
        array.add("      ");
        array.add(null);
        array.add(123);
        array.add(objInner);

        JsonObject obj = getJsonManager().create();
        obj.set("key1", "a  ");
        obj.set("key2", array);
        obj.set("key3", "b  ");

        assertEquals("a  ", obj.getObject("key1"));
        assertEquals("b  ", obj.getObject("key3"));

        JsonArray arrayResult = obj.getJsonArray("key2");
        assertNotNull(arrayResult);
        assertEquals("a", arrayResult.getObject(0));
        assertEquals("b  ", arrayResult.getObject(1));
        assertEquals("   c   ", arrayResult.getObject(2));
        assertEquals("      ", arrayResult.getObject(3));
        assertEquals(null, arrayResult.getObject(4));
        assertEquals(new Integer(123), arrayResult.getObject(5));

        JsonObject objResult = arrayResult.getJsonObject(6);
        assertNotNull(objResult);
        assertEquals("a", objResult.getObject("key1"));
        assertEquals("b  ", objResult.getObject("key2"));
        assertEquals("   c   ", objResult.getObject("key3"));
        assertEquals("      ", objResult.getObject("key4"));
        assertEquals(null, objResult.getObject("key5"));
        assertEquals(new Integer(123), objResult.getObject("key6"));

        JsonArray array2Result = objResult.getJsonArray("inner");
        assertNotNull(array2Result);
        assertEquals("x  ", array2Result.getObject(0));
        assertEquals("z  ", array2Result.getObject(1));

        // transformation
        obj.transform("key2[6].inner[1]", getSpaceToStarTransformer());
        obj.transform("key2[2]", getSpaceToStarTransformer());
        obj.transform("key2[6].key3", getSpaceToStarTransformer());

        obj.transformAll(getAToDollarTransformer());

        assertEquals("$  ", obj.getObject("key1"));
        assertEquals("b  ", obj.getObject("key3"));

        arrayResult = obj.getJsonArray("key2");
        assertNotNull(arrayResult);
        assertEquals("$", arrayResult.getObject(0));
        assertEquals("b  ", arrayResult.getObject(1));
        assertEquals("***c***", arrayResult.getObject(2));
        assertEquals("      ", arrayResult.getObject(3));
        assertEquals(null, arrayResult.getObject(4));
        assertEquals(new Integer(123), arrayResult.getObject(5));

        objResult = arrayResult.getJsonObject(6);
        assertNotNull(objResult);
        assertEquals("$", objResult.getObject("key1"));
        assertEquals("b  ", objResult.getObject("key2"));
        assertEquals("***c***", objResult.getObject("key3"));
        assertEquals("      ", objResult.getObject("key4"));
        assertEquals(null, objResult.getObject("key5"));
        assertEquals(new Integer(123), objResult.getObject("key6"));

        array2Result = objResult.getJsonArray("inner");
        assertNotNull(array2Result);
        assertEquals("x  ", array2Result.getObject(0));
        assertEquals("z**", array2Result.getObject(1));
    }

}
