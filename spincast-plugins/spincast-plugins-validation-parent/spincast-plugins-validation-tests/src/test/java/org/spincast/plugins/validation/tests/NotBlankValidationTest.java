package org.spincast.plugins.validation.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.spincast.defaults.tests.DefaultTestingModule;
import org.spincast.plugins.validation.IValidationError;
import org.spincast.plugins.validation.IValidator;
import org.spincast.plugins.validation.IValidatorFactory;
import org.spincast.plugins.validation.SpincastValidationPluginGuiceModule;
import org.spincast.plugins.validation.SpincastValidatorBase;
import org.spincast.plugins.validation.SpincastValidatorBaseDeps;
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

public class NotBlankValidationTest extends SpincastGuiceModuleBasedTestBase {

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

            // Name not blank
            validateNotBlank("name", getObjToValidate().getName());
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
    public void nameNull() throws Exception {

        IUser user = new User();
        user.setName(null);
        user.setTitle("alien");

        IValidator userValidator = getUserValidatorFactory().create(user);

        assertFalse(userValidator.isValid());
        assertNotNull(userValidator.getErrors());
        assertEquals(1, userValidator.getErrors().size());

        List<IValidationError> errors = userValidator.getErrors().get("name");
        assertNotNull(userValidator.getErrors());
        assertEquals(1, userValidator.getErrors().size());

        IValidationError error = errors.get(0);
        assertNotNull(error);
        assertEquals(IValidationError.VALIDATION_TYPE_NOT_BLANK, error.getType());
        assertNotNull(error.getFieldName());
        assertEquals("name", error.getFieldName());
        assertFalse(StringUtils.isBlank(error.getMessage()));
    }

    @Test
    public void nameEmpty() throws Exception {

        IUser user = new User();
        user.setName(null);
        user.setTitle("");

        IValidator userValidator = getUserValidatorFactory().create(user);

        assertFalse(userValidator.isValid());
        assertNotNull(userValidator.getErrors());
        assertEquals(1, userValidator.getErrors().size());

        List<IValidationError> errors = userValidator.getErrors().get("name");
        assertNotNull(userValidator.getErrors());
        assertEquals(1, userValidator.getErrors().size());

        IValidationError error = errors.get(0);
        assertNotNull(error);
        assertEquals(IValidationError.VALIDATION_TYPE_NOT_BLANK, error.getType());
        assertNotNull(error.getFieldName());
        assertEquals("name", error.getFieldName());
        assertFalse(StringUtils.isBlank(error.getMessage()));
    }

    @Test
    public void nameContainsOnlySpaces() throws Exception {

        IUser user = new User();
        user.setName(null);
        user.setTitle("    ");

        IValidator userValidator = getUserValidatorFactory().create(user);

        assertFalse(userValidator.isValid());
        assertNotNull(userValidator.getErrors());
        assertEquals(1, userValidator.getErrors().size());

        List<IValidationError> errors = userValidator.getErrors().get("name");
        assertNotNull(userValidator.getErrors());
        assertEquals(1, userValidator.getErrors().size());

        IValidationError error = errors.get(0);
        assertNotNull(error);
        assertEquals(IValidationError.VALIDATION_TYPE_NOT_BLANK, error.getType());
        assertNotNull(error.getFieldName());
        assertEquals("name", error.getFieldName());
        assertFalse(StringUtils.isBlank(error.getMessage()));
    }

}
