package org.spincast.plugins.configpropsfile.tests;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.util.UUID;

import javax.annotation.Nullable;

import org.junit.Test;
import org.spincast.core.guice.MainArgs;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.defaults.testing.IntegrationTestNoAppDefaultContextsBase;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFileBased;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFilePluginConfig;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFilePluginDefault;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFilePluginModule;
import org.spincast.testing.utils.ExpectingBeforeClassException;

import com.google.inject.Inject;
import com.google.inject.Injector;

@ExpectingBeforeClassException
public class SpecifiedFileNotFoundTest extends IntegrationTestNoAppDefaultContextsBase {

    /**
     * We'll manage the testing configurations by ourself.
     */
    @Override
    protected boolean isEnableGuiceTweakerTestingConfigMecanism() {
        return false;
    }

    @Override
    protected Injector createInjector() {

        return Spincast.configure()
                       .module(new SpincastConfigPropsFilePluginModule(PropsFileBasedConfig.class))
                       .module(new SpincastGuiceModuleBase() {

                           @Override
                           protected void configure() {

                               //==========================================
                               // The configuration for the prop file based
                               // config plugin
                               //==========================================
                               bind(SpincastConfigPropsFilePluginConfig.class).toInstance(new SpincastConfigPropsFilePluginDefault() {

                                   //==========================================
                                   // We enable the main arg strategy!
                                   //==========================================
                                   @Override
                                   public int getSpecificPathMainArgsPosition() {
                                       return 1;
                                   }
                               });
                           }
                       })
                       .mainArgs(getMainArgsToUse())
                       .init();
    }

    protected String[] getMainArgsToUse() {

        String nopeFilePath = getTestingWritableDir().getAbsolutePath() + "/" + UUID.randomUUID().toString();
        assertFalse(new File(nopeFilePath).exists());

        //==========================================
        // Passes an non existing file!
        //==========================================
        return new String[]{nopeFilePath};
    }

    protected String appPropertiesPath;

    public static class PropsFileBasedConfig extends SpincastConfigPropsFileBased {

        @Inject
        public PropsFileBasedConfig(SpincastUtils spincastUtils,
                                    @MainArgs @Nullable String[] mainArgs,
                                    @Nullable SpincastConfigPropsFilePluginConfig pluginConfig) {
            super(spincastUtils, mainArgs, pluginConfig);
        }
    }

    @Test
    public void emptyTest() throws Exception {

    }
}
