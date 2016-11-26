package org.spincast.tests.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonObject;
import org.spincast.core.validation.JsonObjectValidationSet;
import org.spincast.core.validation.ValidationMessage;
import org.spincast.core.validation.ValidationSet;

public class LengthValidationTest extends ValidationTestBase {

    @Test
    public void minLengthStringEquals() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationMinLength(3)
                                                  .key("key1")
                                                  .element("123")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void maxLengthStringEquals() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationMaxLength(3)
                                                  .key("key1")
                                                  .element("123")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void minLengthStringLonger() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationMinLength(3)
                                                  .key("key1")
                                                  .element("abcde")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void minLengthStringTooShort() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationMinLength(3)
                                                  .key("key1")
                                                  .element("ab")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_MIN_LENGTH, message.getCode());
    }

    @Test
    public void minLengthStringEqualsOnJsonObject() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "abc");

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationMinLength(3)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void minLengthStringLongerOnJsonObject() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "abcde");

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationMinLength(3)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void minLengthStringTooShortOnJsonObject() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "ab");

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationMinLength(3)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_MIN_LENGTH, message.getCode());
    }

    @Test
    public void minLengthOnArrayAllElementsValid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("abcd");
        array.add(123);
        array.add(new BigDecimal("123.2"));

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationMinLength(3)
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
    }

    @Test
    public void minLengthOnArrayAllElementsInvalid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(1234);
        array.add("");
        array.add("abcd");
        array.add(null);
        array.add("    ");
        array.add(1.01F);
        array.add(12F); // becomes "12.0"
        array.add(2F); // becomes "2.0"

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationMinLength(4)
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[5]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[6]");
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
        assertEquals(ValidationSet.VALIDATION_CODE_MIN_LENGTH, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_MIN_LENGTH, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_MIN_LENGTH, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[7]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_MIN_LENGTH, message.getCode());
        assertEquals("element message", message.getText());
    }

    @Test
    public void maxLengthOnArrayAllElementsValid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("abc");
        array.add(123);
        array.add(new BigDecimal("1.2"));

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationMaxLength(3)
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
    }

    @Test
    public void maxLengthOnArrayAllElementsInvalid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(1234);
        array.add("");
        array.add("abc");
        array.add(null);
        array.add("    ");
        array.add(1.01F);
        array.add(12F); // becomes "12.0"
        array.add(2F); // becomes "2.0"

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationMaxLength(3)
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[7]");
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
        assertEquals(ValidationSet.VALIDATION_CODE_MAX_LENGTH, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_MAX_LENGTH, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_MAX_LENGTH, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[5]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_MAX_LENGTH, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[6]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_MAX_LENGTH, message.getCode());
        assertEquals("element message", message.getText());
    }

    @Test
    public void exactLengthSuccess() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationLength(3)
                                                  .key("key1")
                                                  .element("123")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void exactLengthError() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationLength(3)
                                                  .key("key1")
                                                  .element("1234")
                                                  .failMessageText("errr")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_LENGTH, message.getCode());
        assertEquals("errr", message.getText());
    }

    @Test
    public void exactLengthSuccessOnJsonObject() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "123");

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationLength(3)
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
    public void exactLengthErrorOnJsonObject() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "1234");

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationLength(3)
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

        assertEquals(ValidationSet.VALIDATION_CODE_LENGTH, message.getCode());
        assertEquals("errr", message.getText());
    }

}
