package org.spincast.tests.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonObject;
import org.spincast.core.validation.JsonObjectValidationSet;
import org.spincast.core.validation.ValidationMessage;
import org.spincast.core.validation.ValidationSet;

import com.google.common.collect.Lists;

public class SizeValidationTest extends ValidationTestBase {

    @Test
    public void minSizeJsonArrayEquals() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("a");
        array.add("b");
        array.add("c");

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationMinSize(3, false)
                                                  .key("key1")
                                                  .element(array)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void minSizeJsonArrayGreater() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("a");
        array.add("b");
        array.add("c");
        array.add("d");

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationMinSize(3, false)
                                                  .key("key1")
                                                  .element(array)
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void minSizeJsonArrayLess() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("a");
        array.add("b");

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationMinSize(3, false)
                                                  .key("key1")
                                                  .element(array)
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_MIN_SIZE, message.getCode());
    }

    @Test
    public void minSizeJsonObjectEquals() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "a");
        obj.put("key2", "b");
        obj.put("key3", "c");

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationMinSize(3, false)
                                                  .key("key1")
                                                  .element(obj)
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void minSizeJsonObjectGreater() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "a");
        obj.put("key2", "b");
        obj.put("key3", "c");
        obj.put("key4", "d");

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationMinSize(3, false)
                                                  .key("key1")
                                                  .element(obj)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void minSizeJsonObjectLess() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "a");
        obj.put("key2", "b");

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationMinSize(3, false)
                                                  .key("key1")
                                                  .element(obj)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_MIN_SIZE, message.getCode());
    }

    @Test
    public void minSizeArrayEquals() throws Exception {

        byte[] array = new byte[]{1, 2, 3};

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationMinSize(3, false)
                                                  .key("key1")
                                                  .element(array)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void minSizeArrayGreater() throws Exception {

        byte[] array = new byte[]{1, 2, 3, 4};

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationMinSize(3, false)
                                                  .key("key1")
                                                  .element(array)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void minSizeArrayLess() throws Exception {

        byte[] array = new byte[]{1, 2};

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationMinSize(3, false)
                                                  .key("key1")
                                                  .element(array)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_MIN_SIZE, message.getCode());
    }

    @Test
    public void minSizeCollectionEquals() throws Exception {

        List<Integer> list = Lists.newArrayList(1, 2, 3);

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationMinSize(3, false)
                                                  .key("key1")
                                                  .element(list)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void minSizeCollectionGreater() throws Exception {

        List<Integer> list = Lists.newArrayList(1, 2, 3, 4);

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationMinSize(3, false)
                                                  .key("key1")
                                                  .element(list)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void minSizeCollectionLess() throws Exception {

        List<Integer> list = Lists.newArrayList(1, 2);

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationMinSize(3, false)
                                                  .key("key1")
                                                  .element(list)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_MIN_SIZE, message.getCode());
    }

    @Test
    public void minSizeJsonArrayEqualsOnJsonObject() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("a");
        array.add("b");
        array.add("c");

        JsonObject toValidate = getJsonManager().create();
        toValidate.put("key1", array);

        JsonObjectValidationSet validationFinal = toValidate.validationSet();
        ValidationSet validation = validationFinal.validationMinSize(3, false)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void minSizeJsonArrayGreaterOnJsonObject() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("a");
        array.add("b");
        array.add("c");
        array.add("d");

        JsonObject toValidate = getJsonManager().create();
        toValidate.put("key1", array);

        JsonObjectValidationSet validationFinal = toValidate.validationSet();
        ValidationSet validation = validationFinal.validationMinSize(3, false)
                                                  .jsonPath("key1")
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void minSizeJsonArrayLessOnJsonObject() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("a");
        array.add("b");

        JsonObject toValidate = getJsonManager().create();
        toValidate.put("key1", array);

        JsonObjectValidationSet validationFinal = toValidate.validationSet();
        ValidationSet validation = validationFinal.validationMinSize(3, false)
                                                  .jsonPath("key1")
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_MIN_SIZE, message.getCode());
    }

    @Test
    public void minSizeJsonObjectEqualsOnJsonObject() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "a");
        obj.put("key2", "b");
        obj.put("key3", "c");

        JsonObject toValidate = getJsonManager().create();
        toValidate.put("key1", obj);

        JsonObjectValidationSet validationFinal = toValidate.validationSet();
        ValidationSet validation = validationFinal.validationMinSize(3, false)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void minSizeJsonObjectGreaterOnJsonObject() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "a");
        obj.put("key2", "b");
        obj.put("key3", "c");
        obj.put("key4", "d");

        JsonObject toValidate = getJsonManager().create();
        toValidate.put("key1", obj);

        JsonObjectValidationSet validationFinal = toValidate.validationSet();
        ValidationSet validation = validationFinal.validationMinSize(3, false)
                                                  .jsonPath("key1")
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void minSizeJsonObjectLessOnJsonObject() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "a");
        obj.put("key2", "b");

        JsonObject toValidate = getJsonManager().create();
        toValidate.put("key1", obj);

        JsonObjectValidationSet validationFinal = toValidate.validationSet();
        ValidationSet validation = validationFinal.validationMinSize(3, false)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validationFinal.isError());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_MIN_SIZE, message.getCode());
    }

    @Test
    public void minSizeArrayEqualsOnJsonObject() throws Exception {

        byte[] array = new byte[]{1, 2, 3};

        JsonObject toValidate = getJsonManager().create();
        toValidate.put("key1", array);

        JsonObjectValidationSet validationFinal = toValidate.validationSet();
        ValidationSet validation = validationFinal.validationMinSize(3, false)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void minSizeArrayGreaterOnJsonObject() throws Exception {

        byte[] array = new byte[]{1, 2, 3, 4};

        JsonObject toValidate = getJsonManager().create();
        toValidate.put("key1", array);

        JsonObjectValidationSet validationFinal = toValidate.validationSet();
        ValidationSet validation = validationFinal.validationMinSize(3, false)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void minSizeArrayLessOnJsonObject() throws Exception {

        byte[] array = new byte[]{1, 2};

        JsonObject toValidate = getJsonManager().create();
        toValidate.put("key1", array);

        JsonObjectValidationSet validationFinal = toValidate.validationSet();
        ValidationSet validation = validationFinal.validationMinSize(3, false)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_MIN_SIZE, message.getCode());
    }

    @Test
    public void minSizeCantUseSizeValidationOnJsonObject() throws Exception {

        JsonObject toValidate = getJsonManager().create();
        toValidate.put("key1", "nope");

        JsonObjectValidationSet validationFinal = toValidate.validationSet();
        ValidationSet validation = validationFinal.validationMinSize(3, false)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_MIN_SIZE, message.getCode());
    }

    @Test
    public void maxSizeJsonObjectGreater() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "a");
        obj.put("key2", "b");
        obj.put("key3", "c");
        obj.put("key4", "d");

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationMaxSize(3, false)
                                                  .key("key1")
                                                  .element(obj)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_MAX_SIZE, message.getCode());
    }

    @Test
    public void maxSizeJsonObjectLess() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "a");
        obj.put("key2", "b");

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationMaxSize(3, false)
                                                  .key("key1")
                                                  .element(obj)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void maxSizeArrayEquals() throws Exception {

        byte[] array = new byte[]{1, 2, 3};

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationMaxSize(3, false)
                                                  .key("key1")
                                                  .element(array)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void maxSizeArrayGreater() throws Exception {

        byte[] array = new byte[]{1, 2, 3, 4};

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationMaxSize(3, false)
                                                  .key("key1")
                                                  .element(array)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_MAX_SIZE, message.getCode());
    }

    @Test
    public void maxSizeArrayLess() throws Exception {

        byte[] array = new byte[]{1, 2};

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationMaxSize(3, false)
                                                  .key("key1")
                                                  .element(array)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void maxSizeCollectionEquals() throws Exception {

        List<Integer> list = Lists.newArrayList(1, 2, 3);

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationMaxSize(3, false)
                                                  .key("key1")
                                                  .element(list)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void maxSizeCollectionGreater() throws Exception {

        List<Integer> list = Lists.newArrayList(1, 2, 3, 4);

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationMaxSize(3, false)
                                                  .key("key1")
                                                  .element(list)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_MAX_SIZE, message.getCode());
    }

    @Test
    public void maxSizeCollectionLess() throws Exception {

        List<Integer> list = Lists.newArrayList(1, 2);

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationMaxSize(3, false)
                                                  .key("key1")
                                                  .element(list)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void maxSizeJsonArrayEqualsOnJsonObject() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("a");
        array.add("b");
        array.add("c");

        JsonObject toValidate = getJsonManager().create();
        toValidate.put("key1", array);

        JsonObjectValidationSet validationFinal = toValidate.validationSet();
        ValidationSet validation = validationFinal.validationMaxSize(3, false)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void maxSizeJsonArrayGreaterOnJsonObject() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("a");
        array.add("b");
        array.add("c");
        array.add("d");

        JsonObject toValidate = getJsonManager().create();
        toValidate.put("key1", array);

        JsonObjectValidationSet validationFinal = toValidate.validationSet();
        ValidationSet validation = validationFinal.validationMaxSize(3, false)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_MAX_SIZE, message.getCode());
    }

    @Test
    public void maxSizeJsonArrayLessOnJsonObject() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("a");
        array.add("b");

        JsonObject toValidate = getJsonManager().create();
        toValidate.put("key1", array);

        JsonObjectValidationSet validationFinal = toValidate.validationSet();
        ValidationSet validation = validationFinal.validationMaxSize(3, false)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void maxSizeJsonObjectEqualsOnJsonObject() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "a");
        obj.put("key2", "b");
        obj.put("key3", "c");

        JsonObject toValidate = getJsonManager().create();
        toValidate.put("key1", obj);

        JsonObjectValidationSet validationFinal = toValidate.validationSet();
        ValidationSet validation = validationFinal.validationMaxSize(3, false)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void maxSizeJsonObjectGreaterOnJsonObject() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "a");
        obj.put("key2", "b");
        obj.put("key3", "c");
        obj.put("key4", "d");

        JsonObject toValidate = getJsonManager().create();
        toValidate.put("key1", obj);

        JsonObjectValidationSet validationFinal = toValidate.validationSet();
        ValidationSet validation = validationFinal.validationMaxSize(3, false)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_MAX_SIZE, message.getCode());
    }

    @Test
    public void maxSizeJsonObjectLessOnJsonObject() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "a");
        obj.put("key2", "b");

        JsonObject toValidate = getJsonManager().create();
        toValidate.put("key1", obj);

        JsonObjectValidationSet validationFinal = toValidate.validationSet();
        ValidationSet validation = validationFinal.validationMaxSize(3, false)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void maxSizeArrayEqualsOnJsonObject() throws Exception {

        byte[] array = new byte[]{1, 2, 3};

        JsonObject toValidate = getJsonManager().create();
        toValidate.put("key1", array);

        JsonObjectValidationSet validationFinal = toValidate.validationSet();
        ValidationSet validation = validationFinal.validationMaxSize(3, false)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void maxSizeArrayGreaterOnJsonObject() throws Exception {

        byte[] array = new byte[]{1, 2, 3, 4};

        JsonObject toValidate = getJsonManager().create();
        toValidate.put("key1", array);

        JsonObjectValidationSet validationFinal = toValidate.validationSet();
        ValidationSet validation = validationFinal.validationMaxSize(3, false)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_MAX_SIZE, message.getCode());
    }

    @Test
    public void maxSizeArrayLessOnJsonObject() throws Exception {

        byte[] array = new byte[]{1, 2};

        JsonObject toValidate = getJsonManager().create();
        toValidate.put("key1", array);

        JsonObjectValidationSet validationFinal = toValidate.validationSet();
        ValidationSet validation = validationFinal.validationMaxSize(3, false)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validationFinal.isSuccess());
        assertEquals(0, validationFinal.getMessages("key1").size());
    }

    @Test
    public void maxSizeCantUseSizeValidationOnJsonObject() throws Exception {

        JsonObject toValidate = getJsonManager().create();
        toValidate.put("key1", "nope");

        JsonObjectValidationSet validationFinal = toValidate.validationSet();
        ValidationSet validation = validationFinal.validationMaxSize(3, false)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_MAX_SIZE, message.getCode());
    }

    @Test
    public void minSizeOnArrayAllElementsValid() throws Exception {

        JsonArray innerArray = getJsonManager().createArray();
        innerArray.add("a");
        innerArray.add("b");
        innerArray.add("c");

        JsonObject innerObj = getJsonManager().create();
        innerObj.put("key1", "a");
        innerObj.put("key2", "b");
        innerObj.put("key3", "c");

        byte[] byteArray = new byte[]{1, 2, 3};

        List<Integer> list = Lists.newArrayList(1, 2, 3);

        JsonArray array = getJsonManager().createArray();
        array.add(innerArray);
        array.add(innerObj);
        array.add(byteArray);
        array.add(list);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationMaxSize(3, false)
                                                  .jsonPathAll("key1")
                                                  .failMessageText("custom element msg")
                                                  .arrayItselfAddFailMessage("_key1", "custom array msg")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(0, messages.size());
    }

    @Test
    public void minSizeOnArrayAllElementsInvalid() throws Exception {

        JsonArray innerArray = getJsonManager().createArray();
        innerArray.add("a");
        innerArray.add("b");
        innerArray.add("c");

        JsonObject innerObj = getJsonManager().create();
        innerObj.put("key1", "a");
        innerObj.put("key2", "b");
        innerObj.put("key3", "c");

        byte[] byteArray = new byte[]{1, 2, 3};

        List<Integer> list = Lists.newArrayList(1, 2, 3);

        List<Integer> listOk = Lists.newArrayList(1, 2, 3, 4);

        JsonArray array = getJsonManager().createArray();
        array.add(innerArray);
        array.add(innerObj);
        array.add(byteArray);
        array.add(list);
        array.add(listOk);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet builder = obj.validationSet();
        assertNotNull(builder);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationMinSize(4, false)
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        //==========================================
        // Message on the array itself
        //==========================================
        messages = validation.getMessages("_key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_MIN_SIZE, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_MIN_SIZE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_MIN_SIZE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_MIN_SIZE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_MIN_SIZE, message.getCode());
        assertEquals("element message", message.getText());
    }

    @Test
    public void maxSizeOnArrayAllElementsValid() throws Exception {

        JsonArray innerArray = getJsonManager().createArray();
        innerArray.add("a");
        innerArray.add("b");
        innerArray.add("c");

        JsonObject innerObj = getJsonManager().create();
        innerObj.put("key1", "a");
        innerObj.put("key3", "c");

        byte[] byteArray = new byte[]{1, 2, 3};

        List<Integer> list = Lists.newArrayList(1, 2, 3);

        JsonArray array = getJsonManager().createArray();
        array.add(innerArray);
        array.add(innerObj);
        array.add(byteArray);
        array.add(list);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet builder = obj.validationSet();
        assertNotNull(builder);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationMaxSize(3, false)
                                                  .jsonPathAll("key1")
                                                  .failMessageText("custom element msg")
                                                  .arrayItselfAddFailMessage("_key1", "custom array msg")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(0, messages.size());
    }

    @Test
    public void maxSizeOnArrayAllElementsInvalid() throws Exception {

        JsonArray innerArray = getJsonManager().createArray();
        innerArray.add("a");
        innerArray.add("b");
        innerArray.add("c");

        JsonObject innerObj = getJsonManager().create();
        innerObj.put("key1", "a");
        innerObj.put("key2", "b");
        innerObj.put("key3", "c");

        byte[] byteArray = new byte[]{1, 2, 3};

        List<Integer> list = Lists.newArrayList(1, 2, 3);

        List<Integer> listOk = Lists.newArrayList(1, 2);

        JsonArray array = getJsonManager().createArray();
        array.add(innerArray);
        array.add(innerObj);
        array.add(byteArray);
        array.add(list);
        array.add(listOk);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet builder = obj.validationSet();
        assertNotNull(builder);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationMaxSize(2, false)
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        //==========================================
        // Message on the array itself
        //==========================================
        messages = validation.getMessages("_key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_MAX_SIZE, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_MAX_SIZE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_MAX_SIZE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_MAX_SIZE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_MAX_SIZE, message.getCode());
        assertEquals("element message", message.getText());
    }

    @Test
    public void notAnArray() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "test");

        JsonObjectValidationSet builder = obj.validationSet();
        assertNotNull(builder);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationMinSize(3, false)
                                                  .jsonPathAll("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_NOT_AN_ARRAY, message.getCode());
        assertNotNull(message.getText());
    }

    @Test
    public void exactSizeSuccess() throws Exception {

        Integer[] array = new Integer[]{1, 2, 3};

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationSize(3, false)
                                                  .key("key1")
                                                  .element(array)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void exactSizeError() throws Exception {

        Integer[] array = new Integer[]{1, 2, 3, 4};

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationSize(3, false)
                                                  .key("key1")
                                                  .element(array)
                                                  .failMessageText("errr")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_SIZE, message.getCode());
        assertEquals("errr", message.getText());
    }

    @Test
    public void exactSizeSuccessOnJsonObject() throws Exception {

        Integer[] array = new Integer[]{1, 2, 3};

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationSize(3, false)
                                                  .jsonPath("key1")
                                                  .failMessageText("errr")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(0, messages.size());
    }

    @Test
    public void exactSizeErrorOnJsonObject() throws Exception {

        Integer[] array = new Integer[]{1, 2, 3, 4};

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationSize(3, false)
                                                  .jsonPath("key1")
                                                  .failMessageText("errr")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_SIZE, message.getCode());
        assertEquals("errr", message.getText());
    }

    @Test
    public void ignoreNullValuesSize() throws Exception {

        Integer[] array = new Integer[]{null, 2, 3};

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationSize(3, false)
                                                  .key("key1")
                                                  .element(array)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());

        validation = validationFinal.validationSize(3, true)
                                    .key("key2")
                                    .element(array)
                                    .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key2");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_SIZE, message.getCode());
    }

    @Test
    public void ignoreNullValuesMinSize() throws Exception {

        Integer[] array = new Integer[]{null, 2, 3};

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationMinSize(3, false)
                                                  .key("key1")
                                                  .element(array)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());

        validation = validationFinal.validationMinSize(3, true)
                                    .key("key2")
                                    .element(array)
                                    .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key2");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_MIN_SIZE, message.getCode());
    }

    @Test
    public void ignoreNullValuesMaxSize() throws Exception {

        Integer[] array = new Integer[]{null, 2, 3};

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationMaxSize(2, true)
                                                  .key("key1")
                                                  .element(array)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());

        validation = validationFinal.validationMaxSize(2, false)
                                    .key("key2")
                                    .element(array)
                                    .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key2");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_MAX_SIZE, message.getCode());
    }

}
