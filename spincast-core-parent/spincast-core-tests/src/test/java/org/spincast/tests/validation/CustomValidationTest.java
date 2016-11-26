package org.spincast.tests.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonObject;
import org.spincast.core.validation.JsonObjectValidationSet;
import org.spincast.core.validation.SimpleValidator;
import org.spincast.core.validation.ValidationMessage;
import org.spincast.core.validation.ValidationSet;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

public class CustomValidationTest extends ValidationTestBase {

    public static final String APP_VALIDATION_ERROR_NO_SPACE = "VALIDATION_ERROR_APP_NO_SPACE";

    private SimpleValidator simpleValidator = new SimpleValidator() {

        @Override
        public boolean validate(Object elementToValidate) {

            return elementToValidate != null &&
                   elementToValidate instanceof String &&
                   "test".equals(elementToValidate);
        }

        @Override
        public String getCode() {
            return "NOT_TEST";
        }

        @Override
        public String getSuccessMessage(Object elementToValidate) {
            return "Success!";
        }

        @Override
        public String getFailMessage(Object elementToValidate) {
            return "not test!";
        }
    };

    protected SimpleValidator getSimpleValidator() {
        return this.simpleValidator;
    }

    @Test
    public void customInvalidSpacesTooShort() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        String name = "a ";

        // Name not null
        ValidationSet nameResult = validationFinal.validationNotNull()
                                                  .key("name")
                                                  .element(name)
                                                  .validate();

        if(nameResult.isValid()) {

            // Name: no space in it
            if(name.indexOf(" ") > -1) {
                validationFinal.addError("name", APP_VALIDATION_ERROR_NO_SPACE, "No space in the name!");
            }

            // Name: min 3 characters
            nameResult = validationFinal.validationMinLength(3).key("name").element(name).validate();
        }

        List<ValidationMessage> errors = validationFinal.getMessages("name");
        assertNotNull(errors);
        assertEquals(2, errors.size());

        ValidationMessage error = errors.get(0);
        assertNotNull(error);
        assertEquals(APP_VALIDATION_ERROR_NO_SPACE, error.getCode());
        assertEquals("No space in the name!", error.getText());

        error = errors.get(1);
        assertNotNull(error);
        assertEquals(ValidationSet.VALIDATION_CODE_MIN_LENGTH, error.getCode());
        assertFalse(StringUtils.isBlank(error.getText()));
    }

    @Test
    public void customInvalidSpaces() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        String name = "a b c d";

        // Name not null
        ValidationSet nameResult = validationFinal.validationNotNull()
                                                  .key("name")
                                                  .element(name)
                                                  .validate();

        if(nameResult.isValid()) {

            // Name: no space in it
            if(name.indexOf(" ") > -1) {
                validationFinal.addError("name", APP_VALIDATION_ERROR_NO_SPACE, "No space in the name!");
            }

            // Name: min 3 characters
            nameResult = validationFinal.validationMinLength(3).key("name").element(name).validate();
        }

        List<ValidationMessage> errors = validationFinal.getMessages("name");
        assertNotNull(errors);
        assertEquals(1, errors.size());

        ValidationMessage error = errors.get(0);
        assertNotNull(error);
        assertEquals(APP_VALIDATION_ERROR_NO_SPACE, error.getCode());
        assertEquals("No space in the name!", error.getText());
    }

    @Test
    public void customValidatorUsingBuilder() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();

        ValidationSet result = validationFinal.validation(getSimpleValidator())
                                              .key("key1")
                                              .element("nope")
                                              .validate();
        assertTrue(result.isError());

        result = validationFinal.validation(getSimpleValidator())
                                .key("key2")
                                .element("test")
                                .validate();
        assertTrue(result.isSuccess());

        result = validationFinal.validation(getSimpleValidator())
                                .key("key3")
                                .element("test")
                                .addMessageOnSuccess()
                                .validate();
        assertTrue(result.isSuccess());

        result = validationFinal.validation(getSimpleValidator())
                                .key("key4")
                                .element("nope")
                                .addMessageOnSuccess()
                                .treatErrorAsWarning()
                                .validate();
        assertTrue(result.isWarning());

        assertTrue(validationFinal.isError());

        assertEquals(3, validationFinal.getMessages().size());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertTrue(message.isError());

        assertEquals("NOT_TEST", message.getCode());
        assertEquals("not test!", message.getText());

        messages = validationFinal.getMessages("key2");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validationFinal.getMessages("key3");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);
        assertTrue(message.isSuccess());
        assertEquals("NOT_TEST", message.getCode());
        assertEquals("Success!", message.getText());

        messages = validationFinal.getMessages("key4");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);
        assertTrue(message.isWarning());
        assertEquals("NOT_TEST", message.getCode());
        assertEquals("not test!", message.getText());
    }

    @Test
    public void customArrayValidatorUsingBuilder() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("nope");
        array.add("");
        array.add("test");
        array.add(null);

        ValidationSet validationFinal = getValidationFactory().createValidationSet();

        ValidationSet result = validationFinal.validation(getSimpleValidator())
                                              .key("key1")
                                              .all(array)
                                              .failMessageText("not test!")
                                              .code("NOT_TEST")
                                              .arrayItselfAddFailMessage("_key1", "array custom!")
                                              .validate();
        assertTrue(result.isError());
        assertTrue(validationFinal.isError());

        List<ValidationMessage> messages = validationFinal.getMessages("_key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertTrue(message.isError());

        assertEquals("NOT_TEST", message.getCode());
        assertEquals("array custom!", message.getText());

        messages = validationFinal.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);
        assertTrue(message.isError());
        assertEquals("NOT_TEST", message.getCode());
        assertEquals("not test!", message.getText());

        messages = validationFinal.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);
        assertTrue(message.isError());
        assertEquals("NOT_TEST", message.getCode());
        assertEquals("not test!", message.getText());

        messages = validationFinal.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validationFinal.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);
        assertTrue(message.isError());
        assertEquals("NOT_TEST", message.getCode());
        assertEquals("not test!", message.getText());
    }

    @Test
    public void customValidatorOnJsonObject() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "test");

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet result = validationFinal.validation(getSimpleValidator())
                                              .jsonPath("key1")
                                              .validate();

        assertTrue(result.isSuccess());
        assertTrue(validationFinal.isSuccess());

        List<ValidationMessage> errors = validationFinal.getMessages("key1");
        assertNotNull(errors);
        assertEquals(0, errors.size());
    }

    @Test
    public void customValidationWithValidatorInvalid() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("key1", "nope");

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet result = validationFinal.validation(getSimpleValidator())
                                              .jsonPath("key1")
                                              .treatErrorAsWarning()
                                              .validate();

        assertTrue(result.isWarning());
        assertTrue(result.isValid());
        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertTrue(message.isWarning());

        assertEquals("not test!", message.getText());
    }

    @Test
    public void customArrayValidationWithValidator() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("nope");
        array.add("");
        array.add("test");
        array.add(null);

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet validationFinal = obj.validationSet();
        ValidationSet result = validationFinal.validation(getSimpleValidator())
                                              .jsonPathAll("key1")
                                              .arrayItselfAddFailMessage("_key1", "array custom!")
                                              .validate();

        assertTrue(result.isError());
        assertFalse(result.isValid());

        List<ValidationMessage> messages = validationFinal.getMessages("_key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertTrue(message.isError());
        assertEquals("NOT_TEST", message.getCode());
        assertEquals("array custom!", message.getText());

        messages = validationFinal.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);
        assertTrue(message.isError());
        assertEquals("NOT_TEST", message.getCode());
        assertEquals("not test!", message.getText());

        messages = validationFinal.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);
        assertTrue(message.isError());
        assertEquals("NOT_TEST", message.getCode());
        assertEquals("not test!", message.getText());

        messages = validationFinal.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validationFinal.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);
        assertTrue(message.isError());
        assertEquals("NOT_TEST", message.getCode());
        assertEquals("not test!", message.getText());
    }

}
