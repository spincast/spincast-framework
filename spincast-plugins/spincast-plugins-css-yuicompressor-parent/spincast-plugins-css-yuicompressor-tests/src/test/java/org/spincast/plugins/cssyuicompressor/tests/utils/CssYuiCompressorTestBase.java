package org.spincast.plugins.cssyuicompressor.tests.utils;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.cssyuicompressor.SpincastCssYuiCompressorManager;
import org.spincast.plugins.cssyuicompressor.SpincastCssYuiCompressorPebbleExtension;
import org.spincast.plugins.cssyuicompressor.SpincastCssYuiCompressorPlugin;
import org.spincast.plugins.cssyuicompressor.config.SpincastCssYuiCompressorConfig;
import org.spincast.plugins.cssyuicompressor.config.SpincastCssYuiCompressorConfigDefault;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

public abstract class CssYuiCompressorTestBase extends NoAppStartHttpServerTestingBase {


    protected static final Logger logger = LoggerFactory.getLogger(CssYuiCompressorTestBase.class);

    protected static File cssBundlesTestDir;

    @Override
    public void beforeClass() {
        cssBundlesTestDir = new File(getTestingWritableTempDir(), "cssBundles");
        super.beforeClass();
    }

    @Override
    protected List<SpincastPlugin> getExtraPlugins() {
        List<SpincastPlugin> extraPlugins = super.getExtraPlugins();
        extraPlugins.add(new SpincastCssYuiCompressorPlugin());
        return extraPlugins;
    }

    protected static class TestSpincastCssYuiCompressorConfig extends SpincastCssYuiCompressorConfigDefault {

        @Inject
        public TestSpincastCssYuiCompressorConfig(SpincastConfig spincastConfig) {
            super(spincastConfig);
        }

        @Override
        public boolean isCssBundlesIgnoreSslCertificateErrors() {
            return true;
        }

        @Override
        public String getCssBundlesUrlPath() {
            return "/test-css-bundles";
        }

        @Override
        public File getCssBundlesDir() {
            return cssBundlesTestDir;
        }
    }

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastCssYuiCompressorConfig.class).to(TestSpincastCssYuiCompressorConfig.class).in(Scopes.SINGLETON);
            }
        });
    }

    @Inject
    private SpincastCssYuiCompressorManager spincastCssYuiCompressorManager;

    @Inject
    private TemplatingEngine templatingEngine;

    @Inject
    private SpincastCssYuiCompressorPebbleExtension spincastCssYuiCompressorPebbleExtension;

    @Inject
    private SpincastUtils spincastUtils;

    @Inject
    private SpincastCssYuiCompressorConfig spincastCssYuiCompressorConfig;

    protected SpincastCssYuiCompressorManager getSpincastCssYuiCompressorManager() {
        return this.spincastCssYuiCompressorManager;
    }

    protected TemplatingEngine getTemplatingEngine() {
        return this.templatingEngine;
    }

    protected SpincastCssYuiCompressorPebbleExtension getSpincastCssYuiCompressorPebbleExtension() {
        return this.spincastCssYuiCompressorPebbleExtension;
    }

    protected SpincastCssYuiCompressorConfig getSpincastCssYuiCompressorConfig() {
        return this.spincastCssYuiCompressorConfig;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

}
