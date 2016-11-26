package org.spincast.tests.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.validation.ValidationFactory;
import org.spincast.core.validation.ValidationLevel;
import org.spincast.core.validation.ValidationSet;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.testing.core.SpincastTestBase;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class ValidationResultTest extends SpincastTestBase {

    @Override
    protected Injector createInjector() {
        return Guice.createInjector(new SpincastDefaultTestingModule());
    }

    @Inject
    protected ValidationFactory validationFactory;

    public ValidationFactory getValidationFactory() {
        return this.validationFactory;
    }

    @Test
    public void error() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationNotNull()
                                                  .key("field1")
                                                  .element(null)
                                                  .validate();

        assertNotNull(validation);
        assertFalse(validation.isValid());
        assertFalse(validation.isSuccess());
        assertFalse(validation.isWarning());
        assertTrue(validation.isError());

        assertFalse(validationFinal.isValid());
        assertFalse(validationFinal.isSuccess());
        assertFalse(validationFinal.isWarning());
        assertTrue(validationFinal.isError());
    }

    @Test
    public void success() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationNotNull()
                                                  .key("field1")
                                                  .element("ok")
                                                  .validate();

        assertNotNull(validation);

        assertTrue(validation.isValid());
        assertTrue(validation.isSuccess());
        assertFalse(validation.isWarning());
        assertFalse(validation.isError());

        assertTrue(validationFinal.isValid());
        assertTrue(validationFinal.isSuccess());
        assertFalse(validationFinal.isWarning());
        assertFalse(validationFinal.isError());
    }

    @Test
    public void warningWinsOverSuccess() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();
        ValidationSet validation = validationFinal.validationNotNull()
                                                  .key("field1")
                                                  .element("ok")
                                                  .validate();

        validationFinal.addMessage("field1",
                                   ValidationLevel.WARNING,
                                   "customCode",
                                   "customMessage");

        assertTrue(validation.isValid());
        assertTrue(validation.isSuccess());
        assertFalse(validation.isWarning());
        assertFalse(validation.isError());

        assertTrue(validationFinal.isValid());
        assertFalse(validationFinal.isSuccess());
        assertTrue(validationFinal.isWarning());
        assertFalse(validationFinal.isError());
    }

    @Test
    public void errorWinsOverAll() throws Exception {

        ValidationSet validationFinal = getValidationFactory().createValidationSet();

        ValidationSet validation1 = validationFinal.validationNotNull()
                                                   .key("field1")
                                                   .element("ok")
                                                   .validate();

        ValidationSet validation2 = validationFinal.validationNotNull()
                                                   .key("field1")
                                                   .element(null)
                                                   .validate();

        validationFinal.addMessage("field1",
                                   ValidationLevel.WARNING,
                                   "customCode",
                                   "customMessage");

        assertTrue(validation1.isValid());
        assertTrue(validation1.isSuccess());
        assertFalse(validation1.isWarning());
        assertFalse(validation1.isError());

        assertFalse(validation2.isValid());
        assertFalse(validation2.isSuccess());
        assertFalse(validation2.isWarning());
        assertTrue(validation2.isError());

        assertFalse(validationFinal.isValid());
        assertFalse(validationFinal.isSuccess());
        assertFalse(validationFinal.isWarning());
        assertTrue(validationFinal.isError());
    }

}
