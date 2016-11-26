package org.spincast.tests.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonObject;
import org.spincast.core.validation.JsonObjectValidationSet;
import org.spincast.core.validation.ValidationMessage;
import org.spincast.core.validation.ValidationSet;
import org.spincast.shaded.org.apache.commons.lang3.time.DateUtils;
import org.spincast.testing.core.utils.SpincastTestUtils;

public class GreaterValidationTest extends ValidationTestBase {

    /**
     * A null value is less than a non-null value.
     */
    @Test
    public void validateGreaterNullValue() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationGreater(5L)
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

        assertEquals(ValidationSet.VALIDATION_CODE_GREATER, message.getCode());
    }

    /**
     * A null value is less than a non-null value.
     */
    @Test
    public void validateGreaterNullCompareTo() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationGreater(null)
                                                  .key("key1")
                                                  .element(4)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateGreaterNullValueAndNullCompareTo() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationGreater(null)
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

        assertEquals(ValidationSet.VALIDATION_CODE_GREATER, message.getCode());
    }

    @Test
    public void validateGreaterLess() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationGreater(5)
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

        assertEquals(ValidationSet.VALIDATION_CODE_GREATER, message.getCode());
    }

    @Test
    public void validateGreaterEqual() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationGreater(5)
                                                  .key("key1")
                                                  .element(5)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_GREATER, message.getCode());
    }

    @Test
    public void validateGreaterGreater() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationGreater(5)
                                                  .key("key1")
                                                  .element(6)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateGreaterOnJsonObjectWithConversionValid() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "6");

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationGreater(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateGreaterOnJsonObjectWithConversionInvalid() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "4");

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationGreater(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_GREATER, message.getCode());
    }

    @Test
    public void validateGreaterOnJsonObjectCantConvert() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "nope");

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationGreater(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_GREATER, message.getCode());
    }

    /**
     * A null (not found) value is less than a non null value.
     */
    @Test
    public void validateGreaterOnJsonObjectNotFound() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("nope", 4);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationGreater(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_GREATER, message.getCode());
    }

    /**
     * A null value is less than a non null value.
     */
    @Test
    public void validateGreaterOnJsonObjectNull() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", null);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationGreater(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_GREATER, message.getCode());
    }

    @Test
    public void validateGreaterOnJsonObjectNullAndCompareTooNull() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", null);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationGreater(null)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_GREATER, message.getCode());
    }

    @Test
    public void validateGreaterOnJsonObjectCompareToNull() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", 4);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationGreater(null)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateGreaterOnJsonObjectLess() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", 4);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationGreater(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_GREATER, message.getCode());
    }

    @Test
    public void validateGreaterOnJsonObjectEqual() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", 5);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationGreater(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_GREATER, message.getCode());
    }

    @Test
    public void validateGreaterOnJsonObjectGreater() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", 6);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationGreater(5)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateGreaterOnJsonObjectBigDecimalEqual() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", 5);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationGreater(new BigDecimal("5"))
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_GREATER, message.getCode());
    }

    @Test
    public void validateGreaterOnJsonObjectBigDecimalGreater() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", 6);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationGreater(new BigDecimal("5"))
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateGreaterOnJsonObjectBigDecimalEqual2() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", new BigDecimal("5"));

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationGreater(new BigDecimal("5"))
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_GREATER, message.getCode());
    }

    @Test
    public void validateGreaterOnJsonObjectBigDecimalGreater2() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", new BigDecimal("6"));

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationGreater(new BigDecimal("5"))
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateGreaterOnJsonObjectDateGreater() throws Exception {

        Date compareTo = SpincastTestUtils.getTestDateNoTime();
        Date dateToCompare = DateUtils.addMinutes(compareTo, 1);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", dateToCompare);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationGreater(compareTo)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void validateGreaterOnJsonObjectDateStrGreater() throws Exception {

        Date compareTo = SpincastTestUtils.getTestDateNoTime();
        Date dateToCompare = DateUtils.addMinutes(compareTo, 1);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", getObjectConverter().convertToJsonDateFormat(dateToCompare));

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationGreater(compareTo)
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        assertEquals(0, validation.getMessages("key1").size());

        validation = validationFinal.validationEquivalent(compareTo).jsonPath("key1")
                                    .validate();
        assertNotNull(validation);
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT, message.getCode());

        validation = validationFinal.validationLess(compareTo).jsonPath("key1")
                                    .validate();
        assertNotNull(validation);
        assertNotNull(validation);
        assertTrue(validation.isError());

        messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_LESS, message.getCode());
    }

    @Test
    public void greaterOnArrayAllElementsValid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("124");
        array.add(123.1);
        array.add(124.0F);
        array.add(new BigDecimal("123.2"));

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationGreater(123L)
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
    public void greaterOnArrayAllElementsInvalid() throws Exception {

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
        ValidationSet validation = validationFinal.validationGreater(123L)
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[5]");
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
        assertEquals(ValidationSet.VALIDATION_CODE_GREATER, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_GREATER, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_GREATER, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_GREATER, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_GREATER, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_GREATER, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[6]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_GREATER, message.getCode());
        assertEquals("element message", message.getText());
    }

}
