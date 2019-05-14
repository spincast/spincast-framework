package org.spincast.plugins.jsclosurecompiler.tests.utils;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.jsclosurecompiler.SpincastJsClosureCompilerManager;
import org.spincast.plugins.jsclosurecompiler.SpincastJsClosureCompilerPebbleExtension;
import org.spincast.plugins.jsclosurecompiler.SpincastJsClosureCompilerPlugin;
import org.spincast.plugins.jsclosurecompiler.config.SpincastJsClosureCompilerConfig;
import org.spincast.plugins.jsclosurecompiler.config.SpincastJsClosureCompilerConfigDefault;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

public abstract class JsClosureCompileTestBase extends NoAppStartHttpServerTestingBase {

    protected static final Logger logger = LoggerFactory.getLogger(JsClosureCompileTestBase.class);

    protected static File jsBundlesTestDir;

    @Override
    public void beforeClass() {
        jsBundlesTestDir = new File(getTestingWritableTempDir(), "jsBundles");
        super.beforeClass();
    }

    @Override
    protected List<SpincastPlugin> getExtraPlugins() {
        List<SpincastPlugin> extraPlugins = super.getExtraPlugins();
        extraPlugins.add(new SpincastJsClosureCompilerPlugin());
        return extraPlugins;
    }

    protected static class TestSpincastJsClosureCompilerConfig extends SpincastJsClosureCompilerConfigDefault {

        @Inject
        public TestSpincastJsClosureCompilerConfig(SpincastConfig spincastConfig) {
            super(spincastConfig);
        }

        @Override
        public boolean isJsBundlesIgnoreSslCertificateErrors() {
            return true;
        }

        @Override
        public String getJsBundlesUrlPath() {
            return "/test-js-bundles";
        }

        @Override
        public File getJsBundlesDir() {
            return jsBundlesTestDir;
        }
    }

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastJsClosureCompilerConfig.class).to(TestSpincastJsClosureCompilerConfig.class).in(Scopes.SINGLETON);
            }
        });
    }

    @Inject
    private SpincastJsClosureCompilerManager spincastJsClosureCompilerPlugin;

    @Inject
    private TemplatingEngine templatingEngine;

    @Inject
    private SpincastJsClosureCompilerPebbleExtension spincastJsClosureCompilerPebbleExtension;

    @Inject
    private SpincastUtils spincastUtils;

    @Inject
    private SpincastJsClosureCompilerConfig spincastJsClosureCompilerConfig;

    protected SpincastJsClosureCompilerManager getSpincastJsClosureCompilerManager() {
        return this.spincastJsClosureCompilerPlugin;
    }

    protected TemplatingEngine getTemplatingEngine() {
        return this.templatingEngine;
    }

    protected SpincastJsClosureCompilerPebbleExtension getSpincastJsClosureCompilerPebbleExtension() {
        return this.spincastJsClosureCompilerPebbleExtension;
    }

    protected SpincastJsClosureCompilerConfig getSpincastJsClosureCompilerConfig() {
        return this.spincastJsClosureCompilerConfig;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }


}
