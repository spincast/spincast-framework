package org.spincast.plugins.validation;

import org.spincast.core.json.IJsonManager;
import org.spincast.core.xml.IXmlManager;

import com.google.inject.Inject;

/**
 * A wrapper object for the dependencies required by SpincastValidatorBase.
 * We inject this wrapper instead of injecting each individual dependency.
 * We do this because the SpincastValidatorBase is made to be extended frequently
 * by developers and :
 * <ul>
 *     <li> 
 *     We want it to be easily extended without having to inject too many
 *     dependencies in the child class.
 *     </li>
 *     <li> 
 *     We want to keep using constructor injection instead of setter and field
 *     injection.
 *     </li>  
 *     <li> 
 *     By using a wrapper, we can add new dependencies to SpincastValidatorBase
 *     without breaking the client classes.
 *     </li>  
 * </ul>
 */
public class SpincastValidatorBaseDeps {

    private final IValidationErrorFactory validationErrorFactory;
    private final ISpincastValidationConfig spincastBeanValidationConfig;
    private final IJsonManager jsonManager;
    private final IXmlManager xmlManager;

    /**
     * Constructor
     */
    @Inject
    public SpincastValidatorBaseDeps(IValidationErrorFactory validationErrorFactory,
                                     ISpincastValidationConfig spincastBeanValidationConfig,
                                     IJsonManager jsonManager,
                                     IXmlManager xmlManager) {
        this.validationErrorFactory = validationErrorFactory;
        this.spincastBeanValidationConfig = spincastBeanValidationConfig;
        this.jsonManager = jsonManager;
        this.xmlManager = xmlManager;
    }

    public IValidationErrorFactory getValidationErrorFactory() {
        return this.validationErrorFactory;
    }

    public ISpincastValidationConfig getSpincastBeanValidationConfig() {
        return this.spincastBeanValidationConfig;
    }

    public IJsonManager getJsonManager() {
        return this.jsonManager;
    }

    public IXmlManager getXmlManager() {
        return this.xmlManager;
    }

}
