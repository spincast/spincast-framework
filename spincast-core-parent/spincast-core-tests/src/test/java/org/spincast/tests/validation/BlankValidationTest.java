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

public class BlankValidationTest extends ValidationTestBase {

    @Test
    public void notBlank() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();

        ValidationSet validation = validationFinal.validationBlank()
                                                  .key("key1")
                                                  .element("123")
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_BLANK, message.getCode());
    }

    @Test
    public void blankEmpty() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();

        ValidationSet validation = validationFinal.validationBlank()
                                                  .key("key1")
                                                  .element("")
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void blankNull() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();

        ValidationSet validation = validationFinal.validationBlank()
                                                  .key("key1")
                                                  .element(null)
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void blankSpacesOnly() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();

        ValidationSet validation = validationFinal.validationBlank()
                                                  .key("key1")
                                                  .element("     ")
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validationFinal.getMessages("key1").size());
    }

    @Test
    public void notBlankOnJsonObject() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "abc");

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationBlank().jsonPath("key1").validate();

        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_BLANK, message.getCode());
    }

    @Test
    public void blankEmptyOnJsonObject() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "");

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationBlank().jsonPath("key1").validate();

        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void blankNullOnJsonObject() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", null);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationBlank().jsonPath("key1").validate();

        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void blankSpacesOnlyOnJsonObject() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "      ");

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationBlank().jsonPath("key1").validate();

        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void blankOnArrayAllElementsValid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("  ");
        array.add("");
        array.add(null);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationBlank().jsonPathAll("key1").validate();

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
    public void blankOnArrayAllElementsInvalid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(123);
        array.add("");
        array.add("abc");
        array.add(null);
        array.add("   ");

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet validation = validationFinal.validationBlank()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("not blank!")
                                                  .arrayItselfAddFailMessage("_key1", "some invalid custom")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validationFinal.isError());

        List<ValidationMessage> messages = validationFinal.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validationFinal.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validationFinal.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        //==========================================
        // Message on the array itself
        //==========================================
        messages = validationFinal.getMessages("_key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_BLANK, message.getCode());
        assertEquals("some invalid custom", message.getText());

        messages = validationFinal.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_BLANK, message.getCode());
        assertEquals("not blank!", message.getText());

        messages = validationFinal.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_BLANK, message.getCode());
        assertEquals("not blank!", message.getText());
    }

}
