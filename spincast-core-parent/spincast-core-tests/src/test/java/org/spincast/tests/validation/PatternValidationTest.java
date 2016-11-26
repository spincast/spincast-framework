package org.spincast.tests.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.tests.validation.utils.User;
import org.spincast.tests.validation.utils.UserDefault;

public class PatternValidationTest extends ValidationTestBase {

    protected ValidationSet validate(User user) {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();

        // Name can only contain "a" and "b".
        ValidationSet validation = validationFinal.validationPattern("[ab]+")
                                                  .key("name")
                                                  .element(user.getName())
                                                  .validate();

        // ... but not be simply "b"
        if(validation.isValid()) {
            validation = validationFinal.validationNotPattern("b")
                                        .key("name")
                                        .element(user.getName())
                                        .validate();
        }

        return validationFinal;
    }

    @Test
    public void nameValid1() throws Exception {

        User user = new UserDefault();
        user.setName("ababababab");

        ValidationSet validation = validate(user);
        assertNotNull(validation);

        assertTrue(validation.isValid());
        assertNotNull(validation.getMessages());
        assertEquals(0, validation.getMessages().size());
    }

    @Test
    public void nameValid2() throws Exception {

        User user = new UserDefault();
        user.setName("a");

        ValidationSet validation = validate(user);
        assertNotNull(validation);

        assertTrue(validation.isValid());
        assertNotNull(validation.getMessages());
        assertEquals(0, validation.getMessages().size());
    }

    @Test
    public void nameInvalid1() throws Exception {

        User user = new UserDefault();
        user.setName("abc");

        ValidationSet validation = validate(user);
        assertNotNull(validation);
        assertFalse(validation.isValid());

        List<ValidationMessage> errors = validation.getMessages().get("name");
        assertNotNull(errors);
        assertEquals(1, errors.size());

        ValidationMessage error = errors.get(0);
        assertNotNull(error);
        assertEquals(ValidationSet.VALIDATION_CODE_PATTERN, error.getCode());
        assertFalse(StringUtils.isBlank(error.getText()));
    }

    @Test
    public void nameInvalidB() throws Exception {

        User user = new UserDefault();
        user.setName("b");

        ValidationSet validation = validate(user);
        assertNotNull(validation);
        assertFalse(validation.isValid());

        List<ValidationMessage> errors = validation.getMessages().get("name");
        assertNotNull(errors);
        assertEquals(1, errors.size());

        ValidationMessage error = errors.get(0);
        assertNotNull(error);
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_PATTERN, error.getCode());
        assertFalse(StringUtils.isBlank(error.getText()));
    }

    @Test
    public void nameInvalidEmpty() throws Exception {

        User user = new UserDefault();
        user.setName("");

        ValidationSet validation = validate(user);
        assertNotNull(validation);
        assertFalse(validation.isValid());

        List<ValidationMessage> errors = validation.getMessages().get("name");
        assertNotNull(errors);
        assertEquals(1, errors.size());

        ValidationMessage error = errors.get(0);
        assertNotNull(error);
        assertEquals(ValidationSet.VALIDATION_CODE_PATTERN, error.getCode());
        assertFalse(StringUtils.isBlank(error.getText()));
    }

    @Test
    public void nameInvalidNull() throws Exception {

        User user = new UserDefault();
        user.setName(null);

        ValidationSet validation = validate(user);
        assertNotNull(validation);
        assertFalse(validation.isValid());

        List<ValidationMessage> errors = validation.getMessages().get("name");
        assertNotNull(errors);
        assertEquals(1, errors.size());

        ValidationMessage error = errors.get(0);
        assertNotNull(error);
        assertEquals(ValidationSet.VALIDATION_CODE_PATTERN, error.getCode());
        assertFalse(StringUtils.isBlank(error.getText()));
    }

    @Test
    public void patternOnArrayAllElementsValid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("123");
        array.add(1);
        array.add(new BigDecimal("12345"));

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet builder = obj.validationSet();
        assertNotNull(builder);

        ValidationSet validation = builder.validationPattern("[0-9]+")
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
    }

    @Test
    public void patternOnArrayAllElementsInvalid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(123);
        array.add("");
        array.add("abc");
        array.add(null);
        array.add("   ");
        array.add("5");

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet builder = obj.validationSet();
        assertNotNull(builder);

        ValidationSet validation = builder.validationPattern("[0-9]+")
                                          .jsonPathAll("key1")
                                          .failMessageText("element message")
                                          .arrayItselfAddFailMessage("_key1", "array message")
                                          .validate();
        assertNotNull(validation);
        assertTrue(validation.isError());

        List<ValidationMessage> messages = validation.getMessages("key1[0]");
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

        assertEquals(ValidationSet.VALIDATION_CODE_PATTERN, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[1]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_PATTERN, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[2]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_PATTERN, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[3]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_PATTERN, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[4]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_PATTERN, message.getCode());
        assertEquals("element message", message.getText());
    }

    @Test
    public void notPatternOnArrayAllElementsValid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add("123x");
        array.add(1.0F);
        array.add(null);
        array.add("");
        array.add(new BigDecimal("123.45"));

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet builder = obj.validationSet();
        assertNotNull(builder);

        ValidationSet validation = builder.validationNotPattern("[0-9]+")
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
        assertNotNull(validation);
        assertEquals(0, messages.size());
    }

    @Test
    public void notPatternOnArrayAllElementsInvalid() throws Exception {

        JsonArray array = getJsonManager().createArray();
        array.add(123);
        array.add("");
        array.add("abc");
        array.add(null);
        array.add("   ");
        array.add("5");

        JsonObject obj = getJsonManager().create();
        obj.put("key1", array);

        JsonObjectValidationSet builder = obj.validationSet();
        assertNotNull(builder);

        ValidationSet validation = builder.validationNotPattern("[0-9]+")
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

        assertEquals(ValidationSet.VALIDATION_CODE_NOT_PATTERN, message.getCode());
        assertEquals("array message", message.getText());

        messages = validation.getMessages("key1[0]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_PATTERN, message.getCode());
        assertEquals("element message", message.getText());

        messages = validation.getMessages("key1[5]");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.get(0);
        assertNotNull(message);
        assertEquals(ValidationSet.VALIDATION_CODE_NOT_PATTERN, message.getCode());
        assertEquals("element message", message.getText());
    }

}
