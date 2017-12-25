package org.spincast.tests.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.validation.Validators;
import org.spincast.defaults.testing.NoAppTestingBase;

import com.google.inject.Inject;

public class ValidatorsTest extends NoAppTestingBase {

    @Inject
    private Validators validators;

    protected Validators getValidators() {
        return this.validators;
    }

    @Test
    public void emailValid() throws Exception {
        assertTrue(getValidators().isEmailValid("test@example.com"));
        assertTrue(getValidators().isEmailValid("test.test2@example.com"));
    }

    @Test
    public void emailInvalid() throws Exception {
        assertFalse(getValidators().isEmailValid("@example.com"));
        assertFalse(getValidators().isEmailValid("example.com"));
        assertFalse(getValidators().isEmailValid("com"));
        assertFalse(getValidators().isEmailValid(""));
        assertFalse(getValidators().isEmailValid(null));
    }


}
