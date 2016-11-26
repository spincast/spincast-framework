package org.spincast.tests.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonObject;
import org.spincast.core.validation.JsonArrayValidationSet;
import org.spincast.core.validation.JsonObjectValidationSet;
import org.spincast.core.validation.ValidationLevel;
import org.spincast.core.validation.ValidationMessage;
import org.spincast.core.validation.ValidationSet;

public class ValidationBuilderBaseTest extends ValidationTestBase {

    @Test
    public void successDefault() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotBlank()
                                                  .key("key1")
                                                  .element("toto")
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(0, messages.size());
    }

    @Test
    public void validationKeyNull() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        try {
            validationFinal.validationNotBlank()
                           .key(null)
                           .element("toto")
                           .validate();
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void validationKeyEmpty() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        try {
            validationFinal.validationNotBlank()
                           .key("   ")
                           .element("toto")
                           .validate();
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void errorDefault() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotBlank()
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

        assertEquals(ValidationLevel.ERROR, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
        assertEquals(getSpincastDictionary().validation_not_blank_default_text(), message.getText());
    }

    @Test
    public void errorAsWarning() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotBlank()
                                                  .key("key1")
                                                  .element("")
                                                  .treatErrorAsWarning()
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isWarning());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationLevel.WARNING, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
        assertEquals(getSpincastDictionary().validation_not_blank_default_text(), message.getText());
    }

    @Test
    public void errorAsWarningCustomMessage() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotBlank()
                                                  .key("key1")
                                                  .element("")
                                                  .treatErrorAsWarning()
                                                  .failMessageText("My custom message")
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isWarning());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationLevel.WARNING, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
        assertEquals("My custom message", message.getText());
    }

    @Test
    public void errorAsWarningCustomMessageNull() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotBlank()
                                                  .key("key1")
                                                  .element("")
                                                  .treatErrorAsWarning()
                                                  .failMessageText(null)
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isWarning());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationLevel.WARNING, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
        assertEquals(getSpincastDictionary().validation_not_blank_default_text(), message.getText());
    }

    @Test
    public void errorAsWarningCustomMessageEmpty() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotBlank()
                                                  .key("key1")
                                                  .element("")
                                                  .treatErrorAsWarning()
                                                  .failMessageText("")
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isWarning());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationLevel.WARNING, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
        assertEquals("", message.getText());
    }

    @Test
    public void successAddMessage() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotBlank()
                                                  .key("key1")
                                                  .element("toto")
                                                  .addMessageOnSuccess()
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationLevel.SUCCESS, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
        assertEquals(getSpincastDictionary().validation_success_message_default_text(), message.getText());
    }

    @Test
    public void successAddMessageCustomText() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotBlank()
                                                  .key("key1")
                                                  .element("toto")
                                                  .addMessageOnSuccess("My custom text")
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationLevel.SUCCESS, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
        assertEquals("My custom text", message.getText());
    }

    @Test
    public void successAddMessageCustomTextAndCode() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotBlank()
                                                  .key("key1")
                                                  .element("toto")
                                                  .code("CUSTOM_CODE")
                                                  .addMessageOnSuccess("My custom text")
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationLevel.SUCCESS, message.getValidationLevel());
        assertEquals("CUSTOM_CODE", message.getCode());
        assertEquals("My custom text", message.getText());
    }

    @Test
    public void successAddMessageCustomTextAndCodeNull() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotBlank()
                                                  .key("key1")
                                                  .element("toto")
                                                  .code(null)
                                                  .addMessageOnSuccess("My custom text")
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationLevel.SUCCESS, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
        assertEquals("My custom text", message.getText());
    }

    @Test
    public void successAddMessageCustomTextAndCodeEmpty() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotBlank()
                                                  .key("key1")
                                                  .element("toto")
                                                  .code("")
                                                  .addMessageOnSuccess("My custom text")
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationLevel.SUCCESS, message.getValidationLevel());
        assertEquals("", message.getCode());
        assertEquals("My custom text", message.getText());
    }

    @Test
    public void arraySuccessDefault() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("aaa");
        array.add("bbb");

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotBlank()
                                                  .key("key1")
                                                  .all(array)
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(0, messages.size());
    }

    @Test
    public void arrayNull() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("aaa");
        array.add("bbb");

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotBlank()
                                                  .key("key1")
                                                  .all(null)
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validationFinal.getMessages().size());
    }

    @Test
    public void arrayNullSuccessNoMessageByDefault() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotBlank()
                                                  .key("key1")
                                                  .all(null)
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(0, validationFinal.getMessages().size());
    }

    @Test
    public void arrayNullSuccessNoMessage() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotBlank()
                                                  .key("key1")
                                                  .all(null)
                                                  .arrayItselfAddSuccessMessage("_key1", null)
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(1, validationFinal.getMessages().size());

        List<ValidationMessage> messages = validationFinal.getMessages("_key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationLevel.SUCCESS, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
        assertEquals(getSpincastDictionary().validation_array_itself_success_message_default_text(), message.getText());
    }

    @Test
    public void arraySuccessElementMessages() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("aaa");
        array.add("bbb");

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotBlank()
                                                  .key("key1")
                                                  .all(array)
                                                  .addMessageOnSuccess()
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(0, messages.size());
    }

    @Test
    public void arraySuccessArrayItselfMessageDefault() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("aaa");
        array.add("bbb");

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotBlank()
                                                  .key("key1")
                                                  .all(array)
                                                  .addMessageOnSuccess()
                                                  .arrayItselfAddSuccessMessage()
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        List<ValidationMessage> messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertTrue(message.isSuccess());

        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
        assertEquals(getSpincastDictionary().validation_array_itself_success_message_default_text(), message.getText());
    }

    @Test
    public void arraySuccessArrayItselfCustomMessage() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("aaa");
        array.add("bbb");

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotBlank()
                                                  .key("key1")
                                                  .all(array)
                                                  .addMessageOnSuccess()
                                                  .arrayItselfAddSuccessMessage("toto")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        List<ValidationMessage> messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertTrue(message.isSuccess());

        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
        assertEquals("toto", message.getText());
    }

    @Test
    public void arraySuccessArrayItselfCustomValidationKey() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("aaa");
        array.add("bbb");

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotBlank()
                                                  .key("key1")
                                                  .all(array)
                                                  .addMessageOnSuccess()
                                                  .arrayItselfAddSuccessMessage("myKey", null)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        List<ValidationMessage> messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        messages = validation.getMessages("myKey");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertTrue(message.isSuccess());

        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
        assertEquals(getSpincastDictionary().validation_array_itself_success_message_default_text(), message.getText());
    }

    @Test
    public void arraySuccessArrayItselfCustomMessageAndValidationKey() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("aaa");
        array.add("bbb");

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotBlank()
                                                  .key("key1")
                                                  .all(array)
                                                  .addMessageOnSuccess()
                                                  .arrayItselfAddSuccessMessage("myKey", "toto")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        List<ValidationMessage> messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        messages = validation.getMessages("myKey");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertTrue(message.isSuccess());

        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
        assertEquals("toto", message.getText());
    }

    @Test
    public void arrayFailArrayItselfMessageDefault() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("aaa");
        array.add("bbb");

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationBlank()
                                                  .key("key1")
                                                  .all(array)
                                                  .addMessageOnSuccess()
                                                  .arrayItselfAddFailMessage()
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertTrue(message.isError());

        assertEquals(ValidationSet.VALIDATION_CODE_BLANK, message.getCode());
        assertEquals(getSpincastDictionary().validation_array_itself_error_message_default_text(), message.getText());
    }

    @Test
    public void arrayFailArrayItselfCustomMessage() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("aaa");
        array.add("bbb");

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationBlank()
                                                  .key("key1")
                                                  .all(array)
                                                  .addMessageOnSuccess()
                                                  .arrayItselfAddFailMessage("toto")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertTrue(message.isError());

        assertEquals(ValidationSet.VALIDATION_CODE_BLANK, message.getCode());
        assertEquals("toto", message.getText());
    }

    @Test
    public void arrayFailArrayItselfCustomValidationKey() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("aaa");
        array.add("bbb");

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationBlank()
                                                  .key("key1")
                                                  .all(array)
                                                  .addMessageOnSuccess()
                                                  .arrayItselfAddFailMessage("myKey", null)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        messages = validation.getMessages("myKey");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertTrue(message.isError());

        assertEquals(ValidationSet.VALIDATION_CODE_BLANK, message.getCode());
        assertEquals(getSpincastDictionary().validation_array_itself_error_message_default_text(), message.getText());
    }

    @Test
    public void arrayFailArrayItselfCustomMessageAndValidationKey() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("aaa");
        array.add("bbb");

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationBlank()
                                                  .key("key1")
                                                  .all(array)
                                                  .addMessageOnSuccess()
                                                  .arrayItselfAddFailMessage("myKey", "toto")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        messages = validation.getMessages("myKey");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertTrue(message.isError());

        assertEquals(ValidationSet.VALIDATION_CODE_BLANK, message.getCode());
        assertEquals("toto", message.getText());
    }

    @Test
    public void arrayWarningArrayItselfMessageDefault() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("aaa");
        array.add("bbb");

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationBlank()
                                                  .key("key1")
                                                  .all(array)
                                                  .addMessageOnSuccess()
                                                  .treatErrorAsWarning()
                                                  .arrayItselfAddFailMessage()
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isWarning());

        List<ValidationMessage> messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertTrue(message.isWarning());

        assertEquals(ValidationSet.VALIDATION_CODE_BLANK, message.getCode());
        assertEquals(getSpincastDictionary().validation_array_itself_warning_message_default_text(), message.getText());
    }

    @Test
    public void arrayWarningArrayItselfCustomMessage() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("aaa");
        array.add("bbb");

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationBlank()
                                                  .key("key1")
                                                  .all(array)
                                                  .addMessageOnSuccess()
                                                  .treatErrorAsWarning()
                                                  .arrayItselfAddFailMessage("toto")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isWarning());

        List<ValidationMessage> messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertTrue(message.isWarning());

        assertEquals(ValidationSet.VALIDATION_CODE_BLANK, message.getCode());
        assertEquals("toto", message.getText());
    }

    @Test
    public void arrayWarningArrayItselfCustomValidationKey() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("aaa");
        array.add("bbb");

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationBlank()
                                                  .key("key1")
                                                  .all(array)
                                                  .addMessageOnSuccess()
                                                  .treatErrorAsWarning()
                                                  .arrayItselfAddFailMessage("myKey", null)
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isWarning());

        List<ValidationMessage> messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        messages = validation.getMessages("myKey");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertTrue(message.isWarning());

        assertEquals(ValidationSet.VALIDATION_CODE_BLANK, message.getCode());
        assertEquals(getSpincastDictionary().validation_array_itself_warning_message_default_text(), message.getText());
    }

    @Test
    public void arrayWarningArrayItselfCustomMessageAndValidationKey() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("aaa");
        array.add("bbb");

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationBlank()
                                                  .key("key1")
                                                  .all(array)
                                                  .addMessageOnSuccess()
                                                  .treatErrorAsWarning()
                                                  .arrayItselfAddFailMessage("myKey", "toto")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isWarning());

        List<ValidationMessage> messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        messages = validation.getMessages("myKey");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertTrue(message.isWarning());

        assertEquals(ValidationSet.VALIDATION_CODE_BLANK, message.getCode());
        assertEquals("toto", message.getText());
    }

    @Test
    public void arraySuccessAddMessage() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("aaa");
        array.add("bbb");

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotBlank()
                                                  .key("key1")
                                                  .all(array)
                                                  .addMessageOnSuccess()
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isSuccess());
        assertEquals(2, validationFinal.getMessages().size());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validationFinal.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
        assertEquals(getSpincastDictionary().validation_success_message_default_text(), message.getText());

        messages = validationFinal.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
        assertEquals(getSpincastDictionary().validation_success_message_default_text(), message.getText());
    }

    @Test
    public void successDefaultOnJsonObject() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "test");

        JsonObjectValidationSet validationFinal = obj.validationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotBlank()
                                                  .jsonPath("key1")
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(0, messages.size());
    }

    @Test
    public void successDefaultElementOnJsonObject() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "test");

        JsonObjectValidationSet validationFinal = obj.validationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotBlank()
                                                  .key("key1")
                                                  .element("toto")
                                                  .addMessageOnSuccess()
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
        assertEquals(getSpincastDictionary().validation_success_message_default_text(), message.getText());
    }

    @Test
    public void arrayOnJsonObject() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(123);
        array.add("");
        array.add("abc");
        array.add(null);
        array.add("   ");

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();

        ValidationSet validation = validationFinal.validationNotBlank()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("blank!")
                                                  .arrayItselfAddFailMessage("_key1", "some array custom message")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validationFinal.getMessages("key1[2]");
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

        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
        assertEquals("some array custom message", message.getText());

        messages = validationFinal.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
        assertEquals("blank!", message.getText());

        messages = validationFinal.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
        assertEquals("blank!", message.getText());

        messages = validationFinal.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
        assertEquals("blank!", message.getText());
    }

    @Test
    public void arrayOnJsonObjectCustomValidationKey() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(123);
        array.add("");
        array.add("abc");
        array.add(null);
        array.add("   ");

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();

        ValidationSet validation = validationFinal.validationNotBlank()
                                                  .jsonPathAll("key1", "customKey")
                                                  .failMessageText("blank!")
                                                  .arrayItselfAddFailMessage("_key1", "some array custom message")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("customKey[0]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validationFinal.getMessages("customKey[2]");
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
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
        assertEquals("some array custom message", message.getText());

        messages = validationFinal.getMessages("customKey[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
        assertEquals("blank!", message.getText());

        messages = validationFinal.getMessages("customKey[3]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
        assertEquals("blank!", message.getText());

        messages = validationFinal.getMessages("customKey[4]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
        assertEquals("blank!", message.getText());
    }

    @Test
    public void arrayExpectedButIsNot() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "nope");

        JsonObjectValidationSet validationFinal = obj.validationSet();

        ValidationSet validation = validationFinal.validationNotBlank()
                                                  .jsonPathAll("key1")
                                                  .failMessageText("blank!")
                                                  .arrayItselfAddFailMessage("_key1", "some invalid custom")
                                                  .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationSet.VALIDATION_CODE_NOT_AN_ARRAY, message.getCode());
        assertEquals("some invalid custom", message.getText());
    }

    @Test
    public void multipleValidations() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotBlank()
                                                  .key("key1")
                                                  .element("toto")
                                                  .addMessageOnSuccess()
                                                  .validate();

        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        List<ValidationMessage> messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationLevel.SUCCESS, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
        assertEquals(getSpincastDictionary().validation_success_message_default_text(), message.getText());

        validation = validationFinal.validationNotBlank()
                                    .key("key1")
                                    .element("")
                                    .validate();

        assertNotNull(validation);
        assertTrue(validation.isError());

        messages = validation.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationLevel.ERROR, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
        assertEquals(getSpincastDictionary().validation_not_blank_default_text(), message.getText());

        messages = validationFinal.getMessages("key1");
        assertEquals(2, messages.size());

        message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationLevel.SUCCESS, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
        assertEquals(getSpincastDictionary().validation_success_message_default_text(), message.getText());

        message = messages.get(1);
        assertNotNull(message);

        assertEquals(ValidationLevel.ERROR, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
        assertEquals(getSpincastDictionary().validation_not_blank_default_text(), message.getText());
    }

    @Test
    public void prefixPaths() throws Exception {

        JsonObject obj1 = getJsonManager().create();
        obj1.put("key1", "ac");
        obj1.put("key2", "bb");

        JsonObject obj2 = getJsonManager().create();
        obj2.put("key1", "ab");
        obj2.put("key2", "bbb");

        JsonArray arrayInner = getJsonManager().createArray();
        arrayInner.add(123);
        arrayInner.add(3);

        JsonArray array1 = getJsonManager().createArray();
        array1.add(obj1);
        array1.add(obj2);
        array1.add(arrayInner);

        JsonObject obj = getJsonManager().create();
        obj.put("theArray", array1);

        JsonObjectValidationSet validationSet = obj.validationSet();

        // From the root
        validationSet.validationSize(7, false).jsonPath("theArray").validate();

        // From the array
        JsonArray arr = obj.getJsonArray("theArray");
        JsonArrayValidationSet arrayValidation = arr.validationSet();
        arrayValidation.validationEquivalent("nope").jsonPath("[0].key1").validate();

        // From leaf objects
        for(int i = 0; i < 2; i++) {
            JsonObject oneObj = arr.getJsonObject(i);

            JsonObjectValidationSet objValidation = oneObj.validationSet();
            objValidation.validationLength(3).jsonPath("key1").validate();
            objValidation.validationLength(3).jsonPath("key2").validate();
            arrayValidation.mergeValidationSet("[" + i + "].", objValidation);
        }
        JsonArray oneInnerArray = arr.getJsonArray(2);
        assertNotNull(oneInnerArray);
        JsonArrayValidationSet innerArrayValidation = oneInnerArray.validationSet();
        innerArrayValidation.validationLess(10).jsonPath("[0]").validate();
        innerArrayValidation.validationLess(10).jsonPath("[1]").validate();
        arrayValidation.mergeValidationSet("[2]", innerArrayValidation);

        validationSet.mergeValidationSet("theArray", arrayValidation);

        assertEquals(5, validationSet.getMessages().size());

        List<ValidationMessage> messages = validationSet.getMessages("theArray");
        assertEquals(1, messages.size());
        ValidationMessage message = messages.get(0);
        assertEquals(ValidationSet.VALIDATION_CODE_SIZE, message.getCode());

        messages = validationSet.getMessages("theArray[0].key1");
        assertEquals(2, messages.size());
        message = messages.get(0);
        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT, message.getCode());
        message = messages.get(1);
        assertEquals(ValidationSet.VALIDATION_CODE_LENGTH, message.getCode());

        messages = validationSet.getMessages("theArray[0].key2");
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertEquals(ValidationSet.VALIDATION_CODE_LENGTH, message.getCode());

        messages = validationSet.getMessages("theArray[1].key1");
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertEquals(ValidationSet.VALIDATION_CODE_LENGTH, message.getCode());

        messages = validationSet.getMessages("theArray[1].key2");
        assertEquals(0, messages.size());

        messages = validationSet.getMessages("theArray[2][0]");
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertEquals(ValidationSet.VALIDATION_CODE_LESS, message.getCode());

        messages = validationSet.getMessages("theArray[2][1]");
        assertEquals(0, messages.size());
    }

    @Test
    public void arrayAtIndex() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(123);
        array.add(3);

        JsonArrayValidationSet validationSet = array.validationSet();

        validationSet.validationEquivalentOrGreater(10).index(1).validate();

        assertEquals(1, validationSet.getMessages().size());

        List<ValidationMessage> messages = validationSet.getMessages("[1]");
        assertEquals(1, messages.size());
        ValidationMessage message = messages.get(0);
        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT_OR_GREATER, message.getCode());
    }

    @Test
    public void arrayIndexAll() throws Exception {

        JsonArray arrayInner = getJsonManager().createArray();
        arrayInner.add(123);
        arrayInner.add(3);
        arrayInner.add(5);
        JsonArray array = getJsonManager().createArray();
        array.add(123);
        array.add(arrayInner);

        JsonArrayValidationSet validationSet = array.validationSet();

        validationSet.validationEquivalentOrGreater(10).indexAll(1).arrayItselfAddFailMessage("_key1", null).validate();

        assertEquals(3, validationSet.getMessages().size());

        List<ValidationMessage> messages = validationSet.getMessages("[1][1]");
        assertEquals(1, messages.size());
        ValidationMessage message = messages.get(0);
        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT_OR_GREATER, message.getCode());

        messages = validationSet.getMessages("[1][2]");
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT_OR_GREATER, message.getCode());

        // For the array itself
        messages = validationSet.getMessages("_key1");
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertEquals(ValidationSet.VALIDATION_CODE_EQUIVALENT_OR_GREATER, message.getCode());

        messages = validationSet.getMessages("[1][0]");
        assertEquals(0, messages.size());
    }

    @Test
    public void arrayValidationOfItselfAndElements() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(123);
        array.add(23);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationSet = obj.validationSet();
        validationSet.validationSize(10, false).jsonPath("_key1").validate();
        validationSet.validationLess(10).jsonPathAll("key1").arrayItselfAddFailMessage("_key1", null).validate();

        List<ValidationMessage> messages = validationSet.getMessages("_key1");
        assertEquals(2, messages.size());
        ValidationMessage message = messages.get(0);
        assertEquals(ValidationSet.VALIDATION_CODE_SIZE, message.getCode());
        message = messages.get(1);
        assertEquals(ValidationSet.VALIDATION_CODE_LESS, message.getCode());

        messages = validationSet.getMessages("key1[0]");
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertEquals(ValidationSet.VALIDATION_CODE_LESS, message.getCode());

        messages = validationSet.getMessages("key1[1]");
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertEquals(ValidationSet.VALIDATION_CODE_LESS, message.getCode());

        JsonObject resultObj = validationSet.convertToJsonObject();

        JsonArray valResult = resultObj.getJsonArray("_key1.messages");
        assertNotNull(valResult);
        assertEquals(2, valResult.size());

        valResult = resultObj.getJsonArray("['key1[0]'].messages");
        assertNotNull(valResult);
        assertEquals(1, valResult.size());

        valResult = resultObj.getJsonArray("['key1[1]'].messages");
        assertNotNull(valResult);
        assertEquals(1, valResult.size());
    }

    @Test
    public void arrayValidationOfItselfFailAsWarning() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("");

        ValidationSet validationSet = getValidationFactory().createValidationSet();
        validationSet.validationNotBlank()
                     .key("key1")
                     .all(array)
                     .treatErrorAsWarning()
                     .arrayItselfAddFailMessage()
                     .validate();

        assertEquals(2, validationSet.getMessages().size());

        List<ValidationMessage> messages = validationSet.getMessages("key1");
        assertEquals(1, messages.size());
        ValidationMessage message = messages.get(0);
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
        assertEquals(ValidationLevel.WARNING, message.getValidationLevel());

        messages = validationSet.getMessages("key1[0]");
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
        assertEquals(ValidationLevel.WARNING, message.getValidationLevel());

    }

}
