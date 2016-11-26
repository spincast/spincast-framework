package org.spincast.tests.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.json.JsonObject;
import org.spincast.core.validation.ValidationMessageFormatType;
import org.spincast.core.validation.ValidationSet;
import org.spincast.core.xml.XmlManager;
import org.spincast.tests.validation.utils.User;
import org.spincast.tests.validation.utils.UserDefault;

import com.google.inject.Inject;

public class GetFormattedErrorsTest extends ValidationTestBase {

    public static final String APP_VALIDATION_ERROR_NO_SPACE = "VALIDATION_ERROR_APP_NO_SPACE";

    @Inject
    protected XmlManager xmlManager;

    protected XmlManager getXmlManager() {
        return this.xmlManager;
    }

    protected ValidationSet validate(User user) {

        ValidationSet validation = getValidationFactory().createValidationSet();

        // Name: no space in it
        if(user.getName().indexOf(" ") > -1) {
            validation.addError("name", APP_VALIDATION_ERROR_NO_SPACE, "No space allowed!");
        }

        // Name: min 3 characters
        validation.validationMinLength(3).key("name").element(user.getName()).validate();

        // Email valid
        validation.validationEmail().key("email").element(user.getEmail()).validate();

        return validation;
    }

    @Test
    public void getFormattedErrorsPlainText() throws Exception {

        User user = new UserDefault();
        user.setName("S ");
        user.setEmail("nope");

        ValidationSet validationResult = validate(user);
        assertNotNull(validationResult);

        String errorsFormatted = validationResult.getMessagesFormatted(ValidationMessageFormatType.PLAIN_TEXT);
        assertNotNull(errorsFormatted);

        StringBuilder expected = new StringBuilder();
        expected.append("key \"email\"\n");
        expected.append("    - Invalid email address\n");
        expected.append("\n");
        expected.append("key \"name\"\n");
        expected.append("    - No space allowed!\n");
        expected.append("    - Minimum length of 3 characters (currently 2)\n");
        expected.append("\n");

        StringBuilder expected2 = new StringBuilder();
        expected2.append("key \"name\"\n");
        expected2.append("    - No space allowed!\n");
        expected2.append("    - Minimum length of 3 characters (currently 2)\n");
        expected2.append("\n");
        expected2.append("key \"email\"\n");
        expected2.append("    - Invalid email address\n");
        expected2.append("\n");

        assertTrue(expected.toString().equals(errorsFormatted) || expected2.toString().equals(errorsFormatted));
    }

    @Test
    public void getFormattedErrorsHtml() throws Exception {

        User user = new UserDefault();
        user.setName("S ");
        user.setEmail("nope");

        ValidationSet validationResult = validate(user);
        assertNotNull(validationResult);

        String errorsFormatted = validationResult.getMessagesFormatted(ValidationMessageFormatType.HTML);
        assertNotNull(errorsFormatted);

        StringBuilder expected = new StringBuilder();
        expected.append("<li class=\"validationKey\">email\n");
        expected.append("    <ul>\n");
        expected.append("        <li class=\"validationError\">Invalid email address</li>\n");
        expected.append("    </ul>\n");
        expected.append("</li>\n");
        expected.append("<li class=\"validationKey\">name\n");
        expected.append("    <ul>\n");
        expected.append("        <li class=\"validationError\">No space allowed!</li>\n");
        expected.append("        <li class=\"validationError\">Minimum length of 3 characters (currently 2)</li>\n");
        expected.append("    </ul>\n");
        expected.append("</li>\n");

        StringBuilder expected2 = new StringBuilder();
        expected2.append("<li class=\"validationKey\">name\n");
        expected2.append("    <ul>\n");
        expected2.append("        <li class=\"validationError\">No space allowed!</li>\n");
        expected2.append("        <li class=\"validationError\">Minimum length of 3 characters (currently 2)</li>\n");
        expected2.append("    </ul>\n");
        expected2.append("</li>\n");
        expected2.append("<li class=\"validationKey\">email\n");
        expected2.append("    <ul>\n");
        expected2.append("        <li class=\"validationError\">Invalid email address</li>\n");
        expected2.append("    </ul>\n");
        expected2.append("</li>\n");

        System.out.println(expected2);

        assertTrue(expected.toString().equals(errorsFormatted) || expected2.toString().equals(errorsFormatted));
    }

    @Test
    public void getFormattedErrorsJson() throws Exception {

        User user = new UserDefault();
        user.setName("S ");
        user.setEmail("nope");

        ValidationSet validationResult = validate(user);
        assertNotNull(validationResult);

        String errorsFormatted = validationResult.getMessagesFormatted(ValidationMessageFormatType.JSON);
        assertNotNull(errorsFormatted);

        StringBuilder expected = new StringBuilder();
        expected.append("{\n");
        expected.append("    \"email\" : [\n");
        expected.append("        {\n");
        expected.append("            \"text\" : \"Invalid email address\",\n");
        expected.append("            \"code\" : \"VALIDATION_TYPE_EMAIL\",\n");
        expected.append("            \"level\" : \"ERROR\"\n");
        expected.append("        }\n");
        expected.append("    ],\n");
        expected.append("    \"name\" : [\n");
        expected.append("        {\n");
        expected.append("            \"text\" : \"No space allowed!\",\n");
        expected.append("            \"code\" : \"VALIDATION_ERROR_APP_NO_SPACE\",\n");
        expected.append("            \"level\" : \"ERROR\"\n");
        expected.append("        },\n");
        expected.append("        {\n");
        expected.append("            \"text\" : \"Minimum length of 3 characters (currently 2)\",\n");
        expected.append("            \"code\" : \"VALIDATION_TYPE_MIN_LENGTH\",\n");
        expected.append("            \"level\" : \"ERROR\"\n");
        expected.append("        }\n");
        expected.append("    ]\n");
        expected.append("}");

        //==========================================
        // Fields can be in differernt orders, so we only
        // compare the length.
        //==========================================
        assertEquals(expected.toString().length(), errorsFormatted.length());
    }

    @Test
    public void getFormattedErrorsXml() throws Exception {

        User user = new UserDefault();
        user.setName("S ");
        user.setEmail("nope");

        ValidationSet validationResult = validate(user);
        assertNotNull(validationResult);

        String errorsFormatted = validationResult.getMessagesFormatted(ValidationMessageFormatType.XML);
        assertNotNull(errorsFormatted);

        JsonObject jsonObject = getXmlManager().fromXml(errorsFormatted);
        assertNotNull(jsonObject);
        JsonObject emailObj = jsonObject.getArrayFirstJsonObject("email");
        assertNotNull(emailObj);
        assertEquals("Invalid email address", emailObj.getString("text"));
        assertEquals("VALIDATION_TYPE_EMAIL", emailObj.getString("code"));
    }

}
