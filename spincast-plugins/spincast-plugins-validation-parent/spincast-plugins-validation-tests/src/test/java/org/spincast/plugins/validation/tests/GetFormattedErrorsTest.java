package org.spincast.plugins.validation.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.defaults.tests.DefaultTestingModule;
import org.spincast.plugins.validation.FormatType;
import org.spincast.plugins.validation.IValidator;
import org.spincast.plugins.validation.IValidatorFactory;
import org.spincast.plugins.validation.SpincastValidationPluginGuiceModule;
import org.spincast.plugins.validation.SpincastValidatorBase;
import org.spincast.plugins.validation.SpincastValidatorBaseDeps;
import org.spincast.plugins.validation.tests.utils.IUser;
import org.spincast.plugins.validation.tests.utils.User;
import org.spincast.testing.core.SpincastGuiceModuleBasedTestBase;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class GetFormattedErrorsTest extends SpincastGuiceModuleBasedTestBase {

    public static final String APP_VALIDATION_ERROR_NO_SPACE = "VALIDATION_ERROR_APP_NO_SPACE";

    @Inject
    protected IValidatorFactory<IUser> userValidatorFactory;

    public static class UserValidator extends SpincastValidatorBase<IUser> {

        @AssistedInject
        public UserValidator(@Assisted IUser user,
                             SpincastValidatorBaseDeps spincastValidatorBaseDeps) {
            super(user, spincastValidatorBaseDeps);
        }

        @Override
        protected void validate() {

            // Name: no space in it
            if(getObjToValidate().getName().indexOf(" ") > -1) {
                addError("name", APP_VALIDATION_ERROR_NO_SPACE, "No space allowed!");
            }

            // Name: min 3 characters
            validateMinLength("name", getObjToValidate().getName(), 3);

            // Email valid
            validateEmail("email", getObjToValidate().getEmail());
        }
    }

    @Override
    public Module getTestingModule() {
        return new DefaultTestingModule() {

            @Override
            protected void configure() {
                super.configure();

                install(new SpincastValidationPluginGuiceModule(getRequestContextType()));

                //==========================================
                // Binds a user validator factory
                //==========================================
                install(new FactoryModuleBuilder().implement(IValidator.class, UserValidator.class)
                                                  .build(new TypeLiteral<IValidatorFactory<IUser>>() {}));
            }
        };
    }

    protected IValidatorFactory<IUser> getUserValidatorFactory() {
        return this.userValidatorFactory;
    }

    @Test
    public void getFormattedErrorsPlainText() throws Exception {

        IUser user = new User();
        user.setName("S ");
        user.setEmail("nope");

        IValidator userValidator = getUserValidatorFactory().create(user);
        assertFalse(userValidator.isValid());

        String errorsFormatted = userValidator.getErrorsFormatted(FormatType.PLAIN_TEXT);
        assertNotNull(errorsFormatted);

        StringBuilder expected = new StringBuilder();
        expected.append("Field \"email\"\n");
        expected.append("    - Not a valid email address.\n");
        expected.append("\n");
        expected.append("Field \"name\"\n");
        expected.append("    - No space allowed!\n");
        expected.append("    - Minimum length of 3 characters. Currently: 2 characters.\n");
        expected.append("\n");

        StringBuilder expected2 = new StringBuilder();
        expected2.append("Field \"name\"\n");
        expected2.append("    - No space allowed!\n");
        expected2.append("    - Minimum length of 3 characters. Currently: 2 characters.\n");
        expected2.append("\n");
        expected2.append("Field \"email\"\n");
        expected2.append("    - Not a valid email address.\n");
        expected2.append("\n");

        assertTrue(expected.toString().equals(errorsFormatted) || expected2.toString().equals(errorsFormatted));

    }

    @Test
    public void getFormattedErrorsHtml() throws Exception {

        IUser user = new User();
        user.setName("S ");
        user.setEmail("nope");

        IValidator userValidator = getUserValidatorFactory().create(user);
        assertFalse(userValidator.isValid());

        String errorsFormatted = userValidator.getErrorsFormatted(FormatType.HTML);
        assertNotNull(errorsFormatted);

        StringBuilder expected = new StringBuilder();
        expected.append("<li class=\"validationField\">email\n");
        expected.append("    <ul>\n");
        expected.append("        <li class=\"validationError\">Not a valid email address.</li>\n");
        expected.append("    </ul>\n");
        expected.append("</li>\n");
        expected.append("<li class=\"validationField\">name\n");
        expected.append("    <ul>\n");
        expected.append("        <li class=\"validationError\">No space allowed!</li>\n");
        expected.append("        <li class=\"validationError\">Minimum length of 3 characters. Currently: 2 characters.</li>\n");
        expected.append("    </ul>\n");
        expected.append("</li>\n");

        StringBuilder expected2 = new StringBuilder();
        expected2.append("<li class=\"validationField\">name\n");
        expected2.append("    <ul>\n");
        expected2.append("        <li class=\"validationError\">No space allowed!</li>\n");
        expected2.append("        <li class=\"validationError\">Minimum length of 3 characters. Currently: 2 characters.</li>\n");
        expected2.append("    </ul>\n");
        expected2.append("</li>\n");
        expected2.append("<li class=\"validationField\">email\n");
        expected2.append("    <ul>\n");
        expected2.append("        <li class=\"validationError\">Not a valid email address.</li>\n");
        expected2.append("    </ul>\n");
        expected2.append("</li>\n");

        assertTrue(expected.toString().equals(errorsFormatted) || expected2.toString().equals(errorsFormatted));
    }

    @Test
    public void getFormattedErrorsJson() throws Exception {

        IUser user = new User();
        user.setName("S ");
        user.setEmail("nope");

        IValidator userValidator = getUserValidatorFactory().create(user);
        assertFalse(userValidator.isValid());

        String errorsFormatted = userValidator.getErrorsFormatted(FormatType.JSON);
        assertNotNull(errorsFormatted);

        StringBuilder expected = new StringBuilder();
        expected.append("{\n");
        expected.append("    \"email\" : [\n");
        expected.append("        {\n");
        expected.append("            \"fieldName\" : \"email\",\n");
        expected.append("            \"message\" : \"Not a valid email address.\",\n");
        expected.append("            \"type\" : \"VALIDATION_TYPE_EMAIL\"\n");
        expected.append("        }\n");
        expected.append("    ],\n");
        expected.append("    \"name\" : [\n");
        expected.append("        {\n");
        expected.append("            \"fieldName\" : \"name\",\n");
        expected.append("            \"message\" : \"No space allowed!\",\n");
        expected.append("            \"type\" : \"VALIDATION_ERROR_APP_NO_SPACE\"\n");
        expected.append("        },\n");
        expected.append("        {\n");
        expected.append("            \"fieldName\" : \"name\",\n");
        expected.append("            \"message\" : \"Minimum length of 3 characters. Currently: 2 characters.\",\n");
        expected.append("            \"type\" : \"VALIDATION_TYPE_MIN_LENGTH\"\n");
        expected.append("        }\n");
        expected.append("    ]\n");
        expected.append("}");

        StringBuilder expected2 = new StringBuilder();
        expected2.append("{\n");
        expected2.append("    \"name\" : [\n");
        expected2.append("        {\n");
        expected2.append("            \"fieldName\" : \"name\",\n");
        expected2.append("            \"message\" : \"No space allowed!\",\n");
        expected2.append("            \"type\" : \"VALIDATION_ERROR_APP_NO_SPACE\"\n");
        expected2.append("        },\n");
        expected2.append("        {\n");
        expected2.append("            \"fieldName\" : \"name\",\n");
        expected2.append("            \"message\" : \"Minimum length of 3 characters. Currently: 2 characters.\",\n");
        expected2.append("            \"type\" : \"VALIDATION_TYPE_MIN_LENGTH\"\n");
        expected2.append("        }\n");
        expected2.append("    ],\n");
        expected2.append("    \"email\" : [\n");
        expected2.append("        {\n");
        expected2.append("            \"fieldName\" : \"email\",\n");
        expected2.append("            \"message\" : \"Not a valid email address.\",\n");
        expected2.append("            \"type\" : \"VALIDATION_TYPE_EMAIL\"\n");
        expected2.append("        }\n");
        expected2.append("    ]\n");
        expected2.append("}");

        StringBuilder expected3 = new StringBuilder();
        expected3.append("{\n");
        expected3.append("    \"email\" : [\n");
        expected3.append("        {\n");
        expected3.append("            \"fieldName\" : \"email\",\n");
        expected3.append("            \"message\" : \"Not a valid email address.\",\n");
        expected3.append("            \"type\" : \"VALIDATION_TYPE_EMAIL\"\n");
        expected3.append("        }\n");
        expected3.append("    ],\n");
        expected3.append("    \"name\" : [\n");
        expected3.append("        {\n");
        expected3.append("            \"fieldName\" : \"name\",\n");
        expected3.append("            \"message\" : \"Minimum length of 3 characters. Currently: 2 characters.\",\n");
        expected3.append("            \"type\" : \"VALIDATION_TYPE_MIN_LENGTH\"\n");
        expected3.append("        },\n");
        expected3.append("        {\n");
        expected3.append("            \"fieldName\" : \"name\",\n");
        expected3.append("            \"message\" : \"No space allowed!\",\n");
        expected3.append("            \"type\" : \"VALIDATION_ERROR_APP_NO_SPACE\"\n");
        expected3.append("        }\n");
        expected3.append("    ]\n");
        expected3.append("}");

        StringBuilder expected4 = new StringBuilder();
        expected4.append("{\n");
        expected4.append("    \"name\" : [\n");
        expected4.append("        {\n");
        expected4.append("            \"fieldName\" : \"name\",\n");
        expected4.append("            \"message\" : \"Minimum length of 3 characters. Currently: 2 characters.\",\n");
        expected4.append("            \"type\" : \"VALIDATION_TYPE_MIN_LENGTH\"\n");
        expected4.append("        },\n");
        expected4.append("        {\n");
        expected4.append("            \"fieldName\" : \"name\",\n");
        expected4.append("            \"message\" : \"No space allowed!\",\n");
        expected4.append("            \"type\" : \"VALIDATION_ERROR_APP_NO_SPACE\"\n");
        expected4.append("        },\n");
        expected4.append("    ]\n");
        expected4.append("    \"email\" : [\n");
        expected4.append("        {\n");
        expected4.append("            \"fieldName\" : \"email\",\n");
        expected4.append("            \"message\" : \"Not a valid email address.\",\n");
        expected4.append("            \"type\" : \"VALIDATION_TYPE_EMAIL\"\n");
        expected4.append("        }\n");
        expected4.append("    ]\n");
        expected4.append("}");

        assertTrue(expected.toString().equals(errorsFormatted) ||
                   expected2.toString().equals(errorsFormatted) ||
                   expected3.toString().equals(errorsFormatted) ||
                   expected4.toString().equals(errorsFormatted));

    }

    @Test
    public void getFormattedErrorsXml() throws Exception {

        IUser user = new User();
        user.setName("S ");
        user.setEmail("nope");

        IValidator userValidator = getUserValidatorFactory().create(user);
        assertFalse(userValidator.isValid());

        String errorsFormatted = userValidator.getErrorsFormatted(FormatType.XML);
        assertNotNull(errorsFormatted);

        StringBuilder expected = new StringBuilder();
        expected.append("<JsonObject>\n");
        expected.append("    <email>\n");
        expected.append("        <item>\n");
        expected.append("            <fieldName>email</fieldName>\n");
        expected.append("            <message>Not a valid email address.</message>\n");
        expected.append("            <type>VALIDATION_TYPE_EMAIL</type>\n");
        expected.append("        </item>\n");
        expected.append("    </email>\n");
        expected.append("    <name>\n");
        expected.append("        <item>\n");
        expected.append("            <fieldName>name</fieldName>\n");
        expected.append("            <message>No space allowed!</message>\n");
        expected.append("            <type>VALIDATION_ERROR_APP_NO_SPACE</type>\n");
        expected.append("        </item>\n");
        expected.append("        <item>\n");
        expected.append("            <fieldName>name</fieldName>\n");
        expected.append("            <message>Minimum length of 3 characters. Currently: 2 characters.</message>\n");
        expected.append("            <type>VALIDATION_TYPE_MIN_LENGTH</type>\n");
        expected.append("        </item>\n");
        expected.append("    </name>\n");
        expected.append("</JsonObject>");

        StringBuilder expected2 = new StringBuilder();
        expected2.append("<JsonObject>\n");
        expected2.append("    <name>\n");
        expected2.append("        <item>\n");
        expected2.append("            <fieldName>name</fieldName>\n");
        expected2.append("            <message>No space allowed!</message>\n");
        expected2.append("            <type>VALIDATION_ERROR_APP_NO_SPACE</type>\n");
        expected2.append("        </item>\n");
        expected2.append("        <item>\n");
        expected2.append("            <fieldName>name</fieldName>\n");
        expected2.append("            <message>Minimum length of 3 characters. Currently: 2 characters.</message>\n");
        expected2.append("            <type>VALIDATION_TYPE_MIN_LENGTH</type>\n");
        expected2.append("        </item>\n");
        expected2.append("    </name>\n");
        expected2.append("    <email>\n");
        expected2.append("        <item>\n");
        expected2.append("            <fieldName>email</fieldName>\n");
        expected2.append("            <message>Not a valid email address.</message>\n");
        expected2.append("            <type>VALIDATION_TYPE_EMAIL</type>\n");
        expected2.append("        </item>\n");
        expected2.append("    </email>\n");
        expected2.append("</JsonObject>");

        StringBuilder expected3 = new StringBuilder();
        expected3.append("<JsonObject>\n");
        expected3.append("    <email>\n");
        expected3.append("        <item>\n");
        expected3.append("            <fieldName>email</fieldName>\n");
        expected3.append("            <message>Not a valid email address.</message>\n");
        expected3.append("            <type>VALIDATION_TYPE_EMAIL</type>\n");
        expected3.append("        </item>\n");
        expected3.append("    </email>\n");
        expected3.append("    <name>\n");
        expected3.append("        <item>\n");
        expected3.append("            <fieldName>name</fieldName>\n");
        expected3.append("            <message>Minimum length of 3 characters. Currently: 2 characters.</message>\n");
        expected3.append("            <type>VALIDATION_TYPE_MIN_LENGTH</type>\n");
        expected3.append("        </item>\n");
        expected3.append("        <item>\n");
        expected3.append("            <fieldName>name</fieldName>\n");
        expected3.append("            <message>No space allowed!</message>\n");
        expected3.append("            <type>VALIDATION_ERROR_APP_NO_SPACE</type>\n");
        expected3.append("        </item>\n");
        expected3.append("    </name>\n");
        expected3.append("</JsonObject>");

        StringBuilder expected4 = new StringBuilder();
        expected4.append("<JsonObject>\n");
        expected4.append("    <name>\n");
        expected4.append("        <item>\n");
        expected4.append("            <fieldName>name</fieldName>\n");
        expected4.append("            <message>Minimum length of 3 characters. Currently: 2 characters.</message>\n");
        expected4.append("            <type>VALIDATION_TYPE_MIN_LENGTH</type>\n");
        expected4.append("        </item>\n");
        expected4.append("        <item>\n");
        expected4.append("            <fieldName>name</fieldName>\n");
        expected4.append("            <message>No space allowed!</message>\n");
        expected4.append("            <type>VALIDATION_ERROR_APP_NO_SPACE</type>\n");
        expected4.append("        </item>\n");
        expected4.append("    </name>\n");
        expected4.append("    <email>\n");
        expected4.append("        <item>\n");
        expected4.append("            <fieldName>email</fieldName>\n");
        expected4.append("            <message>Not a valid email address.</message>\n");
        expected4.append("            <type>VALIDATION_TYPE_EMAIL</type>\n");
        expected4.append("        </item>\n");
        expected4.append("    </email>\n");
        expected4.append("</JsonObject>");

        assertTrue(expected.toString().equals(errorsFormatted) ||
                   expected2.toString().equals(errorsFormatted) ||
                   expected3.toString().equals(errorsFormatted) ||
                   expected4.toString().equals(errorsFormatted));

    }

}
