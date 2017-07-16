package org.spincast.tests.validation;

import org.spincast.core.config.SpincastDictionary;
import org.spincast.core.json.JsonManager;
import org.spincast.core.utils.ObjectConverter;
import org.spincast.core.validation.ValidationFactory;
import org.spincast.defaults.testing.NoAppTestingBase;

import com.google.inject.Inject;

public abstract class ValidationTestBase extends NoAppTestingBase {

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
}
