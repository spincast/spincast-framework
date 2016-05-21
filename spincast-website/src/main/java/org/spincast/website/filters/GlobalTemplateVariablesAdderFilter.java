package org.spincast.website.filters;

import org.spincast.core.utils.ISpincastUtils;
import org.spincast.website.exchange.IAppRequestContext;

import com.google.inject.Inject;

/**
 * This will add some global variables that will be 
 * available to any templating engine evaluation.
 */
public class GlobalTemplateVariablesAdderFilter {

    private final ISpincastUtils spincastUtils;

    @Inject
    public GlobalTemplateVariablesAdderFilter(ISpincastUtils spincastUtils) {
        this.spincastUtils = spincastUtils;
    }

    protected ISpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    public void apply(IAppRequestContext context) {

        //==========================================
        // The Language abreviation
        //==========================================
        context.templating().addTemplatingGlobalVariable("langAbrv", context.getLocaleToUse().getLanguage());

        //==========================================
        // The current Spincast version
        //==========================================
        String currentVersion = getSpincastUtils().getSpincastCurrentVersion();
        context.templating().addTemplatingGlobalVariable("spincastCurrrentVersion", currentVersion);
        context.templating().addTemplatingGlobalVariable("spincastCurrrentVersionIsSnapshot",
                                                         currentVersion.contains("-SNAPSHOT"));
    }

}
