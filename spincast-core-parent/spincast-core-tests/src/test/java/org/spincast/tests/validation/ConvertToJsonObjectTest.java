package org.spincast.tests.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.validation.JsonObjectValidationSet;
import org.spincast.core.validation.ValidationLevel;
import org.spincast.core.validation.ValidationSet;
import org.spincast.defaults.testing.NoAppTestingBase;

import com.google.inject.Inject;

public class ConvertToJsonObjectTest extends NoAppTestingBase {

    @Inject
    private JsonManager jsonManager;

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    @Test
    public void conversionSuccessNoMessages() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("email", "test@example.com");

        JsonObjectValidationSet validationFinal = obj.validationSet();
        validationFinal.validationEmail().jsonPath("email").validate();

        JsonObject asJsonObj = validationFinal.convertToJsonObject();
        assertNotNull(asJsonObj);

        JsonObject emailField = asJsonObj.getJsonObject("email");
        assertNull(emailField);

        // The form itself
        JsonObject formItselfResult = asJsonObj.getJsonObject("_");
        // No messages on the form itself
        assertNull(formItselfResult.getObject("messages"));
        assertTrue(formItselfResult.getBoolean("isValid"));
        assertFalse(formItselfResult.getBoolean("hasErrors"));
        assertFalse(formItselfResult.getBoolean("hasWarnings"));
        assertFalse(formItselfResult.getBoolean("hasSuccesses"));
    }

    @Test
    public void conversionSuccessMessages() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("email", "test@example.com");

        JsonObjectValidationSet validationFinal = obj.validationSet();
        validationFinal.validationEmail()
                       .jsonPath("email")
                       .addMessageOnSuccess("success!")
                       .validate();

        JsonObject asJsonObj = validationFinal.convertToJsonObject();
        assertNotNull(asJsonObj);

        JsonObject emailField = asJsonObj.getJsonObject("email");
        assertNotNull(emailField);

        assertTrue(emailField.getBoolean("isValid"));
        assertFalse(emailField.getBoolean("hasErrors"));
        assertFalse(emailField.getBoolean("hasWarnings"));
        assertTrue(emailField.getBoolean("hasSuccesses"));

        JsonArray messages = emailField.getJsonArray("messages");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        JsonObject message = messages.getJsonObject(0);
        assertNotNull(message);
        assertEquals(ValidationLevel.SUCCESS.toString(), message.getString("level"));
        assertEquals(ValidationSet.VALIDATION_CODE_EMAIL, message.getString("code"));
        assertEquals("success!", message.getString("text"));

        // The form itself
        JsonObject formItselfResult = asJsonObj.getJsonObject("_");
        // No messages on the form itself
        assertNull(formItselfResult.getObject("messages"));
        assertTrue(formItselfResult.getBoolean("isValid"));
        assertFalse(formItselfResult.getBoolean("hasErrors"));
        assertFalse(formItselfResult.getBoolean("hasWarnings"));
        assertTrue(formItselfResult.getBoolean("hasSuccesses"));
    }

    @Test
    public void conversionError() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("email", "nope");

        JsonObjectValidationSet validationFinal = obj.validationSet();
        validationFinal.validationEmail()
                       .jsonPath("email")
                       .failMessageText("fail!")
                       .validate();

        JsonObject asJsonObj = validationFinal.convertToJsonObject();
        assertNotNull(asJsonObj);

        JsonObject emailField = asJsonObj.getJsonObject("email");
        assertNotNull(emailField);

        assertFalse(emailField.getBoolean("isValid"));
        assertTrue(emailField.getBoolean("hasErrors"));
        assertFalse(emailField.getBoolean("hasWarnings"));
        assertFalse(emailField.getBoolean("hasSuccesses"));

        JsonArray messages = emailField.getJsonArray("messages");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        JsonObject message = messages.getJsonObject(0);
        assertNotNull(message);
        assertEquals(ValidationLevel.ERROR.toString(), message.getString("level"));
        assertEquals(ValidationSet.VALIDATION_CODE_EMAIL, message.getString("code"));
        assertEquals("fail!", message.getString("text"));

        // The form itself
        JsonObject formItselfResult = asJsonObj.getJsonObject("_");
        // No messages on the form itself
        assertNull(formItselfResult.getObject("messages"));
        assertFalse(formItselfResult.getBoolean("isValid"));
        assertTrue(formItselfResult.getBoolean("hasErrors"));
        assertFalse(formItselfResult.getBoolean("hasWarnings"));
        assertFalse(formItselfResult.getBoolean("hasSuccesses"));
    }

    @Test
    public void conversionWarnings() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("email", "warning-only");

        JsonObjectValidationSet validationFinal = obj.validationSet();
        validationFinal.validationEmail()
                       .jsonPath("email")
                       .failMessageText("fail!")
                       .treatErrorAsWarning()
                       .validate();

        JsonObject asJsonObj = validationFinal.convertToJsonObject();
        assertNotNull(asJsonObj);

        JsonObject emailField = asJsonObj.getJsonObject("email");
        assertNotNull(emailField);

        assertTrue(emailField.getBoolean("isValid"));
        assertFalse(emailField.getBoolean("hasErrors"));
        assertTrue(emailField.getBoolean("hasWarnings"));
        assertFalse(emailField.getBoolean("hasSuccesses"));

        JsonArray messages = emailField.getJsonArray("messages");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        JsonObject message = messages.getJsonObject(0);
        assertNotNull(message);
        assertEquals(ValidationLevel.WARNING.toString(), message.getString("level"));
        assertEquals(ValidationSet.VALIDATION_CODE_EMAIL, message.getString("code"));
        assertEquals("fail!", message.getString("text"));

        // The form itself
        JsonObject formItselfResult = asJsonObj.getJsonObject("_");
        // No messages on the form itself
        assertNull(formItselfResult.getObject("messages"));
        assertTrue(formItselfResult.getBoolean("isValid"));
        assertFalse(formItselfResult.getBoolean("hasErrors"));
        assertTrue(formItselfResult.getBoolean("hasWarnings"));
        assertFalse(formItselfResult.getBoolean("hasSuccesses"));
    }

    @Test
    public void conversionMixed() throws Exception {

        JsonObject obj = getJsonManager().create();
        obj.put("email1", "test@example.com");
        obj.put("email2", "nope");
        obj.put("email3", "nope");
        obj.put("email4", "test@example.com");

        JsonObjectValidationSet validationFinal = obj.validationSet();

        validationFinal.validationEmail()
                       .jsonPath("email1")
                       .addMessageOnSuccess("success!")
                       .failMessageText("fail!")
                       .validate();

        validationFinal.validationEmail()
                       .jsonPath("email2")
                       .addMessageOnSuccess("success!")
                       .failMessageText("fail!")
                       .validate();

        validationFinal.validationEmail()
                       .jsonPath("email3")
                       .addMessageOnSuccess("success!")
                       .failMessageText("warn!")
                       .treatErrorAsWarning()
                       .validate();

        validationFinal.validationEmail()
                       .jsonPath("email4")
                       .validate();

        JsonObject asJsonObj = validationFinal.convertToJsonObject();
        assertNotNull(asJsonObj);

        JsonObject emailField = asJsonObj.getJsonObject("email1");
        assertNotNull(emailField);

        assertTrue(emailField.getBoolean("isValid"));
        assertFalse(emailField.getBoolean("hasErrors"));
        assertFalse(emailField.getBoolean("hasWarnings"));
        assertTrue(emailField.getBoolean("hasSuccesses"));

        JsonArray messages = emailField.getJsonArray("messages");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        JsonObject message = messages.getJsonObject(0);
        assertNotNull(message);
        assertEquals(ValidationLevel.SUCCESS.toString(), message.getString("level"));
        assertEquals(ValidationSet.VALIDATION_CODE_EMAIL, message.getString("code"));
        assertEquals("success!", message.getString("text"));

        emailField = asJsonObj.getJsonObject("email2");
        assertNotNull(emailField);

        assertFalse(emailField.getBoolean("isValid"));
        assertTrue(emailField.getBoolean("hasErrors"));
        assertFalse(emailField.getBoolean("hasWarnings"));
        assertFalse(emailField.getBoolean("hasSuccesses"));

        messages = emailField.getJsonArray("messages");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.getJsonObject(0);
        assertNotNull(message);
        assertEquals(ValidationLevel.ERROR.toString(), message.getString("level"));
        assertEquals(ValidationSet.VALIDATION_CODE_EMAIL, message.getString("code"));
        assertEquals("fail!", message.getString("text"));

        emailField = asJsonObj.getJsonObject("email3");
        assertNotNull(emailField);

        assertTrue(emailField.getBoolean("isValid"));
        assertFalse(emailField.getBoolean("hasErrors"));
        assertTrue(emailField.getBoolean("hasWarnings"));
        assertFalse(emailField.getBoolean("hasSuccesses"));

        messages = emailField.getJsonArray("messages");
        assertNotNull(messages);
        assertEquals(1, messages.size());

        message = messages.getJsonObject(0);
        assertNotNull(message);
        assertEquals(ValidationLevel.WARNING.toString(), message.getString("level"));
        assertEquals(ValidationSet.VALIDATION_CODE_EMAIL, message.getString("code"));
        assertEquals("warn!", message.getString("text"));

        // No Message for field "email4"...
        emailField = asJsonObj.getJsonObject("email4");
        assertNull(emailField);

        // The form itself
        JsonObject formItselfResult = asJsonObj.getJsonObject("_");
        // No messages on the form itself
        assertNull(formItselfResult.getObject("messages"));
        assertFalse(formItselfResult.getBoolean("isValid"));
        assertTrue(formItselfResult.getBoolean("hasErrors"));
        assertTrue(formItselfResult.getBoolean("hasWarnings"));
        assertTrue(formItselfResult.getBoolean("hasSuccesses"));
    }

}
