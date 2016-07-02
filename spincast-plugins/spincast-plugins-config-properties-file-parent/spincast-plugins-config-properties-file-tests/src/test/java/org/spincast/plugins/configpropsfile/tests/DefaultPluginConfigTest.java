package org.spincast.plugins.configpropsfile.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.annotation.Nullable;

import org.junit.Test;
import org.spincast.core.guice.MainArgs;
import org.spincast.core.utils.ISpincastUtils;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.plugins.configpropsfile.ISpincastConfigPropsFileBasedConfig;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFileBased;
import org.spincast.plugins.configpropsfile.SpincastConfigPropsFilePluginGuiceModule;

import com.google.inject.Inject;
import com.google.inject.Module;

public class DefaultPluginConfigTest extends SpincastDefaultNoAppIntegrationTestBase {

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
        return new SpincastDefaultTestingModule(getMainArgsToUse()) {

            @Override
            protected void bindConfigPlugin() {
                install(new SpincastConfigPropsFilePluginGuiceModule(getRequestContextType(), getWebsocketContextType()));
            }
        };
    }

    @Test
    public void defaultConfigOverriden() throws Exception {

        assertNotNull(getSpincastConfig().getHttpServerPort());
        assertEquals(44419, getSpincastConfig().getHttpServerPort());
    }

}
