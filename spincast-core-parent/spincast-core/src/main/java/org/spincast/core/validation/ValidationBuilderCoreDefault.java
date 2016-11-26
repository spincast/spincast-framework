package org.spincast.core.validation;

import javax.annotation.Nullable;

import org.spincast.core.config.SpincastDictionary;
import org.spincast.core.json.JsonManager;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class ValidationBuilderCoreDefault extends ValidationBuilderCoreBaseDefault<ValidationBuilderCore>
                                          implements ValidationBuilderCore {

    @AssistedInject
    public ValidationBuilderCoreDefault(@Assisted ValidationSet validationSet,
                                        @Assisted SimpleValidator validator,
                                        @Assisted String validationKey,
                                        @Assisted @Nullable Object elementToValidate,
                                        ValidationFactory validationFactory,
                                        SpincastDictionary spincastDictionary,
                                        JsonManager jsonManager) {
        super(validationSet,
              validator,
              validationKey,
              elementToValidate,
              validationFactory,
              spincastDictionary,
              jsonManager);
    }

}
