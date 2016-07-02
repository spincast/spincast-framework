package org.spincast.plugins.validation.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.plugins.validation.IValidationError;
import org.spincast.plugins.validation.IValidator;
import org.spincast.plugins.validation.IValidatorFactory;
import org.spincast.plugins.validation.SpincastValidationPluginGuiceModule;
import org.spincast.plugins.validation.SpincastValidatorBase;
import org.spincast.plugins.validation.SpincastValidatorBaseDeps;
import org.spincast.plugins.validation.tests.utils.IUser;
import org.spincast.plugins.validation.tests.utils.User;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.testing.core.SpincastTestBase;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class SizeValidationTest extends SpincastTestBase {

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

            // Age minimum 10
            validateMinSize("age", getObjToValidate().getAge(), 10);

            // Age maximum 65
            validateMaxSize("age", getObjToValidate().getAge(), 65);

        }
    }

    @Override
    protected Injector createInjector() {
        return Guice.createInjector(getTestingModule());
    }

    public Module getTestingModule() {
        return new SpincastDefaultTestingModule() {

            @Override
            protected void configure() {
                super.configure();

                install(new SpincastValidationPluginGuiceModule(getRequestContextType(), getWebsocketContextType()));

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
    public void ageValid() throws Exception {

        IUser user = new User();
        user.setAge(30);

        IValidator userValidator = getUserValidatorFactory().create(user);

        assertTrue(userValidator.isValid());
        assertNotNull(userValidator.getErrors());
        assertEquals(0, userValidator.getErrors().size());
    }

    @Test
    public void ageMinSize() throws Exception {

        IUser user = new User();
        user.setAge(3);

        IValidator userValidator = getUserValidatorFactory().create(user);

        assertFalse(userValidator.isValid());
        assertNotNull(userValidator.getErrors());
        assertEquals(1, userValidator.getErrors().size());

        List<IValidationError> errors = userValidator.getErrors().get("age");
        assertNotNull(errors);
        assertEquals(1, errors.size());

        IValidationError error = errors.get(0);
        assertNotNull(error);
        assertEquals(IValidationError.VALIDATION_TYPE_MIN_SIZE, error.getType());
        assertNotNull(error.getFieldName());
        assertEquals("age", error.getFieldName());
        assertFalse(StringUtils.isBlank(error.getMessage()));
    }

    @Test
    public void ageMaxSize() throws Exception {

        IUser user = new User();
        user.setAge(90);

        IValidator userValidator = getUserValidatorFactory().create(user);

        assertFalse(userValidator.isValid());
        assertNotNull(userValidator.getErrors());
        assertEquals(1, userValidator.getErrors().size());

        List<IValidationError> errors = userValidator.getErrors().get("age");
        assertNotNull(errors);
        assertEquals(1, errors.size());

        IValidationError error = errors.get(0);
        assertNotNull(error);
        assertEquals(IValidationError.VALIDATION_TYPE_MAX_SIZE, error.getType());
        assertNotNull(error.getFieldName());
        assertEquals("age", error.getFieldName());
        assertFalse(StringUtils.isBlank(error.getMessage()));
    }

}
