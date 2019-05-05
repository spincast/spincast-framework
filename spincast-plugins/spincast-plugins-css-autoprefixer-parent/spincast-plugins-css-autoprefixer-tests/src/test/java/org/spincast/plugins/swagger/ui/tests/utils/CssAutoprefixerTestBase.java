package org.spincast.plugins.swagger.ui.tests.utils;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.core.json.JsonManager;
import org.spincast.plugins.cssautoprefixer.SpincastCssAutoprefixerManager;
import org.spincast.plugins.cssautoprefixer.SpincastCssAutoprefixerPlugin;
import org.spincast.testing.defaults.NoAppTestingBase;

import com.google.inject.Inject;

public abstract class CssAutoprefixerTestBase extends NoAppTestingBase {

    protected static final Logger logger = LoggerFactory.getLogger(CssAutoprefixerTestBase.class);

    /**
     * Those tests can only be run on an environment where
     * Autoprefixer is installed!
     */
    @Override
    public boolean isTestClassDisabledPostBeforeClass() {

        boolean validEnv = getSpincastCssAutoprefixerManager().isValidAutoprefixerEnvironment();
        if (!validEnv) {
            logger.warn("Autoprefixer environment not valid, skipping all related tests!");
            return true;
        }

        return false;
    }

    @Override
    protected List<SpincastPlugin> getExtraPlugins() {
        List<SpincastPlugin> extraPlugins = super.getExtraPlugins();
        extraPlugins.add(new SpincastCssAutoprefixerPlugin());
        return extraPlugins;
    }

    @Inject
    private SpincastCssAutoprefixerManager spincastCssAutoprefixerManager;

    @Inject
    private JsonManager jsonManager;

    protected SpincastCssAutoprefixerManager getSpincastCssAutoprefixerManager() {
        return this.spincastCssAutoprefixerManager;
    }

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

}
