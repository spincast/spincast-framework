package org.spincast.tests.validation;

import org.spincast.core.config.SpincastDictionary;
import org.spincast.core.json.JsonManager;
import org.spincast.core.utils.ObjectConverter;
import org.spincast.core.validation.ValidationFactory;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.testing.core.SpincastTestBase;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public abstract class ValidationTestBase extends SpincastTestBase {

    @Inject
    private JsonManager jsonManager;

    @Inject
    private ValidationFactory validationFactory;

    @Inject
    private ObjectConverter objectConverter;

    @Inject
    private SpincastDictionary spincastDictionary;

    protected ObjectConverter getObjectConverter() {
        return this.objectConverter;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected ValidationFactory getValidationFactory() {
        return this.validationFactory;
    }

    protected SpincastDictionary getSpincastDictionary() {
        return this.spincastDictionary;
    }

    @Override
    protected Injector createInjector() {
        return Guice.createInjector(new SpincastDefaultTestingModule());
    }
}
