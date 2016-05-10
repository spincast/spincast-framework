package org.spincast.plugins.validation.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.xml.IXmlManager;
import org.spincast.defaults.tests.DefaultTestingModule;
import org.spincast.plugins.validation.ISpincastValidationConfig;
import org.spincast.plugins.validation.IValidationError;
import org.spincast.plugins.validation.IValidationErrorFactory;
import org.spincast.plugins.validation.IValidator;
import org.spincast.plugins.validation.IValidatorFactory;
import org.spincast.plugins.validation.SpincastValidationPluginGuiceModule;
import org.spincast.plugins.validation.SpincastValidatorBase;
import org.spincast.plugins.validation.tests.utils.IUser;
import org.spincast.plugins.validation.tests.utils.User;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.testing.core.SpincastGuiceModuleBasedTestBase;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class PatternValidationTest extends SpincastGuiceModuleBasedTestBase {

    @Inject
    protected IValidatorFactory<IUser> userValidatorFactory;

    public static class UserValidator extends SpincastValidatorBase<IUser> {

        @AssistedInject
        public UserValidator(@Assisted IUser user,
                             IValidationErrorFactory validationErrorFactory,
                             ISpincastValidationConfig spincastBeanValidationConfig,
                             IJsonManager jsonManager,
                             IXmlManager xmlManager) {
            super(user,
                  validationErrorFactory,
                  spincastBeanValidationConfig,
                  jsonManager,
                  xmlManager);
        }

        @Override
        protected void validate() {

            // Name can only contain "a" and "b".
            boolean nameValid = validatePattern("name", getObjToValidate().getName(), "[ab]+");

            // ... but not be simply "b"
            if(nameValid) {
                nameValid = validatePattern("name", getObjToValidate().getName(), "b", false);
            }
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
    public void nameValid1() throws Exception {

        IUser user = new User();
        user.setName("ababababab");

        IValidator userValidator = getUserValidatorFactory().create(user);
        assertNotNull(userValidator);

        assertTrue(userValidator.isValid());
        assertNotNull(userValidator.getErrors());
        assertEquals(0, userValidator.getErrors().size());
    }

    @Test
    public void nameValid2() throws Exception {

        IUser user = new User();
        user.setName("a");

        IValidator userValidator = getUserValidatorFactory().create(user);
        assertNotNull(userValidator);

        assertTrue(userValidator.isValid());
        assertNotNull(userValidator.getErrors());
        assertEquals(0, userValidator.getErrors().size());
    }

    @Test
    public void nameInvalid1() throws Exception {

        IUser user = new User();
        user.setName("abc");

        IValidator userValidator = getUserValidatorFactory().create(user);
        assertNotNull(userValidator);
        assertFalse(userValidator.isValid());

        List<IValidationError> errors = userValidator.getErrors().get("name");
        assertNotNull(errors);
        assertEquals(1, errors.size());

        IValidationError error = errors.get(0);
        assertNotNull(error);
        assertEquals(IValidationError.VALIDATION_TYPE_PATTERN, error.getType());
        assertNotNull(error.getFieldName());
        assertEquals("name", error.getFieldName());
        assertFalse(StringUtils.isBlank(error.getMessage()));
    }

    @Test
    public void nameInvalidB() throws Exception {

        IUser user = new User();
        user.setName("b");

        IValidator userValidator = getUserValidatorFactory().create(user);
        assertNotNull(userValidator);
        assertFalse(userValidator.isValid());

        List<IValidationError> errors = userValidator.getErrors().get("name");
        assertNotNull(errors);
        assertEquals(1, errors.size());

        IValidationError error = errors.get(0);
        assertNotNull(error);
        assertEquals(IValidationError.VALIDATION_TYPE_PATTERN, error.getType());
        assertNotNull(error.getFieldName());
        assertEquals("name", error.getFieldName());
        assertFalse(StringUtils.isBlank(error.getMessage()));
    }

    @Test
    public void nameInvalidEmpty() throws Exception {

        IUser user = new User();
        user.setName("");

        IValidator userValidator = getUserValidatorFactory().create(user);
        assertNotNull(userValidator);
        assertFalse(userValidator.isValid());

        List<IValidationError> errors = userValidator.getErrors().get("name");
        assertNotNull(errors);
        assertEquals(1, errors.size());

        IValidationError error = errors.get(0);
        assertNotNull(error);
        assertEquals(IValidationError.VALIDATION_TYPE_PATTERN, error.getType());
        assertNotNull(error.getFieldName());
        assertEquals("name", error.getFieldName());
        assertFalse(StringUtils.isBlank(error.getMessage()));
    }

    @Test
    public void nameInvalidNull() throws Exception {

        IUser user = new User();
        user.setName(null);

        IValidator userValidator = getUserValidatorFactory().create(user);
        assertNotNull(userValidator);
        assertFalse(userValidator.isValid());

        List<IValidationError> errors = userValidator.getErrors().get("name");
        assertNotNull(errors);
        assertEquals(1, errors.size());

        IValidationError error = errors.get(0);
        assertNotNull(error);
        assertEquals(IValidationError.VALIDATION_TYPE_PATTERN, error.getType());
        assertNotNull(error.getFieldName());
        assertEquals("name", error.getFieldName());
        assertFalse(StringUtils.isBlank(error.getMessage()));
    }

}
