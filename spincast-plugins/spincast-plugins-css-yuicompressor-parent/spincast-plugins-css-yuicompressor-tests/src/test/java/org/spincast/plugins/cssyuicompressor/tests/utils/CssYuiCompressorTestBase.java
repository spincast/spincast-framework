package org.spincast.plugins.cssyuicompressor.tests.utils;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.plugins.cssyuicompressor.SpincastCssYuiCompressorManager;
import org.spincast.plugins.cssyuicompressor.SpincastCssYuiCompressorPlugin;
import org.spincast.testing.defaults.NoAppTestingBase;

import com.google.inject.Inject;

public abstract class CssYuiCompressorTestBase extends NoAppTestingBase {

    protected static final Logger logger = LoggerFactory.getLogger(CssYuiCompressorTestBase.class);

    @Override
    protected List<SpincastPlugin> getExtraPlugins() {
        List<SpincastPlugin> extraPlugins = super.getExtraPlugins();
        extraPlugins.add(new SpincastCssYuiCompressorPlugin());
        return extraPlugins;
    }

    @Inject
    private SpincastCssYuiCompressorManager spincastCssYuiCompressorManager;

    protected SpincastCssYuiCompressorManager getSpincastCssYuiCompressorManager() {
        return this.spincastCssYuiCompressorManager;
    }

}
