package org.spincast.plugins.configpropsfile.tests;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.util.UUID;

import javax.annotation.Nullable;

import org.spincast.core.guice.MainArgs;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFileBasedConfig;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFileBased;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFileBasedConfigDefault;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFilePluginGuiceModule;
import org.spincast.testing.utils.ExpectingBeforeClassException;

import com.google.inject.Inject;
import com.google.inject.Module;

@ExpectingBeforeClassException
public class SpecifiedFileNotFoundTest extends SpincastDefaultNoAppIntegrationTestBase {

    protected String appPropertiesPath;

    public static class PropsFileBasedConfig extends SpincastConfigPropsFileBased {

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

                bind(SpincastConfigPropsFileBasedConfig.class).toInstance(new SpincastConfigPropsFileBasedConfigDefault() {

                    //==========================================
                    // We enable the main arg strategy!
                    //==========================================
                    @Override
                    public int getSpecificPathMainArgsPosition() {
                        return 1;
                    }
                });
            }
        };
    }

    @Override
    protected String[] getMainArgsToUse() {

        String nopeFilePath = getTestingWritableDir().getAbsolutePath() + "/" + UUID.randomUUID().toString();
        assertFalse(new File(nopeFilePath).exists());

        //==========================================
        // Pass an non existing file!
        //==========================================
        return new String[]{nopeFilePath};
    }

}
