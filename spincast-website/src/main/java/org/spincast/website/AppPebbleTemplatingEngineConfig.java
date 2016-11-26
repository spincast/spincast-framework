package org.spincast.website;

import org.spincast.core.config.SpincastConfig;
import org.spincast.plugins.pebble.SpincastPebbleTemplatingEngineConfigDefault;
import org.spincast.website.pebble.AppPebbleExtension;

import com.google.inject.Inject;
import com.mitchellbosecke.pebble.extension.Extension;

/**
 * Custom Pebble plugin configurations.
 */
public class AppPebbleTemplatingEngineConfig extends SpincastPebbleTemplatingEngineConfigDefault {

    private final AppPebbleExtension appPebbleExtension;

    @Inject
    public AppPebbleTemplatingEngineConfig(AppPebbleExtension appPebbleExtension,
                                           SpincastConfig spincastConfig) {
        super(spincastConfig);
        this.appPebbleExtension = appPebbleExtension;
    }

    @Override
    public Extension getExtension() {

        //==========================================
        // Our custom Pebble extension.
        //==========================================
        return this.appPebbleExtension;
    }

    @Override
    public String getValidationMessagesTemplatePath() {

        //==========================================
        // Custom template to display the validation messages
        // for a form field.
        //==========================================
        return "/templates/fragments/validationMessagesTemplate.html";
    }

}
