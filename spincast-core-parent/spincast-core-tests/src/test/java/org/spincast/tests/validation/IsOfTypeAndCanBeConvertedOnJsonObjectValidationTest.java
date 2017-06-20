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
import org.spincast.testing.core.utils.SpincastTestUtils;

public class IsOfTypeAndCanBeConvertedOnJsonObjectValidationTest extends ValidationTestBase {

    @Test
    public void typeString() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "123");

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeStringOrNull()
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());

        validation = validationFinal.validationCanBeConvertedToString().jsonPath("key1").validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());

        validation = validationFinal.validationCanBeConvertedToInteger().jsonPath("key1").validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());

        validation = validationFinal.validationCanBeConvertedToLong().jsonPath("key1").validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());

        validation = validationFinal.validationCanBeConvertedToFloat().jsonPath("key1").validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());

        validation = validationFinal.validationCanBeConvertedToDouble().jsonPath("key1").validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());

        validation = validationFinal.validationCanBeConvertedToBigDecimal().jsonPath("key1").validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());

        validation = validationFinal.validationCanBeConvertedToByteArray().jsonPath("key1").validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());

        validation = validationFinal.validationIsOfTypeIntegerOrNull().jsonPath("key1").validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());

        validation = validationFinal.validationCanBeConvertedToBoolean().jsonPath("key1").validate();
        assertNotNull(validation);
        assertTrue(validation.isError());
        messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(2, messages.size());

        validation = validationFinal.validationCanBeConvertedToDate().jsonPath("key1").validate();
        assertNotNull(validation);
        assertTrue(validation.isError());
        messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(3, messages.size());

        validation = validationFinal.validationCanBeConvertedToJsonObject().jsonPath("key1").validate();
        assertNotNull(validation);
        assertTrue(validation.isError());
        messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(4, messages.size());

        validation = validationFinal.validationCanBeConvertedToJsonArray().jsonPath("key1").validate();
        assertNotNull(validation);
        assertTrue(validation.isError());
        messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(5, messages.size());

        validation = validationFinal.validationCanBeConvertedToDate().jsonPath("key1").validate();
        assertNotNull(validation);
        assertTrue(validation.isError());
        messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(6, messages.size());
    }

    @Test
    public void typeInteger() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", 123);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeIntegerOrNull()
                                                  .jsonPath("key1")
                                                  .validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationIsOfTypeStringOrNull().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToString().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToInteger().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToLong().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToFloat().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToDouble().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToBigDecimal().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToByteArray().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToBoolean().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToJsonObject().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToJsonArray().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToDate().jsonPath("key1").validate();
        assertTrue(validation.isError());
    }

    @Test
    public void typeLong() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", 123L);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeLongOrNull()
                                                  .jsonPath("key1")
                                                  .validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationIsOfTypeStringOrNull().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationIsOfTypeIntegerOrNull().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToString().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToInteger().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToLong().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToFloat().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToDouble().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToBigDecimal().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToByteArray().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToBoolean().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToJsonObject().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToJsonArray().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToDate().jsonPath("key1").validate();
        assertTrue(validation.isError());
    }

    @Test
    public void typeDouble() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", 123D);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeDoubleOrNull()
                                                  .jsonPath("key1")
                                                  .validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationIsOfTypeStringOrNull().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationIsOfTypeIntegerOrNull().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationIsOfTypeLongOrNull().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToString().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToInteger().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToLong().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToFloat().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToDouble().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToBigDecimal().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToByteArray().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToBoolean().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToJsonObject().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToJsonArray().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToDate().jsonPath("key1").validate();
        assertTrue(validation.isError());
    }

    @Test
    public void typeFloat() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", 123F);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeFloatOrNull()
                                                  .jsonPath("key1")
                                                  .validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationIsOfTypeStringOrNull().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationIsOfTypeIntegerOrNull().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationIsOfTypeLongOrNull().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationIsOfTypeDoubleOrNull().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToString().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToInteger().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToLong().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToFloat().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToDouble().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToBigDecimal().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToByteArray().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToBoolean().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToJsonObject().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToJsonArray().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToDate().jsonPath("key1").validate();
        assertTrue(validation.isError());
    }

    @Test
    public void typeBigDecimal() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", new BigDecimal("123"));

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeBigDecimalOrNull()
                                                  .jsonPath("key1")
                                                  .validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationIsOfTypeStringOrNull().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationIsOfTypeIntegerOrNull().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationIsOfTypeLongOrNull().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationIsOfTypeDoubleOrNull().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationIsOfTypeFloatOrNull().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToString().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToInteger().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToLong().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToFloat().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToDouble().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToBigDecimal().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToByteArray().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToBoolean().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToJsonObject().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToJsonArray().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToDate().jsonPath("key1").validate();
        assertTrue(validation.isError());
    }

    @Test
    public void typeBoolean() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", true);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeBooleanOrNull()
                                                  .jsonPath("key1")
                                                  .validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationIsOfTypeStringOrNull().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationIsOfTypeIntegerOrNull().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationIsOfTypeLongOrNull().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationIsOfTypeDoubleOrNull().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationIsOfTypeFloatOrNull().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationIsOfTypeBigDecimalOrNull().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToString().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToInteger().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToLong().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToFloat().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToDouble().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToBigDecimal().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToByteArray().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToBoolean().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToJsonObject().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToJsonArray().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToDate().jsonPath("key1").validate();
        assertTrue(validation.isError());
    }

    @Test
    public void typeDate() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", SpincastTestUtils.getTestDateNoTime());

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeDateOrNull()
                                                  .jsonPath("key1")
                                                  .validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationIsOfTypeStringOrNull().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationIsOfTypeIntegerOrNull().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationIsOfTypeLongOrNull().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationIsOfTypeDoubleOrNull().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationIsOfTypeFloatOrNull().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationIsOfTypeBigDecimalOrNull().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationIsOfTypeBooleanOrNull().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToString().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());

        validation = validationFinal.validationCanBeConvertedToInteger().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToLong().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToFloat().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToDouble().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToBigDecimal().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToByteArray().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToBoolean().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToJsonObject().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToJsonArray().jsonPath("key1").validate();
        assertTrue(validation.isError());

        validation = validationFinal.validationCanBeConvertedToDate().jsonPath("key1").validate();
        assertTrue(validation.isSuccess());
    }

    @Test
    public void stringCanBeConvertedOnArrayAllElementsValid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("123");
        array.add("");
        array.add(123);
        array.add(13.0F);
        array.add(null);
        array.add(new Date());
        array.add(new BigDecimal("12.2"));

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationCanBeConvertedToString()
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

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[5]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[6]");
        assertNotNull(messages);
        assertEquals(0, messages.size());
    }

    @Test
    public void stringCanBeConvertedOnArrayAllElementsInvalid() throws Exception {
        // none!
    }

    @Test
    public void stringIsOfTypeOnArrayAllElementsValid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("123");
        array.add("");
        array.add(null);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeStringOrNull()
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
    public void stringIsOfTypeOnArrayAllElementsInvalid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(123);
        array.add("");
        array.add("abc");
        array.add(null);
        array.add("   ");
        array.add(new Date());
        array.add(122.9F);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeStringOrNull()
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

        messages = validation.getMessages("key1[4]");
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

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[5]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[6]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());
    }

    @Test
    public void integerCanBeConvertedOnArrayAllElementsValid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("123");
        array.add(123);
        array.add(123L);
        array.add(13.0F);
        array.add(null);
        array.add(new BigDecimal("12.000"));

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationCanBeConvertedToInteger()
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

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[5]");
        assertNotNull(messages);
        assertEquals(0, messages.size());
    }

    @Test
    public void integerCanBeConvertedOnArrayAllElementsInvalid() throws Exception {
        JsonArray array = getJsonManager().createArray();
        array.add(123);
        array.add("");
        array.add("abc");
        array.add(null);
        array.add("   ");
        array.add(new Date());
        array.add(122.9F);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationCanBeConvertedToInteger()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[3]");
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

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[5]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[6]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());
    }

    @Test
    public void integerIsOfTypeOnArrayAllElementsValid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(123);
        array.add(null);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeIntegerOrNull()
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
    }

    @Test
    public void integerIsOfTypeOnArrayAllElementsInvalid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(123);
        array.add(null);
        array.add("");
        array.add(12L);
        array.add(new BigDecimal("123"));
        array.add(122.9F);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeIntegerOrNull()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[1]");
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

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[5]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());
    }

    @Test
    public void longCanBeConvertedOnArrayAllElementsValid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("123");
        array.add(123);
        array.add(123L);
        array.add(13.0F);
        array.add(null);
        array.add(new BigDecimal("12.000"));

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationCanBeConvertedToLong()
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

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[5]");
        assertNotNull(messages);
        assertEquals(0, messages.size());
    }

    @Test
    public void longCanBeConvertedOnArrayAllElementsInvalid() throws Exception {
        JsonArray array = getJsonManager().createArray();
        array.add(123);
        array.add("");
        array.add("abc");
        array.add(null);
        array.add("   ");
        array.add(new Date());
        array.add(122.9F);
        array.add(123L);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationCanBeConvertedToLong()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[0]");
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

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[5]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[6]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());
    }

    @Test
    public void longIsOfTypeOnArrayAllElementsValid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(123L);
        array.add(null);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeLongOrNull()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
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
    }

    @Test
    public void longIsOfTypeOnArrayAllElementsInvalid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(123);
        array.add(null);
        array.add("");
        array.add(12L);
        array.add(new BigDecimal("123"));
        array.add(122.9F);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeLongOrNull()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[3]");
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

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[5]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());
    }

    @Test
    public void floatCanBeConvertedOnArrayAllElementsValid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("123");
        array.add(123);
        array.add(123L);
        array.add(13.02F);
        array.add(null);
        array.add(new BigDecimal("12.020"));

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationCanBeConvertedToFloat()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
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

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[5]");
        assertNotNull(messages);
        assertEquals(0, messages.size());
    }

    @Test
    public void floatCanBeConvertedOnArrayAllElementsInvalid() throws Exception {
        JsonArray array = getJsonManager().createArray();
        array.add(123);
        array.add("");
        array.add("abc");
        array.add(null);
        array.add("   ");
        array.add(new Date());
        array.add(122.9F);
        array.add(123L);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationCanBeConvertedToFloat()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[6]");
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

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[5]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());
    }

    @Test
    public void floatIsOfTypeOnArrayAllElementsValid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(123F);
        array.add(null);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeFloatOrNull()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
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
    }

    @Test
    public void floatIsOfTypeOnArrayAllElementsInvalid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(123);
        array.add(null);
        array.add("");
        array.add(12L);
        array.add(new BigDecimal("123"));
        array.add(122.9F);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeFloatOrNull()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[1]");
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

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());
    }

    @Test
    public void doubleCanBeConvertedOnArrayAllElementsValid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("123");
        array.add(123);
        array.add(123L);
        array.add(13.02F);
        array.add(13.02D);
        array.add(null);
        array.add(new BigDecimal("12.020"));

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationCanBeConvertedToDouble()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
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

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[5]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[6]");
        assertNotNull(messages);
        assertEquals(0, messages.size());
    }

    @Test
    public void doubleCanBeConvertedOnArrayAllElementsInvalid() throws Exception {
        JsonArray array = getJsonManager().createArray();
        array.add(123);
        array.add("");
        array.add("abc");
        array.add(null);
        array.add("   ");
        array.add(new Date());
        array.add(122.9F);
        array.add(123L);
        array.add(123.34D);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationCanBeConvertedToDouble()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[6]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[7]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[8]");
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

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[5]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());
    }

    @Test
    public void doubleIsOfTypeOnArrayAllElementsValid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(123D);
        array.add(null);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeDoubleOrNull()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
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
    }

    @Test
    public void doubleIsOfTypeOnArrayAllElementsInvalid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(123);
        array.add(null);
        array.add("");
        array.add(12L);
        array.add(new BigDecimal("123"));
        array.add(122.9F);
        array.add(122.9D);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeDoubleOrNull()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[1]");
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

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[5]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());
    }

    @Test
    public void booleanCanBeConvertedOnArrayAllElementsValid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("true");
        array.add("false");
        array.add(true);
        array.add(false);
        array.add(null);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationCanBeConvertedToBoolean()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
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

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(0, messages.size());
    }

    @Test
    public void booleanCanBeConvertedOnArrayAllElementsInvalid() throws Exception {
        JsonArray array = getJsonManager().createArray();
        array.add(true);
        array.add("true");
        array.add(null);
        array.add("");
        array.add("abc");
        array.add("   ");
        array.add(new Date());
        array.add(122.9F);
        array.add(123L);
        array.add(123.34D);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationCanBeConvertedToBoolean()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[2]");
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

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[5]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[6]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[7]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);
        ;
        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[8]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);
        ;
        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[9]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

    }

    @Test
    public void booleanIsOfTypeOnArrayAllElementsValid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(false);
        array.add(true);
        array.add(null);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeBooleanOrNull()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
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
    }

    @Test
    public void booleanIsOfTypeOnArrayAllElementsInvalid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(false);
        array.add(null);
        array.add("");
        array.add(12L);
        array.add(new BigDecimal("123"));
        array.add(122.9F);
        array.add(122.9D);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeBooleanOrNull()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[1]");
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

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[5]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[6]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());
    }

    @Test
    public void bigDecimalCanBeConvertedOnArrayAllElementsValid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("123.234234");
        array.add(123);
        array.add(123L);
        array.add(13.02F);
        array.add(13.02D);
        array.add(null);
        array.add(new BigDecimal("12.020"));

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationCanBeConvertedToBigDecimal()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
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

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[5]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[6]");
        assertNotNull(messages);
        assertEquals(0, messages.size());
    }

    @Test
    public void bigDecimalCanBeConvertedOnArrayAllElementsInvalid() throws Exception {
        JsonArray array = getJsonManager().createArray();
        array.add(true);
        array.add("true");
        array.add(null);
        array.add("");
        array.add("abc");
        array.add("   ");
        array.add(new Date());
        array.add(new BigDecimal("12.020"));
        array.add(123L);
        array.add(123.34D);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationCanBeConvertedToBigDecimal()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[7]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[8]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[9]");
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

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[5]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[6]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());
    }

    @Test
    public void bigDecimalIsOfTypeOnArrayAllElementsValid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(new BigDecimal("12.020"));
        array.add(null);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeBigDecimalOrNull()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
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
    }

    @Test
    public void bigDecimalIsOfTypeOnArrayAllElementsInvalid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(true);
        array.add(null);
        array.add("");
        array.add("abc");
        array.add("   ");
        array.add(new Date());
        array.add(new BigDecimal("12.020"));
        array.add(123L);
        array.add(123.34D);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeBigDecimalOrNull()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[1]");
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

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[5]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[7]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);
        ;
        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[8]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);
        ;
        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());
    }

    @Test
    public void byteArrayCanBeConvertedOnArrayAllElementsValid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("MTI=");
        array.add("MTIz");
        array.add(new byte[]{1, 2, 3});
        array.add(null);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationCanBeConvertedToByteArray()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
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
    public void byteArrayCanBeConvertedOnArrayAllElementsInvalid() throws Exception {
        JsonArray array = getJsonManager().createArray();
        array.add(true);
        array.add("MTI=");
        array.add("MT漢");
        array.add(null);
        array.add("");
        array.add("   ");
        array.add(new Date());
        array.add(new BigDecimal("12.020"));
        array.add(123L);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationCanBeConvertedToByteArray()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        // Spaces are ignored.
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

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[6]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[7]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);
        ;
        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[8]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);
        ;
        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());
    }

    @Test
    public void byteArrayIsOfTypeOnArrayAllElementsValidAcceptStrings() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("MTI=");
        array.add(new byte[]{1, 2, 3});
        array.add(null);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeByteArrayOrNull(true)
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
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
    }

    @Test
    public void byteArrayIsOfTypeOnArrayAllElementsValidDoesntAcceptStrings() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(new byte[]{1, 2, 3});
        array.add(null);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeByteArrayOrNull(false)
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
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
    }

    @Test
    public void byteArrayIsOfTypeOnArrayAllElementsInvalidAcceptStrings() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("MTI=");
        array.add(new byte[]{1, 2, 3});
        array.add(null);
        array.add("");
        array.add("abc");
        array.add("   ");
        array.add(new Date());
        array.add(new BigDecimal("12.020"));
        array.add(123L);
        array.add(123.34D);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeByteArrayOrNull(true)
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[0]");
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

        messages = validation.getMessages("key1[4]");
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
        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[6]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[7]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);
        ;
        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[8]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);
        ;
        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[9]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());
    }

    @Test
    public void byteArrayIsOfTypeOnArrayAllElementsInvalidDoesntAcceptStrings() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("MTI=");
        array.add(new byte[]{1, 2, 3});
        array.add(null);
        array.add("");
        array.add("abc");
        array.add("   ");
        array.add(new Date());
        array.add(new BigDecimal("12.020"));
        array.add(123L);
        array.add(123.34D);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeByteArrayOrNull(false)
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

        //==========================================
        // Message on the array itself
        //==========================================
        messages = validation.getMessages("_key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[5]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[6]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[7]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);
        ;
        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[8]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);
        ;
        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[9]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());
    }

    @Test
    public void dateCanBeConvertedOnArrayAllElementsValid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(null);
        array.add("2016-09-18T18:45+0000");
        array.add(new Date());

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationCanBeConvertedToDate()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
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
    }

    @Test
    public void dateCanBeConvertedOnArrayAllElementsInvalid() throws Exception {
        JsonArray array = getJsonManager().createArray();
        array.add(null);
        array.add("2016-09-18T18:45+0000");
        array.add(new Date());
        array.add("2016-09");
        array.add("");
        array.add("abc");
        array.add("   ");
        array.add(123L);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationCanBeConvertedToDate()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[2]");
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

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[5]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[6]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[7]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);
        ;
        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());
    }

    @Test
    public void dateIsOfTypeOnArrayAllElementsValid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(new Date());
        array.add(null);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeDateOrNull()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
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
    }

    @Test
    public void dateIsOfTypeOnArrayAllElementsInvalid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(null);
        array.add("2016-09-18T18:45+0000");
        array.add(new Date());
        array.add("2016-09-18");
        array.add("");
        array.add("abc");
        array.add("   ");
        array.add(123L);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeDateOrNull()
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

        //==========================================
        // Message on the array itself
        //==========================================
        messages = validation.getMessages("_key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[5]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[6]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[7]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);
        ;
        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());
    }

    @Test
    public void jsonObjectCanBeConvertedOnArrayAllElementsValid() throws Exception {

        JsonObject objInner = getJsonManager().create();
        objInner.put("key1", "test");

        JsonArray array = getJsonManager().createArray();
        array.add(null);
        array.add(objInner);
        array.add("{\"key2\":\"test2\"}");

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationCanBeConvertedToJsonObject()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
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
    }

    @Test
    public void jsonObjectCanBeConvertedOnArrayAllElementsInvalid() throws Exception {
        JsonArray array = getJsonManager().createArray();
        array.add("{}");
        array.add("{\"key2\":\"test2\"}");
        array.add("");
        array.add("abc");
        array.add("   ");
        array.add(123L);
        array.add(getJsonManager().create());
        array.add(null);
        array.add("[\"test\"]");

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationCanBeConvertedToJsonObject()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[6]");
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

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[5]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[8]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());
    }

    @Test
    public void jsonObjectIsOfTypeOnArrayAllElementsValid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(getJsonManager().create());
        array.add(null);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeJsonObjectOrNull()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
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
    }

    @Test
    public void jsonObjectIsOfTypeOnArrayAllElementsInvalid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("{}");
        array.add("{\"key2\":\"test2\"}");
        array.add("");
        array.add("abc");
        array.add("   ");
        array.add(123L);
        array.add(getJsonManager().create());
        array.add(null);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeJsonObjectOrNull()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[6]");
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

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[5]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());
    }

    @Test
    public void jsonArrayCanBeConvertedOnArrayAllElementsValid() throws Exception {

        JsonArray arrayInner = getJsonManager().createArray();
        arrayInner.add("test");

        JsonArray array = getJsonManager().createArray();
        array.add(null);
        array.add(arrayInner);
        array.add("[\"test\"]");

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationCanBeConvertedToJsonArray()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
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
    }

    @Test
    public void jsonArrayCanBeConvertedOnArrayAllElementsInvalid() throws Exception {
        JsonArray array = getJsonManager().createArray();
        array.add("{}");
        array.add("{\"key2\":\"test2\"}");
        array.add("");
        array.add("abc");
        array.add("   ");
        array.add(123L);
        array.add(getJsonManager().create());
        array.add(getJsonManager().createArray());
        array.add(null);
        array.add("[]");
        array.add("[\"test\"]");

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationCanBeConvertedToJsonArray()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[7]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[8]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[9]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[10]");
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

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[5]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[6]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_CAN_BE_CONVERTED_TO, message.getCode());
        assertEquals("element message", message.getText());

    }

    @Test
    public void jsonArrayIsOfTypeOnArrayAllElementsValid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(getJsonManager().createArray());
        array.add(null);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeJsonArrayOrNull()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
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
    }

    @Test
    public void jsonArrayIsOfTypeOnArrayAllElementsInvalid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("{}");
        array.add("{\"key2\":\"test2\"}");
        array.add("");
        array.add("abc");
        array.add("   ");
        array.add(123L);
        array.add(getJsonManager().create());
        array.add(getJsonManager().createArray());
        array.add(null);
        array.add("[]");
        array.add("[\"test\"]");

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationIsOfTypeJsonArrayOrNull()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[7]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[8]");
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

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[5]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[6]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[9]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[10]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_IS_OF_TYPE, message.getCode());
        assertEquals("element message", message.getText());
    }

}
