package org.spincast.tests.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.spincast.core.json.JsonArray;
import org.spincast.core.validation.ValidationLevel;
import org.spincast.core.validation.ValidationMessage;
import org.spincast.core.validation.ValidationSet;

public class ValidationBuilderIfNoMessageYetTest extends ValidationTestBase {

    @Test
    public void validateOnlyIfNoMessageYetTrueNoMessage() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotNull()
                                                  .key("key1")
                                                  .element("ok")
                                                  .validate(true);

        validation = validationFinal.validationNotBlank()
                                    .key("key1")
                                    .element(null)
                                    .validate(true);

        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationLevel.ERROR, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
    }

    @Test
    public void validateOnlyIfNoMessageYetTrueError() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotNull()
                                                  .key("key1")
                                                  .element(null)
                                                  .validate(true);

        validation = validationFinal.validationNotBlank()
                                    .key("key1")
                                    .element(null)
                                    .validate(true);

        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationLevel.ERROR, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_NULL, message.getCode());
    }

    @Test
    public void validateOnlyIfNoMessageYetTrueWarning() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotNull()
                                                  .key("key1")
                                                  .element(null)
                                                  .treatErrorAsWarning()
                                                  .validate(true);

        validation = validationFinal.validationNotBlank()
                                    .key("key1")
                                    .element(null)
                                    .validate(true);

        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationLevel.WARNING, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_NULL, message.getCode());
    }

    @Test
    public void validateOnlyIfNoMessageYetTrueSuccess() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotNull()
                                                  .key("key1")
                                                  .element("ok")
                                                  .addMessageOnSuccess()
                                                  .validate(true);

        validation = validationFinal.validationNotBlank()
                                    .key("key1")
                                    .element(null)
                                    .validate(true);

        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationLevel.SUCCESS, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_NULL, message.getCode());
    }

    @Test
    public void validateOnlyIfNoMessageYetSuccessNoMessage() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotNull()
                                                  .key("key1")
                                                  .element("ok")
                                                  .validate(ValidationLevel.SUCCESS);

        validation = validationFinal.validationNotBlank()
                                    .key("key1")
                                    .element(null)
                                    .validate(ValidationLevel.SUCCESS);

        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationLevel.ERROR, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
    }

    @Test
    public void validateOnlyIfNoMessageYetSuccessError() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotNull()
                                                  .key("key1")
                                                  .element(null)
                                                  .validate(ValidationLevel.SUCCESS);

        validation = validationFinal.validationNotBlank()
                                    .key("key1")
                                    .element(null)
                                    .validate(ValidationLevel.SUCCESS);

        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationLevel.ERROR, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_NULL, message.getCode());
    }

    @Test
    public void validateOnlyIfNoMessageYetSuccessWarning() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotNull()
                                                  .key("key1")
                                                  .element(null)
                                                  .treatErrorAsWarning()
                                                  .validate(ValidationLevel.SUCCESS);

        validation = validationFinal.validationNotBlank()
                                    .key("key1")
                                    .element(null)
                                    .validate(ValidationLevel.SUCCESS);

        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationLevel.WARNING, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_NULL, message.getCode());
    }

    @Test
    public void validateOnlyIfNoMessageYetSuccessSuccess() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotNull()
                                                  .key("key1")
                                                  .element("ok")
                                                  .addMessageOnSuccess()
                                                  .validate(ValidationLevel.SUCCESS);

        validation = validationFinal.validationNotBlank()
                                    .key("key1")
                                    .element(null)
                                    .validate(ValidationLevel.SUCCESS);

        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationLevel.SUCCESS, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_NULL, message.getCode());
    }

    @Test
    public void validateOnlyIfNoMessageYetWarningNoMessage() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotNull()
                                                  .key("key1")
                                                  .element("ok")
                                                  .validate(ValidationLevel.WARNING);

        validation = validationFinal.validationNotBlank()
                                    .key("key1")
                                    .element(null)
                                    .validate(ValidationLevel.WARNING);

        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationLevel.ERROR, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
    }

    @Test
    public void validateOnlyIfNoMessageYetWarningError() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotNull()
                                                  .key("key1")
                                                  .element(null)
                                                  .validate(ValidationLevel.WARNING);

        validation = validationFinal.validationNotBlank()
                                    .key("key1")
                                    .element(null)
                                    .validate(ValidationLevel.WARNING);

        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationLevel.ERROR, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_NULL, message.getCode());
    }

    @Test
    public void validateOnlyIfNoMessageYetWarningWarning() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotNull()
                                                  .key("key1")
                                                  .element(null)
                                                  .treatErrorAsWarning()
                                                  .validate(ValidationLevel.WARNING);

        validation = validationFinal.validationNotBlank()
                                    .key("key1")
                                    .element(null)
                                    .validate(ValidationLevel.WARNING);

        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationLevel.WARNING, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_NULL, message.getCode());
    }

    @Test
    public void validateOnlyIfNoMessageYetWarningSuccess() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotNull()
                                                  .key("key1")
                                                  .element("ok")
                                                  .addMessageOnSuccess()
                                                  .validate(ValidationLevel.WARNING);

        validation = validationFinal.validationNotBlank()
                                    .key("key1")
                                    .element(null)
                                    .validate(ValidationLevel.WARNING);

        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(2, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationLevel.SUCCESS, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_NULL, message.getCode());

        message = messages.get(1);
        assertNotNull(message);

        assertEquals(ValidationLevel.ERROR, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
    }

    @Test
    public void validateOnlyIfNoMessageYetErrorNoMessage() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotNull()
                                                  .key("key1")
                                                  .element("ok")
                                                  .validate(ValidationLevel.ERROR);

        validation = validationFinal.validationNotBlank()
                                    .key("key1")
                                    .element(null)
                                    .validate(ValidationLevel.ERROR);

        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationLevel.ERROR, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
    }

    @Test
    public void validateOnlyIfNoMessageYetErrorError() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotNull()
                                                  .key("key1")
                                                  .element(null)
                                                  .validate(ValidationLevel.ERROR);

        validation = validationFinal.validationNotBlank()
                                    .key("key1")
                                    .element(null)
                                    .validate(ValidationLevel.ERROR);

        assertNotNull(validation);
        assertTrue(validation.isSuccess());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationLevel.ERROR, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_NULL, message.getCode());
    }

    @Test
    public void validateOnlyIfNoMessageYetErrorWarning() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotNull()
                                                  .key("key1")
                                                  .element(null)
                                                  .treatErrorAsWarning()
                                                  .validate(ValidationLevel.ERROR);

        validation = validationFinal.validationNotBlank()
                                    .key("key1")
                                    .element(null)
                                    .validate(ValidationLevel.ERROR);

        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(2, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationLevel.WARNING, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_NULL, message.getCode());

        message = messages.get(1);
        assertNotNull(message);

        assertEquals(ValidationLevel.ERROR, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
    }

    @Test
    public void validateOnlyIfNoMessageYetErrorSuccess() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        ValidationSet validation = validationFinal.validationNotNull()
                                                  .key("key1")
                                                  .element("ok")
                                                  .addMessageOnSuccess()
                                                  .validate(ValidationLevel.ERROR);

        validation = validationFinal.validationNotBlank()
                                    .key("key1")
                                    .element(null)
                                    .validate(ValidationLevel.ERROR);

        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validationFinal.getMessages("key1");
        assertNotNull(messages);
        assertEquals(2, messages.size());

        ValidationMessage message = messages.get(0);
        assertNotNull(message);

        assertEquals(ValidationLevel.SUCCESS, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_NULL, message.getCode());

        message = messages.get(1);
        assertNotNull(message);

        assertEquals(ValidationLevel.ERROR, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
    }

    @Test
    public void validateOnlyIfNoMessageYetTrueOnArray() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        JsonArray array = getJsonManager().createArray();
        array.add("first");
        array.add(null);
        array.add("");

        ValidationSet validation = validationFinal.validationNotNull()
                                                  .key("key1")
                                                  .all(array)
                                                  .validate();

        validation = validationFinal.validationNotBlank()
                                    .key("key1")
                                    .all(array)
                                    .validate(true);

        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validationFinal.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validationFinal.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationLevel.ERROR, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_NULL, message.getCode());

        messages = validationFinal.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationLevel.ERROR, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
    }

    @Test
    public void validateOnlyIfNoMessageYetSuccessOnArray() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        JsonArray array = getJsonManager().createArray();
        array.add("first");
        array.add(null);
        array.add("");

        ValidationSet validation = validationFinal.validationNotNull()
                                                  .key("key1")
                                                  .all(array)
                                                  .validate();

        validation = validationFinal.validationNotBlank()
                                    .key("key1")
                                    .all(array)
                                    .validate(ValidationLevel.SUCCESS);

        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validationFinal.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validationFinal.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationLevel.ERROR, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_NULL, message.getCode());

        messages = validationFinal.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationLevel.ERROR, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
    }

    @Test
    public void validateOnlyIfNoMessageYetWarningOnArray() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        JsonArray array = getJsonManager().createArray();
        array.add("first");
        array.add(null);
        array.add("");

        ValidationSet validation = validationFinal.validationNotNull()
                                                  .key("key1")
                                                  .all(array)
                                                  .treatErrorAsWarning()
                                                  .validate();

        validation = validationFinal.validationNotBlank()
                                    .key("key1")
                                    .all(array)
                                    .validate(ValidationLevel.WARNING);

        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validationFinal.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validationFinal.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationLevel.WARNING, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_NULL, message.getCode());

        messages = validationFinal.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationLevel.ERROR, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
    }

    @Test
    public void validateOnlyIfNoMessageYetErrorOnArray() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        JsonArray array = getJsonManager().createArray();
        array.add("first");
        array.add(null);
        array.add("");

        ValidationSet validation = validationFinal.validationNotNull()
                                                  .key("key1")
                                                  .all(array)
                                                  .validate();

        validation = validationFinal.validationNotBlank()
                                    .key("key1")
                                    .all(array)
                                    .validate(ValidationLevel.ERROR);

        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validationFinal.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validationFinal.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationLevel.ERROR, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_NULL, message.getCode());

        messages = validationFinal.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationLevel.ERROR, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
    }

    @Test
    public void validateOnlyIfNoMessageYetErrorOnArray2() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        assertNotNull(validationFinal);

        JsonArray array = getJsonManager().createArray();
        array.add("first");
        array.add(null);
        array.add("");

        ValidationSet validation = validationFinal.validationNotNull()
                                                  .key("key1")
                                                  .all(array)
                                                  .treatErrorAsWarning()
                                                  .validate();

        validation = validationFinal.validationNotBlank()
                                    .key("key1")
                                    .all(array)
                                    .validate(ValidationLevel.ERROR);

        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validationFinal.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(0, messages.size());

        messages = validationFinal.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(2, messages.size());
        ValidationMessage message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationLevel.WARNING, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_NULL, message.getCode());
        message = messages.get(1);
        assertNotNull(message);
        assertEquals(ValidationLevel.ERROR, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());

        messages = validationFinal.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(1, messages.size());
        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationLevel.ERROR, message.getValidationLevel());
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_BLANK, message.getCode());
    }
}
