package org.spincast.plugins.validation.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

public class NotNullValidationTest extends SpincastGuiceModuleBasedTestBase {

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

            // Name not null
            validateNotNull("name", getObjToValidate().getName());

            // Title not null, custom message
            validateNotNull("title", getObjToValidate().getTitle(), "The title cannot be null...");
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
    public void objValid() throws Exception {

        IUser user = new User();
        user.setName("Stromgol");
        user.setTitle("alien");

        IValidator userValidator = getUserValidatorFactory().create(user);

        assertTrue(userValidator.isValid());
        assertNotNull(userValidator.getErrors());
        assertEquals(0, userValidator.getErrors().size());
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
        assertEquals(IValidationError.VALIDATION_TYPE_NOT_NULL, error.getType());
        assertNotNull(error.getFieldName());
        assertEquals("name", error.getFieldName());
        assertFalse(StringUtils.isBlank(error.getMessage()));
    }

    @Test
    public void nameAndTitleNullCustomErrorMessage() throws Exception {

        IUser user = new User();
        user.setName(null);
        user.setTitle(null);

        IValidator userValidator = getUserValidatorFactory().create(user);

        assertFalse(userValidator.isValid());
        assertNotNull(userValidator.getErrors());
        assertEquals(2, userValidator.getErrors().size());

        List<IValidationError> errors = userValidator.getErrors().get("name");
        assertNotNull(errors);
        assertEquals(1, errors.size());

        IValidationError error = errors.get(0);
        assertNotNull(error);
        assertEquals(IValidationError.VALIDATION_TYPE_NOT_NULL, error.getType());
        assertNotNull(error.getFieldName());
        assertEquals("name", error.getFieldName());
        assertFalse(StringUtils.isBlank(error.getMessage()));

        errors = userValidator.getErrors().get("title");
        assertNotNull(errors);
        assertEquals(1, errors.size());

        error = errors.get(0);
        assertNotNull(error);
        assertEquals(IValidationError.VALIDATION_TYPE_NOT_NULL, error.getType());
        assertNotNull(error.getFieldName());
        assertEquals("title", error.getFieldName());
        assertEquals("The title cannot be null...", error.getMessage());
    }

}
