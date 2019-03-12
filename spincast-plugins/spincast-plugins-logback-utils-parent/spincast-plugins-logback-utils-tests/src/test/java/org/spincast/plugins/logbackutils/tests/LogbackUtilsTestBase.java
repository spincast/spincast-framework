package org.spincast.plugins.logbackutils.tests;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.logbackutils.SpincastLogbackUtilsPlugin;
import org.spincast.plugins.logbackutils.config.SpincastLogbackConfigurerConfig;
import org.spincast.testing.defaults.NoAppTestingBase;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.util.Modules;

public abstract class LogbackUtilsTestBase extends NoAppTestingBase {

    @Inject
    protected SpincastUtils spincastUtils;

    protected PrintStream originalSystemOut = System.out;
    protected ByteArrayOutputStream baos = new ByteArrayOutputStream();

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    @Override
    protected List<SpincastPlugin> getExtraPlugins() {
        List<SpincastPlugin> extraPlugins = super.getExtraPlugins();
        extraPlugins.add(new SpincastLogbackUtilsPlugin());
        return extraPlugins;
    }

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                if (getSpincastLogbackConfigurerConfigImpl() != null) {
                    bind(SpincastLogbackConfigurerConfig.class).toInstance(getSpincastLogbackConfigurerConfigImpl());
                }
            }
        });
    }

    protected abstract SpincastLogbackConfigurerConfig getSpincastLogbackConfigurerConfigImpl();

    @Override
    public void beforeTest() {
        super.beforeTest();
        //==========================================
        // Capture System.out
        //==========================================
        resetOutput();
        PrintStream ps = new PrintStream(this.baos);
        System.setOut(ps);
    }

    @Override
    public void afterTest() {
        //==========================================
        // Reste output
        //==========================================
        System.setOut(this.originalSystemOut);
        super.afterTest();
    }

    protected String getOutput() {
        System.out.flush();
        return this.baos.toString();
    }

    protected void resetOutput() {
        this.baos.reset();
    }

}
