package org.spincast.plugins.validation;

import java.lang.reflect.Type;

import org.spincast.core.guice.SpincastPluginGuiceModuleBase;

import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * Guice module for the Spincast Validation plugin.
 */
public class SpincastValidationPluginGuiceModule extends SpincastPluginGuiceModuleBase {

    /**
     * Constructor.
     */
    public SpincastValidationPluginGuiceModule(Type requestContextType,
                                               Type websocketContextType) {
        super(requestContextType, websocketContextType);
    }

    @Override
    protected void configure() {
        bindValidationErrorFactory();
        bindSpincastValidatorBaseDeps();
    }

    protected void bindValidationErrorFactory() {

        install(new FactoryModuleBuilder().implement(IValidationError.class, getValidationErrorImplementationClass())
                                          .build(IValidationErrorFactory.class));
    }

    protected Class<? extends IValidationError> getValidationErrorImplementationClass() {
        return ValidatorError.class;
    }

    protected void bindSpincastValidatorBaseDeps() {
        bind(SpincastValidatorBaseDeps.class).in(Scopes.SINGLETON);
    }

}
