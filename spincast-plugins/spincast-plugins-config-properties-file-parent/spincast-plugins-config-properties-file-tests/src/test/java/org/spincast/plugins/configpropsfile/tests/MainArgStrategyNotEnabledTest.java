package org.spincast.plugins.configpropsfile.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.InputStream;

import javax.annotation.Nullable;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.MainArgs;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.plugins.configpropsfile.FreeKeyConfig;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFileBasedConfig;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFileBased;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFilePluginGuiceModule;
import org.spincast.shaded.org.apache.commons.io.FileUtils;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class MainArgStrategyNotEnabledTest extends SpincastDefaultNoAppIntegrationTestBase {

    @Inject
    protected AppConfig appConfig;

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

    @Override
    protected String[] getMainArgsToUse() {
        //==========================================
        // Pass the path to our configuation file.
        //==========================================
        return new String[]{this.appPropertiesPath};
    }

    public static interface AppConfig extends SpincastConfig, FreeKeyConfig {
        // ...

    }

    public static class PropsFileBasedConfig extends SpincastConfigPropsFileBased implements AppConfig {

        @Inject
        public PropsFileBasedConfig(SpincastUtils spincastUtils,
                                    @MainArgs @Nullable String[] mainArgs,
                                    @Nullable SpincastConfigPropsFileBasedConfig pluginConfig) {
            super(spincastUtils, mainArgs, pluginConfig);
        }
    }

    @Override
    public Module getTestingModule() {
        return new SpincastDefaultTestingModule(getMainArgsToUse()) {

            @Override
            protected void bindConfigPlugin() {
                install(new SpincastConfigPropsFilePluginGuiceModule(getRequestContextType(), getWebsocketContextType()));
            }

            @Override
            protected void configure() {
                super.configure();
                bind(AppConfig.class).to(PropsFileBasedConfig.class).in(Scopes.SINGLETON);
            }
        };
    }

    protected AppConfig getAppConfig() {
        return this.appConfig;
    }

    /**
     * The path to the configuration file we passed as the first main
     * argument won't be used since this strategy has to be explicitly
     * enabled!
     */
    @Test
    public void testDefaultConfig() throws Exception {

        assertNotNull(getSpincastConfig().getHttpServerPort());
        assertEquals(44419, getSpincastConfig().getHttpServerPort());

        assertNotNull(getAppConfig().getHttpServerPort());
        assertEquals(44419, getAppConfig().getHttpServerPort());
    }

}
