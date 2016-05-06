package org.spincast.plugins.configpropsfile.tests;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.util.UUID;

import javax.annotation.Nullable;

import org.spincast.core.guice.MainArgs;
import org.spincast.core.utils.ISpincastUtils;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.defaults.tests.DefaultTestingModule;
import org.spincast.plugins.configpropsfile.ISpincastConfigPropsFileBasedConfig;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFileBased;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFileBasedConfigDefault;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFilePluginGuiceModule;
import org.spincast.testing.utils.ExpectingInstanciationException;

import com.google.inject.Inject;
import com.google.inject.Module;

@ExpectingInstanciationException
public class SpecifiedFileNotFoundTest extends DefaultIntegrationTestingBase {

    protected String appPropertiesPath;

    public static class PropsFileBasedConfig extends SpincastConfigPropsFileBased {

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
                install(new SpincastConfigPropsFilePluginGuiceModule(getRequestContextType()));
            }

            @Override
            protected void configure() {
                super.configure();

                bind(ISpincastConfigPropsFileBasedConfig.class).toInstance(new SpincastConfigPropsFileBasedConfigDefault() {

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
