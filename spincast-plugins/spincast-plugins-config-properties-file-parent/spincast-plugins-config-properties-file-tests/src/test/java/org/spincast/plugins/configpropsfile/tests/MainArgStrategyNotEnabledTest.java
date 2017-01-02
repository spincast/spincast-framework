package org.spincast.plugins.configpropsfile.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.InputStream;

import javax.annotation.Nullable;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.MainArgs;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.defaults.testing.IntegrationTestNoAppDefaultContextsBase;
import org.spincast.plugins.configpropsfile.FreeKeyConfig;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFileBased;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFilePluginConfig;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFilePluginModule;
import org.spincast.shaded.org.apache.commons.io.FileUtils;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Scopes;

public class MainArgStrategyNotEnabledTest extends IntegrationTestNoAppDefaultContextsBase {

    @Override
    protected boolean isEnableGuiceTweakerTestingConfigMecanism() {
        return false;
    }

    @Override
    protected Injector createInjector() {

        return Spincast.configure()
                       .module(new SpincastConfigPropsFilePluginModule(PropsFileBasedConfigTesting.class))
                       .module(new SpincastGuiceModuleBase() {

                           @Override
                           protected void configure() {
                               bind(AppConfig.class).to(PropsFileBasedConfigTesting.class).in(Scopes.SINGLETON);
                           }
                       })
                       .mainArgs(new String[]{this.appPropertiesPath})
                       .init();
    }

    @Inject
    protected AppConfig appConfig;

    protected AppConfig getAppConfig() {
        return this.appConfig;
    }

    protected String appPropertiesPath;

    //==========================================
    // We create the "app.properties" file to use.
    //==========================================
    @Override
    public void beforeClass() {

        createTempPropsFile();

        super.beforeClass();
    };

    protected void createTempPropsFile() {

        try {
            InputStream appPropsStream = getClass().getClassLoader().getResourceAsStream("app.properties");
            assertNotNull(appPropsStream);

            this.appPropertiesPath = createTestingFilePath();
            FileUtils.copyInputStreamToFile(appPropsStream, new File(this.appPropertiesPath));
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    public static interface AppConfig extends SpincastConfig, FreeKeyConfig {
        // ...
    }

    public static class PropsFileBasedConfigTesting extends SpincastConfigPropsFileBased implements AppConfig {

        @Inject
        public PropsFileBasedConfigTesting(SpincastUtils spincastUtils,
                                           @MainArgs @Nullable String[] mainArgs,
                                           @Nullable SpincastConfigPropsFilePluginConfig pluginConfig) {
            super(spincastUtils, mainArgs, pluginConfig);
        }
    }

    /**
     * The path to the configuration file we passed as the first main
     * argument won't be used since this strategy has to be explicitly
     * enabled!
     */
    @Test
    public void testDefaultConfig() throws Exception {
        assertNotNull(getSpincastConfig().getServerHost());
        assertEquals("0.0.0.0", getSpincastConfig().getServerHost());
    }

}
