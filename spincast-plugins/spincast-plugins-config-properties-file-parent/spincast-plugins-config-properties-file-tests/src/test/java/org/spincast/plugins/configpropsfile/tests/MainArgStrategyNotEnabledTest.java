package org.spincast.plugins.configpropsfile.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.InputStream;

import javax.annotation.Nullable;

import org.junit.Test;
import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.guice.MainArgs;
import org.spincast.core.utils.ISpincastUtils;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.defaults.tests.DefaultTestingModule;
import org.spincast.plugins.configpropsfile.IFreeKeyConfig;
import org.spincast.plugins.configpropsfile.ISpincastConfigPropsFileBasedConfig;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFileBased;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFilePluginGuiceModule;
import org.spincast.shaded.org.apache.commons.io.FileUtils;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class MainArgStrategyNotEnabledTest extends DefaultIntegrationTestingBase {

    @Inject
    protected IAppConfig appConfig;

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

    public static interface IAppConfig extends ISpincastConfig, IFreeKeyConfig {
        // ...

    }

    public static class PropsFileBasedConfig extends SpincastConfigPropsFileBased implements IAppConfig {

        @Inject
        public PropsFileBasedConfig(ISpincastUtils spincastUtils,
                                    @MainArgs @Nullable String[] mainArgs,
                                    @Nullable ISpincastConfigPropsFileBasedConfig pluginConfig) {
            super(spincastUtils, mainArgs, pluginConfig);
        }
    }

    @Override
    public Module getTestingModule() {
        return new DefaultTestingModule(getMainArgsToUse()) {

            @Override
            protected void bindConfigPlugin() {
                install(new SpincastConfigPropsFilePluginGuiceModule(getRequestContextType(), getWebsocketContextType()));
            }

            @Override
            protected void configure() {
                super.configure();
                bind(IAppConfig.class).to(PropsFileBasedConfig.class).in(Scopes.SINGLETON);
            }
        };
    }

    protected IAppConfig getAppConfig() {
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
