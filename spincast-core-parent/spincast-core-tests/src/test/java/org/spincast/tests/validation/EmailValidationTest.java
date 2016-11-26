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

public class EmailValidationTest extends ValidationTestBase {

    @Test
    public void emailValid() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEmail()
                                                  .key("key1")
                                                  .element("test@example.com")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void emailInvalid() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEmail()
                                                  .key("key1")
                                                  .element("test")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EMAIL, message.getCode());
    }

    @Test
    public void emailEmpty() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEmail()
                                                  .key("key1")
                                                  .element("")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EMAIL, message.getCode());
    }

    @Test
    public void emailNull() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationEmail()
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

        assertEquals(ValidationSet.VALIDATION_CODE_EMAIL, message.getCode());
    }

    @Test
    public void emailValidOnJsonObject() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "test@example.com");

        JsonObjectValidationSet validationFinal = obj.validationSet();

        ValidationSet validation = validationFinal.validationEmail()
                                                  .jsonPath("key1")
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validation.getMessages("key1").size());
    }

    @Test
    public void emailInvalidOnJsonObject() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "test");

        JsonObjectValidationSet validationFinal = obj.validationSet();

        ValidationSet validation = validationFinal.validationEmail()
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EMAIL, message.getCode());
    }

    @Test
    public void emailEmptyOnJsonObject() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "");

        JsonObjectValidationSet validationFinal = obj.validationSet();

        ValidationSet validation = validationFinal.validationEmail()
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EMAIL, message.getCode());
    }

    @Test
    public void emailNullOnJsonObject() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", null);

        JsonObjectValidationSet validationFinal = obj.validationSet();

        ValidationSet validation = validationFinal.validationEmail()
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EMAIL, message.getCode());
    }

    @Test
    public void emailInvalidTypeOnJsonObject() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", 123);

        JsonObjectValidationSet validationFinal = obj.validationSet();

        ValidationSet validation = validationFinal.validationEmail()
                                                  .jsonPath("key1")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validationFinal.isError());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_EMAIL, message.getCode());
    }

    @Test
    public void emailOnArrayAllElementsValid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("test@example.com");
        array.add("test.test2@example.com");

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();

        ValidationSet validation = validationFinal.validationEmail()
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
    }

    @Test
    public void emailOnArrayAllElementsInvalid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(123);
        array.add("");
        array.add("test@example.com");
        array.add(null);
        array.add("   ");
        array.add("5");

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();

        ValidationSet validation = validationFinal.validationEmail()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("element message")
                                                  .arrayItselfAddFailMessage("_key1", "array message")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validationFinal.isError());

        List<ValidationMessage> messages = validationFinal.getMessages("key1[2]");
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
        assertEquals(ValidationSet.VALIDATION_CODE_EMAIL, message.getCode());
        assertEquals("array message", message.getText());

        messages = validationFinal.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_EMAIL, message.getCode());
        assertEquals("element message", message.getText());

        messages = validationFinal.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_EMAIL, message.getCode());
        assertEquals("element message", message.getText());

        messages = validationFinal.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_EMAIL, message.getCode());
        assertEquals("element message", message.getText());

        messages = validationFinal.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_EMAIL, message.getCode());
        assertEquals("element message", message.getText());

        messages = validationFinal.getMessages("key1[5]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_EMAIL, message.getCode());
        assertEquals("element message", message.getText());
    }

}
