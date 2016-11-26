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

public class EqualsOrGreaterValidationTest extends ValidationTestBase {

    /**
     * A null value is less than a non-null value.
     */
    @Test
    public void validateEquivalentOrGreaterNullValue() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrGreater(5L)
                                                  .key("key1")
                                                  .element(null)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT_OR_GREATER, message.getCode());
    }

    /**
     * A null value is less than a non-null value.
     */
    @Test
    public void validateEquivalentOrGreaterNullCompareTo() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrGreater(null)
                                                  .key("key1")
                                                  .element(4)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateEqualOrGreaterNullValueAndNullCompareTo() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrGreater(null)
                                                  .key("key1")
                                                  .element(null)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateEquivalentOrGreaterLess() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrGreater(5)
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

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT_OR_GREATER, message.getCode());
    }

    @Test
    public void validateEquivalentOrGreaterEqual() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrGreater(5)
                                                  .key("key1")
                                                  .element(5)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateEquivalentOrGreaterGreater() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrGreater(5)
                                                  .key("key1")
                                                  .element(6)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateEquivalentOrGreaterOnJsonObjectWithConversionValid() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "6");

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrGreater(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateEquivalentOrGreaterOnJsonObjectWithConversionInvalid() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "4");

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrGreater(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT_OR_GREATER, message.getCode());
    }

    @Test
    public void validateEquivalentOrGreaterOnJsonObjectCantConvert() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "nope");

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrGreater(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT_OR_GREATER, message.getCode());
    }

    /**
     * A null (not found) value is less than a non null value.
     */
    @Test
    public void validateEquivalentOrGreaterOnJsonObjectNotFound() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("nope", 4);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrGreater(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT_OR_GREATER, message.getCode());
    }

    /**
     * A null value is less than a non null value.
     */
    @Test
    public void validateEquivalentOrGreaterOnJsonObjectNull() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", null);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrGreater(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT_OR_GREATER, message.getCode());
    }

    @Test
    public void validateEquivalentOrGreaterOnJsonObjectNullAndCompareTooNull() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", null);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrGreater(null)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateEquivalentOrGreaterOnJsonObjectCompareToNull() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", 4);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrGreater(null)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateEquivalentOrGreaterOnJsonObjectLess() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", 4);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrGreater(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT_OR_GREATER, message.getCode());
    }

    @Test
    public void validateEquivalentOrGreaterOnJsonObjectEqual() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", 5);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrGreater(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateEquivalentOrGreaterOnJsonObjectGreater() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", 6);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrGreater(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void equalsOrGreaterOnArrayAllElementsValid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("123");
        array.add(123);
        array.add(124.0F);
        array.add(new BigDecimal("123.2"));

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationEquivalentOrGreater(123L)
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
    public void equalsOrGreaterOnArrayAllElementsInvalid() throws Exception {

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
        ValidationSet validation = validationFinal.validationEquivalentOrGreater(123L)
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[5]");
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
        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT_OR_GREATER, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT_OR_GREATER, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT_OR_GREATER, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT_OR_GREATER, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT_OR_GREATER, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[6]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT_OR_GREATER, message.getCode());
        assertEquals("element message", message.getText());
    }

}
