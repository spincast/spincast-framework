package org.spincast.plugins.jsclosurecompiler.tests.utils;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.plugins.jsclosurecompiler.SpincastJsClosureCompilerManager;
import org.spincast.plugins.jsclosurecompiler.SpincastJsClosureCompilerPlugin;
import org.spincast.testing.defaults.NoAppTestingBase;

import com.google.inject.Inject;

public abstract class JsClosureCompileTestBase extends NoAppTestingBase {

    protected static final Logger logger = LoggerFactory.getLogger(JsClosureCompileTestBase.class);

    @Override
    protected List<SpincastPlugin> getExtraPlugins() {
        List<SpincastPlugin> extraPlugins = super.getExtraPlugins();
        extraPlugins.add(new SpincastJsClosureCompilerPlugin());
        return extraPlugins;
    }

    @Inject
    private SpincastJsClosureCompilerManager spincastJsClosureCompilerPlugin;

    protected SpincastJsClosureCompilerManager getSpincastJsClosureCompilerManager() {
        return this.spincastJsClosureCompilerPlugin;
    }

}
