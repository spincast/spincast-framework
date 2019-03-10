package org.spincast.plugins.dateformatter.tests;

import java.util.List;

import org.spincast.core.guice.SpincastPlugin;
import org.spincast.core.json.JsonManager;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.plugins.dateformatter.DateFormatterFactory;
import org.spincast.plugins.dateformatter.SpincastDateFormatterPlugin;
import org.spincast.testing.defaults.NoAppTestingBase;

import com.google.inject.Inject;

public abstract class DateFormatterTestBase extends NoAppTestingBase {

    @Override
    protected List<SpincastPlugin> getExtraPlugins() {
        List<SpincastPlugin> extraPlugins = super.getExtraPlugins();
        extraPlugins.add(new SpincastDateFormatterPlugin());
        return extraPlugins;
    }

    @Inject
    protected DateFormatterFactory dateFormatterFactory;

    protected DateFormatterFactory getDateFormatterFactory() {
        return this.dateFormatterFactory;
    }

    @Inject
    protected TemplatingEngine templatingEngine;

    @Inject
    protected JsonManager jsonManager;

    protected TemplatingEngine getTemplatingEngine() {
        return this.templatingEngine;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

}
