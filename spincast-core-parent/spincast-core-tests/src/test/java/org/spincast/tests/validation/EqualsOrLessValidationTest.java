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

public class EqualsOrLessValidationTest extends ValidationTestBase {

    /**
     * A null value is less than a non-null value.
     */
    @Test
    public void validateEquivalentOrLessNullValue() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrLess(5L)
                                                  .key("key1")
                                                  .element(null)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        assertEquals(0, validation.getMessages("key1").size());
    }

    /**
     * A null value is less than a non-null value.
     */
    @Test
    public void validateEquivalentOrLessNullCompareTo() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrLess(null)
                                                  .key("key1")
                                                  .element(4)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT_OR_LESS, message.getCode());
    }

    @Test
    public void validateEquivalentOrLessNullValueAndNullCompareTo() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrLess(null)
                                                  .key("key1")
                                                  .element(null)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateEquivalentOrLessLess() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrLess(5)
                                                  .key("key1")
                                                  .element(4)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateEquivalentOrLessEqual() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrLess(5)
                                                  .key("key1")
                                                  .element(5)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateEquivalentOrLessGreater() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrLess(5)
                                                  .key("key1")
                                                  .element(6)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT_OR_LESS, message.getCode());
    }

    @Test
    public void validateEquivalentOrLessOnJsonObjectWithConversionValid() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "4");

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrLess(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateEquivalentOrLessOnJsonObjectWithConversionInvalid() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "6");

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrLess(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT_OR_LESS, message.getCode());
    }

    @Test
    public void validateEquivalentOrLessOnJsonObjectCantConvert() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "nope");

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrLess(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT_OR_LESS, message.getCode());
    }

    /**
     * A null (not found) value is less than a non null value.
     */
    @Test
    public void validateEquivalentOrLessOnJsonObjectNotFound() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("nope", 4);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrLess(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    /**
     * A null value is less than a non null value.
     */
    @Test
    public void validateEquivalentOrLessOnJsonObjectNull() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", null);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrLess(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateEquivalentOrLessOnJsonObjectNullAndCompareToNull() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", null);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrLess(null)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateEquivalentOrLessOnJsonObjectCompareToNull() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", 4);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrLess(null)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT_OR_LESS, message.getCode());
    }

    @Test
    public void validateEquivalentOrLessOnJsonObjectLess() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", 4);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrLess(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateEquivalentOrLessOnJsonObjectEqual() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", 5);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrLess(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateEquivalentOrLessOnJsonObjectGreater() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", 6);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrLess(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT_OR_LESS, message.getCode());
    }

    @Test
    public void equalsOrLessOnArrayAllElementsValid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("123");
        array.add(123);
        array.add(13.0F);
        array.add(new BigDecimal("12.2"));

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrLess(123L)
                                                  .jsonPathAll("key1")
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
    public void equalsOrLessOnArrayAllElementsInvalid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(123);
        array.add("");
        array.add("abc");
        array.add(null);
        array.add("   ");
        array.add(123.01F);
        array.add(122.9F);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrLess(123L)
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        // NULL is less!
        messages = validation.getMessages("key1[3]");
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
        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT_OR_LESS, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT_OR_LESS, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT_OR_LESS, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT_OR_LESS, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[5]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT_OR_LESS, message.getCode());
        assertEquals("element message", message.getText());
    }

}
